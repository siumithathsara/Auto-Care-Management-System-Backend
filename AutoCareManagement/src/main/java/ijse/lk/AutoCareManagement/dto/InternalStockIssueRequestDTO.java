package ijse.lk.AutoCareManagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternalStockIssueRequestDTO {

    @NotNull(message = "Spare Part ID is required")
    private Long partId;

    @NotBlank(message = "Employee code is required")
    private String employeeCode;

    private String sectionCode;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @NotBlank(message = "Usage reason is required")
    private String usageReason;
}
