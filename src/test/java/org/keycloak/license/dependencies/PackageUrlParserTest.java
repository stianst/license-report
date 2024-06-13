package org.keycloak.license.dependencies;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PackageUrlParserTest {

    @Test
    public void testMaven() {
        String url = PackageUrlParser.toUrl("pkg:maven/jakarta.interceptor/jakarta.interceptor-api@2.1.0?type=jar");
        Assertions.assertEquals("https://central.sonatype.com/artifact/jakarta.interceptor/jakarta.interceptor-api", url);
    }

    @Test
    public void testNpmNoGroup() {
        String url = PackageUrlParser.toUrl("pkg:npm/abstract-logging@2.0.1");
        Assertions.assertEquals("https://www.npmjs.com/package/abstract-logging", url);
    }

    @Test
    public void testNpmWithGroup() {
        String url = PackageUrlParser.toUrl("pkg:npm/%40typescript-eslint/parser@7.12.0");
        Assertions.assertEquals("https://www.npmjs.com/package/%40typescript-eslint/parser", url);
    }

}
