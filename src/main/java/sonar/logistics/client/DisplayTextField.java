package sonar.logistics.client;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.logistics.api.display.DisplayConstants;
import sonar.logistics.api.display.IDisplayInfo;

@SideOnly(Side.CLIENT)
public class DisplayTextField extends Gui {

	public final int id;
	public final FontRenderer fontRendererInstance;
	public int xPosition;
	public int yPosition;
	public int width;
	public int height;
	public int currentLine = 0;
	public int maxLines;
	public ArrayList<String> textList = Lists.newArrayList("");

	public int maxStringLength = 32;
	public int cursorCounter;
	public boolean enableBackgroundDrawing = true;
	public boolean canLoseFocus = true;
	public boolean isFocused;
	public boolean isEnabled = true;
	public int lineScrollOffset;
	public int cursorPosition;
	public int selectionEnd;

	public int enabledColor = 14737632;
	public int disabledColor = 7368816;

	public boolean visible = true;
	public GuiPageButtonList.GuiResponder guiResponder;
	public Predicate<String> validator = Predicates.<String>alwaysTrue();

	public DisplayTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height) {
		this.id = componentId;
		this.fontRendererInstance = fontrendererObj;
		this.xPosition = x;
		this.yPosition = y;
		this.width = par5Width;
		this.height = par6Height;
		this.maxLines = height / 12;
	}

	public void setStrings(ArrayList<String> list) {
		textList = list;
	}

	public String getFormattedText(IDisplayInfo info, int line) {
		return DisplayConstants.formatText(getText(line), info);
	}

	/** Sets the GuiResponder associated with this text box. */
	public void setGuiResponder(GuiPageButtonList.GuiResponder guiResponderIn) {
		this.guiResponder = guiResponderIn;
	}

	/** Increments the cursor counter */
	public void updateCursorCounter() {
		++this.cursorCounter;
	}

	/** Sets the text of the textbox, and moves the cursor to the end. */
	public void setText(int line, String textIn) {
		if (this.validator.apply(textIn)) {
			addMissingLines(line);
			if (textIn.length() > this.maxStringLength) {
				this.textList.set(line, textIn.substring(0, this.maxStringLength));
			} else {
				this.textList.set(line, textIn);
			}
			this.setCursorPositionEnd();
		}
	}

	/** Returns the contents of the textbox */
	public String getText(int line) {
		String text = textList.size() > line ? this.textList.get(line) : null;
		if (text == null) {
			addMissingLines(line);
		}
		return text == null ? "" : text;
	}

	public void addMissingLines(int line) {
		int dif = line - this.textList.size();
		while (dif >= 0) {
			this.textList.add("");
			dif--;
		}
	}

	/** returns the text between the cursor and selectionEnd */
	public String getSelectedText() {
		int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
		int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
		return getText(currentLine).substring(i, j);
	}

	public void setValidator(Predicate<String> theValidator) {
		this.validator = theValidator;
	}

	/** Adds the given text after the cursor, or replaces the currently selected text if there is a selection. */
	public void writeText(String textToWrite) {
		String text = getText(currentLine);
		String s = "";
		String s1 = ChatAllowedCharacters.filterAllowedCharacters(textToWrite);
		int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
		int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
		int k = this.maxStringLength - text.length() - (i - j);

		if (!text.isEmpty()) {
			s = s + text.substring(0, i);
		}

		int l;

		if (k < s1.length()) {
			s = s + s1.substring(0, k);
			l = k;
		} else {
			s = s + s1;
			l = s1.length();
		}

		if (!text.isEmpty() && j < text.length()) {
			s = s + text.substring(j);
		}

		if (this.validator.apply(s)) {
			this.setText(currentLine, s);
			this.moveCursorBy(i - this.selectionEnd + l);

			if (this.guiResponder != null) {
				this.guiResponder.setEntryValue(this.id, text);
			}
		}
	}

	/** Deletes the given number of words from the current cursor's position, unless there is currently a selection, in which case the selection is deleted instead. */
	public void deleteWords(int num) {
		if (!getText(currentLine).isEmpty()) {
			if (this.selectionEnd != this.cursorPosition) {
				this.writeText("");
			} else {
				this.deleteFromCursor(this.getNthWordFromCursor(num) - this.cursorPosition);
			}
		} else {
			if (currentLine != 0) {
				textList.remove(currentLine);
				moveUp();
			}
		}
	}

	/** Deletes the given number of characters from the current cursor's position, unless there is currently a selection, in which case the selection is deleted instead. */
	public void deleteFromCursor(int num) {
		String text = getText(currentLine);
		if (!text.isEmpty()) {
			if (this.selectionEnd != this.cursorPosition) {
				this.writeText("");
			} else {
				boolean flag = num < 0;
				int i = flag ? this.cursorPosition + num : this.cursorPosition;
				int j = flag ? this.cursorPosition : this.cursorPosition + num;
				String s = "";

				if (i >= 0) {
					s = text.substring(0, Math.min(i, text.length()));
				}

				if (j < text.length()) {
					s = s + text.substring(Math.min(j, text.length()));
				}

				if (this.validator.apply(s)) {
					this.setText(currentLine, s);

					if (flag) {
						this.moveCursorBy(num);
					}

					if (this.guiResponder != null) {
						this.guiResponder.setEntryValue(this.id, text);
					}
				}
			}
		} else {
			if (currentLine != 0) {
				textList.remove(currentLine);
				moveUp();
			}
		}
	}

	public int getId() {
		return this.id;
	}

	/** Gets the starting index of the word at the specified number of words away from the cursor position. */
	public int getNthWordFromCursor(int numWords) {
		return this.getNthWordFromPos(numWords, this.getCursorPosition());
	}

	/** Gets the starting index of the word at a distance of the specified number of words away from the given position. */
	public int getNthWordFromPos(int n, int pos) {
		return this.getNthWordFromPosWS(n, pos, true);
	}

	/** Like getNthWordFromPos (which wraps this), but adds option for skipping consecutive spaces */
	public int getNthWordFromPosWS(int n, int pos, boolean skipWs) {
		String text = getText(currentLine);
		int i = pos;
		boolean flag = n < 0;
		int j = Math.abs(n);

		for (int k = 0; k < j; ++k) {
			if (!flag) {
				int l = text.length();
				i = text.indexOf(32, i);

				if (i == -1) {
					i = l;
				} else {
					while (skipWs && i < l && text.charAt(i) == 32) {
						++i;
					}
				}
			} else {
				while (skipWs && i > 0 && text.charAt(i - 1) == 32) {
					--i;
				}

				while (i > 0 && text.charAt(i - 1) != 32) {
					--i;
				}
			}
		}

		return i;
	}

	/** Moves the text cursor by a specified number of characters and clears the selection */
	public void moveCursorBy(int num) {
		this.setCursorPosition(this.selectionEnd + num);
	}

	/** Sets the current position of the cursor. */
	public void setCursorPosition(int pos) {
		this.cursorPosition = pos;
		int i = getText(currentLine).length();
		this.cursorPosition = MathHelper.clamp_int(this.cursorPosition, 0, i);
		this.setSelectionPos(this.cursorPosition);
	}

	/** Moves the cursor to the very start of this text box. */
	public void setCursorPositionZero() {
		this.setCursorPosition(0);
	}

	/** Moves the cursor to the very end of this text box. */
	public void setCursorPositionEnd() {
		this.setCursorPosition(getText(currentLine).length());
	}

	public void moveUp() {
		if (this.currentLine != 0) {
			this.currentLine--;
			this.setCursorPositionEnd();
			this.setSelectionPos(getCursorPosition());
		}
	}

	public void moveDown() {
		if (currentLine < maxLines) {
			this.currentLine++;
			this.setCursorPositionEnd();
			this.setSelectionPos(getCursorPosition());
		}else{
			this.setCursorPositionEnd();
		}
	}

	/** Call this method from your GuiScreen to process the keys into the textbox */
	public boolean textboxKeyTyped(char typedChar, int keyCode) {
		if (!this.isFocused) {
			return false;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			moveUp();
			return true;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			moveDown();
			return true;
		} else if (GuiScreen.isKeyComboCtrlA(keyCode)) {
			this.setCursorPositionEnd();
			this.setSelectionPos(0);
			return true;
		} else if (GuiScreen.isKeyComboCtrlC(keyCode)) {
			GuiScreen.setClipboardString(this.getSelectedText());
			return true;
		} else if (GuiScreen.isKeyComboCtrlV(keyCode)) {
			if (this.isEnabled) {
				this.writeText(GuiScreen.getClipboardString());
			}
			return true;
		} else if (GuiScreen.isKeyComboCtrlX(keyCode)) {
			GuiScreen.setClipboardString(this.getSelectedText());

			if (this.isEnabled) {
				this.writeText("");
			}

			return true;
		} else {
			switch (keyCode) {
			case 14:
				if (GuiScreen.isCtrlKeyDown()) {
					if (this.isEnabled) {
						this.deleteWords(-1);
					}
				} else if (this.isEnabled) {
					this.deleteFromCursor(-1);
				}

				return true;
			case 199:

				if (GuiScreen.isShiftKeyDown()) {
					this.setSelectionPos(0);
				} else {
					this.setCursorPositionZero();
				}

				return true;
			case 203:

				if (GuiScreen.isShiftKeyDown()) {
					if (GuiScreen.isCtrlKeyDown()) {
						this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
					} else {
						this.setSelectionPos(this.getSelectionEnd() - 1);
					}
				} else if (GuiScreen.isCtrlKeyDown()) {
					this.setCursorPosition(this.getNthWordFromCursor(-1));
				} else {
					this.moveCursorBy(-1);
				}

				return true;
			case 205:

				if (GuiScreen.isShiftKeyDown()) {
					if (GuiScreen.isCtrlKeyDown()) {
						this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
					} else {
						this.setSelectionPos(this.getSelectionEnd() + 1);
					}
				} else if (GuiScreen.isCtrlKeyDown()) {
					this.setCursorPosition(this.getNthWordFromCursor(1));
				} else {
					this.moveCursorBy(1);
				}

				return true;
			case 207:

				if (GuiScreen.isShiftKeyDown()) {
					this.setSelectionPos(getText(currentLine).length());
				} else {
					this.setCursorPositionEnd();
				}

				return true;
			case 211:

				if (GuiScreen.isCtrlKeyDown()) {
					if (this.isEnabled) {
						this.deleteWords(1);
					}
				} else if (this.isEnabled) {
					this.deleteFromCursor(1);
				}

				return true;
			default:

				if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
					if (this.isEnabled) {
						this.writeText(Character.toString(typedChar));
					}

					return true;
				} else {
					return false;
				}
			}
		}
	}

	/** Called when mouse is clicked, regardless as to whether it is over this button or not. */
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		boolean flag = mouseX >= this.xPosition && mouseX < this.xPosition + this.width && mouseY >= this.yPosition && mouseY < this.yPosition + this.height;

		if (this.canLoseFocus) {
			this.setFocused(flag);
		}

		if (this.isFocused && flag && mouseButton == 0) {
			boolean withintext = mouseX >= this.xPosition && mouseX < this.xPosition + this.width && mouseY >= this.yPosition && mouseY < this.yPosition + this.height;

			int i = mouseX - this.xPosition;
			int y = mouseY - this.yPosition;

			if (this.enableBackgroundDrawing) {
				i -= 4;
			}

			String s = this.fontRendererInstance.trimStringToWidth(getText(currentLine).substring(this.lineScrollOffset), this.getWidth());
			this.setCursorPosition(this.fontRendererInstance.trimStringToWidth(s, i).length() + this.lineScrollOffset);
			this.currentLine = Math.min(y / this.fontRendererInstance.FONT_HEIGHT, textList.size());
		}
	}

	/** Draws the textbox */
	public void drawTextBox() {
		if (this.getVisible()) {
			if (this.getEnableBackgroundDrawing()) {
				drawRect(this.xPosition - 1, this.yPosition - 1, this.xPosition + this.width + 1, this.yPosition + this.height + 1, -6250336);
				drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, -16777216);
			}

			int i = this.isEnabled ? this.enabledColor : this.disabledColor;
			int j = this.cursorPosition - this.lineScrollOffset;
			int k = this.selectionEnd - this.lineScrollOffset;

			for (int c = 0; c < textList.size(); c++) {
				String text = this.getText(c);
				boolean selectedLine = c == currentLine;

				String s = this.fontRendererInstance.trimStringToWidth(selectedLine ? text.substring(this.lineScrollOffset) : text, this.getWidth());
				boolean flag = j >= 0 && j <= s.length();
				boolean flag1 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;
				int l = this.enableBackgroundDrawing ? this.xPosition + 4 : this.xPosition;
				int i1 = this.enableBackgroundDrawing ? this.yPosition + (c * this.fontRendererInstance.FONT_HEIGHT) : this.yPosition;
				int j1 = l;

				if (k > s.length()) {
					k = s.length();
				}

				if (!s.isEmpty()) {
					String s1 = flag ? s.substring(0, j) : s;
					j1 = this.fontRendererInstance.drawStringWithShadow(s1, (float) l, (float) i1, i);
				}

				boolean flag2 = this.cursorPosition < text.length() || text.length() >= this.getMaxStringLength();
				int k1 = j1;

				if (!flag) {
					k1 = j > 0 ? l + this.width : l;
				} else if (flag2) {
					k1 = j1 - 1;
					--j1;
				}

				if (!s.isEmpty() && flag && j < s.length()) {
					j1 = this.fontRendererInstance.drawStringWithShadow(s.substring(j), (float) j1, (float) i1, i);
				}

				if (selectedLine && flag1) {
					if (flag2) {
						// selection is a lil bugged at the moment from what I can see. idk
						Gui.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + this.fontRendererInstance.FONT_HEIGHT, -3092272);
					} else {
						this.fontRendererInstance.drawStringWithShadow("_", (float) k1, (float) i1, i);
					}
				}

				if (selectedLine && k != j) {
					int l1 = l + this.fontRendererInstance.getStringWidth(s.substring(0, k));
					this.drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + this.fontRendererInstance.FONT_HEIGHT);
				}
			}
		}
	}

	/** Draws the current selection and a vertical line cursor in the text box. */
	private void drawCursorVertical(int startX, int startY, int endX, int endY) {
		if (startX < endX) {
			int i = startX;
			startX = endX;
			endX = i;
		}

		if (startY < endY) {
			int j = startY;
			startY = endY;
			endY = j;
		}

		if (endX > this.xPosition + this.width) {
			endX = this.xPosition + this.width;
		}

		if (startX > this.xPosition + this.width) {
			startX = this.xPosition + this.width;
		}

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
		GlStateManager.disableTexture2D();
		GlStateManager.enableColorLogic();
		GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
		vertexbuffer.pos((double) startX, (double) endY, 0.0D).endVertex();
		vertexbuffer.pos((double) endX, (double) endY, 0.0D).endVertex();
		vertexbuffer.pos((double) endX, (double) startY, 0.0D).endVertex();
		vertexbuffer.pos((double) startX, (double) startY, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.disableColorLogic();
		GlStateManager.enableTexture2D();
	}

	/** Sets the maximum length for the text in this text box. If the current text is longer than this length, the current text will be trimmed. */
	public void setMaxStringLength(int length) {
		this.maxStringLength = length;
		for (int i = 0; i < textList.size(); i++) {
			String text = this.getText(i);
			if (text.length() > length) {
				this.setText(i, text.substring(0, length));
			}
		}
	}

	/** returns the maximum number of character that can be contained in this textbox */
	public int getMaxStringLength() {
		return this.maxStringLength;
	}

	/** returns the current position of the cursor */
	public int getCursorPosition() {
		return this.cursorPosition;
	}

	/** Gets whether the background and outline of this text box should be drawn (true if so). */
	public boolean getEnableBackgroundDrawing() {
		return this.enableBackgroundDrawing;
	}

	/** Sets whether or not the background and outline of this text box should be drawn. */
	public void setEnableBackgroundDrawing(boolean enableBackgroundDrawingIn) {
		this.enableBackgroundDrawing = enableBackgroundDrawingIn;
	}

	/** Sets the color to use when drawing this text box's text. A different color is used if this text box is disabled. */
	public void setTextColor(int color) {
		this.enabledColor = color;
	}

	/** Sets the color to use for text in this text box when this text box is disabled. */
	public void setDisabledTextColour(int color) {
		this.disabledColor = color;
	}

	/** Sets focus to this gui element */
	public void setFocused(boolean isFocusedIn) {
		if (isFocusedIn && !this.isFocused) {
			this.cursorCounter = 0;
		}

		this.isFocused = isFocusedIn;
	}

	/** Getter for the focused field */
	public boolean isFocused() {
		return this.isFocused;
	}

	/** Sets whether this text box is enabled. Disabled text boxes cannot be typed in. */
	public void setEnabled(boolean enabled) {
		this.isEnabled = enabled;
	}

	/** the side of the selection that is not the cursor, may be the same as the cursor */
	public int getSelectionEnd() {
		return this.selectionEnd;
	}

	/** returns the width of the textbox depending on if background drawing is enabled */
	public int getWidth() {
		return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
	}

	/** Sets the position of the selection anchor (the selection anchor and the cursor position mark the edges of the selection). If the anchor is set beyond the bounds of the current text, it will be put back inside. */
	public void setSelectionPos(int position) {
		String text = this.getText(currentLine);
		int i = text.length();

		if (position > i) {
			position = i;
		}

		if (position < 0) {
			position = 0;
		}

		this.selectionEnd = position;

		if (this.fontRendererInstance != null) {
			if (this.lineScrollOffset > i) {
				this.lineScrollOffset = i;
			}

			int j = this.getWidth();
			String s = this.fontRendererInstance.trimStringToWidth(text.substring(this.lineScrollOffset), j);
			int k = s.length() + this.lineScrollOffset;

			if (position == this.lineScrollOffset) {
				this.lineScrollOffset -= this.fontRendererInstance.trimStringToWidth(text, j, true).length();
			}

			if (position > k) {
				this.lineScrollOffset += position - k;
			} else if (position <= this.lineScrollOffset) {
				this.lineScrollOffset -= this.lineScrollOffset - position;
			}

			this.lineScrollOffset = MathHelper.clamp_int(this.lineScrollOffset, 0, i);
		}
	}

	/** Sets whether this text box loses focus when something other than it is clicked. */
	public void setCanLoseFocus(boolean canLoseFocusIn) {
		this.canLoseFocus = canLoseFocusIn;
	}

	/** returns true if this textbox is visible */
	public boolean getVisible() {
		return this.visible;
	}

	/** Sets whether or not this textbox is visible */
	public void setVisible(boolean isVisible) {
		this.visible = isVisible;
	}
}