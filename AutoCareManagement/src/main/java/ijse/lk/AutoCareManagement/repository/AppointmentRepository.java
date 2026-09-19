package ijse.lk.AutoCareManagement.repository;

import ijse.lk.AutoCareManagement.dto.AppointmentSummaryDTO;
import ijse.lk.AutoCareManagement.entity.Appointment;
import ijse.lk.AutoCareManagement.enumeration.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

    @Query("SELECT new ijse.lk.AutoCareManagement.dto.AppointmentSummaryDTO(" +
            "a.appointmentCode, a.appointmentDate, a.appointmentTime, a.specialNotes, CAST(a.status AS string), a.vehicle.licensePlate) " +
            "FROM Appointment a WHERE a.customer.userCode = :customerCode " +
            "AND a.appointmentDate >= :today AND a.status = :status " +
            "ORDER BY a.appointmentDate ASC, a.appointmentTime ASC")
    List<AppointmentSummaryDTO> findApprovedUpcomingAppointmentsByCustomerCode(
            @Param("customerCode") String customerCode,
            @Param("today") LocalDate today,
            @Param("status") AppointmentStatus status
    );
}
