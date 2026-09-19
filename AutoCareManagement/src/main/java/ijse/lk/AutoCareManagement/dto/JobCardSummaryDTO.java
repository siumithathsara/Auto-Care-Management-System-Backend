package ijse.lk.AutoCareManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobCardSummaryDTO {

    private String jobCardCode;
    private String status;
    private LocalDateTime checkInTime;
    private LocalDateTime estimatedCompletionTime;
    private String licensePlate;
}
