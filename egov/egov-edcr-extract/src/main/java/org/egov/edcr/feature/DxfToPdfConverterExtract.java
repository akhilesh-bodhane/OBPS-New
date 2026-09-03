package org.egov.edcr.feature;

import org.egov.edcr.entity.blackbox.PlanDetail;
import org.springframework.stereotype.Service;

// STUBBED OUT: original implementation depends on org.egov.common.entity.edcr classes
// that do not exist in this branch's egov-commons (build blocker, not fixed here).
// Original source preserved below as a comment.
@Service
public class DxfToPdfConverterExtract extends FeatureExtract {

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
// import static org.apache.commons.lang3.StringUtils.isBlank;
// 
// import java.io.File;
// import java.io.FileInputStream;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.math.BigDecimal;
// import java.math.BigInteger;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.HashMap;
// import java.util.Iterator;
// import java.util.LinkedHashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;
// import java.util.regex.Pattern;
// import java.util.stream.Collectors;
// 
// import javax.persistence.EntityManager;
// import javax.persistence.PersistenceContext;
// 
// import org.apache.commons.lang3.StringUtils;
// import org.apache.log4j.Logger;
// import org.apache.pdfbox.io.MemoryUsageSetting;
// import org.apache.pdfbox.multipdf.PDFMergerUtility;
// import org.apache.pdfbox.printing.Orientation;
// import org.egov.common.entity.dcr.helper.DxfToPdfLayerConfig;
// import org.egov.common.entity.dcr.helper.PlanPdfLayerConfig;
// import org.egov.common.entity.edcr.Block;
// import org.egov.common.entity.edcr.EdcrPdfDetail;
// import org.egov.common.entity.edcr.Floor;
// import org.egov.commons.mdms.EDCRMdmsUtil;
// import org.egov.commons.mdms.config.MdmsConfiguration;
// import org.egov.commons.mdms.model.MdmsEdcrResponse;
// import org.egov.commons.mdms.validator.MDMSValidator;
// import org.egov.edcr.constants.OdishaUlbs;
// import org.egov.edcr.entity.PdfPageSize;
// import org.egov.edcr.entity.blackbox.PlanDetail;
// import org.egov.edcr.service.DcrSvgGenerator;
// import org.egov.edcr.utility.DcrConstants;
// import org.egov.edcr.utility.Util;
// import org.egov.infra.admin.master.entity.AppConfigValues;
// import org.egov.infra.admin.master.entity.City;
// import org.egov.infra.admin.master.service.AppConfigValueService;
// import org.egov.infra.admin.master.service.CityService;
// import org.egov.infra.config.core.ApplicationThreadLocals;
// import org.egov.infra.filestore.entity.FileStoreMapper;
// import org.egov.infra.filestore.service.FileStoreService;
// import org.egov.infra.microservice.models.RequestInfo;
// import org.egov.infra.utils.FileStoreUtils;
// import org.json.simple.JSONObject;
// import org.kabeja.batik.tools.SAXPDFSerializer;
// import org.kabeja.dxf.Bounds;
// import org.kabeja.dxf.DXFBlock;
// import org.kabeja.dxf.DXFConstants;
// import org.kabeja.dxf.DXFDimension;
// import org.kabeja.dxf.DXFDocument;
// import org.kabeja.dxf.DXFEntity;
// import org.kabeja.dxf.DXFHatch;
// import org.kabeja.dxf.DXFImage;
// import org.kabeja.dxf.DXFInsert;
// import org.kabeja.dxf.DXFLWPolyline;
// import org.kabeja.dxf.DXFLayer;
// import org.kabeja.dxf.DXFLine;
// import org.kabeja.dxf.DXFMText;
// import org.kabeja.dxf.DXFPolyline;
// import org.kabeja.dxf.DXFSolid;
// import org.kabeja.dxf.DXFStyle;
// import org.kabeja.dxf.DXFText;
// import org.kabeja.dxf.DXFVariable;
// import org.kabeja.dxf.DXFVertex;
// import org.kabeja.dxf.helpers.Point;
// import org.kabeja.dxf.helpers.StyledTextParagraph;
// import org.kabeja.math.MathUtils;
// import org.kabeja.xml.SAXSerializer;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// 
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.itextpdf.text.BaseColor;
// import com.itextpdf.text.Element;
// import com.itextpdf.text.Font;
// import com.itextpdf.text.PageSize;
// import com.itextpdf.text.Phrase;
// import com.itextpdf.text.Rectangle;
// import com.itextpdf.text.pdf.ColumnText;
// import com.itextpdf.text.pdf.PdfContentByte;
// import com.itextpdf.text.pdf.PdfReader;
// import com.itextpdf.text.pdf.PdfStamper;
// import com.itextpdf.text.pdf.PdfStream;
// 
// @Service
// public class DxfToPdfConverterExtract extends FeatureExtract {
// 
//     private static final Logger LOG = Logger.getLogger(DxfToPdfConverterExtract.class);
// 
//     private static final String UNDERLINE_CAPITAL = "\\L";
//     private static final String UNDERLINE_SMALL = "\\l";
//     // DXFTEXT.VALIGN_TOP = 3 meaning the text is aligned vertical to the top
//     private static final int TEXT_VALLIGNMENT_TOP = 3;
//     private static final String POWER = "Â";
// 
//     @Autowired
//     private AppConfigValueService appConfigValueService;
//     @Autowired
//     private EDCRMdmsUtil edcrMdmsUtil;
//     @Autowired
//     private MdmsConfiguration mdmsConfiguration;
//     @Autowired
//     private MDMSValidator mdmsValidator;
//     @Autowired
//     private CityService cityService;
//     @Autowired
// 	private FileStoreService fileStoreService;
//     
//     @PersistenceContext
//     private EntityManager entityManager;
//     
//     @Value("${dxf.pdf.memory.threshold:0.25}")
//     private double memoryThreshold;
//     
//     @Value("${dxf.pdf.merge.memory.mb:50}")
//     private long mergeMemoryThresholdMB;
// 
// 	@Transactional
// 	private BigInteger getNextReferenceNumber() {
// 	    return (BigInteger) entityManager.createNativeQuery("SELECT nextval('seq_dxf_to_pdf_no')")
// 	                               .getSingleResult();
// 	}
// 
//     @Override
//     public PlanDetail extract(PlanDetail planDetail) {
//     	
//     	Boolean isBasePdf= planDetail.getIsBasePdf();
//     	
//     	//FIXME:removed for backward compatibility. Will be added later after frontend integration.
// //    	if(isBasePdf==null) {
// //    		planDetail.addError("CadToPdf", "CAD to PDF must be done separately!");
// //    		throw new RuntimeException("CAD to PDF must be done separately!");
// //    	}
//     	
//     	if (isBasePdf == null) {
//             return extractOriginalLogic(planDetail);
//         }
//     	
//     	String categoryOfProject = determineCategory(planDetail);
//     	    	
//     	if(!areRequiredLayersPresent(planDetail, categoryOfProject))
//     		return planDetail;
//     	
//     	// Log available layers for debugging missing layer issues
//     	logAvailableLayersInDxf(planDetail, "Starting PDF conversion for category: " + categoryOfProject);
//     	
//         Boolean mdmsEnabled = mdmsConfiguration.getMdmsEnabled();
//         boolean mdmsDxfToPdfEnabled = false;
// 		if (mdmsEnabled != null && mdmsEnabled) {
// 			City stateCity = cityService.fetchStateCityDetails();
// 			String tenantID = ApplicationThreadLocals.getTenantID();
// 			Object mdmsData = edcrMdmsUtil.mDMSCall(new RequestInfo(),
// 					new StringBuilder().append(stateCity.getCode()).append(".").append(tenantID).toString());
// 
// 			if (mdmsData == null) {
// 				tenantID = stateCity.getCode();
// 				mdmsData = edcrMdmsUtil.mDMSCall(new RequestInfo(), tenantID);
// 			}
// 			if (mdmsData != null) {
// 				Map<String, List<Object>> edcrMdmsConfig = mdmsValidator.getAttributeValues(mdmsData,
// 						DcrConstants.MDMS_EDCR_MODULE);
// 				MdmsEdcrResponse mdmsEdcrResponse = null;
// 				ObjectMapper mapper = new ObjectMapper();
// 				try {
// 					List<Object> dxfToPdfMdmsEnabled = edcrMdmsConfig.get("DxfToPdfConfig");
// 
// 					String jsonStr = new JSONObject((LinkedHashMap<?, ?>) dxfToPdfMdmsEnabled.get(0)).toString();
// 					mdmsEdcrResponse = mapper.readValue(jsonStr, MdmsEdcrResponse.class);
// 				} catch (IOException e) {
// 					LOG.error("Error occured while reading mdms data", e);
// 				}
// 				if (mdmsEdcrResponse != null && mdmsEdcrResponse.getEnabled().equals("true")) {
// 					mdmsDxfToPdfEnabled = true;
// 
// 					int blkSize = planDetail.getBlocks().size();
// 					List<Object> dxfToPdfConfig = new ArrayList<>();
// 
// 					if (categoryOfProject.equals("Category A")) {
// 						dxfToPdfConfig = isBasePdf ? edcrMdmsConfig.get("DxfToPdfLayerConfigCatA_Base")
// 								: edcrMdmsConfig.get("DxfToPdfLayerConfigCatA_ALL");
// 					} else if (categoryOfProject.equals("Category B")) {
// 						if (blkSize == 1) {
// 							dxfToPdfConfig = isBasePdf ? edcrMdmsConfig.get("DxfToPdfLayerConfigCatB_SingleBlock_Base")
// 									: edcrMdmsConfig.get("DxfToPdfLayerConfigCatB_SingleBlock_ALL");
// 						} else {
// 							dxfToPdfConfig = isBasePdf ? edcrMdmsConfig.get("DxfToPdfLayerConfigCatB_MultiBlock_Base")
// 									: edcrMdmsConfig.get("DxfToPdfLayerConfigCatB_MultiBlock_ALL");
// 						}
// 					} else if (categoryOfProject.equals("Category C") || categoryOfProject.equals("Category D")) {
// 						dxfToPdfConfig = isBasePdf ? edcrMdmsConfig.get("DxfToPdfLayerConfigCat_CD_Base")
// 								: edcrMdmsConfig.get("DxfToPdfLayerConfigCat_CD_ALL");
// 					}
// 
// 					List<EdcrPdfDetail> edcrPdfDetails = new ArrayList<>();
// 					for (Object obj : dxfToPdfConfig) {
// 						try {
// 							String jsonString = new JSONObject((LinkedHashMap<?, ?>) obj).toString();
// 							DxfToPdfLayerConfig config = mapper.readValue(jsonString, DxfToPdfLayerConfig.class);
// 							List<EdcrPdfDetail> layerNameList = getPdfLayerNames(planDetail, config);
// 							if (layerNameList != null && !layerNameList.isEmpty()) {
// 								edcrPdfDetails.addAll(layerNameList);
// 							}
// 						} catch (IOException e) {
// 							LOG.error("Error occurred while reading mdms data", e);
// 						}
// 					}
// 
// 					if (isBasePdf) {
// 						planDetail.setEdcrPdfDetails1(edcrPdfDetails);
// 					} else {
// 						planDetail.setEdcrPdfDetails2(edcrPdfDetails);
// 					}
// 				}
// 
// 			}
// 		} else {
//             List<AppConfigValues> dxfToPdfAppConfigEnabled = appConfigValueService
//                     .getConfigValuesByModuleAndKey(DcrConstants.APPLICATION_MODULE_TYPE, DcrConstants.DXF_PDF_CONVERSION_ENABLED);
// 
//             if (!dxfToPdfAppConfigEnabled.isEmpty() && dxfToPdfAppConfigEnabled.get(0).getValue().equalsIgnoreCase("NO"))
//                 return planDetail;
//         }
// 
//         if (!mdmsDxfToPdfEnabled) {
//             List<AppConfigValues> appConfigValues = appConfigValueService
//                     .getConfigValuesByModuleAndKey(DcrConstants.APPLICATION_MODULE_TYPE, DcrConstants.EDCR_DXF_PDF);
//             for (AppConfigValues appConfigValue : appConfigValues) {
//                 if (LOG.isDebugEnabled())
//                     LOG.debug("App Config value :" + appConfigValue.getValue());
//                 List<EdcrPdfDetail> layerNameList = getPdfLayerNames(planDetail, appConfigValue.getValue());
//                 for (EdcrPdfDetail d : layerNameList) {
//                     if (LOG.isDebugEnabled())
//                         LOG.debug("\t\t\tSheetName : " + d.getLayer() + " , list of layers :\n" + d.getLayers());
//                 }
//                 // get a particular layer from the document and enable the layer
//                 if (layerNameList != null && !layerNameList.isEmpty()) {
// 
//                     if (planDetail.getEdcrPdfDetails() == null)
//                         planDetail.setEdcrPdfDetails(layerNameList);
//                     else
//                         planDetail.getEdcrPdfDetails().addAll(layerNameList);
//                 }
//             }
//         }
// 
//         validate(planDetail);
// 
//         String fileName = planDetail.getApplicationDate().toString();
//         
//         fileName = fileName.replaceAll("\\s", "");
//         fileName = fileName.replaceAll(":", "");
//         
//         List<EdcrPdfDetail> pdfDetails = isBasePdf ? planDetail.getEdcrPdfDetails1()
//                 : planDetail.getEdcrPdfDetails2();
//         
//         Boolean printSingleSheet = false;
//         EdcrPdfDetail printSingleSheetDetails = null;
// 
//         Iterator dxfBlockIterator = planDetail.getDxfDocument().getDXFBlockIterator();
//         while (dxfBlockIterator.hasNext()) {
//             DXFBlock block = (DXFBlock) dxfBlockIterator.next();
//             Iterator dxfEntitiesIterator = block.getDXFEntitiesIterator();
//             while (dxfEntitiesIterator.hasNext()) {
//                 DXFEntity e = (DXFEntity) dxfEntitiesIterator.next();
//                 e.setLineWeight(-1);
// 
//             }
//         }
//         Iterator dxfStyleIterator = planDetail.getDxfDocument().getDXFStyleIterator();
// 
//         while (dxfStyleIterator.hasNext()) {
//             DXFStyle style = (DXFStyle) dxfStyleIterator.next();
// 
//             LOG.debug(",,DXF style,,,,,    " + style.getName() + "    " + style.getFontFile() + ""
//                     + style.getWidthFactor());
//             style.setWidthFactor(-1);
//             style.setFontFile("romans");
//             style.setBigFontFile("romans");
//             style.setName("romans");
//         }
// 
//         Iterator layerIterator = planDetail.getDxfDocument().getDXFLayerIterator();
//         while (layerIterator.hasNext()) {
//             DXFLayer layer = (DXFLayer) layerIterator.next();
//             layer.setFlags(1);
//         }
//         
//         String ref = planDetail.getPlanInformation().getDxfToPdfCorrelationId();
//         String uniqueReferenceNumber="";
//         
//         if(ref==null) {
//         	BigInteger referenceNumber = getNextReferenceNumber();
//             int currentYear = LocalDate.now().getYear();
//             char categoryInitial = categoryOfProject.charAt(categoryOfProject.length() - 1);
//             uniqueReferenceNumber = String.format("%d%c%07d", currentYear, categoryInitial, referenceNumber); 
//         } else {
//         	uniqueReferenceNumber=ref;
//         }
//         
//               
//         //YYYY "CATEGORY A/B/C/D" 7 DIGITS
// 
//         for (EdcrPdfDetail edcrPdfDetail : pdfDetails) {
// 
//             if (edcrPdfDetail.getLayers() == null || edcrPdfDetail.getLayers().isEmpty()) {
//                 LOG.warn("Skipping PDF detail '" + edcrPdfDetail.getLayer() + "' - no layers defined");
//                 continue;
//             }
// 
//             if (edcrPdfDetail.getLayers().contains("All")) {
//                 printSingleSheet = true;
//                 printSingleSheetDetails = edcrPdfDetail;
//                 continue;
//             }
//            
//             // Check if any of the required layers actually exist in the DXF
//             boolean hasValidLayers = false;
//             for (String layerName : edcrPdfDetail.getLayers()) {
//                 DXFLayer dxfLayer = planDetail.getDxfDocument().getDXFLayer(layerName);
//                 if (dxfLayer != null) {
//                     hasValidLayers = true;
//                     break;
//                 }
//             }
//             
//             if (!hasValidLayers) {
//                 LOG.warn("Skipping PDF detail '" + edcrPdfDetail.getLayer() + "' - none of the required layers exist in DXF: " + edcrPdfDetail.getLayers());
//                 continue;
//             }
//            
//             enablePrintableLayers(edcrPdfDetail, planDetail.getDxfDocument());
//             sanitize(fileName, planDetail.getDxfDocument(), edcrPdfDetail, planDetail);
// 
//             FileStoreMapper fileStoreMapper = convertDxfToPdf(planDetail, fileName, edcrPdfDetail.getLayer(), edcrPdfDetail, uniqueReferenceNumber);
//             disablePrintableLayers(edcrPdfDetail, planDetail.getDxfDocument());
// 
//             if (fileStoreMapper != null) {
//             	edcrPdfDetail.setConvertedFileStoreMapper(fileStoreMapper);
//             }
// 
//         }
// 
//         // enable all layers back
//         layerIterator = planDetail.getDxfDocument().getDXFLayerIterator();
//         while (layerIterator.hasNext()) {
//             DXFLayer layer = (DXFLayer) layerIterator.next();
//             layer.setFlags(0);
//             if (printSingleSheet && !layer.getName().equalsIgnoreCase("0")) {
//                 printSingleSheetDetails.getMeasurementLayers().add(layer.getName());
// 
//             }
//         }
// 
//         if (printSingleSheet) {
// 
//             sanitize(fileName, planDetail.getDxfDocument(), printSingleSheetDetails, planDetail);
// 
//             FileStoreMapper fileStoreMapper = convertDxfToPdf(planDetail, fileName, printSingleSheetDetails.getLayer(),
//                     printSingleSheetDetails, uniqueReferenceNumber);
// 
//             if (fileStoreMapper != null) {
//                 printSingleSheetDetails.setConvertedFileStoreMapper(fileStoreMapper);
//             }
// 
//         }
// 
// 		if (!pdfDetails.isEmpty()) {
// 			planDetail.getPlanInformation().setDxfToPdfCorrelationId(uniqueReferenceNumber);
// 		} else {
// 			if (categoryOfProject.equals("Category A")) {
// 				planDetail.addErrorMsg("DxfToPdf", "DXF to PDF failed! Site Plan layer is missing from the drawing.");
// 			} else if (categoryOfProject.equals("Category B")) {
// 				planDetail.addErrorMsg("DxfToPdf",
// 						"DXF to PDF failed! Site Plan/Floor Plan layer is missing from the drawing.");
// 			} else {
// 				planDetail.addErrorMsg("DxfToPdf",
// 						"DXF to PDF failed! Site Plan / Floor Plan / Elevation Plan / Section Plan / Service Plan layer is missing from the drawing.");
// 			}
// 		}
// 
// 		if(isBasePdf)
// 			generateCombinedPdfForDetails(planDetail.getEdcrPdfDetails1(), "BASE_LAYERS", planDetail.getThirdPartyUserTenantld());
// 		else
// 			generateCombinedPdfForDetails(planDetail.getEdcrPdfDetails2(), "BASE_AND_OBPAS_LAYERS", planDetail.getThirdPartyUserTenantld());
// 			
// 
// 		return planDetail;
// 
//     }
// 
//     @Override
//     public PlanDetail validate(PlanDetail planDetail) {
// 
//     	validateEdcrPdfDetails(planDetail, planDetail.getEdcrPdfDetails1());
//         validateEdcrPdfDetails(planDetail, planDetail.getEdcrPdfDetails2());
//         return planDetail;
//     }
//     
//     private void validateEdcrPdfDetails(PlanDetail planDetail, List<EdcrPdfDetail> layerNameList) {
//         if (layerNameList != null) {
//             for (EdcrPdfDetail pdfDetail : layerNameList) {
//                 if (pdfDetail.getLayers() != null) {
//                     for (String layerName : pdfDetail.getLayers()) {
//                         DXFLayer dxfLayer = planDetail.getDxfDocument().getDXFLayer(layerName);
//                         checkNegetiveWidth(dxfLayer, pdfDetail);
//                     }
//                 }
//             }
//         }
//     }
// 
//     private void sanitize2(String fileName, DXFDocument dxfDocument, EdcrPdfDetail edcrPdfDetail, PlanDetail pl) {
//         // StringBuffer standardViolations = new StringBuffer();
// 
//         boolean addMeasurement = false;
//         if (edcrPdfDetail.getLayers() != null)
//             Outer: for (String layer : edcrPdfDetail.getLayers()) {
// 
//                 if (edcrPdfDetail.getMeasurementLayers().contains(layer))
//                     addMeasurement = true;
// 
//                 DXFLayer dxfLayer = dxfDocument.getDXFLayer(layer);
//                 LOG.debug(edcrPdfDetail.getLayer());
// 
//                 sanitizeTexts(edcrPdfDetail, dxfDocument, dxfLayer);
//                 sanitizeMtext(edcrPdfDetail, dxfDocument, dxfLayer);
//                 sanitizeDimension(edcrPdfDetail, dxfDocument, dxfLayer);
// 
//                 Iterator dxfEntityTypeIterator = dxfLayer.getDXFEntityTypeIterator();
//                 inner: while (dxfEntityTypeIterator.hasNext()) {
// 
//                     String type;
//                     try {
//                         type = (String) dxfEntityTypeIterator.next();
//                         if (LOG.isDebugEnabled())
//                             LOG.debug("Type is " + type);
//                     } catch (Exception e1) {
// 
//                         e1.printStackTrace();
//                         break inner;
//                     }
// 
//                     List<DXFEntity> entity = dxfLayer.getDXFEntities(type);
//                     if (entity != null && !entity.isEmpty()) {
//                         for (DXFEntity e : entity) {
//                             if (LOG.isDebugEnabled())
//                                 LOG.debug(e.getLineWeight());
//                             e.setLineWeight(-1);
//                             switch (type) {
//                             case DXFConstants.ENTITY_TYPE_LWPOLYLINE: {
//                                 if (addMeasurement) {
//                                     addPolygonMeasurement(dxfLayer, e, edcrPdfDetail, pl);
//                                     // e.setThickness(8);
// 
//                                     e.setLineWeight(2);
//                                 }
//                                 break;
//                             }
//                             case DXFConstants.ENTITY_TYPE_POLYLINE: {
//                                 if (addMeasurement) {
//                                     addPolygonMeasurement(dxfLayer, e, edcrPdfDetail, pl);
//                                     // e.setThickness(8);
//                                     e.setLineWeight(2);
//                                 }
//                                 break;
//                             }
//                             case DXFConstants.ENTITY_TYPE_MTEXT: {
//                                 DXFMText t = (DXFMText) e;
//                                 if (LOG.isDebugEnabled())
//                                     LOG.debug("Thickness-------Mtext-----------of  " + t.getText() + "  is "
//                                             + t.getThickness());
//                                 t.setText(t.getText().replaceAll("\n", " "));
//                                 String textStyle = t.getTextStyle();
//                                 t.setTextStyle("timesnewroman");
//                                 LOG.debug("Style--------" + textStyle);
//                                 break;
// 
//                             }
// 
//                             case DXFConstants.ENTITY_TYPE_TEXT: {
//                                 DXFText t = (DXFText) e;
//                                 if (LOG.isDebugEnabled())
//                                     LOG.debug("Thickness-------Mtext-----------of  " + t.getText() + "  is "
//                                             + t.getThickness());
//                                 t.setText(t.getText().replaceAll("\n", " "));
//                                 String textStyle = t.getTextStyle();
//                                 t.setTextStyle("timesnewroman");
//                                 LOG.debug("Style--------" + textStyle);
//                                 break;
// 
//                             }
// 
//                             case DXFConstants.ENTITY_TYPE_HATCH: {
//                                 // e.setLineWeight(0);
//                                 // e.setVisibile(visibile);
//                                 break;
//                             }
//                             }
// 
//                         }
//                     }
// 
//                 }
// 
//                 DXFVariable psltScale = dxfDocument.getDXFHeader().getVariable("$PSLTSCALE");
// 
//                 if (psltScale != null) {
//                     String psltScaleValue = psltScale.getValue("70");
// 
//                     if (!isBlank(psltScaleValue)) {
//                         dxfDocument.getDXFHeader().getVariable("$PSLTSCALE").setValue("70", String.valueOf(.1));
//                     }
// 
//                 }
//             }
// 
//     }
// 
//     private void sanitize(String fileName, DXFDocument dxfDocument, EdcrPdfDetail edcrPdfDetail, PlanDetail pl) {
//         // StringBuffer standardViolations = new StringBuffer();
//     	
//         boolean addMeasurement = false;
//         if (edcrPdfDetail.getLayers() != null)
//             Outer: for (String layer : edcrPdfDetail.getLayers()) {
// 
//                 if (edcrPdfDetail.getMeasurementLayers().contains(layer)
//                         || edcrPdfDetail.getDimensionLayers().contains(layer))
//                     addMeasurement = true;
// 
//                 DXFLayer dxfLayer = dxfDocument.getDXFLayer(layer);
//                 LOG.debug(edcrPdfDetail.getLayer());
// 
//                 sanitizeTexts(edcrPdfDetail, dxfDocument, dxfLayer);
//                 sanitizeMtext(edcrPdfDetail, dxfDocument, dxfLayer);
//                 sanitizeDimension(edcrPdfDetail, dxfDocument, dxfLayer);
// 
//                 List<DXFEntity> entity = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_LWPOLYLINE);
//                 if (entity != null && !entity.isEmpty()) {
//                     for (DXFEntity e : entity) {
//                         if (LOG.isDebugEnabled())
//                             LOG.debug(e.getType() + " Line Weight" + e.getLineWeight());
//                         e.setLineWeight(-1);
//                         if (addMeasurement) {
//                         	if(!edcrPdfDetail.getMeasurementLayers().contains(layer)) {
//                         		addPolygonMeasurement(dxfLayer, e, edcrPdfDetail, pl);
//                         	}                          
//                             if (edcrPdfDetail.getColorOverrides().containsKey(dxfLayer.getName()))
//                             	e.setColor(edcrPdfDetail.getColorOverrides().get(dxfLayer.getName()));
//                             
//                             if (edcrPdfDetail.getThicknessOverrides().containsKey(dxfLayer.getName()))
//                             	e.setLineWeight(edcrPdfDetail.getThicknessOverrides().get(dxfLayer.getName()));
//                         }
// 
//                     }
//                     entity = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_POLYLINE);
//                     if (entity != null && !entity.isEmpty())
//                         for (DXFEntity e : entity) {
//                             if (LOG.isDebugEnabled())
//                                 LOG.debug(e.getType() + " Line Weight" + e.getLineWeight());
//                             if (addMeasurement) {
//                             	if(!edcrPdfDetail.getMeasurementLayers().contains(layer)) {
//                             		addPolygonMeasurement(dxfLayer, e, edcrPdfDetail, pl);
//                             	}
//                                 if (edcrPdfDetail.getColorOverrides().containsKey(dxfLayer.getName()))
//                                     //e.setLineWeight(edcrPdfDetail.getColorOverrides().get(dxfLayer.getName()));
//                                 	e.setColor(edcrPdfDetail.getColorOverrides().get(dxfLayer.getName()));
//                                 
//                                 if (edcrPdfDetail.getThicknessOverrides().containsKey(dxfLayer.getName()))
//                                 	e.setLineWeight(edcrPdfDetail.getThicknessOverrides().get(dxfLayer.getName()));
//                             }
// 
//                         }
// 
//                     entity = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_MTEXT);
//                     if (entity != null && !entity.isEmpty())
//                         for (DXFEntity e : entity) {
// 
//                             DXFMText t = (DXFMText) e;
//                             if (LOG.isDebugEnabled())
//                                 LOG.debug("Thickness-------Mtext-----------of  " + t.getText() + "  is "
//                                         + t.getThickness());
//                             t.setText(t.getText().replaceAll("\n", " "));
//                             String textStyle = t.getTextStyle();
//                             t.setTextStyle("timesnewroman");
//                             if (edcrPdfDetail.getColorOverrides().containsKey(dxfLayer.getName()))
//                             	t.setColor(edcrPdfDetail.getColorOverrides().get(dxfLayer.getName()));
//                             LOG.debug("Style--------" + textStyle);
// 
//                         }
//                     entity = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_TEXT);
//                     if (entity != null && !entity.isEmpty())
//                         for (DXFEntity e : entity) {
// 
//                             DXFText t = (DXFText) e;
//                             if (LOG.isDebugEnabled())
//                                 LOG.debug("Thickness-------Mtext-----------of  " + t.getText() + "  is "
//                                         + t.getThickness());
//                             t.setText(t.getText().replaceAll("\n", " "));
//                             String textStyle = t.getTextStyle();
//                             if (edcrPdfDetail.getColorOverrides().containsKey(dxfLayer.getName()))
//                             	t.setColor(edcrPdfDetail.getColorOverrides().get(dxfLayer.getName()));
//                             t.setTextStyle("timesnewroman");
//                             LOG.debug("Style--------" + textStyle);
// 
//                         }
//                     entity = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_HATCH);
//                     int i = 0;
//                     if (entity != null && !entity.isEmpty())
//                         for (DXFEntity e : entity) {
//                             if (LOG.isDebugEnabled())
//                                 LOG.debug(e.getType() + " Line Weight" + e.getLineWeight());
//                             DXFHatch hatch = (DXFHatch) e;
//                             if (LOG.isDebugEnabled())
//                                 LOG.debug("Hatch Style" + hatch.getHatchStyle() + " " + ++i);
//                             if (LOG.isDebugEnabled())
//                                 LOG.debug("Hatch getDefinationLinesCount " + hatch.getDefinationLinesCount()
//                                         + "in layer " + hatch.getLayerName() + " getLineType  " + hatch.getLineType()
//                                         + " getLinetypeScaleFactor " + hatch.getLinetypeScaleFactor());
// 
//                         }
//                     entity = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_DIMENSION);
//                     if (entity != null && !entity.isEmpty())
//                         for (DXFEntity e : entity) {
//                             if (LOG.isDebugEnabled())
//                                 LOG.debug(e.getType() + " Line Weight" + e.getLineWeight());
//                             e.setLineWeight(-1);
//                             // e.setVisibile(visibile);
// 
//                         }
// 
//                     entity = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_INSERT);
//                     if (entity != null && !entity.isEmpty())
//                         for (DXFEntity e : entity) {
//                             if (LOG.isDebugEnabled())
//                                 LOG.debug(e.getType() + " Line Weight" + e.getLineWeight());
//                             e.setLineWeight(-1);
//                             // e.setVisibile(visibile);
// 
//                         }
// 
//                     entity = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_LINE);
//                     if (entity != null && !entity.isEmpty())
//                         for (DXFEntity e : entity) {
//                             if (LOG.isDebugEnabled())
//                                 LOG.debug(e.getType() + " Line Weight" + e.getLineWeight());
//                             e.setLineWeight(-1);
//                             // e.setVisibile(visibile);
// 
//                         }
// 
//                     entity = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_ARC);
//                     if (entity != null && !entity.isEmpty())
//                         for (DXFEntity e : entity) {
//                             if (LOG.isDebugEnabled())
//                                 LOG.debug(e.getType() + " Line Weight" + e.getLineWeight());
//                             e.setLineWeight(-1);
//                             // e.setVisibile(visibile);
// 
//                         }
//                     entity = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_CIRCLE);
//                     if (entity != null && !entity.isEmpty())
//                         for (DXFEntity e : entity) {
//                             if (LOG.isDebugEnabled())
//                                 LOG.debug(e.getType() + " Line Weight" + e.getLineWeight());
//                             e.setLineWeight(-1);
//                             // e.setVisibile(visibile);
// 
//                         }
// 
//                     entity = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_LEADER);
//                     if (entity != null && !entity.isEmpty())
//                         for (DXFEntity e : entity) {
//                             if (LOG.isDebugEnabled())
//                                 LOG.debug(e.getType() + " Line Weight" + e.getLineWeight());
//                             e.setLineWeight(-1);
//                             // e.setVisibile(visibile);
// 
//                         }
//                     
// 					entity = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_IMAGE);
// 					List<DXFLWPolyline> signs = Util.getPolyLinesByLayer(pl.getDoc(), dxfLayer.getName()+"_SIGN");
// 					List<DXFImage> images = null;
// 					if (entity != null && !entity.isEmpty()) {
// 						
// 						if(signs.isEmpty()) {
// 							pl.addErrorMsg("missing_layer" + entity.toString(), dxfLayer.getName()+"_SIGN layer is missing from the drawing.");
// 							continue;
// 						}
// 						
// 						if(signs.size() < entity.size()) {
// 							pl.addErrorMsg("missing_layer" + entity.toString(), dxfLayer.getName()+"_SIGN layer is missing from the drawing. Number of polygons must match the number of images.");
// 						}
// 						
// 						images = entity.stream().filter(e -> e instanceof DXFImage).map(e -> (DXFImage) e)
// 								.collect(Collectors.toList());
// 
// 						for (int i1 = 0; i1 < signs.size() && i1 < images.size(); i1++) {
// 							DXFLWPolyline sign = signs.get(i1);
// 							DXFImage img = images.get(i1);
// 							Bounds b = sign.getBounds();
// 							double width = b.getMaximumX() - b.getMinimumX();
// 							double height = b.getMaximumY() - b.getMinimumY();
// 
// 							img.setImageSizeAlongU(width);
// 							img.setImageSizeAlongV(height);
// 			                
// 						}
// 
// 					}
// 				}
// 
//             }
// 
//         DXFVariable psltScale = dxfDocument.getDXFHeader().getVariable("$PSLTSCALE");
// 
//         if (psltScale != null) {
//             String psltScaleValue = psltScale.getValue("70");
// 
//             if (!isBlank(psltScaleValue)) {
//                 dxfDocument.getDXFHeader().getVariable("$PSLTSCALE").setValue("70", String.valueOf(.1));
//             }
// 
//         }
//     }
// 
//     private void enablePrintableLayers(EdcrPdfDetail edcrPdfDetail, DXFDocument dxfDocument) {
// 
//         if (edcrPdfDetail.getLayers() != null)
//             for (String layer : edcrPdfDetail.getLayers()) {
//                 // Enable layer for Print
//                 DXFLayer dxfLayer = dxfDocument.getDXFLayer(layer);
//                 if (LOG.isDebugEnabled())
//                     LOG.debug(layer + " Enabled");
//                 dxfLayer.setFlags(0);
//             }
// 
//     }
// 
//     private void disablePrintableLayers(EdcrPdfDetail edcrPdfDetail, DXFDocument dxfDocument) {
//         if (edcrPdfDetail.getLayers() != null)
//             for (String layer : edcrPdfDetail.getLayers()) {
//                 // Enable layer for Print
//                 DXFLayer dxfLayer = dxfDocument.getDXFLayer(layer);
//                 if (LOG.isDebugEnabled())
//                     LOG.debug(layer + " Disabled");
//                 dxfLayer.setFlags(1);
//             }
// 
//     }
// 
//     private void addPolygonMeasurement(DXFLayer dxfLayer, DXFEntity e, EdcrPdfDetail detail, PlanDetail pl) {
//         DXFPolyline pline = (DXFPolyline) e;
//         Iterator vertexIterator = pline.getVertexIterator();
//         DXFVertex point1 = null;
//         DXFVertex first = null;
//         DXFVertex point2 = null;
//         String content = "";
//         double x = 0, y = 0;
//         double centroidX = 0, centroidY = 0;
//         StringBuilder plineDimensionText = new StringBuilder(50);
// 
//         while (vertexIterator.hasNext()) {
//             if (point1 == null) {
//                 point1 = (DXFVertex) vertexIterator.next();
//                 first = point1;
//                 x += point1.getX();
//                 y += point1.getY();
//             }
//             point2 = (DXFVertex) vertexIterator.next();
//             x += point2.getX();
//             y += point2.getY();
//             Point p = Util.getMidPoint(point1, point2);
//             point1.getPoint();
//             LOG.debug("point1 x " + point1.getX() + "   y " + point1.getY());
//             LOG.debug("point2 x " + point2.getX() + "   y " + point2.getY());
// 
//             BigDecimal length = BigDecimal.valueOf(MathUtils.distance(point1.getPoint(), point2.getPoint()))
//                     .setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);
// 
//             if (length.intValue() == 0)
//                 continue;
// 
//             if (detail.getMeasurementLayers().contains(dxfLayer.getName())) {
//                 DXFMText text1 = new DXFMText();
//                 
// 				/*
// 				 * if (detail.getPrintNameLayers().contains(dxfLayer.getName())) content = "" +
// 				 * dxfLayer.getName() + " " + length; else
// 				 */
//                 content = "" + length;
//                 LOG.debug("length...." + length);
//                 text1.setHeight(0.25d);
//                 text1.setText("" + content);
//                 text1.setAlign(1);
// 
//                 text1.setX(p.getX());
//                 if (detail.getColorOverrides().get(dxfLayer.getName().toString()) != null)
//                     text1.setColor(detail.getColorOverrides().get(dxfLayer.getName()));
//                 text1.setThickness(2);
//                 text1.setY(p.getY());
// 
//                 dxfLayer.addDXFEntity(text1);
//             } else if (detail.getDimensionLayers().contains(dxfLayer.getName())) {
// 
//                 plineDimensionText.append(length);
// 
//                 if (vertexIterator.hasNext())
//                     plineDimensionText.append(" X ");
// 
//             }
// 
//             point1 = point2;
// 
//         }
//         String content1;
//         if (pline.isClosed()) {
//             BigDecimal length = BigDecimal.valueOf(MathUtils.distance(first.getPoint(), point2.getPoint()))
//                     .setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);
//             Point p = Util.getMidPoint(first, point2);
//             // plineDimensionText.append(length);
// 
//             // x+=point2.getX();
//             // y+=point2.getY();
// 
//             if (detail.getMeasurementLayers().contains(dxfLayer.getName())) {
//                 DXFMText text1 = new DXFMText();
//                 
// 				/*
// 				 * if (detail.getPrintNameLayers().contains(dxfLayer.getName())) content1 = "" +
// 				 * dxfLayer.getName() + " " +length; else
// 				 */
//                 content1 = "" + length;
//                 LOG.debug("length...." + length);
//                 text1.setHeight(0.25d);
//                 text1.setText("" + content1);
//                 text1.setAlign(1);
// 
//                 text1.setX(p.getX());
//                 if (detail.getColorOverrides().get(dxfLayer.getName().toString()) != null)
//                     text1.setColor(detail.getColorOverrides().get(dxfLayer.getName()));
// 
//                 text1.setThickness(2);
//                 text1.setY(p.getY());
// 
//                 dxfLayer.addDXFEntity(text1);
//             } else if (detail.getDimensionLayers().contains(dxfLayer.getName())) {
//                 plineDimensionText.append(" X ");
// 
//                 plineDimensionText.append(length);
// 
//             }
//         }
//         centroidX = x / pline.getVertexCount();
//         centroidY = y / pline.getVertexCount();
//         DXFMText plineDimension = new DXFMText();
//         plineDimension.setHeight(0.25d);
//         if (detail.getMeasurementLayers().contains(dxfLayer.getName())) {
//             BigDecimal area = Util.getPolyLineArea(pline).setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
//                     DcrConstants.ROUNDMODE_MEASUREMENTS);
//             plineDimension.setText(Util.getPolylinePrintableText(pline, dxfLayer, detail, pl) + " " + area + "m2");
//         } else if (detail.getDimensionLayers().contains(dxfLayer.getName())) {
// 
//             plineDimension.setText(Util.getPolylinePrintableText(pline, dxfLayer, detail, pl) + "\n"
//                     + Util.getTexForDimension(plineDimensionText.toString()));
//         }
// 
//         if (detail.getColorOverrides().get(dxfLayer.getName().toString()) != null) {
//             e.setColor(detail.getColorOverrides().get(dxfLayer.getName()));
//         }
// 
//         if (detail.getColorOverrides().get(dxfLayer.getName().toString()) != null) {
//             plineDimension.setColor(detail.getColorOverrides().get(dxfLayer.getName()));
//         }
//         plineDimension.setAlign(1);
//         plineDimension.setHeight(0.25d);
//         plineDimension.setX(centroidX);
//         plineDimension.setY(centroidY);
//         plineDimension.setThickness(2);
//         dxfLayer.addDXFEntity(plineDimension);
//         if (LOG.isDebugEnabled())
//             LOG.debug("Added text " + plineDimension.getText() + "at x=" + centroidX + " y=" + centroidY);
// 
//         if (LOG.isDebugEnabled())
//             LOG.debug("Printing layer Name");
//         if (detail.getPrintNameLayers().contains(dxfLayer.getName())) {
//             DXFMText plineLayer = new DXFMText();
//             plineLayer.setHeight(0.25d);
//             plineLayer.setText(dxfLayer.getName());
//             plineLayer.setAlign(1);
//             plineLayer.setHeight(0.25d);
//             plineLayer.setX(centroidX);
//             plineLayer.setY(centroidY - 0.5d);
//             plineLayer.setThickness(2);
//             if (detail.getColorOverrides().get(dxfLayer.getName().toString()) != null) {
//                 plineLayer.setColor(detail.getColorOverrides().get(dxfLayer.getName()));
//             }
//             dxfLayer.addDXFEntity(plineLayer);
//         }
//     }
// 
//     private void printNext(DXFLayer dxfLayer, EdcrPdfDetail detail, DXFPolyline pline, DXFVertex first,
//             DXFVertex point2) {
//         String content;
//         if (pline.isClosed()) {
//             BigDecimal length = BigDecimal.valueOf(MathUtils.distance(first.getPoint(), point2.getPoint()))
//                     .setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);
//             Point p = Util.getMidPoint(first, point2);
//             // plineDimensionText.append(length);
// 
//             // x+=point2.getX();
//             // y+=point2.getY();
// 
//             if (detail.getMeasurementLayers().contains(dxfLayer.getName())) {
//                 DXFMText text1 = new DXFMText();
// 
//                 if (detail.getPrintNameLayers().contains(dxfLayer.getName()))
//                     content = "" + dxfLayer.getName() + " " + length;
//                 else
//                     content = "" + length;
//                 LOG.debug("length...." + length);
//                 text1.setHeight(0.25d);
//                 text1.setText("" + content);
//                 text1.setAlign(1);
// 
//                 text1.setX(p.getX());
//                 if (detail.getColorOverrides().get(dxfLayer.getName().toString()) != null)
//                     text1.setColor(detail.getColorOverrides().get(dxfLayer.getName()));
// 
//                 text1.setThickness(2);
//                 text1.setY(p.getY());
// 
//                 dxfLayer.addDXFEntity(text1);
//             }
//         }
//     }
//     
//     private static File createTempFile(String prefix, String suffix) throws IOException {
//         return File.createTempFile(prefix, suffix, new File(FileStoreUtils.TEMP_DIRECTORY));
//     }
//     
// 	/**
// 	 * Logs all available layers in the DXF document for debugging
// 	 */
// 	private void logAvailableLayersInDxf(PlanDetail planDetail, String context) {
// 		if (!LOG.isDebugEnabled()) return;
// 		
// 		StringBuilder layerList = new StringBuilder();
// 		Iterator layerIterator = planDetail.getDxfDocument().getDXFLayerIterator();
// 		int layerCount = 0;
// 		
// 		while (layerIterator.hasNext()) {
// 			DXFLayer layer = (DXFLayer) layerIterator.next();
// 			if (layerList.length() > 0) layerList.append(", ");
// 			layerList.append(layer.getName());
// 			layerCount++;
// 		}
// 		
// 		LOG.debug(context + " - DXF contains " + layerCount + " layers: " + layerList.toString());
// 	}
// 
// 	/**
// 	 * Checks memory availability before processing
// 	 */
// 	private boolean checkMemoryAvailability() {
// 		Runtime runtime = Runtime.getRuntime();
// 		long maxMemory = runtime.maxMemory();
// 		long totalMemory = runtime.totalMemory();
// 		long freeMemory = runtime.freeMemory();
// 		long usedMemory = totalMemory - freeMemory;
// 		long availableMemory = maxMemory - usedMemory;
// 		
// 		// Validate threshold is between 0 and 1
// 		double threshold = memoryThreshold;
// 		if (threshold <= 0 || threshold > 1.0) {
// 			LOG.warn("Invalid memory threshold: " + threshold + ", using default 0.25");
// 			threshold = 0.25;
// 		}
// 		
// 		long requiredMemory = (long) (maxMemory * threshold);
// 		
// 		LOG.info("Memory check - Available: " + (availableMemory / 1024 / 1024) + "MB, Required: " + (requiredMemory / 1024 / 1024) + "MB, Threshold: " + (threshold * 100) + "%");
// 		
// 		return availableMemory > requiredMemory;
// 	}
// 	
// 	/**
// 	 * Forces garbage collection and checks if enough memory is freed
// 	 */
// 	private void forceGarbageCollection() {
// 		System.gc();
// 		System.runFinalization();
// 		try {
// 			Thread.sleep(100); 
// 		} catch (InterruptedException e) {
// 			Thread.currentThread().interrupt();
// 		}
// 	}
// 
// 	private FileStoreMapper convertDxfToPdf(PlanDetail planDetail, String fileName, String layerName,
// 			EdcrPdfDetail edcrPdfDetail, String uniqueReferenceNumber) {
// 		File tempFile = null;
// 		FileOutputStream fos = null;
// 		
// 		if (!checkMemoryAvailability()) {
// 			LOG.warn("Insufficient memory available for PDF conversion. Attempting garbage collection.");
// 			forceGarbageCollection();
// 			
// 			if (!checkMemoryAvailability()) {
// 				String errorMsg = "Insufficient memory available for PDF conversion of " + fileName + " - " + layerName;
// 				LOG.error(errorMsg);
// 				planDetail.addErrorMsg("CadToPdfMemoryError", 
// 					"CAD to PDF conversion failed. Kindly check if any unnecessary layers remain in the sheets and contact SUJOG administrator.");
// 				edcrPdfDetail.setFailureReasons("Insufficient memory for conversion");
// 				return null; // Return gracefully instead of throwing exception
// 			}
// 		}
// 		
// 		try {
// 			tempFile = createTempFile("dxf_" + fileName + "_", ".pdf");
// 			fos = new FileOutputStream(tempFile);
// 
// 			LOG.info("---------converting " + fileName + " - " + layerName + " to pdf----------");
// 
// 			Runtime runtime = Runtime.getRuntime();
// 			long initialUsedMemory = runtime.totalMemory() - runtime.freeMemory();
// 			
// 			DcrSvgGenerator generator = new DcrSvgGenerator();
// 			SAXSerializer out = new SAXPDFSerializer();
// 			out.setOutput(fos);
// 
// 			HashMap<String, Object> map = new HashMap<>();
// 
// 			Rectangle rectangle;
// 
// 			if (edcrPdfDetail.getPageSize().getSize().equals("A0")) {
// 				rectangle = PageSize.getRectangle("3177 4500");
// 			} else {
// 				rectangle = PageSize.getRectangle("2250 3177");
// 			}
// 
// 			if (edcrPdfDetail.getPageSize().getOrientation().ordinal() == Orientation.PORTRAIT.ordinal()) {
// 				map.put("width", String.valueOf(rectangle.getWidth()));
// 				map.put("height", String.valueOf(rectangle.getHeight()));
// 			} else {
// 				map.put("width", String.valueOf(rectangle.getHeight()));
// 				map.put("height", String.valueOf(rectangle.getWidth()));
// 			}
// 
// 			map.put("scale", "1.0");
// 			map.put("margin", String.valueOf(0.5));
// 
// 			if (edcrPdfDetail.getPageSize().getRemoveHatch()) {
// 				map.put("stroke.width", new Double(0));
// 			}
// 
// 			if (!checkMemoryAvailability()) {
// 				throw new RuntimeException("Could not perform DXF to PDF. Please check all sheets and try again.");
// 			}
// 
// 			// Execute PDF generation with timeout
// 			final DcrSvgGenerator finalGenerator = generator;
// 			final SAXSerializer finalOut = out;
// 			final HashMap<String, Object> finalMap = map;
// 			final DXFDocument finalDoc = planDetail.getDxfDocument();
// 			
// 			java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
// 			java.util.concurrent.Future<?> future = executor.submit(() -> {
// 				try {
// 					finalGenerator.generate(finalDoc, finalOut, finalMap);
// 				} catch (Exception e) {
// 					throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
// 				}
// 			});
// 			
// 			try {
// 				future.get(2, java.util.concurrent.TimeUnit.MINUTES);
// 			} catch (java.util.concurrent.TimeoutException e) {
// 				future.cancel(true);
// 				executor.shutdownNow();
// 				String errorMsg = "CAD to PDF conversion failed for sheet: " + layerName;
// 				LOG.error(errorMsg);
// 				planDetail.addErrorMsg("CadToPdfTimeout", 
// 					"CAD to PDF conversion failed for sheet '" + layerName,
// 					"This may be due to the sheet containing unnecessary layers",
// 					"Please check the drawing or contact SUJOG administrator.");
// 				edcrPdfDetail.setFailureReasons("Timeout after 2 minutes");
// 				cleanupResources(tempFile, fos);
// 				return null;
// 			} catch (java.util.concurrent.ExecutionException e) {
// 				executor.shutdownNow();
// 				Throwable cause = e.getCause();
// 				LOG.error("PDF generation failed for " + layerName + ": " + cause.getMessage(), cause);
// 				throw new RuntimeException("CAD to PDF conversion failed for sheet: " + layerName);
// 			} catch (InterruptedException e) {
// 				future.cancel(true);
// 				executor.shutdownNow();
// 				Thread.currentThread().interrupt();
// 				String errorMsg = "CAD to PDF conversion failed for sheet: " + layerName;
// 				LOG.error(errorMsg);
// 				planDetail.addErrorMsg("CadToPdfInterrupted", 
// 					"CAD to PDF conversion failed for sheet '" + layerName + "'");
// 				edcrPdfDetail.setFailureReasons("Process interrupted");
// 				cleanupResources(tempFile, fos);
// 				return null;
// 			} finally {
// 				executor.shutdown();
// 			}
// 			
// 			long finalUsedMemory = runtime.totalMemory() - runtime.freeMemory();
// 			long memoryUsedForConversion = finalUsedMemory - initialUsedMemory;
// 			LOG.info("Memory change during PDF conversion: " + (memoryUsedForConversion / 1024 / 1024) + "MB" + 
// 					(memoryUsedForConversion < 0 ? " (memory recovered by GC)" : " (memory consumed)"));
// 			
// 			fos.flush();
// 			fos.close();
// 			fos = null; 
// 			
// 			File modifiedTempFile = addTextToPdf(tempFile, "Drawing Reference ID: " + uniqueReferenceNumber);
// 
// 			edcrPdfDetail.setTempFile(modifiedTempFile);
// 
// 			if (tempFile != null && tempFile.exists()) {
// 				tempFile.delete();
// 			}
// 			
// 			generator = null;
// 			out = null;
// 			map.clear();
// 			map = null;
// 			
// 			return null;
// 		} catch (OutOfMemoryError ex) {
// 			Runtime runtime = Runtime.getRuntime();
// 			long maxMemory = runtime.maxMemory();
// 			long totalMemory = runtime.totalMemory();
// 			long freeMemory = runtime.freeMemory();
// 			
// 			LOG.error("OutOfMemoryError during PDF conversion for " + fileName + " - " + layerName);
// 			LOG.error("Memory stats - Max: " + (maxMemory / 1024 / 1024) + "MB, Total: " + (totalMemory / 1024 / 1024) + "MB, Free: " + (freeMemory / 1024 / 1024) + "MB");
// 			
// 			planDetail.addErrorMsg("CadToPdfMemoryError",
// 				    "CAD to PDF conversion failed due to insufficient memory.",
// 				    "This may be due to one or more of the following reasons:",
// 				    "1. The DXF file is too large or complex for processing.",
// 				    "2. Image files in .jpg, .jpeg, or .png format may be too large.",
// 				    "3. The DXF file may contain too many unnecessary layers or entities.",
// 				    "4. Server memory is currently insufficient.",
// 				    "",
// 				    "Please try with a smaller/simpler DXF file or contact the administrator."
// 				);
// 
// 			edcrPdfDetail.setFailureReasons("OutOfMemoryError: " + ex.getMessage());
// 			
// 			cleanupResources(tempFile, fos);
// 			
// 			forceGarbageCollection();
// 			
// 			return null;
// 		} catch (IOException ex) {
// 			LOG.error("IOException during PDF conversion for " + fileName + " - " + layerName + ": " + ex.getMessage());
// 			planDetail.addErrorMsg("CadToPdfIOError",
// 				    "CAD to PDF conversion failed due to file processing error.",
// 				    "This may be due to one or more of the following reasons:",
// 				    "1. Image files in .jpg, .jpeg, or .png format may be missing from the submitted ZIP file.",
// 				    "2. File permission issues or disk space problems.",
// 				    "3. Corrupt or invalid DXF file format.",
// 				    "",
// 				    "Please check your file and try again or contact administrator for assistance."
// 				);
// 
// 			edcrPdfDetail.setFailureReasons("IOException: " + ex.getMessage());
// 			cleanupResources(tempFile, fos);
// 			return null; 
// 		} catch (Exception ep) {
// 			LOG.error("Unexpected error during PDF conversion for " + fileName + " - " + layerName + ": " + ep.getMessage(), ep);
// 			planDetail.addErrorMsg("CadToPdfError",
// 				    "CAD to PDF conversion failed due to an unexpected error.",
// 				    "This may be due to one or more of the following reasons:",
// 				    "1. Invalid or corrupted DXF file format.",
// 				    "2. Unsupported DXF entities or structures.",
// 				    "3. System resource limitations.",
// 				    "",
// 				    "Please verify your DXF file format and try again or contact administrator."
// 				);
// 
// 			edcrPdfDetail.setFailureReasons("Unexpected error: " + ep.getMessage());
// 			cleanupResources(tempFile, fos);
// 			return null; 	
// 		} finally {
// 			cleanupResources(tempFile, fos);
// 		}
// 	}
// 	
// 	/**
// 	 * Cleanup resources safely
// 	 */
// 	private void cleanupResources(File tempFile, FileOutputStream fos) {
// 		try {
// 			if (fos != null) {
// 				fos.close();
// 			}
// 		} catch (IOException e) {
// 			LOG.warn("Failed to close FileOutputStream: " + e.getMessage());
// 		}
// 		
// 		if (tempFile != null && tempFile.exists()) {
// 			try {
// 				if (!tempFile.delete()) {
// 					LOG.warn("Failed to delete temp file: " + tempFile.getAbsolutePath());
// 				}
// 			} catch (SecurityException e) {
// 				LOG.warn("Security exception while deleting temp file: " + e.getMessage());
// 			}
// 		}
// 	}
// 
//     private void generateCombinedPdfForDetails(List<EdcrPdfDetail> edcrPdfDetails, String layerName, String tenantId) {
//         if (edcrPdfDetails == null || edcrPdfDetails.isEmpty()) {
//             LOG.info("No PDF details to merge for layer: " + layerName);
//             return;
//         }
//         
//         if (!checkMemoryAvailability()) {
//             LOG.warn("Insufficient memory for PDF merging of " + layerName + ". Skipping merge operation.");
//             return;
//         }
//         
//         PDFMergerUtility merger = new PDFMergerUtility();
//         File mergedTempFile = null;
//         List<File> tempFilesToCleanup = new ArrayList<>();
// 
//         try {
//             mergedTempFile = File.createTempFile("merged_" + layerName + "_", ".pdf");
//             merger.setDestinationFileName(mergedTempFile.getAbsolutePath());
//             
//             int validPdfCount = 0;
//             StringBuilder skippedLayers = new StringBuilder();
//             
//             for (EdcrPdfDetail detail : edcrPdfDetails) {
//                 File pdfFile = detail.getTempFile();
//                 if (pdfFile != null && pdfFile.exists() && pdfFile.length() > 0) {
//                     merger.addSource(pdfFile);
//                     tempFilesToCleanup.add(pdfFile); 
//                     validPdfCount++;
//                 } else {
//                     String layerName2 = (detail.getLayer() != null ? detail.getLayer() : "unknown");
//                     if (skippedLayers.length() > 0) skippedLayers.append(", ");
//                     skippedLayers.append(layerName2);
//                     
//                     if (pdfFile == null) {
//                         LOG.debug("Skipping layer '" + layerName2 + "' - no PDF file was generated (likely no valid layers in DXF)");
//                     } else if (!pdfFile.exists()) {
//                         LOG.warn("Skipping layer '" + layerName2 + "' - PDF file does not exist: " + pdfFile.getAbsolutePath());
//                     } else {
//                         LOG.warn("Skipping layer '" + layerName2 + "' - PDF file is empty: " + pdfFile.getAbsolutePath());
//                     }
//                 }
//             }
//             
//             if (skippedLayers.length() > 0) {
//                 LOG.info("Layers not included in " + layerName + " merge: " + skippedLayers.toString() + " (likely missing from DXF or failed conversion)");
//             }
//             
//             if (validPdfCount == 0) {
//                 LOG.warn("No valid PDF files found to merge for layer: " + layerName);
//                 return;
//             }
//             
//             if (!checkMemoryAvailability()) {
//                 LOG.warn("Insufficient memory detected before merging " + layerName + ". Aborting merge operation.");
//                 return;
//             }
//             
//             long memoryThresholdBytes = mergeMemoryThresholdMB * 1024 * 1024;
//             
//             if (mergeMemoryThresholdMB < 1 || mergeMemoryThresholdMB > 500) {
//                 LOG.warn("Invalid merge memory threshold: " + mergeMemoryThresholdMB + "MB, using default 50MB");
//                 memoryThresholdBytes = 50 * 1024 * 1024;
//             }
//             
//             LOG.info("PDF merge using mixed memory strategy with " + (memoryThresholdBytes / 1024 / 1024) + "MB threshold for " + layerName);
//             merger.mergeDocuments(MemoryUsageSetting.setupMixed(memoryThresholdBytes));
//             
//             LocalDateTime now = LocalDateTime.now();
//             String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
//             String mergedFileName = layerName + "_" + timestamp + ".pdf";
//             
//             FileStoreMapper mergedFileStoreMapper = fileStoreService.store(
//                 new FileInputStream(mergedTempFile), 
//                 mergedFileName, 
//                 "application/pdf", 
//                 DcrConstants.FILESTORE_MODULECODE,
//                 tenantId
//             );
// 
//             EdcrPdfDetail combinedDetail = new EdcrPdfDetail();
//             combinedDetail.setConvertedFileStoreMapper(mergedFileStoreMapper);
//             combinedDetail.setLayer(layerName);
//             edcrPdfDetails.add(combinedDetail);
//             
//             LOG.info("Successfully merged " + validPdfCount + " PDF files for layer: " + layerName);
// 
//         } catch (OutOfMemoryError ex) {
//             LOG.error("OutOfMemoryError during PDF merging for " + layerName + ": " + ex.getMessage());
//             LOG.error("This may indicate the need to increase DXF_PDF_MERGE_MEMORY_MB configuration or reduce DXF file complexity");
//             forceGarbageCollection(); 
//         } catch (IOException ex) {
//             LOG.error("IOException during PDF merging for " + layerName + ": " + ex.getMessage());
//         } catch (Exception ex) {
//             LOG.error("Unexpected error during PDF merging for " + layerName + ": " + ex.getMessage(), ex);
//         } finally {
//             if (tempFilesToCleanup != null) {
//                 for (File tempFile : tempFilesToCleanup) {
//                     if (tempFile != null && tempFile.exists()) {
//                         try {
//                             if (!tempFile.delete()) {
//                                 LOG.warn("Failed to delete temp file: " + tempFile.getAbsolutePath());
//                                 tempFile.deleteOnExit();
//                             }
//                         } catch (SecurityException e) {
//                             LOG.warn("Security exception while deleting temp file: " + e.getMessage());
//                             tempFile.deleteOnExit();
//                         }
//                     }
//                 }
//                 
//                 tempFilesToCleanup.clear();
//             }
//             
//             if (mergedTempFile != null && mergedTempFile.exists()) {
//                 try {
//                     if (!mergedTempFile.delete()) {
//                         LOG.warn("Failed to delete merged temp file: " + mergedTempFile.getAbsolutePath());
//                         mergedTempFile.deleteOnExit();
//                     }
//                 } catch (SecurityException e) {
//                     LOG.warn("Security exception while deleting merged temp file: " + e.getMessage());
//                     mergedTempFile.deleteOnExit();
//                 }
//             }
//             
//             merger = null;
//             
//             if (LOG.isDebugEnabled()) {
//                 Runtime runtime = Runtime.getRuntime();
//                 long freeMemory = runtime.freeMemory() / 1024 / 1024;
//                 LOG.debug("Memory after merge cleanup - Free: " + freeMemory + "MB");
//             }
//         }
//     }
//     
//     private static File addTextToPdf(File originalPdfFile, String text) throws Exception {
//         File modifiedFile = createTempFile("modified_", ".pdf");
//         
//         PdfReader reader = new PdfReader(new FileInputStream(originalPdfFile));
//         PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(modifiedFile));      
//         stamper.getWriter().setCompressionLevel(PdfStream.BEST_COMPRESSION);
//         
// 
//         PdfContentByte canvas = stamper.getOverContent(1);
//         Rectangle pageSize = reader.getPageSize(1);
// 
//         Font font = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.BLACK);
//         Phrase phrase = new Phrase(text, font);
// 
//         float x = pageSize.getRight() - 100;
//         float y = pageSize.getTop() - 20;
// 
//         ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, phrase, x, y, 0);
//         
//         stamper.setFullCompression(); 
//         stamper.close();
//         reader.close();
//         
// 
//         originalPdfFile.delete();
//         return modifiedFile;
//     }
// 
//     private List<String> checkNegetiveWidth(DXFLayer dxfLayer, EdcrPdfDetail pdfDetail) {
// 
//         StringBuilder errorBuffer = new StringBuilder();
// 
//         List<String> blks = new ArrayList<>();
//         ArrayList<String> errors = new ArrayList<>();
//         boolean negetiveWidhPresent = false;
//         List insertEntites = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_INSERT);
// 
//         if (insertEntites != null && insertEntites.size() > 0) {
//             for (Object o : insertEntites) {
//                 DXFInsert insert = (DXFInsert) o;
// 
//                 if (insert.getScaleX() < 0 || insert.getScaleY() < 0) {
//                     if (LOG.isDebugEnabled())
//                         LOG.debug("Negetive width in " + insert.getBlockID());
//                     if (LOG.isDebugEnabled())
//                         LOG.debug("nsert.getScaleX()" + insert.getScaleX());
//                     if (LOG.isDebugEnabled())
//                         LOG.debug("nsert.getScaleY()" + insert.getScaleY());
//                     insert.setScaleX(1);
//                     insert.setScaleY(1);
//                     blks.add(insert.getBlockID());
//                     negetiveWidhPresent = true;
//                     insert.setLineWeight(-1);
// 
//                 }
//             }
//         }
// 
//         if (negetiveWidhPresent) {
//             errorBuffer.append("Negetive with Present in Block(s)");
//             for (String blk : blks) {
//                 errorBuffer = errorBuffer.append(blk).append(", ");
//             }
// 
//         }
// 
//         String insertError = errorBuffer.toString();
//         if (insertError != null && !StringUtils.isBlank(insertError)) {
//             errors.add("" + insertError.substring(0, insertError.length() - 1) + ".");
//         }
// 
//         if (!errors.isEmpty()) {
//             for (String error : errors) {
//                 if (pdfDetail.getFailureReasons() == null)
//                     pdfDetail.setFailureReasons(error);
//                 else {
//                     error = error + pdfDetail.getFailureReasons();
//                     pdfDetail.setFailureReasons(error);
//                 }
// 
//             }
//         }
// 
//         return errors;
// 
//     }
// 
//     private boolean isDuplicatePresent(List<String> layerList) {
//         Set<String> duplicateLayerList = layerList.stream().filter(i -> Collections.frequency(layerList, i) > 1)
//                 .collect(Collectors.toSet());
//         return duplicateLayerList.isEmpty() ? false : true;
//     }
// 
//     private void sanitizeTexts(EdcrPdfDetail pdfDetail, DXFDocument doc, DXFLayer dxfLayer) {
// 
//         List texts = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_TEXT);
//         StringBuilder message = new StringBuilder();
//         if (texts != null && texts.size() > 0) {
//             long issueCount = 0;
//             StringBuilder errorMText = new StringBuilder();
//             Iterator iterator = texts.iterator();
//             while (iterator.hasNext()) {
//                 DXFText text = (DXFText) iterator.next();
//                 boolean underLinePresent = text.getText().contains(UNDERLINE_CAPITAL)
//                         || text.getText().contains(UNDERLINE_SMALL);
//                 if (underLinePresent) {
//                     text.setText(text.getText().replace(UNDERLINE_CAPITAL, ""));
//                     Iterator styledParagraphIterator = text.getTextDocument().getStyledParagraphIterator();
//                     while (styledParagraphIterator.hasNext()) {
//                         StyledTextParagraph styledTextParagraph = (StyledTextParagraph) styledParagraphIterator.next();
//                         styledTextParagraph.setUnderline(true);
//                         styledTextParagraph.setValign(TEXT_VALLIGNMENT_TOP);
//                     }
//                 }
// 
//                 boolean powerPresent = text.getText().contains(POWER);
// 
//                 if (powerPresent) {
//                     text.setText(text.getText().replace(POWER, ""));
//                 }
// 
//                 if (text.getText().contains("{") || text.getText().contains("}")) {
//                     issueCount++;
//                     if (errorMText.toString().split(",").length < 5) {
//                         if (StringUtils.isNotBlank(text.getText()))
//                             errorMText.append(text.getText()).append(",");
//                     }
//                 }
// 
//             }
// 
//             if (issueCount > 0) {
//                 message.append("Text defined as ").append(errorMText.toString(), 0, errorMText.toString().length() - 1)
//                         .append(issueCount > 5 ? " and " + (issueCount - 5) + " others " : "")
//                         .append(" are not as per standards.|");
//                 pdfDetail.setStandardViolations(message.toString());
//             }
// 
//         }
//     }
// 
//     private void sanitizeMtext(EdcrPdfDetail pdfDetail, DXFDocument doc, DXFLayer dxfLayer) {
// 
//         List mtexts = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_MTEXT);
//         StringBuilder message = new StringBuilder();
//         if (mtexts != null && mtexts.size() > 0) {
//             String text = "";
// 
//             long issueCount = 0;
//             for (Object o : mtexts) {
//                 DXFMText mText = (DXFMText) o;
//                 boolean underLinePresent = mText.getText().contains("\\L") || mText.getText().contains("\\l");
// 
//                 LOG.debug(mText.getText() + " Under line Present" + underLinePresent);
//                 mText.setText(mText.getText().replace(UNDERLINE_CAPITAL, ""));
//                 mText.setText(mText.getText().replace(UNDERLINE_SMALL, ""));
//                 Iterator styledParagraphIterator = mText.getTextDocument().getStyledParagraphIterator();
// 
//                 while (styledParagraphIterator.hasNext()) {
//                     StyledTextParagraph styledTextParagraph = (StyledTextParagraph) styledParagraphIterator.next();
// 
//                     if (underLinePresent) {
//                         styledTextParagraph.setUnderline(true);
//                         if (LOG.isDebugEnabled())
//                             LOG.debug("Styled Paragraph.get text " + styledTextParagraph.getText());
//                         styledTextParagraph.setValign(TEXT_VALLIGNMENT_TOP);
//                     }
// 
//                     if (styledTextParagraph.getInsertPoint().getX() == 0) {
//                         styledTextParagraph.getInsertPoint().setX(mText.getInsertPoint().getX());
//                     }
// 
//                     if (styledTextParagraph.getInsertPoint().getY() == 0) {
//                         styledTextParagraph.getInsertPoint().setY(mText.getInsertPoint().getY());
//                     }
//                 }
// 
//                 boolean powerPresent = mText.getText().contains(POWER);
// 
//                 if (powerPresent) {
//                     mText.setText(mText.getText().replace(POWER, ""));
//                 }
// 
//                 if (mText.getText().contains("{") || mText.getText().contains("}")) {
//                     issueCount++;
//                     if (issueCount == 1)
//                         text = mText.getText();
//                 }
//             }
// 
//             if (issueCount > 0) {
//                 message.append("Mtext defined as ").append(text)
//                         .append(issueCount > 5 ? " and " + (issueCount - 5) + " others " : "")
//                         .append(" are not as per standards.|");
//                 pdfDetail.setStandardViolations(message.toString());
//             }
//         }
// 
//     }
// 
//     private void sanitizeDimension(EdcrPdfDetail pdfDetail, DXFDocument doc, DXFLayer dxfLayer) {
// 
//         List dimensions = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_DIMENSION);
//         StringBuilder message = new StringBuilder();
//         if (dimensions != null && dimensions.size() > 0) {
//             long issueCount = 0;
//             Iterator iterator = dimensions.iterator();
//             StringBuffer mText = new StringBuffer();
// 
//             while (iterator.hasNext()) {
//                 DXFDimension dimension = (DXFDimension) iterator.next();
//                 /*
//                  * if (sampleDim == null) { sampleDim = dimension; }
//                  */
//                 dimension.setVisibile(false);
//                 String dimensionBlock = dimension.getDimensionBlock();
//                 DXFBlock dxfBlock = doc.getDXFBlock(dimensionBlock);
//                 if (dxfBlock != null) {
//                     Iterator entitiesIterator = dxfBlock.getDXFEntitiesIterator();
//                     boolean issuePresent = false;
// 
//                     while (entitiesIterator.hasNext()) {
//                         DXFEntity e = (DXFEntity) entitiesIterator.next();
// 
//                         if (e.getType().equalsIgnoreCase(DXFConstants.ENTITY_TYPE_LINE)) {
//                             DXFLine dxfLine = (DXFLine) e;
//                             if (dxfLine.getLineWeight() > 1) {
//                                 dxfLine.setLineWeight(-1);
//                                 issuePresent = true;
//                             }
//                         }
// 
//                         if (e.getType().equalsIgnoreCase(DXFConstants.ENTITY_TYPE_SOLID)) {
//                             DXFSolid dxfSolid = (DXFSolid) e;
//                             if (dxfSolid.getLineWeight() > 1) {
//                                 dxfSolid.setLineWeight(-1);
//                                 if (issuePresent = false)
//                                     issuePresent = true;
//                             }
//                         }
// 
//                         if (e.getType().equals(DXFConstants.ENTITY_TYPE_MTEXT)) {
//                             DXFMText dxfmText = (DXFMText) e;
//                             dxfmText.setHeight(0.2d);
//                             if (issuePresent) {
//                                 issueCount++;
//                                 if (mText.toString().split(",").length < 5) {
//                                     mText.append(dxfmText.getText()).append(",");
//                                 }
//                             }
//                         }
//                     }
//                 }
//             }
// 
//             if (mText != null && mText.length() > 0) {
//                 message.append("Line weight defined for " + (issueCount > 5 ? " dimensions " : " dimension "))
//                         .append(mText.toString(), 0, mText.toString().length() - 1)
//                         .append(issueCount > 5 ? " and " + (issueCount - 5) + " others " : "")
//                         .append(" are not as per standards.");
//                 pdfDetail.setStandardViolations(message.toString());
// 
//             }
// 
//         }
//     }
// 
//     private List<EdcrPdfDetail> getPdfLayerNames(PlanDetail planDetail, String appConfigValue) {
// 
//         boolean evaluate = false;
//         List<EdcrPdfDetail> pdfLayers = new ArrayList<>();
//         EdcrPdfDetail pdfdetail = new EdcrPdfDetail();
//         List<String> layers = new ArrayList<>();
//         String sheetName = "";
//         String layerNamesRegEx = "";
//         String sheetNameFinal = "";
//         String pageSize = "";
//         int enlarger = 1;
//         String orientation = "Portrait";
//         PdfPageSize page = new PdfPageSize();
//         // Name_of_the_sheet,PageSize,multiplication_factor_of_Page_Size,#Layer_regex:Measurement(M)/Dimension(D)LayerNametoInclude(L)ColorCode(C1),Repeat
// 
//         // BLK_*_FLR_*_FLOOR_PLAN,A0,1#BLK_*_FLR_*_FLOOR_PLAN,BLK_*_FLR_*_BLT_UP_AREA:ML,BLK_*_FLR_*_BLT_UP_AREA_DEDUCT:DL
//         // SITE_PLAN,A0,1#SITE_PLAN
//         // PARKING_PLAN_NO_*,A1,1#PARKING_PLAN_NO_*,PARKING_SLOT:M
//         // BLK_*_FLR_*_UNIT_FA,A0,1#BLK_*_FLR_*_BLT_UP_AREA:ML,BLK_*_FLR_*_BLT_UP_AREA_DEDUCT:DL,BLK_*_FLR_*_UNITFA:M
//         // COMPLETE_PLAN,A0,4#*
// 
//         // if (appConfigValue.contains("_*")) {
//         String[] regEx = appConfigValue.split("#");
//         if (regEx.length != 2) {
//             LOG.error("RegEx for PDF print in " + appConfigValue + "  is not as per Standard");
//             return pdfLayers;
//         } else {
//             try {
//                 sheetName = regEx[0];
//                 layerNamesRegEx = regEx[1];
//                 String[] split = sheetName.split(",");
//                 if (split.length < 4) {
//                     LOG.error(
//                             "Page size,name etc not defined properly format is 'name,pagesize,nooftimes,LANDSCAPE/PORTRAIT,removehatch");
//                     return pdfLayers;
//                 }
//                 sheetName = split[0];
// 
//                 // set page size
//                 page.setSize(split[1]);
//                 // set
//                 if (!split[2].equals("1"))
//                     enlarger = Integer.valueOf(split[2]);
//                 page.setEnlarge(enlarger);
// 
//                 if (!split[3].equalsIgnoreCase(orientation))
//                     page.setOrientation(Orientation.LANDSCAPE);
//                 else
//                     page.setOrientation(Orientation.PORTRAIT);
//                 if (split.length >= 5)
//                     page.setRemoveHatch(Boolean.valueOf(split[4]));
//                 else
//                     page.setRemoveHatch(false);
// 
//             } catch (NumberFormatException e) {
//                 LOG.error("RegEx for PDF print in " + appConfigValue + "  is not as per Standard");
//             }
// 
//         }
//         layers = new ArrayList<>();
//         if (layerNamesRegEx.equals("*")) {
// 
//             pdfdetail = new EdcrPdfDetail();
//             pdfdetail.setPageSize(page);
// 
//             sheetNameFinal = sheetName;
//             pdfdetail.setLayer(sheetNameFinal);
//             // List<String> layer= new ArrayList<>();
//             layers.add("All");
//             pdfdetail.setLayers(layers);
//             // pdfLayers.add(pdfdetail);
//             // pdfdetail.getPrintNameLayers().add("All");
//         } else if (appConfigValue.contains("BLK_*")) {
//             String[] split = layerNamesRegEx.split(","); // split by comma
//             for (Block b : planDetail.getBlocks()) {
//                 for (Floor f : b.getBuilding().getFloors()) {
//                     sheetNameFinal = sheetName.replace("BLK_*", "BLK_" + b.getNumber());
//                     sheetNameFinal = sheetNameFinal.replace("FLR_*", "FLR_" + f.getNumber());
//                     sheetNameFinal = sheetNameFinal.replace("LVL_*", "LVL_" + f.getNumber());
//                     sheetNameFinal = sheetNameFinal.replace("_*", "_" + b.getNumber());
//                     // sheetNameFinal =
//                     // sheetNameFinal.substring(0,sheetNameFinal.indexOf(":"));
//                     pdfdetail = new EdcrPdfDetail();
//                     pdfdetail.setPageSize(page);
//                     pdfdetail.setLayer(sheetNameFinal);
//                     evaluate = true;
//                     for (String s : split) {
//                         s = s.replace("BLK_*", "BLK_" + b.getNumber());
//                         s = s.replace("FLR_*", "FLR_" + f.getNumber());
//                         s = s.replace("LVL_*", "LVL_" + f.getNumber());
//                         s = s.replace("_*", "_" + b.getNumber());
//                         getLayerColorConfigs(planDetail, pdfdetail, s);
//                         s = s.substring(0, s.indexOf(":") != -1 ? s.indexOf(":") : s.length());
// 
//                         List<String> layer = Util.getLayerNamesLike(planDetail.getDxfDocument(), s);
// 
//                         if (layer != null && !layer.isEmpty()) {
//                             if (pdfdetail.getLayers() == null || pdfdetail.getLayers().isEmpty()) {
//                                 pdfdetail.setLayers(layer);
//                             } else {
//                                 pdfdetail.getLayers().addAll(layer);
//                             }
// 
//                         }
//                     }
//                     pdfLayers.add(pdfdetail);
// 
//                 }
//             }
//         } else if (appConfigValue.contains("NO_*")) {
// 
//             // fix this case after getting usecase
//             pdfdetail = new EdcrPdfDetail();
//             pdfdetail.setPageSize(page);
//             pdfdetail.setLayer(sheetNameFinal);
//             int i = 1;
//             String[] split = layerNamesRegEx.split(",");
//             for (String s : split) {
// 
//                 getLayerColorConfigs(planDetail, pdfdetail, s);
//                 s = s.substring(0, s.indexOf(":") != -1 ? s.indexOf(":") : s.length());
//                 s = s.replace("NO_*", "NO_" + i);
// 
//                 List<String> layer = Util.getLayerNamesLike(planDetail.getDxfDocument(), s);
//                 if (layer != null && !layer.isEmpty()) {
//                     if (pdfdetail.getLayers() == null || pdfdetail.getLayers().isEmpty()) {
//                         pdfdetail.setLayers(layer);
//                     } else {
//                         pdfdetail.getLayers().addAll(layer);
//                     }
// 
//                 }
//             }
// 
//         } else {
//             if (layerNamesRegEx.contains("_*")) {
//                 for (Block b : planDetail.getBlocks()) {
//                     pdfdetail = new EdcrPdfDetail();
//                     pdfdetail.setPageSize(page);
//                     pdfdetail.setLayer(sheetNameFinal);
//                     String[] split = layerNamesRegEx.split(",");
//                     for (String s : split) {
//                         s = s.replace("_*", "_" + b.getNumber());
//                         getLayerColorConfigs(planDetail, pdfdetail, s);
//                         s = s.substring(0, s.indexOf(":") != -1 ? s.indexOf(":") : s.length());
//                         layers.addAll(Util.getLayerNamesLike(planDetail.getDxfDocument(), s));
//                     }
//                 }
//             } else {
//                 pdfdetail = new EdcrPdfDetail();
//                 pdfdetail.setPageSize(page);
//                 pdfdetail.setLayer(sheetNameFinal);
//                 String[] split = layerNamesRegEx.split(",");
//                 for (String s : split) {
// 
//                     getLayerColorConfigs(planDetail, pdfdetail, s);
//                     s = s.substring(0, s.indexOf(":") != -1 ? s.indexOf(":") : s.length());
//                     layers.addAll(Util.getLayerNamesLike(planDetail.getDxfDocument(), s));
//                 }
//             }
// 
//         }
// 
//         if (!layers.isEmpty()) {
//             pdfdetail.setLayer(layers.get(0));
//             pdfdetail.setLayers(layers);
//             pdfLayers.add(pdfdetail);
//         }
// 
//         return pdfLayers;
// 
//     }
// 
//     private List<EdcrPdfDetail> getPdfLayerNames(PlanDetail planDetail, DxfToPdfLayerConfig config) {
//         List<EdcrPdfDetail> pdfLayers = new ArrayList<>();
//         boolean evaluate = false;
//         EdcrPdfDetail pdfdetail = new EdcrPdfDetail();
//         List<String> layers = new ArrayList<>();
//         String sheetName = config.getSheetName();
//         String layerNamesRegEx = "";
//         String sheetNameFinal = "";
//         PdfPageSize page = new PdfPageSize();
//         // Name_of_the_sheet,PageSize,multiplication_factor_of_Page_Size,#Layer_regex:Measurement(M)/Dimension(D)LayerNametoInclude(L)ColorCode(C1),Repeat
// 
//         // BLK_*_FLR_*_FLOOR_PLAN,A0,1#BLK_*_FLR_*_FLOOR_PLAN,BLK_*_FLR_*_BLT_UP_AREA:ML,BLK_*_FLR_*_BLT_UP_AREA_DEDUCT:DL
//         // SITE_PLAN,A0,1#SITE_PLAN
//         // PARKING_PLAN_NO_*,A1,1#PARKING_PLAN_NO_*,PARKING_SLOT:M
//         // BLK_*_FLR_*_UNIT_FA,A0,1#BLK_*_FLR_*_BLT_UP_AREA:ML,BLK_*_FLR_*_BLT_UP_AREA_DEDUCT:DL,BLK_*_FLR_*_UNITFA:M
//         // COMPLETE_PLAN,A0,4#*
// 
//         // set page size
//         page.setSize(config.getSheetSize());
//         page.setEnlarge(config.getSheetSizeEnlargeFactor());
// 
//         page.setOrientation(config.getOrientation());
//         page.setRemoveHatch(config.isRemoveHatch());
//         pdfdetail.setPageSize(page);
// 
//         layers = new ArrayList<>();
//         String layerRegEx = constructIntoSingleLineConfig(config);
//         if (layerRegEx.contains("COMPLETE_PLAN")) {
//             pdfdetail = new EdcrPdfDetail();
//             pdfdetail.setPageSize(page);
// 
//             sheetNameFinal = sheetName;
//             pdfdetail.setLayer(sheetNameFinal);
//             layers.add("All");
//             pdfdetail.setLayers(layers);
//         } else if (layerRegEx.contains("BLK_*")) {
//             String[] split = layerRegEx.split(","); // split by comma
//             layerNamesRegEx = split[0];
//             
//             boolean isSitePlanAdded = false;
//             
//             for (Block b : planDetail.getBlocks()) {
// 
//                 for (Floor f : b.getBuilding().getFloors()) {
//                     sheetNameFinal = sheetName.replace("BLK_*", "BLK_" + b.getNumber());
//                     sheetNameFinal = sheetNameFinal.replace("FLR_*", "FLR_" + f.getNumber());
//                     sheetNameFinal = sheetNameFinal.replace("LVL_*", "LVL_" + f.getNumber());
//                     sheetNameFinal = sheetNameFinal.replace("_*", "_" + b.getNumber());
//                     // sheetNameFinal =
//                     // sheetNameFinal.substring(0,sheetNameFinal.indexOf(":"));
//                     pdfdetail = new EdcrPdfDetail();
//                     pdfdetail.setPageSize(page);
//                     pdfdetail.setLayer(sheetNameFinal);
//                     evaluate = true;
//                     for (String s : split) {
//                         s = s.replace("BLK_*", "BLK_" + b.getNumber());
//                         s = s.replace("FLR_*", "FLR_" + f.getNumber());
//                         s = s.replace("LVL_*", "LVL_" + f.getNumber());
//                         s = s.replace("_*", "_" + b.getNumber());
// 						getLayerColorConfigs(planDetail, pdfdetail, s);
// 						s = s.substring(0, s.indexOf(":") != -1 ? s.indexOf(":") : s.length());
// 
// 						List<String> layer = Util.getLayerNamesLike(planDetail.getDxfDocument(), s);
// 
// 						if (layer != null && !layer.isEmpty()) {
// 
// 							if ("SITE_PLAN".equals(layer.get(0))) {
// 								// Check if SITE_PLAN is already added
// 								if (isSitePlanAdded) {
// 									continue; // Skip additional SITE_PLAN layers
// 								}
// 								isSitePlanAdded = true; // Mark SITE_PLAN as added
// 							}
// 
// 							if (pdfdetail.getLayers() == null || pdfdetail.getLayers().isEmpty()) {
// 								pdfdetail.setLayers(layer);
// 							} else {
// 								pdfdetail.getLayers().addAll(layer);
// 							}
// 						}
// 
// 					}
// 
// 					pdfLayers.add(pdfdetail);
// 
//                 }
//             }
// 
//         } else if (layerRegEx.contains("NO_*")) {
// 
//             pdfdetail = new EdcrPdfDetail();
//             pdfdetail.setPageSize(page);
//             int i = 1;
//             String[] split = layerRegEx.split(",");
//             for (String s : split) {
// 
//                 getLayerColorConfigs(planDetail, pdfdetail, s);
//                 s = s.substring(0, s.indexOf(":") != -1 ? s.indexOf(":") : s.length());
//                 s = s.replace("NO_*", "NO_" + i);
//                 pdfdetail.setLayer(s);
//                 List<String> layer = Util.getLayerNamesLike(planDetail.getDxfDocument(), s);
//                 if (layer != null && !layer.isEmpty()) {
//                     if (pdfdetail.getLayers() == null || pdfdetail.getLayers().isEmpty()) {
//                         pdfdetail.setLayers(layer);
//                     } else {
//                         pdfdetail.getLayers().addAll(layer);
//                     }
// 
//                 }
//             }
//             pdfLayers.add(pdfdetail);
// 
//         } else if (layerRegEx.contains("SECTION_PLAN_*") || layerRegEx.contains("ELEVATION_PLAN_*")) {
//             String[] split = layerRegEx.split(","); // split by comma
// 
//             for (Block block : planDetail.getBlocks()) {
//                 String blockNumber = block.getNumber(); // Get the block number
//                 for (String s : split) {
//                     // Replace '*' with the block number
//                     String layerPattern = s.replace("*", blockNumber);
// 
//                     // Get all matching layers based on the updated pattern
//                     List<String> matchingLayers = Util.getLayerNamesLike(planDetail.getDxfDocument(), layerPattern);
// 
//                     if (matchingLayers != null && !matchingLayers.isEmpty()) {
//                         for (String layer : matchingLayers) {
//                             EdcrPdfDetail pdfDetail = new EdcrPdfDetail();
//                             pdfDetail.setPageSize(page); // Set page size
//                             pdfDetail.setLayer(layer); // Set the individual layer name
//                             pdfDetail.setLayers(Collections.singletonList(layer)); // Add the single layer to layers
// 
//                             // Configure color and other properties
//                             getLayerColorConfigs(planDetail, pdfDetail, layer);
// 
//                             // Add to the list
//                             pdfLayers.add(pdfDetail);
//                         }
//                     }
//                 }
//             }
//         } else {
//             if (layerRegEx.contains("_*")) {
//                 for (Block b : planDetail.getBlocks()) {
//                     pdfdetail = new EdcrPdfDetail();
//                     pdfdetail.setPageSize(page);
//                     pdfdetail.setLayer(sheetNameFinal);
//                     String[] split = layerRegEx.split(",");
//                     for (String s : split) {
//                         s = s.replace("_*", "_" + b.getNumber());
//                         getLayerColorConfigs(planDetail, pdfdetail, s);
//                         s = s.substring(0, s.indexOf(":") != -1 ? s.indexOf(":") : s.length());
//                         layers.addAll(Util.getLayerNamesLike(planDetail.getDxfDocument(), s));
//                     }
//                 }
//             } else {
//                 pdfdetail = new EdcrPdfDetail();
//                 pdfdetail.setPageSize(page);
//                 pdfdetail.setLayer(sheetNameFinal);
//                 String[] split = layerRegEx.split(",");
//                 for (String s : split) {
//                     getLayerColorConfigs(planDetail, pdfdetail, s);
//                     s = s.substring(0, s.indexOf(":") != -1 ? s.indexOf(":") : s.length());
//                     layers.addAll(Util.getLayerNamesLike(planDetail.getDxfDocument(), s));
//                 }
//             }
//         }
//         if (!layers.isEmpty()) {
//             pdfdetail.setLayer(layers.get(0));
//             pdfdetail.setLayers(layers);
//             pdfLayers.add(pdfdetail);
//         }
//         // }
// 
//         return pdfLayers;
// 
//     }
// 
//     private String constructIntoSingleLineConfig(DxfToPdfLayerConfig config) {
//         StringBuilder layerRegEx = new StringBuilder();
//         Iterator<PlanPdfLayerConfig> itr = config.getPlanPdfLayerConfigs().iterator();
//         while (itr.hasNext()) {
//             PlanPdfLayerConfig pc = itr.next();
//             layerRegEx = layerRegEx.append(pc.getLayerName());
//             if (pc.getLayerType() != null)
//                 layerRegEx = layerRegEx.append(":").append(pc.getLayerType());
//             if (pc.getOverrideColor() != 0)
//                 layerRegEx = layerRegEx.append(pc.getOverrideColor());
//             if (pc.getOverrideThickness() != 0)
//                 layerRegEx = layerRegEx.append(pc.getOverrideThickness());
//             if (itr.hasNext())
//                 layerRegEx = layerRegEx.append(",");
//         }
//         return layerRegEx.toString();
//     }
// 
//     private void getLayerColorConfigs(PlanDetail planDetail, EdcrPdfDetail pdfdetail, String s) {
//         if (s.indexOf(":") != -1) {
// 
//             String[] layerAndConf = s.split(":");
// 
//             List<String> layerNamesLike = Util.getLayerNamesLike(planDetail.getDxfDocument(), layerAndConf[0]);
// 
//             if (layerAndConf[1].contains("ML") || s.contains("DL")) {
//                 pdfdetail.getPrintNameLayers().addAll(layerNamesLike);
//             }
//             if (layerAndConf[1].contains("M")) {
//                 // s=s.substring(0,s.indexOf(":"));
//                 if (pdfdetail.getMeasurementLayers() == null) {
//                     pdfdetail.setMeasurementLayers(new ArrayList<>());
//                 }
//                 pdfdetail.getMeasurementLayers().addAll(layerNamesLike);
//             }
//             if (layerAndConf[1].contains("D")) {
//                 // s=s.substring(0,s.indexOf(":D"));
//                 if (pdfdetail.getDimensionLayers() == null) {
//                     pdfdetail.setDimensionLayers(new ArrayList<>());
//                 }
//                 pdfdetail.getDimensionLayers().addAll(layerNamesLike);
// 
//             }
// 
//             if (layerAndConf[1].contains("C")) {
//                 String color = "";
//                 if (layerAndConf[1].contains("T"))
//                     color = layerAndConf[1].substring(layerAndConf[1].indexOf("C") + 1,
//                             layerAndConf[1].indexOf("T") - 1);
//                 else
//                     color = layerAndConf[1].substring(layerAndConf[1].indexOf("C") + 1, layerAndConf[1].length());
//                 if (color != null) {
//                     Integer no = Integer.parseInt(color);
//                     for (String ln : layerNamesLike) {
//                         pdfdetail.getColorOverrides().put(ln, no);
//                     }
//                 }
//             }
//             if (layerAndConf[1].contains("T")) {
//                 String color = layerAndConf[1].substring(layerAndConf[1].indexOf("T") + 1, layerAndConf[1].length());
//                 if (color != null) {
//                     Integer no = Integer.parseInt(color);
//                     for (String ln : layerNamesLike) {
//                         pdfdetail.getThicknessOverrides().put(ln, no);
//                     }
//                 }
//             }
//         }
//     }
// 
//     private void getLayerColorConfigs(PlanDetail planDetail, EdcrPdfDetail pdfdetail, PlanPdfLayerConfig planLayer) {
// 
//         List<String> layerNamesLike = Util.getLayerNamesLike(planDetail.getDxfDocument(), planLayer.getLayerName());
// 
//         if (planLayer.getLayerType() != null && planLayer.isPrintLayerName() && (planLayer.getLayerType().equalsIgnoreCase("M")
//                 || planLayer.getLayerType().equalsIgnoreCase("D"))) {
//             pdfdetail.getPrintNameLayers().addAll(layerNamesLike);
//         }
//         if (planLayer.getLayerType() != null && planLayer.getLayerType().equalsIgnoreCase("M")) {
//             if (pdfdetail.getMeasurementLayers() == null) {
//                 pdfdetail.setMeasurementLayers(new ArrayList<>());
//             }
//             pdfdetail.getMeasurementLayers().addAll(layerNamesLike);
// 
//         }
//         if (planLayer.getLayerType() != null && planLayer.getLayerType().equalsIgnoreCase("D")) {
//             if (pdfdetail.getDimensionLayers() == null) {
//                 pdfdetail.setDimensionLayers(new ArrayList<>());
//             }
//             pdfdetail.getDimensionLayers().addAll(layerNamesLike);
// 
//         }
// 
//         if (planLayer.getOverrideColor() != 0) {
//             for (String ln : layerNamesLike) {
//                 pdfdetail.getColorOverrides().put(ln, planLayer.getOverrideColor());
//             }
//         }
//         if (planLayer.getOverrideThickness() != 0) {
//             for (String ln : layerNamesLike) {
//                 pdfdetail.getThicknessOverrides().put(ln, planLayer.getOverrideThickness());
//             }
//         }
//     }
//     
// 	private String determineCategory(PlanDetail planDetail) {
// 
// 		OdishaUlbs ulb = OdishaUlbs.getUlb(planDetail.getThirdPartyUserTenantld());
// 		String isSpecial = planDetail.getPlanInfoProperties()
// 				.get("IS_THE_PROJECT_COMING_UNDER_SPECIAL_BUILDING_CATEGORY");
// 		
// 		if(isSpecial==null) {
// 			planDetail.addErrorMsg("dxfToPdf", "Declaring IS_THE_PROJECT_COMING_UNDER_SPECIAL_BUILDING_CATEGORY is mandatory in the plan info.");
// 		}
// 
// 		BigDecimal buildingHeight = planDetail.getBlocks().stream()
// 		        .map(block -> block.getBuilding().getBuildingHeight())
// 		        .max(BigDecimal::compareTo)
// 		        .orElse(BigDecimal.ZERO);
// 
// 		BigDecimal plotArea = planDetail.getPlot().getArea();
// 		
// 		String serviceCatByBuildingHeight = determineServiceByBuildingHeight(buildingHeight.doubleValue(), Boolean.getBoolean(isSpecial));
// 		String serviceCatByPlotArea = determineServiceByPlotArea(plotArea.doubleValue(), ulb, Boolean.getBoolean(isSpecial));
// 		
// 
// 		return getHigherPriorityService(serviceCatByBuildingHeight, serviceCatByPlotArea);
// 	}
// 	
//     public boolean areRequiredLayersPresent(PlanDetail planDetail, String category) {
//         // Extract all layers from the DXF document using an iterator
//         List<String> availableLayers = new ArrayList<>();
//         Iterator<?> layerIterator = planDetail.getDxfDocument().getDXFLayerIterator();
//         while (layerIterator.hasNext()) {
//             DXFLayer layer = (DXFLayer) layerIterator.next();
//             availableLayers.add(layer.getName());
//         }
// 
//         // Determine block size
//         int blkSize = planDetail.getBlocks().size();
//         boolean isMultipleBlocks = blkSize > 1;
// 
//         // Determine required layer patterns based on category and block conditions
//         List<String> requiredPatterns = getRequiredLayerPatterns(category, isMultipleBlocks);
// 
//         // Validate each required pattern
//         List<String> missingLayers = new ArrayList<>();
//         for (String requiredPattern : requiredPatterns) {
//             boolean layerExists = availableLayers.stream()
//                     .anyMatch(layer -> Pattern.matches(requiredPattern, layer));
// 
//             if (!layerExists) {
//             	missingLayers.add(requiredPattern);
//             }
//         }
// 
//         if (!missingLayers.isEmpty()) {
//             StringBuilder errorMessages = new StringBuilder("The following layers: '");
//             for (String missingLayer : missingLayers) {
//                 errorMessages.append(missingLayer + ", ");
//             }
//             planDetail.addErrorMsg("LayerMissing", errorMessages.toString()+"' are missing in the drawing!");
//             return false;
//         }
// 
//         return true;
// 
//     }
// 	
//     private List<String> getRequiredLayerPatterns(String category, boolean isMultipleBlocks) {
//         if ("Category A".equalsIgnoreCase(category)) {
//             return Arrays.asList("SITE_PLAN");
//         } else if ("Category B".equalsIgnoreCase(category)) {
//             if (isMultipleBlocks) {
//                 return Arrays.asList("SITE_PLAN", "BLK_.*_FLR_.*_FLOOR_PLAN");
//             } else {
//                 return Arrays.asList("SITE_PLAN");
//             }
//         } else if ("Category C".equalsIgnoreCase(category) || "Category D".equalsIgnoreCase(category)) {
//             return Arrays.asList(
//                 "SITE_PLAN",
//                 "BLK_.*_FLR_.*_FLOOR_PLAN",
//                 "ELEVATION_PLAN_.*",
//                 "SECTION_PLAN_.*",
//                 "SERVICE_PLAN"
//             );
//         } else {
//             throw new IllegalArgumentException("The drawing does not fall under the defined categories, please contact the SUJOG Administrator.");
//         }
//     }
//     
//     
//     private String determineServiceByBuildingHeight(Double buildingHeight, boolean isSpecialBuilding) {
// 	    if (isSpecialBuilding) {
// 	        if (buildingHeight <= 15) {
// 	            return "Category B";
// 	        } else if (buildingHeight > 15 && buildingHeight <= 30) {
// 	            return "Category C";
// 	        } else {
// 	            return "Category D";
// 	        }
// 	    } else {
// 	        if (buildingHeight <= 10) {
// 	            return "Category A";
// 	        } else if (buildingHeight > 10 && buildingHeight <= 15) {
// 	            return "Category B";
// 	        } else if (buildingHeight > 15 && buildingHeight <= 30) {
// 	            return "Category C";
// 	        } else {
// 	            return "Category D";
// 	        }
// 	    }
// 	}
//     
//     private String determineServiceByPlotArea(Double plotArea, OdishaUlbs ulb, boolean isSpecialBuilding) {
// 		if (plotArea <= 500) {
// 			return "Category A";
// 		} else if (plotArea > 500 && plotArea <= 4047) {
// 			return "Category B";
// 		} else if (plotArea > 4047 && (ulb.isSparitFlag() ? plotArea <= 20000 : plotArea <= 10000)) {
// 			return "Category C";
// 		} else {
// 			return "Category D";
// 		}
// 
// 	}
//     
//     private static String getHigherPriorityService(String service1, String service2) {
// 	    // Business service priority mapping
// 	    Map<String, Integer> servicePriority = new HashMap<>();
// 	    servicePriority.put("Category A", 1);
// 	    servicePriority.put("Category B", 2);
// 	    servicePriority.put("Category C", 3);
// 	    servicePriority.put("Category D", 4);
// 
// 		// Compare priorities and return the higher one
// 		return servicePriority.get(service1) >= servicePriority.get(service2) ? service1 : service2;
// 	}
// 
//     private PlanDetail extractOriginalLogic(PlanDetail planDetail) {
//         
//         String categoryOfProject = determineCategory(planDetail);
//         
//         if (!areRequiredLayersPresent(planDetail, categoryOfProject))
//             return planDetail;
//         
//         Boolean mdmsEnabled = mdmsConfiguration.getMdmsEnabled();
//         boolean mdmsDxfToPdfEnabled = false;
//         if (mdmsEnabled != null && mdmsEnabled) {
//             City stateCity = cityService.fetchStateCityDetails();
//             String tenantID = ApplicationThreadLocals.getTenantID();
//             Object mdmsData = edcrMdmsUtil.mDMSCall(new RequestInfo(),
//                     new StringBuilder().append(stateCity.getCode()).append(".").append(tenantID).toString());
// 
//             if (mdmsData == null) {
//                 tenantID = stateCity.getCode();
//                 mdmsData = edcrMdmsUtil.mDMSCall(new RequestInfo(), tenantID);
//             }
//             if (mdmsData != null) {
//                 Map<String, List<Object>> edcrMdmsConfig = mdmsValidator.getAttributeValues(mdmsData,
//                         DcrConstants.MDMS_EDCR_MODULE);
//                 MdmsEdcrResponse mdmsEdcrResponse = null;
//                 try {
//                     List<Object> dxfToPdfMdmsEnabled = edcrMdmsConfig.get("DxfToPdfConfig");
// 
//                     String jsonStr = new JSONObject((LinkedHashMap<?, ?>) dxfToPdfMdmsEnabled.get(0)).toString();
//                     ObjectMapper mapper = new ObjectMapper();
//                     mdmsEdcrResponse = mapper.readValue(jsonStr, MdmsEdcrResponse.class);
//                 } catch (IOException e) {
//                     LOG.error("Error occured while reading mdms data", e);
//                 }
//                 if (mdmsEdcrResponse != null && mdmsEdcrResponse.getEnabled().equals("true")) {
//                     mdmsDxfToPdfEnabled = true;              
//                     
//                     List<Object> dxfToPdfConfig1 = null;
//                     List<Object> dxfToPdfConfig2 = null;
//                   
//                     int blkSize = planDetail.getBlocks().size();
//                     
//                     if (categoryOfProject.equals("Category A")) {
//                         dxfToPdfConfig2 = edcrMdmsConfig.get("DxfToPdfLayerConfigCatA_ALL");
//                         dxfToPdfConfig1 = edcrMdmsConfig.get("DxfToPdfLayerConfigCatA_Base");
//                         
//                     } else if (categoryOfProject.equals("Category B")) {
//                         if (blkSize == 1) {
//                             dxfToPdfConfig2 = edcrMdmsConfig.get("DxfToPdfLayerConfigCatB_SingleBlock_ALL");
//                             dxfToPdfConfig1 = edcrMdmsConfig.get("DxfToPdfLayerConfigCatB_SingleBlock_Base");
//                         } else {
//                             dxfToPdfConfig2 = edcrMdmsConfig.get("DxfToPdfLayerConfigCatB_MultiBlock_ALL");
//                             dxfToPdfConfig1 = edcrMdmsConfig.get("DxfToPdfLayerConfigCatB_MultiBlock_Base");
//                         }                       
//                     } else if (categoryOfProject.equals("Category C") || categoryOfProject.equals("Category D")) {
//                         dxfToPdfConfig2 = edcrMdmsConfig.get("DxfToPdfLayerConfigCat_CD_ALL");
//                         dxfToPdfConfig1 = edcrMdmsConfig.get("DxfToPdfLayerConfigCat_CD_Base");
//                     }
//                     
//                    
//                     List<List<Object>> listOfDxfToPdfConfig = new ArrayList<>();
//                     listOfDxfToPdfConfig.add(dxfToPdfConfig1);
//                     listOfDxfToPdfConfig.add(dxfToPdfConfig2);
//                     
//                     List<EdcrPdfDetail> edcrPdfDetails1 = new ArrayList<>();
//                     List<EdcrPdfDetail> edcrPdfDetails2 = new ArrayList<>();
//                     
//                     int configIndex = 0;
//                     for (List<Object> dxfToPdfConfig : listOfDxfToPdfConfig) {
//                         List<EdcrPdfDetail> currentPdfDetails;
//                         if (configIndex == 0) {
//                             currentPdfDetails = edcrPdfDetails1;
//                         } else {
//                             currentPdfDetails = edcrPdfDetails2;
//                         }
//                         configIndex++;
// 
//                         for (Object obj : dxfToPdfConfig) {
//                             try {
//                                 String jsonString = new JSONObject((LinkedHashMap<?, ?>) obj).toString();
//                                 ObjectMapper mapper1 = new ObjectMapper();
//                                 DxfToPdfLayerConfig config = mapper1.readValue(jsonString, DxfToPdfLayerConfig.class);
//                                 List<EdcrPdfDetail> layerNameList = getPdfLayerNames(planDetail, config);
//                                 for (EdcrPdfDetail d : layerNameList) {
//                                     LOG.info("\t\t\tSheetName : " + d.getLayer() + " , list of layers :\n" + d.getLayers());
//                                 }
//                                 if (layerNameList != null && !layerNameList.isEmpty()) {
//                                     currentPdfDetails.addAll(layerNameList);
//                                 }
//                             } catch (IOException e) {
//                                 LOG.error("Error occurred while reading mdms data", e);
//                             }
//                         }
//                     }
//                     
//                     planDetail.setEdcrPdfDetails1(edcrPdfDetails1);
//                     planDetail.setEdcrPdfDetails2(edcrPdfDetails2);
//                 }
// 
//             }
//         } else {
//             List<AppConfigValues> dxfToPdfAppConfigEnabled = appConfigValueService
//                     .getConfigValuesByModuleAndKey(DcrConstants.APPLICATION_MODULE_TYPE, DcrConstants.DXF_PDF_CONVERSION_ENABLED);
// 
//             if (!dxfToPdfAppConfigEnabled.isEmpty() && dxfToPdfAppConfigEnabled.get(0).getValue().equalsIgnoreCase("NO"))
//                 return planDetail;
//         }
// 
//         if (!mdmsDxfToPdfEnabled) {
//             List<AppConfigValues> appConfigValues = appConfigValueService
//                     .getConfigValuesByModuleAndKey(DcrConstants.APPLICATION_MODULE_TYPE, DcrConstants.EDCR_DXF_PDF);
//             for (AppConfigValues appConfigValue : appConfigValues) {
//                 if (LOG.isDebugEnabled())
//                     LOG.debug("App Config value :" + appConfigValue.getValue());
//                 List<EdcrPdfDetail> layerNameList = getPdfLayerNames(planDetail, appConfigValue.getValue());
//                 for (EdcrPdfDetail d : layerNameList) {
//                     if (LOG.isDebugEnabled())
//                         LOG.debug("\t\t\tSheetName : " + d.getLayer() + " , list of layers :\n" + d.getLayers());
//                 }
//                 // get a particular layer from the document and enable the layer
//                 if (layerNameList != null && !layerNameList.isEmpty()) {
// 
//                     if (planDetail.getEdcrPdfDetails() == null)
//                         planDetail.setEdcrPdfDetails(layerNameList);
//                     else
//                         planDetail.getEdcrPdfDetails().addAll(layerNameList);
//                 }
//             }
//         }
// 
//         validate(planDetail);
// 
//         String fileName = planDetail.getApplicationDate().toString();
//         
//         fileName = fileName.replaceAll("\\s", "");
//         fileName = fileName.replaceAll(":", "");
//         
//         if (LOG.isDebugEnabled())
//             LOG.debug("*************** Converting " + fileName + " to pdf ***************" + "\n");
// 
//         List<EdcrPdfDetail> edcrPdfDetails1 = planDetail.getEdcrPdfDetails1();
//         List<EdcrPdfDetail> edcrPdfDetails2 = planDetail.getEdcrPdfDetails2();
//         
//         Boolean printSingleSheet = false;
//         EdcrPdfDetail printSingleSheetDetails = null;
// 
//         Iterator dxfBlockIterator = planDetail.getDxfDocument().getDXFBlockIterator();
//         while (dxfBlockIterator.hasNext()) {
//             DXFBlock block = (DXFBlock) dxfBlockIterator.next();
//             Iterator dxfEntitiesIterator = block.getDXFEntitiesIterator();
//             while (dxfEntitiesIterator.hasNext()) {
//                 DXFEntity e = (DXFEntity) dxfEntitiesIterator.next();
//                 e.setLineWeight(-1);
// 
//             }
//         }
//         Iterator dxfStyleIterator = planDetail.getDxfDocument().getDXFStyleIterator();
// 
//         while (dxfStyleIterator.hasNext()) {
//             DXFStyle style = (DXFStyle) dxfStyleIterator.next();
// 
//             LOG.debug(",,DXF style,,,,,    " + style.getName() + "    " + style.getFontFile() + ""
//                     + style.getWidthFactor());
//             style.setWidthFactor(-1);
//             style.setFontFile("romans");
//             style.setBigFontFile("romans");
//             style.setName("romans");
//         }
// 
//         Iterator layerIterator = planDetail.getDxfDocument().getDXFLayerIterator();
//         while (layerIterator.hasNext()) {
//             DXFLayer layer = (DXFLayer) layerIterator.next();
//             layer.setFlags(1);
//         }
//         
//         BigInteger referenceNumber = getNextReferenceNumber();
//         int currentYear = LocalDate.now().getYear();
//         char categoryInitial = categoryOfProject.charAt(categoryOfProject.length() - 1);
//         String uniqueReferenceNumber = String.format("%d%c%07d", currentYear, categoryInitial, referenceNumber);       
//         // YYYY "CATEGORY A/B/C/D" 7 DIGITS
// 
//         for (EdcrPdfDetail edcrPdfDetail : edcrPdfDetails1) {
// 
//             if (edcrPdfDetail.getLayers() == null || edcrPdfDetail.getLayers().isEmpty()) {
//                 LOG.warn("Skipping PDF detail '" + edcrPdfDetail.getLayer() + "' - no layers defined");
//                 continue;
//             }
// 
//             if (edcrPdfDetail.getLayers().contains("All")) {
//                 printSingleSheet = true;
//                 printSingleSheetDetails = edcrPdfDetail;
//                 continue;
//             }
//            
//             // Check if any of the required layers actually exist in the DXF
//             boolean hasValidLayers = false;
//             for (String layerName : edcrPdfDetail.getLayers()) {
//                 DXFLayer dxfLayer = planDetail.getDxfDocument().getDXFLayer(layerName);
//                 if (dxfLayer != null) {
//                     hasValidLayers = true;
//                     break;
//                 }
//             }
//             
//             if (!hasValidLayers) {
//                 LOG.warn("Skipping PDF detail '" + edcrPdfDetail.getLayer() + "' - none of the required layers exist in DXF: " + edcrPdfDetail.getLayers());
//                 continue;
//             }
//             
//             enablePrintableLayers(edcrPdfDetail, planDetail.getDxfDocument());
//             sanitize(fileName, planDetail.getDxfDocument(), edcrPdfDetail, planDetail);
// 
//             FileStoreMapper fileStoreMapper = convertDxfToPdf(planDetail, fileName, edcrPdfDetail.getLayer(), edcrPdfDetail, uniqueReferenceNumber);
//             disablePrintableLayers(edcrPdfDetail, planDetail.getDxfDocument());
// 
//             if (fileStoreMapper != null) {
//             	edcrPdfDetail.setConvertedFileStoreMapper(fileStoreMapper);
//             }
// 
//         }
// 
//         for (EdcrPdfDetail edcrPdfDetail : edcrPdfDetails2) {
// 
//             if (edcrPdfDetail.getLayers() == null || edcrPdfDetail.getLayers().isEmpty()) {
//                 LOG.warn("Skipping PDF detail '" + edcrPdfDetail.getLayer() + "' - no layers defined");
//                 continue;
//             }
// 
//             if (edcrPdfDetail.getLayers().contains("All")) {
//                 printSingleSheet = true;
//                 printSingleSheetDetails = edcrPdfDetail;
//                 continue;
//             }
//            
//             // Check if any of the required layers actually exist in the DXF
//             boolean hasValidLayers = false;
//             for (String layerName : edcrPdfDetail.getLayers()) {
//                 DXFLayer dxfLayer = planDetail.getDxfDocument().getDXFLayer(layerName);
//                 if (dxfLayer != null) {
//                     hasValidLayers = true;
//                     break;
//                 }
//             }
//             
//             if (!hasValidLayers) {
//                 LOG.warn("Skipping PDF detail '" + edcrPdfDetail.getLayer() + "' - none of the required layers exist in DXF: " + edcrPdfDetail.getLayers());
//                 continue;
//             }
//             
//             enablePrintableLayers(edcrPdfDetail, planDetail.getDxfDocument());
//             sanitize(fileName, planDetail.getDxfDocument(), edcrPdfDetail, planDetail);
// 
//             FileStoreMapper fileStoreMapper = convertDxfToPdf(planDetail, fileName, edcrPdfDetail.getLayer(), edcrPdfDetail, uniqueReferenceNumber);
//             disablePrintableLayers(edcrPdfDetail, planDetail.getDxfDocument());
// 
//             if (fileStoreMapper != null) {
//             	edcrPdfDetail.setConvertedFileStoreMapper(fileStoreMapper);
//             }
// 
//         }
// 
//         // enable all layers back
//         layerIterator = planDetail.getDxfDocument().getDXFLayerIterator();
//         while (layerIterator.hasNext()) {
//             DXFLayer layer = (DXFLayer) layerIterator.next();
//             layer.setFlags(0);
//             if (printSingleSheet && !layer.getName().equalsIgnoreCase("0")) {
//                 printSingleSheetDetails.getMeasurementLayers().add(layer.getName());
// 
//             }
//         }
// 
//         if (printSingleSheet) {
// 
//             sanitize(fileName, planDetail.getDxfDocument(), printSingleSheetDetails, planDetail);
// 
//             FileStoreMapper fileStoreMapper = convertDxfToPdf(planDetail, fileName, printSingleSheetDetails.getLayer(),
//                     printSingleSheetDetails, uniqueReferenceNumber);
// 
//             if (fileStoreMapper != null) {
//                 printSingleSheetDetails.setConvertedFileStoreMapper(fileStoreMapper);
//             }
// 
//         }
// 
//         if (!edcrPdfDetails1.isEmpty() && !edcrPdfDetails2.isEmpty()) {
//             planDetail.getPlanInformation().setDxfToPdfCorrelationId(uniqueReferenceNumber);
//         } else {
//             if (categoryOfProject.equals("Category A")) {
//                 planDetail.addErrorMsg("DxfToPdf", "DXF to PDF failed! Site Plan layer is missing from the drawing.");
//             } else if (categoryOfProject.equals("Category B")) {
//                 planDetail.addErrorMsg("DxfToPdf",
//                         "DXF to PDF failed! Site Plan/Floor Plan layer is missing from the drawing.");
//             } else {
//                 planDetail.addErrorMsg("DxfToPdf",
//                         "DXF to PDF failed! Site Plan / Floor Plan / Elevation Plan / Section Plan / Service Plan layer is missing from the drawing.");
//             }
//         }
//         
//         // merge all pdfs
//         generateCombinedPdf(planDetail, planDetail.getThirdPartyUserTenantld());
// 
//         return planDetail;
//     }
//     
//     private void generateCombinedPdf(PlanDetail planDetail, String tenantId) {
// 		generateCombinedPdfForDetails(planDetail.getEdcrPdfDetails1(), "BASE_LAYERS", tenantId);
// 		generateCombinedPdfForDetails(planDetail.getEdcrPdfDetails2(), "BASE_AND_OBPAS_LAYERS", tenantId);
// 	}
// }// ===== END ORIGINAL IMPLEMENTATION =====
