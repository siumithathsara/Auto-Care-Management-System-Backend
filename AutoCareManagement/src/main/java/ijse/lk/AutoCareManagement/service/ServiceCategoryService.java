package ijse.lk.AutoCareManagement.service;

import ijse.lk.AutoCareManagement.dto.ServiceCategoryRequestDTO;
import ijse.lk.AutoCareManagement.dto.ServiceCategoryResponseDTO;

import java.util.List;

public interface ServiceCategoryService {

    ServiceCategoryResponseDTO createServiceCategory(ServiceCategoryRequestDTO serviceCategoryRequestDTO);

    ServiceCategoryResponseDTO updateServiceCategory(String categoryCode, ServiceCategoryRequestDTO serviceCategoryRequestDTO);

    List<ServiceCategoryResponseDTO> getAllServiceCategories();

    ServiceCategoryResponseDTO getServiceCategoryByCode(String categoryCode);

    boolean changeServiceCategoryStatus(String categoryCode, String status);


}
