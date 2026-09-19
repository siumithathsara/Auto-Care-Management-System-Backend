package ijse.lk.AutoCareManagement.service;

import ijse.lk.AutoCareManagement.dto.AdminDashboardDTO;
import ijse.lk.AutoCareManagement.dto.AdvisorDashboardDTO;
import ijse.lk.AutoCareManagement.dto.SharedDashboardOverviewDTO;

public interface DashboardService {

    AdminDashboardDTO getAdminDashboard();

    AdvisorDashboardDTO getAdvisorDashboard();

    SharedDashboardOverviewDTO getSharedOverview();
}
