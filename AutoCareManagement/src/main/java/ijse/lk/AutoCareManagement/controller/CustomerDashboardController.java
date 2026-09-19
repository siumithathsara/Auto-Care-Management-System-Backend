package ijse.lk.AutoCareManagement.controller;

import ijse.lk.AutoCareManagement.constant.CommonResponse;
import ijse.lk.AutoCareManagement.dto.CustomerDashboardDTO;
import ijse.lk.AutoCareManagement.service.CustomerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/customer-dashboard")
@RequiredArgsConstructor
public class CustomerDashboardController {

    private final CustomerDashboardService customerDashboardService;

//         Get dashboard summary data by customer code
    @GetMapping(value = "/get-by-customer/{customerCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CUSTOMER')")
    public CommonResponse getCustomerDashboardData(@PathVariable String customerCode) {
        CustomerDashboardDTO dashboardData = customerDashboardService.getCustomerDashboardData(customerCode);
        return new CommonResponse(200, dashboardData, "Customer dashboard data fetched successfully!");
    }
}
