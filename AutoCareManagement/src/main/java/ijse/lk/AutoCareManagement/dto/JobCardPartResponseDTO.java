package ijse.lk.AutoCareManagement.dto;

import ijse.lk.AutoCareManagement.enumeration.IssueStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobCardPartResponseDTO {

    private String partCode;
    private String partName;
    private String requestedByName;
    private int quantity;
    private double unitPrice;
    private double subTotal;
    private IssueStatus issueStatus;
}
