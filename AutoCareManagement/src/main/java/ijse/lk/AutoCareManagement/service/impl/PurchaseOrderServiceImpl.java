package ijse.lk.AutoCareManagement.service.impl;

import ijse.lk.AutoCareManagement.dto.PurchaseOrderItemRequestDTO;
import ijse.lk.AutoCareManagement.dto.PurchaseOrderItemResponseDTO;
import ijse.lk.AutoCareManagement.dto.PurchaseOrderRequestDTO;
import ijse.lk.AutoCareManagement.dto.PurchaseOrderResponseDTO;
import ijse.lk.AutoCareManagement.entity.*;
import ijse.lk.AutoCareManagement.enumeration.PoStatus;
import ijse.lk.AutoCareManagement.enumeration.Role;
import ijse.lk.AutoCareManagement.exception.CustomException;
import ijse.lk.AutoCareManagement.service.PurchaseOrderService;
import ijse.lk.AutoCareManagement.repository.*;
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
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository poRepository;
    private final PurchaseOrderItemRepository poItemRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final SparePartRepository sparePartRepository;

    @Override
    public PurchaseOrderResponseDTO createPurchaseOrder(PurchaseOrderRequestDTO dto, String username) {
        log.info("Creating new purchase order for supplier code: {} by user: {}", dto.getSupplierCode(), username);

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.warn("Purchase order creation failed: User '{}' not found", username);
            throw new CustomException(404, "User with username '" + username + "' not found");
        }

        User user = userOptional.get();
        if (user.getRole() != Role.ADMIN) {
            log.warn("Purchase order creation failed: User '{}' is not an ADMIN", username);
            throw new CustomException(403, "Only ADMIN users are authorized to create purchase orders");
        }

        Optional<Supplier> supplierOptional = supplierRepository.findBySupplierCode(dto.getSupplierCode());
        if (supplierOptional.isEmpty()) {
            log.warn("Purchase order creation failed: Supplier code '{}' not found", dto.getSupplierCode());
            throw new CustomException(404, "Supplier with code '" + dto.getSupplierCode() + "' not found");
        }

        String generatedPoCode = generateNextPoCode();

        PurchaseOrder po = new PurchaseOrder();
        po.setPoCode(generatedPoCode);
        po.setSupplier(supplierOptional.get());
        po.setCreatedBy(user);
        po.setExpectedDeliveryDate(dto.getExpectedDeliveryDate());
        po.setStatus(PoStatus.CREATED);

        double totalAmount = 0.0;
        List<PurchaseOrderItem> itemList = new ArrayList<>();

        for (PurchaseOrderItemRequestDTO itemDTO : dto.getItems()) {
            Optional<SparePart> sparePartOptional = sparePartRepository.findByPartCode(itemDTO.getPartCode());
            if (sparePartOptional.isEmpty()) {
                log.warn("Purchase order creation failed: Spare part code '{}' not found", itemDTO.getPartCode());
                throw new CustomException(404, "Spare Part with code '" + itemDTO.getPartCode() + "' not found");
            }

            double subTotal = itemDTO.getOrderedQty() * itemDTO.getUnitCost();
            totalAmount += subTotal;

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setPurchaseOrder(po);
            item.setSparePart(sparePartOptional.get());
            item.setOrderedQty(itemDTO.getOrderedQty());
            item.setUnitCost(itemDTO.getUnitCost());
            item.setSubTotal(subTotal);

            itemList.add(item);
        }

        po.setTotalAmount(totalAmount);

        PurchaseOrder savedPo = poRepository.saveAndFlush(po);

        for (PurchaseOrderItem item : itemList) {
            item.setPurchaseOrder(savedPo);
        }

        List<PurchaseOrderItem> savedItems = poItemRepository.saveAll(itemList);

        log.info("Purchase order successfully created with code: {}", generatedPoCode);

        return mapToResponseDTO(savedPo, savedItems);
    }

    @Override
    public PurchaseOrderResponseDTO getPurchaseOrderByCode(String poCode) {
        log.info("Fetching purchase order with code: {}", poCode);

        Optional<PurchaseOrder> poOptional = poRepository.findByPoCode(poCode);
        if (poOptional.isEmpty()) {
            log.warn("Purchase order with code '{}' not found", poCode);
            throw new CustomException(404, "Purchase Order with code '" + poCode + "' not found");
        }

        List<PurchaseOrderItem> items = poItemRepository.findByPurchaseOrder_PoCode(poCode);
        return mapToResponseDTO(poOptional.get(), items);
    }

    @Override
    public List<PurchaseOrderResponseDTO> getAllPurchaseOrders() {
        log.info("Fetching all purchase orders");

        List<PurchaseOrder> poList = poRepository.findAll();
        List<PurchaseOrderResponseDTO> responseDTOList = new ArrayList<>();

        for (PurchaseOrder po : poList) {
            List<PurchaseOrderItem> items = poItemRepository.findByPurchaseOrder_PoCode(po.getPoCode());
            responseDTOList.add(mapToResponseDTO(po, items));
        }

        log.info("Successfully retrieved {} purchase orders", responseDTOList.size());
        return responseDTOList;
    }

    @Override
    public void cancelPurchaseOrder(String poCode) {
        log.info("Cancelling purchase order with code: {}", poCode);

        Optional<PurchaseOrder> poOptional = poRepository.findByPoCode(poCode);
        if (poOptional.isEmpty()) {
            log.warn("Purchase order cancellation failed: Code '{}' not found", poCode);
            throw new CustomException(404, "Purchase Order with code '" + poCode + "' not found");
        }

        PurchaseOrder po = poOptional.get();
        if (po.getStatus() == PoStatus.RECEIVED || po.getStatus() == PoStatus.COMPLETED) {
            log.warn("Purchase order cancellation failed: Code '{}' is already in status {}", poCode, po.getStatus());
            throw new CustomException(400, "Cannot cancel an order that is already received or completed");
        }

        po.setStatus(PoStatus.CANCELLED);
        poRepository.save(po);
        log.info("Purchase order with code '{}' successfully cancelled", poCode);
    }

    @Override
    public PurchaseOrderResponseDTO markAsReceived(String poCode) {
        log.info("Marking Purchase Order as RECEIVED: {}", poCode);

        Optional<PurchaseOrder> poOptional = poRepository.findByPoCode(poCode);
        if (poOptional.isEmpty()) {
            log.warn("PO mark as received failed: Code '{}' not found", poCode);
            throw new CustomException(404, "Purchase Order with code '" + poCode + "' not found");
        }

        PurchaseOrder po = poOptional.get();

        if (po.getStatus() == PoStatus.RECEIVED || po.getStatus() == PoStatus.COMPLETED) {
            throw new CustomException(400, "Purchase Order is already received or completed");
        }

        if (po.getStatus() == PoStatus.CANCELLED) {
            throw new CustomException(400, "Cannot receive a cancelled Purchase Order");
        }

        List<PurchaseOrderItem> items = poItemRepository.findByPurchaseOrder_PoCode(poCode);

        for (PurchaseOrderItem item : items) {
            SparePart sparePart = item.getSparePart();

            sparePart.setQuantityInStock(sparePart.getQuantityInStock() + item.getOrderedQty());
            sparePartRepository.save(sparePart);

            item.setReceivedQty(item.getOrderedQty());
            poItemRepository.save(item);
        }

        po.setStatus(PoStatus.RECEIVED);
        PurchaseOrder updatedPo = poRepository.save(po);

        log.info("Purchase Order '{}' successfully marked as RECEIVED and Stock updated", poCode);

        return mapToResponseDTO(updatedPo, items);
    }

    private String generateNextPoCode() {
        long currentCount = poRepository.count();
        long nextNumber = currentCount + 1;
        int currentYear = Year.now().getValue();

        String generatedCode = String.format("PO-%d-%04d", currentYear, nextNumber);

        while (poRepository.findByPoCode(generatedCode).isPresent()) {
            nextNumber++;
            generatedCode = String.format("PO-%d-%04d", currentYear, nextNumber);
        }

        log.debug("Generated unique purchase order code: {}", generatedCode);
        return generatedCode;
    }

    private PurchaseOrderResponseDTO mapToResponseDTO(PurchaseOrder po, List<PurchaseOrderItem> items) {
        List<PurchaseOrderItemResponseDTO> itemDTOs = new ArrayList<>();

        for (PurchaseOrderItem item : items) {
            PurchaseOrderItemResponseDTO itemDTO = new PurchaseOrderItemResponseDTO();
            itemDTO.setPartCode(item.getSparePart().getPartCode());
            itemDTO.setPartName(item.getSparePart().getPartName());
            itemDTO.setOrderedQty(item.getOrderedQty());
            itemDTO.setReceivedQty(item.getReceivedQty());
            itemDTO.setUnitCost(item.getUnitCost());
            itemDTO.setSubTotal(item.getSubTotal());
            itemDTOs.add(itemDTO);
        }

        PurchaseOrderResponseDTO dto = new PurchaseOrderResponseDTO();
        dto.setPoCode(po.getPoCode());
        dto.setSupplierCode(po.getSupplier().getSupplierCode());
        dto.setSupplierName(po.getSupplier().getCompanyName());
        dto.setCreatedByUserCode(po.getCreatedBy().getUserCode());
        dto.setCreatedByUserName(po.getCreatedBy().getUsername());
        dto.setTotalAmount(po.getTotalAmount());
        dto.setOrderDate(po.getOrderDate());
        dto.setExpectedDeliveryDate(po.getExpectedDeliveryDate());
        dto.setStatus(po.getStatus());
        dto.setItems(itemDTOs);

        return dto;
    }
}
