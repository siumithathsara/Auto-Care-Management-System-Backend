package ijse.lk.AutoCareManagement.service.impl;

import ijse.lk.AutoCareManagement.service.EmailService;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final ResourceLoader resourceLoader;

    @Value("${spring.mail.username}")
    private String adminEmail;

    @Override
    @Async
    public void sendTemplateEmail(String toEmail, String subject, String templateName, Map<String, String> variables) {
        try {

            Resource resource = resourceLoader.getResource("classpath:templates/" + templateName + ".html");
            String htmlContent = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

            if (variables != null) {
                for (Map.Entry<String, String> entry : variables.entrySet()) {
                    String value = entry.getValue() != null ? entry.getValue() : "";
                    htmlContent = htmlContent.replace("{{" + entry.getKey() + "}}", value);
                }
            }

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(adminEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Template email [{}] sent successfully to: {}", templateName, toEmail);

        } catch (Exception e) {
            log.error("Failed to send template email [{}] to {}: {}", templateName, toEmail, e.getMessage());
        }
    }

    @Override
    public void sendInvoiceEmailWithPdf(
            String to,
            String subject,
            String templateName,
            Map<String, Object> templateVars,
            byte[] pdfBytes,
            String pdfFileName) {

        try {

            ClassPathResource templateResource = new ClassPathResource("templates/" + templateName + ".html");
            String htmlContent = new String(templateResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            if (templateVars != null) {
                for (Map.Entry<String, Object> entry : templateVars.entrySet()) {
                    String placeholder = "{{" + entry.getKey() + "}}";
                    String value = entry.getValue() != null ? entry.getValue().toString() : "";
                    htmlContent = htmlContent.replace(placeholder, value);
                }
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.addAttachment(pdfFileName, new ByteArrayResource(pdfBytes));

            mailSender.send(message);
            log.info("Invoice Email with PDF Attachment successfully sent to {}", to);

        } catch (Exception e) {
            log.error("Failed to send Invoice Email with PDF to {}: {}", to, e.getMessage());
        }
    }
}
