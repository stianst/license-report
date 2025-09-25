package org.keycloak.license.report.beans;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.annotation.PostConstruct;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jboss.logging.Logger;
import org.keycloak.license.licenses.LicenseContentManager;
import org.keycloak.license.licenses.spdx.License;
import org.keycloak.license.repository.RepositoryInfo;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import java.io.IOException;
import java.net.URISyntaxException;

@RegisterForReflection
public class DependencyLicenseBean {

    private static final Logger LOGGER = Logger.getLogger(DependencyLicenseBean.class);

    private PolicyFactory sanitizerPolicy = Sanitizers.BLOCKS.and(Sanitizers.FORMATTING);

    private final RepositoryInfo repositoryInfo;
    private final LicenseContentManager licenseContentManager;
    private final License license;
    private String licenseContents;

    public DependencyLicenseBean(RepositoryInfo repositoryInfo, LicenseContentManager licenseContentManager, License license) throws URISyntaxException, IOException {
        this.repositoryInfo = repositoryInfo;
        this.licenseContentManager = licenseContentManager;
        this.license = license;
        if (repositoryInfo != null && repositoryInfo.getLicenseUrl() != null) {
            this.licenseContents = licenseContentManager.getLicenseContent(repositoryInfo.getLicenseUrl());
        }
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
        if (repositoryInfo.getLicenseUrl().endsWith(".md")) {
            return false;
        }
        if (repositoryInfo.getLicenseUrl().endsWith(".html")) {
            return false;
        }
        return !licenseContents.startsWith("<");
    }

    public String getLicenseContent() {
        if (isLicenseContentPlainText()) {
            return licenseContents;
        }

        if (repositoryInfo.getLicenseUrl() != null && repositoryInfo.getLicenseUrl().endsWith(".md")) {
            Parser parser = Parser.builder().build();
            Node document = parser.parse(licenseContents);
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            return renderer.render(document);
        } else {
            return sanitizerPolicy.sanitize(licenseContents);
        }
    }

}
