package org.keycloak.license.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TagResolverTest {

    @Test
    public void resolveTagUrlWithVersion() {
        Assertions.assertEquals("https://github.com/org/pro/tree/999.888.777", TagResolver.resolveTagUrl("999.888.777", "https://github.com/org/pro/tree/${version}"));
    }

    @Test
    public void resolveTagUrlWithVersionUnderscore() {
        Assertions.assertEquals("https://github.com/org/pro/tree/999_888_777", TagResolver.resolveTagUrl("999.888.777", "https://github.com/org/pro/tree/${versionUnderscore}"));
    }

}
