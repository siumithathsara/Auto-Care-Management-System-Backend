package ijse.lk.AutoCareManagement.dto;

import ijse.lk.AutoCareManagement.enumeration.DataStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceCategoryResponseDTO {

    private long categoryId;
    private String categoryCode;
    private String categoryName;
    private String description;
    private DataStatus status;
}
