package ijse.lk.AutoCareManagement.entity;

import ijse.lk.AutoCareManagement.enumeration.IssueStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class JobCardPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_card_part_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_card_id", nullable = false)
    private JobCard jobCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    private SparePart sparePart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by", nullable = false)
    private Users requestedBy;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private double unitPrice;

    @Column(name = "sub_total", nullable = false, precision = 10, scale = 2)
    private double subTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "issue_status", nullable = false, length = 30)
    private IssueStatus issueStatus = IssueStatus.REQUESTED;

}
