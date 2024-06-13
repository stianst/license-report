package org.keycloak.license.repository;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.keycloak.license.config.Config;
import org.keycloak.license.dependencies.Dependency;
import org.keycloak.license.dependencies.DependencyManager;
import org.keycloak.license.http.InvalidResponseException;
import org.keycloak.license.http.SimpleHttp;
import org.keycloak.license.repository.bitbucket.BitBucketRepositoryUrl;
import org.keycloak.license.repository.github.GitHubRepository;
import org.keycloak.license.repository.github.GitHubRepositoryUrl;
import org.keycloak.license.repository.gitlab.GitlabRepositoryUrl;
import org.keycloak.license.repository.sourceforge.SourceForgeRepositoryUrl;
import org.keycloak.license.repository.unknown.UnknownRepositoryUrl;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RepositoryManager {

    private static final Logger LOGGER = Logger.getLogger(RepositoryManager.class);

    @Inject
    Config config;

    @Inject
    SimpleHttp simpleHttp;

    private List<RepositoryInfo> repositoryInfos;
    private File repositoryInfoFile;

    @Inject
    DependencyManager dependencyManager;

    @PostConstruct
    public void init() throws IOException {
        File profileCacheDir = config.getProfileCacheDir();
        repositoryInfoFile = new File(profileCacheDir, "repository-info.json");
        if (repositoryInfoFile.isFile()) {
            repositoryInfos = new LinkedList<>(List.of(new ObjectMapper().readValue(repositoryInfoFile, RepositoryInfo[].class)));
        } else {
            repositoryInfos = new LinkedList<>();
        }

        List<RepositoryInfo> additionalRepositoryInfo = config.getAdditionalRepositoryInfos();
        for (RepositoryInfo addedInfo : additionalRepositoryInfo) {
            Optional<RepositoryInfo> existing = repositoryInfos.stream().filter(i -> i.getSourceUrl().equals(addedInfo.getSourceUrl())).findFirst();
            if (existing.isPresent()) {
                RepositoryInfo existingInfo = existing.get();
                if (addedInfo.getLicenseUrl() != null) {
                    existingInfo.setLicenseUrl(addedInfo.getLicenseUrl());
                }
                if (addedInfo.getTagUrl() != null) {
                    existingInfo.setTagUrl(addedInfo.getTagUrl());
                }
                if (addedInfo.getType() != null) {
                    existingInfo.setType(addedInfo.getType());
                }
            } else {
                repositoryInfos.add(addedInfo);
            }
        }
    }

    public void resolveRepositoryInfo() {
        for (Dependency d : dependencyManager.getDependencies()) {
            RepositoryInfo repositoryInfo = getRepositoryInfo(d);
            if (repositoryInfo != null) {
                try {
                    resolveTagUrl(d);
                    resolveLicenseUrl(d);
                } catch (Exception e) {
                }
            }
        }
        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public RepositoryInfo getRepositoryInfo(Dependency dependency) {
        RepositoryUrl repositoryUrl = getRepositoryUrl(dependency);
        if (repositoryUrl == null) {
            return null;
        }

        RepositoryInfo repositoryInfo = repositoryInfos.stream().filter(r -> r.getSourceUrl().equals(repositoryUrl.getSourceUrl())).findFirst().orElse(null);
        if (repositoryInfo == null) {
            repositoryInfo = new RepositoryInfo();
            repositoryInfo.setType(repositoryUrl.getRepositoryType());
            repositoryInfo.setSourceUrl(repositoryUrl.getSourceUrl());

            repositoryInfos.add(repositoryInfo);
        }
        return repositoryInfo;
    }

    private void resolveTagUrl(Dependency dependency) throws IOException {
        RepositoryInfo repositoryInfo = getRepositoryInfo(dependency);
        if (repositoryInfo == null) {
            return;
        }

        if (repositoryInfo.getTagUrl() == null) {
            RepositoryUrl repositoryUrl = getRepositoryUrl(dependency);
            Repository repository = getRepository(repositoryUrl.getRepositoryType());
            if (repository != null) {
                try {
                    List<String> tags = repository.listTags(repositoryUrl);
                    String tagPattern = TagResolver.resolveTagPattern(tags, dependency.getVersion());
                    if (tagPattern.equals(TagResolver.UNRESOLVABLE)) {
                        LOGGER.warnv("Tag pattern not detected; dependency={0}, tags={1}", dependency.getIdentifier(), String.join(",", tags));
                        repositoryInfo.setTagUrl(TagResolver.UNRESOLVABLE);
                    } else {
                        repositoryInfo.setTagUrl(repositoryUrl.getTagWebUrl(tagPattern));
                    }
                } catch (InvalidResponseException e) {
                    LOGGER.warnv("Failed to list tags; dependency={0}, error={1}", dependency.getIdentifier(), e.getMessage());
                    if (e.getStatusCode() == 404) {
                        repositoryInfo.setTagUrl(TagResolver.UNRESOLVABLE);
                    }
                } catch (IOException e) {
                    LOGGER.warnv("Failed to list tags; dependency={0}, error={1}", dependency.getIdentifier(), e.getMessage());
                }
            }
        }
    }

    private void resolveLicenseUrl(Dependency dependency) throws IOException {
        RepositoryInfo repositoryInfo = getRepositoryInfo(dependency);
        if (repositoryInfo == null) {
            return;
        }

        if (repositoryInfo.getLicenseUrl() == null) {
            RepositoryUrl repositoryUrl = getRepositoryUrl(dependency);
            Repository repository = getRepository(repositoryUrl.getRepositoryType());
            if (repository != null) {
                try {
                    List<String> files = repository.listFiles(getRepositoryUrl(dependency));
                    String license = LicenseResolver.resolveLicense(files);
                    if (license != null) {
                        repositoryInfo.setLicenseUrl(license);
                    } else {
                        LOGGER.warnv("License not found; dependency={0}, files={1}", dependency.getIdentifier(), String.join(",", files));
                        if (dependency.getLicenseInfo().size() == 1 && dependency.getLicenseInfo().get(0).getUrl() != null) {
                            repositoryInfo.setLicenseUrl(dependency.getLicenseInfo().get(0).getUrl());
                            LOGGER.infov("Using license URL for dependency metadata; dependency={0}, licenseUrl={1}", dependency.getIdentifier(), dependency.getLicenseInfo().get(0).getUrl());
                        }
                    }
                } catch (IOException e) {
                    LOGGER.warnv("Failed to list files; dependency={0}, error={1}", dependency.getIdentifier(), e.getMessage());
                }
            }
        }
    }

    public void save() throws IOException {
        repositoryInfos.sort(Comparator.comparing(RepositoryInfo::getSourceUrl));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(repositoryInfoFile, repositoryInfos);
    }

    public RepositoryUrl getRepositoryUrl(Dependency dependency) {
        String sourceUrl = getSourceUrl(dependency);
        if (sourceUrl == null) {
            return null;
        }

        RepositoryUrl url = GitHubRepositoryUrl.parse(sourceUrl);
        if (url != null) {
            return url;
        }

        url = BitBucketRepositoryUrl.parse(sourceUrl);
        if (url != null) {
            return url;
        }

        url = SourceForgeRepositoryUrl.parse(sourceUrl);
        if (url != null) {
            return url;
        }

        url = GitlabRepositoryUrl.parse(sourceUrl);
        if (url != null) {
            return url;
        }

        return new UnknownRepositoryUrl(sourceUrl);
    }

    private String getSourceUrl(Dependency dependency) {
        String sourceUrl = config.getSourceMappings().getDependencySourceUrl(dependency);
        if (sourceUrl == null) {
            sourceUrl = dependency.getVcsUrl();
        }
        if (sourceUrl == null) {
            sourceUrl = dependency.getWebsiteUrl();
        }
        if (sourceUrl == null) {
            return null;
        }

        // Rewrite Apache URLs that redirect to GitHub
        if (sourceUrl.startsWith("https") && sourceUrl.contains("apache.org/repos/asf?p=")) {
            String repository = sourceUrl.split("asf\\?p=")[1].split(".git")[0];
            sourceUrl = "https://github.com/apache/" + repository;
        }

        // Fix Quarkus URLs using wrong organization name
        if (sourceUrl.contains("github.com/quarkus/")) {
            sourceUrl = sourceUrl.replaceAll("github.com/quarkus/", "github.com/quarkusio/");
        }

        return sourceUrl;
    }

    private Repository getRepository(RepositoryType type) {
        switch (type) {
            case GITHUB:
                return new GitHubRepository(simpleHttp);
            default:
                return null;
        }
    }

}
