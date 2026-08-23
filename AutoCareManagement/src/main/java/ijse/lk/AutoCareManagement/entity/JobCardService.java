package ijse.lk.AutoCareManagement.entity;

import ijse.lk.AutoCareManagement.enumeration.ApprovalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class JobCardService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long JobCardServiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_card_id", nullable = false)
    private JobCard jobCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private VehicleService vehicleService;

    @Column(nullable = false)
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false)
    private ApprovalStatus approvalStatus;

    @PrePersist
    protected void onCreate() {
        if (this.approvalStatus == null) {
            this.approvalStatus = ApprovalStatus.PENDING;
        }
    }
}
