package ijse.lk.AutoCareManagement.service.impl;

import ijse.lk.AutoCareManagement.dto.UserDTO;
import ijse.lk.AutoCareManagement.entity.Users;
import ijse.lk.AutoCareManagement.repository.UserRepository;
import ijse.lk.AutoCareManagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDTO getUserDetails(String username, String password) {
        Optional<Users> optionalUser = userRepository.findByUsernameAndPassword(username,password);
        if(optionalUser.isEmpty())
            throw new RuntimeException("Sorry no user");

        Users user = optionalUser.get();
        return new UserDTO(user.getUserId(),user.getUsername(),user.getRole(),user.getPassword());

    }

}
