package ijse.lk.AutoCareManagement.service.impl;

import ijse.lk.AutoCareManagement.dto.JobSectionRequestDTO;
import ijse.lk.AutoCareManagement.dto.JobSectionResponseDTO;
import ijse.lk.AutoCareManagement.entity.Employee;
import ijse.lk.AutoCareManagement.entity.JobCard;
import ijse.lk.AutoCareManagement.entity.JobSection;
import ijse.lk.AutoCareManagement.entity.User;
import ijse.lk.AutoCareManagement.enumeration.SectionStatus;
import ijse.lk.AutoCareManagement.exception.CustomException;
import ijse.lk.AutoCareManagement.repository.EmployeeRepository;
import ijse.lk.AutoCareManagement.repository.JobCardRepository;
import ijse.lk.AutoCareManagement.repository.JobSectionRepository;
import ijse.lk.AutoCareManagement.repository.UserRepository;
import ijse.lk.AutoCareManagement.service.JobSectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JobSectionServiceImpl implements JobSectionService {
    private final JobSectionRepository jobSectionRepository;
    private final JobCardRepository jobCardRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public JobSectionResponseDTO assignJobSection(JobSectionRequestDTO jobSectionRequestDTO) {
        log.info("Assigning new Job Section for JobCardCode: {} and SectionName: {}",
                jobSectionRequestDTO.getJobCardCode(), jobSectionRequestDTO.getSectionName());

        Optional<JobCard> jobCardOptional = jobCardRepository.findByJobCardCode(jobSectionRequestDTO.getJobCardCode());
        if (jobCardOptional.isEmpty()) {
            log.warn("Job Section assignment failed: Job Card '{}' not found", jobSectionRequestDTO.getJobCardCode());
            throw new CustomException(404, "Job Card with code '" + jobSectionRequestDTO.getJobCardCode() + "' not found!");
        }

        Optional<User> supervisorOptional = userRepository.findById(jobSectionRequestDTO.getAssignedByUserId());
        if (supervisorOptional.isEmpty()) {
            log.warn("Job Section assignment failed: Supervisor with ID '{}' not found", jobSectionRequestDTO.getAssignedByUserId());
            throw new CustomException(404, "Supervisor with ID '" + jobSectionRequestDTO.getAssignedByUserId() + "' not found!");
        }

        Optional<Employee> mechanicOptional = employeeRepository.findById(jobSectionRequestDTO.getMechanicEmployeeId());
        if (mechanicOptional.isEmpty()) {
            log.warn("Job Section assignment failed: Mechanic Employee with ID '{}' not found", jobSectionRequestDTO.getMechanicEmployeeId());
            throw new CustomException(404, "Mechanic Employee with ID '" + jobSectionRequestDTO.getMechanicEmployeeId() + "' not found!");
        }

        if (jobSectionRepository.existsByJobCard_JobCardCodeAndSectionName(jobSectionRequestDTO.getJobCardCode(), jobSectionRequestDTO.getSectionName())) {
            log.warn("Job Section assignment failed: Section '{}' is already assigned to JobCard '{}'",
                    jobSectionRequestDTO.getSectionName(), jobSectionRequestDTO.getJobCardCode());
            throw new CustomException(400, "Section '" + jobSectionRequestDTO.getSectionName() + "' is already assigned to this Job Card!");
        }

        String generatedSectionCode = generateSectionCode();

        JobSection jobSection = new JobSection();
        jobSection.setSectionCode(generatedSectionCode);
        jobSection.setJobCard(jobCardOptional.get());
        jobSection.setAssignedByUser(supervisorOptional.get());
        jobSection.setMechanicEmployee(mechanicOptional.get());
        jobSection.setSectionName(jobSectionRequestDTO.getSectionName());
        jobSection.setSectionStatus(SectionStatus.PENDING);
        jobSection.setRemarks(jobSectionRequestDTO.getRemarks());

        JobSection savedSection = jobSectionRepository.save(jobSection);
        log.info("Job Section successfully assigned with code: {} for JobCard: {}", generatedSectionCode, jobSectionRequestDTO.getJobCardCode());

        return mapToResponseDTO(savedSection);
    }

    @Override
    public JobSectionResponseDTO startJobSection(String sectionCode) {
        log.info("Starting Job Section with code: {}", sectionCode);

        Optional<JobSection> jobSectionOptional = jobSectionRepository.findBySectionCode(sectionCode);
        if (jobSectionOptional.isEmpty()) {
            log.warn("Job Section with code '{}' not found to start", sectionCode);
            throw new CustomException(404, "Job Section with code '" + sectionCode + "' not found!");
        }

        JobSection jobSection = jobSectionOptional.get();

        if (jobSection.getSectionStatus() != SectionStatus.PENDING) {
            log.warn("Cannot start Job Section '{}': Current status is {}", sectionCode, jobSection.getSectionStatus());
            throw new CustomException(400, "Job Section can only be started when it is in PENDING status!");
        }

        jobSection.setSectionStatus(SectionStatus.IN_PROGRESS);
        jobSection.setStartedAt(LocalDateTime.now());

        JobSection updatedSection = jobSectionRepository.save(jobSection);
        log.info("Job Section successfully started with code: {}", sectionCode);

        return mapToResponseDTO(updatedSection);
    }

    @Override
    public JobSectionResponseDTO completeJobSection(String sectionCode, String remarks) {
        log.info("Completing Job Section with code: {}", sectionCode);

        Optional<JobSection> jobSectionOptional = jobSectionRepository.findBySectionCode(sectionCode);
        if (jobSectionOptional.isEmpty()) {
            log.warn("Job Section with code '{}' not found to complete", sectionCode);
            throw new CustomException(404, "Job Section with code '" + sectionCode + "' not found!");
        }

        JobSection jobSection = jobSectionOptional.get();

        if (jobSection.getSectionStatus() != SectionStatus.IN_PROGRESS) {
            log.warn("Cannot complete Job Section '{}': Current status is {}", sectionCode, jobSection.getSectionStatus());
            throw new CustomException(400, "Job Section can only be completed when it is IN_PROGRESS!");
        }

        jobSection.setSectionStatus(SectionStatus.COMPLETED);
        jobSection.setCompletedAt(LocalDateTime.now());

        if (remarks != null && !remarks.trim().isEmpty()) {
            jobSection.setRemarks(remarks);
        }

        JobSection updatedSection = jobSectionRepository.save(jobSection);
        log.info("Job Section successfully completed with code: {}", sectionCode);

        return mapToResponseDTO(updatedSection);
    }

    @Override
    @Transactional(readOnly = true)
    public JobSectionResponseDTO getJobSectionByCode(String sectionCode) {
        log.info("Fetching Job Section with code: {}", sectionCode);

        Optional<JobSection> jobSectionOptional = jobSectionRepository.findBySectionCode(sectionCode);
        if (jobSectionOptional.isEmpty()) {
            log.warn("Job Section with code '{}' not found", sectionCode);
            throw new CustomException(404, "Job Section with code '" + sectionCode + "' not found!");
        }

        return mapToResponseDTO(jobSectionOptional.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobSectionResponseDTO> getSectionsByJobCardCode(String jobCardCode) {
        log.info("Fetching Job Sections for JobCardCode: {}", jobCardCode);

        List<JobSection> sections = jobSectionRepository.findByJobCard_JobCardCode(jobCardCode);
        List<JobSectionResponseDTO> responseDTOList = mapToResponseDTOList(sections);

        log.info("Successfully retrieved {} Job Sections for JobCardCode: {}", responseDTOList.size(), jobCardCode);
        return responseDTOList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobSectionResponseDTO> getSectionsByMechanic(String employeeCode) {
        log.info("Fetching Job Sections for mechanic employeeCode: {}", employeeCode);

        List<JobSection> sections = jobSectionRepository.findByMechanicEmployee_EmployeeCode(employeeCode);
        List<JobSectionResponseDTO> responseDTOList = mapToResponseDTOList(sections);

        log.info("Successfully retrieved {} Job Sections for mechanic employeeCode: {}", responseDTOList.size(), employeeCode);
        return responseDTOList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobSectionResponseDTO> getSectionsByStatus(SectionStatus status) {
        log.info("Fetching Job Sections with status: {}", status);

        List<JobSection> sections = jobSectionRepository.findBySectionStatus(status);
        List<JobSectionResponseDTO> responseDTOList = mapToResponseDTOList(sections);

        log.info("Successfully retrieved {} Job Sections with status: {}", responseDTOList.size(), status);
        return responseDTOList;
    }

    private String generateSectionCode() {
        long currentCount = jobSectionRepository.getJobSectionCount();
        long nextNumber = currentCount + 1;
        int currentYear = Year.now().getValue();

        String generatedCode = String.format("SEC-%d-%04d", currentYear, nextNumber);

        while (jobSectionRepository.findBySectionCode(generatedCode).isPresent()) {
            nextNumber++;
            generatedCode = String.format("SEC-%d-%04d", currentYear, nextNumber);
        }

        log.debug("Generated unique Job Section code: {}", generatedCode);
        return generatedCode;
    }

    private JobSectionResponseDTO mapToResponseDTO(JobSection entity) {
        JobSectionResponseDTO dto = new JobSectionResponseDTO();
        dto.setSectionId(entity.getSectionId());
        dto.setSectionCode(entity.getSectionCode());

        if (entity.getJobCard() != null) {
            dto.setJobCardCode(entity.getJobCard().getJobCardCode());
            if (entity.getJobCard().getVehicle() != null) {
                dto.setVehicleLicensePlate(entity.getJobCard().getVehicle().getLicensePlate());
            }
        }

        if (entity.getAssignedByUser() != null) {
            dto.setSupervisorId(entity.getAssignedByUser().getUserId());
            dto.setSupervisorName(entity.getAssignedByUser().getUsername());
        }

        if (entity.getMechanicEmployee() != null) {
            dto.setMechanicEmployeeId(entity.getMechanicEmployee().getEmployeeId());
            dto.setMechanicEmployeeCode(entity.getMechanicEmployee().getEmployeeCode());
            dto.setMechanicEmployeeName(entity.getMechanicEmployee().getEmployeeName());
        }

        dto.setSectionName(entity.getSectionName());
        dto.setSectionStatus(entity.getSectionStatus());
        dto.setRemarks(entity.getRemarks());
        dto.setStartedAt(entity.getStartedAt());
        dto.setCompletedAt(entity.getCompletedAt());

        return dto;
    }

    private List<JobSectionResponseDTO> mapToResponseDTOList(List<JobSection> sections) {
        List<JobSectionResponseDTO> list = new ArrayList<>();
        for (JobSection section : sections) {
            list.add(mapToResponseDTO(section));
        }
        return list;
    }
}
