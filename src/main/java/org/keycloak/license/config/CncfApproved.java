package org.keycloak.license.config;

import java.util.List;

public class CncfApproved {

    private List<String> licenses;
    private List<Exception> exceptions;

    public List<String> getLicenses() {
        return licenses;
    }

    public void setLicenses(List<String> licenses) {
        this.licenses = licenses;
    }

    public List<Exception> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<Exception> exceptions) {
        this.exceptions = exceptions;
    }

    public static class Exception {

        private String dependency;
        private String license;

        public String getDependency() {
            return dependency;
        }

        public void setDependency(String dependency) {
            this.dependency = dependency;
        }

        public String getLicense() {
            return license;
        }

        public void setLicense(String license) {
            this.license = license;
        }
    }

}
