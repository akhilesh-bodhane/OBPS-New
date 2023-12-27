package org.egov.bpa.transaction.entity;

public class PropertyOwnerDetailsResponse {

	private String CategoryOfProperty;
	private String TenureType;
	private String NameOfOwners;
	private String OwnersShare;
	
	public String getCategoryOfProperty() {
		return CategoryOfProperty;
	}
	public void setCategoryOfProperty(String categoryOfProperty) {
		CategoryOfProperty = categoryOfProperty;
	}
	public String getTenureType() {
		return TenureType;
	}
	public void setTenureType(String tenureType) {
		TenureType = tenureType;
	}
	public String getNameOfOwners() {
		return NameOfOwners;
	}
	public void setNameOfOwners(String nameOfOwners) {
		NameOfOwners = nameOfOwners;
	}
	public String getOwnersShare() {
		return OwnersShare;
	}
	public void setOwnersShare(String ownersShare) {
		OwnersShare = ownersShare;
	}
	
	

}
