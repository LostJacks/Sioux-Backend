package org.spms.spmsbackend.logic.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spms.spmsbackend.domain.orders.AddressedTo;
import org.spms.spmsbackend.entity.PackageEntity;
import org.spms.spmsbackend.entity.UserEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pure unit tests for {@link SiouxPackageClassifier}.
 * No Spring context needed — the classifier has no external dependencies.
 */
class SiouxPackageClassifierTest {

    private SiouxPackageClassifier classifier;

    @BeforeEach
    void setUp() {
        classifier = new SiouxPackageClassifier("Sioux");
    }

    // ── EMPLOYEE ────────────────────────────────────────────────────────────

    @Test
    void classify_packageWithEmployee_returnsEmployee() {
        PackageEntity pkg = new PackageEntity();
        UserEntity employee = new UserEntity();
        employee.setId(42);
        pkg.setEmployee(employee);

        assertEquals(AddressedTo.EMPLOYEE, classifier.classify(pkg));
    }

    // ── COMPANY ─────────────────────────────────────────────────────────────

    @Test
    void classify_packageWithSiouxRecipient_returnsCompany() {
        PackageEntity pkg = new PackageEntity();
        pkg.setRecipientName("Sioux Technologies");

        assertEquals(AddressedTo.COMPANY, classifier.classify(pkg));
    }

    @Test
    void classify_recipientMatchIsCaseInsensitive() {
        PackageEntity pkg = new PackageEntity();
        pkg.setRecipientName("SIOUX B.V.");

        assertEquals(AddressedTo.COMPANY, classifier.classify(pkg));
    }

    @Test
    void classify_customCompanyName_matchesConfiguredName() {
        SiouxPackageClassifier custom = new SiouxPackageClassifier("Acme");
        PackageEntity pkg = new PackageEntity();
        pkg.setRecipientName("Acme Corp");

        assertEquals(AddressedTo.COMPANY, custom.classify(pkg));
    }

    // ── UNKNOWN ─────────────────────────────────────────────────────────────

    @Test
    void classify_nullPackage_returnsUnknown() {
        assertEquals(AddressedTo.UNKNOWN, classifier.classify(null));
    }

    @Test
    void classify_noEmployeeNoRecipient_returnsUnknown() {
        PackageEntity pkg = new PackageEntity();

        assertEquals(AddressedTo.UNKNOWN, classifier.classify(pkg));
    }

    @Test
    void classify_blankRecipientName_returnsUnknown() {
        PackageEntity pkg = new PackageEntity();
        pkg.setRecipientName("   ");

        assertEquals(AddressedTo.UNKNOWN, classifier.classify(pkg));
    }

    @Test
    void classify_recipientDoesNotContainCompanyName_returnsUnknown() {
        PackageEntity pkg = new PackageEntity();
        pkg.setRecipientName("John Doe");

        assertEquals(AddressedTo.UNKNOWN, classifier.classify(pkg));
    }

    // ── EMPLOYEE takes precedence over company name ──────────────────────────

    @Test
    void classify_employeeAndSiouxRecipient_employeeTakesPrecedence() {
        PackageEntity pkg = new PackageEntity();
        UserEntity employee = new UserEntity();
        employee.setId(7);
        pkg.setEmployee(employee);
        pkg.setRecipientName("Sioux Technologies");

        // Employee link beats recipient-name match
        assertEquals(AddressedTo.EMPLOYEE, classifier.classify(pkg));
    }
}
