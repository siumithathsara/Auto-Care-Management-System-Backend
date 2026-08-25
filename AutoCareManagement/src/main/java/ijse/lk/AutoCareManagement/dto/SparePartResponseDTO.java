package ijse.lk.AutoCareManagement.dto;

import ijse.lk.AutoCareManagement.enumeration.DataStatus;
import ijse.lk.AutoCareManagement.enumeration.PartType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SparePartResponseDTO {

    private long partId;
    private String partCode;
    private String partName;
    private String brand;
    private double costPrice;
    private double unitPrice;
    private int quantityInStock;
    private int reorderLevel;
    private PartType partType;
    private DataStatus dataStatus;
}
