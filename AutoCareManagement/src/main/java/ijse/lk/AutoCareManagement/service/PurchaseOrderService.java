package ijse.lk.AutoCareManagement.service;

import ijse.lk.AutoCareManagement.dto.PurchaseOrderRequestDTO;
import ijse.lk.AutoCareManagement.dto.PurchaseOrderResponseDTO;

import java.util.List;

public interface PurchaseOrderService {
    PurchaseOrderResponseDTO createPurchaseOrder(PurchaseOrderRequestDTO dto, String username);

    PurchaseOrderResponseDTO getPurchaseOrderByCode(String poCode);

    List<PurchaseOrderResponseDTO> getAllPurchaseOrders();

    void cancelPurchaseOrder(String poCode);

    PurchaseOrderResponseDTO markAsReceived(String poCode);

}
