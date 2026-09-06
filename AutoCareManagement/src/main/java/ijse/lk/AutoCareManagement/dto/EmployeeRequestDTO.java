package ijse.lk.AutoCareManagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequestDTO {

    @NotBlank(message = "Employee name is required")
    private String employeeName;

    @NotBlank(message = "Designation is required")
    private String designation;

    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid phone number. Must be 10 digits")
    private String phone;

    private String address;
}
