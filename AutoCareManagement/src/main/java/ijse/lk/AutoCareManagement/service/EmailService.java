package ijse.lk.AutoCareManagement.service;

public interface EmailService {

    void sendSimpleEmail(String toEmail, String subject, String body);
    void sendHtmlEmail(String toEmail, String subject, String htmlBody);
}
