package org.egov.bpa.transaction.entity;

import java.util.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;

import org.egov.bpa.master.entity.AppointmentLocations;
import org.egov.bpa.transaction.entity.enums.AppointmentSchedulePurpose;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.hibernate.validator.constraints.Length;
import org.egov.tracer.annotations.CustomSafeHtml;

@Entity
@Table(name = "EGBPA_APPOINTMENT_SCHEDULE")
@SequenceGenerator(name = BpaAppointmentSchedule.SEQ_EGBPA_APPOINTMENT_SCHEDULE, sequenceName = BpaAppointmentSchedule.SEQ_EGBPA_APPOINTMENT_SCHEDULE, allocationSize = 1)
public class BpaAppointmentSchedule extends AbstractAuditable {

    private static final long serialVersionUID = -8837161988347102633L;
    public static final String SEQ_EGBPA_APPOINTMENT_SCHEDULE = "SEQ_EGBPA_APPOINTMENT_SCHEDULE";
    @Id
    @GeneratedValue(generator = SEQ_EGBPA_APPOINTMENT_SCHEDULE, strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "application")
    private BpaApplication application;
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private AppointmentSchedulePurpose purpose;
    @Temporal(value = TemporalType.DATE)
    private Date appointmentDate;

    @CustomSafeHtml
    @Length(min = 1, max = 50)
    private String appointmentTime;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "appointmentLocation")
    private AppointmentLocations appointmentLocation;

    @CustomSafeHtml
    @Length(min = 1, max = 256)
    private String remarks;

    @CustomSafeHtml
    @Length(min = 1, max = 256)
    private String postponementReason;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "parent")
    private BpaAppointmentSchedule parent;
    private boolean isPostponed;

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

    public void setApplication(final BpaApplication application) {
        this.application = application;
    }

    public AppointmentSchedulePurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(AppointmentSchedulePurpose purpose) {
        this.purpose = purpose;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(final Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(final String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public AppointmentLocations getAppointmentLocation() {
        return appointmentLocation;
    }

    public void setAppointmentLocation(AppointmentLocations appointmentLocation) {
        this.appointmentLocation = appointmentLocation;
    }

    public String getPostponementReason() {
        return postponementReason;
    }

    public void setPostponementReason(final String postponementReason) {
        this.postponementReason = postponementReason;
    }

    public BpaAppointmentSchedule getParent() {
        return parent;
    }

    public void setParent(final BpaAppointmentSchedule parent) {
        this.parent = parent;
    }

    public boolean isPostponed() {
        return isPostponed;
    }

    public void setPostponed(final boolean isPostponed) {
        this.isPostponed = isPostponed;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(final String remarks) {
        this.remarks = remarks;
    }

}
