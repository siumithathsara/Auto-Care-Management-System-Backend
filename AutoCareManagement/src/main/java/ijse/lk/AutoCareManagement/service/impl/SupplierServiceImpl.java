package ijse.lk.AutoCareManagement.service.impl;

import ijse.lk.AutoCareManagement.dto.SupplierRequestDTO;
import ijse.lk.AutoCareManagement.dto.SupplierResponseDTO;
import ijse.lk.AutoCareManagement.entity.Supplier;
import ijse.lk.AutoCareManagement.exception.CustomException;
import ijse.lk.AutoCareManagement.repository.SupplierRepository;
import ijse.lk.AutoCareManagement.service.SupplierService;
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
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public SupplierResponseDTO saveSupplier(SupplierRequestDTO supplierRequestDTO) {
        log.info("Registering new supplier: {}", supplierRequestDTO);

        if (supplierRequestDTO.getEmail() != null && !supplierRequestDTO.getEmail().trim().isEmpty()) {
            if (supplierRepository.existsByEmail(supplierRequestDTO.getEmail())) {
                log.warn("Supplier registration failed: Email '{}' is already registered!", supplierRequestDTO.getEmail());
                throw new CustomException(400, "Email '" + supplierRequestDTO.getEmail() + "' is already registered!");
            }
        }

        if (supplierRepository.existsByPhone(supplierRequestDTO.getPhone())) {
            log.warn("Supplier registration failed: Phone number '{}' is already registered!", supplierRequestDTO.getPhone());
            throw new CustomException(400, "Phone number '" + supplierRequestDTO.getPhone() + "' is already registered!");
        }

        String generatedSupplierCode = generateNextSupplierCode();

        Supplier supplier = new Supplier();
        supplier.setSupplierCode(generatedSupplierCode);
        supplier.setCompanyName(supplierRequestDTO.getCompanyName());
        supplier.setContactPerson(supplierRequestDTO.getContactPerson());
        supplier.setPhone(supplierRequestDTO.getPhone());
        supplier.setEmail(supplierRequestDTO.getEmail());
        supplier.setAddress(supplierRequestDTO.getAddress());
        supplier.setBrnNo(supplierRequestDTO.getBrnNo());
        supplier.setActive(true);

        Supplier savedSupplier = supplierRepository.save(supplier);
        log.info("Supplier successfully registered with code: {}", generatedSupplierCode);
        return mapToResponseDTO(savedSupplier);
    }

    @Override
    public SupplierResponseDTO updateSupplier(String supplierCode, SupplierRequestDTO supplierRequestDTO) {
        log.info("Updating supplier with code: {}", supplierCode);

        Optional<Supplier> supplierOptional = supplierRepository.findBySupplierCode(supplierCode);
        if (supplierOptional.isEmpty()) {
            log.warn("Supplier with code '{}' not found for update", supplierCode);
            throw new CustomException(404, "Supplier with code '" + supplierCode + "' not found");
        }

        Supplier supplier = supplierOptional.get();

        if (supplierRequestDTO.getPhone() != null && !supplierRequestDTO.getPhone().trim().isEmpty()) {
            if (!supplierRequestDTO.getPhone().equals(supplier.getPhone())) {
                if (supplierRepository.existsByPhoneAndSupplierCodeNot(supplierRequestDTO.getPhone(), supplierCode)) {
                    log.warn("Supplier update failed: Phone number '{}' is already in use", supplierRequestDTO.getPhone());
                    throw new CustomException(400, "Phone number '" + supplierRequestDTO.getPhone() + "' is already in use!");
                }
            }
        }

        if (supplierRequestDTO.getEmail() != null && !supplierRequestDTO.getEmail().trim().isEmpty()) {
            if (supplier.getEmail() == null || !supplier.getEmail().equalsIgnoreCase(supplierRequestDTO.getEmail())) {
                if (supplierRepository.existsByEmailAndSupplierCodeNot(supplierRequestDTO.getEmail(), supplierCode)) {
                    log.warn("Supplier update failed: Email '{}' is already in use", supplierRequestDTO.getEmail());
                    throw new CustomException(400, "Email '" + supplierRequestDTO.getEmail() + "' is already in use!");
                }
            }
        }


        supplier.setCompanyName(supplierRequestDTO.getCompanyName());
        supplier.setContactPerson(supplierRequestDTO.getContactPerson());
        supplier.setPhone(supplierRequestDTO.getPhone());
        supplier.setEmail(supplierRequestDTO.getEmail());
        supplier.setAddress(supplierRequestDTO.getAddress());
        supplier.setBrnNo(supplierRequestDTO.getBrnNo());

        Supplier updatedSupplier = supplierRepository.save(supplier);
        log.info("Supplier successfully updated with code: {}", supplierCode);
        return mapToResponseDTO(updatedSupplier);
    }

    @Override
    public SupplierResponseDTO getSupplierByCode(String supplierCode) {
        log.info("Fetching supplier with code: {}", supplierCode);
        Optional<Supplier> supplierOptional = supplierRepository.findBySupplierCode(supplierCode);
        if (supplierOptional.isEmpty()) {
            log.warn("Supplier with code '{}' not found", supplierCode);
            throw new CustomException(404, "Supplier with code '" + supplierCode + "' not found");
        }
        return mapToResponseDTO(supplierOptional.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponseDTO> getAllSuppliers() {
        log.info("Fetching all suppliers");
        List<Supplier> suppliers = supplierRepository.findAll();
        List<SupplierResponseDTO> responseDTOList = new ArrayList<>();

        for (Supplier supplier : suppliers) {
            responseDTOList.add(mapToResponseDTO(supplier));
        }

        log.info("Successfully retrieved {} suppliers", responseDTOList.size());
        return responseDTOList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponseDTO> getAllActiveSuppliers() {
        log.info("Fetching all active suppliers");
        List<Supplier> activeSuppliers = supplierRepository.findByIsActiveTrue();
        List<SupplierResponseDTO> responseDTOList = new ArrayList<>();

        for (Supplier supplier : activeSuppliers) {
            responseDTOList.add(mapToResponseDTO(supplier));
        }

        log.info("Successfully retrieved {} active suppliers", responseDTOList.size());
        return responseDTOList;
    }

    @Override
    public void toggleSupplierStatus(String supplierCode) {
        log.info("Toggling active status for supplier code: {}", supplierCode);
        Optional<Supplier> supplierOptional = supplierRepository.findBySupplierCode(supplierCode);
        if (supplierOptional.isEmpty()) {
            log.warn("Supplier with code '{}' not found for status toggle", supplierCode);
            throw new CustomException(404, "Supplier with code '" + supplierCode + "' not found");
        }

        Supplier supplier = supplierOptional.get();
        boolean newStatus = !supplier.isActive();
        supplier.setActive(newStatus);
        supplierRepository.save(supplier);

        log.info("Supplier status updated to '{}' for code: {}", newStatus ? "ACTIVE" : "INACTIVE", supplierCode);
    }

    private String generateNextSupplierCode() {
        long currentCount = supplierRepository.count();
        long nextNumber = currentCount + 1;
        int currentYear = java.time.Year.now().getValue();

        String generatedCode = String.format("SUP-%d-%04d", currentYear, nextNumber);

        while (supplierRepository.findBySupplierCode(generatedCode).isPresent()) {
            nextNumber++;
            generatedCode = String.format("SUP-%d-%04d", currentYear, nextNumber);
        }

        log.debug("Generated unique supplier code: {}", generatedCode);
        return generatedCode;
    }

    private SupplierResponseDTO mapToResponseDTO(Supplier supplier) {
        SupplierResponseDTO dto = new SupplierResponseDTO();
        dto.setSupplierId(supplier.getSupplierId());
        dto.setSupplierCode(supplier.getSupplierCode());
        dto.setCompanyName(supplier.getCompanyName());
        dto.setContactPerson(supplier.getContactPerson());
        dto.setPhone(supplier.getPhone());
        dto.setEmail(supplier.getEmail());
        dto.setAddress(supplier.getAddress());
        dto.setBrnNo(supplier.getBrnNo());
        dto.setActive(supplier.isActive());
        return dto;
    }
}
