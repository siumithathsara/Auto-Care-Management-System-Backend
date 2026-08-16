package ijse.lk.AutoCareManagement.service;

import ijse.lk.AutoCareManagement.dto.UserDTO;

public interface UserService {

    UserDTO getUserDetails(String username, String password);
}
