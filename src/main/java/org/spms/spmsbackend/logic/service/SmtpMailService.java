package org.spms.spmsbackend.logic.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Production MailService backed by Spring's {@link JavaMailSender}. Active
 * when {@code spms.mail.enabled=true}.
 *
 * <p>One message is built per recipient (rather than one message with many
 * To: addresses) so a bad address does not block delivery for the others.</p>
 */
@Service
@ConditionalOnProperty(name = "spms.mail.enabled", havingValue = "true")
public class SmtpMailService implements MailService {

    private static final Logger log = LoggerFactory.getLogger(SmtpMailService.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public SmtpMailService(JavaMailSender mailSender,
                           @Value("${spms.mail.from:noreply@sioux.local}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void send(List<String> recipients, String subject, String body) {
        if (recipients == null || recipients.isEmpty()) {
            log.warn("[MAIL/smtp] skipped — no recipients (subject='{}')", subject);
            return;
        }
        for (String to : recipients) {
            if (to == null || to.isBlank()) {
                continue;
            }
            try {
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setFrom(fromAddress);
                msg.setTo(to);
                msg.setSubject(subject);
                msg.setText(body);
                mailSender.send(msg);
                log.debug("[MAIL/smtp] sent to={} subject='{}'", to, subject);
            } catch (Exception ex) {
                log.warn("[MAIL/smtp] failed to send to {}: {}", to, ex.getMessage());
            }
        }
    }
}
