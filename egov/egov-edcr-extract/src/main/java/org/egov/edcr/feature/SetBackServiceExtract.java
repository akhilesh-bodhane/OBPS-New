package org.egov.edcr.feature;

import org.egov.edcr.entity.blackbox.PlanDetail;
import org.springframework.stereotype.Service;

// STUBBED OUT: original implementation depends on org.egov.common.entity.edcr classes
// that do not exist in this branch's egov-commons (build blocker, not fixed here).
// Original source preserved below as a comment.
@Service
public class SetBackServiceExtract extends FeatureExtract {

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
// import static org.egov.edcr.utility.DcrConstants.MORETHANONEPOLYLINEDEFINED;
// import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
// 
// import java.math.BigDecimal;
// import java.util.Collections;
// import java.util.List;
// 
// import org.apache.log4j.Logger;
// import org.egov.common.entity.edcr.Block;
// import org.egov.common.entity.edcr.SetBack;
// import org.egov.edcr.constants.DxfFileConstants;
// import org.egov.edcr.entity.blackbox.PlanDetail;
// import org.egov.edcr.entity.blackbox.YardDetail;
// import org.egov.edcr.service.LayerNames;
// import org.egov.edcr.utility.MinDistance;
// import org.egov.edcr.utility.Util;
// import org.kabeja.dxf.DXFDocument;
// import org.kabeja.dxf.DXFLWPolyline;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// 
// @Service
// public class SetBackServiceExtract extends FeatureExtract {
// 
//     private static final Logger LOG = Logger.getLogger(SetBackServiceExtract.class);
//     @Autowired
//     private LayerNames layerNames;
//     @Autowired
//     private MinDistance minDistance;
// 
//     public static String ERR_MIN_DISTANCE = "Minimum distance is not defined in layer %s";
//     
//     private String blockPrefixKey = "";
//     private String levelPrefixKey = "";
//     private String frontYardLayerName = "";
//     private String rearYardLayerName = "";
//     private String sideYard1LayerName = "";
//     private String sideYard2LayerName = "";
//     private String overrideSuffix = "";
//     private String basementFrontYardLayerName = "";
//     private String basementRearYardLayerName = "";
//     private String basementSideYard1LayerName = "";
//     private String basementSideYard2LayerName = "";
//     
//     @Override
//     public PlanDetail extract(PlanDetail pl) {
//     	
//     	blockPrefixKey = layerNames.getLayerName("LAYER_NAME_BLOCK_NAME_PREFIX");
//         levelPrefixKey = layerNames.getLayerName("LAYER_NAME_LEVEL_NAME_PREFIX");
//         frontYardLayerName = layerNames.getLayerName("LAYER_NAME_FRONT_YARD");
//         rearYardLayerName = layerNames.getLayerName("LAYER_NAME_REAR_YARD");
//         sideYard1LayerName = layerNames.getLayerName("LAYER_NAME_SIDE_YARD_1");
//         sideYard2LayerName = layerNames.getLayerName("LAYER_NAME_SIDE_YARD_2");
//         overrideSuffix = layerNames.getLayerName("LAYER_NAME_OVERRIDE");
//         basementFrontYardLayerName = layerNames.getLayerName("LAYER_NAME_BSMNT_FRONT_YARD");
//         basementRearYardLayerName = layerNames.getLayerName("LAYER_NAME_BSMNT_REAR_YARD");
//         basementSideYard1LayerName = layerNames.getLayerName("LAYER_NAME_BSMNT_SIDE_YARD_1");
//         basementSideYard2LayerName = layerNames.getLayerName("LAYER_NAME_BSMNT_SIDE_YARD_2");
//         
//         
//         extractSetBack(pl, pl.getDoc());    
//         
//         processOverrideSetback(pl, pl.getDoc());
//         return pl;
//     }
// 
// 	@Override
//     public PlanDetail validate(PlanDetail pl) {
//         return pl;
//     }
// 
//     private void extractSetBack(PlanDetail pl, DXFDocument doc) {
// //        LOG.info("Starting set back Extract......");
//         String yardName;
//         // VALIDATION : CHECK NUMBER OF BLOCKS and floors. Check block height provided ?
//         // Check whether level defined ? if yes, then check level height is correct
//         // format ?
//         // check whether for each block setback defined ?
//         // side/front/front yard.. Not necessary to define level for all the side.. if
//         // any one side define also.. we need to
//         // consider
//         // Each block combine multiple occupancies to decide the most restrictive
//         // occupancy.
//         // if height is more than building height in the level. if more than one level,
//         // then height is mandatory from 1st level.
//         // It should be greater than previous level.
//         // they may or may not define yards in that case ..?? throw error ? required
//         // only other than level cases.
//         // if all levels not defined, then how to using building height ?
//         // extract NOC Details and opening above 2.1mt etc.
//         
//         
// 
//         for (Block block : pl.getBlocks()) {
// //            LOG.info("Block....   " + block.getName());
// 
//             // extractBasementFootPrint(doc, block);
// 
//             // based on foot prints provided, set back will be decide in general rule.
//             for (SetBack setBack : block.getSetBacks())
//                 if (setBack.getLevel() < 0)
//                     extractBasementSetBacks(pl, doc, block, setBack);
//                 else {
//                     yardName = blockPrefixKey + block.getName() + "_"
//                             + levelPrefixKey + setBack.getLevel() + "_"
//                             + frontYardLayerName;
//                     setFrontYardDetails(pl, doc, setBack, yardName);
//                     yardName = blockPrefixKey + block.getName() + "_"
//                             + levelPrefixKey + setBack.getLevel() + "_"
//                             + rearYardLayerName;
//                     setRearYardDetails(pl, doc, setBack, yardName);
//                     yardName = blockPrefixKey + block.getName() + "_"
//                             + levelPrefixKey + setBack.getLevel() + "_"
//                             + sideYard1LayerName;
//                     setSideYard1Details(pl, doc, setBack, yardName);
//                     yardName = blockPrefixKey + block.getName() + "_"
//                             + levelPrefixKey + setBack.getLevel() + "_"
//                             + sideYard2LayerName;
//                     setSideYard2Details(pl, doc, yardName, setBack);
//                 }
//         }
//         pl.sortBlockByName();
//         pl.sortSetBacksByLevel();
// //        LOG.info("End of set back Extract......");
// 
//     }
//     
// 	private void processOverrideSetback(PlanDetail pl, DXFDocument doc) {
// 		String yardNameOverride;
// 		/*
// 		 * Override setback: Will override any setback when the layer is present in the
// 		 * drawing.
// 		 * 
// 		 */      
//         
// 		for (Block block : pl.getBlocks()) {
// //			LOG.info("block" + block.getColorCode());
// 
// 			for (SetBack setBack : block.getSetBacks()) {
// 				yardNameOverride = blockPrefixKey + block.getName() + "_"
// 						+ levelPrefixKey + setBack.getLevel() + "_"
// 						+ frontYardLayerName + "_"
// 						+ overrideSuffix;
// 				setFrontYardOverrideDetails(pl, doc, setBack, yardNameOverride);
// 				yardNameOverride = blockPrefixKey + block.getName() + "_"
// 						+ levelPrefixKey + setBack.getLevel() + "_"
// 						+ rearYardLayerName + "_"
// 						+ overrideSuffix;
// 				setRearYardOverrideDetails(pl, doc, setBack, yardNameOverride);
// 				yardNameOverride = blockPrefixKey + block.getName() + "_"
// 						+ levelPrefixKey + setBack.getLevel() + "_"
// 						+ sideYard1LayerName + "_"
// 						+ overrideSuffix;
// 				setSideYard1OverrideDetails(pl, doc, setBack, yardNameOverride);
// 				yardNameOverride = blockPrefixKey + block.getName() + "_"
// 						+ levelPrefixKey + setBack.getLevel() + "_"
// 						+ sideYard2LayerName + "_"
// 						+ overrideSuffix;
// 				setSideYard2OverrideDetails(pl, doc, setBack, yardNameOverride);
// 			}
// 		}
// 	}
// 
// 	private void setSideYard2Details(PlanDetail pl, DXFDocument doc, String yardName, SetBack setBack) {
//         boolean layerPresent;
//         layerPresent = doc.containsDXFLayer(yardName);
// 
//         if (layerPresent) {
//             YardDetail yard = getYard(pl, doc, yardName, setBack.getLevel());
//             if (yard != null && yard.getPolyLine() != null) {
//                 setBack.setSideYard2(yard);
//                 if (pl.getDrawingPreference() != null &&
//                         org.egov.infra.utils.StringUtils.isNotBlank(pl.getDrawingPreference().getUom())
//                         && (DxfFileConstants.INCH_UOM.equalsIgnoreCase(pl.getDrawingPreference().getUom())
//                                 || DxfFileConstants.FEET_UOM.equalsIgnoreCase(pl.getDrawingPreference().getUom()))) {
//                     List<BigDecimal> yardWidthDistance = Util.getListOfDimensionByColourCode(pl, yardName,
//                             DxfFileConstants.YARD_DIMENSION_COLOR);
//                     if (!yardWidthDistance.isEmpty()) {
//                         yard.setMinimumDistance(Collections.min(yardWidthDistance));
//                     } else {
//                         pl.addError(yardName + "_MIN_DISTANCE", String.format(ERR_MIN_DISTANCE, yardName));
//                     }
//                 } else {
//                 	List<BigDecimal> yardWidthDistance = Util.getListOfDimensionByColourCode(pl, yardName,
//                             DxfFileConstants.YARD_DIMENSION_COLOR);
//                     if (!yardWidthDistance.isEmpty()) {
//                         yard.setMinimumDistance(Collections.min(yardWidthDistance));
//                     } else {
//                     	yard.setMinimumDistance(
//                                 minDistance.getYardMinDistance(pl, yardName, String.valueOf(setBack.getLevel()), doc));
//                     }
//                     
//                 }
//                 setYardHeight(doc, yardName, yard);
//             }
//         }
//     }
// 
//     private void setYardHeight(DXFDocument doc, String yardName, YardDetail yard) {
//         String height = Util.getMtextByLayerName(doc, yardName, "");// change this api to get by using layer name and
//                                                                     // text.
//         if (height != null) {
//             if (height.contains("="))
//                 height = height.split("=")[1] != null ? height.split("=")[1].replaceAll("[^\\d.]", "") : "";
//             else
//                 height = height.replaceAll("[^\\d.]", "");
// 
//             if (!height.isEmpty())
//                 yard.setHeight(BigDecimal.valueOf(Double.parseDouble(height)));
//         }
//     }
// 
//     private YardDetail getYard(PlanDetail pl, DXFDocument doc, String yardName, Integer level) {
//         YardDetail yard = new YardDetail();
//         List<DXFLWPolyline> frontYardLines = Util.getPolyLinesByLayer(doc, yardName);
// 
//         // VALIDATE WHETHER ONE SINGLE POLYLINE PRESENT.
//         if (frontYardLines != null && frontYardLines.size() > 1)
//             pl.addError("", edcrMessageSource.getMessage(MORETHANONEPOLYLINEDEFINED, new String[] { yardName }, null));
//         else if (frontYardLines != null && !frontYardLines.isEmpty()) {
//             yard.setPolyLine(frontYardLines.get(0));
//             yard.setArea(Util.getPolyLineArea(yard.getPolyLine()));
//             yard.setPresentInDxf(true);
//             yard.setLevel(level);
// 
//         }
//         return yard;
// 
//     }
// 
//         private void extractBasementSetBacks(PlanDetail pl, DXFDocument doc, Block block, SetBack setBack) {
//                 String bsmntYardName = blockPrefixKey + block.getNumber() + "_"
//                                 + levelPrefixKey + setBack.getLevel() + "_"
//                                 + basementFrontYardLayerName;
//                 setFrontYardDetails(pl, doc, setBack, bsmntYardName);
//                 bsmntYardName = blockPrefixKey + block.getNumber() + "_"
//                                 + levelPrefixKey + setBack.getLevel() + "_"
//                                 + basementRearYardLayerName;
//                 setRearYardDetails(pl, doc, setBack, bsmntYardName);
//                 bsmntYardName = blockPrefixKey + block.getNumber() + "_"
//                                 + levelPrefixKey + setBack.getLevel() + "_"
//                                 + basementSideYard1LayerName;
//                 setSideYard1Details(pl, doc, setBack, bsmntYardName);
//                 bsmntYardName = blockPrefixKey + block.getNumber() + "_"
//                                 + levelPrefixKey + setBack.getLevel() + "_"
//                                 + basementSideYard2LayerName;
//                 setSideYard2Details(pl, doc, bsmntYardName, setBack);
//         }
// 
//     private void setSideYard1Details(PlanDetail pl, DXFDocument doc, SetBack setBack, String yardName) {
//         boolean layerPresent;
//         layerPresent = doc.containsDXFLayer(yardName);
//         if (layerPresent) {
//             YardDetail sideYard1 = getYard(pl, doc, yardName, setBack.getLevel());
//             if (sideYard1 != null && sideYard1.getPolyLine() != null) {
//                 setBack.setSideYard1(sideYard1);
//                 if (pl.getDrawingPreference() != null &&
//                         org.egov.infra.utils.StringUtils.isNotBlank(pl.getDrawingPreference().getUom())
//                         && (DxfFileConstants.INCH_UOM.equalsIgnoreCase(pl.getDrawingPreference().getUom())
//                                 || DxfFileConstants.FEET_UOM.equalsIgnoreCase(pl.getDrawingPreference().getUom()))) {
//                     List<BigDecimal> yardWidthDistance = Util.getListOfDimensionByColourCode(pl, yardName,
//                             DxfFileConstants.YARD_DIMENSION_COLOR);
//                     if (!yardWidthDistance.isEmpty()) {
//                         sideYard1.setMinimumDistance(Collections.min(yardWidthDistance));
//                     } else {
//                         pl.addError(yardName + "_MIN_DISTANCE", String.format(ERR_MIN_DISTANCE, yardName));
//                     }
//                 } else {
//                 	List<BigDecimal> yardWidthDistance = Util.getListOfDimensionByColourCode(pl, yardName,
//                             DxfFileConstants.YARD_DIMENSION_COLOR);
//                     if (!yardWidthDistance.isEmpty()) {
//                         sideYard1.setMinimumDistance(Collections.min(yardWidthDistance));
//                     } else {
//                     	sideYard1.setMinimumDistance(
//                                 minDistance.getYardMinDistance(pl, yardName, String.valueOf(setBack.getLevel()), doc));
//                     }
//                 }
//                 setYardHeight(doc, yardName, sideYard1);
//             } 
//         }
//     }
// 
//     private void yardNotDefined(PlanDetail pl, String yardName) {
//         pl.addError("", edcrMessageSource.getMessage(OBJECTNOTDEFINED, new String[] { yardName }, null));
//     }
// 
//     private void setRearYardDetails(PlanDetail pl, DXFDocument doc, SetBack setBack, String yardName) {
//         boolean layerPresent;
//         layerPresent = doc.containsDXFLayer(yardName);
//         if (layerPresent) {
//             YardDetail rearYard = getYard(pl, doc, yardName, setBack.getLevel());
//             if (rearYard != null && rearYard.getPolyLine() != null) {
//                 setBack.setRearYard(rearYard);
//                 if (pl.getDrawingPreference() != null &&
//                         org.egov.infra.utils.StringUtils.isNotBlank(pl.getDrawingPreference().getUom())
//                         && (DxfFileConstants.INCH_UOM.equalsIgnoreCase(pl.getDrawingPreference().getUom())
//                                 || DxfFileConstants.FEET_UOM.equalsIgnoreCase(pl.getDrawingPreference().getUom()))) {
//                     List<BigDecimal> yardWidthDistance = Util.getListOfDimensionByColourCode(pl, yardName,
//                             DxfFileConstants.YARD_DIMENSION_COLOR);
//                     if (!yardWidthDistance.isEmpty()) {
//                         rearYard.setMinimumDistance(Collections.min(yardWidthDistance));
//                     } else {
//                         pl.addError(yardName + "_MIN_DISTANCE", String.format(ERR_MIN_DISTANCE, yardName));
//                     }
//                 } else {
//                 	 List<BigDecimal> yardWidthDistance = Util.getListOfDimensionByColourCode(pl, yardName,
//                              DxfFileConstants.YARD_DIMENSION_COLOR);
//                      if (!yardWidthDistance.isEmpty()) {
//                          rearYard.setMinimumDistance(Collections.min(yardWidthDistance));
//                      } else {
//                     	 rearYard.setMinimumDistance(
//                                  minDistance.getYardMinDistance(pl, yardName, String.valueOf(setBack.getLevel()), doc));
//                      }
//                 }
//                 setYardHeight(doc, yardName, rearYard);
//             }
//         }
//     }
// 
//     private void setFrontYardDetails(PlanDetail pl, DXFDocument doc, SetBack setBack, String yardName) {
//         boolean layerPresent = doc.containsDXFLayer(yardName);
//         if (layerPresent) {
//             YardDetail frontYard = getYard(pl, doc, yardName, setBack.getLevel());
//             if (frontYard != null && frontYard.getPolyLine() != null) {
//                 setBack.setFrontYard(frontYard);
//                 if (pl.getDrawingPreference() != null &&
//                         org.egov.infra.utils.StringUtils.isNotBlank(pl.getDrawingPreference().getUom())
//                         && (DxfFileConstants.INCH_UOM.equalsIgnoreCase(pl.getDrawingPreference().getUom())
//                                 || DxfFileConstants.FEET_UOM.equalsIgnoreCase(pl.getDrawingPreference().getUom()))) {
//                     List<BigDecimal> yardWidthDistance = Util.getListOfDimensionByColourCode(pl, yardName,
//                             DxfFileConstants.YARD_DIMENSION_COLOR);
//                     if (!yardWidthDistance.isEmpty()) {
//                         frontYard.setMinimumDistance(Collections.min(yardWidthDistance));
//                     } else {
//                         pl.addError(yardName + "_MIN_DISTANCE", String.format(ERR_MIN_DISTANCE, yardName));
//                     }
//                 } else {
//                 	 List<BigDecimal> yardWidthDistance = Util.getListOfDimensionByColourCode(pl, yardName,
//                              DxfFileConstants.YARD_DIMENSION_COLOR);
//                 	 if (!yardWidthDistance.isEmpty()) {
//                          frontYard.setMinimumDistance(Collections.min(yardWidthDistance));
//                      } else {
//                     	 frontYard.setMinimumDistance(
//                                  minDistance.getYardMinDistance(pl, yardName, String.valueOf(setBack.getLevel()), doc));
//                      }
//                 }
//                 setYardHeight(doc, yardName, frontYard);
//             } else
//                 yardNotDefined(pl, yardName);
//         }
//     }
//     
// 	private void setFrontYardOverrideDetails(PlanDetail pl, DXFDocument doc, SetBack setBack, String yardNameOverride) {
// 		boolean layerPresent = doc.containsDXFLayer(yardNameOverride);
// 		if (layerPresent) {
// 			YardDetail frontYardOverride = getYard(pl, doc, yardNameOverride, setBack.getLevel());
// 			if (frontYardOverride != null && frontYardOverride.getPolyLine() != null) {
// 				setBack.setFrontYardOverride(frontYardOverride);
// 
// 				frontYardOverride.setMinimumDistance(minDistance.getYardMinDistanceOverride(pl, yardNameOverride,
// 						String.valueOf(setBack.getLevel()), doc));
// 
// 			} else
// 				yardNotDefined(pl, yardNameOverride);
// 		}
// 	}
// 	
// 	private void setRearYardOverrideDetails(PlanDetail pl, DXFDocument doc, SetBack setBack, String yardNameOverride) {
// 		boolean layerPresent = doc.containsDXFLayer(yardNameOverride);
// 		if (layerPresent) {
// 			YardDetail rearYardOverride = getYard(pl, doc, yardNameOverride, setBack.getLevel());
// 			if (rearYardOverride != null && rearYardOverride.getPolyLine() != null) {
// 				setBack.setRearYardOverride(rearYardOverride);
// 
// 				rearYardOverride.setMinimumDistance(minDistance.getYardMinDistanceOverride(pl, yardNameOverride,
// 						String.valueOf(setBack.getLevel()), doc));
// 
// 			} else
// 				yardNotDefined(pl, yardNameOverride);
// 		}
// 	}
// 	
// 	private void setSideYard1OverrideDetails(PlanDetail pl, DXFDocument doc, SetBack setBack, String yardNameOverride) {
// 		boolean layerPresent = doc.containsDXFLayer(yardNameOverride);
// 		if (layerPresent) {
// 			YardDetail sideYard1Override = getYard(pl, doc, yardNameOverride, setBack.getLevel());
// 			if (sideYard1Override != null && sideYard1Override.getPolyLine() != null) {
// 				setBack.setSideYard1Override(sideYard1Override);
// 
// 				sideYard1Override.setMinimumDistance(minDistance.getYardMinDistanceOverride(pl, yardNameOverride,
// 						String.valueOf(setBack.getLevel()), doc));
// 
// 			} else
// 				yardNotDefined(pl, yardNameOverride);
// 		}
// 	}
// 	
// 	private void setSideYard2OverrideDetails(PlanDetail pl, DXFDocument doc, SetBack setBack, String yardNameOverride) {
// 		boolean layerPresent = doc.containsDXFLayer(yardNameOverride);
// 		if (layerPresent) {
// 			YardDetail sideYard2Override = getYard(pl, doc, yardNameOverride, setBack.getLevel());
// 			if (sideYard2Override != null && sideYard2Override.getPolyLine() != null) {
// 				setBack.setSideYard2Override(sideYard2Override);
// 
// 				sideYard2Override.setMinimumDistance(minDistance.getYardMinDistanceOverride(pl, yardNameOverride,
// 						String.valueOf(setBack.getLevel()), doc));
// 
// 			} else
// 				yardNotDefined(pl, yardNameOverride);
// 		}
// 	}
// 
//     
// }
// ===== END ORIGINAL IMPLEMENTATION =====
