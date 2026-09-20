package ijse.lk.AutoCareManagement.service.impl;

import ijse.lk.AutoCareManagement.dto.InvoiceRequestDTO;
import ijse.lk.AutoCareManagement.dto.InvoiceResponseDTO;
import ijse.lk.AutoCareManagement.entity.*;
import ijse.lk.AutoCareManagement.enumeration.JobStatus;
import ijse.lk.AutoCareManagement.enumeration.PaymentStatus;
import ijse.lk.AutoCareManagement.exception.CustomException;
import ijse.lk.AutoCareManagement.repository.InvoiceRepository;
import ijse.lk.AutoCareManagement.repository.JobCardRepository;
import ijse.lk.AutoCareManagement.repository.UserRepository;
import ijse.lk.AutoCareManagement.service.EmailService;
import ijse.lk.AutoCareManagement.service.InvoiceService;
import ijse.lk.AutoCareManagement.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final JobCardRepository jobCardRepository;
    private final UserRepository userRepository;

    private final ReportService reportService;
    private final EmailService emailService;

    @Override
    public InvoiceResponseDTO createInvoice(InvoiceRequestDTO dto, String username) {
        log.info("Creating Invoice for JobCard code: {}", dto.getJobCardCode());

        JobCard jobCard = jobCardRepository.findByJobCardCode(dto.getJobCardCode())
                .orElseThrow(() -> {
                    log.warn("Invoice creation failed: Job Card '{}' not found", dto.getJobCardCode());
                    return new CustomException(404, "Job Card not found!");
                });

        if (invoiceRepository.existsByJobCard_JobCardCode(dto.getJobCardCode())) {
            log.warn("Invoice creation failed: Invoice already exists for Job Card '{}'", dto.getJobCardCode());
            throw new CustomException(400, "An invoice has already been issued for this Job Card!");
        }

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User issuedBy = userRepository.findByUsername(currentUsername)
                .orElse(null);

        double totalServicesFee = 0.0;
        if (jobCard.getServices() != null) {
            for (JobCardServiceCategory jcs : jobCard.getServices()) {
                totalServicesFee += jcs.getPrice();
            }
        }

        double totalPartsFee = 0.0;
        if (jobCard.getItems() != null) {
            for (JobCardPart jcp : jobCard.getItems()) {
                totalPartsFee += jcp.getSubTotal();
            }
        }

        double subtotal = totalServicesFee + totalPartsFee;

        double taxRate = dto.getTaxPercentage() != null ? dto.getTaxPercentage() : 0.0;
        double discountRate = dto.getDiscountPercentage() != null ? dto.getDiscountPercentage() : 0.0;

        double taxAmount = subtotal * (taxRate / 100.0);
        double discount = subtotal * (discountRate / 100.0);

        double totalAmount = (subtotal + taxAmount) - discount;
        if (totalAmount < 0) {
            totalAmount = 0.0;
        }

        double paidAmount = dto.getPaidAmount() != null ? dto.getPaidAmount() : 0.0;
        double balanceAmount = paidAmount - totalAmount;

        PaymentStatus paymentStatus;
        if (paidAmount >= totalAmount) {
            paymentStatus = PaymentStatus.PAID;
        } else {
            paymentStatus = PaymentStatus.UNPAID;
        }

        String invoiceCode = generateInvoiceCode();

        Invoice invoice = new Invoice();
        invoice.setInvoiceCode(invoiceCode);
        invoice.setJobCard(jobCard);
        invoice.setIssuedBy(issuedBy);
        invoice.setSubtotal(subtotal);
        invoice.setTaxAmount(taxAmount);
        invoice.setDiscount(discount);
        invoice.setTotalAmount(totalAmount);
        invoice.setPaidAmount(paidAmount);
        invoice.setBalanceAmount(balanceAmount);
        invoice.setPaymentStatus(paymentStatus);
        invoice.setPaymentMethod(dto.getPaymentMethod());

        Invoice savedInvoice = invoiceRepository.save(invoice);

        jobCard.setStatus(JobStatus.COMPLETED);
        jobCard.setCheckOutTime(LocalDateTime.now());
        jobCardRepository.save(jobCard);

        log.info("Invoice created successfully with code: {}", invoiceCode);

        if (savedInvoice.getPaymentStatus() == PaymentStatus.PAID) {
            sendCustomerInvoiceEmailHelper(savedInvoice, jobCard);
        }

        return mapToResponseDTO(savedInvoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponseDTO getInvoiceByCode(String invoiceCode) {
        log.info("Fetching invoice with code: {}", invoiceCode);
        Optional<Invoice> invoiceOptional = invoiceRepository.findByInvoiceCode(invoiceCode);
        if (invoiceOptional.isEmpty()) {
            log.warn("Invoice with code '{}' not found", invoiceCode);
            throw new CustomException(404, "Invoice with code '" + invoiceCode + "' not found");
        }
        return mapToResponseDTO(invoiceOptional.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponseDTO> getAllInvoices() {
        log.info("Fetching all invoices");
        List<Invoice> invoices = invoiceRepository.findAll();
        List<InvoiceResponseDTO> responseDTOList = new ArrayList<>();

        for (Invoice invoice : invoices) {
            responseDTOList.add(mapToResponseDTO(invoice));
        }

        log.info("Successfully fetched {} invoices", responseDTOList.size());
        return responseDTOList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponseDTO> getInvoicesByCustomerCode(String customerUserCode) {
        log.info("Fetching invoices for customer code: {}", customerUserCode);
        if (!userRepository.existsByUserCode(customerUserCode)) {
            log.error("Customer invoices fetch failed: Customer not found with code: {}", customerUserCode);
            throw new CustomException(404, "Customer not found with code: " + customerUserCode);
        }

        List<Invoice> invoices = invoiceRepository.findByJobCard_Vehicle_Customer_UserCode(customerUserCode);
        List<InvoiceResponseDTO> responseDTOList = new ArrayList<>();

        for (Invoice invoice : invoices) {
            responseDTOList.add(mapToResponseDTO(invoice));
        }

        log.info("Found {} invoices for customer: {}", responseDTOList.size(), customerUserCode);
        return responseDTOList;
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponseDTO getInvoiceByJobCardCode(String jobCardCode) {
        log.info("Fetching invoice for JobCard code: {}", jobCardCode);
        Optional<Invoice> invoiceOptional = invoiceRepository.findByJobCard_JobCardCode(jobCardCode);
        if (invoiceOptional.isEmpty()) {
            log.warn("Invoice for JobCard '{}' not found", jobCardCode);
            throw new CustomException(404, "Invoice for JobCard '" + jobCardCode + "' not found");
        }
        return mapToResponseDTO(invoiceOptional.get());
    }

    private String generateInvoiceCode() {
        long currentCount = invoiceRepository.count();
        long nextNumber = currentCount + 1;
        int currentYear = Year.now().getValue();

        String generatedCode = String.format("INV-%d-%04d", currentYear, nextNumber);

        while (invoiceRepository.existsByInvoiceCode(generatedCode)) {
            nextNumber++;
            generatedCode = String.format("INV-%d-%04d", currentYear, nextNumber);
        }

        log.debug("Generated unique invoice code: {}", generatedCode);
        return generatedCode;
    }

    private InvoiceResponseDTO mapToResponseDTO(Invoice invoice) {
        InvoiceResponseDTO dto = new InvoiceResponseDTO();

        dto.setInvoiceId(invoice.getInvoiceId());
        dto.setInvoiceCode(invoice.getInvoiceCode());

        if (invoice.getJobCard() != null) {
            dto.setJobCardCode(invoice.getJobCard().getJobCardCode());

            if (invoice.getJobCard().getVehicle() != null) {
                var vehicle = invoice.getJobCard().getVehicle();
                dto.setVehicleCode(vehicle.getVehicleCode());
                dto.setLicensePlate(vehicle.getLicensePlate());
                dto.setBrand(vehicle.getBrand());
                dto.setModel(vehicle.getModel());

                if (vehicle.getCustomer() != null) {
                    var customer = vehicle.getCustomer();
                    dto.setCustomerUserCode(customer.getUserCode());
                    dto.setCustomerUsername(customer.getUsername());
                    dto.setCustomerPhone(customer.getPhone());
                    dto.setCustomerEmail(customer.getEmail());
                }
            }
        }

        if (invoice.getIssuedBy() != null) {
            dto.setIssuedByUsername(invoice.getIssuedBy().getUsername());
        }

        dto.setSubtotal(invoice.getSubtotal());
        dto.setTaxAmount(invoice.getTaxAmount());
        dto.setDiscount(invoice.getDiscount());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setPaidAmount(invoice.getPaidAmount());
        dto.setBalanceAmount(invoice.getBalanceAmount());
        dto.setPaymentStatus(invoice.getPaymentStatus());
        dto.setPaymentMethod(invoice.getPaymentMethod());
        dto.setIssuedDate(invoice.getIssuedDate());

        return dto;
    }

    private void sendCustomerInvoiceEmailHelper(Invoice invoice, JobCard jobCard) {
        if (jobCard.getVehicle() == null || jobCard.getVehicle().getCustomer() == null) return;

        User customer = jobCard.getVehicle().getCustomer();
        if (customer.getEmail() == null || customer.getEmail().isBlank()) return;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // 1. Email HTML Template එකට යන Data Map එක
        Map<String, Object> templateVars = new HashMap<>();
        templateVars.put("customerName",  customer.getUsername());
        templateVars.put("invoiceCode", invoice.getInvoiceCode());
        templateVars.put("licensePlate", jobCard.getVehicle().getLicensePlate());
        templateVars.put("totalAmount", String.format("%.2f", invoice.getTotalAmount()));

        // 2. Jasper JRXML එකට යන Parameters Map එක
        Map<String, Object> jasperParams = new HashMap<>();
        jasperParams.put("invoiceCode", invoice.getInvoiceCode());
        jasperParams.put("customerName", customer.getUsername());
        jasperParams.put("licensePlate", jobCard.getVehicle().getLicensePlate());
        jasperParams.put("vehicleInfo", jobCard.getVehicle().getBrand() + " " + jobCard.getVehicle().getModel());
        jasperParams.put("subtotal", invoice.getSubtotal());
        jasperParams.put("taxAmount", invoice.getTaxAmount());
        jasperParams.put("discount", invoice.getDiscount());
        jasperParams.put("totalAmount", invoice.getTotalAmount());
        jasperParams.put("paidAmount", invoice.getPaidAmount());
        jasperParams.put("balanceAmount", invoice.getBalanceAmount());
        jasperParams.put("issuedDate", invoice.getIssuedDate() != null ? invoice.getIssuedDate().format(formatter) : LocalDateTime.now().format(formatter));

        // 3. ඔයා හදපු ReportService එකෙන් Memory එකේ PDF Bytes එක හදාගැනීම
        byte[] pdfBytes = reportService.generateInvoicePdfByte(jasperParams);

        // 4. EmailService හරහා PDF Attachment එකත් එක්ක Email එක යැවීම
        emailService.sendInvoiceEmailWithPdf(
                customer.getEmail(),
                "Payment Receipt - " + invoice.getInvoiceCode(),
                "invoice-customer", // resources/templates/invoice-customer.html
                templateVars,
                pdfBytes,
                "Invoice_" + invoice.getInvoiceCode() + ".pdf"
        );
    }
}
