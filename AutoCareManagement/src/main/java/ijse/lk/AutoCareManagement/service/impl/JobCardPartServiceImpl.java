package ijse.lk.AutoCareManagement.service.impl;

import ijse.lk.AutoCareManagement.dto.JobCardPartResponseDTO;
import ijse.lk.AutoCareManagement.entity.JobCardPart;
import ijse.lk.AutoCareManagement.entity.SparePart;
import ijse.lk.AutoCareManagement.enumeration.IssueStatus;
import ijse.lk.AutoCareManagement.exception.CustomException;
import ijse.lk.AutoCareManagement.repository.JobCardPartRepository;
import ijse.lk.AutoCareManagement.repository.SparePartRepository;
import ijse.lk.AutoCareManagement.service.JobCardPartService;
import ijse.lk.AutoCareManagement.service.SparePartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JobCardPartServiceImpl implements JobCardPartService {

    private final JobCardPartRepository jobCardPartRepository;
    private final SparePartRepository sparePartRepository;
    private SparePartService sparePartService;

    @Override
    public JobCardPartResponseDTO issueJobCardPart(Long jobCardPartId) {
        log.info("Processing part issue for JobCardPart ID: {}", jobCardPartId);

        Optional<JobCardPart> partOptional = jobCardPartRepository.findById(jobCardPartId);
        if (partOptional.isEmpty()) {
            log.warn("Part issue failed: JobCardPart ID '{}' not found", jobCardPartId);
            throw new CustomException(404, "Job Card Part Request not found with ID: " + jobCardPartId);
        }

        JobCardPart jobCardPart = partOptional.get();

        if (jobCardPart.getIssueStatus() != IssueStatus.REQUESTED) {
            log.warn("Part issue failed: Current status is '{}', only REQUESTED allowed", jobCardPart.getIssueStatus());
            throw new CustomException(400, "Only REQUESTED parts can be issued! Current status: " + jobCardPart.getIssueStatus());
        }

        SparePart sparePart = jobCardPart.getSparePart();

        if (sparePart.getQuantityInStock() < jobCardPart.getQuantity()) {
            log.warn("Part issue failed: Insufficient stock for part '{}'. Available: {}, Requested: {}",
                    sparePart.getPartName(), sparePart.getQuantityInStock(), jobCardPart.getQuantity());
            throw new CustomException(400, "Insufficient stock in store for: " + sparePart.getPartName() +
                    ". Available: " + sparePart.getQuantityInStock());
        }

        int updatedStock = sparePart.getQuantityInStock() - jobCardPart.getQuantity();
        sparePart.setQuantityInStock(updatedStock);
        sparePartRepository.save(sparePart);

        sparePartService.deductStock(sparePart.getPartCode(), jobCardPart.getQuantity());
        jobCardPart.setIssueStatus(IssueStatus.ISSUED);
        JobCardPart updatedPart = jobCardPartRepository.save(jobCardPart);

        log.info("JobCardPart ID '{}' successfully ISSUED. Updated stock for '{}': {}", jobCardPartId, sparePart.getPartName(), updatedStock);

        return mapToResponseDTO(updatedPart);
    }

    @Override
    public JobCardPartResponseDTO returnJobCardPart(Long jobCardPartId) {
        log.info("Processing part return for JobCardPart ID: {}", jobCardPartId);

        Optional<JobCardPart> partOptional = jobCardPartRepository.findById(jobCardPartId);
        if (partOptional.isEmpty()) {
            log.warn("Part return failed: JobCardPart ID '{}' not found", jobCardPartId);
            throw new CustomException(404, "Job Card Part Record not found with ID: " + jobCardPartId);
        }

        JobCardPart jobCardPart = partOptional.get();

        if (jobCardPart.getIssueStatus() != IssueStatus.ISSUED) {
            log.warn("Part return failed: Current status is '{}', only ISSUED allowed", jobCardPart.getIssueStatus());
            throw new CustomException(400, "Only ISSUED parts can be returned!");
        }

        SparePart sparePart = jobCardPart.getSparePart();

        int updatedStock = sparePart.getQuantityInStock() + jobCardPart.getQuantity();
        sparePart.setQuantityInStock(updatedStock);
        sparePartRepository.save(sparePart);

        jobCardPart.setIssueStatus(IssueStatus.RETURNED);
        JobCardPart updatedPart = jobCardPartRepository.save(jobCardPart);

        log.info("JobCardPart ID '{}' successfully RETURNED. Restored stock for '{}': {}", jobCardPartId, sparePart.getPartName(), updatedStock);

        return mapToResponseDTO(updatedPart);
    }

    @Override
    public JobCardPartResponseDTO rejectJobCardPart(Long jobCardPartId) {
        log.info("Processing part rejection for JobCardPart ID: {}", jobCardPartId);

        Optional<JobCardPart> partOptional = jobCardPartRepository.findById(jobCardPartId);
        if (partOptional.isEmpty()) {
            log.warn("Part rejection failed: JobCardPart ID '{}' not found", jobCardPartId);
            throw new CustomException(404, "Job Card Part Record not found with ID: " + jobCardPartId);
        }

        JobCardPart jobCardPart = partOptional.get();

        if (jobCardPart.getIssueStatus() != IssueStatus.REQUESTED) {
            log.warn("Part rejection failed: Current status is '{}', only REQUESTED allowed", jobCardPart.getIssueStatus());
            throw new CustomException(400, "Only REQUESTED parts can be rejected!");
        }

        jobCardPart.setIssueStatus(IssueStatus.REJECTED);
        JobCardPart updatedPart = jobCardPartRepository.save(jobCardPart);

        log.info("JobCardPart ID '{}' successfully REJECTED", jobCardPartId);

        return mapToResponseDTO(updatedPart);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobCardPartResponseDTO> getPendingRequests() {
        log.info("Fetching all pending part requests");

        List<JobCardPart> pendingParts = jobCardPartRepository.findByIssueStatus(IssueStatus.REQUESTED);
        List<JobCardPartResponseDTO> responseDTOList = new ArrayList<>();

        for (JobCardPart jobCardPart : pendingParts) {
            responseDTOList.add(mapToResponseDTO(jobCardPart));
        }

        log.info("Successfully retrieved {} pending part requests", responseDTOList.size());
        return responseDTOList;
    }


    @Override
    @Transactional(readOnly = true)
    public List<JobCardPartResponseDTO> getPartsByJobCardCode(String jobCardCode) {
        log.info("Fetching all parts for job card code: {}", jobCardCode);

        List<JobCardPart> jobCardParts = jobCardPartRepository.findByJobCard_JobCardCode(jobCardCode);
        List<JobCardPartResponseDTO> responseDTOList = new ArrayList<>();

        for (JobCardPart jobCardPart : jobCardParts) {
            responseDTOList.add(mapToResponseDTO(jobCardPart));
        }

        log.info("Successfully retrieved {} parts for job card code: {}", responseDTOList.size(), jobCardCode);
        return responseDTOList;
    }

    private JobCardPartResponseDTO mapToResponseDTO(JobCardPart jobCardPart) {
        JobCardPartResponseDTO dto = new JobCardPartResponseDTO();
        dto.setPartCode(jobCardPart.getSparePart().getPartCode());
        dto.setPartName(jobCardPart.getSparePart().getPartName());
        dto.setRequestedByName(jobCardPart.getRequestedBy().getUsername());
        dto.setQuantity(jobCardPart.getQuantity());
        dto.setUnitPrice(jobCardPart.getUnitPrice());
        dto.setSubTotal(jobCardPart.getSubTotal());
        dto.setIssueStatus(jobCardPart.getIssueStatus());
        return dto;
    }
}
