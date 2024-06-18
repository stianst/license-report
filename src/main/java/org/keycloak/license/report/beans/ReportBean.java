package org.keycloak.license.report.beans;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.jboss.logging.Logger;
import org.keycloak.license.dependencies.Dependency;
import org.keycloak.license.licenses.LicenseContentManager;
import org.keycloak.license.licenses.spdx.License;
import org.keycloak.license.licenses.spdx.SpdxLicenses;
import org.keycloak.license.repository.RepositoryInfo;
import org.keycloak.license.repository.RepositoryManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@RegisterForReflection
public class ReportBean {

    private static final Logger LOGGER = Logger.getLogger(ReportBean.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    private final RepositoryManager repositoryManager;
    private final LicenseContentManager licenseContentManager;
    private final SpdxLicenses spdxLicenses;
    private List<LicenseBean> licenses = new LinkedList<>();
    private final OffsetDateTime date;

    public ReportBean(RepositoryManager repositoryManager, LicenseContentManager licenseContentManager, SpdxLicenses spdxLicenses) {
        this.repositoryManager = repositoryManager;
        this.licenseContentManager = licenseContentManager;
        this.spdxLicenses = spdxLicenses;
        this.date = OffsetDateTime.now(ZoneOffset.UTC);
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

    public void add(Dependency dependency) throws URISyntaxException, IOException {
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

    public String getDate() {
        return date.toString();
    }
}
