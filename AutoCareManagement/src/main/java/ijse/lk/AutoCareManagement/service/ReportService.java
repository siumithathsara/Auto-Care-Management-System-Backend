package ijse.lk.AutoCareManagement.service;

import java.util.Map;

public interface ReportService {

    byte[] generateInvoicePdfByte(Map<String, Object> parameters);
}
