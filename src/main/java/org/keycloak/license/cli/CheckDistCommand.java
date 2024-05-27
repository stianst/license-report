package org.keycloak.license.cli;

import jakarta.inject.Inject;
import org.keycloak.license.dependencies.Dependency;
import org.keycloak.license.cyclonedx.CycloneDxParser;
import org.keycloak.license.dependencies.DependencyManager;
import picocli.CommandLine;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@CommandLine.Command(name = "check-dist")
public class CheckDistCommand implements Runnable {

    @CommandLine.Option(names = {"-s", "--sbom"}, description = "CycloneDX SBOM for server distribution", required = true)
    File sbom;

    @CommandLine.Option(names = {"-k", "--keycloak"}, description = "Keycloak distribution directory", required = true)
    File keycloakHome;

    List<String> jars;

    @Inject
    DependencyManager dependencyManager;

    @Override
    public void run() {
        dependencyManager.parseSboms(List.of(sbom));
        File libDir = new File(keycloakHome, "lib" + File.separator + "lib");
        jars = scan(libDir);

        List<Dependency> depencies = dependencyManager.getDependencies();

        verify(depencies);
    }

    private void verify(List<Dependency> dependencies) {
        List<String> dependencyList = dependencies.stream().map(this::toJar).toList();
        List<String> notFoundInDist = dependencyList.stream().filter(s -> !jars.contains(s)).toList();

        System.out.println("Missing in dist");
        System.out.println(notFoundInDist.stream().collect(Collectors.joining(" ")));

        List<String> notFoundInReport = jars.stream().filter(s -> !dependencyList.contains(s)).toList();

        System.out.println("");
        System.out.println("Missing in report");
        System.out.println(notFoundInReport.stream().collect(Collectors.joining(" ")));
    }

    private List<String> scan(File directory) {
        List<String> jars = new LinkedList<>(Arrays.stream(directory.listFiles(f -> f.getName().endsWith(".jar"))).map(File::getName).toList());
        for (File d : directory.listFiles(File::isDirectory)) {
            jars.addAll(scan(d));
        }
        return jars;
    }

    private String toJar(Dependency dependency) {
        return dependency.getIdentifier().replaceFirst(":", ".").replaceAll(":", "-") + ".jar";
    }

}
