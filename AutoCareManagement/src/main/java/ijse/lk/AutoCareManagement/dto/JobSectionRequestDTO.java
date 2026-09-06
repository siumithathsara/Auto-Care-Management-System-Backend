package ijse.lk.AutoCareManagement.dto;

import ijse.lk.AutoCareManagement.enumeration.SectionName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobSectionRequestDTO {

    @NotBlank(message = "Job Card Code is required.")
    private String jobCardCode;

    @NotNull(message = "Assigned By User ID is required.")
    private Long assignedByUserId;

    @NotNull(message = "Mechanic Employee ID is required.")
    private Long mechanicEmployeeId;

    @NotNull(message = "Section Name is required.")
    private SectionName sectionName;

    private String remarks;
}
