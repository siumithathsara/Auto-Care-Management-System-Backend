package ijse.lk.AutoCareManagement.dto;

import ijse.lk.AutoCareManagement.enumeration.PartType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SparePartRequestDTO {

    @NotBlank(message = "Part name is required")
    private String partName;

    private String brand;

    @NotNull(message = "Cost price is required")
    @Min(value = 0, message = "Cost price cannot be negative")
    private double costPrice;

    @NotNull(message = "Unit price is required")
    @Min(value = 0, message = "Unit price cannot be negative")
    private double unitPrice;

    @NotNull(message = "Quantity in stock is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private int quantityInStock;

    @NotNull(message = "Reorder level is required")
    @Min(value = 0, message = "Reorder level cannot be negative")
    private int reorderLevel;

    @NotNull(message = "Part type is required")
    private PartType partType;
}
