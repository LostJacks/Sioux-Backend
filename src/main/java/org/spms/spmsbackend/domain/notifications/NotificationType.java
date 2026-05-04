package org.spms.spmsbackend.domain.notifications;

public enum NotificationType {
    /** Personal: the employee's own package has arrived. */
    ORDER_ARRIVED,
    /** Personal: pickup reminder for the employee's package. */
    PICKUP_REMINDER,
    /** Global: a package addressed to Sioux as a company. */
    COMPANY_PACKAGE,
    /** Global: a package whose recipient could not be resolved. */
    UNADDRESSED_PACKAGE
}
