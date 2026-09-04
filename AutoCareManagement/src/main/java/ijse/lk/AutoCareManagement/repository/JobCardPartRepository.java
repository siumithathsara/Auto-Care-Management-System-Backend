package ijse.lk.AutoCareManagement.repository;

import ijse.lk.AutoCareManagement.entity.JobCardPart;
import ijse.lk.AutoCareManagement.enumeration.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobCardPartRepository extends JpaRepository<JobCardPart,Long> {

    List<JobCardPart> findByIssueStatus(IssueStatus issueStatus);

    List<JobCardPart> findByJobCard_JobCardCode(String jobCardCode);
}
