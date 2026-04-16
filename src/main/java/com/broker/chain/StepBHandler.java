package com.broker.chain;

import com.broker.model.BaseRetryJob;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class StepBHandler implements RetryHandler {
    private static final Logger log = LoggerFactory.getLogger(StepBHandler.class);
    private RetryHandler next;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public StepBHandler(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void setNext(RetryHandler next) {
        this.next = next;
    }

    @Override
    public void handle(BaseRetryJob job) {
        log.info("Executing Step B (Send Real Email) for job ID: {}", job.getId());
        
        try {
            boolean success = "SUCCESS".equals(job.getStatusA());
            String emailMessage = getEmailMessage(job.getJobType(), success);
            String emailStatus = success ? "SUCCESS" : "FAILED";

            sendEmail(fromEmail, "Notificación de Microservicio: " + job.getJobType(), emailMessage);

            log.info("Email sent successfully to {}. Message: {}", fromEmail, emailMessage);

            ObjectNode rootNode = (ObjectNode) objectMapper.readTree(job.getData());
            ObjectNode emailNode = (ObjectNode) rootNode.path("sendEmail");
            if (emailNode.isMissingNode()) {
                emailNode = rootNode.putObject("sendEmail");
            }
            emailNode.put("status", emailStatus);
            emailNode.put("message", emailMessage);
            
            job.setData(objectMapper.writeValueAsString(rootNode));
            job.setStatusB("SUCCESS");
        } catch (Exception e) {
            job.setStatusB("FAILED");
            log.error("Step B failed (Email error) for job ID: {}. Error: {}", job.getId(), e.getMessage());
        }

        if (next != null) next.handle(job);
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    private String getEmailMessage(String type, boolean success) {
        if (success) {
            return switch (type) {
                case "PAYMENT" -> "Pago creado correctamente";
                case "ORDER" -> "Orden creada correctamente";
                case "PRODUCT" -> "Producto creado correctamente";
                default -> "Operación exitosa";
            };
        } else {
            return switch (type) {
                case "PAYMENT" -> "Error al procesar el pago";
                case "ORDER" -> "Error al crear la orden";
                case "PRODUCT" -> "Error al crear el producto";
                default -> "Error en la operación";
            };
        }
    }
}
