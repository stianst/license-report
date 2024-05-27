package org.keycloak.license.cyclonedx;

import org.cyclonedx.exception.ParseException;
import org.cyclonedx.model.Bom;
import org.cyclonedx.model.Component;
import org.cyclonedx.model.ExternalReference;
import org.cyclonedx.model.License;
import org.cyclonedx.model.LicenseChoice;
import org.cyclonedx.parsers.BomParserFactory;
import org.cyclonedx.parsers.Parser;
import org.keycloak.license.dependencies.Dependency;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class CycloneDxParser {

    private CycloneDxParser() {
    }

    public static List<Dependency> parse(File file) throws IOException, ParseException {
        List<Dependency> dependencies = new LinkedList<>();

        Parser parser = BomParserFactory.createParser(file);
        Bom bom = parser.parse(file);
        for (Component component : bom.getComponents()) {
            String g = component.getGroup();
            String a = component.getName();
            String v = component.getVersion();
            String purl = component.getPurl();

            Dependency dependency = new Dependency(g, a, v);
            dependency.setPurl(purl);
            dependency.setDescription(component.getDescription());

            List<ExternalReference> externalReferences = component.getExternalReferences();
            if (externalReferences != null) {
                String websiteUrl = externalReferences.stream().filter(r -> r.getType().equals(ExternalReference.Type.WEBSITE)).map(ExternalReference::getUrl).findFirst().orElse(null);
                dependency.setWebsiteUrl(websiteUrl);

                String vcsUrl = externalReferences.stream().filter(r -> r.getType().equals(ExternalReference.Type.VCS)).map(ExternalReference::getUrl).findFirst().orElse(null);
                dependency.setVcsUrl(vcsUrl);
            }

            LicenseChoice licenses = component.getLicenses();
            if (licenses != null && licenses.getLicenses() != null) {
                for (License license : licenses.getLicenses()) {
                    dependency.addLicenseInfo(license.getId(), license.getName(), license.getUrl());
                }
            }

            dependencies.add(dependency);
        }

        return dependencies;
    }

}
