package ijse.lk.AutoCareManagement.repository;

import ijse.lk.AutoCareManagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeCode(String employeeCode);

    @Query("SELECT COUNT(e) FROM Employee e")
    long getEmployeeCount();

}
