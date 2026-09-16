package ijse.lk.AutoCareManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderItemResponseDTO {

    private String partCode;
    private String partName;
    private int orderedQty;
    private int receivedQty;
    private double unitCost;
    private double subTotal;
}
