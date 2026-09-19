package ijse.lk.AutoCareManagement.service.impl;

import ijse.lk.AutoCareManagement.dto.AdminDashboardDTO;
import ijse.lk.AutoCareManagement.dto.AdvisorDashboardDTO;
import ijse.lk.AutoCareManagement.dto.SharedDashboardOverviewDTO;
import ijse.lk.AutoCareManagement.enumeration.JobStatus;
import ijse.lk.AutoCareManagement.enumeration.PaymentStatus;
import ijse.lk.AutoCareManagement.repository.InvoiceRepository;
import ijse.lk.AutoCareManagement.repository.JobCardRepository;
import ijse.lk.AutoCareManagement.repository.SparePartRepository;
import ijse.lk.AutoCareManagement.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {
    private final InvoiceRepository invoiceRepository;
    private final JobCardRepository jobCardRepository;
    private final SparePartRepository sparePartRepository;

    @Override
    public SharedDashboardOverviewDTO getSharedOverview() {
        log.info("Fetching shared dashboard overview data...");

        SharedDashboardOverviewDTO shared = new SharedDashboardOverviewDTO();

        Long activeJobCards = jobCardRepository.countByStatus(JobStatus.IN_PROGRESS);
        Long completedJobCards = jobCardRepository.countByStatus(JobStatus.COMPLETED);
        Long vehiclesInWorkshop = jobCardRepository.countByStatusIn(List.of(JobStatus.PENDING, JobStatus.IN_PROGRESS));

        shared.setActiveJobCards(activeJobCards);
        shared.setCompletedJobCards(completedJobCards);
        shared.setVehiclesInWorkshop(vehiclesInWorkshop);

        log.debug("Active Job Cards: {}, Completed: {}, Vehicles in Workshop: {}",
                activeJobCards, completedJobCards, vehiclesInWorkshop);

        Map<String, Long> statusBreakdown = new HashMap<>();
        for (JobStatus status : JobStatus.values()) {
            Long count = jobCardRepository.countByStatus(status);
            statusBreakdown.put(status.name(), count);
        }
        shared.setJobCardStatusBreakdown(statusBreakdown);

        log.info("Shared dashboard overview data loaded successfully");
        return shared;
    }

    @Override
    public AdminDashboardDTO getAdminDashboard() {
        log.info("Generating Admin Dashboard data...");

        AdminDashboardDTO adminDTO = new AdminDashboardDTO();

        adminDTO.setOverview(getSharedOverview());

        log.debug("Calculating financial metrics for Admin Dashboard...");
        Double todayRevenue = invoiceRepository.getTodayRevenue();
        Double thisMonthRevenue = invoiceRepository.getThisMonthRevenue();
        Double totalRevenue = invoiceRepository.getTotalRevenue();
        Long pendingInvoicesCount = invoiceRepository.countByPaymentStatus(PaymentStatus.UNPAID);
        Double totalUnpaidAmount = invoiceRepository.getTotalUnpaidAmount();

        adminDTO.setTodayRevenue(todayRevenue);
        adminDTO.setThisMonthRevenue(thisMonthRevenue);
        adminDTO.setTotalRevenue(totalRevenue);
        adminDTO.setPendingInvoicesCount(pendingInvoicesCount);
        adminDTO.setTotalUnpaidAmount(totalUnpaidAmount);

        // Spare Parts / Stock Alerts (Admin Only)
        log.debug("Fetching inventory stock alerts...");
        Long lowStockCount = sparePartRepository.countLowStockItems();
        Long outOfStockCount = sparePartRepository.countByQuantityInStockLessThanEqual(0);

        adminDTO.setLowStockCount(lowStockCount);
        adminDTO.setOutOfStockCount(outOfStockCount);

        log.info("Admin Dashboard data generated successfully. Total Revenue: {}, Pending Invoices: {}",
                totalRevenue, pendingInvoicesCount);

        return adminDTO;
    }

    @Override
    public AdvisorDashboardDTO getAdvisorDashboard() {
        log.info("Generating Advisor Dashboard data...");

        AdvisorDashboardDTO advisorDTO = new AdvisorDashboardDTO();
        advisorDTO.setOverview(getSharedOverview());

        log.info("Advisor Dashboard data generated successfully");
        return advisorDTO;
    }
}
