package ijse.lk.AutoCareManagement.repository;

import ijse.lk.AutoCareManagement.entity.Appointment;
import ijse.lk.AutoCareManagement.enumeration.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByAppointmentCode(String appointmentCode);

    List<Appointment> findByCustomerUserCode(String userCode);

    List<Appointment> findByVehicleVehicleCode(String vehicleCode);

    List<Appointment> findByStatus(AppointmentStatus status);

    boolean existsByAppointmentCode(String appointmentCode);

    @Query("SELECT COUNT(a) FROM Appointment a")
    long getAppointmentCount();
}
