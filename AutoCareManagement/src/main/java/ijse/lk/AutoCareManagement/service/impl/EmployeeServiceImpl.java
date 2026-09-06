package ijse.lk.AutoCareManagement.service.impl;

import ijse.lk.AutoCareManagement.dto.EmployeeRequestDTO;
import ijse.lk.AutoCareManagement.dto.EmployeeResponseDTO;
import ijse.lk.AutoCareManagement.entity.Employee;
import ijse.lk.AutoCareManagement.exception.CustomException;
import ijse.lk.AutoCareManagement.repository.EmployeeRepository;
import ijse.lk.AutoCareManagement.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

   private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeResponseDTO saveEmployee(EmployeeRequestDTO dto) {
        log.info("Registering new employee: {}", dto.getEmployeeName());

        String generatedEmployeeCode = generateNextEmployeeCode();

        Employee employee = new Employee();
        employee.setEmployeeCode(generatedEmployeeCode);
        employee.setEmployeeName(dto.getEmployeeName());
        employee.setDesignation(dto.getDesignation());
        employee.setPhone(dto.getPhone());
        employee.setAddress(dto.getAddress());

        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee successfully registered with code: {}", generatedEmployeeCode);

        return mapToResponseDTO(savedEmployee);
    }

    @Override
    public EmployeeResponseDTO updateEmployee(String employeeCode, EmployeeRequestDTO dto) {
        log.info("Updating employee with code: {}", employeeCode);

        Optional<Employee> employeeOptional = employeeRepository.findByEmployeeCode(employeeCode);
        if (employeeOptional.isEmpty()) {
            log.warn("Employee update failed: Code '{}' not found", employeeCode);
            throw new CustomException(404, "Employee with code '" + employeeCode + "' not found");
        }

        Employee employee = employeeOptional.get();
        employee.setEmployeeName(dto.getEmployeeName());
        employee.setDesignation(dto.getDesignation());
        employee.setPhone(dto.getPhone());
        employee.setAddress(dto.getAddress());

        Employee updatedEmployee = employeeRepository.save(employee);
        log.info("Employee with code '{}' successfully updated", employeeCode);

        return mapToResponseDTO(updatedEmployee);
    }

    @Override
    public EmployeeResponseDTO getEmployeeByCode(String employeeCode) {
        log.info("Fetching employee with code: {}", employeeCode);

        Optional<Employee> employeeOptional = employeeRepository.findByEmployeeCode(employeeCode);
        if (employeeOptional.isEmpty()) {
            log.warn("Employee with code '{}' not found", employeeCode);
            throw new CustomException(404, "Employee with code '" + employeeCode + "' not found");
        }

        return mapToResponseDTO(employeeOptional.get());
    }

    @Override
    public List<EmployeeResponseDTO> getAllEmployees() {
        log.info("Fetching all registered employees");

        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeResponseDTO> responseDTOList = new ArrayList<>();

        for (Employee employee : employees) {
            responseDTOList.add(mapToResponseDTO(employee));
        }

        log.info("Successfully retrieved {} employees", responseDTOList.size());
        return responseDTOList;
    }

    @Override
    public void deleteEmployee(String employeeCode) {
        log.info("Deleting employee with code: {}", employeeCode);

        Optional<Employee> employeeOptional = employeeRepository.findByEmployeeCode(employeeCode);
        if (employeeOptional.isEmpty()) {
            log.warn("Employee with code '{}' not found for deletion", employeeCode);
            throw new CustomException(404, "Employee with code '" + employeeCode + "' not found");
        }

        employeeRepository.delete(employeeOptional.get());
        log.info("Employee successfully deleted with code: {}", employeeCode);
    }

    private String generateNextEmployeeCode() {
        long currentCount = employeeRepository.getEmployeeCount();
        long nextNumber = currentCount + 1;
        int currentYear = Year.now().getValue();

        String generatedCode = String.format("EMP-%d-%04d", currentYear, nextNumber);

        while (employeeRepository.findByEmployeeCode(generatedCode).isPresent()) {
            nextNumber++;
            generatedCode = String.format("EMP-%d-%04d", currentYear, nextNumber);
        }

        log.debug("Generated unique employee code: {}", generatedCode);
        return generatedCode;
    }

    private EmployeeResponseDTO mapToResponseDTO(Employee employee) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setEmployeeCode(employee.getEmployeeCode());
        dto.setEmployeeName(employee.getEmployeeName());
        dto.setDesignation(employee.getDesignation());
        dto.setPhone(employee.getPhone());
        dto.setAddress(employee.getAddress());
        return dto;
    }
}
