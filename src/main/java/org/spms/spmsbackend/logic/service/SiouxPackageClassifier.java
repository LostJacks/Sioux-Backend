package org.spms.spmsbackend.logic.service;

import org.spms.spmsbackend.domain.orders.AddressedTo;
import org.spms.spmsbackend.entity.PackageEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Decides whether a {@link PackageEntity} is addressed to a specific
 * employee, to Sioux as a company, or whether the recipient cannot be
 * resolved at all.
 *
 * <p>Rules (US-03):</p>
 * <ol>
 *     <li>If the package has a linked employee &rarr; {@link AddressedTo#EMPLOYEE}.</li>
 *     <li>If the recipient name matches the configured company name (e.g. "Sioux")
 *         and there is no employee &rarr; {@link AddressedTo#COMPANY}.</li>
 *     <li>Otherwise &rarr; {@link AddressedTo#UNKNOWN} (still escalated to
 *         managers, just under a different label).</li>
 * </ol>
 */
@Component
public class SiouxPackageClassifier {

    private final String companyName;

    public SiouxPackageClassifier(@Value("${spms.company.name:Sioux}") String companyName) {
        this.companyName = companyName == null ? "Sioux" : companyName.trim();
    }

    public AddressedTo classify(PackageEntity pkg) {
        if (pkg == null) {
            return AddressedTo.UNKNOWN;
        }
        if (pkg.getEmployee() != null && pkg.getEmployee().getId() != null) {
            return AddressedTo.EMPLOYEE;
        }

        String recipient = pkg.getRecipientName();
        if (recipient != null && !recipient.isBlank()
                && recipient.toLowerCase().contains(companyName.toLowerCase())) {
            return AddressedTo.COMPANY;
        }
        return AddressedTo.UNKNOWN;
    }

    public String getCompanyName() {
        return companyName;
    }
}
