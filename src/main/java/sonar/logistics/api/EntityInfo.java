package sonar.logistics.api;

import sonar.logistics.info.providers.entity.EntityProviderRegistry;

public class EntityInfo extends StandardInfo {

	@Override
	public String getType() {
		return "Entity";
	}

	public EntityInfo() {

	}

	public EntityInfo(byte providerID, int category, int subCategory, Object data, String suffix) {
		super(providerID, category, subCategory, data, suffix);
	}

	public EntityInfo(byte providerID, String category, String subCategory, Object data, String suffix) {
		super(providerID, category, subCategory, data, suffix);
	}

	public EntityInfo(byte providerID, int category, int subCategory, Object data) {
		super(providerID, category, subCategory, data);
	}

	public EntityInfo(byte providerID, String category, String subCategory, Object data) {
		super(providerID, category, subCategory, data);
	}

	@Override
	public String getCategory() {
		return (catID == -1 || providerID == -1) ? category : EntityProviderRegistry.getProvider(providerID).getCategory(catID);
	}

	@Override
	public String getSubCategory() {
		return (subCatID == -1 || providerID == -1) ? subCategory : EntityProviderRegistry.getProvider(providerID).getSubCategory(subCatID);
	}

	@Override
	public Info newInfo() {
		return new EntityInfo();
	}
}
