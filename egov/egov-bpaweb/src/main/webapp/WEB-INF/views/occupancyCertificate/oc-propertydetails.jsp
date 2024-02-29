<%--
  ~    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) 2017  eGovernments Foundation
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
  ~            Further, all user interfaces, including but not limited to citizen facing interfaces,
  ~            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
  ~            derived works should carry eGovernments Foundation logo on the top right corner.
  ~
  ~            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
  ~            For any further queries on attribution, including queries on brand guidelines,
  ~            please contact contact@egovernments.org
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
  ~
  --%>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>

<div class="panel-heading toggle-header custom_form_panel_heading">
	<div class="panel-title">
		<spring:message code="lbl.property.details" />
	</div>
	<div class="history-icon toggle-icon">
		<i class="fa fa-angle-up fa-2x"></i>
	</div>
</div>
<div class="panel-body display-hide">
	<div class="form-group">
		<label class="col-sm-3 control-label text-right"><spring:message
				code="lbl.file.type" /></label>
		<div class="col-sm-3 add-margin">
			<form:select name="propertyFileType" id="filetype"
				path="" cssClass="form-control"
				cssErrorClass="form-control error">
				<option value=""><spring:message code="lbl.select" /></option>
				<option value="RP">RP</option>
				<option value="AH">AH</option>
				<option value="BL">BL</option>
				<option value="CP">CP</option>
				<option value="CPL">CPL</option>
				<option value="FVL">FVL</option>
				<option value="FW">FW</option>
				<option value="INCL">INCL</option>
				<option value="IND">IND</option>
				<option value="INDL">INDL</option>
				<option value="LPGBPCL">LPGBPCL</option>
				<option value="LPGHPCL">LPGHPCL</option>
				<option value="LPGIOCL">LPGIOCL</option>
				<option value="LRP">LRP</option>
				<option value="M">M</option>
				<option value="M1">M1</option>
				<option value="M10">M10</option>
				<option value="M1004">M1004</option>
				<option value="M1025">M1025</option>
				<option value="M12">M12</option>
				<option value="M13">M13</option>
				<option value="M14">M14</option>
				<option value="M15">M15</option>
				<option value="M17">M17</option>
				<option value="M18">M18</option>
				<option value="M19">M19</option>
				<option value="M2">M2</option>
				<option value="M20">M20</option>
				<option value="M21">M21</option>
				<option value="M22">M22</option>
				<option value="M27">M27</option>
				<option value="M27A">M27A</option>
				<option value="M3">M3</option>
				<option value="M4">M4</option>
				<option value="M571">M571</option>
				<option value="M6">M6</option>
				<option value="M751">M751</option>
				<option value="M787">M787</option>
				<option value="M8">M8</option>
				<option value="M815">M815</option>
				<option value="M828">M828</option>
				<option value="M83">M83</option>
				<option value="M840">M840</option>
				<option value="M841">M841</option>
				<option value="M866">M866</option>
				<option value="M881">M881</option>
				<option value="M882">M882</option>
				<option value="M885">M885</option>
				<option value="M9">M9</option>
				<option value="M968">M968</option>
				<option value="M976">M976</option>
				<option value="MMKT">MMKT</option>
				<option value="PPBPCL">PPBPCL</option>
				<option value="PPCITCO">PPCITCO</option>
				<option value="PPHPCL">PPHPCL</option>
				<option value="PPIOCL">PPIOCL</option>
				<option value="RBL">RBL</option>
				<option value="RC">RC</option>
				<option value="RCE">RCE</option>
				<option value="RDP">RDP</option>
				<option value="RPD">RPD</option>
				<option value="RPL">RPL</option>
				<option value="SB">SB</option>
				<option value="SMS">SMS</option>
				<option value="SNM">SNM</option>
				<option value="TAL">TAL</option>
				<option value="TF">TF</option>
			</form:select>
		</div>
		<label class="col-sm-2 control-label text-right"><spring:message
				code="lbl.file.number" /></label>
		<div class="col-sm-3 add-margin">
			<form:select name="propertyFileNumber" id="FileNumber"
				path="" cssClass="form-control"
				cssErrorClass="form-control error">
				<%-- <option value=""><spring:message code="lbl.select" /></option>
				<option value="1000">1000</option> --%>
			</form:select>
		</div>
	</div>
	                                             
	<div class="form-group">
		<label class="col-sm-3 control-label text-right"><spring:message
				code="lbl.plot.no" /></label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control patternvalidation" name="propertyPlotNumber" maxlength="20"
				data-pattern="alphanumericspecialcharacters"  id="plotno"
				path=""/>	
			<form:errors path=""
				cssClass="add-margin error-msg" />
		</div>
		<label class="col-sm-2 control-label text-right"><spring:message
				code="lbl.sector.no" /></label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control patternvalidation" name="propertySectorNumber" maxlength="20"
				data-pattern="alphanumericspecialcharacters"  id="sectorno"
				path=""/>
			<form:errors path=""
				cssClass="add-margin error-msg"/>
		</div>
			<input type="hidden" id="jsonData" name="jsonData"/> 
	</div>
	<%-- <div class="form-group">
		<label class="col-sm-3 control-label text-right"><spring:message
				code="lbl.category.property" /><span class="mandatory"></span></label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control patternvalidation" maxlength="20"
				data-pattern="alphanumericspecialcharacters"  id="categoryproperty"
				path="" required="required" />	
			<form:errors path=""
				cssClass="add-margin error-msg" />
		</div>
		<label class="col-sm-2 control-label text-right"><spring:message
				code="lbl.tenure.Type" /><span class="mandatory"></span> </label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control patternvalidation" maxlength="20"
				data-pattern="alphanumericspecialcharacters"  id="tenuretype"
				path="" required="required" />
			<form:errors path=""
				cssClass="add-margin error-msg"/>
		</div>
	</div>
		 --%><%-- <div class="form-group">
		<label class="col-sm-3 control-label text-right"><spring:message
				code="lbl.name.owner" /><span class="mandatory"></span></label>
		<div class="col-sm-3 add-margin">
			<form:input class="form-control patternvalidation" maxlength="20"
				data-pattern="alphanumericspecialcharacters"  id="ownername"
				path="" required="required" />	
			<form:errors path=""
				cssClass="add-margin error-msg" />
		</div>
	</div> --%>
		
</div>
<jsp:include page="oc-propertyownerdetails.jsp"></jsp:include>

<script src="<cdn:url value='/resources/js/app/bpa-ajax-helper.js?rnd=${app_release_no}'/>"></script>