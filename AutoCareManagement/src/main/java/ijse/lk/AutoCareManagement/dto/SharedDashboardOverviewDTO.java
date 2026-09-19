package ijse.lk.AutoCareManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SharedDashboardOverviewDTO {

    private Long activeJobCards;
    private Long completedJobCards;
    private Long vehiclesInWorkshop;

    private Map<String, Long> jobCardStatusBreakdown;
    private List<Object> recentJobCards;
    private List<Object> vehiclesReadyForCheckout;
}
