package ijse.lk.AutoCareManagement.service.impl;

import ijse.lk.AutoCareManagement.service.ReportService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    @Override
    public byte[] generateInvoicePdfByte(Map<String, Object> parameters) {
        try {
            // 1. JRXML file එක Classpath එකෙන් load කිරීම
            InputStream inputStream = new ClassPathResource("report/invoice-template.jrxml").getInputStream();

            // 2. JRXML File එක Compile කිරීම
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

            // 3. Data Source එකක් නැතිව Parameters පමණක් භාවිතා කරන්නේ නම් JREmptyDataSource ලබා දීම
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            // 4. PDF byte array එක Return කිරීම
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (Exception e) {
            throw new RuntimeException("Jasper PDF Generation Failed: " + e.getMessage(), e);
        }
    }
}
