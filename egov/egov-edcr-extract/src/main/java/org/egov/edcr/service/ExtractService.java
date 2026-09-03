package org.egov.edcr.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
// STUBBED OUT (not present in this branch's egov-commons): import org.egov.common.entity.ApplicationType;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.PlanFeature;
import org.egov.common.entity.edcr.PlanInformation;
// STUBBED OUT (not present in this branch's egov-commons): import org.egov.commons.mdms.EDCRMdmsUtil;
import org.egov.commons.mdms.config.MdmsConfiguration;
// STUBBED OUT (not present in this branch's egov-commons): import org.egov.commons.mdms.model.MdmsEdcrResponse;
import org.egov.commons.mdms.validator.MDMSValidator;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.entity.Amendment;
import org.egov.edcr.entity.AmendmentDetails;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.feature.FeatureExtract;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.City;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.custom.CustomImplProvider;
import org.egov.infra.microservice.models.RequestInfo;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.json.simple.JSONObject;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.parser.DXFParser;
import org.kabeja.parser.ParseException;
import org.kabeja.parser.Parser;
import org.kabeja.parser.ParserBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ExtractService {
    @Autowired
    private CustomImplProvider specificRuleService;
    @Autowired
    private AppConfigValueService appConfigValueService;
    // STUBBED OUT (not present in this branch's egov-commons): @Autowired private EDCRMdmsUtil edcrMdmsUtil;
    @Autowired
    private MdmsConfiguration mdmsConfiguration;
    @Autowired
    private CityService cityService;
    @Autowired
    private MDMSValidator mdmsValidator;

    private Logger LOG = Logger.getLogger(ExtractService.class);

    public Plan extract(File dxfFile, Amendment amd, Date scrutinyDate, List<PlanFeature> features /* , String tenantId, ApplicationType applicationType — STUBBED OUT: tenantId setter and applicationType type not present in this branch's egov-commons */ ) {

        PlanInformation pi = new PlanInformation();
        DXFDocument doc = getDxfDocument(dxfFile);
        PlanDetail planDetail = new PlanDetail();
        planDetail.setDoc(doc);
        planDetail.setPlanInformation(pi);
        planDetail.setApplicationDate(scrutinyDate);
        // STUBBED OUT (not present in this branch's egov-commons): planDetail.setThirdPartyUserTenantld(tenantId);
        // STUBBED OUT (not present in this branch's egov-commons): planDetail.setApplicationType(applicationType);
        Map<String, String> cityDetails = specificRuleService.getCityDetails();

        if (doc.getDXFHeader().getVariable("$INSUNITS") != null) {
            String unitValue = doc.getDXFHeader().getVariable("$INSUNITS").getValue("70");
            if ("1".equalsIgnoreCase(unitValue)) {
                planDetail.getDrawingPreference().setUom(DxfFileConstants.INCH_UOM);
            } else if ("2".equalsIgnoreCase(unitValue)) {
                planDetail.getDrawingPreference().setUom(DxfFileConstants.FEET_UOM);
            } else if ("6".equalsIgnoreCase(unitValue)) {
                planDetail.getDrawingPreference().setUom(DxfFileConstants.METER_UOM);
            } else {
                planDetail.getDrawingPreference().setInMeters(false);
                planDetail.getErrors().put("units not in meters", "The 'Drawing Unit' is not as per standard. ");
            }
        }

        /*
         * // dimension length factor should be 1 if (doc.getDXFHeader() != null && doc.getDXFHeader().getVariable("$DIMLFAC") !=
         * null) { BigDecimal dimensionLengthFactor = new BigDecimal( doc.getDXFHeader().getVariable("$DIMLFAC").getValue("40"));
         * if (dimensionLengthFactor.compareTo(BigDecimal.ONE) != 0) { planDetail.getDrawingPreference().setLengthFactor(false);
         * planDetail.getErrors().put("length factor", "The dimension length factor is not 1."); } }
         */
        if (planDetail.getErrors().size() > 0)
            return (Plan) planDetail;
        Boolean mdmsEnabled = mdmsConfiguration.getMdmsEnabled();
        // STUBBED OUT (not present in this branch's egov-commons: EDCRMdmsUtil, MdmsEdcrResponse,
        // MDMSValidator#getAttributeValues(Object,String), PlanDetail#setStrictlyValidateBldgHeightDimension):
        // the entire mdmsEnabled branch below is disabled; original block preserved as a comment.
        // if (mdmsEnabled != null && mdmsEnabled) {
        //     City stateCity = cityService.fetchStateCityDetails();
        //     String tenantID = ApplicationThreadLocals.getTenantID();
        //     Object mdmsData = edcrMdmsUtil.mDMSCall(new RequestInfo(),
        //             new StringBuilder().append(stateCity.getCode()).append(".").append(tenantID).toString());
        //
        //     if (mdmsData == null) {
        //         tenantID = stateCity.getCode();
        //         mdmsData = edcrMdmsUtil.mDMSCall(new RequestInfo(), tenantID);
        //     }
        //     if (mdmsData != null) {
        //         Map<String, List<Object>> edcrMdmsConfig = mdmsValidator.getAttributeValues(mdmsData,
        //                 DcrConstants.MDMS_EDCR_MODULE);
        //         List<Object> dimensionConfig = edcrMdmsConfig.get("DimensionConfig");
        //         LinkedHashMap<String, String> configs = new LinkedHashMap<>();
        //         for (Object obj : dimensionConfig) {
        //             try {
        //                 String jsonString = new JSONObject((LinkedHashMap<?, ?>) obj).toString();
        //                 ObjectMapper mapper = new ObjectMapper();
        //                 MdmsEdcrResponse res = mapper.readValue(jsonString, MdmsEdcrResponse.class);
        //                 configs.put(res.getCode(), res.getEnabled());
        //             } catch (IOException e) {
        //                 LOG.error("Error occured while reading mdms data", e);
        //             }
        //
        //         }
        //         if (!configs.isEmpty()) {
        //             planDetail.setStrictlyValidateDimension(
        //                     Boolean.valueOf(configs.get(DcrConstants.MDMS_STRICTLY_VALIDATE_DIMENSION)));
        //             planDetail.setStrictlyValidateBldgHeightDimension(
        //                     Boolean.valueOf(configs.get(DcrConstants.MDMS_STRICTLY_VALIDATE_BLDG_HGHT_DIMENSION)));
        //         }
        //
        //     }
        // } else {
        if (mdmsEnabled == null || true) {
            List<AppConfigValues> appConfigValueList = appConfigValueService.getConfigValuesByModuleAndKey(
                    DcrConstants.APPLICATION_MODULE_TYPE, DcrConstants.STRICTLY_VALIDATE_DIMENSION);

            if (appConfigValueList != null && !appConfigValueList.isEmpty()) {
                String value = appConfigValueList.get(0).getValue();
                planDetail.setStrictlyValidateDimension(DcrConstants.YES.equalsIgnoreCase(value));
            }
            List<AppConfigValues> bldgHghtDimensionValidation = appConfigValueService.getConfigValuesByModuleAndKey(
                    DcrConstants.APPLICATION_MODULE_TYPE, DcrConstants.STRICTLY_VALIDATE_BLDG_HGHT_DIMENSION);
            if (bldgHghtDimensionValidation != null && !bldgHghtDimensionValidation.isEmpty()) {
                // STUBBED OUT (PlanDetail#setStrictlyValidateBldgHeightDimension not present in this branch's egov-commons):
                // String value = bldgHghtDimensionValidation.get(0).getValue();
                // planDetail.setStrictlyValidateBldgHeightDimension(DcrConstants.YES.equalsIgnoreCase(value));
            }
        }

        int index = -1;
        AmendmentDetails[] a = null;
        int length = amd.getDetails().size();
        if (!amd.getDetails().isEmpty()) {
            index = amd.getIndex(planDetail.getApplicationDate());
            a = new AmendmentDetails[amd.getDetails().size()];
            amd.getDetails().toArray(a);
        }

        Date start = new Date();
        LOG.info("Initializeing fetch extract api" + start);
        for (PlanFeature ruleClass : features) {
            FeatureExtract rule = null;

            try {

                if (ruleClass.getRuleClass() != null) {
                    String str = ruleClass.getRuleClass().getSimpleName();
                    str = str.substring(0, 1).toLowerCase() + str.substring(1);
                    LOG.info("Looking for bean " + str);

                    if (amd.getDetails().isEmpty() || index == -1)
                        rule = (FeatureExtract) specificRuleService.find(str + "Extract");
                    else {

                        if (index >= 0) {
                            for (int i = index; i < length; i++) {
                                if (a[i].getChanges().keySet().contains(ruleClass.getClass().getSimpleName())) {
                                    String strNew = str + "Extract_" + a[i].getDateOfBylawString();

                                    rule = (FeatureExtract) specificRuleService.find(strNew);
                                    if (rule != null)
                                        break;
                                }

                            }

                        }

                        if (rule == null) {
                            rule = (FeatureExtract) specificRuleService.find(str + "Extract");
                        }
                        // for all amendments

                    }

                }
            } catch (Exception e) {
            	e.printStackTrace();
                LOG.error("Exception while finding extract api for  " + ruleClass.getRuleClass(), e);
            }

            if (rule != null) {
                LOG.info("Got bean ..." + rule.getClass().getSimpleName());
                try {
                    rule.extract(planDetail);
                } catch (RuntimeException e) {
                	LOG.error("========== RUNTIME EXCEPTION CAUGHT IN EXTRACT SERVICE: " + e.getMessage() + " ==========");
                	e.printStackTrace();
                	// Re-throw RuntimeException to propagate to caller
                	throw e;
                } catch (Exception e) {
                	e.printStackTrace();
                    planDetail.addError("msg.error.failed.on.extraction",
                            "Please contact the adminstrator for the further information. The plan is failing while extracting data from plan in the feature "
                                    + (rule.getClass().getSimpleName().equals("DxfToPdfConverterExtract") ? "CAD to PDF conversion.":rule.getClass().getSimpleName()));
                }
            } 
//            else
//                LOG.error("Extract Api is not defined for " + ruleClass.getRuleClass());

        }
        Date end = new Date();
        LOG.info("Ending fetch extract api" + end);
        return (Plan) planDetail;

    }
    
    public PlanDetail extractDxfToPdf(File dxfFile, Amendment amd, Date scrutinyDate, List<PlanFeature> features, String tenantId, Plan plan, Boolean isBasePdf) {

        PlanInformation pi = new PlanInformation();
        DXFDocument doc = getDxfDocument(dxfFile);
        PlanDetail planDetail = new PlanDetail();
        planDetail.setDoc(doc);
        planDetail.setPlanInformation(pi);
        planDetail.setApplicationDate(scrutinyDate);
        // STUBBED OUT (not present in this branch's egov-commons): planDetail.setThirdPartyUserTenantld(tenantId);


        try {
			BeanUtils.copyProperties(planDetail, plan);
			// STUBBED OUT (not present in this branch's egov-commons): planDetail.setIsBasePdf(isBasePdf);
		} catch (InvocationTargetException | IllegalAccessException e) {
			LOG.error("Error occured while copying parent class values to child class", e);
			e.printStackTrace();
		}
        
        Map<String, String> cityDetails = specificRuleService.getCityDetails();

        if (doc.getDXFHeader().getVariable("$INSUNITS") != null) {
            String unitValue = doc.getDXFHeader().getVariable("$INSUNITS").getValue("70");
            if ("1".equalsIgnoreCase(unitValue)) {
                planDetail.getDrawingPreference().setUom(DxfFileConstants.INCH_UOM);
            } else if ("2".equalsIgnoreCase(unitValue)) {
                planDetail.getDrawingPreference().setUom(DxfFileConstants.FEET_UOM);
            } else if ("6".equalsIgnoreCase(unitValue)) {
                planDetail.getDrawingPreference().setUom(DxfFileConstants.METER_UOM);
            } else {
                planDetail.getDrawingPreference().setInMeters(false);
                planDetail.getErrors().put("units not in meters", "The 'Drawing Unit' is not as per standard. ");
            }
        }

        /*
         * // dimension length factor should be 1 if (doc.getDXFHeader() != null && doc.getDXFHeader().getVariable("$DIMLFAC") !=
         * null) { BigDecimal dimensionLengthFactor = new BigDecimal( doc.getDXFHeader().getVariable("$DIMLFAC").getValue("40"));
         * if (dimensionLengthFactor.compareTo(BigDecimal.ONE) != 0) { planDetail.getDrawingPreference().setLengthFactor(false);
         * planDetail.getErrors().put("length factor", "The dimension length factor is not 1."); } }
         */
        if (planDetail.getErrors().size() > 0)
            return planDetail;
        Boolean mdmsEnabled = mdmsConfiguration.getMdmsEnabled();
        // STUBBED OUT (not present in this branch's egov-commons: EDCRMdmsUtil, MdmsEdcrResponse,
        // MDMSValidator#getAttributeValues(Object,String), PlanDetail#setStrictlyValidateBldgHeightDimension):
        // the entire mdmsEnabled branch below is disabled; original block preserved as a comment.
        // if (mdmsEnabled != null && mdmsEnabled) {
        //     City stateCity = cityService.fetchStateCityDetails();
        //     String tenantID = ApplicationThreadLocals.getTenantID();
        //     Object mdmsData = edcrMdmsUtil.mDMSCall(new RequestInfo(),
        //             new StringBuilder().append(stateCity.getCode()).append(".").append(tenantID).toString());
        //
        //     if (mdmsData == null) {
        //         tenantID = stateCity.getCode();
        //         mdmsData = edcrMdmsUtil.mDMSCall(new RequestInfo(), tenantID);
        //     }
        //     if (mdmsData != null) {
        //         Map<String, List<Object>> edcrMdmsConfig = mdmsValidator.getAttributeValues(mdmsData,
        //                 DcrConstants.MDMS_EDCR_MODULE);
        //         List<Object> dimensionConfig = edcrMdmsConfig.get("DimensionConfig");
        //         LinkedHashMap<String, String> configs = new LinkedHashMap<>();
        //         for (Object obj : dimensionConfig) {
        //             try {
        //                 String jsonString = new JSONObject((LinkedHashMap<?, ?>) obj).toString();
        //                 ObjectMapper mapper = new ObjectMapper();
        //                 MdmsEdcrResponse res = mapper.readValue(jsonString, MdmsEdcrResponse.class);
        //                 configs.put(res.getCode(), res.getEnabled());
        //             } catch (IOException e) {
        //                 LOG.error("Error occured while reading mdms data", e);
        //             }
        //
        //         }
        //         if (!configs.isEmpty()) {
        //             planDetail.setStrictlyValidateDimension(
        //                     Boolean.valueOf(configs.get(DcrConstants.MDMS_STRICTLY_VALIDATE_DIMENSION)));
        //             planDetail.setStrictlyValidateBldgHeightDimension(
        //                     Boolean.valueOf(configs.get(DcrConstants.MDMS_STRICTLY_VALIDATE_BLDG_HGHT_DIMENSION)));
        //         }
        //
        //     }
        // } else {
        if (mdmsEnabled == null || true) {
            List<AppConfigValues> appConfigValueList = appConfigValueService.getConfigValuesByModuleAndKey(
                    DcrConstants.APPLICATION_MODULE_TYPE, DcrConstants.STRICTLY_VALIDATE_DIMENSION);

            if (appConfigValueList != null && !appConfigValueList.isEmpty()) {
                String value = appConfigValueList.get(0).getValue();
                planDetail.setStrictlyValidateDimension(DcrConstants.YES.equalsIgnoreCase(value));
            }
            List<AppConfigValues> bldgHghtDimensionValidation = appConfigValueService.getConfigValuesByModuleAndKey(
                    DcrConstants.APPLICATION_MODULE_TYPE, DcrConstants.STRICTLY_VALIDATE_BLDG_HGHT_DIMENSION);
            if (bldgHghtDimensionValidation != null && !bldgHghtDimensionValidation.isEmpty()) {
                // STUBBED OUT (PlanDetail#setStrictlyValidateBldgHeightDimension not present in this branch's egov-commons):
                // String value = bldgHghtDimensionValidation.get(0).getValue();
                // planDetail.setStrictlyValidateBldgHeightDimension(DcrConstants.YES.equalsIgnoreCase(value));
            }
        }

        int index = -1;
        AmendmentDetails[] a = null;
        int length = amd.getDetails().size();
        if (!amd.getDetails().isEmpty()) {
            index = amd.getIndex(planDetail.getApplicationDate());
            a = new AmendmentDetails[amd.getDetails().size()];
            amd.getDetails().toArray(a);
        }

        Date start = new Date();
        LOG.info("Initializeing fetch extract api" + start);
        for (PlanFeature ruleClass : features) {
            FeatureExtract rule = null;

            try {

                if (ruleClass.getRuleClass() != null) {
                    String str = ruleClass.getRuleClass().getSimpleName();
                    str = str.substring(0, 1).toLowerCase() + str.substring(1);
                    LOG.info("Looking for bean " + str);

                    if (amd.getDetails().isEmpty() || index == -1)
                        rule = (FeatureExtract) specificRuleService.find(str + "Extract");
                    else {

                        if (index >= 0) {
                            for (int i = index; i < length; i++) {
                                if (a[i].getChanges().keySet().contains(ruleClass.getClass().getSimpleName())) {
                                    String strNew = str + "Extract_" + a[i].getDateOfBylawString();

                                    rule = (FeatureExtract) specificRuleService.find(strNew);
                                    if (rule != null)
                                        break;
                                }

                            }

                        }

                        if (rule == null) {
                            rule = (FeatureExtract) specificRuleService.find(str + "Extract");
                        }
                        // for all amendments

                    }

                }
            } catch (Exception e) {
            	e.printStackTrace();
                LOG.error("Exception while finding extract api for  " + ruleClass.getRuleClass(), e);
            }

            if (rule != null) {
                LOG.info("Got bean ..." + rule.getClass().getSimpleName());
                try {
                    rule.extract(planDetail);
                } catch (RuntimeException e) {
                	// Re-throw RuntimeException to propagate to caller
                	throw e;
                } catch (Exception e) {
                	e.printStackTrace();
                    planDetail.addError("msg.error.failed.on.extraction",
                            "Please contact the adminstrator for the further information. The plan is failing while extracting data from plan in the feature "
                                    + rule);
                }
            } 
//            else
//                LOG.error("Extract Api is not defined for " + ruleClass.getRuleClass());

        }
        Date end = new Date();
        LOG.info("Ending fetch extract api" + end);
        return planDetail;

    }

    private DXFDocument getDxfDocument(File file) {
        Parser parser = ParserBuilder.createDefaultParser();
        try {
            parser.parse(file.getPath(), DXFParser.DEFAULT_ENCODING);
        } catch (ParseException e) {
            LOG.error("Error in gettting default parser", e);
            // throw e;

            StackTraceElement[] stackTrace = e.getStackTrace();
            for (StackTraceElement ele : stackTrace) {
                if (ele.toString().toLowerCase().contains("font")) {
                    throw new ValidationException(
                            Arrays.asList(new ValidationError("Unsupported font is used", "Unsupported font is used")));
                }
            }

        } catch (NoSuchElementException e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (StackTraceElement ele : stackTrace) {
                if (ele.toString().toLowerCase().contains("font")) {
                    throw new ValidationException(
                            Arrays.asList(new ValidationError("Unsupported font is used", "Unsupported font is used")));
                }
            }
        } catch (Exception e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (StackTraceElement ele : stackTrace) {
                if (ele.toString().toLowerCase().contains("font")) {
                    throw new ValidationException(
                            Arrays.asList(new ValidationError("Unsupported font is used", "Unsupported font is used")));
                }
            }
        }
        // Extract DXF Data
        DXFDocument doc = parser.getDocument();
        return doc;
    }
    
    /**
     * Creates a DXFDocument from a file. If the file is a zip, extracts the DXF file first.
     * 
     * @param file The file to parse (can be a zip file containing a DXF)
     * @return The parsed DXFDocument
     * @throws ValidationException If there are issues with the DXF format or unsupported fonts
     */
    private DXFDocument getDxfDocument2(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        
        File dxfFile = file;
        File tempExtractedFile = null;
        
        try {
            // Check if the file is a zip
            if (isZipFile(file)) {
                tempExtractedFile = extractDxfFromZipFile(file);
                if (tempExtractedFile == null) {
                    throw new ValidationException(
                        Arrays.asList(new ValidationError("Invalid zip file", "No valid DXF file found in the zip")));
                }
                dxfFile = tempExtractedFile;
            }
            
            // Parse the DXF file
            Parser parser = ParserBuilder.createDefaultParser();
            try {
                parser.parse(dxfFile.getPath(), DXFParser.DEFAULT_ENCODING);
            } catch (ParseException | NoSuchElementException e) {
                LOG.error("Error in parsing DXF file", e);
                
                // Check if the error is related to fonts
                StackTraceElement[] stackTrace = e.getStackTrace();
                for (StackTraceElement ele : stackTrace) {
                    if (ele.toString().toLowerCase().contains("font")) {
                        throw new ValidationException(
                            Arrays.asList(new ValidationError("Unsupported font is used", "Unsupported font is used")));
                    }
                }
                
                throw new ValidationException(
                    Arrays.asList(new ValidationError("Invalid DXF file", "The file could not be parsed as a valid DXF")));
            }
            
            DXFDocument doc = parser.getDocument();
            return doc;
            
        } finally {
            if (tempExtractedFile != null && tempExtractedFile.exists()) {
                tempExtractedFile.delete();
            }
        }
    }

    /**
     * Checks if a file is a zip file based on its extension and content.
     */
    private boolean isZipFile(File file) {
        if (file.getName().toLowerCase().endsWith(".zip")) {
            return true;
        }
        
        // Check file signature (magic number)
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] signature = new byte[4];
            if (fis.read(signature) == 4) {
                // ZIP file signature: PK\03\04 (50 4B 03 04 in hex)
                return signature[0] == 0x50 && signature[1] == 0x4B && 
                       signature[2] == 0x03 && signature[3] == 0x04;
            }
        } catch (IOException e) {
            LOG.error("Error checking if file is a zip", e);
        }
        
        return false;
    }

    /**
     * Extracts a DXF file from a zip archive.
     * 
     * @param zipFile The zip file containing a DXF file
     * @return The extracted DXF file, or null if no DXF file was found
     * @throws ValidationException If multiple DXF files are found in the zip
     */
    private File extractDxfFromZipFile(File zipFile) throws ValidationException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            File extractedFile = null;
            
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                
                // Validate file extension is .dxf
                String fileName = entry.getName().toLowerCase();
                if (!fileName.endsWith(".dxf")) {
                    continue; // Skip non-DXF files
                }
                
                if (extractedFile != null) {
                    // Clean up the first file before throwing exception
                    extractedFile.delete();
                    throw new ValidationException(Arrays.asList(
                        new ValidationError("Multiple DXF files present", 
                                          "Only one DXF file is allowed in the zip archive")));
                }
                
                // Extract DXF file
                extractedFile = Files.createTempFile("extracted-dxf-", "-" + sanitizeFileName(entry.getName())).toFile();
                extractedFile.deleteOnExit();
                
                try (FileOutputStream fos = new FileOutputStream(extractedFile)) {
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = zipInputStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
            }
            
            return extractedFile;
        } catch (IOException e) {
            LOG.error("Error extracting DXF from zip file", e);
            throw new ValidationException(Arrays.asList(
                new ValidationError("Zip extraction error", "Failed to extract DXF file from zip: " + e.getMessage())));
        }
    }

    /**
     * Sanitizes the file name to prevent path traversal attacks.
     */
    private String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "unnamed-file";
        }
        String name = new File(fileName).getName();      
        return name.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

}
