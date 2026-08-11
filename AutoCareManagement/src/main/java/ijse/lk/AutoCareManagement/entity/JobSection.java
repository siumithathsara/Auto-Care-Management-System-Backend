package ijse.lk.AutoCareManagement.entity;

import ijse.lk.AutoCareManagement.enumeration.SectionName;
import ijse.lk.AutoCareManagement.enumeration.SectionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class JobSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    private long sectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_card_id", nullable = false)
    private JobCard jobCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", nullable = false)
    private Users supervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mechanic_employee_id", nullable = false)
    private Employee mechanicEmployee;


    @Enumerated(EnumType.STRING)
    @Column(name = "section_name", nullable = false, length = 50)
    private SectionName sectionName;

    @Enumerated(EnumType.STRING)
    @Column(name = "section_status", nullable = false, length = 30)
    private SectionStatus sectionStatus;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        if (this.sectionStatus == null) {
            this.sectionStatus = SectionStatus.PENDING;
        }
    }
}
