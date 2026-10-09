package org.egov.bpa.transaction.entity;

import java.util.Collections;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.egov.infra.filestore.entity.FileStoreMapper;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "EGBPA_DOC_FILES")
@SequenceGenerator(name = DocumentAttachment.SEQ, sequenceName = DocumentAttachment.SEQ, allocationSize = 1)
public class DocumentAttachment {
	public static final String SEQ = "SEQ_EGBPA_DOC_FILES";
	@Id
	@GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@Length(min = 2, max = 1024)
	private String type;

	@Length(min = 2, max = 1024)
	private String description;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "egbpa_doc_files2", joinColumns = @JoinColumn(name = "filestoreid"), inverseJoinColumns = @JoinColumn(name = "filestore"))
	private Set<FileStoreMapper> docs = Collections.emptySet();

	

}
