package ijse.lk.AutoCareManagement.repository;

import ijse.lk.AutoCareManagement.entity.ServiceCategory;
import ijse.lk.AutoCareManagement.enumeration.DataStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {

    Optional<ServiceCategory> findByCategoryCode(String categoryCode);

    Optional<ServiceCategory> findByCategoryCodeAndStatus(String categoryCode, DataStatus status);

    boolean existsByCategoryName(String categoryName);

    boolean existsByCategoryCode(String categoryCode);

    List<ServiceCategory> findByStatus(DataStatus status);

    @Query("SELECT COUNT(sc) FROM ServiceCategory sc")
    long getCategoryCount();
}
