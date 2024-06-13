package org.keycloak.license.report.beans;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.jboss.logging.Logger;
import org.keycloak.license.dependencies.Dependency;
import org.keycloak.license.licenses.spdx.License;
import org.keycloak.license.licenses.spdx.SpdxLicenses;
import org.keycloak.license.repository.RepositoryInfo;
import org.keycloak.license.repository.TagResolver;

import java.util.LinkedList;
import java.util.List;

@RegisterForReflection
public class DependencyBean {

    private static final Logger LOGGER = Logger.getLogger(DependencyBean.class);

    private final Dependency dependency;
    private final RepositoryInfo repositoryInfo;
    private final DependencyLicenseBean licenseBean;
    private final SpdxLicenses spdxLicenses;

    public DependencyBean(Dependency dependency, RepositoryInfo repositoryInfo, DependencyLicenseBean licenseBean, SpdxLicenses spdxLicenses) {
        this.dependency = dependency;
        this.repositoryInfo = repositoryInfo;
        this.licenseBean = licenseBean;
        this.spdxLicenses = spdxLicenses;
    }

    public String getReference() {
        return dependency.getIdentifier();
    }

    public String getDescription() {
        return dependency.getDescription();
    }

    public String getGroup() {
        return dependency.getGroup();
    }

    public String getName() {
        return dependency.getName();
    }

    public String getVersion() {
        return dependency.getVersion();
    }

    public String getTagSourceUrl() {
        if (repositoryInfo == null) {
            return null;
        }

        String tagUrl = TagResolver.resolveTagUrl(dependency.getVersion(), repositoryInfo.getTagUrl());
        return tagUrl != null ? tagUrl : repositoryInfo.getSourceUrl();
    }

    public String getSourceUrl() {
        if (repositoryInfo == null) {
            return null;
        }
        return repositoryInfo.getSourceUrl();
    }

    public String getPackageUrl() {
        return dependency.getPurl();
    }

    public String getWebUrl() {
        return dependency.getWebsiteUrl();
    }

    public DependencyLicenseBean getLicense() {
        return licenseBean;
    }

    public boolean isApprovedByCncf() {
        return licenseBean.getLicense().isCncfApproved();
    }

    public List<License> getAllDeclaredLicenses() {
        List<License> licenses = new LinkedList<>();
        for (Dependency.DependencyLicenseInfo i : dependency.getLicenseInfo()) {
            License license = spdxLicenses.findLicense(i);
            if (license != null) {
                licenses.add(license);
            }
        }
        return licenses;
    }

}
