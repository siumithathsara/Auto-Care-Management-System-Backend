package ijse.lk.AutoCareManagement.controller;

import ijse.lk.AutoCareManagement.constant.CommonResponse;
import ijse.lk.AutoCareManagement.dto.InvoiceRequestDTO;
import ijse.lk.AutoCareManagement.dto.InvoiceResponseDTO;
import ijse.lk.AutoCareManagement.service.InvoiceService;
import ijse.lk.AutoCareManagement.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final ReportService reportService;



// create invoice
@PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasAnyAuthority('ADMIN')")
public CommonResponse createInvoice(@Valid @RequestBody InvoiceRequestDTO dto,
                                    Authentication authentication) {
    String username = authentication.getName();

    InvoiceResponseDTO createdInvoice = invoiceService.createInvoice(dto, username);

    Map<String, Object> responseData = new HashMap<>();
    responseData.put("invoice", createdInvoice);

    try {
        Map<String, Object> jasperParams = new HashMap<>();

        jasperParams.put("invoiceCode", createdInvoice.getInvoiceCode());
        jasperParams.put("issuedDate", createdInvoice.getIssuedDate() != null ? createdInvoice.getIssuedDate().toString() : "N/A");
        jasperParams.put("paymentStatus", createdInvoice.getPaymentStatus() != null ? createdInvoice.getPaymentStatus().name() : "N/A");
        jasperParams.put("paymentMethod", createdInvoice.getPaymentMethod() != null ? createdInvoice.getPaymentMethod().name() : "Cash / Card");

        jasperParams.put("customerName", createdInvoice.getCustomerUsername());
        jasperParams.put("customerPhone", createdInvoice.getCustomerPhone());

        jasperParams.put("vehicleNo", createdInvoice.getLicensePlate());
        jasperParams.put("vehicleModel", createdInvoice.getModel());

        jasperParams.put("subtotal", createdInvoice.getSubtotal());
        jasperParams.put("taxAmount", createdInvoice.getTaxAmount());
        jasperParams.put("discount", createdInvoice.getDiscount());
        jasperParams.put("totalAmount", createdInvoice.getTotalAmount());
        jasperParams.put("paidAmount", createdInvoice.getPaidAmount());
        jasperParams.put("balanceAmount", createdInvoice.getBalanceAmount());

        byte[] pdfBytes = reportService.generateInvoicePdfByte(jasperParams);
        String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);

        responseData.put("pdfBase64", base64Pdf);
    } catch (Exception e) {
        e.printStackTrace();
        responseData.put("pdfBase64", null);
    }

    return new CommonResponse(201, responseData, "Invoice created successfully!");
}

//     get invoice by code
    @GetMapping(value = "/get-by-code/{invoiceCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    public CommonResponse getInvoiceByCode(@PathVariable String invoiceCode) {
        InvoiceResponseDTO invoice = invoiceService.getInvoiceByCode(invoiceCode);
        return new CommonResponse(200, invoice, "Invoice fetched successfully!");
    }

//     get all invoices
    @GetMapping(value = "/get-all",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public CommonResponse getAllInvoices() {
        List<InvoiceResponseDTO> invoices = invoiceService.getAllInvoices();
        return new CommonResponse(200, invoices, "All invoices fetched successfully!");
    }

//     get invoices by customer code
    @GetMapping(value = "/customer/{customerUserCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN',  'CUSTOMER')")
    public CommonResponse getInvoicesByCustomerCode(@PathVariable String customerUserCode) {
        List<InvoiceResponseDTO> invoices = invoiceService.getInvoicesByCustomerCode(customerUserCode);
        return new CommonResponse(200, invoices, "Customer invoices fetched successfully!");
    }

//     get invoice by job card code
    @GetMapping(value = "/job-card/{jobCardCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    public CommonResponse getInvoiceByJobCardCode(@PathVariable String jobCardCode) {
        InvoiceResponseDTO invoice = invoiceService.getInvoiceByJobCardCode(jobCardCode);
        return new CommonResponse(200, invoice, "Job card invoice fetched successfully!");
    }
}
