package ijse.lk.AutoCareManagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobCardPartRequestDTO {

    @NotBlank(message = "Part Code is required")
    private String partCode;

    @NotBlank(message = "Requested by User Code is required")
    private String requestedByUserCode;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}
