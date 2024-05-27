package org.keycloak.license.dependencies;

import java.util.LinkedList;
import java.util.List;

public class Dependency {

    private final String group;
    private final String name;
    private final String version;
    private String description;
    private String vcsUrl;
    private String websiteUrl;
    private final List<DependencyLicenseInfo> licenseInfo = new LinkedList<>();
    private String purl;

    public Dependency(String group, String name, String version) {
        this.group = group;
        this.name = name;
        this.version = version;
    }

    public String getIdentifier() {
        return (group != null ? group + ":" : "") + name + ":" + version;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVcsUrl() {
        return vcsUrl;
    }

    public void setVcsUrl(String vcsUrl) {
        this.vcsUrl = vcsUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public void addLicenseInfo(String id, String name, String url) {
        licenseInfo.add(new DependencyLicenseInfo(id, name, url));
    }

    public List<DependencyLicenseInfo> getLicenseInfo() {
        return licenseInfo;
    }

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }

    public static final class DependencyLicenseInfo {

        private final String id;
        private final String name;
        private final String url;

        public DependencyLicenseInfo(String id, String name, String url) {
            this.id = id;
            this.name = name;
            this.url = url;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }
}
