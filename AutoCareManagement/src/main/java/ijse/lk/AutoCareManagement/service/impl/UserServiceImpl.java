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
        log.info("Attempting to get user details for username: {}", username);

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            log.warn("User detail fetch failed: Username '{}' not found", username);
            throw new CustomException(404, "User not found");
        }

        User user = optionalUser.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("User detail fetch failed: Incorrect password for username '{}'", username);
            throw new CustomException(401, "Password is incorrect");
        }
        log.info("Successfully retrieved details for user ID: {}", user.getUserId());
        return new UserResponseDTO(user.getUserId(), user.getUsername(), user.getRole(), user.getPassword());

    }

    @Override
    public UserResponseDTO registerCustomer(UserRequestDTO userRequestDTO) {
        log.info("Starting customer registration for username: {}, email: {}", userRequestDTO.getUsername(), userRequestDTO.getEmail());

        if (userRepository.existsByUsername((userRequestDTO.getUsername()))) {
            log.warn("Customer registration failed: Username '{}' already exists", userRequestDTO.getUsername());
            throw new CustomException(400, "Username" + userRequestDTO.getUsername() + " already exists");
        }
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            log.warn("Customer registration failed: Email '{}' already exists", userRequestDTO.getEmail());
            throw new CustomException(400, "Email" + userRequestDTO.getEmail() + " already exists");
        }
        if (userRequestDTO.getNicPassport() == null || userRequestDTO.getNicPassport().trim().isEmpty()) {
            log.warn("Customer registration failed: NIC/Passport is required");
            throw new CustomException(400, "NIC/Passport is required");
        }
        if (userRequestDTO.getPhone() == null || userRequestDTO.getPhone().trim().isEmpty()) {
            log.warn("Customer registration failed: Phone number is required");
            throw new CustomException(400, "Phone number is required");
        }
        if (userRequestDTO.getAddress() == null || userRequestDTO.getAddress().trim().isEmpty()) {
            log.warn("Customer registration failed: Address is required");
            throw new CustomException(400, "Address is required");
        }
        String generatedUserCode = generateUserCode("CUS");
        log.debug("Generated user code for customer: {}", generatedUserCode);

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
        log.info("Customer registered successfully with user ID: {}", savedCustomer.getUserId());
        return mapToResponseDTO(savedCustomer);
    }

    @Override
    public UserResponseDTO createAdmin(UserRequestDTO userRequestDTO) {
        log.info("Starting admin creation for username: {}, email: {}", userRequestDTO.getUsername(), userRequestDTO.getEmail());

        if (userRepository.existsByUsername((userRequestDTO.getUsername()))) {
            log.warn("Admin creation failed: Username '{}' already exists", userRequestDTO.getUsername());
            throw new CustomException(400, "Username" + userRequestDTO.getUsername() + " already exists");
        }
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            log.warn("Admin creation failed: Email '{}' already exists", userRequestDTO.getEmail());
            throw new CustomException(400, "Email" + userRequestDTO.getEmail() + " already exists");
        }
        String generatedUserCode = generateUserCode("ADM");
        log.debug("Generated user code for admin: {}", generatedUserCode);

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
        log.info("Admin created successfully with user ID: {}", savedAdmin.getUserCode());
        return mapToResponseDTO(savedAdmin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllActiveUsers() {
        log.info("Fetching all active users");

        List<User> activeUsers = userRepository.findAllByStatus(UserStatus.ACTIVE);
        List<UserResponseDTO> responseDTOList = new ArrayList<>();

        for (User user : activeUsers) {
            UserResponseDTO responseDTO = mapToResponseDTO(user);
            responseDTOList.add(responseDTO);
        }
        log.info("Retrieved {} active users", responseDTOList.size());
        return responseDTOList;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUserCode(String userCode) {
        log.info("Fetching active user details for User Code: {}", userCode);
        Optional<User> userOptional = userRepository.findByUserCodeAndStatus(userCode, UserStatus.ACTIVE);
        if (userOptional.isEmpty()) {
            log.warn("User not found for user code: {}", userCode);
            throw new CustomException(404, "User not found");
        }
        log.info("Successfully retrieved user details for User Code: {}", userCode);
        return mapToResponseDTO(userOptional.get());
    }

    @Override
    public UserResponseDTO getUserByUsername(String username) {
        log.info("Fetching user details for username: {}", username);
        Optional<User> userOptional = userRepository.findByUsernameAndStatus(username, UserStatus.ACTIVE);
        if (userOptional.isPresent()) {
            log.info("Successfully retrieved user details for username: {}", username);
            return mapToResponseDTO(userOptional.get());
        }
        log.warn("User not found for username: {}", username);
        throw new CustomException(404, "User not found");
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> filterUserByUsername(String username) {
        log.info("Filtering users by username: {}", username);
        if (username == null || username.trim().isEmpty()) {
            log.info("Username filter keyword is empty, returning all active users");
            return getAllActiveUsers();
        }
        List<User> filteredUsers = userRepository.findByUsernameContainingIgnoreCaseAndStatus(username, UserStatus.ACTIVE);
        List<UserResponseDTO> responseDTOList = new ArrayList<>();

        for (User user : filteredUsers) {
            UserResponseDTO responseDTO = mapToResponseDTO(user);
            responseDTOList.add(responseDTO);
        }
        log.info("Filtered {} users by username: {}", responseDTOList.size(), username);
        return responseDTOList;
    }

    @Override
    public UserResponseDTO updateUser(String userCode, UserRequestDTO userRequestDTO) {
        log.info("Attempting to update user with User Code: {}", userCode);
        Optional<User> userOptional = userRepository.findByUserCodeAndStatus(userCode, UserStatus.ACTIVE);
        if (userOptional.isEmpty()) {
            log.warn("User update failed: User not found for User Code: {}", userCode);
            throw new CustomException(404, "User not found");
        }
        User user = userOptional.get();
        if (!user.getUsername().equals(userRequestDTO.getUsername()) &&
                userRepository.existsByUsername(userRequestDTO.getUsername())) {
            log.warn("User update failed: New Username '{}' is already in use", userRequestDTO.getUsername());
            throw new CustomException(400, "Username '" + userRequestDTO.getUsername() + "' is already in use!");
        }

        if (!user.getEmail().equals(userRequestDTO.getEmail()) &&
                userRepository.existsByEmail(userRequestDTO.getEmail())) {
            log.warn("User update failed: New Email '{}' is already in use", userRequestDTO.getEmail());
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
            log.debug("Updating password for User Code: {}", userCode);
            user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        log.info("Successfully updated user with User Code: {}", userCode);
        return mapToResponseDTO(updatedUser);

    }

    @Override
    public void deleteUser(String userCode) {
        log.info("Attempting to soft delete user with User Code: {}", userCode);
        Optional<User> userOptional = userRepository.findByUserCodeAndStatus(userCode, UserStatus.ACTIVE);
        if (userOptional.isEmpty()) {
            log.warn("User deletion failed: User Code '{}' not found or already inactive", userCode);
            throw new CustomException(404, "User not found");
        }
        User user = userOptional.get();
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        log.info("Successfully deactivated user with User Code: {}", userCode);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalUsersCount() {
        log.info("Fetching total users count from database");
        return userRepository.getAllUserCount();
    }

    private String generateUserCode(String priffix) {
        log.debug("Generating user code with prefix: {}", priffix);
        long currentCount = userRepository.getAllUserCount();
        long newCount = currentCount + 1;
        int currentYear = Year.now().getValue();

        String generatedCode = String.format("%s-%d-%04d", priffix, currentYear, newCount);

        while (userRepository.existsByUserCode(generatedCode)) {
            newCount++;
            generatedCode = String.format("%s-%d-%04d", priffix, currentYear, newCount);
        }
        log.debug("Final generated user code: {}", generatedCode);
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
