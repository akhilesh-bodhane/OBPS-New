package org.egov.bpa.transaction.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.egov.common.entity.bpa.Checklist;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.tracer.annotations.CustomSafeHtml;

@Entity
@Table(name = "eg_noc_evaluation")
@SequenceGenerator(name = NocEvaluation.SEQ_NOC_EVALUATION, sequenceName = NocEvaluation.SEQ_NOC_EVALUATION, allocationSize = 1)
public class NocEvaluation extends AbstractAuditable {
	
	private static final long serialVersionUID = 3078684328383202788L;
    public static final String SEQ_NOC_EVALUATION = "seq_eg_noc_evaluation";
    
    @Id
    @GeneratedValue(generator = SEQ_NOC_EVALUATION, strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist")
    private Checklist checklist;
    
    @CustomSafeHtml
    private String remarks;
    
    @CustomSafeHtml
    private String comment;
    
    private Long nocapplication;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Checklist getChecklist() {
		return checklist;
	}

	public void setChecklist(Checklist checklist) {
		this.checklist = checklist;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Long getNocapplication() {
		return nocapplication;
	}

	public void setNocapplication(Long nocapplication) {
		this.nocapplication = nocapplication;
	}
    
    
}
