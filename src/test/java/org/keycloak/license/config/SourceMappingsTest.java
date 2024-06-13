package org.keycloak.license.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.license.dependencies.Dependency;

import java.util.List;

public class SourceMappingsTest {

    @Test
    public void getSourceUrlMapping() {
        Dependency dependency = new Dependency("io.vertx", "vertx-auth-common", "NA");
        SourceMappings sourceMappings = new SourceMappings(List.of(new SourceMapping("io.vertx:vertx-auth-common", "https://github.com/eclipse-vertx/vertx-auth")));
        Assertions.assertEquals("https://github.com/eclipse-vertx/vertx-auth", sourceMappings.getDependencySourceUrl(dependency));
    }

    @Test
    public void getSourceUrlMappingNpmWithoutGroup() {
        Dependency dependency = new Dependency(null, "test", "NA");
        SourceMappings sourceMappings = new SourceMappings(List.of(new SourceMapping("test", "https://github.com/test")));
        Assertions.assertEquals("https://github.com/test", sourceMappings.getDependencySourceUrl(dependency));
    }

    @Test
    public void getSourceUrlMappingNoMapping() {
        Dependency dependency = new Dependency("io.foo", "bar", "NA");
        SourceMappings sourceMappings = new SourceMappings(List.of(new SourceMapping("io.vertx:vertx-auth-common", "https://github.com/eclipse-vertx/vertx-auth")));
        Assertions.assertNull(sourceMappings.getDependencySourceUrl(dependency));
    }

}
