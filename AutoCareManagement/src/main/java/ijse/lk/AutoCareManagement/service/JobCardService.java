package ijse.lk.AutoCareManagement.service;

import ijse.lk.AutoCareManagement.dto.JobCardPartRequestDTO;
import ijse.lk.AutoCareManagement.dto.JobCardRequestDTO;
import ijse.lk.AutoCareManagement.dto.JobCardResponseDTO;
import ijse.lk.AutoCareManagement.enumeration.JobStatus;

import java.util.List;

public interface JobCardService {

    JobCardResponseDTO createJobCard(JobCardRequestDTO dto);

    JobCardResponseDTO addPartsToJobCard(String jobCardCode, List<JobCardPartRequestDTO> partDTOs);

    JobCardResponseDTO getJobCardByCode(String jobCardCode);

    List<JobCardResponseDTO> getAllJobCards();

    JobCardResponseDTO updateJobCardStatus(String jobCardCode, JobStatus status);
}
