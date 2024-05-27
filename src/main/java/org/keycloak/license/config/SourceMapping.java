package org.keycloak.license.config;

public class SourceMapping {

    private String dependency;
    private String sourceUrl;

    public SourceMapping() {
    }

    public SourceMapping(String dependency, String sourceUrl) {
        this.dependency = dependency;
        this.sourceUrl = sourceUrl;
    }

    public String getDependency() {
        return dependency;
    }

    public void setDependency(String dependency) {
        this.dependency = dependency;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
