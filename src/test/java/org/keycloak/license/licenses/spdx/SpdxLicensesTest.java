package org.keycloak.license.licenses.spdx;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.license.config.CncfApproved;
import org.keycloak.license.config.Config;
import org.keycloak.license.config.LicenseMapping;
import org.keycloak.license.dependencies.Dependency;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SpdxLicensesTest {

    private SpdxLicenses spdxLicenses = new SpdxLicenses();

    @BeforeEach
    public void init() {
        spdxLicenses = new SpdxLicenses();

        CncfApproved cncfApproved = new CncfApproved();
        cncfApproved.setLicenses(Collections.emptyList());

        LicenseMapping licenseMapping = new LicenseMapping();
        licenseMapping.setLicenseId("Apache-2.0");
        licenseMapping.setNameMappings(Set.of("The Apache Software License, Version 2.0"));
        licenseMapping.setUrlMappings(Set.of("https://custom/asl2.txt"));
        licenseMapping.setDependencyMappings(Set.of("font-awesome", "@swc:core-darwin-arm64"));

        LicenseMapping licenseMapping2 = new LicenseMapping();
        licenseMapping2.setLicenseId("MIT");
        licenseMapping2.setDependencyMappings(Set.of("font-awesome"));

        List<LicenseMapping> licenseMappings = List.of(licenseMapping, licenseMapping2);

        spdxLicenses.config = new Config(null, null, cncfApproved, Collections.emptyList(), licenseMappings, null);
        spdxLicenses.init();
    }

    @Test
    public void testFindById() {
        Assertions.assertEquals("Apache-2.0", spdxLicenses.findByLicenseId("Apache-2.0").getLicenseId());
    }

    @Test
    public void testFindByName() {
        Assertions.assertEquals("Apache-2.0", spdxLicenses.findByName("Apache License 2.0").getLicenseId());
        Assertions.assertEquals("Apache-2.0", spdxLicenses.findByName("The Apache Software License, Version 2.0").getLicenseId());
        Assertions.assertEquals("EPL-2.0", spdxLicenses.findByName("Eclipse Public License 2.0").getLicenseId());
    }

    @Test
    public void testFindByUrl() {
        Assertions.assertEquals("Apache-2.0", spdxLicenses.findByUrl("https://spdx.org/licenses/Apache-2.0.html").getLicenseId());
        Assertions.assertEquals("Apache-2.0", spdxLicenses.findByUrl("https://www.apache.org/licenses/LICENSE-2.0").getLicenseId());
        Assertions.assertEquals("Apache-2.0", spdxLicenses.findByUrl("https://custom/asl2.txt").getLicenseId());
    }

    @Test
    public void testFindByDependency() {
        List<License> byDependency = spdxLicenses.findByDependency(new Dependency(null, "font-awesome", "4.7.0"));
        Assertions.assertEquals(2, byDependency.size());

        List<License> byDependency1 = spdxLicenses.findByDependency(new Dependency("@swc", "core-darwin-arm64", "1.5.7"));
        Assertions.assertEquals(1, byDependency1.size());
    }
}
