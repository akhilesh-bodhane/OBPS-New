package org.egov.bpa.transaction.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.egov.bpa.transaction.entity.PermitLetterToParty;
import org.egov.infra.persistence.entity.AbstractAuditable;

@Entity
@Table(name = "egbpa_permit_lettertoparty_fee")
@SequenceGenerator(name = LetterToPartyFee.SEQ_LETTERTOPARTY_FEE, sequenceName = LetterToPartyFee.SEQ_LETTERTOPARTY_FEE, allocationSize = 1)
public class LetterToPartyFee extends AbstractAuditable {
	private static final long serialVersionUID = 3078684328383202788L;
    public static final String SEQ_LETTERTOPARTY_FEE = "seq_egbpa_permit_lettertoparty_fee";
    
    @Id
    @GeneratedValue(generator = SEQ_LETTERTOPARTY_FEE, strategy = GenerationType.SEQUENCE)
    private Long id;
    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "application")
    private BpaApplication application;
    
    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lettertoparty")
    private PermitLetterToParty permitLetterToParty;
    
    @OrderBy("id ASC")
	@OneToMany(mappedBy = "letterToPartyFee", cascade = CascadeType.ALL)
	private List<LetterToPartyFeeDetails> letterToPartyFeeDetails = new ArrayList<>(0);
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

	public BpaApplication getApplication() {
		return application;
	}

	public void setApplication(BpaApplication application) {
		this.application = application;
	}

	public PermitLetterToParty getPermitLetterToParty() {
		return permitLetterToParty;
	}

	public void setPermitLetterToParty(PermitLetterToParty permitLetterToParty) {
		this.permitLetterToParty = permitLetterToParty;
	}

	public List<LetterToPartyFeeDetails> getLetterToPartyFeeDetails() {
		return letterToPartyFeeDetails;
	}

	public void setLetterToPartyFeeDetails(List<LetterToPartyFeeDetails> letterToPartyFeeDetails) {
		this.letterToPartyFeeDetails = letterToPartyFeeDetails;
	}
}
