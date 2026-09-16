package ijse.lk.AutoCareManagement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderRequestDTO {

    @NotBlank(message = "Supplier code is required")
    private String supplierCode;

    @FutureOrPresent(message = "Expected delivery date must be present or in the future")
    private LocalDateTime expectedDeliveryDate;

    @NotEmpty(message = "At least one purchase order item must be added")
    @Valid
    private List<PurchaseOrderItemRequestDTO> items;
}
