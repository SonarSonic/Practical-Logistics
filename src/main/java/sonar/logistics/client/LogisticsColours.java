package sonar.logistics.client;

import sonar.core.utils.CustomColour;

public class LogisticsColours {

	public static CustomColour backgroundColour = new CustomColour(7, 7, 9);
	public static CustomColour grey_base = new CustomColour(5, 5, 2);
	public static CustomColour blue_overlay = new CustomColour(5, 5, 16);
	public static CustomColour category = new CustomColour(25, 25, 35);
	public static CustomColour grey_text = new CustomColour(105, 105, 116);
	public static CustomColour white_text = new CustomColour(170, 170, 170);

	public static CustomColour[] layers = new CustomColour[]{
			backgroundColour,//
			grey_base,//
			blue_overlay,//
	};
	
	public static CustomColour[] infoColours = new CustomColour[] { // all the info colours
			new CustomColour(5, 50, 2), // green
			new CustomColour(50, 5, 2), //
			new CustomColour(5, 50, 20), //
			new CustomColour(50, 50, 2), //
			new CustomColour(50, 150, 2), //
			new CustomColour(50, 150, 122), //
			new CustomColour(150, 150, 2), //
			new CustomColour(150, 150, 132),//

	};

	public static CustomColour getDefaultSelection() {
		return infoColours[0];
	}

}
