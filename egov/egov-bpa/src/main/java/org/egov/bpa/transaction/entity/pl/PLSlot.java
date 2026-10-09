package org.egov.bpa.transaction.entity.pl;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.egov.bpa.transaction.entity.SlotDetail;
import org.egov.bpa.transaction.entity.enums.ScheduleAppointmentType;
import org.egov.infra.persistence.entity.AbstractAuditable;

@Entity
@Table(name = "egbpa_pl_slot")
@SequenceGenerator(name = PLSlot.SEQ, sequenceName = PLSlot.SEQ, allocationSize = 1)
public class PLSlot extends AbstractAuditable {
	protected static final String SEQ = "seq_egbpa_pl_slot";
	private static final long serialVersionUID = 3649900317247159528L;

	@Id
	@GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
	private Long id;

	@Enumerated(EnumType.STRING)
	@NotNull
	private ScheduleAppointmentType scheduleAppointmentType;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "plinthlevelcertificate", nullable = false)
	private PlinthLevelCertificate pl;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "slotdetailid", nullable = false)
	private SlotDetail slotDetail;

	private Boolean isActive;
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public ScheduleAppointmentType getScheduleAppointmentType() {
		return scheduleAppointmentType;
	}

	public void setScheduleAppointmentType(ScheduleAppointmentType scheduleAppointmentType) {
		this.scheduleAppointmentType = scheduleAppointmentType;
	}
	
	public SlotDetail getSlotDetail() {
		return slotDetail;
	}

	public void setSlotDetail(SlotDetail slotDetail) {
		this.slotDetail = slotDetail;
	}

	public Boolean getActive() {
		return isActive;
	}

	public void setActive(Boolean active) {
		isActive = active;
	}

	public PlinthLevelCertificate getPl() {
		return pl;
	}

	public void setPl(PlinthLevelCertificate pl) {
		this.pl = pl;
	}
}
