package ijse.lk.AutoCareManagement.repository;

import ijse.lk.AutoCareManagement.entity.Invoice;
import ijse.lk.AutoCareManagement.enumeration.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceCode(String invoiceCode);

    boolean existsByInvoiceCode(String invoiceCode);

    boolean existsByJobCard_JobCardCode(String jobCardCode);

    Optional<Invoice> findByJobCard_JobCardCode(String jobCardCode);

    List<Invoice> findByJobCard_Vehicle_Customer_UserCode(String customerUserCode);

    Long countByPaymentStatus(PaymentStatus paymentStatus);

    @Query("SELECT COALESCE(SUM(i.paidAmount), 0) FROM Invoice i " +
            "WHERE i.issuedDate >= CURRENT_DATE AND i.paymentStatus = 'PAID'")
    Double getTodayRevenue();

    @Query("SELECT COALESCE(SUM(i.paidAmount), 0) FROM Invoice i WHERE MONTH(i.issuedDate) = MONTH(CURRENT_DATE) AND YEAR(i.issuedDate) = YEAR(CURRENT_DATE) AND i.paymentStatus = 'PAID'")
    Double getThisMonthRevenue();

    @Query("SELECT COALESCE(SUM(i.paidAmount), 0) FROM Invoice i WHERE i.paymentStatus = 'PAID'")
    Double getTotalRevenue();

    @Query("SELECT COALESCE(SUM(i.balanceAmount), 0) FROM Invoice i WHERE i.paymentStatus = 'UNPAID' OR i.paymentStatus = 'PARTIALLY_PAID'")
    Double getTotalUnpaidAmount();
}
