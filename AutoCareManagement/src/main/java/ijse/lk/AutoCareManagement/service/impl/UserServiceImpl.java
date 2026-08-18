package ijse.lk.AutoCareManagement.service.impl;

import ijse.lk.AutoCareManagement.dto.UserRequestDTO;
import ijse.lk.AutoCareManagement.dto.UserResponseDTO;
import ijse.lk.AutoCareManagement.entity.User;
import ijse.lk.AutoCareManagement.enumeration.Role;
import ijse.lk.AutoCareManagement.enumeration.UserStatus;
import ijse.lk.AutoCareManagement.exception.CustomException;
import ijse.lk.AutoCareManagement.repository.UserRepository;
import ijse.lk.AutoCareManagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO getUserDetails(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new CustomException(404, "User not found");
        }

        User user = optionalUser.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(401, "Password is incorrect");
        }
        return new UserResponseDTO(user.getUserId(), user.getUsername(), user.getRole(), user.getPassword());

    }

    @Override
    public UserResponseDTO registerCustomer(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByUsername((userRequestDTO.getUsername()))) {
            throw new CustomException(400, "Username" + userRequestDTO.getUsername() + " already exists");
        }
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new CustomException(400, "Email" + userRequestDTO.getEmail() + " already exists");
        }
        if (userRequestDTO.getNicPassport() == null || userRequestDTO.getNicPassport().trim().isEmpty()) {
            throw new CustomException(400, "NIC/Passport is required");
        }
        if (userRequestDTO.getPhone() == null || userRequestDTO.getPhone().trim().isEmpty()) {
            throw new CustomException(400, "Phone number is required");
        }
        if (userRequestDTO.getAddress() == null || userRequestDTO.getAddress().trim().isEmpty()) {
            throw new CustomException(400, "Address is required");
        }
        String generatedUserCode = generateUserCode("CUS");

        User customer = new User();
        customer.setUserCode(generatedUserCode);
        customer.setUsername(userRequestDTO.getUsername());
        customer.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        customer.setEmail(userRequestDTO.getEmail());
        customer.setPhone(userRequestDTO.getPhone());
        customer.setRole(Role.CUSTOMER);
        customer.setStatus(UserStatus.ACTIVE);


        customer.setNicPassport(userRequestDTO.getNicPassport());
        customer.setAddress(userRequestDTO.getAddress());

        User savedCustomer = userRepository.save(customer);
        return mapToResponseDTO(savedCustomer);
    }

    @Override
    public UserResponseDTO createAdmin(UserRequestDTO userRequestDTO) {

        if (userRepository.existsByUsername((userRequestDTO.getUsername()))) {
            throw new CustomException(400, "Username" + userRequestDTO.getUsername() + " already exists");
        }
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new CustomException(400, "Email" + userRequestDTO.getEmail() + " already exists");
        }
        String generatedUserCode = generateUserCode("ADM");

        User admin = new User();
        admin.setUserCode(generatedUserCode);
        admin.setUsername(userRequestDTO.getUsername());
        admin.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        admin.setEmail(userRequestDTO.getEmail());
        admin.setPhone(userRequestDTO.getPhone());
        admin.setRole(Role.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);

        admin.setNicPassport(null);
        admin.setAddress(null);

        User savedAdmin = userRepository.save(admin);
        return mapToResponseDTO(savedAdmin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllActiveUsers() {

        List<User> activeUsers = userRepository.findAllByStatus(UserStatus.ACTIVE);
        List<UserResponseDTO> responseDTOList = new ArrayList<>();

        for (User user : activeUsers) {
            UserResponseDTO responseDTO = mapToResponseDTO(user);
            responseDTOList.add(responseDTO);
        }
        return responseDTOList;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUserCode(String userCode) {
        Optional<User> userOptional = userRepository.findByUserCodeAndStatus(userCode, UserStatus.ACTIVE);
        if (userOptional.isEmpty()) {
            throw new CustomException(404, "User not found");
        }
        return mapToResponseDTO(userOptional.get());
    }

    @Override
    public UserResponseDTO getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsernameAndStatus(username, UserStatus.ACTIVE);
        if (userOptional.isPresent()) {
            return mapToResponseDTO(userOptional.get());
        }
        throw new CustomException(404, "User not found");
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> filterUserByUsername(String username) {

        if (username == null || username.trim().isEmpty()) {
            return getAllActiveUsers();
        }
        List<User> filteredUsers = userRepository.findByUsernameContainingIgnoreCaseAndStatus(username, UserStatus.ACTIVE);
        List<UserResponseDTO> responseDTOList = new ArrayList<>();

        for (User user : filteredUsers) {
            UserResponseDTO responseDTO = mapToResponseDTO(user);
            responseDTOList.add(responseDTO);
        }
        return responseDTOList;
    }

    @Override
    public UserResponseDTO updateUser(String userCode, UserRequestDTO userRequestDTO) {

        Optional<User> userOptional = userRepository.findByUserCodeAndStatus(userCode, UserStatus.ACTIVE);
        if (userOptional.isEmpty()) {
            throw new CustomException(404, "User not found");
        }
        User user = userOptional.get();
        if (!user.getUsername().equals(userRequestDTO.getUsername()) &&
                userRepository.existsByUsername(userRequestDTO.getUsername())) {
            throw new CustomException(400, "Username '" + userRequestDTO.getUsername() + "' is already in use!");
        }

        if (!user.getEmail().equals(userRequestDTO.getEmail()) &&
                userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new CustomException(400, "Email '" + userRequestDTO.getEmail() + "' is already in use!");
        }

        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setPhone(userRequestDTO.getPhone());


        if (user.getRole() == Role.CUSTOMER) {
            user.setNicPassport(userRequestDTO.getNicPassport());
            user.setAddress(userRequestDTO.getAddress());
        }
        if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return mapToResponseDTO(updatedUser);

    }

    @Override
    public void deleteUser(String userCode) {
        Optional<User> userOptional = userRepository.findByUserCodeAndStatus(userCode, UserStatus.ACTIVE);
        if (userOptional.isEmpty()) {
            throw new CustomException(404, "User not found");
        }
        User user = userOptional.get();
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalUsersCount() {
        return userRepository.getAllUserCount();
    }

    private String generateUserCode(String priffix) {
        long currentCount = userRepository.getAllUserCount();
        long newCount = currentCount + 1;
        int currentYear = Year.now().getValue();

        String generatedCode = String.format("%s-%d-%04d", priffix, currentYear, newCount);

        while (userRepository.existsByUserCode(generatedCode)) {
            newCount++;
            generatedCode = String.format("%s-%d-%04d", priffix, currentYear, newCount);
        }
        return generatedCode;
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setUserCode(user.getUserCode());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setNicPassport(user.getNicPassport());
        dto.setAddress(user.getAddress());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }


}
