package ijse.lk.AutoCareManagement.service;

import ijse.lk.AutoCareManagement.dto.InternalStockIssueRequestDTO;
import ijse.lk.AutoCareManagement.dto.InternalStockIssueResponseDTO;

import java.util.List;

public interface InternalStockIssueService {

    InternalStockIssueResponseDTO issueInternalStock(InternalStockIssueRequestDTO dto);

    InternalStockIssueResponseDTO getInternalIssueByCode(String internalPartCode);

    List<InternalStockIssueResponseDTO> getAllInternalStockIssues();

    List<InternalStockIssueResponseDTO> getInternalIssuesByEmployee(String employeeCode);

    List<InternalStockIssueResponseDTO> getInternalIssuesBySection(String sectionCode);

    long getTotalInternalIssuesCount();
}
