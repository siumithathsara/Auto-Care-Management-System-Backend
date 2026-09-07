package ijse.lk.AutoCareManagement.dto;

import ijse.lk.AutoCareManagement.enumeration.SectionName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternalStockIssueResponseDTO {

    private long issueId;
    private String internalPartCode;

    private Long partId;
    private String partCode;
    private String partName;

    private String employeeCode;
    private String employeeName;

    private String userCode;
    private String issuedByUserName;

    private String sectionCode;
    private SectionName sectionName;

    private int quantity;
    private double unitCost;
    private double totalCost;
    private String usageReason;
    private LocalDateTime issuedAt;
}
