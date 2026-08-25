package ijse.lk.AutoCareManagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobCardRequestDTO {

    @NotBlank(message = "Vehicle Code is required")
    private String vehicleCode;

    @NotBlank(message = "Advisor User Code is required")
    private String advisorUserCode;

    private String appointmentCode;

    @Min(value = 0, message = "Mileage must be a positive number")
    private int mileageIn;

    @NotBlank(message = "Fuel level is required")
    private String fuelLevel;

    private String customerNotes;
    private String advisorNotes;

    @NotNull(message = "Estimated completion time is required")
    private LocalDateTime estimatedCompletionTime;

    @NotEmpty(message = "At least one service must be selected")
    private List<String> serviceCodes;
}
