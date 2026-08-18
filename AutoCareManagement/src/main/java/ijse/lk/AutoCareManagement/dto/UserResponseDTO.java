package ijse.lk.AutoCareManagement.dto;

import ijse.lk.AutoCareManagement.enumeration.Role;
import ijse.lk.AutoCareManagement.enumeration.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {


    private long userId;
    private String userCode;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Role role;
    private UserStatus status;
    private LocalDateTime createdAt;
    private String nicPassport;
    private String address;

    public UserResponseDTO(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public UserResponseDTO(long userId, String username, Role role, String password) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.password = password;
    }
}
