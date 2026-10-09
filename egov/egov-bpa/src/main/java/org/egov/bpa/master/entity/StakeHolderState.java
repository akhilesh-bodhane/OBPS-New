package org.egov.bpa.master.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.egov.infra.utils.DateUtils;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.pims.commons.Position;


@SuppressWarnings("serial")
@Entity
@Table(name = "EGBPA_STAKEHOLDER_STATE")
@SequenceGenerator(name = StakeHolderState.SEQ_STAKEHOLDER_STATE, sequenceName = StakeHolderState.SEQ_STAKEHOLDER_STATE, allocationSize = 1)
public class StakeHolderState extends StateAware<Position>{
	
	public static final String SEQ_STAKEHOLDER_STATE = "SEQ_STAKEHOLDER_STATE";
	@Id
	@GeneratedValue(generator = SEQ_STAKEHOLDER_STATE, strategy = GenerationType.SEQUENCE)
	private Long id;

	@NotNull
	@OneToOne
	@JoinColumn(name = "stakeholder", nullable = false)
	private StakeHolder stakeHolder;
	
	@Override
	public String getStateDetails() {
		return String.format("Applicant Name: %s Acknowledgement Number %s Dated %s ",
				stakeHolder.getName() == null ? "Not Specified" : stakeHolder.getName(),
				stakeHolder.getCode() == null ? null : stakeHolder.getCode(),
				stakeHolder.getCreateDate() == null ? DateUtils.toDefaultDateFormat(new Date()) : DateUtils.toDefaultDateFormat(stakeHolder.getCreateDate()));

	}
	@Override
	 public String myLinkId() {
	        return stakeHolder.getId().toString();
	    }

	@Override
    public Long getId() {
        return id;
    }

    @Override
    protected void setId(final Long id) {
        this.id = id;
    }
	
	public StakeHolder getStakeHolder() {
		return stakeHolder;
	}

	public void setStakeHolder(StakeHolder stakeHolder) {
		this.stakeHolder = stakeHolder;
	}
	
}
