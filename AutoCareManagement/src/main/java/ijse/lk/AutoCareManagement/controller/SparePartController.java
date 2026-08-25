package ijse.lk.AutoCareManagement.controller;

import ijse.lk.AutoCareManagement.constant.CommonResponse;
import ijse.lk.AutoCareManagement.dto.SparePartRequestDTO;
import ijse.lk.AutoCareManagement.dto.SparePartResponseDTO;
import ijse.lk.AutoCareManagement.service.SparePartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/spare-part")
@RequiredArgsConstructor
public class SparePartController {

    private final SparePartService sparePartService;

//     save new spare part
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse saveSparePart(@Valid @RequestBody SparePartRequestDTO dto) {
        SparePartResponseDTO response = sparePartService.saveSparePart(dto);
        return new CommonResponse(201, response, "Spare Part saved successfully!");
    }

//     get spare part details by code
    @GetMapping(value = "/get-by-code/{partCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR', 'MECHANIC')")
    public CommonResponse getSparePartByCode(@PathVariable String partCode) {
        SparePartResponseDTO response = sparePartService.getSparePartByCode(partCode);
        return new CommonResponse(200, response, "Spare Part fetched successfully!");
    }

//     get all active spare parts
    @GetMapping(value = "/get-all", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ADVISOR', 'MECHANIC')")
    public CommonResponse getAllSpareParts() {
        List<SparePartResponseDTO> list = sparePartService.getAllSpareParts();
        return new CommonResponse(200, list, "All Spare Parts fetched successfully!");
    }

//     get low stock spare parts alert list
    @GetMapping(value = "/get-low-stock", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse getLowStockSpareParts() {
        List<SparePartResponseDTO> list = sparePartService.getLowStockSpareParts();
        return new CommonResponse(200, list, "Low stock Spare Parts fetched successfully!");
    }

//     update spare part by code
    @PutMapping(value = "/update/{partCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse updateSparePart(@PathVariable String partCode, @Valid @RequestBody SparePartRequestDTO dto) {
        SparePartResponseDTO response = sparePartService.updateSparePart(partCode, dto);
        return new CommonResponse(200, response, "Spare Part updated successfully!");
    }

//     delete spare part by code
    @DeleteMapping(value = "/delete/{partCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse deleteSparePart(@PathVariable String partCode) {
        sparePartService.deleteSparePart(partCode);
        return new CommonResponse(200, null, "Spare Part deleted successfully!");
    }

}
