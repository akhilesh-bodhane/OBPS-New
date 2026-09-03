package org.egov.edcr.feature;

import org.egov.edcr.entity.blackbox.PlanDetail;
import org.springframework.stereotype.Service;

// STUBBED OUT: original implementation depends on org.egov.common.entity.edcr classes
// that do not exist in this branch's egov-commons (build blocker, not fixed here).
// Original source preserved below as a comment.
@Service
public class RoadWidthExtract extends FeatureExtract {

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
// import java.util.ArrayList;
// import java.util.List;
// import java.util.stream.Collectors;
// 
// import org.apache.log4j.Logger;
// import org.egov.common.entity.edcr.AncillaryRoad;
// import org.egov.common.entity.edcr.Block;
// import org.egov.common.entity.edcr.MainEntrance;
// import org.egov.common.entity.edcr.Measurement;
// import org.egov.edcr.entity.blackbox.MeasurementDetail;
// import org.egov.edcr.entity.blackbox.PlanDetail;
// import org.egov.edcr.service.LayerNames;
// import org.egov.edcr.utility.Util;
// import org.kabeja.dxf.DXFDimension;
// import org.kabeja.dxf.DXFDocument;
// import org.kabeja.dxf.DXFLWPolyline;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// 
// @Service
// public class RoadWidthExtract extends FeatureExtract {
// 	private static final Logger LOG = Logger.getLogger(RoadWidthExtract.class);
// 
// 	@Autowired
// 	private LayerNames layerNames;
// 
// 	@Override
// 	public PlanDetail validate(PlanDetail planDetail) {
// 		
// 		return planDetail;
// 	}
// 
// 	@Override
// 	public PlanDetail extract(PlanDetail planDetail) {
// 		
// 		DXFDocument doc = planDetail.getDoc();
// 		
//         for(Block blk: planDetail.getBlocks()) {        	
//         	String ancillaryLayer = String.format(
//                     layerNames.getLayerName("LAYER_NAME_ANCILLARY_ROAD_WIDTH"), blk.getNumber()); 
// 	
//         	List<DXFDimension> ancillaryDimensions = Util.getDimensionsByLayer(doc, ancillaryLayer);
//         	
//         	
//         	 if (ancillaryDimensions.isEmpty()) {
//                  continue;
//              }
//         	 
//         	 AncillaryRoad ancillaryRoad = new AncillaryRoad();
//              List<Measurement> ancillaryDimMeasurement = ancillaryDimensions.stream()
//                      .map(dim -> buildWidth(planDetail, dim, ancillaryLayer))
//                      .collect(Collectors.toList());
//              
//              ancillaryRoad.setAncillaryRoadWidth(ancillaryDimMeasurement);
//              
//              blk.setAncillaryRoad(ancillaryRoad);
//              
//              String mainEntranceLayer = String.format(
//                      layerNames.getLayerName("LAYER_NAME_MAIN_ENTRANCE"), blk.getNumber());
//              
//              List<DXFDimension> mainEntranceDimensions = Util.getDimensionsByLayer(doc, mainEntranceLayer);
//              MainEntrance mainEntrance = new MainEntrance();
//              List<Measurement> mainEntranceDimMeasurement = mainEntranceDimensions.stream()
//                      .map(dim -> buildWidth(planDetail, dim, mainEntranceLayer))
//                      .collect(Collectors.toList());
//              
//              mainEntrance.setMainEntrance(mainEntranceDimMeasurement);
//              blk.setMainEntrance(mainEntrance);
//         }
// 
// 		return planDetail;
// 	}
// 	
// 	private Measurement buildWidth(PlanDetail pl, DXFDimension dim, String layerName) {
//         List<BigDecimal> values = new ArrayList<>();
//         Util.extractDimensionValue(pl, values, dim, layerName);
//         Measurement measurement = new Measurement();
//         measurement.setColorCode(dim.getColor());
//         measurement.setWidth(values.isEmpty() ? BigDecimal.ZERO : values.get(0));
//         return measurement;
//     }
// 
// }
// ===== END ORIGINAL IMPLEMENTATION =====
