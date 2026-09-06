package ijse.lk.AutoCareManagement.dto;

import ijse.lk.AutoCareManagement.enumeration.SectionName;
import ijse.lk.AutoCareManagement.enumeration.SectionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobSectionResponseDTO {

    private long sectionId;
    private String sectionCode;

    private String jobCardCode;
    private String vehicleLicensePlate;

    private Long supervisorId;
    private String supervisorName;

    private Long mechanicEmployeeId;
    private String mechanicEmployeeCode;
    private String mechanicEmployeeName;

    private SectionName sectionName;
    private SectionStatus sectionStatus;
    private String remarks;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
