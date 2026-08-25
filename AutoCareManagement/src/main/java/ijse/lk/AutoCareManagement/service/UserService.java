package ijse.lk.AutoCareManagement.service;

import ijse.lk.AutoCareManagement.dto.UserRequestDTO;
import ijse.lk.AutoCareManagement.dto.UserResponseDTO;

import java.util.List;

public interface UserService {

    UserResponseDTO getUserDetails(String username, String password);

    UserResponseDTO registerCustomer(UserRequestDTO userRequestDTO);

    UserResponseDTO createAdmin(UserRequestDTO userRequestDTO);

    UserResponseDTO createStaffUser(UserRequestDTO userRequestDTO);

    List<UserResponseDTO> getAllActiveUsers();

    UserResponseDTO getUserByUserCode(String userCode);

    UserResponseDTO getUserByUsername(String username);

    List<UserResponseDTO> filterUserByUsername (String username);

    UserResponseDTO updateUser(String userCode, UserRequestDTO userRequestDTO);

    void deleteUser(String userCode);

    long getTotalUsersCount();


}
