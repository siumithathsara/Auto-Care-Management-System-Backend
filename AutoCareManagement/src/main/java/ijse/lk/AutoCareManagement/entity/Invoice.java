package ijse.lk.AutoCareManagement.entity;

import ijse.lk.AutoCareManagement.enumeration.PaymentMethod;
import ijse.lk.AutoCareManagement.enumeration.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private long invoiceId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_card_id", unique = true, nullable = false)
    private JobCard jobCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by", nullable = false)
    private Users issuedBy;

    @Column(nullable = false)
    private double subtotal;

    @Column(name = "tax_amount")
    private double taxAmount = 0.0;

    private double discount = 0.0;

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Column(name = "paid_amount", nullable = false)
    private double paidAmount = 0.0;

    @Column(name = "balance_amount", nullable = false)
    private double balanceAmount = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 30)
    private PaymentMethod paymentMethod;

    @Column(name = "issued_date", nullable = false, updatable = false)
    private LocalDateTime issuedDate;

    @PrePersist
    protected void onCreate() {
        this.issuedDate = LocalDateTime.now();
        if (this.paymentStatus == null) {
            this.paymentStatus = PaymentStatus.UNPAID;
        }
    }
}
