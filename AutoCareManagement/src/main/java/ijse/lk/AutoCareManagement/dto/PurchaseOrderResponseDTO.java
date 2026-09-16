package ijse.lk.AutoCareManagement.dto;

import ijse.lk.AutoCareManagement.enumeration.PoStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderResponseDTO {

    private String poCode;
    private String supplierCode;
    private String supplierName;
    private String createdByUserCode;
    private String createdByUserName;
    private double totalAmount;
    private LocalDateTime orderDate;
    private LocalDateTime expectedDeliveryDate;
    private PoStatus status;
    private List<PurchaseOrderItemResponseDTO> items;
}
