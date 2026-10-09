package org.egov.bpa.transaction.entity;

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
@Entity
@Table(name = "EGBPA_NOCLIST")
@SequenceGenerator(name = NocList.SEQ, sequenceName = NocList.SEQ, allocationSize = 1)
public class NocList {
	public static final String SEQ = "SEQ_EGBPA_NOCLIST";
	@Id
	@GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
	private Long id;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "nocList", targetEntity = NoObjectionCertificate.class)
	private List<NoObjectionCertificate> nocList;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<NoObjectionCertificate> getNocList() {
		return nocList;
	}

	public void setNocList(List<NoObjectionCertificate> nocList) {
		this.nocList = nocList;
	}

	public static String getSeq() {
		return SEQ;
	}

}
