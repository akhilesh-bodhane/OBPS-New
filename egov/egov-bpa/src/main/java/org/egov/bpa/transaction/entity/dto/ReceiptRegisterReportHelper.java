package org.egov.bpa.transaction.entity.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import org.egov.infra.web.support.search.DataTableSearchRequest;
import org.hibernate.validator.constraints.SafeHtml;

public class ReceiptRegisterReportHelper extends DataTableSearchRequest {
    private Long id;
    @SafeHtml
    private String applicationNumber;
    @SafeHtml
    private String receiptNumber;
    @SafeHtml
    private Date paymentDate;
    
    private String Sector;
    
    private String plotNumber;
    
    private String fileNumber;
    
    private Double securityFee=new Double(0.0);
    private Double scrutinyFee=new Double(0.0);
    private Double gst=new Double(0.0);
    private Double additionFee=new Double(0.0);
    private Double labourCess=new Double(0.0);
    private Double rule5=new Double(0.0);
    private Double totalWithoutLaboutCess=new Double(0.0);
    private Double total = new Double(0.0);
    
    private Double additionalHeightFee = new Double(0.0);  
	private Double constructionDemolision = new Double(0.0);
    private Double conversionCharges = new Double(0.0);
    private Double developmentChargesRoads = new Double(0.0);
    private Double DPCCertificateMissingFee = new Double(0.0);
    private Double falseCeilingFee = new Double(0.0);
    private Double additionalHeightSCFConvertSCOFee = new Double(0.0);
    private Double barsatiFloorHeightFee = new Double(0.0);
    private Double excessCoverageAreaFee = new Double(0.0);
    private Double excessCoverageBeyondRoning6Fee = new Double(0.0);
    private Double glazingVerandahFee = new Double(0.0);
    private Double internalChangesConstruction = new Double(0.0);
    private Double stairHeadwayHeightFee = new Double(0.0);
    private Double waterTankLocationFee = new Double(0.0);
    private Double loftsFee = new Double(0.0);
    private Double minorChangesDoorsWindowsFee = new Double(0.0);
    private Double nichesCommonWallFee = new Double(0.0);
    private Double nonStdGateFee = new Double(0.0);
    private Double transferFee = new Double(0.0);
    private Double transferBuildingPlanFee = new Double(0.0);
    
    

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

   

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ReceiptRegisterReportHelper))
            return false;
        ReceiptRegisterReportHelper that = (ReceiptRegisterReportHelper) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getApplicationNumber(), that.getApplicationNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getApplicationNumber());
    }

	public String getReceiptNumber() {
		return receiptNumber;
	}

	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSector() {
		return Sector;
	}

	public void setSector(String sector) {
		Sector = sector;
	}

	public String getPlotNumber() {
		return plotNumber;
	}

	public void setPlotNumber(String plotNumber) {
		this.plotNumber = plotNumber;
	}

	public String getFileNumber() {
		return fileNumber;
	}

	public void setFileNumber(String fileNumber) {
		this.fileNumber = fileNumber;
	}

	public Double getSecurityFee() {
		return securityFee;
	}

	public void setSecurityFee(Double securityFee) {
		this.securityFee = securityFee;
	}

	public Double getScrutinyFee() {
		return scrutinyFee;
	}

	public void setScrutinyFee(Double scrutinyFee) {
		this.scrutinyFee = scrutinyFee;
	}

	public Double getGst() {
		return gst;
	}

	public void setGst(Double gst) {
		this.gst = gst;
	}

	public Double getAdditionFee() {
		return additionFee;
	}

	public void setAdditionFee(Double additionFee) {
		this.additionFee = additionFee;
	}

	public Double getLabourCess() {
		return labourCess;
	}

	public void setLabourCess(Double labourCess) {
		this.labourCess = labourCess;
	}

	public Double getRule5() {
		return rule5;
	}

	public void setRule5(Double rule5) {
		this.rule5 = rule5;
	}

	public Double getTotalWithoutLaboutCess() {
		return totalWithoutLaboutCess;
	}

	public void setTotalWithoutLaboutCess(Double totalWithoutLaboutCess) {
		this.totalWithoutLaboutCess = totalWithoutLaboutCess;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Double getAdditionalHeightFee() {
		return additionalHeightFee;
	}

	public void setAdditionalHeightFee(Double additionalHeightFee) {
		this.additionalHeightFee = additionalHeightFee;
	}

	public Double getConstructionDemolision() {
		return constructionDemolision;
	}

	public void setConstructionDemolision(Double constructionDemolision) {
		this.constructionDemolision = constructionDemolision;
	}

	public Double getConversionCharges() {
		return conversionCharges;
	}

	public void setConversionCharges(Double conversionCharges) {
		this.conversionCharges = conversionCharges;
	}

	public Double getDevelopmentChargesRoads() {
		return developmentChargesRoads;
	}

	public void setDevelopmentChargesRoads(Double developmentChargesRoads) {
		this.developmentChargesRoads = developmentChargesRoads;
	}

	public Double getDPCCertificateMissingFee() {
		return DPCCertificateMissingFee;
	}

	public void setDPCCertificateMissingFee(Double dPCCertificateMissingFee) {
		DPCCertificateMissingFee = dPCCertificateMissingFee;
	}

	public Double getFalseCeilingFee() {
		return falseCeilingFee;
	}

	public void setFalseCeilingFee(Double falseCeilingFee) {
		this.falseCeilingFee = falseCeilingFee;
	}

	public Double getAdditionalHeightSCFConvertSCOFee() {
		return additionalHeightSCFConvertSCOFee;
	}

	public void setAdditionalHeightSCFConvertSCOFee(
			Double additionalHeightSCFConvertSCOFee) {
		this.additionalHeightSCFConvertSCOFee = additionalHeightSCFConvertSCOFee;
	}

	public Double getBarsatiFloorHeightFee() {
		return barsatiFloorHeightFee;
	}

	public void setBarsatiFloorHeightFee(Double barsatiFloorHeightFee) {
		this.barsatiFloorHeightFee = barsatiFloorHeightFee;
	}

	public Double getExcessCoverageAreaFee() {
		return excessCoverageAreaFee;
	}

	public void setExcessCoverageAreaFee(Double excessCoverageAreaFee) {
		this.excessCoverageAreaFee = excessCoverageAreaFee;
	}

	public Double getExcessCoverageBeyondRoning6Fee() {
		return excessCoverageBeyondRoning6Fee;
	}

	public void setExcessCoverageBeyondRoning6Fee(
			Double excessCoverageBeyondRoning6Fee) {
		this.excessCoverageBeyondRoning6Fee = excessCoverageBeyondRoning6Fee;
	}

	public Double getGlazingVerandahFee() {
		return glazingVerandahFee;
	}

	public void setGlazingVerandahFee(Double glazingVerandahFee) {
		this.glazingVerandahFee = glazingVerandahFee;
	}

	public Double getInternalChangesConstruction() {
		return internalChangesConstruction;
	}

	public void setInternalChangesConstruction(Double internalChangesConstruction) {
		this.internalChangesConstruction = internalChangesConstruction;
	}

	public Double getStairHeadwayHeightFee() {
		return stairHeadwayHeightFee;
	}

	public void setStairHeadwayHeightFee(Double stairHeadwayHeightFee) {
		this.stairHeadwayHeightFee = stairHeadwayHeightFee;
	}

	public Double getWaterTankLocationFee() {
		return waterTankLocationFee;
	}

	public void setWaterTankLocationFee(Double waterTankLocationFee) {
		this.waterTankLocationFee = waterTankLocationFee;
	}

	public Double getLoftsFee() {
		return loftsFee;
	}

	public void setLoftsFee(Double loftsFee) {
		this.loftsFee = loftsFee;
	}

	public Double getMinorChangesDoorsWindowsFee() {
		return minorChangesDoorsWindowsFee;
	}

	public void setMinorChangesDoorsWindowsFee(Double minorChangesDoorsWindowsFee) {
		this.minorChangesDoorsWindowsFee = minorChangesDoorsWindowsFee;
	}

	public Double getNichesCommonWallFee() {
		return nichesCommonWallFee;
	}

	public void setNichesCommonWallFee(Double nichesCommonWallFee) {
		this.nichesCommonWallFee = nichesCommonWallFee;
	}

	public Double getNonStdGateFee() {
		return nonStdGateFee;
	}

	public void setNonStdGateFee(Double nonStdGateFee) {
		this.nonStdGateFee = nonStdGateFee;
	}

	public Double getTransferFee() {
		return transferFee;
	}

	public void setTransferFee(Double transferFee) {
		this.transferFee = transferFee;
	}

	public Double getTransferBuildingPlanFee() {
		return transferBuildingPlanFee;
	}

	public void setTransferBuildingPlanFee(Double transferBuildingPlanFee) {
		this.transferBuildingPlanFee = transferBuildingPlanFee;
	}

}
