package ijse.lk.AutoCareManagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierRequestDTO {

    @NotBlank(message = "Company name is required")
    @Size(max = 100, message = "Company name must not exceed 100 characters")
    private String companyName;

    @Size(max = 100, message = "Contact person name must not exceed 100 characters")
    private String contactPerson;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+94|0)?[0-9]{9}$", message = "Invalid Sri Lankan phone number format")
    private String phone;

    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @NotBlank(message = "Address is required")
    private String address;

    @Size(max = 50, message = "BRN number must not exceed 50 characters")
    private String brnNo;
}
