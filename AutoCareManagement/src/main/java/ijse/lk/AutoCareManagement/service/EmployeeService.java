package ijse.lk.AutoCareManagement.service;

import ijse.lk.AutoCareManagement.dto.EmployeeRequestDTO;
import ijse.lk.AutoCareManagement.dto.EmployeeResponseDTO;

import java.util.List;

public interface EmployeeService {

    EmployeeResponseDTO saveEmployee(EmployeeRequestDTO dto);

    EmployeeResponseDTO updateEmployee(String employeeCode, EmployeeRequestDTO dto);

    EmployeeResponseDTO getEmployeeByCode(String employeeCode);

    List<EmployeeResponseDTO> getAllEmployees();

    void deleteEmployee(String employeeCode);
}
