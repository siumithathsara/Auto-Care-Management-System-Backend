package ijse.lk.AutoCareManagement.service;

import ijse.lk.AutoCareManagement.dto.ServiceRequestDTO;
import ijse.lk.AutoCareManagement.dto.ServiceResponseDTO;

import java.util.List;

public interface ServiceService {

    ServiceResponseDTO createService(ServiceRequestDTO serviceRequestDTO);

    List<ServiceResponseDTO> getAllActiveServices();

    ServiceResponseDTO getServiceByCode(String serviceCode);

    List<ServiceResponseDTO> getServicesByCategoryCode(String categoryCode);

    ServiceResponseDTO updateService(String serviceCode, ServiceRequestDTO serviceRequestDTO);

    boolean changeServiceStatus(String serviceCode, String status);

    long getTotalServicesCount();

}
