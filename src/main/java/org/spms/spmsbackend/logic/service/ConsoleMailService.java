package org.spms.spmsbackend.logic.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Development MailService that does not actually send anything — it just
 * writes the message to the application log. Active when
 * {@code spms.mail.enabled} is unset or {@code false}.
 */
@Service
@ConditionalOnProperty(name = "spms.mail.enabled", havingValue = "false", matchIfMissing = true)
public class ConsoleMailService implements MailService {

    private static final Logger log = LoggerFactory.getLogger(ConsoleMailService.class);

    @Override
    public void send(List<String> recipients, String subject, String body) {
        if (recipients == null || recipients.isEmpty()) {
            log.warn("[MAIL/console] skipped — no recipients (subject='{}')", subject);
            return;
        }
        log.info("[MAIL/console] to={} subject=\"{}\" body=\"{}\"",
                recipients, subject, body);
    }
}
