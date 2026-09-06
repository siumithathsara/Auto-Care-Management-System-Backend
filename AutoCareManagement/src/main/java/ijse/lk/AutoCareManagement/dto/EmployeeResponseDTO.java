package ijse.lk.AutoCareManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponseDTO {

    private long employeeId;
    private String employeeCode;
    private String employeeName;
    private String designation;
    private String phone;
    private String address;
}
