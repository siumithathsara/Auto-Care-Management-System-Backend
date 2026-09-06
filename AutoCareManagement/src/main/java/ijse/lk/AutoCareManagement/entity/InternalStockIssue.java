package ijse.lk.AutoCareManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class InternalStockIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_id")
    private long issueId;

    @Column(name = "internal_part_code", unique = true, nullable = false, length = 50)
    private String internalPartCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    private SparePart sparePart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_to_employee", nullable = false)
    private Employee issuedToEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by_user", nullable = false)
    private User issuedByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_section_id")
    private JobSection jobSection;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_cost")
    private double unitCost;

    @Column(name = "usage_reason", length = 255)
    private String usageReason;

    @Column(name = "issued_at", nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    @PrePersist
    protected void onCreate() {
        this.issuedAt = LocalDateTime.now();
    }
}
