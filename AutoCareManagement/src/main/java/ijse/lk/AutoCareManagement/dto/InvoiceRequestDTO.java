package ijse.lk.AutoCareManagement.dto;

import ijse.lk.AutoCareManagement.enumeration.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceRequestDTO {

    @NotBlank(message = "Job Card Code is required")
    private String jobCardCode;

    @Min(value = 0, message = "Tax percentage cannot be negative")
    private Double taxPercentage;

    @Min(value = 0, message = "Discount percentage cannot be negative")
    private Double discountPercentage;

    @Min(value = 0, message = "Paid amount cannot be negative")
    private Double paidAmount;

    private PaymentMethod paymentMethod;
}
