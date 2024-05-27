package org.keycloak.license.repository.github;

import org.keycloak.license.repository.RepositoryType;
import org.keycloak.license.repository.RepositoryUrl;

public class GitHubRepositoryUrl implements RepositoryUrl {

    private final String owner;
    private final String repository;

    public static GitHubRepositoryUrl parse(String url) {
        if ((url.startsWith("scm:git:git") && url.contains("github.com")) || (url.startsWith("git+https://github.com"))) {
            String[] s = url.split("github\\.com.")[1].split("/");
            String owner = s[0];
            String repository = s[1].replace(".git", "");
            return new GitHubRepositoryUrl(owner, repository);
        } else if ((url.startsWith("https") || url.startsWith("http")) && url.contains("://github.com")) {
            String[] s = url.split("://")[1].split("/");
            String owner = s[1];
            String repository = s[2].replace(".git", "");
            if (repository.indexOf('#') != -1) {
                repository = repository.substring(0, repository.indexOf('#'));
            }
            return new GitHubRepositoryUrl(owner, repository);
        } else {
            return null;
        }
    }

    private GitHubRepositoryUrl(String owner, String repository) {
        this.owner = owner;
        this.repository = repository;
    }

    @Override
    public String getSourceUrl() {
        return "https://github.com/" + owner + "/" + repository + "/";
    }

    @Override
    public String getApiUrl() {
        return "https://api.github.com/repos/" + owner + "/" + repository + "/";
    }

    @Override
    public String getTagWebUrl(String tag) {
        return getSourceUrl() + "tree/" + tag + "/";
    }

    @Override
    public RepositoryType getRepositoryType() {
        return RepositoryType.GITHUB;
    }

}
