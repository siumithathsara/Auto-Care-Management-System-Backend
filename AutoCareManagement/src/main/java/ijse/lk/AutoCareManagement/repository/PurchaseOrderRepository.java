package ijse.lk.AutoCareManagement.repository;

import ijse.lk.AutoCareManagement.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    Optional<PurchaseOrder> findByPoCode(String poCode);

    @Query("SELECT MAX(p.poId) FROM PurchaseOrder p")
    Long findLastId();
}
