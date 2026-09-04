package ijse.lk.AutoCareManagement.service;

import ijse.lk.AutoCareManagement.dto.JobCardPartResponseDTO;

import java.util.List;

public interface JobCardPartService {

    JobCardPartResponseDTO issueJobCardPart(Long jobCardPartId);

    JobCardPartResponseDTO returnJobCardPart(Long jobCardPartId);

    JobCardPartResponseDTO rejectJobCardPart(Long jobCardPartId);

    List<JobCardPartResponseDTO> getPendingRequests();

    List<JobCardPartResponseDTO> getPartsByJobCardCode(String jobCardCode);
}
