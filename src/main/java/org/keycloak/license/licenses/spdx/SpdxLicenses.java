package org.keycloak.license.licenses.spdx;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.license.config.Config;
import org.keycloak.license.config.LicenseMapping;
import org.keycloak.license.dependencies.Dependency;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SpdxLicenses {

    private static final String SPDX_URL = "https://raw.githubusercontent.com/spdx/license-list-data/main/json/licenses.json";

    @Inject
    Config config;

    private Licenses licenses;
    private List<LicenseMapping> licenseMappings;

    @PostConstruct
    public void init() {
        ObjectMapper om = new ObjectMapper();
        try {
            licenses = om.readValue(new URL(SPDX_URL), Licenses.class);
            licenses.getLicenses().addAll(config.getAdditionalLicenses());

            for (String cncfApprovedLicense : config.getCncfApproved().getLicenses()) {
                License l = findByLicenseId(cncfApprovedLicense);
                if (l != null) {
                    l.setCncfApproved(true);
                }
            }

            licenseMappings = config.getLicenseMappings();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public License findOptimalLicense(Dependency dependency) {
        List<License> licenses = new LinkedList<>();

        List<License> byDependency = findByDependency(dependency);
        if (byDependency != null) {
            licenses.addAll(byDependency);
        } else {
            licenses = new LinkedList<>();
            for (Dependency.DependencyLicenseInfo d : dependency.getLicenseInfo()) {
                License license = findLicense(d);
                if (license != null) {
                    licenses.add(license);
                }
            }
        }
        licenses.sort(Comparator.comparing(License::getLicenseId));

        Optional<License> match = licenses.stream().filter(License::isCncfApproved).findFirst();
        if (match.isPresent()) {
            return match.get();
        }

        match = licenses.stream().filter(License::isOsiApproved).findFirst();
        if (match.isPresent()) {
            return match.get();
        }

        match = licenses.stream().filter(License::isFsfLibre).findFirst();
        if (match.isPresent()) {
            return match.get();
        }

        return !licenses.isEmpty() ? licenses.get(0) : null;
    }

    public License findLicense(Dependency.DependencyLicenseInfo dependencyLicenseInfo) {
        License license = findByLicenseId(dependencyLicenseInfo.getId());
        if (license != null) {
            return license;
        }

        license = findByName(dependencyLicenseInfo.getName());
        if (license != null) {
            return license;
        }

        if (dependencyLicenseInfo.getUrl() != null) {
            license = findByUrl(dependencyLicenseInfo.getUrl());
            if (license != null) {
                return license;
            }
        }

        return null;
    }

    public License findByName(String name) {
        License license = licenses.getLicenses().stream()
                .filter(l -> l.getName().equals(name) || l.getLicenseId().equals(name)).findFirst().orElse(null);
        if (license != null) {
            return license;
        }

        LicenseMapping licenseMapping = licenseMappings.stream().filter(m -> m.getNameMappings() != null && m.getNameMappings().contains(name)).findFirst().orElse(null);
        if (licenseMapping != null) {
            return findByLicenseId(licenseMapping.getLicenseId());
        } else {
            return null;
        }
    }

    public License findByLicenseId(String licenseId) {
        return licenses.getLicenses().stream().filter(l -> l.getLicenseId().equals(licenseId)).findFirst().orElse(null);
    }

    public License findByUrl(String url) {
        License license = licenses.getLicenses().stream().filter(l ->
                l.getReference().equals(url) || (l.getSeeAlso() != null && l.getSeeAlso().contains(url))).findFirst().orElse(null);
        if (license != null) {
            return license;
        }

        LicenseMapping licenseMapping = licenseMappings.stream().filter(m -> m.getUrlMappings() != null && m.getUrlMappings().contains(url)).findFirst().orElse(null);
        if (licenseMapping != null) {
            return findByLicenseId(licenseMapping.getLicenseId());
        } else {
            return licenses.getLicenses().stream().filter(l ->
                    l.getReference().equals(url) || (l.getSeeAlso() != null && l.getSeeAlso().contains(url))).findFirst().orElse(null);
        }
    }

    public List<License> findByDependency(Dependency dependency) {
        String identifier = (dependency.getGroup() != null ? dependency.getGroup() + ":" : "") + dependency.getName();
        List<LicenseMapping> licenseMapping = licenseMappings.stream().filter(m -> m.getDependencyMappings() != null && m.getDependencyMappings().contains(identifier)).toList();
        if (licenseMapping.isEmpty()) {
            return null;
        } else {
            return licenseMapping.stream().map(m -> findByLicenseId(m.getLicenseId())).toList();
        }
    }

}
