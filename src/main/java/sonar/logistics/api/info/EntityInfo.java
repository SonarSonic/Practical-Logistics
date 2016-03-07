package sonar.logistics.api.info;

import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.providers.EntityProvider;

public class EntityInfo extends StandardInfo {

	public EntityInfo() {}

	public EntityInfo(int providerID, int category, int subCategory, Object data) {
		super(providerID, category, subCategory, data);
	}

	public EntityInfo(int providerID, String category, String subCategory, Object data) {
		super(providerID, category, subCategory, data);
	}

	@Override
	public String getName() {
		return "Entity";
	}

	@Override
	public String getCategory() {
		EntityProvider provider = LogisticsAPI.getRegistry().getEntityProvider(providerID);
		return (provider==null || catID==-1) ? category : provider.getCategory(catID);
	}

	@Override
	public String getSubCategory() {
		EntityProvider provider = LogisticsAPI.getRegistry().getEntityProvider(providerID);
		return (provider==null || subCatID==-1) ? subCategory : provider.getSubCategory(subCatID);
	}

	@Override
	public EntityInfo instance() {
		return new EntityInfo();
	}
}
