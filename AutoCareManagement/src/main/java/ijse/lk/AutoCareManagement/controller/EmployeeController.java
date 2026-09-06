package ijse.lk.AutoCareManagement.controller;

import ijse.lk.AutoCareManagement.constant.CommonResponse;
import ijse.lk.AutoCareManagement.dto.EmployeeRequestDTO;
import ijse.lk.AutoCareManagement.dto.EmployeeResponseDTO;
import ijse.lk.AutoCareManagement.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

//    employee register
    @PostMapping(value = "/register-employee", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse saveEmployee(@Valid @RequestBody EmployeeRequestDTO dto) {
        EmployeeResponseDTO savedEmployee = employeeService.saveEmployee(dto);
        return new CommonResponse(201, savedEmployee, "Employee registered successfully!");
    }

//   employee update by employee code
    @PutMapping(value = "/{employeeCode}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse updateEmployee(@PathVariable String employeeCode, @Valid @RequestBody EmployeeRequestDTO dto) {
        EmployeeResponseDTO updatedEmployee = employeeService.updateEmployee(employeeCode, dto);
        return new CommonResponse(200, updatedEmployee, "Employee updated successfully!");
    }

//    employee get by employee code
    @GetMapping(value = "/{employeeCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR')")
    public CommonResponse getEmployeeByCode(@PathVariable String employeeCode) {
        EmployeeResponseDTO employee = employeeService.getEmployeeByCode(employeeCode);
        return new CommonResponse(200, employee, "Employee fetched successfully!");
    }

//   employee get all employees
    @GetMapping(value = "/get-all-employee", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR')")
    public CommonResponse getAllEmployees() {
        List<EmployeeResponseDTO> list = employeeService.getAllEmployees();
        return new CommonResponse(200, list, "Employees fetched successfully!");
    }

//  delete employee by employee code
    @DeleteMapping(value = "/{employeeCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse deleteEmployee(@PathVariable String employeeCode) {
        employeeService.deleteEmployee(employeeCode);
        return new CommonResponse(200, null, "Employee deleted successfully!");
    }
}
