package ijse.lk.AutoCareManagement.dto;

import ijse.lk.AutoCareManagement.enumeration.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobCardServiceResponseDTO {

    private String serviceCode;
    private String serviceName;
    private double price;
    private ApprovalStatus approvalStatus;
}
