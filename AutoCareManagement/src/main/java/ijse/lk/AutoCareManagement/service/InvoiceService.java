package ijse.lk.AutoCareManagement.service;

import ijse.lk.AutoCareManagement.dto.InvoiceRequestDTO;
import ijse.lk.AutoCareManagement.dto.InvoiceResponseDTO;

import java.util.List;

public interface InvoiceService {

    InvoiceResponseDTO createInvoice(InvoiceRequestDTO dto, String username);

    InvoiceResponseDTO getInvoiceByCode(String invoiceCode);

    List<InvoiceResponseDTO> getAllInvoices();

    List<InvoiceResponseDTO> getInvoicesByCustomerCode(String customerUserCode);

    InvoiceResponseDTO getInvoiceByJobCardCode(String jobCardCode);
}
