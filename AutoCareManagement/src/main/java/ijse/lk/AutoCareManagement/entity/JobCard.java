package ijse.lk.AutoCareManagement.entity;

import ijse.lk.AutoCareManagement.enumeration.JobStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class JobCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_card_id")
    private long jobCardId;

    @Column(name = "job_card_code", nullable = false, unique = true, length = 30)
    private String jobCardCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = true)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User advisor;

    @Column(name = "mileage_in", nullable = false)
    private int mileageIn;

    @Column(name = "fuel_level", length = 20)
    private String fuelLevel;

    @Column(name = "customer_notes", columnDefinition = "TEXT")
    private String customerNotes;

    @Column(name = "advisor_notes", columnDefinition = "TEXT")
    private String advisorNotes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JobStatus status;

    @Column(name = "check_in_time", updatable = false)
    private LocalDateTime checkInTime;

    @Column(name = "estimated_completion_time")
    private LocalDateTime estimatedCompletionTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @OneToMany(mappedBy = "jobCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobCardPart> items;

    @OneToMany(mappedBy = "jobCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobCardServiceCategory> services;

    @PrePersist
    protected void onCreate() {
        this.checkInTime = LocalDateTime.now();
        if (this.status == null) {
            this.status = JobStatus.IN_PROGRESS;
        }
    }
}
