package ijse.lk.AutoCareManagement.controller;

import ijse.lk.AutoCareManagement.constant.CommonResponse;
import ijse.lk.AutoCareManagement.dto.ServiceRequestDTO;
import ijse.lk.AutoCareManagement.dto.ServiceResponseDTO;
import ijse.lk.AutoCareManagement.service.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/service")
@RequiredArgsConstructor
public class VehicleServiceController {

    private final ServiceService serviceService;

//    create new service
    @PostMapping(value = "/create-service", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse createService(@Valid @RequestBody ServiceRequestDTO dto) {
        ServiceResponseDTO responseDTO = serviceService.createService(dto);
        return new CommonResponse(201, responseDTO, "Service created successfully!");
    }

// get all active services
    @GetMapping(value = "/get-all", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllActiveServices() {
        List<ServiceResponseDTO> services = serviceService.getAllActiveServices();
        return new CommonResponse(200, services, "All active services fetched successfully!");
    }

//   to get service details bt using Service code
    @GetMapping(value = "/get-by-code/{serviceCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getServiceByCode(@PathVariable String serviceCode) {
        ServiceResponseDTO service = serviceService.getServiceByCode(serviceCode);
        return new CommonResponse(200, service, "Service fetched successfully!");
    }

//    to get service details by using category code
    @GetMapping(value = "/get-by-category/{categoryCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getServicesByCategoryCode(@PathVariable String categoryCode) {
        List<ServiceResponseDTO> services = serviceService.getServicesByCategoryCode(categoryCode);
        return new CommonResponse(200, services, "Category services fetched successfully!");
    }

//    service update
    @PutMapping(value = "/update/{serviceCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse updateService(@PathVariable String serviceCode, @Valid @RequestBody ServiceRequestDTO dto) {
        ServiceResponseDTO updatedService = serviceService.updateService(serviceCode, dto);
        return new CommonResponse(200, updatedService, "Service updated successfully!");
    }

//  to change service status
    @PatchMapping(value = "/change-status/{serviceCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse changeServiceStatus(@PathVariable String serviceCode, @RequestParam(value = "status") String status) {
        boolean isUpdated = serviceService.changeServiceStatus(serviceCode, status);
        return new CommonResponse(200, isUpdated, "Service status changed successfully!");
    }

//    to get total service count
    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse getTotalServicesCount() {
        long count = serviceService.getTotalServicesCount();
        return new CommonResponse(200, count, "Total services count fetched successfully!");
    }
}
