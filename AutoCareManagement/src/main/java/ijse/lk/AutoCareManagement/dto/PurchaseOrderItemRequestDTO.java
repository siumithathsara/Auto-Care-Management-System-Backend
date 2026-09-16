package ijse.lk.AutoCareManagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderItemRequestDTO {

    @NotBlank(message = "Part code is required")
    private String partCode;

    @NotNull(message = "Ordered quantity is required")
    @Min(value = 1, message = "Ordered quantity must be at least 1")
    private int orderedQty;

    @NotNull(message = "Unit cost is required")
    @Positive(message = "Unit cost must be greater than zero")
    private double unitCost;
}
