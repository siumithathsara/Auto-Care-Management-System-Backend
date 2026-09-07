package ijse.lk.AutoCareManagement.service.impl;

import ijse.lk.AutoCareManagement.dto.InternalStockIssueRequestDTO;
import ijse.lk.AutoCareManagement.dto.InternalStockIssueResponseDTO;
import ijse.lk.AutoCareManagement.entity.*;
import ijse.lk.AutoCareManagement.exception.CustomException;
import ijse.lk.AutoCareManagement.repository.*;
import ijse.lk.AutoCareManagement.service.InternalStockIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InternalStockIssueServiceImpl implements InternalStockIssueService {

    private final InternalStockIssueRepository internalStockIssueRepository;
    private final SparePartRepository sparePartRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final JobSectionRepository jobSectionRepository;

    @Override
    @Transactional
    public InternalStockIssueResponseDTO issueInternalStock(InternalStockIssueRequestDTO dto) {
        log.info("Processing internal stock issue request for Part ID: {}", dto.getPartId());

        // 1. Spare Part පරීක්ෂා කිරීම
        Optional<SparePart> sparePartOptional = sparePartRepository.findById(dto.getPartId());
        if (sparePartOptional.isEmpty()) {
            log.warn("Internal stock issue failed: Spare Part ID '{}' not found", dto.getPartId());
            throw new CustomException(404, "Spare Part with ID '" + dto.getPartId() + "' not found");
        }
        SparePart sparePart = sparePartOptional.get();

        // 2. Stock ප්‍රමාණය පරීක්ෂා කිරීම
        if (sparePart.getQuantityInStock() < dto.getQuantity()) {
            log.warn("Stock issue failed: Insufficient stock for Part ID '{}'. Available: {}, Requested: {}",
                    dto.getPartId(), sparePart.getQuantityInStock(), dto.getQuantity());
            throw new CustomException(400, "Insufficient stock available! Current stock: "
                    + sparePart.getQuantityInStock() + ", Requested: " + dto.getQuantity());
        }

        // 3. Employee පරීක්ෂා කිරීම
        Optional<Employee> employeeOptional = employeeRepository.findByEmployeeCode(dto.getEmployeeCode());
        if (employeeOptional.isEmpty()) {
            log.warn("Internal stock issue failed: Employee code '{}' not found", dto.getEmployeeCode());
            throw new CustomException(404, "Employee with code '" + dto.getEmployeeCode() + "' not found");
        }

        // 4. Currently Logged-in User ව SecurityContext එකෙන් Auto-fetch කිරීම
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<User> userOptional = userRepository.findByUsername(currentUsername);
        if (userOptional.isEmpty()) {
            log.warn("Internal stock issue failed: User '{}' not found in database", currentUsername);
            throw new CustomException(404, "Authenticated user not found with username: " + currentUsername);
        }
        User loggedInUser = userOptional.get();

        // 5. Job Section පරීක්ෂා කිරීම (Optional)
        JobSection jobSection = null;
        if (dto.getSectionCode() != null && !dto.getSectionCode().trim().isEmpty()) {
            Optional<JobSection> jobSectionOptional = jobSectionRepository.findBySectionCode(dto.getSectionCode());
            if (jobSectionOptional.isEmpty()) {
                log.warn("Internal stock issue failed: Job Section code '{}' not found", dto.getSectionCode());
                throw new CustomException(404, "Job Section with code '" + dto.getSectionCode() + "' not found");
            }
            jobSection = jobSectionOptional.get();
        }

        // 6. Stock Update කිරීම
        int updatedStock = sparePart.getQuantityInStock() - dto.getQuantity();
        sparePart.setQuantityInStock(updatedStock);
        sparePartRepository.save(sparePart);
        log.info("Stock successfully updated for Part ID: {}. New Stock: {}", sparePart.getPartId(), updatedStock);

        if (updatedStock <= sparePart.getReorderLevel()) {
            log.warn("LOW STOCK ALERT! Part Code: {}, Name: {}, Remaining Stock: {}, Reorder Level: {}",
                    sparePart.getPartCode(), sparePart.getPartName(), updatedStock, sparePart.getReorderLevel());
        }

        // 7. Internal Stock Issue Entity එක Save කිරීම
        String generatedCode = generateNextInternalPartCode();

        InternalStockIssue issue = new InternalStockIssue();
        issue.setInternalPartCode(generatedCode);
        issue.setSparePart(sparePart);
        issue.setIssuedToEmployee(employeeOptional.get());
        issue.setIssuedByUser(loggedInUser); // Security Context එකෙන් Auto-select වූ User
        issue.setJobSection(jobSection);
        issue.setQuantity(dto.getQuantity());
        issue.setUnitCost(sparePart.getUnitPrice());
        issue.setUsageReason(dto.getUsageReason());

        InternalStockIssue savedIssue = internalStockIssueRepository.save(issue);
        log.info("Internal stock issue successfully recorded with code: {}", generatedCode);

        return mapToResponseDTO(savedIssue);
    }

    @Override
    @Transactional(readOnly = true)
    public InternalStockIssueResponseDTO getInternalIssueByCode(String internalPartCode) {
        log.info("Fetching internal stock issue with code: {}", internalPartCode);

        Optional<InternalStockIssue> issueOptional = internalStockIssueRepository.findByInternalPartCode(internalPartCode);
        if (issueOptional.isEmpty()) {
            log.warn("Internal stock issue with code '{}' not found", internalPartCode);
            throw new CustomException(404, "Internal Stock Issue record with code '" + internalPartCode + "' not found");
        }

        return mapToResponseDTO(issueOptional.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InternalStockIssueResponseDTO> getAllInternalStockIssues() {
        log.info("Fetching all internal stock issues");

        List<InternalStockIssue> issues = internalStockIssueRepository.findAll();
        List<InternalStockIssueResponseDTO> responseDTOList = new ArrayList<>();

        for (InternalStockIssue issue : issues) {
            responseDTOList.add(mapToResponseDTO(issue));
        }

        log.info("Successfully retrieved {} internal stock issues", responseDTOList.size());
        return responseDTOList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InternalStockIssueResponseDTO> getInternalIssuesByEmployee(String employeeCode) {
        log.info("Fetching internal stock issues for employee code: {}", employeeCode);

        List<InternalStockIssue> issues = internalStockIssueRepository.findByIssuedToEmployee_EmployeeCode(employeeCode);
        List<InternalStockIssueResponseDTO> responseDTOList = new ArrayList<>();

        for (InternalStockIssue issue : issues) {
            responseDTOList.add(mapToResponseDTO(issue));
        }

        log.info("Successfully retrieved {} internal stock issues for employee '{}'", responseDTOList.size(), employeeCode);
        return responseDTOList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InternalStockIssueResponseDTO> getInternalIssuesBySection(String sectionCode) {
        log.info("Fetching internal stock issues for job section code: {}", sectionCode);

        List<InternalStockIssue> issues = internalStockIssueRepository.findByJobSection_SectionCode(sectionCode);
        List<InternalStockIssueResponseDTO> responseDTOList = new ArrayList<>();

        for (InternalStockIssue issue : issues) {
            responseDTOList.add(mapToResponseDTO(issue));
        }

        log.info("Successfully retrieved {} internal stock issues for section '{}'", responseDTOList.size(), sectionCode);
        return responseDTOList;
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalInternalIssuesCount() {
        log.info("Fetching total count of internal stock issues");
        long count = internalStockIssueRepository.getInternalIssueCount();
        log.info("Total internal stock issue count: {}", count);
        return count;
    }
    private String generateNextInternalPartCode() {
        long currentCount = internalStockIssueRepository.getInternalIssueCount();
        long nextNumber = currentCount + 1;
        int currentYear = Year.now().getValue();

        String generatedCode = String.format("ISI-%d-%04d", currentYear, nextNumber);

        while (internalStockIssueRepository.findByInternalPartCode(generatedCode).isPresent()) {
            nextNumber++;
            generatedCode = String.format("ISI-%d-%04d", currentYear, nextNumber);
        }

        log.debug("Generated unique internal stock issue code: {}", generatedCode);
        return generatedCode;
    }

    private InternalStockIssueResponseDTO mapToResponseDTO(InternalStockIssue entity) {
        InternalStockIssueResponseDTO dto = new InternalStockIssueResponseDTO();
        dto.setIssueId(entity.getIssueId());
        dto.setInternalPartCode(entity.getInternalPartCode());

        if (entity.getSparePart() != null) {
            dto.setPartId(entity.getSparePart().getPartId());
            dto.setPartCode(entity.getSparePart().getPartCode());
            dto.setPartName(entity.getSparePart().getPartName());
        }

        if (entity.getIssuedToEmployee() != null) {
            dto.setEmployeeCode(entity.getIssuedToEmployee().getEmployeeCode());
            dto.setEmployeeName(entity.getIssuedToEmployee().getEmployeeName());
        }

        if (entity.getIssuedByUser() != null) {
            dto.setUserCode(entity.getIssuedByUser().getUserCode());
            dto.setIssuedByUserName(entity.getIssuedByUser().getUsername());
        }

        if (entity.getJobSection() != null) {
            dto.setSectionCode(entity.getJobSection().getSectionCode());
            dto.setSectionName(entity.getJobSection().getSectionName());
        }

        dto.setQuantity(entity.getQuantity());
        dto.setUnitCost(entity.getUnitCost());
        dto.setTotalCost(entity.getQuantity() * entity.getUnitCost());
        dto.setUsageReason(entity.getUsageReason());
        dto.setIssuedAt(entity.getIssuedAt());

        return dto;
    }
}
