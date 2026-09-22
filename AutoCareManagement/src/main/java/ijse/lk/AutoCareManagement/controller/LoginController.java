package ijse.lk.AutoCareManagement.controller;

import ijse.lk.AutoCareManagement.constant.CommonResponse;
import ijse.lk.AutoCareManagement.dto.AuthDTO;
import ijse.lk.AutoCareManagement.dto.UserResponseDTO;
import ijse.lk.AutoCareManagement.security.JwtUtil;
import ijse.lk.AutoCareManagement.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static ijse.lk.AutoCareManagement.constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping(value = "api/v1/test")
public class LoginController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public LoginController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(value = "/login",produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse authLogin(@RequestBody AuthDTO authDTO) {
        UserResponseDTO userDetails = userService.getUserDetails(authDTO.getUsername(), authDTO.getPassword());
        String token = jwtUtil.generateToken(userDetails);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", token);
        responseData.put("role", userDetails.getRole());

        return new CommonResponse(OPERATION_SUCCESS, responseData, "Login Success");
    }
}
