package ijse.lk.AutoCareManagement.service;

import ijse.lk.AutoCareManagement.dto.JobSectionRequestDTO;
import ijse.lk.AutoCareManagement.dto.JobSectionResponseDTO;
import ijse.lk.AutoCareManagement.enumeration.SectionStatus;

import java.util.List;

public interface JobSectionService {

    JobSectionResponseDTO assignJobSection(JobSectionRequestDTO dto);

    JobSectionResponseDTO startJobSection(String sectionCode);

    JobSectionResponseDTO completeJobSection(String sectionCode, String remarks);

    JobSectionResponseDTO getJobSectionByCode(String sectionCode);

    List<JobSectionResponseDTO> getSectionsByJobCardCode(String jobCardCode);

    List<JobSectionResponseDTO> getSectionsByMechanic(String employeeCode);

    List<JobSectionResponseDTO> getSectionsByStatus(SectionStatus status);
}
