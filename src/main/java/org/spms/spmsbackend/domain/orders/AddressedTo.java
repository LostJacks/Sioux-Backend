package org.spms.spmsbackend.domain.orders;

/**
 * To whom a package is addressed.
 *
 * <ul>
 *     <li>{@link #EMPLOYEE} – addressed to a specific employee on file.</li>
 *     <li>{@link #COMPANY} – addressed to Sioux as a company (e.g. business
 *         mail). Triggers a global notification fan-out to managers.</li>
 *     <li>{@link #UNKNOWN} – the recipient could not be resolved at
 *         registration time. Treated like COMPANY for notification purposes
 *         so it does not silently drop on the floor.</li>
 * </ul>
 */
public enum AddressedTo {
    EMPLOYEE,
    COMPANY,
    UNKNOWN
}
