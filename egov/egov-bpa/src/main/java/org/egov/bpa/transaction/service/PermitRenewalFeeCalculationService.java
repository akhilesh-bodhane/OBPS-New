/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2018>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.bpa.transaction.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.egov.bpa.transaction.entity.ApplicationFeeDetail;
import org.egov.bpa.transaction.entity.PermitFee;
import org.egov.bpa.transaction.entity.PermitRenewal;
import org.egov.bpa.transaction.notice.util.BpaNoticeUtil;
import org.egov.bpa.utils.BpaAppConfigUtil;
import org.egov.infra.utils.DateUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author vinoth
 *
 */
@Service
@Transactional(readOnly = true)
public class PermitRenewalFeeCalculationService {

    @Autowired
    private PermitRenewalService permitRenewalService;
    @Autowired
    private BpaAppConfigUtil appConfigUtil;
    @Autowired
    private BpaNoticeUtil bpaNoticeUtil;

    public Map<String, BigDecimal> calculateRenewalFee(final PermitRenewal permitRenewal) {
    	System.out.println("Inside Calculation Fee Method Permit Renewal");
        Map<String, BigDecimal> demandReasonAndAmt = new ConcurrentHashMap<>();
        BigDecimal totalPermitRenewalFee = BigDecimal.ZERO;
        BigDecimal totalPermitFee = getPermitFee(permitRenewal);
        String demandReasonCode = "PEF";
        
        
        
        /*try {
			Date planValidTillDate1 = sdf.parse(permitRenewal.getPlanValidTillDate().toString());
			Date planExtensionDate1 = sdf.parse(permitRenewal.getPlanExtensionDate().toString());
			long difference_In_Time = planExtensionDate1.getTime() - planValidTillDate1.getTime();
			long difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;
			System.out.println("difference_In_Days : " + difference_In_Days);
			long finalDays = difference_In_Days/365;
	        finalDays = finalDays * 1000;
	        System.out.println("finalDays : " + finalDays);
	        if(finalDays < 0 && finalDays > 1){
	        	finalDays = 1;
	        }
	        System.out.println("finalDays : " + finalDays);
	        totalPermitRenewalFee = totalPermitRenewalFee.add(BigDecimal.valueOf(finalDays));
	        System.out.println("totalPermitRenewalFee : " + totalPermitRenewalFee);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        
        System.out.println("Plan Valid Till Date : " + permitRenewal.getPlanValidTillDate());
        System.out.println("Plan Extension Date : " + permitRenewal.getPlanExtensionDate());
        
        LocalDate planValidTillDate = LocalDate.parse(permitRenewal.getPlanValidTillDate().toString());//LocalDate.parse(permitRenewal.getPlanValidTillDate().toString());
        int validTillYear = planValidTillDate.getYear();
        System.out.println("validTillYear : " + validTillYear);
        
        LocalDate planExtensionDate = LocalDate.parse(permitRenewal.getPlanExtensionDate().toString());//LocalDate.parse(permitRenewal.getPlanExtensionDate().toString());
        int extensionYear = planExtensionDate.getYear();
        System.out.println("extensionYear : " + extensionYear);
        
        System.out.println("planValidTillDate : " + planValidTillDate);
        System.out.println("planExtensionDate : " + planExtensionDate);
         
        //long noOfDays = Duration.between(planExtensionDate, planValidTillDate).toDays();
        //int yearExtension = (extensionYear - validTillYear) * 1000;  
        
        
        long daysBetween = ChronoUnit.DAYS.between(planValidTillDate, planExtensionDate);
        System.out.println("daysBetween : " + daysBetween);
        
        long finalDays = daysBetween/365;
        System.out.println("finalDays : " + finalDays);
        if(finalDays < 0 && finalDays > 1){
        	finalDays = 1;
        }
        
        if(daysBetween%365 != 0){
        	System.out.println("Inside modular method");
        	finalDays = finalDays + 1;
        }
        
        finalDays = finalDays * 1000;
        
        System.out.println("finalDays : " + finalDays);
        totalPermitRenewalFee = totalPermitRenewalFee.add(BigDecimal.valueOf(finalDays));
        System.out.println("totalPermitRenewalFee : " + totalPermitRenewalFee);
        
        demandReasonCode = "PRF";
        /*Date permitExpiryDate = DateUtils.toDateUsingDefaultPattern(bpaNoticeUtil.calculateCertExpryDate(
                new DateTime(permitRenewal.getParent().getPlanPermissionDate()),
                permitRenewal.getParent().getServiceType().getValidity()));
        Date minAllowedRenewalDate = DateUtils.toDateUsingDefaultPattern(
                permitRenewalService.getMinAllowedDateForRenewalPriorExpiry(permitRenewal.getParent()));
        Date maxAllowedRenewalDate = DateUtils.toDateUsingDefaultPattern(
                permitRenewalService.getMaxAllowedDateForRenewalAfterExpiry(permitRenewal.getParent()));
        if (permitRenewal.getApplicationDate().compareTo(minAllowedRenewalDate) >= 0
                && permitRenewal.getApplicationDate().compareTo(permitExpiryDate) <= 0) {
            BigDecimal permitExtensionFee = appConfigUtil.getPermitExtensionFeeInPercentage();
            totalPermitRenewalFee = totalPermitFee.multiply(permitExtensionFee)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            demandReasonCode = "PEF";
        } else if (permitRenewal.getApplicationDate().after(permitExpiryDate)
                && permitRenewal.getApplicationDate().compareTo(maxAllowedRenewalDate) <= 0) {
            BigDecimal permitRenewalFeeInPercent = appConfigUtil.getPermitRenewalFeeInPercentage();
            totalPermitRenewalFee = totalPermitFee.multiply(permitRenewalFeeInPercent)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            demandReasonCode = "PRF";
        }*/
        demandReasonAndAmt.put(demandReasonCode, totalPermitRenewalFee.setScale(0, BigDecimal.ROUND_HALF_UP));
        return demandReasonAndAmt;
    }
    
    public Map<String, BigDecimal> calculateRenewalFeeNew(final PermitRenewal permitRenewal, String planValidTillDate1, String planExtensionDate1) {
    	System.out.println("Inside Calculation Fee Method Permit Renewal");
        Map<String, BigDecimal> demandReasonAndAmt = new ConcurrentHashMap<>();
        BigDecimal totalPermitRenewalFee = BigDecimal.ZERO;
        BigDecimal totalPermitFee = getPermitFee(permitRenewal);
        String demandReasonCode = "PEF";
        
        
        
        /*try {
			Date planValidTillDate1 = sdf.parse(permitRenewal.getPlanValidTillDate().toString());
			Date planExtensionDate1 = sdf.parse(permitRenewal.getPlanExtensionDate().toString());
			long difference_In_Time = planExtensionDate1.getTime() - planValidTillDate1.getTime();
			long difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;
			System.out.println("difference_In_Days : " + difference_In_Days);
			long finalDays = difference_In_Days/365;
	        finalDays = finalDays * 1000;
	        System.out.println("finalDays : " + finalDays);
	        if(finalDays < 0 && finalDays > 1){
	        	finalDays = 1;
	        }
	        System.out.println("finalDays : " + finalDays);
	        totalPermitRenewalFee = totalPermitRenewalFee.add(BigDecimal.valueOf(finalDays));
	        System.out.println("totalPermitRenewalFee : " + totalPermitRenewalFee);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        
        System.out.println("Plan Valid Till Date1 : " + planValidTillDate1);
        System.out.println("Plan Extension Date1 : " + planExtensionDate1);
        
        LocalDate planValidTillDate = LocalDate.parse(planValidTillDate1);//LocalDate.parse(permitRenewal.getPlanValidTillDate().toString());
        int validTillYear = planValidTillDate.getYear();
        System.out.println("validTillYear : " + validTillYear);
        
        LocalDate planExtensionDate = LocalDate.parse(planExtensionDate1);//LocalDate.parse(permitRenewal.getPlanExtensionDate().toString());
        int extensionYear = planExtensionDate.getYear();
        System.out.println("extensionYear : " + extensionYear);
        
        System.out.println("planValidTillDate : " + planValidTillDate);
        System.out.println("planExtensionDate : " + planExtensionDate);
         
        //long noOfDays = Duration.between(planExtensionDate, planValidTillDate).toDays();
        //int yearExtension = (extensionYear - validTillYear) * 1000;  
        
        
        long daysBetween = ChronoUnit.DAYS.between(planValidTillDate, planExtensionDate);
        System.out.println("daysBetween : " + daysBetween);
        
        long finalDays = daysBetween/365;
        System.out.println("finalDays : " + finalDays);
        if(finalDays < 0 && finalDays > 1){
        	finalDays = 1;
        }
        
        if(daysBetween%365 != 0){
        	System.out.println("Inside modular method");
        	finalDays = finalDays + 1;
        }
        
        finalDays = finalDays * 1000;
        
        System.out.println("finalDays : " + finalDays);
        totalPermitRenewalFee = totalPermitRenewalFee.add(BigDecimal.valueOf(finalDays));
        System.out.println("totalPermitRenewalFee : " + totalPermitRenewalFee);
        
        demandReasonCode = "PRF";
        /*Date permitExpiryDate = DateUtils.toDateUsingDefaultPattern(bpaNoticeUtil.calculateCertExpryDate(
                new DateTime(permitRenewal.getParent().getPlanPermissionDate()),
                permitRenewal.getParent().getServiceType().getValidity()));
        Date minAllowedRenewalDate = DateUtils.toDateUsingDefaultPattern(
                permitRenewalService.getMinAllowedDateForRenewalPriorExpiry(permitRenewal.getParent()));
        Date maxAllowedRenewalDate = DateUtils.toDateUsingDefaultPattern(
                permitRenewalService.getMaxAllowedDateForRenewalAfterExpiry(permitRenewal.getParent()));
        if (permitRenewal.getApplicationDate().compareTo(minAllowedRenewalDate) >= 0
                && permitRenewal.getApplicationDate().compareTo(permitExpiryDate) <= 0) {
            BigDecimal permitExtensionFee = appConfigUtil.getPermitExtensionFeeInPercentage();
            totalPermitRenewalFee = totalPermitFee.multiply(permitExtensionFee)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            demandReasonCode = "PEF";
        } else if (permitRenewal.getApplicationDate().after(permitExpiryDate)
                && permitRenewal.getApplicationDate().compareTo(maxAllowedRenewalDate) <= 0) {
            BigDecimal permitRenewalFeeInPercent = appConfigUtil.getPermitRenewalFeeInPercentage();
            totalPermitRenewalFee = totalPermitFee.multiply(permitRenewalFeeInPercent)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            demandReasonCode = "PRF";
        }*/
        demandReasonAndAmt.put(demandReasonCode, totalPermitRenewalFee.setScale(0, BigDecimal.ROUND_HALF_UP));
        return demandReasonAndAmt;
    }

    private BigDecimal getPermitFee(final PermitRenewal permitRenewal) {
        BigDecimal totalPermitFee = BigDecimal.ZERO;
        if (!permitRenewal.getParent().getPermitFee().isEmpty()) {
            for (PermitFee permitFee : permitRenewal.getParent().getPermitFee()) {
                for (ApplicationFeeDetail feeDetail : permitFee.getApplicationFee().getApplicationFeeDetail()) {
                    totalPermitFee = totalPermitFee.add(feeDetail.getAmount());
                }
            }
        }
        return totalPermitFee;
    }
}
