package org.egov.bpa.transaction.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.egov.bpa.transaction.entity.common.DocumentScrutiny;
import org.egov.infra.persistence.entity.AbstractAuditable;

@Entity
@Table(name = "EGBPA_PERMIT_DOCUMENT_SCRUTINY")
@SequenceGenerator(name = PermitDocumentScrutiny.SEQ_EGBPA_DOCUMENTSCRUTINY, sequenceName = PermitDocumentScrutiny.SEQ_EGBPA_DOCUMENTSCRUTINY, allocationSize = 1)
public class PermitDocumentScrutiny extends AbstractAuditable {
    private static final long serialVersionUID = 1L;
    public static final String SEQ_EGBPA_DOCUMENTSCRUTINY = "SEQ_EGBPA_PERMIT_DOCUMENT_SCRUTINY";
    @Id
    @GeneratedValue(generator = SEQ_EGBPA_DOCUMENTSCRUTINY, strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    @NotNull
    @JoinColumn(name = "application", nullable = false)
    private BpaApplication application;
    @ManyToOne(cascade = CascadeType.ALL)
    @NotNull
    @JoinColumn(name = "documentScrutiny", nullable = false)
    private DocumentScrutiny docScrutiny;

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

    public DocumentScrutiny getDocScrutiny() {
        return docScrutiny;
    }

    public void setDocScrutiny(DocumentScrutiny docScrutiny) {
        this.docScrutiny = docScrutiny;
    }

}
