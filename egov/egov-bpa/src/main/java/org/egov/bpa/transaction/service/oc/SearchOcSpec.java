/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2017>  eGovernments Foundation
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
package org.egov.bpa.transaction.service.oc;

import static org.egov.bpa.utils.BpaConstants.APPLICATION_STATUS_APPROVED;
import static org.egov.bpa.utils.BpaConstants.APPLICATION_STATUS_REGISTERED;
import static org.egov.bpa.utils.BpaConstants.APPLICATION_STATUS_SUBMITTED;

import org.egov.bpa.transaction.entity.BpaApplication;
import org.egov.bpa.transaction.entity.SiteDetail;
import org.egov.bpa.transaction.entity.Slot;
import org.egov.bpa.transaction.entity.SlotDetail;
import org.egov.bpa.transaction.entity.dto.SearchBpaApplicationForm;
import org.egov.bpa.transaction.entity.dto.SearchPendingItemsForm;
import org.egov.bpa.transaction.entity.oc.OCSlot;
import org.egov.bpa.transaction.entity.oc.OccupancyCertificate;
import org.egov.demand.model.EgDemand;
import org.egov.infra.admin.master.entity.Boundary;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.Date;

public class SearchOcSpec {
	final private static Long RURAL_APPLICATION_NUMBER = 4L;
	final private static String OC_END_STATE = "Order Issued to Applicant";
	final private static String CANCELLED = "Cancelled";
	private static final String STATUS = "status";
    public static Specification<OccupancyCertificate> search(final SearchBpaApplicationForm requestForm) {
        return (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            Join<OccupancyCertificate, BpaApplication> bpaApplication = root.join("parent");
            Join<BpaApplication, SiteDetail> siteDetailJoin = bpaApplication.join("siteDetail");
            Join<SiteDetail, Boundary> adminBoundaryJoin = siteDetailJoin.join("adminBoundary");
            if (requestForm.getApplicationNumber() != null)
                predicates.add(builder.equal(root.get("applicationNumber"), requestForm.getApplicationNumber()));
            if (requestForm.getApplicantName() != null)
                predicates.add(builder.equal(bpaApplication.get("owner").get("name"),
                                requestForm.getApplicantName()));
            if(requestForm.getStatusId() !=null)
            	predicates.add(builder.equal(root.get("status").get("id"), requestForm.getStatusId()));
            
            if (requestForm.getServiceTypeId() != null)
                predicates.add(builder.equal(bpaApplication.get("serviceType").get("id"), requestForm.getServiceTypeId()));
            if (requestForm.getServiceType() != null)
                predicates.add(builder.equal(bpaApplication.get("serviceType").get("description"),
                                requestForm.getServiceType()));

            if (requestForm.getOccupancyId() != null)
                predicates.add(builder.equal(bpaApplication.get("occupancy").get("id"), requestForm.getOccupancyId()));
            if(requestForm.getRevenueBoundary()!=null)
                predicates.add(builder.equal(siteDetailJoin.get("adminBoundary").get("id"), requestForm.getRevenueBoundary()));
            if(requestForm.getAdminBoundary()!=null)
                predicates.add(builder.equal(siteDetailJoin.get("electionBoundary").get("id"), requestForm.getAdminBoundary()));
            if(requestForm.getLocationBoundary()!=null)
                predicates.add(builder.equal(siteDetailJoin.get("locationBoundary").get("id"), requestForm.getLocationBoundary()));
            
            if (requestForm.getFromDate() != null)
                predicates.add(builder.greaterThanOrEqualTo(root.get("applicationDate"), requestForm.getFromDate()));
            if (requestForm.getToDate() != null)
                predicates.add(builder.lessThanOrEqualTo(root.get("applicationDate"), requestForm.getToDate()));
            if (requestForm.getFromBuiltUpArea() != null)
                predicates.add(builder.greaterThanOrEqualTo(bpaApplication.get("totalBuiltUpArea"), requestForm.getFromBuiltUpArea()));
            if (requestForm.getToBuiltUpArea() != null)
                predicates.add(builder.lessThanOrEqualTo(bpaApplication.get("totalBuiltUpArea"), requestForm.getToBuiltUpArea()));
            query.distinct(true);
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    public static Specification<OccupancyCertificate> searchPendingOCTasks(SearchPendingItemsForm requestForm) {
    	return (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            commonSpecForOCSearch(requestForm, root, builder, predicates);
            Join<OccupancyCertificate, BpaApplication> bpaApplication = root.join("parent");
            
            //to differential Urban and Rural applications
            if (requestForm.getApplicationTypeId() != null)
                predicates.add(builder.equal(bpaApplication.get("applicationType").get("id"), requestForm.getApplicationTypeId()));
            else
            	predicates.add(builder.notEqual(bpaApplication.get("applicationType").get("id"), RURAL_APPLICATION_NUMBER));
    
            if (requestForm.getStatusId() != null)
                predicates.add(builder.equal(root.get("status").get("id"), requestForm.getStatusId()));
            
            else
            	predicates.add(builder.not(root.get("status").get("code").in(OC_END_STATE,CANCELLED)));
            
            query.distinct(true);
            return builder.and(predicates.toArray(new Predicate[0]));
        };
	}
    
    public static Specification<OccupancyCertificate> searchPendingOCTasksRural(SearchPendingItemsForm requestForm) {
    	return (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            commonSpecForOCSearch(requestForm, root, builder, predicates);
            Join<OccupancyCertificate, BpaApplication> bpaApplication = root.join("parent");
            
            //to differential Urban and Rural applications
            if (requestForm.getApplicationTypeId() != null)
                predicates.add(builder.equal(bpaApplication.get("applicationType").get("id"), requestForm.getApplicationTypeId()));
            else
            	predicates.add(builder.equal(bpaApplication.get("applicationType").get("id"), RURAL_APPLICATION_NUMBER));
    
            if (requestForm.getStatusId() != null)
                predicates.add(builder.equal(root.get("status").get("id"), requestForm.getStatusId()));
            
            else
            	predicates.add(builder.not(root.get("status").get("code").in(OC_END_STATE,CANCELLED)));
            
            query.distinct(true);
            return builder.and(predicates.toArray(new Predicate[0]));
        };
	}
    public static Specification<OccupancyCertificate> searchOCTasksForUrban(SearchPendingItemsForm requestForm) {
    	return (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            commonSpecForOCSearch(requestForm, root, builder, predicates);
            Join<OccupancyCertificate, BpaApplication> bpaApplication = root.join("parent");
            
            //to differential Urban and Rural applications
            if (requestForm.getApplicationTypeId() != null)
                predicates.add(builder.equal(bpaApplication.get("applicationType").get("id"), requestForm.getApplicationTypeId()));
            else
            	predicates.add(builder.notEqual(bpaApplication.get("applicationType").get("id"), RURAL_APPLICATION_NUMBER));
    
            if (requestForm.getStatusId() != null)
                predicates.add(builder.equal(root.get("status").get("id"), requestForm.getStatusId()));
            
            query.distinct(true);
            return builder.and(predicates.toArray(new Predicate[0]));
        };
	}
    
    public static Specification<OccupancyCertificate> searchOCTasksForRural(SearchPendingItemsForm requestForm) {
    	return (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            commonSpecForOCSearch(requestForm, root, builder, predicates);
            Join<OccupancyCertificate, BpaApplication> bpaApplication = root.join("parent");
            //to differential Urban and Rural applications
            if (requestForm.getApplicationTypeId() != null)
                predicates.add(builder.equal(bpaApplication.get("applicationType").get("id"), requestForm.getApplicationTypeId()));
            else
            	predicates.add(builder.equal(bpaApplication.get("applicationType").get("id"), RURAL_APPLICATION_NUMBER));
            
            if (requestForm.getStatusId() != null)
                predicates.add(builder.equal(root.get("status").get("id"), requestForm.getStatusId()));
            
            query.distinct(true);
            return builder.and(predicates.toArray(new Predicate[0]));
        };
	}
    
    private static void commonSpecForOCSearch(SearchPendingItemsForm requestForm, Root<OccupancyCertificate> root,
			CriteriaBuilder builder, List<Predicate> predicates) {
        Join<OccupancyCertificate, BpaApplication> bpaApplication = root.join("parent");
//      Join<BpaApplication, SiteDetail> siteDetailJoin = bpaApplication.join("siteDetail");
//      Join<SiteDetail, Boundary> adminBoundaryJoin = siteDetailJoin.join("adminBoundary");
      if (requestForm.getApplicationNumber() != null)
          predicates.add(builder.like(root.get("applicationNumber"), requestForm.getApplicationNumber()+"%"));
      if (requestForm.getApplicantName() != null)
          predicates.add(builder.like(bpaApplication.get("owner").get("name"),
                          requestForm.getApplicantName()+"%"));
//      if(requestForm.getStatusId() !=null)
//      	predicate.getExpressions()
//          .add(builder.equal(root.get("status").get("id"), requestForm.getStatusId()));
      
      if (requestForm.getServiceTypeId() != null)
          predicates.add(builder.equal(bpaApplication.get("serviceType").get("id"), requestForm.getServiceTypeId()));
      if (requestForm.getServiceType() != null)
          predicates.add(builder.equal(bpaApplication.get("serviceType").get("description"),
                          requestForm.getServiceType()));

      
      if (requestForm.getFromDate() != null)
          predicates.add(builder.greaterThanOrEqualTo(root.get("applicationDate"), requestForm.getFromDate()));
      if (requestForm.getToDate() != null)
          predicates.add(builder.lessThanOrEqualTo(root.get("applicationDate"), requestForm.getToDate()));
      
      if (requestForm.getPlotNumber() != null)
          predicates.add(builder.equal(bpaApplication.get("plotNumber"), requestForm.getPlotNumber()));
      
      if (requestForm.getSector() != null)
          predicates.add(builder.equal(bpaApplication.get("sector"), requestForm.getSector()));
      // the task lists never show an OC without a workflow state; excluding it here keeps the total count honest
      predicates.add(builder.isNotNull(root.get("state")));

	}

    private static void commonSpecForOCSearchDemand(SearchBpaApplicationForm requestForm, Root<OccupancyCertificate> root,
			CriteriaBuilder builder, List<Predicate> predicates) {
        Join<OccupancyCertificate, BpaApplication> bpaApplication = root.join("parent");
//      Join<BpaApplication, SiteDetail> siteDetailJoin = bpaApplication.join("siteDetail");
//      Join<SiteDetail, Boundary> adminBoundaryJoin = siteDetailJoin.join("adminBoundary");
      if (requestForm.getApplicationNumber() != null)
          predicates.add(builder.like(root.get("applicationNumber"), requestForm.getApplicationNumber()+"%"));
      if (requestForm.getApplicantName() != null)
          predicates.add(builder.like(bpaApplication.get("owner").get("name"),
                          requestForm.getApplicantName()+"%"));
//      if(requestForm.getStatusId() !=null)
//      	predicate.getExpressions()
//          .add(builder.equal(root.get("status").get("id"), requestForm.getStatusId()));
      
      if (requestForm.getServiceTypeId() != null)
          predicates.add(builder.equal(bpaApplication.get("serviceType").get("id"), requestForm.getServiceTypeId()));
      if (requestForm.getServiceType() != null)
          predicates.add(builder.equal(bpaApplication.get("serviceType").get("description"),
                          requestForm.getServiceType()));

      
      if (requestForm.getFromDate() != null)
          predicates.add(builder.greaterThanOrEqualTo(root.get("applicationDate"), requestForm.getFromDate()));
      if (requestForm.getToDate() != null)
          predicates.add(builder.lessThanOrEqualTo(root.get("applicationDate"), requestForm.getToDate()));
      
      if (requestForm.getPlotNumber() != null)
          predicates.add(builder.equal(bpaApplication.get("plotNumber"), requestForm.getPlotNumber()));
      
      if (requestForm.getSector() != null)
          predicates.add(builder.equal(bpaApplication.get("sector"), requestForm.getSector()));
		
	}

	public static Specification<OCSlot> hasDocumentScrutinyPendingSpecificationForOc(final SearchBpaApplicationForm requestForm) {
        return (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            Join<OCSlot, OccupancyCertificate> certificateJoin = root.join("oc");
            Join<OccupancyCertificate, BpaApplication> applicationJoin = certificateJoin.join("parent");
            Join<BpaApplication, SiteDetail> siteDetailJoin = applicationJoin.join("siteDetail");
            if (requestForm.getToDate() != null) {
                Join<OCSlot, SlotDetail> slotDetailJoin = root.join("slotDetail");
                Join<SlotDetail, Slot> slotJoin = slotDetailJoin.join("slot");
                predicates.add(builder.equal(slotJoin.get("appointmentDate"), requestForm.getToDate()));
            }
            Join<SiteDetail, Boundary> adminBoundaryJoin = siteDetailJoin.join("adminBoundary");
            if (requestForm.getElectionWardId() != null)
                predicates.add(builder.equal(siteDetailJoin.get("electionBoundary").get("id"), requestForm.getElectionWardId()));
            if (requestForm.getWardId() != null)
                predicates.add(builder.equal(adminBoundaryJoin.get("id"), requestForm.getWardId()));
            if (requestForm.getZoneId() != null) {
                predicates.add(builder.equal(adminBoundaryJoin.get("parent").get("id"), requestForm.getZoneId()));
            }
            if (requestForm.getZoneId() == null && requestForm.getZone() != null)
                predicates.add(builder.equal(adminBoundaryJoin.get("parent").get("name"), requestForm.getZone()));
            predicates.add(certificateJoin.get("status").get("code").in("Scheduled For Document Scrutiny",
                            "Rescheduled For Document Scrutiny"));
            predicates.add(builder.equal(root.get("isActive"), true));
            query.distinct(true);
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

	public static Specification<OccupancyCertificate> searchServiceWiseStatus(SearchBpaApplicationForm requestForm) {
		 return (root, query, builder) -> {
	            final List<Predicate> predicates = new ArrayList<>();
	            Join<OccupancyCertificate, BpaApplication> bpaApplication = root.join("parent");
	            Join<BpaApplication, SiteDetail> siteDetailJoin = bpaApplication.join("siteDetail");
	            Join<SiteDetail, Boundary> adminBoundaryJoin = siteDetailJoin.join("adminBoundary");
	            if (requestForm.getApplicationNumber() != null)
	                predicates.add(builder.equal(root.get("applicationNumber"), requestForm.getApplicationNumber()));
	            if (requestForm.getApplicantName() != null)
	                predicates.add(builder.equal(bpaApplication.get("owner").get("name"),
	                                requestForm.getApplicantName()));
	            if(requestForm.getStatusId() !=null)
	            	predicates.add(builder.equal(root.get("status").get("id"), requestForm.getStatusId()));
	            if(requestForm.getStatus() !=null)
	            	predicates.add(builder.equal(root.get("status").get("code"), requestForm.getStatus()));
	            
	            if (requestForm.getServiceTypeId() != null)
	                predicates.add(builder.equal(bpaApplication.get("serviceType").get("id"), requestForm.getServiceTypeId()));
	            if (requestForm.getServiceType() != null)
	                predicates.add(builder.equal(bpaApplication.get("serviceType").get("description"),
	                                requestForm.getServiceType()));

	            if (requestForm.getOccupancyId() != null)
	                predicates.add(builder.equal(bpaApplication.get("occupancy").get("id"), requestForm.getOccupancyId()));
	            if(requestForm.getRevenueBoundary()!=null)
	                predicates.add(builder.equal(siteDetailJoin.get("adminBoundary").get("id"), requestForm.getRevenueBoundary()));
	            if(requestForm.getAdminBoundary()!=null)
	                predicates.add(builder.equal(siteDetailJoin.get("electionBoundary").get("id"), requestForm.getAdminBoundary()));
	            if(requestForm.getLocationBoundary()!=null)
	                predicates.add(builder.equal(siteDetailJoin.get("locationBoundary").get("id"), requestForm.getLocationBoundary()));
	            
	            if (requestForm.getFromDate() != null)
	                predicates.add(builder.greaterThanOrEqualTo(root.get("applicationDate"), requestForm.getFromDate()));
	            if (requestForm.getToDate() != null)
	                predicates.add(builder.lessThanOrEqualTo(root.get("applicationDate"), requestForm.getToDate()));
	            if (requestForm.getFromBuiltUpArea() != null)
	                predicates.add(builder.greaterThanOrEqualTo(bpaApplication.get("totalBuiltUpArea"), requestForm.getFromBuiltUpArea()));
	            if (requestForm.getToBuiltUpArea() != null)
	                predicates.add(builder.lessThanOrEqualTo(bpaApplication.get("totalBuiltUpArea"), requestForm.getToBuiltUpArea()));
	            
	            if (requestForm.getPlotNumber() != null)
	                predicates.add(builder.equal(bpaApplication.get("plotNumber"), requestForm.getPlotNumber()));
	            
	            //to differential Urban and Rural applications
	            if (requestForm.getApplicationTypeId() != null)
	                predicates.add(builder.equal(bpaApplication.get("applicationType").get("id"), requestForm.getApplicationTypeId()));
	            
	            if (requestForm.getSector() != null)
	                predicates.add(builder.equal(bpaApplication.get("sector"), requestForm.getSector()));
	            
	            
	            query.distinct(true);
	            return builder.and(predicates.toArray(new Predicate[0]));
	        };
	}
	
	public static Specification<OccupancyCertificate> hasCollectionPendingSpecification(final SearchBpaApplicationForm requestForm) {
        return (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            predicates.add(root.get(STATUS).get("code").in(APPLICATION_STATUS_SUBMITTED, APPLICATION_STATUS_REGISTERED,
                            APPLICATION_STATUS_APPROVED));
            Join<OccupancyCertificate, EgDemand> demandJoin = root.join("demand");
            predicates.add(builder.lessThan(demandJoin.get("amtCollected"), demandJoin.get("baseDemand")));
            commonSpecForOCSearchDemand(requestForm, root, builder, predicates);
            //siteDetailSpec(requestForm, root, builder, predicates);
            query.distinct(true);
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
