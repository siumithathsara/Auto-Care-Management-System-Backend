package ijse.lk.AutoCareManagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceCategoryRequestDTO {

    @NotBlank(message = "Category name is required")
    private String categoryName;

    private String description;
}
