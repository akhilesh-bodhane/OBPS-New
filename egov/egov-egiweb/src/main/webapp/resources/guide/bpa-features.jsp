<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>
<html>
	<head>
		<link type="text/css" rel="stylesheet" href="/egi/resources/guide/css/bpa-features.css" />
		 <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
	    <!--Import materialize.css-->
	    <link type="text/css" rel="stylesheet" href="/egi/resources/guide/css/materialize.min.css" media="screen,projection" />
	    <link type="text/css" rel="stylesheet" href="/egi/resources/guide/css/landingpage.css" />   
	    <style>
	        .icon-header {display: block !important;}
	    </style>
	    <script type="text/javascript" src="/egi/resources/guide/js/materialize.min.js"></script>
	    <script type="text/javascript">
	        document.addEventListener('DOMContentLoaded', function () {
	            var elems = document.querySelectorAll('.collapsible');
	            var instances = M.Collapsible.init(elems, {
	                inDuration: 0,
	                outDuration: 0,
	                onOpenStart: function (e) {
	                    var row = e.querySelectorAll('.lp-row-1');
	                    row[0].classList.add("row-hide");
	                    var downarrow = e.querySelectorAll('.landingpage-card-down-arrow');
	                    downarrow[0].classList.add("row-hide");
	                    var uparrow = e.querySelectorAll('.landingpage-card-up-arrow');
	                    uparrow[0].classList.remove("row-hide");
	                },
	                onCloseEnd: function (e) {
	                    var row = e.querySelectorAll('.lp-row-1');
	                    row[0].classList.remove("row-hide");
	                    var downarrow = e.querySelectorAll('.landingpage-card-down-arrow');
	                    downarrow[0].classList.remove("row-hide");
	                    var uparrow = e.querySelectorAll('.landingpage-card-up-arrow');
	                    uparrow[0].classList.add("row-hide");
	                }
	            });
	        });
	        
	    </script>   
	</head>
<body>
	<div class="landingpage-font-style">

		<section class="ob-hl-section" aria-labelledby="obHlAbout">
			<div class="ob-hl-head">
				<span class="ob-eyebrow">About</span>
				<h2 id="obHlAbout">About Building Plan Approval System</h2>
			</div>
			<div class="ob-hl-grid ob-hl-grid-5">
				<article class="ob-hl-card">
					<span class="ob-hl-ico"><img src="/egi/resources/guide/assets/ICONS/BPA_1.png" alt=""></span>
					<h3>End to end Process Digitisation</h3>
					<p>Scrutiny reports are produced in the matter of minutes with automatic rule mapping. The development regulation process is also automated saving paperwork and time of stakeholders.</p>
				</article>
				<article class="ob-hl-card">
					<span class="ob-hl-ico"><img src="/egi/resources/guide/assets/ICONS/BPA_2.png" alt=""></span>
					<h3>Single window for multiple services</h3>
					<p>Stakeholders like ULB officials, architects, town planners, engineers and citizens are connected on a single platform to coordinate various services. Stakeholders are updated in real time of the application progress thus achieving transparency.</p>
				</article>
				<article class="ob-hl-card">
					<span class="ob-hl-ico"><img src="/egi/resources/guide/assets/ICONS/BPA_3.png" alt=""></span>
					<h3>Open source smart platform</h3>
					<p>It is a state-of-the-art, ERP-based platform created for urban governance, built on advanced open source technologies ensuring flexibility, interoperability and faster implementation.</p>
				</article>
				<article class="ob-hl-card">
					<span class="ob-hl-ico"><img src="/egi/resources/guide/assets/ICONS/BPA_4.png" alt=""></span>
					<h3>Achieving Transparency</h3>
					<p>Existing manual processes are completely replaced with automated workflows. Standardisation of processes with well-defined roles and responsibilities of stakeholders mapped in the system. MIS reports are automatically generated to help track process efficiency.</p>
				</article>
				<article class="ob-hl-card">
					<span class="ob-hl-ico"><img src="/egi/resources/guide/assets/ICONS/BPA_5.png" alt=""></span>
					<h3>Seamless Application Processes</h3>
					<p>Earlier processes which were tedious have been replaced with a seamless submission process for Building Plan Licensing systems through auto synchronisation of data between DIGIT DCR and BPA application.</p>
				</article>
			</div>
		</section>

		<section class="ob-hl-section" aria-labelledby="obHlCitizen">
			<div class="ob-hl-head">
				<span class="ob-eyebrow">For applicants</span>
				<h2 id="obHlCitizen">Citizen &amp; Building Licensee</h2>
			</div>
			<div class="ob-hl-grid ob-hl-grid-3">
				<article class="ob-hl-card">
					<span class="ob-hl-ico"><img src="/egi/resources/guide/assets/ICONS/licensee_1.png" alt=""></span>
					<h3>Online payment</h3>
					<p>All payments related to building plan application can be made online using debit/credit card/internet banking/etc. anytime.</p>
				</article>
				<article class="ob-hl-card">
					<span class="ob-hl-ico"><img src="/egi/resources/guide/assets/ICONS/citizen_1.png" alt=""></span>
					<h3>Track Application</h3>
					<p>The citizen and building licensee will receive real time updates related to submitted application via SMS and email notifications.</p>
				</article>
				<article class="ob-hl-card">
					<span class="ob-hl-ico"><img src="/egi/resources/guide/assets/ICONS/citizen_2.png" alt=""></span>
					<h3>Receive alert and notification</h3>
					<p>Centralized platform for all stakeholders including citizens, business users, and department officials.</p>
				</article>
			</div>
		</section>

		<%-- <div class="card landingpage-card">
			<ul class="collapsible card-collapse">
				<li style="padding-left: 40px; padding-right: 40px;">
					<div class="panel-heading">
					<div class="panel-title">
						<spring:message code="title.dashboard" />
					</div>
				</div>
					<div
						class="card-content collapsible-header landingpage-card-content"
						tabindex="0">
						<span class="card-title card-header">Dashboard</span>
					</div>


					<div class="panel-body custom">
						<!-- <div class="row collapsible-header collapsible-body card-body"
							tabindex="0" style=""> -->
							<div
								class="row lp-row-2 collapsible-header landingpage-card-content"
								tabindex="0">
								<table class="table table-bordered  multiheadertbl"
									id="citizenDashboardCount">
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
													code="lbl.total.collection" /></th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td class="view-content text-left" style="font-size: 97%;">1.</td>
											<td class="view-content text-left" style="font-size: 97%;">BPA
												Application</td>
											<td class="view-content text-left" style="font-size: 97%;">711</td>
											<td class="view-content text-left" style="font-size: 97%;">243</td>
											<td class="view-content text-left" style="font-size: 97%;">3</td>
											<td class="view-content text-left" style="font-size: 97%;">2</td>
											<td class="view-content text-left" style="font-size: 97%;">2</td>										
											<td class="view-content text-left" style="font-size: 97%;">1,97,373,073</td>
										</tr>
										<tr>
											<td class="view-content text-left" style="font-size: 97%;">2.</td>
											<td class="view-content text-left" style="font-size: 97%;">Occupancy
												Certificate Application</td>
											<td class="view-content text-left" style="font-size: 97%;">126</td>
											<td class="view-content text-left" style="font-size: 97%;">78</td>
											<td class="view-content text-left" style="font-size: 97%;">2</td>
											<td class="view-content text-left" style="font-size: 97%;">1</td>
											<td class="view-content text-left" style="font-size: 97%;">2</td>
											<td class="view-content text-left" style="font-size: 97%;">622,581</td>
										</tr>
										<tr>
											<td class="view-content text-left" style="font-size: 97%;">3.</td>
											<td class="view-content text-left" style="font-size: 97%;">DPC/Plinth
												Level Certificate Application</td>
											<td class="view-content text-left" style="font-size: 97%;">35</td>
											<td class="view-content text-left" style="font-size: 97%;">17</td>
											<td class="view-content text-left" style="font-size: 97%;">2</td>
											<td class="view-content text-left" style="font-size: 97%;">NA</td>
											<td class="view-content text-left" style="font-size: 97%;">NA</td>
											<td class="view-content text-left" style="font-size: 97%;">NA</td>
										</tr>
										<tr>
											<td class="view-content text-left" style="font-size: 97%;">4.</td>
											<td class="view-content text-left" style="font-size: 97%;">Revalidation
												Application</td>
											<td class="view-content text-left" style="font-size: 97%;">56</td>
											<td class="view-content text-left" style="font-size: 97%;">7</td>
											<td class="view-content text-left" style="font-size: 97%;">3</td>
											<td class="view-content text-left" style="font-size: 97%;">NA</td>
											<td class="view-content text-left" style="font-size: 97%;">NA</td>
											<td class="view-content text-left" style="font-size: 97%;">61,000</td>
										</tr>
									</tbody>
								</table>
							</div>
						<!-- </div> -->
					</div>
					<!-- <div
						class="row collapsible-header card-action card-collapse-header"
						tabindex="0">
						<i class="material-icons landingpage-card-down-arrow-2">keyboard_arrow_down</i>
						<i class="material-icons landingpage-card-up-arrow-2 row-hide">keyboard_arrow_up</i>
					</div> -->
				</li>
			</ul>
		</div> --%>

	</div>
		
	<footer class = "f-regular bpa-login-footer page-common-padding col-md-12 color-black">
		<!-- <div class = "footer-list-content col-md-4">
			<div style = "margin-bottom: 16px;" class = "color-black bpa-common-header2 bpa-common-header">About OBPS</div>
			<div style = "opacity: 0.7;" class = "color-black bpa-common-text">Citizens, Officials, and other stakeholders can submit and track applications in real time and obtain approvals without having to physically visit an office.</div>
		</div> -->
		<div class = "color-black footer-list-content col-md-2">
			<div style="margin-bottom: 16px;" class="color-black bpa-common-header2 bpa-common-header">Help Resources</div>
			<ul class="tutorial-list color-black" >
				
				<li class="bpa-common-text tutorial-item">
					<a href="/egi/resources/guide/bpaHelpDocumentECBC.jsp" target="_blank"><span class = "color-black bpa-common-text">CHECBC and CHECBCR - 2024</span></a>
				</li>
				<li class="bpa-common-text tutorial-item">
					<a href="/egi/resources/guide/assets/docs/User Manual for Architects.pdf" target="_blank"><span class = "color-black bpa-common-text">User Manual for Architects</span></a>
				</li>
				<li class="bpa-common-text tutorial-item">
					<a href="/egi/resources/guide/assets/docs/User Manual for Citizens.pdf" target="_blank"><span class = "color-black bpa-common-text">User Manual for Citizens</span></a>
				</li>
				<li class="bpa-common-text tutorial-item">
					<a href="/egi/resources/guide/assets/docs/Drawing Manual for Architects.pdf" target="_blank"><span class = "color-black bpa-common-text">Drawing Manual for Architects</span></a>
				</li>
				<li class="bpa-common-text tutorial-item">
					<a href="/egi/resources/guide/assets/docs/Rural User Manual.pdf" target="_blank"><span class = "color-black bpa-common-text">Rural User Manual</span></a>
				</li>
				<li class="bpa-common-text tutorial-item">
					<a href="/egi/resources/guide/bpaHelpDocument.jsp" target="_blank"><span class = "color-black bpa-common-text">Training Videos</span></a>
				</li>
				<li class="bpa-common-text tutorial-item">
					<a href="/egi/resources/guide/bpaHelpDocumentDXF.jsp" target="_blank"><span class = "color-black bpa-common-text">Training Drawing</span></a>
				</li>
			</ul>
		</div>
		<div class="footer-list-content ob-foot-about">
			<div class="ob-foot-brand"><img src="/egi/resources/global/images/logo.png" alt="Chandigarh Administration"><span><strong>OBPAS</strong><em>Chandigarh Administration</em></span></div>
			<p>Single window portal for online scrutiny and approval of building plan applications within the municipal limits of Chandigarh.</p>
		</div>
		<div class = "footer-list-content col-md-4">
			<div style = "margin-bottom: 16px;" class = "color-black bpa-common-header2 bpa-common-header">Get in Touch</div>
			<div class = "footer-contact-info-cont">
				<div class = "color-black bpa-common-text">Estate Office, SDO Building,
Town hall Building Sector 17
UT Chandigarh</div>
				<div class = "footer-icon-info-cont" style="display:none;">
					<div class = "footer-icon">
						<i class="fa fa-map-marker fa-1x"></i>
					</div>
					<div class = "footer-info">
					<a href="javascript:void(0);" target="_blank">Find us on map</a>
					</div>
				</div>
				<div class = "footer-icon-info-cont">
					<div class = "footer-icon">
						<i class="fa fa-envelope-square fa-1x"></i>
					</div>
					<div class = "footer-info">
						<a href="javascript:void(0);">obps.chd.helpdesk@gmail.com</a>
					</div>
				</div>
				<div class = "footer-icon-info-cont">
					<div class = "footer-icon">
						<i class="fa fa-phone fa-1x"></i>
					</div>
					<div class = "footer-info">
						<a href="javascript:void(0);">0172-2787200</a>
					</div>
				</div>
				
				<div class = "footer-icon-info-cont" style="display:none;">
					<div class = "footer-icon">
						<i class="fa fa-envelope fa-1x"></i>
					</div>
					<div class = "footer-info">
						<a href="mailto:smartcity.chd@nic.in"><p style = "margin: 0">smartcity.chd@nic.in</p></a>
					</div>
				</div>
				
				<div class = "footer-icon-info-cont" style="display:none;">
					<div class = "footer-icon">
						<a href="javascript:void(0);" target="_blank" class="media-link"><i class="fa fa-facebook fa-1x"></i></a>
					</div>
					<div style = "margin-left: 5px;" class = "footer-info">
						<a href="javascript:void(0);" target="_blank" class="media-link"><i class="fa fa-twitter fa-1x"></i></a>
					</div>
				</div>
			</div>
		</div>
		<div class="ob-foot-bottom"><span>&copy; Chandigarh Administration</span><span>Online Building Plan Approval System</span></div>
	</footer>
	<script type="text/javascript">
		/* Highlights: cards show icon + title; clicking one reveals its description below the row (click again to close). */
		(function () {
			function init() {
				var sections = document.querySelectorAll('.ob-hl-section');
				Array.prototype.forEach.call(sections, function (sec) {
					sec.classList.add('ob-hl-js');
					var grid = sec.querySelector('.ob-hl-grid');
					var panel = document.createElement('div');
					panel.className = 'ob-hl-detail';
					panel.hidden = true;
					panel.setAttribute('aria-live', 'polite');
					grid.parentNode.insertBefore(panel, grid.nextSibling);
					var cards = grid.querySelectorAll('.ob-hl-card');
					function select(card) {
						var open = card.classList.contains('is-active');
						Array.prototype.forEach.call(cards, function (c) { c.classList.remove('is-active'); c.setAttribute('aria-expanded', 'false'); });
						if (open) { panel.hidden = true; return; }
						card.classList.add('is-active');
						card.setAttribute('aria-expanded', 'true');
						panel.innerHTML = '';
						var h = document.createElement('h3'); h.textContent = card.querySelector('h3').textContent;
						var p = document.createElement('p'); p.textContent = card.querySelector('p').textContent;
						panel.appendChild(h); panel.appendChild(p);
						panel.hidden = false;
					}
					Array.prototype.forEach.call(cards, function (card) {
						card.setAttribute('tabindex', '0');
						card.setAttribute('role', 'button');
						card.setAttribute('aria-expanded', 'false');
						card.addEventListener('click', function () { select(card); });
						card.addEventListener('keydown', function (e) { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); select(card); } });
					});
				});
			}
			if (document.readyState !== 'loading') { init(); } else { document.addEventListener('DOMContentLoaded', init); }
		})();
	</script>
</body>
</html>