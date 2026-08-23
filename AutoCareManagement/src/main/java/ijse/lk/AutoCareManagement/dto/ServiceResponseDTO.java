package ijse.lk.AutoCareManagement.dto;

import ijse.lk.AutoCareManagement.enumeration.DataStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponseDTO {

    private long serviceId;
    private String serviceCode;
    private String serviceName;
    private String description;
    private double standardFee;
    private int estimatedTimeMin;
    private DataStatus dataStatus;

    private String categoryCode;
    private String categoryName;
}
