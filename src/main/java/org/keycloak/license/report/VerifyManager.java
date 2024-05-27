package org.keycloak.license.report;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.keycloak.license.dependencies.Dependency;
import org.keycloak.license.dependencies.DependencyManager;
import org.keycloak.license.licenses.spdx.License;
import org.keycloak.license.licenses.spdx.SpdxLicenses;
import org.keycloak.license.repository.RepositoryInfo;
import org.keycloak.license.repository.RepositoryManager;

import java.util.LinkedList;
import java.util.List;

@ApplicationScoped
public class VerifyManager {

    private static final Logger LOGGER = Logger.getLogger(VerifyManager.class);

    @Inject
    DependencyManager dependencyManager;

    @Inject
    RepositoryManager repositoryManager;

    @Inject
    SpdxLicenses spdxLicenses;

    public boolean verify() {
        boolean valid = true;
        valid &= verifyRepositoryInfo();
        valid &= verifyTagUrl();
        valid &= verifyLicenseContent();
        valid &= verifyLicenses();
        return valid;
    }

    private boolean verifyRepositoryInfo() {
        int invalid = 0;
        for (Dependency d : dependencyManager.getDependencies()) {
            RepositoryInfo repositoryInfo = repositoryManager.getRepositoryInfo(d);
            if (repositoryInfo == null || repositoryInfo.getSourceUrl() == null) {
                invalid++;
                LOGGER.warnv("Missing repository info; dependency={0}, webUrl={1}, vcsUrl={2}", d.getIdentifier(), d.getWebsiteUrl(), d.getVcsUrl());
            }
        }
        if (invalid > 0) {
            LOGGER.errorv("Missing repository info for {0} dependencies", invalid);
            return false;
        } else {
            return true;
        }
    }

    private boolean verifyTagUrl() {
        int invalid = 0;
        for (Dependency d : dependencyManager.getDependencies()) {
            RepositoryInfo repositoryInfo = repositoryManager.getRepositoryInfo(d);
            if (repositoryInfo != null) {
                if (repositoryInfo.getTagUrl() == null) {
                    invalid++;
                    LOGGER.warnv("Missing tag url; dependency={0}, sourceUrl={1}", d.getIdentifier(), repositoryInfo.getSourceUrl());
                } else if (repositoryInfo.getTagUrl().equals("UNRESOLVABLE")) {
                    LOGGER.infov("Tag not resolvable; dependency={0}, sourceUrl={1}", d.getIdentifier(), repositoryInfo.getSourceUrl());
                }
            }
        }
        if (invalid > 0) {
            LOGGER.errorv("Missing tag url for {0} dependencies", invalid);
            return false;
        }
        return true;
    }

    private boolean verifyLicenseContent() {
        int invalid = 0;
        for (Dependency d : dependencyManager.getDependencies()) {
            RepositoryInfo repositoryInfo = repositoryManager.getRepositoryInfo(d);
            if (repositoryInfo != null) {
                if (repositoryInfo.getLicenseUrl() == null) {
                    invalid++;
                    LOGGER.warnv("Missing license url; dependency={0}, sourceUrl={1}", d.getIdentifier(), repositoryInfo.getSourceUrl());
                }
            }
        }
        if (invalid > 0) {
            LOGGER.errorv("Missing license url for {0} dependencies", invalid);
            return false;
        }
        return true;
    }

    private boolean verifyLicenses() {
        int invalid = 0;
        for (Dependency d : dependencyManager.getDependencies()) {
            boolean v = true;
            for (Dependency.DependencyLicenseInfo i : d.getLicenseInfo()) {
                License license = spdxLicenses.findLicense(i);
                if (license == null) {
                    RepositoryInfo repositoryInfo = repositoryManager.getRepositoryInfo(d);
                    String sourceUrl = repositoryInfo != null ? repositoryInfo.getSourceUrl() : d.getVcsUrl();

                    LOGGER.warnv("Unmapped license; dependency={0}, licenseId={1}, licenseName={2}, licenseUrl={3}, sourceUrl={4}", d.getIdentifier(), i.getId(), i.getName(), i.getUrl(), sourceUrl);
                    invalid++;
                    v = false;
                }
            }

            if (v) {
                License optimalLicense = spdxLicenses.findOptimalLicense(d);
                if (optimalLicense == null) {
                    invalid++;
                    LOGGER.warnv("Optimal license not found; dependency={0}", d.getIdentifier());
                }
            }
        }
        if (invalid > 0) {
            LOGGER.errorv("Missing license url for {0} dependencies", invalid);
            return false;
        }
        return true;
    }

}
