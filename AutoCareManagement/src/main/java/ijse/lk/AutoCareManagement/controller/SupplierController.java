package ijse.lk.AutoCareManagement.controller;

import ijse.lk.AutoCareManagement.constant.CommonResponse;
import ijse.lk.AutoCareManagement.dto.SupplierRequestDTO;
import ijse.lk.AutoCareManagement.dto.SupplierResponseDTO;
import ijse.lk.AutoCareManagement.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

//     Supplier register
    @PostMapping(value = "/register-supplier", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse saveSupplier(@Valid @RequestBody SupplierRequestDTO dto) {
        SupplierResponseDTO savedSupplier = supplierService.saveSupplier(dto);
        return new CommonResponse(201, savedSupplier, "Supplier registered successfully!");
    }

//     Supplier update by supplier code
    @PutMapping(value = "/update-supplier/{supplierCode}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse updateSupplier(@PathVariable String supplierCode, @Valid @RequestBody SupplierRequestDTO dto) {
        SupplierResponseDTO updatedSupplier = supplierService.updateSupplier(supplierCode, dto);
        return new CommonResponse(200, updatedSupplier, "Supplier updated successfully!");
    }

//     Supplier get by supplier code
    @GetMapping(value = "/get-supplier/{supplierCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse getSupplierByCode(@PathVariable String supplierCode) {
        SupplierResponseDTO supplier = supplierService.getSupplierByCode(supplierCode);
        return new CommonResponse(200, supplier, "Supplier fetched successfully!");
    }

//     Get all suppliers
    @GetMapping(value = "/get-all-suppliers", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse getAllSuppliers() {
        List<SupplierResponseDTO> list = supplierService.getAllSuppliers();
        return new CommonResponse(200, list, "Suppliers fetched successfully!");
    }

//     Get all active suppliers
    @GetMapping(value = "/get-all-active-suppliers", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse getAllActiveSuppliers() {
        List<SupplierResponseDTO> list = supplierService.getAllActiveSuppliers();
        return new CommonResponse(200, list, "Active suppliers fetched successfully!");
    }

//     Toggle active status
    @PatchMapping(value = "/{supplierCode}/toggle-status", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse toggleSupplierStatus(@PathVariable String supplierCode) {
        supplierService.toggleSupplierStatus(supplierCode);
        return new CommonResponse(200, null, "Supplier status toggled successfully!");
    }
}
