package ijse.lk.AutoCareManagement.repository;

import ijse.lk.AutoCareManagement.entity.VehicleService;
import ijse.lk.AutoCareManagement.enumeration.DataStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<VehicleService, Long> {

    Optional<VehicleService> findByServiceCode(String serviceCode);

    Optional<VehicleService> findByServiceCodeAndStatus(String serviceCode, DataStatus status);

    boolean existsByServiceName(String serviceName);

    Boolean existsByServiceCode(String serviceCode);

    List<VehicleService> findByStatus(DataStatus status);

    List<VehicleService> findByCategoryCategoryCodeAndStatus(String categoryCode, DataStatus status);

    @Query("SELECT COUNT(s) FROM VehicleService s")
    long getServiceCount();
}
