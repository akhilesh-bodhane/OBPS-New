package org.egov.bpa.master.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

import org.egov.bpa.transaction.entity.enums.NocIntegrationType;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.hibernate.validator.constraints.Length;
@Entity
@Table(name = "EGBPA_MSTR_NOCCONFIG")
@SequenceGenerator(name = NocConfig.SEQ, sequenceName = NocConfig.SEQ, allocationSize = 1)
public class NocConfig extends AbstractAuditable {
	public static final String SEQ = "SEQ_EGBPA_MSTR_NOCCONF";
	@Id
	@GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
	private Long id;
	 
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)	 
	@JoinColumn(name = "nocDepartment", nullable = false)
	private NocDepartment nocDepartment;
	@Enumerated(EnumType.STRING)
	@Column(name="applicationType")
	
	
	private BpaApplicationType applicationType;
    @Enumerated(EnumType.STRING)
    @Column(name="IntrgrationType")
    
	
	private NocIntegrationType integrationType;
    @Length(min=2, max=1024)
	private String name;
    @Length(min=2, max=1024)
    private String description;
    
   	
    private Boolean isDeemedApproved;
    
    
	/**
	 *  No of Days for deemed approval (sla)
	 */
  	
	@Max(365)
	private Long daysForDeemedApproval;
  	/**
  	 * this field refers to status update. If the NOC dept is pushing the status change then statusByPush is set to else it is false
  	 */
  	@NotNull
  	private Boolean statusByPush;

	@Override
	public Long getId() {
		return this.id;

	}

	@Override
	protected void setId(Long id) {
		this.id = id;

	}

	public NocDepartment getNocDepartment() {
		return nocDepartment;
	}

	public void setNocDepartment(NocDepartment nocDepartment) {
		this.nocDepartment = nocDepartment;
	}

	public BpaApplicationType getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(BpaApplicationType applicationType) {
		this.applicationType = applicationType;
	}

 

	public Boolean getIsDeemedApproved() {
		return isDeemedApproved;
	}

	public void setIsDeemedApproved(Boolean isDeemedApproved) {
		this.isDeemedApproved = isDeemedApproved;
	}

	public Long getDaysForDeemedApproval() {
		return daysForDeemedApproval;
	}

	public void setDaysForDeemedApproval(Long daysForDeemedApproval) {
		this.daysForDeemedApproval = daysForDeemedApproval;
	}

	public NocIntegrationType getIntegrationType() {
		return integrationType;
	}

	public void setIntegrationType(NocIntegrationType integrationType) {
		this.integrationType = integrationType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getStatusByPush() {
		return statusByPush;
	}

	public void setStatusByPush(Boolean statusByPush) {
		this.statusByPush = statusByPush;
	}

	
}
