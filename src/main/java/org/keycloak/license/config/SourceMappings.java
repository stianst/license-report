package org.keycloak.license.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.license.dependencies.Dependency;

import java.io.IOException;
import java.util.List;

public class SourceMappings {

    private final List<SourceMapping> sourceMappings;

    public SourceMappings(List<SourceMapping> sourceMappings) {
        this.sourceMappings = sourceMappings;
    }

    public String getDependencySourceUrl(Dependency dependency) {
        String ga = dependency.getGroup() + ":" + dependency.getName();
        return sourceMappings.stream().filter(s -> s.getDependency().equals(ga)).map(SourceMapping::getSourceUrl).findFirst().orElse(null);
    }

}
