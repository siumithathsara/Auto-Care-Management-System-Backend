package ijse.lk.AutoCareManagement.controller;

import ijse.lk.AutoCareManagement.constant.CommonResponse;
import ijse.lk.AutoCareManagement.dto.InternalStockIssueRequestDTO;
import ijse.lk.AutoCareManagement.dto.InternalStockIssueResponseDTO;
import ijse.lk.AutoCareManagement.service.InternalStockIssueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/internal-stock-issue")
@RequiredArgsConstructor
public class InternalStockIssueController {

    private final InternalStockIssueService internalStockIssueService;

//         issue internal stock
    @PostMapping(value = "/issue", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse issueInternalStock(@Valid @RequestBody InternalStockIssueRequestDTO dto) {
        InternalStockIssueResponseDTO response = internalStockIssueService.issueInternalStock(dto);
        return new CommonResponse(201, response, "Internal stock issued successfully!");
    }

//         get internal issue by code
    @GetMapping(value = "/get-by-code/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR')")
    public CommonResponse getInternalIssueByCode(@PathVariable String code) {
        InternalStockIssueResponseDTO response = internalStockIssueService.getInternalIssueByCode(code);
        return new CommonResponse(200, response, "Internal stock issue fetched successfully!");
    }

//         get all internal stock issues
    @GetMapping(value = "/get-all", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR')")
    public CommonResponse getAllInternalStockIssues() {
        List<InternalStockIssueResponseDTO> responseList = internalStockIssueService.getAllInternalStockIssues();
        return new CommonResponse(200, responseList, "All internal stock issues fetched successfully!");
    }

//         get internal issues by employee
    @GetMapping(value = "/get-by-employee/{employeeCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN',  'ADVISOR')")
    public CommonResponse getInternalIssuesByEmployee(@PathVariable String employeeCode) {
        List<InternalStockIssueResponseDTO> responseList = internalStockIssueService.getInternalIssuesByEmployee(employeeCode);
        return new CommonResponse(200, responseList, "Internal stock issues by employee fetched successfully!");
    }

//         get internal issues by section
    @GetMapping(value = "/get-by-section/{sectionCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR')")
    public CommonResponse getInternalIssuesBySection(@PathVariable String sectionCode) {
        List<InternalStockIssueResponseDTO> responseList = internalStockIssueService.getInternalIssuesBySection(sectionCode);
        return new CommonResponse(200, responseList, "Internal stock issues by section fetched successfully!");
    }

//         get total internal issues count
    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR')")
    public CommonResponse getTotalInternalIssuesCount() {
        long count = internalStockIssueService.getTotalInternalIssuesCount();
        return new CommonResponse(200, count, "Total internal stock issues count fetched successfully!");
    }
}
