package org.egov.bpa.transaction.entity.pl;

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

import org.egov.bpa.transaction.entity.common.NoticeCommon;
import org.egov.infra.persistence.entity.AbstractAuditable;

@Entity
@Table(name = "egbpa_pl_notice")
@SequenceGenerator(name = PLNotice.SEQ_EGBPA_PL_NOTICE, sequenceName = PLNotice.SEQ_EGBPA_PL_NOTICE, allocationSize = 1)
public class PLNotice extends AbstractAuditable {
	public static final String SEQ_EGBPA_PL_NOTICE = "seq_egbpa_pl_notice";
	
	@Id
    @GeneratedValue(generator = SEQ_EGBPA_PL_NOTICE, strategy = GenerationType.SEQUENCE)
    private Long id;
	
	@ManyToOne(cascade = CascadeType.ALL)
    @NotNull
    @JoinColumn(name = "plinthLevelCertificate", nullable = false)
    private PlinthLevelCertificate pl;

    @ManyToOne(cascade = CascadeType.ALL)
    @NotNull
    @JoinColumn(name = "notice", nullable = false)
    private NoticeCommon noticeCommon;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public PlinthLevelCertificate getPl() {
		return pl;
	}

	public void setPl(PlinthLevelCertificate pl) {
		this.pl = pl;
	}

	public NoticeCommon getNoticeCommon() {
		return noticeCommon;
	}

	public void setNoticeCommon(NoticeCommon noticeCommon) {
		this.noticeCommon = noticeCommon;
	}   
}
