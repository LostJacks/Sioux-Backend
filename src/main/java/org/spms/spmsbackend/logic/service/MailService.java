package org.spms.spmsbackend.logic.service;

import java.util.List;

/**
 * Outbound email port. The active implementation is selected by the
 * {@code spms.mail.enabled} property:
 * <ul>
 *     <li>{@code false} (dev default) &rarr; {@link ConsoleMailService}, logs only.</li>
 *     <li>{@code true} &rarr; {@link SmtpMailService}, real SMTP via Spring's
 *         JavaMailSender.</li>
 * </ul>
 */
public interface MailService {

    /**
     * Send a plain-text email to the given recipients. Implementations are
     * expected to swallow per-recipient failures and log them; callers should
     * not block business flows on mail delivery.
     */
    void send(List<String> recipients, String subject, String body);
}
