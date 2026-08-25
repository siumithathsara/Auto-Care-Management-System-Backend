package ijse.lk.AutoCareManagement.repository;

import ijse.lk.AutoCareManagement.entity.SparePart;
import ijse.lk.AutoCareManagement.enumeration.DataStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SparePartRepository extends JpaRepository<SparePart, Long> {

    Optional<SparePart> findByPartCode(String partCode);

    @Query("SELECT COUNT(s) FROM SparePart s")
    long getSparePartCount();

    Optional<SparePart> findByPartCodeAndIsActiveTrue(String partCode);

    Optional<SparePart> findByPartCodeAndDataStatus(String partCode, DataStatus dataStatus);

    List<SparePart> findByDataStatus(DataStatus dataStatus);

    boolean existsByPartNameAndDataStatus(String partName, DataStatus dataStatus);

    @Query("SELECT s FROM SparePart s WHERE s.quantityInStock <= s.reorderLevel AND s.isActive = true")
    List<SparePart> findLowStockParts(DataStatus dataStatus);
}
