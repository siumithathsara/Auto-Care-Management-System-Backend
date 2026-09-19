package ijse.lk.AutoCareManagement.repository;

import ijse.lk.AutoCareManagement.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository  extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByVehicleCode(String vehicleCode);

    Optional<Vehicle> findByLicensePlate(String licensePlate);

//    for customer Dashboard
    List<Vehicle> findByCustomer_userCode(String userCode);

    List<Vehicle> findByLicensePlateContainingIgnoreCase(String licensePlate);

    Boolean existsByLicensePlate(String licensePlate);

    Boolean existsByVehicleCode(String vehicleCode);

    Boolean existsByChassisNumber(String chassisNumber);

    @Query("SELECT COUNT(v) FROM Vehicle v")
    long getAllVehiclesCount();

    long countByCustomerUserCode(String customerCode);



}
