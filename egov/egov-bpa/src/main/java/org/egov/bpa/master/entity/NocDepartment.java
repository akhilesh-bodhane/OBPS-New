package org.egov.bpa.master.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.egov.infra.persistence.entity.AbstractAuditable;
import org.hibernate.validator.constraints.Length;
import org.egov.tracer.annotations.CustomSafeHtml;

@Entity
@Table(name = "EGBPA_MSTR_NOCDEPARTMENT")
@SequenceGenerator(name = NocDepartment.SEQ, sequenceName = NocDepartment.SEQ, allocationSize = 1)
public class NocDepartment extends AbstractAuditable {

	public static final String SEQ = "SEQ_EGBPA_MSTR_NOCDEPARTMENT";
	@Id
	@GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
	private Long id;
	@CustomSafeHtml
	@Length(min = 2, max = 20)
	
	
	private String code;
	@Length(min = 2, max = 124)
	
	
	@CustomSafeHtml
	private String name;
	
	
	private Boolean isActive;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,mappedBy="nocDepartment")
	private List<NocConfig> nocConfigs;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public List<NocConfig> getNocConfigs() {
		return nocConfigs;
	}

	public void setNocConfigs(List<NocConfig> nocConfigs) {
		this.nocConfigs = nocConfigs;
	}

}
