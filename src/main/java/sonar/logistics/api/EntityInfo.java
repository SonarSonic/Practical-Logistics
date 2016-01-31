package sonar.logistics.api;

import sonar.logistics.Logistics;

public class EntityInfo extends StandardInfo {

	@Override
	public String getName() {
		return "Entity";
	}

	public EntityInfo() {

	}
	
	public EntityInfo(byte providerID, int category, int subCategory, Object data) {
		super(providerID, category, subCategory, data);
	}

	public EntityInfo(byte providerID, String category, String subCategory, Object data) {
		super(providerID, category, subCategory, data);
	}

	@Override
	public String getCategory() {
		return (catID == -1 || providerID == -1) ? category : Logistics.entityProviders.getRegisteredObject(providerID).getCategory(catID);
	}

	@Override
	public String getSubCategory() {
		return (subCatID == -1 || providerID == -1) ? subCategory : Logistics.entityProviders.getRegisteredObject(providerID).getSubCategory(subCatID);
	}

	@Override
	public EntityInfo instance() {
		return new EntityInfo();
	}
}
