package org.keycloak.license.report.beans;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.jboss.logging.Logger;
import org.keycloak.license.config.CncfApproved;
import org.keycloak.license.dependencies.Dependency;
import org.keycloak.license.dependencies.PackageUrlParser;
import org.keycloak.license.licenses.spdx.License;
import org.keycloak.license.licenses.spdx.SpdxLicenses;
import org.keycloak.license.repository.RepositoryInfo;
import org.keycloak.license.repository.TagResolver;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@RegisterForReflection
public class DependencyBean {

    private static final Logger LOGGER = Logger.getLogger(DependencyBean.class);

    private final Dependency dependency;
    private final RepositoryInfo repositoryInfo;
    private final DependencyLicenseBean licenseBean;
    private final SpdxLicenses spdxLicenses;
    private final CncfApproved cncfApproved;

    public DependencyBean(Dependency dependency, RepositoryInfo repositoryInfo, DependencyLicenseBean licenseBean, SpdxLicenses spdxLicenses, CncfApproved cncfApproved) {
        this.dependency = dependency;
        this.repositoryInfo = repositoryInfo;
        this.licenseBean = licenseBean;
        this.spdxLicenses = spdxLicenses;
        this.cncfApproved = cncfApproved;
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

    public String getPackage() {
        return dependency.getPurl();
    }

    public String getPackageUrl() {
        return PackageUrlParser.toUrl(dependency.getPurl());
    }

    public String getWebUrl() {
        return dependency.getWebsiteUrl();
    }

    public DependencyLicenseBean getLicense() {
        return licenseBean;
    }

    public boolean isApprovedByCncf() {
        if (licenseBean.getLicense().isCncfApproved()) {
            return true;
        }

        String id = (dependency.getGroup() != null && !dependency.getGroup().isEmpty() ? dependency.getGroup() + ":" : "") + dependency.getName();

        Optional<CncfApproved.Exception> exception = cncfApproved.getExceptions().stream().filter(e -> e.getDependency().equals(id)).findFirst();
        if (exception.isPresent()) {
            return licenseBean.getLicense().getLicenseId().equals(exception.get().getLicense());
        }

        return false;
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
