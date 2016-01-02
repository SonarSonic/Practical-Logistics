package sonar.logistics.api;

import java.util.ArrayList;
import java.util.List;

import sonar.core.utils.helpers.FontHelper;

public class DataCategoriesRegistry {

	public static List<String> categories = new ArrayList();

	public static void clearCategories() {
		categories.clear();
	}

	public static void registerCategories() {
		addCategory("EMPTY");
		addCategory("WORLD");
		addCategory("GENERAL");
		addCategory("PROPERTIES");
		addCategory("ENERGY");
		addCategory("FLUID");
	}

	public static void addCategory(String name) {
		categories.add(name);
	}

	public interface ICategory {
		int getCategory();
	}

	public List<String> getLocalisedList() {
		List<String> localised = new ArrayList();
		for (String string : categories) {
			localised.add(getLocalisedName(string));
		}
		return localised;
	}

	public String getLocalisedName(String name) {
		return FontHelper.translate("category." + name);

	}

}
