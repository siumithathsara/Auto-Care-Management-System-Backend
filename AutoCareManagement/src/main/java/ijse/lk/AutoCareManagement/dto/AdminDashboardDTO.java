package ijse.lk.AutoCareManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardDTO {

    // Financial Metrics (Admin Only)
    private Double todayRevenue;
    private Double thisWeekRevenue;
    private Double thisMonthRevenue;
    private Double totalRevenue;

    private Long pendingInvoicesCount;
    private Double totalUnpaidAmount;

    // Charts & Analytics (Admin Only)
    private Map<String, Double> monthlyRevenueChart;
    private Map<String, Long> paymentStatusBreakdown;
    private Map<String, Long> paymentMethodBreakdown;

    // Inventory Analytics (Admin Only)
    private Long lowStockCount;
    private Long outOfStockCount;

    // Shared Overview (Both Admin & Advisor)
    private SharedDashboardOverviewDTO overview;
}
