package org.keycloak.license.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class LicenseResolverTest {

    @Test
    public void testMatches() {
        assertEquals("LICENSE.txt", LicenseResolver.resolveLicense(createList("LICENSE.txt")));
        assertEquals("license.txt", LicenseResolver.resolveLicense(createList("license.txt")));
        assertEquals("LICENSE", LicenseResolver.resolveLicense(createList("LICENSE")));
        assertEquals("license", LicenseResolver.resolveLicense(createList("license")));

        assertEquals("LICENSE.md", LicenseResolver.resolveLicense(createList("LICENSE.md")));
        assertEquals("license.md", LicenseResolver.resolveLicense(createList("license.md")));
        assertEquals("LICENSE.html", LicenseResolver.resolveLicense(createList("LICENSE.html")));
        assertEquals("license.html", LicenseResolver.resolveLicense(createList("license.html")));
    }

    @Test
    public void testPriority() {
        List<String> files = List.of("https://something/files/filea", "https://something/files/LICENSE.md", "https://something/files/license.txt");
        assertEquals("license.txt", LicenseResolver.resolveLicense(files));
    }

    private List<String> createList(String filename) {
        return List.of("https://something/files/filea", "https://something/files/licenses-are-not-fun", "https://something/files/" + filename);
    }

    private void assertEquals(String expectedFilename, String actualUrl) {
        Assertions.assertEquals("https://something/files/" + expectedFilename, actualUrl);
    }

}
