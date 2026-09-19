package ijse.lk.AutoCareManagement.repository;

import ijse.lk.AutoCareManagement.dto.JobCardSummaryDTO;
import ijse.lk.AutoCareManagement.entity.JobCard;
import ijse.lk.AutoCareManagement.enumeration.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    Long countByStatus(JobStatus status);

    Long countByStatusIn(List<JobStatus> statuses);

    long countByVehicleCustomerUserCodeAndStatusIn(String customerCode, List<JobStatus> statuses);

    long countByVehicleCustomerUserCodeAndStatus(String customerCode, JobStatus status);

    @Query("SELECT new ijse.lk.AutoCareManagement.dto.JobCardSummaryDTO(" +
            "j.jobCardCode, CAST(j.status AS string), j.checkInTime, j.estimatedCompletionTime, j.vehicle.licensePlate) " +
            "FROM JobCard j WHERE j.vehicle.customer.userCode = :customerCode " +
            "AND j.status IN (:statuses) " +
            "ORDER BY j.checkInTime DESC")
    List<JobCardSummaryDTO> findActiveJobsByCustomerCode(
            @Param("customerCode") String customerCode,
            @Param("statuses") List<JobStatus> statuses
    );

    @Query("SELECT new ijse.lk.AutoCareManagement.dto.JobCardSummaryDTO(" +
            "j.jobCardCode, CAST(j.status AS string), j.checkInTime, j.estimatedCompletionTime, j.vehicle.licensePlate) " +
            "FROM JobCard j WHERE j.vehicle.customer.userCode = :customerCode " +
            "ORDER BY j.checkInTime DESC")
    List<JobCardSummaryDTO> findTop5ByVehicleCustomerUserCodeOrderByCheckInTimeDesc(@Param("customerCode") String customerCode);
}
