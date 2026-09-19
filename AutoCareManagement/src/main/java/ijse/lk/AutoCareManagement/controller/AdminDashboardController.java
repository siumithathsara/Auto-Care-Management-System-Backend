package ijse.lk.AutoCareManagement.controller;

import ijse.lk.AutoCareManagement.constant.CommonResponse;
import ijse.lk.AutoCareManagement.dto.AdminDashboardDTO;
import ijse.lk.AutoCareManagement.dto.AdvisorDashboardDTO;
import ijse.lk.AutoCareManagement.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DashboardService dashboardService;

//     Get Admin Dashboard Data
    @GetMapping(value = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse getAdminDashboard() {
        AdminDashboardDTO adminDashboard = dashboardService.getAdminDashboard();
        return new CommonResponse(200, adminDashboard, "Admin dashboard metrics fetched successfully!");
    }

//     Get Service Advisor Dashboard Data
    @GetMapping(value = "/advisor", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SERVICE_ADVISOR')")
    public CommonResponse getAdvisorDashboard() {
        AdvisorDashboardDTO advisorDashboard = dashboardService.getAdvisorDashboard();
        return new CommonResponse(200, advisorDashboard, "Advisor dashboard metrics fetched successfully!");
    }
}
