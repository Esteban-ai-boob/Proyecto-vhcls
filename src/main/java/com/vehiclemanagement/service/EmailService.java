package com.vehiclemanagement.service;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio para envio de correos electronicos.
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    private final @NonNull String fromEmail;

    public EmailService(JavaMailSender mailSender, @Value("${spring.mail.username:}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail != null ? fromEmail : "";
    }

    /**
     * Enviar correo simple de notificacion
     */
    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            logger.info("Correo enviado exitosamente a: {}", to);
        } catch (org.springframework.mail.MailException e) {
            logger.error("Error al enviar correo a {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Error al enviar correo: " + e.getMessage());
        }
    }

    /**
     * Enviar correo con contenido HTML
     */
    public void sendHtmlEmail(@NonNull String to, @NonNull String subject, @NonNull String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = es HTML

            mailSender.send(message);
            logger.info("Correo HTML enviado exitosamente a: {}", to);
        } catch (MessagingException e) {
            logger.error("Error al enviar correo HTML a {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Error al enviar correo: " + e.getMessage());
        }
    }

    public void sendHtmlEmailWithAttachment(@NonNull String to, @NonNull String subject, @NonNull String htmlContent, byte[] attachmentData, @NonNull String attachmentFilename) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            if (attachmentData != null && attachmentData.length > 0) {
                org.springframework.core.io.ByteArrayResource resource = new org.springframework.core.io.ByteArrayResource(attachmentData);
                helper.addAttachment(attachmentFilename, resource);
            }

            mailSender.send(message);
            logger.info("Correo HTML con adjunto enviado exitosamente a: {}", to);
        } catch (MessagingException e) {
            logger.error("Error al enviar correo HTML con adjunto a {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Error al enviar correo: " + e.getMessage());
        }
    }

    /**
     * Enviar notificacion de documento cargado
     */
    public void sendDocumentUploadNotification(@NonNull String to, @NonNull String vehicleLicensePlate,
                                              @NonNull String documentName, @NonNull String documentCode, byte[] documentBlob) {
        String subject = "Notificación: Documento Cargado - " + vehicleLicensePlate;
        
        String htmlContent = String.format(
            "<html>" +
            "<head><meta charset='UTF-8'></head>" +
            "<body style='font-family: Arial, sans-serif; background-color: #f5f5f5;'>" +
            "<div style='max-width: 600px; margin: 20px auto; background-color: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>" +
            "<h2 style='color: #333; border-bottom: 3px solid #007bff; padding-bottom: 10px;'>✓ Documento Cargado Exitosamente</h2>" +
            "<p style='color: #555; line-height: 1.6;'>Hemos recibido la carga de un nuevo documento para su vehículo.</p>" +
            "<div style='background-color: #f8f9fa; padding: 15px; border-left: 4px solid #007bff; margin: 20px 0;'>" +
            "<p><strong>Detalles del Documento:</strong></p>" +
            "<ul style='list-style: none; padding: 0;'>" +
            "<li style='padding: 8px 0;'><strong>Placa del Vehículo:</strong> %s</li>" +
            "<li style='padding: 8px 0;'><strong>Nombre del Documento:</strong> %s</li>" +
            "<li style='padding: 8px 0;'><strong>Código del Documento:</strong> %s</li>" +
            "<li style='padding: 8px 0;'><strong>Fecha y Hora:</strong> " + java.time.LocalDateTime.now() + "</li>" +
            "</ul>" +
            "</div>" +
            "<p style='color: #666; font-size: 14px;'>" +
            "Si no realizó esta acción o tiene alguna pregunta, por favor contacte al administrador del sistema." +
            "</p>" +
            "<hr style='border: none; border-top: 1px solid #ddd; margin: 20px 0;'>" +
            "<p style='color: #999; font-size: 12px; text-align: center;'>" +
            "Sistema de Gestión de Vehículos y Documentos" +
            "</p>" +
            "</div>" +
            "</body>" +
            "</html>",
            vehicleLicensePlate, documentName, documentCode
        );

        if (documentBlob != null && documentBlob.length > 0) {
            sendHtmlEmailWithAttachment(to, subject, Objects.requireNonNull(htmlContent, "htmlContent no puede ser null"), documentBlob, documentName + ".jpg");
        } else {
            sendHtmlEmail(to, subject, Objects.requireNonNull(htmlContent, "htmlContent no puede ser null"));
        }
    }
}
