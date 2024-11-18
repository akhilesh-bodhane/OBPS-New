<%--
  ~ eGov suite of products aim to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) <2017>  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  --%>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>

<%-- <div class="panel-heading">
	<div class="panel-title"><spring:message code="title.dashboard" /> </div>
</div> --%>

<div class="panel-body custom">
	<table class="table table-bordered  multiheadertbl"  id="bpaupdatenocdetails">
		<thead>
			<tr>
				<th class="view-content text-center" style="font-size: 97%;"><spring:message
						code="lbl.srl.no" /></th>
				<th class="view-content text-center" style="font-size: 97%;"><spring:message
						code="lbl.service.name" /></th>
				<th class="view-content text-center" style="font-size: 97%;"><spring:message
						code="lbl.application.count" /></th>
				<th class="view-content text-center" style="font-size: 97%;"><spring:message
						code="lbl.application.granted" /></th>
				<th class="view-content text-center" style="font-size: 97%;"><spring:message
						code="lbl.avg.time.taken" /></th>
				<th class="view-content text-center" style="font-size: 97%;"><spring:message
						code="lbl.avg.time.taken.a2k" /></th>
				<th class="view-content text-center" style="font-size: 97%;"><spring:message
						code="lbl.avg.time.taken.b2k" /></th>
				<th class="view-content text-center" style="font-size: 97%;"><spring:message
						code="lbl.max.time.taken" /></th>
				<th class="view-content text-center" style="font-size: 97%;"><spring:message
						code="lbl.min.time.taken" /></th>
				<th class="view-content text-center" style="font-size: 97%;"><spring:message
						code="lbl.median.time.taken" /></th>
				<th class="view-content text-center" style="font-size: 97%;"><spring:message
						code="lbl.total.collection" /></th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td class="view-content text-left" style="font-size: 97%;">1.</td>
				<td class="view-content text-left" style="font-size: 97%;">BPA
					Application</td>
				<td class="view-content text-left" style="font-size: 97%;">9</td>
				<td class="view-content text-left" style="font-size: 97%;">7</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
			</tr>
			<tr>
				<td class="view-content text-left" style="font-size: 97%;">2.</td>
				<td class="view-content text-left" style="font-size: 97%;">Occupancy
					Certificate Application</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
			</tr>
			<tr>
				<td class="view-content text-left" style="font-size: 97%;">3.</td>
				<td class="view-content text-left" style="font-size: 97%;">DPC/Plinth
					Level Certificate Application</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
			</tr>
			<tr>
				<td class="view-content text-left" style="font-size: 97%;">4.</td>
				<td class="view-content text-left" style="font-size: 97%;">Revalidation
					Application</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
				<td class="view-content text-left" style="font-size: 97%;">0</td>
			</tr>
		</tbody>
	</table>
</div>
<input type="hidden" id="applicationNo" value="${bpaApplication.applicationNumber}"/>
<input type="hidden" id="isPermitApplFeeReq" value="${isPermitApplFeeReq}"/>
<input type="hidden" id="permitApplFeeCollected" value="${permitApplFeeCollected}"/>
<input type="hidden" id="nocAppl" value="${nocApplication}"/>
<input type="hidden" id="nocUserExists" value="${nocUserExists}"/>
<input type="hidden" id="citizenOrBusinessUser" value="${citizenOrBusinessUser}"/>
<input type="hidden" id="nocStatusUpdated" value="${nocStatusUpdated}"/>
<script	src="<cdn:url value='/resources/js/app/noc-helper.js?rnd=${app_release_no}'/>"></script>