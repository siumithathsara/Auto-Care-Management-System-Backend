package ijse.lk.AutoCareManagement.controller;

import ijse.lk.AutoCareManagement.constant.CommonResponse;
import ijse.lk.AutoCareManagement.dto.JobCardPartRequestDTO;
import ijse.lk.AutoCareManagement.dto.JobCardRequestDTO;
import ijse.lk.AutoCareManagement.dto.JobCardResponseDTO;
import ijse.lk.AutoCareManagement.enumeration.JobStatus;
import ijse.lk.AutoCareManagement.service.JobCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/job-card")
@RequiredArgsConstructor
public class JobCardController {

    private final JobCardService jobCardService;

//    create job card
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR')")
    public CommonResponse createJobCard(@Valid @RequestBody JobCardRequestDTO dto) {
        JobCardResponseDTO response = jobCardService.createJobCard(dto);
        return new CommonResponse(201, response, "Job Card created successfully! Status set to IN_PROGRESS.");
    }

//   add spare part to job card
    @PostMapping(value = "/{jobCardCode}/add-parts", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR', 'MECHANIC')")
    public CommonResponse addPartsToJobCard(
            @PathVariable String jobCardCode,
            @Valid @RequestBody List<JobCardPartRequestDTO> partDTOs) {

        JobCardResponseDTO response = jobCardService.addPartsToJobCard(jobCardCode, partDTOs);
        return new CommonResponse(200, response, "Spare parts added to Job Card successfully!");
    }

//   get job card details bu using job card code
    @GetMapping(value = "/get-by-code/{jobCardCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR', 'CUSTOMER')")
    public CommonResponse getJobCardByCode(@PathVariable String jobCardCode) {
        JobCardResponseDTO response = jobCardService.getJobCardByCode(jobCardCode);
        return new CommonResponse(200, response, "Job Card fetched successfully!");
    }

// get all job cards
    @GetMapping(value = "/get-all", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR')")
    public CommonResponse getAllJobCards() {
        List<JobCardResponseDTO> list = jobCardService.getAllJobCards();
        return new CommonResponse(200, list, "All Job Cards fetched successfully!");
    }

// update status to IN_PROGRESS to COMPLETED
    @PatchMapping(value = "/change-status/{jobCardCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR')")
    public CommonResponse updateStatus(@PathVariable String jobCardCode, @RequestParam JobStatus status) {
        JobCardResponseDTO response = jobCardService.updateJobCardStatus(jobCardCode, status);
        return new CommonResponse(200, response, "Job Card status updated to " + status);
    }
}
