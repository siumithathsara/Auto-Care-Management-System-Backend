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

    private long jobCardPartId;
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

    public JobSectionResponseDTO(long sectionId, String sectionCode, String jobCardCode, String vehicleLicensePlate, Long supervisorId, String supervisorName, Long mechanicEmployeeId, String mechanicEmployeeCode, String mechanicEmployeeName, SectionName sectionName, SectionStatus sectionStatus, String remarks, LocalDateTime startedAt, LocalDateTime completedAt) {
        this.sectionId = sectionId;
        this.sectionCode = sectionCode;
        this.jobCardCode = jobCardCode;
        this.vehicleLicensePlate = vehicleLicensePlate;
        this.supervisorId = supervisorId;
        this.supervisorName = supervisorName;
        this.mechanicEmployeeId = mechanicEmployeeId;
        this.mechanicEmployeeCode = mechanicEmployeeCode;
        this.mechanicEmployeeName = mechanicEmployeeName;
        this.sectionName = sectionName;
        this.sectionStatus = sectionStatus;
        this.remarks = remarks;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }
}
