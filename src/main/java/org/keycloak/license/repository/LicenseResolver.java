package org.keycloak.license.repository;

import java.util.List;
import java.util.Set;

public class LicenseResolver {

    private LicenseResolver() {
    }

    private static final List<Set<String>> expectedFiles = List.of(
            Set.of("license", "license.txt"),
            Set.of("license.md", "license.html", "license.htm", "license.markdown"),
            Set.of("copying")
    );

    public static String resolveLicense(List<String> files) {
        for (Set<String> expected : expectedFiles) {
            for (String f : files) {
                String filename = f.substring(f.lastIndexOf('/') + 1).toLowerCase();
                if (expected.contains(filename)) {
                    return f;
                }
            }
        }
        return null;
    }
    
}
