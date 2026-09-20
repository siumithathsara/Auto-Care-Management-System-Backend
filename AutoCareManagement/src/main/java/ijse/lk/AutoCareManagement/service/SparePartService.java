package ijse.lk.AutoCareManagement.service;

import ijse.lk.AutoCareManagement.dto.SparePartRequestDTO;
import ijse.lk.AutoCareManagement.dto.SparePartResponseDTO;

import java.util.List;

public interface SparePartService {

    SparePartResponseDTO saveSparePart(SparePartRequestDTO dto);

    SparePartResponseDTO getSparePartByCode(String partCode);

    List<SparePartResponseDTO> getAllSpareParts();

    List<SparePartResponseDTO> getLowStockSpareParts();

    SparePartResponseDTO updateSparePart(String partCode, SparePartRequestDTO dto);

    void deleteSparePart(String partCode);

    SparePartResponseDTO deductStock(String partCode, int quantityToDeduct);
}
