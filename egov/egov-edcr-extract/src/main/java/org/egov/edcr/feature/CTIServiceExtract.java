package org.egov.edcr.feature;

import org.egov.edcr.entity.blackbox.PlanDetail;
import org.springframework.stereotype.Service;

// STUBBED OUT: original implementation depends on org.egov.common.entity.edcr classes
// that do not exist in this branch's egov-commons (build blocker, not fixed here).
// Original source preserved below as a comment.
@Service("cTIServiceExtract")
public class CTIServiceExtract extends FeatureExtract {

    @Override
    public PlanDetail extract(PlanDetail planDetail) {
        return planDetail;
    }

    @Override
    public PlanDetail validate(PlanDetail planDetail) {
        return planDetail;
    }
}

// ===== ORIGINAL IMPLEMENTATION (commented out) =====
// package org.egov.edcr.feature;
// 
// import java.math.BigDecimal;
// import java.util.List;
// import java.util.Map;
// 
// import org.apache.log4j.Logger;
// import org.egov.common.entity.edcr.Block;
// import org.egov.common.entity.edcr.CTI;
// import org.egov.common.entity.edcr.Measurement;
// import org.egov.edcr.entity.blackbox.MeasurementDetail;
// import org.egov.edcr.entity.blackbox.PlanDetail;
// import org.egov.edcr.utility.Util;
// import org.kabeja.dxf.DXFLWPolyline;
// import org.springframework.stereotype.Service;
// 
// @Service("cTIServiceExtract")
// public class CTIServiceExtract extends FeatureExtract {
//     
//     private static final Logger LOGGER = Logger.getLogger(CTIServiceExtract.class);
// 
//     @Override
//     public PlanDetail extract(PlanDetail planDetail) {
//         if (LOGGER.isDebugEnabled())
//             LOGGER.debug("Starting of CTI Service Extract......");
//         
//         extractCTI(planDetail);
//         extractCTIDimensions(planDetail);
//         
//         if (LOGGER.isDebugEnabled())
//             LOGGER.debug("End of CTI Service Extract......");
//         
//         return planDetail;
//     }
// 
//     @Override
//     public PlanDetail validate(PlanDetail planDetail) {
//         return planDetail;
//     }
// 
//     public PlanDetail extractCTI(PlanDetail planDetail) {
//         
//         for (Block block : planDetail.getBlocks()) {
//             if (block.getBuilding() != null && block.getBuilding().getFloors() != null) {
//                 for (org.egov.common.entity.edcr.Floor floor : block.getBuilding().getFloors()) {
//                     
//                     String ctiLayerPrefix = "BLK_" + block.getNumber() + "_FLR_" + floor.getNumber() + "_IBS_FOR_CTI_";
//                    
//                     String ctiLayerRegex = ctiLayerPrefix + "\\d+.*";
//                     List<String> matchingLayers = Util.getLayerNamesLike(planDetail.getDoc(), ctiLayerRegex);
//                     
//                     for (String ctiLayerPattern : matchingLayers) {
//                         List<DXFLWPolyline> ctiPolylines = Util.getPolyLinesByLayer(planDetail.getDoc(), ctiLayerPattern);
//                         
//                         if (ctiPolylines != null && !ctiPolylines.isEmpty()) {
//                             if (LOGGER.isDebugEnabled())
//                                 LOGGER.debug("Found " + ctiPolylines.size() + " CTI polylines in Block " 
//                                         + block.getNumber() + ", Floor " + floor.getNumber() + ", Layer: " + ctiLayerPattern);
//                             
//                             for (DXFLWPolyline pline : ctiPolylines) {
//                                 Measurement measurement = new MeasurementDetail(pline, true);
//                                 CTI cti = new CTI();
//                                 cti.setColorCode(measurement.getColorCode());
//                                 cti.setLength(measurement.getHeight());
//                                 cti.setWidth(measurement.getWidth());
//                                 cti.setArea(measurement.getArea());
//                                 cti.setInvalidReason(measurement.getInvalidReason());
//                                 cti.setPresentInDxf(true);
//                                 
//                                 cti.setFacilityType(getFacilityTypeFromColorCode(planDetail, measurement.getColorCode()));
//                                 
//                                 cti.setBlockNumber(block.getNumber());
//                                 cti.setFloorNumber(String.valueOf(floor.getNumber()));
//                                 
//                                 if (LOGGER.isDebugEnabled())
//                                     LOGGER.debug("Extracted CTI - Block: " + cti.getBlockNumber()
//                                             + ", Floor: " + cti.getFloorNumber()
//                                             + ", Facility Type: " + cti.getFacilityType()
//                                             + ", Color: " + cti.getColorCode()
//                                             + ", Area: " + cti.getArea());
//                                 
//                                 planDetail.getCtis().add(cti);
//                             }
//                         }
//                     }
//                 }
//             }
//         }
// 
//         return planDetail;
//     }
//     
//     public PlanDetail extractCTIDimensions(PlanDetail planDetail) {
//         
//         Map<String, Integer> ctiDimensionColors = planDetail.getSubFeatureColorCodesMaster().get("CTIDim");
//         
//         if (ctiDimensionColors == null || ctiDimensionColors.isEmpty()) {
//             if (LOGGER.isDebugEnabled())
//                 LOGGER.debug("CTI dimension color codes not found in subFeatureColorCodesMaster");
//             return planDetail;
//         }
//         
//         Integer lengthColorCode = ctiDimensionColors.get("Length");
//         Integer widthColorCode = ctiDimensionColors.get("Width");
//         
//         if (lengthColorCode == null || widthColorCode == null) {
//             if (LOGGER.isDebugEnabled())
//                 LOGGER.debug("Length or Width color code not defined for CTI dimensions");
//             return planDetail;
//         }
//         
//         for (Block block : planDetail.getBlocks()) {
//             String efDimLayer = "BLK_" + block.getNumber() + "_FLR_0_IBS_EF_DIM";
//             updateCTIWithDimensions(planDetail, efDimLayer, "Entrance facility", 
//                     block.getNumber(), "0", lengthColorCode, widthColorCode);
//             
//             if (block.getBuilding() != null && block.getBuilding().getFloors() != null) {
//                 for (org.egov.common.entity.edcr.Floor floor : block.getBuilding().getFloors()) {
//                     String mdfDimLayer = "BLK_" + block.getNumber() + "_FLR_" + 
//                             floor.getNumber() + "_IBS_MDF_DIM";
//                     updateCTIWithDimensions(planDetail, mdfDimLayer, "Main Distribution Frame (MDF)/ Equipment Room (ER)", 
//                             block.getNumber(), String.valueOf(floor.getNumber()), lengthColorCode, widthColorCode);
//                 }
//             }
//         }
//         
//         return planDetail;
//     }
//     
//     private void updateCTIWithDimensions(PlanDetail planDetail, String layerName, 
//             String facilityType, String blockNumber, String floorNumber, 
//             Integer lengthColorCode, Integer widthColorCode) {
//         
//         if (planDetail.getDoc().containsDXFLayer(layerName)) {
//             Map<Integer, List<BigDecimal>> dimensionsByColor = 
//                     Util.extractAndMapDimensionValuesByColorCode(planDetail, layerName);
//             
//             if (dimensionsByColor != null && !dimensionsByColor.isEmpty()) {
//                 List<BigDecimal> lengthValues = dimensionsByColor.get(lengthColorCode);
//                 List<BigDecimal> widthValues = dimensionsByColor.get(widthColorCode);
//                 
//                 BigDecimal length = (lengthValues != null && !lengthValues.isEmpty()) 
//                         ? lengthValues.get(0) : BigDecimal.ZERO;
//                 BigDecimal width = (widthValues != null && !widthValues.isEmpty()) 
//                         ? widthValues.get(0) : BigDecimal.ZERO;
//                 
//                 if (LOGGER.isDebugEnabled()) {
//                     LOGGER.debug("Extracted dimensions from layer " + layerName 
//                             + " - Length: " + length + ", Width: " + width);
//                 }
//                 
//                 boolean found = false;
//                 for (CTI cti : planDetail.getCtis()) {
//                     if (facilityType.equals(cti.getFacilityType()) 
//                             && blockNumber.equals(cti.getBlockNumber())
//                             && floorNumber.equals(cti.getFloorNumber())) {
//                         
//                         cti.setLength(length);
//                         cti.setWidth(width);
//                         cti.setArea(length.multiply(width));
//                         
//                         found = true;
//                         
//                         if (LOGGER.isDebugEnabled()) {
//                             LOGGER.debug("Updated CTI with dimensions - Facility: " + facilityType 
//                                     + ", Block: " + blockNumber + ", Floor: " + floorNumber 
//                                     + ", Length: " + length + ", Width: " + width);
//                         }
//                         break; // Assuming one dimension per facility per floor
//                     }
//                 }
//                 
//                 if (!found) {
//                     LOGGER.debug("No matching CTI found for dimensions - Facility: " + facilityType 
//                             + ", Block: " + blockNumber + ", Floor: " + floorNumber);
//                     CTI newCti = new CTI();
//                     newCti.setFacilityType(facilityType);
//                     newCti.setBlockNumber(blockNumber);
//                     newCti.setFloorNumber(floorNumber);
//                     newCti.setLength(length);
//                     newCti.setWidth(width);
//                     try {
//                         newCti.setArea(length.multiply(width));
//                     } catch (Exception ex) {
//                         newCti.setArea(BigDecimal.ZERO);
//                     }
//                     newCti.setPresentInDxf(true);
//                     planDetail.getCtis().add(newCti);
//                 }
//             }
//         }
//     }
//     
//     private String getFacilityTypeFromColorCode(PlanDetail planDetail, int colorCode) {
//         Map<String, Integer> ctiColorCodes = planDetail.getSubFeatureColorCodesMaster().get("CTI");
//         
//         if (ctiColorCodes == null || ctiColorCodes.isEmpty()) {
//             if (LOGGER.isDebugEnabled())
//                 LOGGER.debug("CTI color codes not found in subFeatureColorCodesMaster");
//             return "Unknown";
//         }
//         
//         for (Map.Entry<String, Integer> entry : ctiColorCodes.entrySet()) {
//             if (entry.getValue() != null && entry.getValue() == colorCode) {
//                 return entry.getKey();
//             }
//         }
// 
//         planDetail.addError("CTIColorCode","No CTI facility type defined for color code: " + colorCode);
//         
//         return "Unknown";
//     }
// }
// ===== END ORIGINAL IMPLEMENTATION =====
