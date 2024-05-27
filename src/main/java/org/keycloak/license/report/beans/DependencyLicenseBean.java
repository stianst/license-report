package org.keycloak.license.report.beans;

import io.quarkus.qute.RawString;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.keycloak.license.licenses.LicenseContentManager;
import org.keycloak.license.repository.RepositoryInfo;
import org.keycloak.license.licenses.spdx.License;

import java.io.IOException;
import java.net.URISyntaxException;

@RegisterForReflection
public class DependencyLicenseBean {

    private final RepositoryInfo repositoryInfo;
    private final LicenseContentManager licenseContentManager;
    private final License license;

    public DependencyLicenseBean(RepositoryInfo repositoryInfo, LicenseContentManager licenseContentManager, License license) {
        this.repositoryInfo = repositoryInfo;
        this.licenseContentManager = licenseContentManager;
        this.license = license;
    }

    public String getName() {
        return license.getName();
    }

    public License getLicense() {
        return license;
    }

    public String getLicenseContentUrl() {
        return repositoryInfo != null ? repositoryInfo.getLicenseUrl() : null;
    }

    public boolean isLicenseContentPlainText() {
        return !(repositoryInfo.getLicenseUrl().endsWith("md") || repositoryInfo.getLicenseUrl().endsWith("html"));
    }

    public RawString getLicenseContent() throws URISyntaxException, IOException {
        if (repositoryInfo == null) {
            return null;
        }

        String licenseText = licenseContentManager.getLicenseContent(repositoryInfo.getLicenseUrl());
        if (repositoryInfo.getLicenseUrl() != null && repositoryInfo.getLicenseUrl().endsWith(".md")) {
            Parser parser = Parser.builder().build();
            Node document = parser.parse(licenseText);
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            licenseText = renderer.render(document);
        }
        return new RawString(licenseText);
    }

}
