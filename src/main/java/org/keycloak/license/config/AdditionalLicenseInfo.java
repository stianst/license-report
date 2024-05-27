package org.keycloak.license.config;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.keycloak.license.licenses.spdx.License;

import java.util.List;
import java.util.Set;

@RegisterForReflection
public class AdditionalLicenseInfo {

    private Set<String> cncfApproved;

    private List<License> additionalLicenses;

    private List<LicenseMapping> mappings;

    public Set<String> getCncfApproved() {
        return cncfApproved;
    }

    private List<String> excludedDependencies;

    public void setCncfApproved(Set<String> cncfApproved) {
        this.cncfApproved = cncfApproved;
    }

    public List<License> getAdditionalLicenses() {
        return additionalLicenses;
    }

    public void setAdditionalLicenses(List<License> additionalLicenses) {
        this.additionalLicenses = additionalLicenses;
    }

    public List<LicenseMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<LicenseMapping> mappings) {
        this.mappings = mappings;
    }

}
