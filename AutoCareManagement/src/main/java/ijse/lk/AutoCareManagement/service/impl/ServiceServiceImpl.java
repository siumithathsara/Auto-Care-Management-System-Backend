package ijse.lk.AutoCareManagement.service.impl;

import ijse.lk.AutoCareManagement.dto.ServiceRequestDTO;
import ijse.lk.AutoCareManagement.dto.ServiceResponseDTO;
import ijse.lk.AutoCareManagement.entity.ServiceCategory;
import ijse.lk.AutoCareManagement.entity.VehicleService;
import ijse.lk.AutoCareManagement.enumeration.DataStatus;
import ijse.lk.AutoCareManagement.exception.CustomException;
import ijse.lk.AutoCareManagement.repository.ServiceCategoryRepository;
import ijse.lk.AutoCareManagement.repository.ServiceRepository;
import ijse.lk.AutoCareManagement.service.ServiceService;
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
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceCategoryRepository categoryRepository;

    @Override
    public ServiceResponseDTO createService(ServiceRequestDTO serviceRequestDTO) {
        log.info("Starting service creation for name: {}, categoryCode: {}", serviceRequestDTO.getServiceName(), serviceRequestDTO.getCategoryCode());

        if (serviceRepository.existsByServiceName(serviceRequestDTO.getServiceName())) {
            log.warn("Service creation failed: Service name '{}' already exists", serviceRequestDTO.getServiceName());
            throw new CustomException(400, "Service name '" + serviceRequestDTO.getServiceName() + "' already exists");
        }

        Optional<ServiceCategory> categoryOptional = categoryRepository.findByCategoryCodeAndStatus(serviceRequestDTO.getCategoryCode(), DataStatus.ACTIVE);
        if (categoryOptional.isEmpty()) {
            log.warn("Service creation failed: Active Service Category not found with code: {}", serviceRequestDTO.getCategoryCode());
            throw new CustomException(404, "Active Service Category not found with code: " + serviceRequestDTO.getCategoryCode());
        }

        String generatedServiceCode = generateServiceCode("SRV");
        log.debug("Generated service code: {}", generatedServiceCode);

        VehicleService vehicleService = new VehicleService();
        vehicleService.setServiceCode(generatedServiceCode);
        vehicleService.setServiceName(serviceRequestDTO.getServiceName());
        vehicleService.setDescription(serviceRequestDTO.getDescription());
        vehicleService.setStandardFee(serviceRequestDTO.getStandardFee());
        vehicleService.setEstimatedTimeMins(serviceRequestDTO.getEstimatedTimeMins());
        vehicleService.setStatus(DataStatus.ACTIVE);
        vehicleService.setCategory(categoryOptional.get());

        VehicleService savedService = serviceRepository.save(vehicleService);
        log.info("Service created successfully with Service Code: {}", savedService.getServiceCode());
        return mapToResponseDTO(savedService);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResponseDTO> getAllActiveServices() {
        log.info("Fetching all active services");

        List<VehicleService> activeServices = serviceRepository.findByStatus(DataStatus.ACTIVE);
        List<ServiceResponseDTO> responseDTOList = new ArrayList<>();

        for (VehicleService service : activeServices) {
            responseDTOList.add(mapToResponseDTO(service));
        }

        log.info("Retrieved {} active services", responseDTOList.size());
        return responseDTOList;
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponseDTO getServiceByCode(String serviceCode) {
        log.info("Fetching active service details for Service Code: {}", serviceCode);

        Optional<VehicleService> serviceOptional = serviceRepository.findByServiceCodeAndStatus(serviceCode, DataStatus.ACTIVE);
        if (serviceOptional.isEmpty()) {
            log.warn("Active service not found for Service Code: {}", serviceCode);
            throw new CustomException(404, "Active Service not found with code: " + serviceCode);
        }

        log.info("Successfully retrieved service details for Service Code: {}", serviceCode);
        return mapToResponseDTO(serviceOptional.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResponseDTO> getServicesByCategoryCode(String categoryCode) {
        log.info("Fetching active services for Category Code: {}", categoryCode);

        List<VehicleService> categoryServices = serviceRepository.findByCategoryCategoryCodeAndStatus(categoryCode, DataStatus.ACTIVE);
        List<ServiceResponseDTO> responseDTOList = new ArrayList<>();

        for (VehicleService service : categoryServices) {
            responseDTOList.add(mapToResponseDTO(service));
        }

        log.info("Retrieved {} services for Category Code: {}", responseDTOList.size(), categoryCode);
        return responseDTOList;
    }

    @Override
    public ServiceResponseDTO updateService(String serviceCode, ServiceRequestDTO serviceRequestDTO) {
        log.info("Attempting to update service with Service Code: {}", serviceCode);

        Optional<VehicleService> serviceOptional = serviceRepository.findByServiceCode(serviceCode);
        if (serviceOptional.isEmpty()) {
            log.warn("Service update failed: Service Code '{}' not found", serviceCode);
            throw new CustomException(404, "Service not found with code: " + serviceCode);
        }

        VehicleService service = serviceOptional.get();

        if (!service.getServiceName().equalsIgnoreCase(serviceRequestDTO.getServiceName()) &&
                serviceRepository.existsByServiceName(serviceRequestDTO.getServiceName())) {
            log.warn("Service update failed: Service name '{}' is already in use", serviceRequestDTO.getServiceName());
            throw new CustomException(400, "Service name '" + serviceRequestDTO.getServiceName() + "' already exists!");
        }

        Optional<ServiceCategory> categoryOptional = categoryRepository.findByCategoryCodeAndStatus(serviceRequestDTO.getCategoryCode(), DataStatus.ACTIVE);
        if (categoryOptional.isEmpty()) {
            log.warn("Service update failed: Active Service Category not found with code: {}", serviceRequestDTO.getCategoryCode());
            throw new CustomException(404, "Active Service Category not found with code: " + serviceRequestDTO.getCategoryCode());
        }

        service.setServiceName(serviceRequestDTO.getServiceName());
        service.setDescription(serviceRequestDTO.getDescription());
        service.setStandardFee(serviceRequestDTO.getStandardFee());
        service.setEstimatedTimeMins(serviceRequestDTO.getEstimatedTimeMins());
        service.setCategory(categoryOptional.get());

        VehicleService updatedService = serviceRepository.save(service);
        log.info("Successfully updated service with Service Code: {}", serviceCode);
        return mapToResponseDTO(updatedService);
    }

    @Override
    public boolean changeServiceStatus(String serviceCode, String status) {
        log.info("Attempting to change status for Service Code: {} to {}", serviceCode, status);

        Optional<VehicleService> serviceOptional = serviceRepository.findByServiceCode(serviceCode);
        if (serviceOptional.isEmpty()) {
            log.warn("Service status change failed: Service Code '{}' not found", serviceCode);
            throw new CustomException(404, "Service not found with code: " + serviceCode);
        }

        try {
            DataStatus dataStatus = DataStatus.valueOf(status.toUpperCase());
            VehicleService service = serviceOptional.get();
            service.setStatus(dataStatus);
            serviceRepository.save(service);
            log.info("Successfully changed status to {} for Service Code: {}", dataStatus, serviceCode);
            return true;
        } catch (IllegalArgumentException e) {
            log.warn("Service status change failed: Invalid status '{}'", status);
            throw new CustomException(400, "Invalid status type provided: " + status);
        }
    }

    @Override
    public long getTotalServicesCount() {
        log.info("Fetching total services count from database");
        return serviceRepository.getServiceCount();
    }

    private String generateServiceCode(String prefix) {
        log.debug("Generating service code with prefix: {}", prefix);
        long currentCount = serviceRepository.getServiceCount();
        long newCount = currentCount + 1;
        int currentYear = Year.now().getValue();

        String generatedCode = String.format("%s-%d-%04d", prefix, currentYear, newCount);

        while (serviceRepository.existsByServiceCode(generatedCode)) {
            newCount++;
            generatedCode = String.format("%s-%d-%04d", prefix, currentYear, newCount);
        }

        log.debug("Final generated service code: {}", generatedCode);
        return generatedCode;
    }

    private ServiceResponseDTO mapToResponseDTO(VehicleService vehicleService) {
        ServiceResponseDTO dto = new ServiceResponseDTO();
        dto.setServiceId(vehicleService.getServiceId());
        dto.setServiceCode(vehicleService.getServiceCode());
        dto.setServiceName(vehicleService.getServiceName());
        dto.setDescription(vehicleService.getDescription());
        dto.setStandardFee(vehicleService.getStandardFee());
        dto.setEstimatedTimeMin(vehicleService.getEstimatedTimeMins());
        dto.setDataStatus(vehicleService.getStatus());

        if (vehicleService.getCategory() != null) {
            dto.setCategoryCode(vehicleService.getCategory().getCategoryCode());
            dto.setCategoryName(vehicleService.getCategory().getCategoryName());
        }
        return dto;
    }
}
