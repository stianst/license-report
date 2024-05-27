package org.keycloak.license.report.beans;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.jboss.logging.Logger;
import org.keycloak.license.dependencies.Dependency;
import org.keycloak.license.licenses.LicenseContentManager;
import org.keycloak.license.licenses.spdx.License;
import org.keycloak.license.licenses.spdx.SpdxLicenses;
import org.keycloak.license.repository.RepositoryInfo;
import org.keycloak.license.repository.RepositoryManager;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@RegisterForReflection
public class ReportBean {

    private static final Logger LOGGER = Logger.getLogger(ReportBean.class);

    private final RepositoryManager repositoryManager;
    private final LicenseContentManager licenseContentManager;
    private final SpdxLicenses spdxLicenses;
    private List<LicenseBean> licenses = new LinkedList<>();

    public ReportBean(RepositoryManager repositoryManager, LicenseContentManager licenseContentManager, SpdxLicenses spdxLicenses) {
        this.repositoryManager = repositoryManager;
        this.licenseContentManager = licenseContentManager;
        this.spdxLicenses = spdxLicenses;
    }

    public List<LicenseBean> getLicenses() {
        return licenses.stream().sorted(Comparator.comparing(LicenseBean::getId)).toList();
    }

    public List<DependencyBean> getDependencies() {
        return licenses.stream().map(LicenseBean::getDependencies).flatMap(Collection::stream).sorted(Comparator.comparing(DependencyBean::getReference)).toList();
    }

    public SpdxLicenses getSpdxLicenses() {
        return spdxLicenses;
    }

    public void add(Dependency dependency) {
        RepositoryInfo repositoryInfo = repositoryManager.getRepositoryInfo(dependency);

        License optimalLicense = spdxLicenses.findOptimalLicense(dependency);
        if (optimalLicense == null) {
            return;
        }
        LicenseBean licenseBean = licenses.stream().filter(l -> l.getId().equals(optimalLicense.getLicenseId())).findFirst().orElseGet(() -> {
            LicenseBean l = new LicenseBean(optimalLicense);
            licenses.add(l);
            return l;
        });


        licenseBean.add(new DependencyBean(dependency, repositoryInfo, new DependencyLicenseBean(repositoryInfo, licenseContentManager, optimalLicense), spdxLicenses));
    }

}
