package ijse.lk.AutoCareManagement.controller;


import ijse.lk.AutoCareManagement.constant.CommonResponse;
import ijse.lk.AutoCareManagement.dto.JobCardPartResponseDTO;
import ijse.lk.AutoCareManagement.service.JobCardPartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/job-card-part")
@RequiredArgsConstructor
public class JobCardPartController {

    private final JobCardPartService jobCardPartService;

//    issue spare part for job card(stock auto deduct)
    @PatchMapping(value = "/issue/{jobCardPartId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse issuePart(@PathVariable Long jobCardPartId) {
        JobCardPartResponseDTO response = jobCardPartService.issueJobCardPart(jobCardPartId);
        return new CommonResponse(200, response, "Spare Part issued successfully and stock updated!");
    }

//    return spare part for job card(stock auto restore)
    @PatchMapping(value = "/return/{jobCardPartId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse returnPart(@PathVariable Long jobCardPartId) {
        JobCardPartResponseDTO response = jobCardPartService.returnJobCardPart(jobCardPartId);
        return new CommonResponse(200, response, "Spare Part returned successfully and stock restored!");
    }

//    reject spare part request for job card
    @PatchMapping(value = "/reject/{jobCardPartId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse rejectPart(@PathVariable Long jobCardPartId) {
        JobCardPartResponseDTO response = jobCardPartService.rejectJobCardPart(jobCardPartId);
        return new CommonResponse(200, response, "Spare Part request rejected successfully!");
    }
//    get pending requests for storekeeper
    @GetMapping(value = "/pending-requests", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse getPendingRequests() {
        List<JobCardPartResponseDTO> list = jobCardPartService.getPendingRequests();
        return new CommonResponse(200, list, "Pending part requests fetched successfully!");
    }

//     get all parts for a specific job card
    @GetMapping(value = "/get-by-job-card/{jobCardCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR')")
    public CommonResponse getPartsByJobCardCode(@PathVariable String jobCardCode) {
        List<JobCardPartResponseDTO> list = jobCardPartService.getPartsByJobCardCode(jobCardCode);
        return new CommonResponse(200, list, "Job Card Parts fetched successfully!");
    }
}
