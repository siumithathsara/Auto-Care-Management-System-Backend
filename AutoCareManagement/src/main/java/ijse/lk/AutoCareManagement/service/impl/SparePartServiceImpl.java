package ijse.lk.AutoCareManagement.service.impl;

import ijse.lk.AutoCareManagement.dto.SparePartRequestDTO;
import ijse.lk.AutoCareManagement.dto.SparePartResponseDTO;
import ijse.lk.AutoCareManagement.entity.SparePart;
import ijse.lk.AutoCareManagement.enumeration.DataStatus;
import ijse.lk.AutoCareManagement.exception.CustomException;
import ijse.lk.AutoCareManagement.repository.SparePartRepository;
import ijse.lk.AutoCareManagement.service.EmailService;
import ijse.lk.AutoCareManagement.service.SparePartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SparePartServiceImpl implements SparePartService {

    private final SparePartRepository sparePartRepository;
    private final EmailService emailService;

    @Override
    public SparePartResponseDTO saveSparePart(SparePartRequestDTO dto) {
        log.info("Registering new spare part: {}", dto.getPartName());

        if (sparePartRepository.existsByPartNameAndDataStatus(dto.getPartName(), DataStatus.ACTIVE)) {
            log.warn("Spare part registration failed: Part name '{}' already exists", dto.getPartName());
            throw new CustomException(400, "Spare Part already exists with name: " + dto.getPartName());
        }

        String generatedPartCode = generateNextPartCode();

        SparePart sparePart = new SparePart();
        sparePart.setPartCode(generatedPartCode);
        sparePart.setPartName(dto.getPartName());
        sparePart.setBrand(dto.getBrand());
        sparePart.setCostPrice(dto.getCostPrice());
        sparePart.setUnitPrice(dto.getUnitPrice());
        sparePart.setQuantityInStock(dto.getQuantityInStock());
        sparePart.setReorderLevel(dto.getReorderLevel());
        sparePart.setPartType(dto.getPartType());
        sparePart.setDataStatus(DataStatus.ACTIVE);

        SparePart savedPart = sparePartRepository.save(sparePart);
        log.info("Spare part successfully registered with code: {}", generatedPartCode);

        checkAndSendLowStockAlertEmail(savedPart);

        return mapToResponseDTO(savedPart);
    }

    @Override
    @Transactional(readOnly = true)
    public SparePartResponseDTO getSparePartByCode(String partCode) {
        log.info("Fetching spare part with code: {}", partCode);

        Optional<SparePart> partOptional = sparePartRepository.findByPartCodeAndDataStatus(partCode, DataStatus.ACTIVE);
        if (partOptional.isEmpty()) {
            log.warn("Spare part fetch failed: Code '{}' not found or inactive", partCode);
            throw new CustomException(404, "Spare Part not found with code: " + partCode);
        }

        log.info("Successfully retrieved spare part details for code: {}", partCode);
        return mapToResponseDTO(partOptional.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SparePartResponseDTO> getAllSpareParts() {
        log.info("Fetching all active spare parts");

        List<SparePart> spareParts = sparePartRepository.findByDataStatus(DataStatus.ACTIVE);
        List<SparePartResponseDTO> responseDTOList = new ArrayList<>();

        for (SparePart sparePart : spareParts) {
            responseDTOList.add(mapToResponseDTO(sparePart));
        }

        log.info("Successfully retrieved {} active spare parts", responseDTOList.size());
        return responseDTOList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SparePartResponseDTO> getLowStockSpareParts() {
        log.info("Fetching low stock spare parts alert list");

        List<SparePart> lowStockParts = sparePartRepository.findLowStockParts(DataStatus.ACTIVE);
        List<SparePartResponseDTO> responseDTOList = new ArrayList<>();

        for (SparePart sparePart : lowStockParts) {
            responseDTOList.add(mapToResponseDTO(sparePart));
        }

        log.info("Found {} low stock spare parts requiring reorder", responseDTOList.size());
        return responseDTOList;
    }

    @Override
    public SparePartResponseDTO updateSparePart(String partCode, SparePartRequestDTO dto) {
        log.info("Updating spare part with code: {}", partCode);

        Optional<SparePart> partOptional = sparePartRepository.findByPartCodeAndDataStatus(partCode, DataStatus.ACTIVE);
        if (partOptional.isEmpty()) {
            log.warn("Spare part update failed: Code '{}' not found or inactive", partCode);
            throw new CustomException(404, "Spare Part not found with code: " + partCode);
        }

        SparePart sparePart = partOptional.get();

        if (!sparePart.getPartName().equalsIgnoreCase(dto.getPartName())) {
            if (sparePartRepository.existsByPartNameAndDataStatus(dto.getPartName(), DataStatus.ACTIVE)) {
                log.warn("Spare part update failed: Part name '{}' is already in use", dto.getPartName());
                throw new CustomException(400, "Spare Part name " + dto.getPartName() + " is already in use!");
            }
        }

        sparePart.setPartName(dto.getPartName());
        sparePart.setBrand(dto.getBrand());
        sparePart.setCostPrice(dto.getCostPrice());
        sparePart.setUnitPrice(dto.getUnitPrice());
        sparePart.setQuantityInStock(dto.getQuantityInStock());
        sparePart.setReorderLevel(dto.getReorderLevel());
        sparePart.setPartType(dto.getPartType());

        SparePart updatedPart = sparePartRepository.save(sparePart);
        log.info("Spare part with code '{}' successfully updated", partCode);

        checkAndSendLowStockAlertEmail(updatedPart);

        return mapToResponseDTO(updatedPart);
    }

    @Override
    public void deleteSparePart(String partCode) {
        log.info("Deleting (Soft Delete) spare part with code: {}", partCode);

        Optional<SparePart> partOptional = sparePartRepository.findByPartCodeAndDataStatus(partCode, DataStatus.ACTIVE);
        if (partOptional.isEmpty()) {
            log.warn("Spare part deletion failed: Code '{}' not found or inactive", partCode);
            throw new CustomException(404, "Spare Part not found with code: " + partCode);
        }

        SparePart sparePart = partOptional.get();
        sparePart.setDataStatus(DataStatus.INACTIVE);
        sparePartRepository.save(sparePart);

        log.info("Spare part successfully marked as inactive for code: {}", partCode);
    }

    @Override
    public SparePartResponseDTO deductStock(String partCode, int quantityToDeduct) {
        log.info("Deducting stock for part code: {} by quantity: {}", partCode, quantityToDeduct);

        Optional<SparePart> partOptional = sparePartRepository.findByPartCodeAndDataStatus(partCode, DataStatus.ACTIVE);
        if (partOptional.isEmpty()) {
            throw new CustomException(404, "Spare Part not found with code: " + partCode);
        }

        SparePart sparePart = partOptional.get();

        if (sparePart.getQuantityInStock() < quantityToDeduct) {
            throw new CustomException(400, "Insufficient stock for part: " + sparePart.getPartName() +
                    ". Available: " + sparePart.getQuantityInStock() + ", Requested: " + quantityToDeduct);
        }

        int newQuantity = sparePart.getQuantityInStock() - quantityToDeduct;
        sparePart.setQuantityInStock(newQuantity);

        SparePart updatedPart = sparePartRepository.save(sparePart);
        log.info("Stock updated for code '{}'. New stock level: {}", partCode, newQuantity);

        checkAndSendLowStockAlertEmail(updatedPart);

        return mapToResponseDTO(updatedPart);
    }

    private String generateNextPartCode() {
        long currentCount = sparePartRepository.getSparePartCount();
        long nextNumber = currentCount + 1;

        String generatedCode = String.format("PRT-%04d", nextNumber);

        while (sparePartRepository.findByPartCodeAndDataStatus(generatedCode, DataStatus.ACTIVE).isPresent()) {
            nextNumber++;
            generatedCode = String.format("PRT-%04d", nextNumber);
        }

        log.debug("Generated unique spare part code: {}", generatedCode);
        return generatedCode;
    }

    private SparePartResponseDTO mapToResponseDTO(SparePart sparePart) {
        SparePartResponseDTO dto = new SparePartResponseDTO();
        dto.setPartId(sparePart.getPartId());
        dto.setPartCode(sparePart.getPartCode());
        dto.setPartName(sparePart.getPartName());
        dto.setBrand(sparePart.getBrand());
        dto.setCostPrice(sparePart.getCostPrice());
        dto.setUnitPrice(sparePart.getUnitPrice());
        dto.setQuantityInStock(sparePart.getQuantityInStock());
        dto.setReorderLevel(sparePart.getReorderLevel());
        dto.setPartType(sparePart.getPartType());
        dto.setDataStatus(sparePart.getDataStatus());
        return dto;
    }
    private void checkAndSendLowStockAlertEmail(SparePart sparePart) {
        if (sparePart.getQuantityInStock() <= sparePart.getReorderLevel()) {
            log.warn("Low stock detected for item: {} (Code: {}). Current: {}, Reorder Level: {}",
                    sparePart.getPartName(), sparePart.getPartCode(), sparePart.getQuantityInStock(), sparePart.getReorderLevel());

            try {
                String subject = " Low Stock Alert - " + sparePart.getPartCode() + " (" + sparePart.getPartName() + ")";

                Map<String, String> variables = new HashMap<>();
                variables.put("partCode", sparePart.getPartCode());
                variables.put("partName", sparePart.getPartName());
                variables.put("brand", sparePart.getBrand() != null ? sparePart.getBrand() : "N/A");
                variables.put("partType", sparePart.getPartType() != null ? sparePart.getPartType().name() : "N/A");
                variables.put("quantityInStock", String.valueOf(sparePart.getQuantityInStock()));
                variables.put("reorderLevel", String.valueOf(sparePart.getReorderLevel()));
                variables.put("unitPrice", String.format("%.2f", sparePart.getUnitPrice()));

                emailService.sendTemplateEmail("autocare.service.official@gmail.com", subject, "low-stock-admin-alert", variables);
            } catch (Exception e) {
                log.error("Failed to send low stock alert email for part code: {}: {}", sparePart.getPartCode(), e.getMessage());
            }
        }
    }
}
