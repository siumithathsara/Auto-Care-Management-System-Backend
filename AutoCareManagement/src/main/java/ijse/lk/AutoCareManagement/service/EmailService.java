package ijse.lk.AutoCareManagement.service;

import java.util.Map;

public interface EmailService {

    void sendTemplateEmail(String toEmail, String subject, String templateName, Map<String, String> variables);
}
