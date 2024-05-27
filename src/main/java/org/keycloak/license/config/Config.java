package org.keycloak.license.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.license.licenses.spdx.License;
import org.keycloak.license.repository.RepositoryInfo;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Config {

    private final File globalCacheDir;
    private final File profileCacheDir;
    private final SourceMappings sourceMappings;
    private final List<String> excludedDependencies;
    private final CncfApproved cncfApproved;
    private final List<License> additionalLicenses;
    private final List<LicenseMapping> licenseMappings;
    private final List<RepositoryInfo> additionalRepositoryInfos;
    private final String profile;

    public Config(SourceMappings sourceMappings, List<String> excludedDependencies, CncfApproved cncfApproved, List<License> additionalLicenses, List<LicenseMapping> licenseMappings, List<RepositoryInfo> additionalRepositoryInfos) {
        this.profile = null;
        this.globalCacheDir = null;
        this.profileCacheDir = null;
        this.sourceMappings = sourceMappings;
        this.excludedDependencies = excludedDependencies;
        this.cncfApproved = cncfApproved;
        this.additionalLicenses = additionalLicenses;
        this.licenseMappings = licenseMappings;
        this.additionalRepositoryInfos = additionalRepositoryInfos;
    }

    public Config(File configDir, String profile) throws IOException {
        this.profile = profile;
        this.globalCacheDir =  new File("cache");
        this.profileCacheDir = new File(globalCacheDir, profile);

        configDir = new File(configDir, profile);

        ObjectMapper objectMapper = new ObjectMapper();

        File sourceMappingsFile = new File(configDir, "source-mappings.json");
        if (sourceMappingsFile.isFile()) {
            sourceMappings = new SourceMappings(List.of(objectMapper.readValue(sourceMappingsFile, SourceMapping[].class)));
        } else {
            sourceMappings = new SourceMappings(new LinkedList<>());
        }

        File excludedDependenciesFile = new File(configDir, "excluded-dependencies.json");
        if (excludedDependenciesFile.isFile()) {
            excludedDependencies = List.of(objectMapper.readValue(excludedDependenciesFile, String[].class));
        } else {
            excludedDependencies = new LinkedList<>();
        }

        File cncfApprovedFile = new File(configDir, "cncf-approved.json");
        if (cncfApprovedFile.isFile()) {
            cncfApproved = objectMapper.readValue(cncfApprovedFile, CncfApproved.class);
        } else {
            cncfApproved = new CncfApproved();
            cncfApproved.setLicenses(new LinkedList<>());
        }

        File additionalLicensesFile = new File(configDir, "additional-licenses.json");
        if (additionalLicensesFile.isFile()) {
            additionalLicenses = List.of(objectMapper.readValue(additionalLicensesFile, License[].class));
        } else {
            additionalLicenses = new LinkedList<>();
        }

        File licenseMappingsFile = new File(configDir, "license-mappings.json");
        if (licenseMappingsFile.isFile()) {
            licenseMappings = List.of(objectMapper.readValue(licenseMappingsFile, LicenseMapping[].class));
        } else {
            licenseMappings = new LinkedList<>();
        }

        File additionalRepositoryInfosFile = new File(configDir, "additional-repository-info.json");
        if (additionalRepositoryInfosFile.isFile()) {
            additionalRepositoryInfos = List.of(objectMapper.readValue(additionalRepositoryInfosFile, RepositoryInfo[].class));
        } else {
            additionalRepositoryInfos = new LinkedList<>();
        }
    }

    public String getProfile() {
        return profile;
    }

    public File getGlobalCacheDir() {
        if (!globalCacheDir.isDirectory()) {
            globalCacheDir.mkdirs();
        }
        return globalCacheDir;
    }

    public File getProfileCacheDir() {
        if (!profileCacheDir.isDirectory()) {
            profileCacheDir.mkdirs();
        }
        return profileCacheDir;
    }

    public SourceMappings getSourceMappings() {
        return sourceMappings;
    }

    public List<String> getExcludedDependencies() {
        return excludedDependencies;
    }

    public CncfApproved getCncfApproved() {
        return cncfApproved;
    }

    public List<License> getAdditionalLicenses() {
        return additionalLicenses;
    }

    public List<LicenseMapping> getLicenseMappings() {
        return licenseMappings;
    }

    public List<RepositoryInfo> getAdditionalRepositoryInfos() {
        return additionalRepositoryInfos;
    }
}
