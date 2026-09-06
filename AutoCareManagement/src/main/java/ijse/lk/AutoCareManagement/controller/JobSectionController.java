package ijse.lk.AutoCareManagement.controller;

import ijse.lk.AutoCareManagement.constant.CommonResponse;
import ijse.lk.AutoCareManagement.dto.JobSectionRequestDTO;
import ijse.lk.AutoCareManagement.dto.JobSectionResponseDTO;
import ijse.lk.AutoCareManagement.enumeration.SectionStatus;
import ijse.lk.AutoCareManagement.service.JobSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/job-section")
@RequiredArgsConstructor
public class JobSectionController {

    private final JobSectionService jobSectionService;

//     assign job section
    @PostMapping(value = "/assign", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN','ADVISOR')")
    public CommonResponse assignJobSection(@Valid @RequestBody JobSectionRequestDTO jobSectionRequestDTO) {
        JobSectionResponseDTO responseDTO = jobSectionService.assignJobSection(jobSectionRequestDTO);
        return new CommonResponse(201, responseDTO, "Job Section assigned successfully!");
    }

//     start job section
    @PutMapping(value = "/start/{sectionCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN','ADVISOR')")
    public CommonResponse startJobSection(@PathVariable String sectionCode) {
        JobSectionResponseDTO responseDTO = jobSectionService.startJobSection(sectionCode);
        return new CommonResponse(200, responseDTO, "Job Section started successfully!");
    }

//     complete job section
    @PutMapping(value = "/complete/{sectionCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR')")
    public CommonResponse completeJobSection(@PathVariable String sectionCode,
                                             @RequestParam(value = "remarks", required = false) String remarks) {
        JobSectionResponseDTO responseDTO = jobSectionService.completeJobSection(sectionCode, remarks);
        return new CommonResponse(200, responseDTO, "Job Section completed successfully!");
    }

//     get job section by code
    @GetMapping(value = "/get-by-code/{sectionCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN','ADVISOR')")
    public CommonResponse getJobSectionByCode(@PathVariable String sectionCode) {
        JobSectionResponseDTO responseDTO = jobSectionService.getJobSectionByCode(sectionCode);
        return new CommonResponse(200, responseDTO, "Job Section fetched successfully!");
    }

//     get sections by job card code
    @GetMapping(value = "/get-by-job-card/{jobCardCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN','ADVISOR', 'CUSTOMER')")
    public CommonResponse getSectionsByJobCardCode(@PathVariable String jobCardCode) {
        List<JobSectionResponseDTO> sections = jobSectionService.getSectionsByJobCardCode(jobCardCode);
        return new CommonResponse(200, sections, "Job Card sections fetched successfully!");
    }

//     get sections by mechanic employee id
    @GetMapping(value = "/get-by-mechanic/{employeeCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN','ADVISOR')")
    public CommonResponse getSectionsByMechanic(@PathVariable String employeeCode) {
        List<JobSectionResponseDTO> sections = jobSectionService.getSectionsByMechanic(employeeCode);
        return new CommonResponse(200, sections, "Mechanic sections fetched successfully!");
    }

//     get sections by status
    @GetMapping(value = "/get-by-status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN','ADVISOR')")
    public CommonResponse getSectionsByStatus(@PathVariable SectionStatus status) {
        List<JobSectionResponseDTO> sections = jobSectionService.getSectionsByStatus(status);
        return new CommonResponse(200, sections, "Sections fetched by status successfully!");
    }
}
