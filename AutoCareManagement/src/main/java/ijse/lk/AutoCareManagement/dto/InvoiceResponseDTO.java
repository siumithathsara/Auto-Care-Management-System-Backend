package ijse.lk.AutoCareManagement.dto;

import ijse.lk.AutoCareManagement.enumeration.PaymentMethod;
import ijse.lk.AutoCareManagement.enumeration.PaymentStatus;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponseDTO {

    private long invoiceId;
    private String invoiceCode;
    private String jobCardCode;

    private String vehicleCode;
    private String licensePlate;
    private String brand;
    private String model;
    private String customerUserCode;
    private String customerUsername;
    private String customerPhone;
    private String customerEmail;

    private String issuedByUsername;
    private double subtotal;
    private Double taxAmount;
    private Double discount;
    private double totalAmount;
    private double paidAmount;
    private double balanceAmount;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private LocalDateTime issuedDate;
}
