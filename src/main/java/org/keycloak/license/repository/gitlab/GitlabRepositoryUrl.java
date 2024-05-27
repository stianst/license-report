package org.keycloak.license.repository.gitlab;

import org.keycloak.license.repository.RepositoryType;
import org.keycloak.license.repository.RepositoryUrl;

public class GitlabRepositoryUrl implements RepositoryUrl {

    private final String hostname;
    private final String owner;
    private final String repository;

    public static GitlabRepositoryUrl parse(String url) {
        if (url.startsWith("https://gitlab")) {
            String[] split = url.split("://")[1].split("/");
            String hostname = split[0];
            String owner = split[1];
            String repository = split[2];
            return new GitlabRepositoryUrl(hostname, owner, repository);
        } else {
            return null;
        }
    }

    private GitlabRepositoryUrl(String hostname, String owner, String repository) {
        this.hostname = hostname;
        this.owner = owner;
        this.repository = repository;
    }

    @Override
    public String getSourceUrl() {
        return "https://" + hostname + "/" + owner + "/" + repository + "/";
    }

    @Override
    public String getApiUrl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTagWebUrl(String tag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RepositoryType getRepositoryType() {
        return RepositoryType.GITLAB;
    }
}
