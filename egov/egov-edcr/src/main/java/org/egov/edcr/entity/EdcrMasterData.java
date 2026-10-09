package org.egov.edcr.entity;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.tracer.annotations.CustomSafeHtml;

@Entity
@Table(name= "EGDCR_MSTR_DATA_UPLOAD")
@SequenceGenerator(name = EdcrMasterData.SEQ_EDCR_MSTR_DATA_UPLOAD, sequenceName = EdcrMasterData.SEQ_EDCR_MSTR_DATA_UPLOAD, allocationSize = 1)
public class EdcrMasterData implements Serializable {
	
	public static final String SEQ_EDCR_MSTR_DATA_UPLOAD = "SEQ_EDCR_MSTR_DATA_UPLOAD";
    private static final long serialVersionUID = 61L;
    
    @Id
    @GeneratedValue(generator = SEQ_EDCR_MSTR_DATA_UPLOAD, strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @CustomSafeHtml
    private String occupancyType;
    
    @CustomSafeHtml
    private String subOccupancyType;
    
    @CustomSafeHtml
    private String code;
    
    @CustomSafeHtml
    private String phase;
    
    @CustomSafeHtml
    private String sector;
    
    @CustomSafeHtml
    private String plotNo;
    
    @CustomSafeHtml
    private String bycWidth;
    
    @CustomSafeHtml
    private String bycHeight;
    
    @CustomSafeHtml
    private String plotArea;
    
    @CustomSafeHtml
    private String plotAreaSqYards;
    
    @CustomSafeHtml
    private String areaType;
    
    @CustomSafeHtml
    private String plotDepthAvg;
    
    @CustomSafeHtml
    private String plotDepthWidthAvg;
    
    @CustomSafeHtml
    private String permissibleBuildingStories;
    
    @CustomSafeHtml
    private String permissibleBuildingHeight;
    
    @CustomSafeHtml
    private String maxPermissibleFar;
    
    @CustomSafeHtml
    private String minPermissibleSetBackFront;
    
    @CustomSafeHtml
    private String minPermissibleSetBackRear;
    
    @CustomSafeHtml
    private String minPermissibleSetBackRight;
    
    @CustomSafeHtml
    private String minPermissibleSetBackLeft;
    
    @CustomSafeHtml
    private String drawingNumber;
    
    @CustomSafeHtml
    private String jobNumber; 
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public String getOccupancyType() {
		return occupancyType;
	}

	public void setOccupancyType(String occupancyType) {
		this.occupancyType = occupancyType;
	}

	public String getSubOccupancyType() {
		return subOccupancyType;
	}

	public void setSubOccupancyType(String subOccupancyType) {
		this.subOccupancyType = subOccupancyType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getPlotNo() {
		return plotNo;
	}

	public void setPlotNo(String plotNo) {
		this.plotNo = plotNo;
	}

	public String getBycWidth() {
		return bycWidth;
	}

	public void setBycWidth(String bycWidth) {
		this.bycWidth = bycWidth;
	}

	public String getBycHeight() {
		return bycHeight;
	}

	public void setBycHeight(String bycHeight) {
		this.bycHeight = bycHeight;
	}

	public String getPlotArea() {
		return plotArea;
	}

	public void setPlotArea(String plotArea) {
		this.plotArea = plotArea;
	}

	public String getPlotAreaSqYards() {
		return plotAreaSqYards;
	}

	public void setPlotAreaSqYards(String plotAreaSqYards) {
		this.plotAreaSqYards = plotAreaSqYards;
	}

	public String getAreaType() {
		return areaType;
	}

	public void setAreaType(String areaType) {
		this.areaType = areaType;
	}

	public String getPlotDepthAvg() {
		return plotDepthAvg;
	}

	public void setPlotDeptthAvg(String plotDepthAvg) {
		this.plotDepthAvg = plotDepthAvg;
	}

	public String getPlotDepthWidthAvg() {
		return plotDepthWidthAvg;
	}

	public void setPlotDepthWidthAvg(String plotDepthWidthAvg) {
		this.plotDepthWidthAvg = plotDepthWidthAvg;
	}

	public String getPermissibleBuildingStories() {
		return permissibleBuildingStories;
	}

	public void setPermissibleBuildingStories(String permissibleBuildingStories) {
		this.permissibleBuildingStories = permissibleBuildingStories;
	}

	public String getPermissibleBuildingHeight() {
		return permissibleBuildingHeight;
	}

	public void setPermissibleBuildingHeight(String permissibleBuildingHeight) {
		this.permissibleBuildingHeight = permissibleBuildingHeight;
	}

	public String getMaxPermissibleFar() {
		return maxPermissibleFar;
	}

	public void setMaxPermissibleFar(String maxPermissibleFar) {
		this.maxPermissibleFar = maxPermissibleFar;
	}

	public String getMinPermissibleSetBackFront() {
		return minPermissibleSetBackFront;
	}

	public void setMinPermissibleSetBackFront(String minPermissibleSetBackFront) {
		this.minPermissibleSetBackFront = minPermissibleSetBackFront;
	}

	public String getMinPermissibleSetBackRear() {
		return minPermissibleSetBackRear;
	}

	public void setMinPermissibleSetBackRear(String minPermissibleSetBackRear) {
		this.minPermissibleSetBackRear = minPermissibleSetBackRear;
	}

	public String getMinPermissibleSetBackRight() {
		return minPermissibleSetBackRight;
	}

	public void setMinPermissibleSetBackRight(String minPermissibleSetBackRight) {
		this.minPermissibleSetBackRight = minPermissibleSetBackRight;
	}

	public String getMinPermissibleSetBackLeft() {
		return minPermissibleSetBackLeft;
	}

	public void setMinPermissibleSetBackLeft(String minPermissibleSetBackLeft) {
		this.minPermissibleSetBackLeft = minPermissibleSetBackLeft;
	}

	public String getDrawingNumber() {
		return drawingNumber;
	}

	public void setDrawingNumber(String drawingNumber) {
		this.drawingNumber = drawingNumber;
	}

	public String getJobNumber() {
		return jobNumber;
	}

	public void setJobNumber(String jobNumber) {
		this.jobNumber = jobNumber;
	}

}
