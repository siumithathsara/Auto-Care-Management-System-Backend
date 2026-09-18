package ijse.lk.AutoCareManagement.repository;

import ijse.lk.AutoCareManagement.entity.Invoice;
import ijse.lk.AutoCareManagement.enumeration.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceCode(String invoiceCode);

    boolean existsByInvoiceCode(String invoiceCode);

    boolean existsByJobCard_JobCardCode(String jobCardCode);

    Optional<Invoice> findByJobCard_JobCardCode(String jobCardCode);

    List<Invoice> findByJobCard_Vehicle_Customer_UserCode(String customerUserCode);
}
