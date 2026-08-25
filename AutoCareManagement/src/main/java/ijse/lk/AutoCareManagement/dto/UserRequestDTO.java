package ijse.lk.AutoCareManagement.dto;

import ijse.lk.AutoCareManagement.enumeration.Role;
import ijse.lk.AutoCareManagement.enumeration.UserStatus;
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
public class UserRequestDTO {
// customer request dto when registering a new account

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be a valid 10-digit number")
    private String phone;

    @NotBlank(message = "NIC or Passport number is required")
    @Pattern(regexp = "^([0-9]{9}[vVxX]|[0-9]{12}|[A-Za-z0-9]{6,9})$",
            message = "Invalid NIC or Passport format")
    private String nicPassport;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
    private String address;

    private Role role;

//    admin use this constructor to create a new user with username, phone, email and password


    public UserRequestDTO(String username, String password, String email, String phone, String nicPassport, String address) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.nicPassport = nicPassport;
        this.address = address;
    }

    public UserRequestDTO(String username, String password, String email, String phone) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
    }
}
