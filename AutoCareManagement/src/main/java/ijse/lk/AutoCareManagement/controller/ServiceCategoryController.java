package ijse.lk.AutoCareManagement.controller;

import ijse.lk.AutoCareManagement.constant.CommonResponse;
import ijse.lk.AutoCareManagement.dto.ServiceCategoryRequestDTO;
import ijse.lk.AutoCareManagement.dto.ServiceCategoryResponseDTO;
import ijse.lk.AutoCareManagement.service.ServiceCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/service-category")
@RequiredArgsConstructor
public class ServiceCategoryController {

    private final ServiceCategoryService serviceCategoryService;

    // create service category
    @PostMapping(value = "/create-category", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse createCategory(@Valid @RequestBody ServiceCategoryRequestDTO dto) {
        ServiceCategoryResponseDTO responseDTO = serviceCategoryService.createServiceCategory(dto);
        return new CommonResponse(201, responseDTO, "Service Category created successfully!");
    }

    // update service category
    @PutMapping(value = "/update/{categoryCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse updateCategory(@PathVariable String categoryCode, @Valid @RequestBody ServiceCategoryRequestDTO dto) {
        ServiceCategoryResponseDTO responseDTO = serviceCategoryService.updateServiceCategory(categoryCode, dto);
        return new CommonResponse(200, responseDTO, "Service Category updated successfully!");
    }

    // get all active categories
    @GetMapping(value = "/get-all", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllActiveCategories() {
        List<ServiceCategoryResponseDTO> categories = serviceCategoryService.getAllServiceCategories();
        return new CommonResponse(200, categories, "All active service categories fetched successfully!");
    }

    // get service category by code
    @GetMapping(value = "/get-by-code/{categoryCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getCategoryByCode(@PathVariable String categoryCode) {
        ServiceCategoryResponseDTO category = serviceCategoryService.getServiceCategoryByCode(categoryCode);
        return new CommonResponse(200, category, "Service Category fetched successfully!");
    }

    // change service category status
    @PatchMapping(value = "/change-status/{categoryCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse changeCategoryStatus(@PathVariable String categoryCode, @RequestParam(value = "status") String status) {
        boolean isUpdated = serviceCategoryService.changeServiceCategoryStatus(categoryCode, status);
        return new CommonResponse(200, isUpdated, "Service Category status changed successfully!");
    }
}
