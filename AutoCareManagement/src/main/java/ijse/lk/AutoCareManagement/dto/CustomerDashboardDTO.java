package ijse.lk.AutoCareManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDashboardDTO {

    // Overview Stats
    private long totalVehicles;
    private long activeJobCards;
    private long completedJobCards;
    private double pendingPaymentAmount;

    // Lists
    private List<AppointmentSummaryDTO> approvedAppointments;
    private List<JobCardSummaryDTO> activeJobsList;
    private List<JobCardSummaryDTO> recentJobHistoryList;
}
