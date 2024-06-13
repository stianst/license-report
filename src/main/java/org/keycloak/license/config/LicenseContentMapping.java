package org.keycloak.license.config;

public class LicenseContentMapping {

    private String licenseUrl;

    private String mapsTo;

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    public String getMapsTo() {
        return mapsTo;
    }

    public void setMapsTo(String mapsTo) {
        this.mapsTo = mapsTo;
    }
}
