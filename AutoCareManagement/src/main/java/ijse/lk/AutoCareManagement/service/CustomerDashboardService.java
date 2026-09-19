package ijse.lk.AutoCareManagement.service;

import ijse.lk.AutoCareManagement.dto.CustomerDashboardDTO;

public interface CustomerDashboardService {

    CustomerDashboardDTO getCustomerDashboardData(String customerCode);
}
