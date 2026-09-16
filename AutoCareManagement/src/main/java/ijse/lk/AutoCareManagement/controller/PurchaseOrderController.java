package ijse.lk.AutoCareManagement.controller;

import ijse.lk.AutoCareManagement.constant.CommonResponse;
import ijse.lk.AutoCareManagement.dto.PurchaseOrderRequestDTO;
import ijse.lk.AutoCareManagement.dto.PurchaseOrderResponseDTO;
import ijse.lk.AutoCareManagement.service.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/purchase-order")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

//     Create new purchase order (Only ADMIN can create)
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public CommonResponse createPurchaseOrder(@Valid @RequestBody PurchaseOrderRequestDTO dto, Authentication authentication) {
        String username = authentication.getName();
        PurchaseOrderResponseDTO responseDTO = purchaseOrderService.createPurchaseOrder(dto, username);
        return new CommonResponse(201, responseDTO, "Purchase Order created successfully!");
    }

//     Get purchase order details by PO code
    @GetMapping(value = "/get-by-code/{poCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public CommonResponse getPurchaseOrderByCode(@PathVariable String poCode) {
        PurchaseOrderResponseDTO responseDTO = purchaseOrderService.getPurchaseOrderByCode(poCode);
        return new CommonResponse(200, responseDTO, "Purchase Order fetched successfully!");
    }

//     Get all purchase orders
    @GetMapping(value = "/get-all", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public CommonResponse getAllPurchaseOrders() {
        List<PurchaseOrderResponseDTO> purchaseOrders = purchaseOrderService.getAllPurchaseOrders();
        return new CommonResponse(200, purchaseOrders, "All purchase orders fetched successfully!");
    }

//     Cancel purchase order
    @PatchMapping(value = "/cancel/{poCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public CommonResponse cancelPurchaseOrder(@PathVariable String poCode) {
        purchaseOrderService.cancelPurchaseOrder(poCode);
        return new CommonResponse(200, null, "Purchase Order cancelled successfully!");
    }

//     Mark Order as Received and Update Stock
    @PatchMapping(value = "/mark-as-received/{poCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public CommonResponse markAsReceived(@PathVariable String poCode) {
        PurchaseOrderResponseDTO responseDTO = purchaseOrderService.markAsReceived(poCode);
        return new CommonResponse(200, responseDTO, "Purchase Order marked as RECEIVED and stock updated!");
    }
}
