package org.spms.spmsbackend.domain.notifications;

/**
 * Whom the notification copy belongs to.
 *
 * <p>{@link #PERSONAL} means the notification is addressed to the recipient
 * because the package itself is theirs.</p>
 *
 * <p>{@link #GLOBAL} means this is a manager-side copy of a Sioux/unaddressed
 * package alert. Each targeted manager gets their own row with scope=GLOBAL
 * so per-user read state still works.</p>
 */
public enum NotificationScope {
    PERSONAL,
    GLOBAL
}
