package ijse.lk.AutoCareManagement.repository;

import ijse.lk.AutoCareManagement.entity.JobCard;
import ijse.lk.AutoCareManagement.enumeration.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JobCardRepository extends JpaRepository<JobCard, Long> {

    Optional<JobCard> findByJobCardCode(String jobCardCode);

    List<JobCard> findByVehicleVehicleCode(String vehicleCode);

    List<JobCard> findByStatus(JobStatus status);

    boolean existsByAppointmentAppointmentCode(String appointmentCode);

    boolean existsByVehicleVehicleCodeAndStatusIn(String vehicleCode, List<JobStatus> statuses);

    @Query("SELECT COUNT(j) FROM JobCard j")
    long getJobCardCount();
}
