package ijse.lk.AutoCareManagement.repository;

import ijse.lk.AutoCareManagement.entity.JobSection;
import ijse.lk.AutoCareManagement.enumeration.SectionName;
import ijse.lk.AutoCareManagement.enumeration.SectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JobSectionRepository extends JpaRepository<JobSection, Long> {

    Optional<JobSection> findBySectionCode(String sectionCode);

    @Query("SELECT COUNT(js) FROM JobSection js")
    long getJobSectionCount();

    List<JobSection> findByJobCard_JobCardCode(String jobCardCode);

    List<JobSection> findByMechanicEmployee_EmployeeCode(String employeeCode);

    List<JobSection> findBySectionStatus(SectionStatus status);

    boolean existsByJobCard_JobCardCodeAndSectionName(String jobCardCode, SectionName sectionName);
}
