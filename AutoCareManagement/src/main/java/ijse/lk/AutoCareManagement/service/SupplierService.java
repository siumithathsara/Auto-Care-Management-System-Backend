package ijse.lk.AutoCareManagement.service;

import ijse.lk.AutoCareManagement.dto.SupplierRequestDTO;
import ijse.lk.AutoCareManagement.dto.SupplierResponseDTO;

import java.util.List;

public interface SupplierService {

    SupplierResponseDTO saveSupplier(SupplierRequestDTO dto);

    SupplierResponseDTO updateSupplier(String supplierCode, SupplierRequestDTO dto);

    SupplierResponseDTO getSupplierByCode(String supplierCode);

    List<SupplierResponseDTO> getAllSuppliers();

    List<SupplierResponseDTO> getAllActiveSuppliers();

    void toggleSupplierStatus(String supplierCode);
}
