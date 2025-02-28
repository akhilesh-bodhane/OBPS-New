/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
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
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
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
 *
 */

package org.egov.bpa.web.controller.rest;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.validation.Valid;

import org.egov.bpa.entitiy.national.dashboard.GroupBy;
import org.egov.bpa.entitiy.national.dashboard.Metrics;
import org.egov.bpa.entitiy.national.dashboard.NationalDashboardResponse;
import org.egov.bpa.entitiy.national.dashboard.SearchCriteria;
import org.egov.bpa.transaction.entity.dto.SearchBpaApplicationForm;
import org.egov.bpa.transaction.service.SearchBpaApplicationService;
import org.egov.bpa.transaction.service.report.NationalDashboardService;
import org.egov.infra.microservice.contract.RequestInfoWrapper;
import org.joda.time.LocalDate;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/national-dashboard")
public class BpaCollectionRestController {
	@Autowired
	SearchBpaApplicationService searchBpaApplicationService;
	
	@Autowired
	NationalDashboardService nationalDashboardService;
	
	@PostMapping("/_test")
	public String create() {
		System.out.println("post rest controller");
		return "test string response";
	}
	
	@GetMapping("/_test")
	public String test() {
		System.out.println("get rest controller");
		return "test string response";
	}
	
//	@GetMapping("/_obpas")
//	public ResponseEntity<NationalDashboardResponse> collection() {
//		SearchBpaApplicationForm  bpaApplicationForm = new SearchBpaApplicationForm();
//		NationalDashboardResponse response = new NationalDashboardResponse();
//		
//		response=nationalDashboardService.getDashboardData(response,bpaApplicationForm);
//		return new ResponseEntity<>(response, HttpStatus.OK);
//		
//	}
//	
	//API :http://ulb.chandigarh.local.org:8080/bpa/national-dashboard/_obpas
	 @PostMapping(value = "/_obpas", produces = MediaType.APPLICATION_JSON_VALUE)
	 public ResponseEntity<?> dashboardData(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,@Valid @ModelAttribute SearchCriteria searchCriteria) {
//		 nationalDashboardService.validateUser(requestInfoWrapper);
		 SearchBpaApplicationForm  bpaApplicationForm = new SearchBpaApplicationForm();
			NationalDashboardResponse response = new NationalDashboardResponse();
			
			Date fromDate = null;
			Date toDate = null;
			if(searchCriteria.getFromDate()!=null)
				try {
					fromDate = new SimpleDateFormat("dd/MM/yyyy").parse(searchCriteria.getFromDate());
				} catch (ParseException e) {
					e.printStackTrace();
				}  
				bpaApplicationForm.setFromDate(fromDate);
				
			if(searchCriteria.getToDate()!=null)
				try {
					toDate = new SimpleDateFormat("dd/MM/yyyy").parse(searchCriteria.getToDate());
				} catch (ParseException e) {
					e.printStackTrace();
				}  
				bpaApplicationForm.setToDate(toDate);
			
			try {
				response=nationalDashboardService.getDashboardData(response,bpaApplicationForm);
				System.out.println("NIUA Dashboard Response : " + response.toString());
				transformPaymentModes(response);
				transformDashboardResponse(response);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
	    }
	 
		private void transformPaymentModes(NationalDashboardResponse response) {
			if (response.getMetrics() != null && response.getMetrics().getTodaysCollection() != null) {
				for (GroupBy collection : response.getMetrics().getTodaysCollection()) {
					if ("paymentMode".equals(collection.getGroupBy())) {
						// Extract existing map-based buckets
						List<Map<String, Object>> originalBuckets = (List<Map<String, Object>>) (List<?>) collection
								.getBuckets();
						if (originalBuckets != null && !originalBuckets.isEmpty()) {
							Map<String, Integer> groupedPayments = new LinkedHashMap<>(); // Maintain order
							groupedPayments.put("Digital", 0); // First entry: Digital
							groupedPayments.put("Non Digital", 0); // Second entry: Non Digital
							for (Map<String, Object> bucket : originalBuckets) {
								String name = bucket.get("name") != null ? bucket.get("name").toString().toLowerCase()
										: "";
								int value = bucket.get("value") != null ? ((Number) bucket.get("value")).intValue() : 0;

								if (Arrays.asList("cash", "cheque/dd", "bankchallan").contains(name)) {
									groupedPayments.put("Non Digital", groupedPayments.get("Non Digital") + value);
								} else if (Arrays.asList("card", "online").contains(name)) {
									groupedPayments.put("Digital", groupedPayments.get("Digital") + value);
								}
							}

							// Create new list of maps
							List<Map<String, Object>> newBuckets = new ArrayList<>();
							for (Map.Entry<String, Integer> entry : groupedPayments.entrySet()) {
								Map<String, Object> newBucket = new HashMap<>();
								newBucket.put("name", entry.getKey());
								newBucket.put("value", entry.getValue());
								newBuckets.add(newBucket);
							}

							// Update collection with transformed data
							collection.setBuckets((List<JSONObject>) (List<?>) newBuckets);
						}
					}
				}
			}
		}
	 
		private void transformDashboardResponse(NationalDashboardResponse response) {
			if (response.getMetrics() != null) {
				Metrics metrics = response.getMetrics();
				// Convert "groupBy" values to camel case
				if (metrics.getTodaysCollection() != null) {
					metrics.setTodaysCollection(metrics.getTodaysCollection().stream().map(group -> {
						group.setGroupBy(toCamelCase(group.getGroupBy()));
						return group;
					}).collect(Collectors.toList()));
				}

				if (metrics.getPermitsIssued() != null) {
					metrics.setPermitsIssued(metrics.getPermitsIssued().stream().map(group -> {
						group.setGroupBy(toCamelCase(group.getGroupBy()));
						return group;
					}).collect(Collectors.toList()));
				}
			}
		}


		private String toCamelCase(String input) {
			if (input == null || input.isEmpty()) {
				return input;
			}
			String[] parts = input.split("(?=[A-Z])"); // Split by uppercase letters
			String result = parts[0].toLowerCase();
			for (int i = 1; i < parts.length; i++) {
				result += parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1).toLowerCase();
			}
			return result;
		}
	 

}