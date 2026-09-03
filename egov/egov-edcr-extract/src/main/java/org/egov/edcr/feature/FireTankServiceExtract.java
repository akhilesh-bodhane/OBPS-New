package org.egov.edcr.feature;

import org.egov.edcr.entity.blackbox.PlanDetail;
import org.springframework.stereotype.Service;

// STUBBED OUT: original implementation depends on org.egov.common.entity.edcr classes
// that do not exist in this branch's egov-commons (build blocker, not fixed here).
// Original source preserved below as a comment.
@Service("fireTankServiceExtract")
public class FireTankServiceExtract extends FeatureExtract {

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
// import java.util.List;
// 
// import org.apache.commons.lang.StringUtils;
// import org.apache.log4j.Logger;
// import org.egov.common.entity.edcr.FireTank;
// import org.egov.common.entity.edcr.Measurement;
// import org.egov.edcr.entity.blackbox.MeasurementDetail;
// import org.egov.edcr.entity.blackbox.PlanDetail;
// import org.egov.edcr.utility.Util;
// import org.kabeja.dxf.DXFLWPolyline;
// import org.springframework.stereotype.Service;
// 
// @Service("fireTankServiceExtract")
// public class FireTankServiceExtract extends FeatureExtract {
// 	
// 	private static final Logger LOGGER = Logger.getLogger(FireTankServiceExtract.class);
//     private String LAYER_NAME_FIRE_TANK = "FIRE_TANK";
//     private String MTEXT_FIRE_TANK_CAPACITY_L = "FIRE_TANK_CAPACITY_L";
// 
//     @Override
//     public PlanDetail extract(PlanDetail pl) {
//     	
//         if (LOGGER.isDebugEnabled())
//             LOGGER.debug("Starting of FireTankServiceExtract......");
// 
//         List<DXFLWPolyline> fireTankPolyLines = Util.getPolyLinesByLayer(pl.getDoc(),LAYER_NAME_FIRE_TANK);
//         
//         if (fireTankPolyLines != null && !fireTankPolyLines.isEmpty()) {
//         	
//             for (DXFLWPolyline pline : fireTankPolyLines) {
//             	
//                 Measurement measurement = new MeasurementDetail(pline, true);
//                 FireTank fireTank = new FireTank();
//                 fireTank.setArea(measurement.getArea());
//                 fireTank.setColorCode(measurement.getColorCode());
//                 fireTank.setHeight(measurement.getHeight());
//                 fireTank.setWidth(measurement.getWidth());
//                 fireTank.setLength(measurement.getLength());
//                 fireTank.setCapacity(findFireTankCapcity(pl));
//                 pl.getUtility().addFireTank(fireTank);
//             }
//         }
// 
//         return pl;
//     }
// 
// 	private String findFireTankCapcity(PlanDetail pl) {
// 
// 		String tankCapacity = Util.getMtextByLayerName(pl.getDoc(), LAYER_NAME_FIRE_TANK, MTEXT_FIRE_TANK_CAPACITY_L);
// 	    
// 		if (StringUtils.isNotBlank(tankCapacity) && tankCapacity.contains("=")) {
// 	        String[] tankCapacityArr = tankCapacity.split("="); 
// 	        if (tankCapacityArr.length > 1 && StringUtils.isNotBlank(tankCapacityArr[1])) {
// 	            tankCapacity = tankCapacityArr[1];
// 	        }
// 	    }
// 		return tankCapacity;
// 	}
// 
// 	@Override
//     public PlanDetail validate(PlanDetail pl) {
//         return pl;
//     }
// 
// }
// ===== END ORIGINAL IMPLEMENTATION =====
