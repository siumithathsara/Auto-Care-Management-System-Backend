package ijse.lk.AutoCareManagement.repository;

import ijse.lk.AutoCareManagement.entity.InternalStockIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InternalStockIssueRepository extends JpaRepository<InternalStockIssue, Long> {

    Optional<InternalStockIssue> findByInternalPartCode(String internalPartCode);

    List<InternalStockIssue> findByIssuedToEmployee_EmployeeCode(String employeeCode);

    List<InternalStockIssue> findByJobSection_SectionCode(String sectionCode);

    @Query("SELECT COUNT(i) FROM InternalStockIssue i")
    long getInternalIssueCount();

    boolean existsByInternalPartCode(String internalPartCode);
}
