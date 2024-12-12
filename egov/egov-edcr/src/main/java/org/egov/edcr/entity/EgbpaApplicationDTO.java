package org.egov.edcr.entity;

public class EgbpaApplicationDTO {
	
	private String plotnumber;
	
	private String sector;
	
	public String getPlotnumber() {
		return plotnumber;
	}
	public void setPlotnumber(String plotnumber) {
		this.plotnumber = plotnumber;
	}
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}
	
	 @Override
	    public String toString() {
	        return "BPAPlotDetails{" +
	                "plotnumber='" + plotnumber + '\'' +
	                ", sector='" + sector + '\'' +
	                '}';
	    }
	
	

}
