package ijse.lk.AutoCareManagement.controller;

import ijse.lk.AutoCareManagement.constant.CommonResponse;
import ijse.lk.AutoCareManagement.dto.UserRequestDTO;
import ijse.lk.AutoCareManagement.dto.UserResponseDTO;
import ijse.lk.AutoCareManagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/user")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    // Call the service to register the customer
    @PostMapping(value = "/register-customer",produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse registerCustomer(@Valid @RequestBody UserRequestDTO userDTO){
        UserResponseDTO userResponseDTO = userService.registerCustomer(userDTO);
        return new CommonResponse(201, userResponseDTO,"Customer registered successfully");
    }
// Call the service to register the admin
    @PostMapping(value = "/create-admin", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public CommonResponse createAdmin(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO responseDTO = userService.createAdmin(userRequestDTO);
        return new CommonResponse(201, responseDTO, "Admin created successfully!");
    }

    // Call the service to create a staff user (MANAGER, ADVISOR, SUPERVISOR)
    @PostMapping(value = "/create-staff", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public CommonResponse createStaffUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO responseDTO = userService.createStaffUser(userRequestDTO);
        return new CommonResponse(201, responseDTO, "Staff user created successfully!");
    }

//  call the service to get all active users
    @GetMapping(value = "/getAllActiveUsers", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public CommonResponse getAllActiveUsers(){
        List<UserResponseDTO> activeUsers = userService.getAllActiveUsers();
        return new CommonResponse(200, activeUsers, "Active users fetched successfully!");
    }
// call the service to get user by username
    @GetMapping(value = "/get-user/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public CommonResponse getUserByUsername(@PathVariable String username){
        UserResponseDTO userResponseDTO = userService.getUserByUsername(username);
        return new CommonResponse(200, userResponseDTO, "User fetched successfully!");
    }

//    call the service to filter users by username
    @GetMapping(value = "/filter-users", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public CommonResponse filterUsersByUsername(@RequestParam String userName) {
        List<UserResponseDTO> users = userService.filterUserByUsername(userName);
        return new CommonResponse(200, users, "Users filtered successfully!");
    }

// call the service to update user by userCode
    @PutMapping(value = "/update-user/{userCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public CommonResponse updateUser(@PathVariable String userCode, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO updatedUser = userService.updateUser(userCode, userRequestDTO);
        return new CommonResponse(200, updatedUser, "User updated successfully!");
    }

//    call the service to delete user by userCode
    @DeleteMapping(value = "/delete-user/{userCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public CommonResponse deleteUser(@PathVariable String userCode) {
        userService.deleteUser(userCode);
        return new CommonResponse(200, null, "User deleted successfully!");
    }
//  call the service to get total users count
    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public CommonResponse getTotalUsersCount() {
        long count = userService.getTotalUsersCount();
        return new CommonResponse(200, count, "Total user count fetched successfully!");
    }

}
