package ijse.lk.AutoCareManagement.service.impl;

import ijse.lk.AutoCareManagement.dto.ServiceCategoryRequestDTO;
import ijse.lk.AutoCareManagement.dto.ServiceCategoryResponseDTO;
import ijse.lk.AutoCareManagement.entity.ServiceCategory;
import ijse.lk.AutoCareManagement.enumeration.DataStatus;
import ijse.lk.AutoCareManagement.exception.CustomException;
import ijse.lk.AutoCareManagement.repository.ServiceCategoryRepository;
import ijse.lk.AutoCareManagement.service.ServiceCategoryService;
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
public class ServiceCategoryServiceImpl implements ServiceCategoryService {

    private final ServiceCategoryRepository categoryRepository;

    @Override
    public ServiceCategoryResponseDTO createServiceCategory(ServiceCategoryRequestDTO serviceCategoryRequestDTO) {
        log.info("Creating service category: {}", serviceCategoryRequestDTO.getCategoryName());

        if (categoryRepository.existsByCategoryName(serviceCategoryRequestDTO.getCategoryName())) {
            log.warn("Category creation failed: Category name '{}' already exists", serviceCategoryRequestDTO.getCategoryName());
            throw new CustomException(400, "Category name '" + serviceCategoryRequestDTO.getCategoryName() + "' already exists");
        }

        if (serviceCategoryRequestDTO.getCategoryName() == null || serviceCategoryRequestDTO.getCategoryName().trim().isEmpty()) {
            log.warn("Category creation failed: Category name is required");
            throw new CustomException(400, "Category name is required");
        }

        String generatedCategoryCode = generateCategoryCode("CAT");
        log.debug("Generated category code: {}", generatedCategoryCode);

        ServiceCategory category = new ServiceCategory();
        category.setCategoryCode(generatedCategoryCode);
        category.setCategoryName(serviceCategoryRequestDTO.getCategoryName());
        category.setDescription(serviceCategoryRequestDTO.getDescription());
        category.setStatus(DataStatus.ACTIVE);

        ServiceCategory savedCategory = categoryRepository.save(category);
        log.info("Service category created successfully with Category Code: {}", savedCategory.getCategoryCode());
        return mapToResponseDTO(savedCategory);

    }

    @Override
    public ServiceCategoryResponseDTO updateServiceCategory(String categoryCode, ServiceCategoryRequestDTO serviceCategoryRequestDTO) {
        log.info("Attempting to update service category with Category Code: {}", categoryCode);

        Optional<ServiceCategory> categoryOptional = categoryRepository.findByCategoryCodeAndStatus(categoryCode, DataStatus.ACTIVE);
        if (categoryOptional.isEmpty()) {
            log.warn("Category update failed: Category Code '{}' not found or inactive", categoryCode);
            throw new CustomException(404, "Category not found");
        }

        ServiceCategory category = categoryOptional.get();

        if (!category.getCategoryName().equals(serviceCategoryRequestDTO.getCategoryName()) &&
                categoryRepository.existsByCategoryName(serviceCategoryRequestDTO.getCategoryName())) {
            log.warn("Category update failed: New Category Name '{}' is already in use", serviceCategoryRequestDTO.getCategoryName());
            throw new CustomException(400, "Category name '" + serviceCategoryRequestDTO.getCategoryName() + "' is already in use!");
        }

        category.setCategoryName(serviceCategoryRequestDTO.getCategoryName());
        category.setDescription(serviceCategoryRequestDTO.getDescription());

        ServiceCategory updatedCategory = categoryRepository.save(category);
        log.info("Successfully updated service category for Category Code: {}", categoryCode);
        return mapToResponseDTO(updatedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceCategoryResponseDTO> getAllServiceCategories() {
        log.info("Fetching all active service categories");

        List<ServiceCategory> activeCategories = categoryRepository.findByStatus(DataStatus.ACTIVE);
        List<ServiceCategoryResponseDTO> responseDTOList = new ArrayList<>();

        for (ServiceCategory category : activeCategories) {
            ServiceCategoryResponseDTO responseDTO = mapToResponseDTO(category);
            responseDTOList.add(responseDTO);
        }

        log.info("Retrieved {} active service categories", responseDTOList.size());
        return responseDTOList;
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceCategoryResponseDTO getServiceCategoryByCode(String categoryCode) {
        log.info("Fetching active service category details for Category Code: {}", categoryCode);

        Optional<ServiceCategory> categoryOptional = categoryRepository.findByCategoryCodeAndStatus(categoryCode, DataStatus.ACTIVE);
        if (categoryOptional.isEmpty()) {
            log.warn("Category not found for Category Code: {}", categoryCode);
            throw new CustomException(404, "Category not found");
        }

        log.info("Successfully retrieved service category details for Category Code: {}", categoryCode);
        return mapToResponseDTO(categoryOptional.get());
    }

    @Override
    public boolean changeServiceCategoryStatus(String categoryCode, String status) {
        log.info("Attempting to change status of category code", categoryCode, status);

        Optional<ServiceCategory> categoryOptional = categoryRepository.findByCategoryCode(categoryCode);
        if (categoryOptional.isEmpty()) {
            log.warn("Category status change failed: Category Code '{}' not found", categoryCode);
            throw new CustomException(404, "Category not found");
        }

        ServiceCategory category = categoryOptional.get();

        try {
            DataStatus newStatus = DataStatus.valueOf(status.toUpperCase());
            category.setStatus(newStatus);
            categoryRepository.save(category);
            log.info("Successfully changed status of category code: {} to {}", categoryCode, newStatus);
            return true;
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status provided: {}", status);
            throw new CustomException(400, "Invalid status provided: " + status);
        }
    }

    private String generateCategoryCode(String prefix) {
        log.debug("Generating category code with prefix: {}", prefix);
        long currentCount = categoryRepository.getCategoryCount();
        long newCount = currentCount + 1;
        int currentYear = Year.now().getValue();

        String generatedCode = String.format("%s-%d-%04d", prefix, currentYear, newCount);

        while (categoryRepository.existsByCategoryCode(generatedCode)) {
            newCount++;
            generatedCode = String.format("%s-%d-%04d", prefix, currentYear, newCount);
        }

        log.debug("Final generated category code: {}", generatedCode);
        return generatedCode;
    }

    private ServiceCategoryResponseDTO mapToResponseDTO(ServiceCategory category) {
        ServiceCategoryResponseDTO dto = new ServiceCategoryResponseDTO();
        dto.setCategoryId(category.getCategoryId());
        dto.setCategoryCode(category.getCategoryCode());
        dto.setCategoryName(category.getCategoryName());
        dto.setDescription(category.getDescription());
        dto.setStatus(category.getStatus());
        return dto;
    }
}
