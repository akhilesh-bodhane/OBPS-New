package org.egov.bpa.transaction.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.egov.infra.persistence.entity.AbstractAuditable;
import org.hibernate.validator.constraints.SafeHtml;

@Entity
@Table(name = "egbpa_property_owner_details")
@SequenceGenerator(name = PropertyOwnerDetails.SEQ_APPLN_PROPERTY_OWNER_DETAILS, sequenceName = PropertyOwnerDetails.SEQ_APPLN_PROPERTY_OWNER_DETAILS, allocationSize = 1)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertyOwnerDetails extends AbstractAuditable {
	
	   public static final String SEQ_APPLN_PROPERTY_OWNER_DETAILS = "seq_egbpa_property_owner_details";
	    private static final long serialVersionUID = -753308478955937552L;
	    @Id
	    @GeneratedValue(generator = SEQ_APPLN_PROPERTY_OWNER_DETAILS, strategy = GenerationType.SEQUENCE)
	    private Long id;
	    @ManyToOne(cascade = CascadeType.ALL)
	    @NotNull
	    @JoinColumn(name = "application")
	    private BpaApplication application;

	    @SafeHtml
		@Column(name="categoryofproperty")
	    @JsonProperty("CategoryOfProperty")
		private String categoryOfProperty;
		
		@SafeHtml
		@Column(name="tenuretype")
		@JsonProperty("TenureType")
		private String tenureType;
		
		@SafeHtml
		@Column(name="nameofowners")
		@JsonProperty("NameOfOwners")
		private String nameOfOwners;
		
		@SafeHtml
		@Column(name="ownersshare")
		@JsonProperty("OwnersShare")
		private String ownersShare;

	    @Override
	    public Long getId() {
	        return id;
	    }

	    @Override
	    public void setId(Long id) {
	        this.id = id;
	    }

	    public BpaApplication getApplication() {
	        return application;
	    }

	    public void setApplication(BpaApplication application) {
	        this.application = application;
	    }

		public String getCategoryOfProperty() {
			return categoryOfProperty;
		}

		public void setCategoryOfProperty(String categoryOfProperty) {
			this.categoryOfProperty = categoryOfProperty;
		}

		public String getTenureType() {
			return tenureType;
		}

		public void setTenureType(String tenureType) {
			this.tenureType = tenureType;
		}

		public String getNameOfOwners() {
			return nameOfOwners;
		}

		public void setNameOfOwners(String nameOfOwners) {
			this.nameOfOwners = nameOfOwners;
		}

		public String getOwnersShare() {
			return ownersShare;
		}

		public void setOwnersShare(String ownersShare) {
			this.ownersShare = ownersShare;
		}

       
}
