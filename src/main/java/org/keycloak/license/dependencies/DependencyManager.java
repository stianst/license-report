package org.keycloak.license.dependencies;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.license.config.Config;
import org.keycloak.license.cyclonedx.CycloneDxParser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DependencyManager {

    @Inject
    Config config;

    private final List<Dependency> dependencies = new LinkedList<>();

    public void parseSboms(List<File> sboms) {
        try {
            for (File f : sboms) {
                List<Dependency> added = CycloneDxParser.parse(f);
                for (Dependency d : added) {
                    if (!isExcluded(d) && dependencies.stream().noneMatch(a -> a.getIdentifier().equals(d.getIdentifier()))) {
                        dependencies.add(d);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isExcluded(Dependency dependency) {
        List<String> excludedDependencies = config.getExcludedDependencies();
        Optional<String> m = excludedDependencies.stream().filter(dependency.getIdentifier()::matches).findFirst();
        return m.isPresent();
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

}
