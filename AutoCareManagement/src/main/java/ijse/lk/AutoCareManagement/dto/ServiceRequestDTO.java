package ijse.lk.AutoCareManagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRequestDTO {

    @NotBlank(message = "Service name is required")
    private String serviceName;

    private String description;

    @NotNull(message = "Standard fee is required")
    @Positive(message = "Standard fee must be greater than zero")
    private Double standardFee;

    @NotNull(message = "Estimated time is required")
    @Positive(message = "Estimated time must be greater than zero")
    private Integer estimatedTimeMins;

    @NotBlank(message = "Category code is required")
    private String categoryCode;
}
