package org.keycloak.license.repository.bitbucket;

import org.keycloak.license.repository.RepositoryType;
import org.keycloak.license.repository.RepositoryUrl;

public class BitBucketRepositoryUrl implements RepositoryUrl {

    private final String owner;
    private final String repository;

    public static BitBucketRepositoryUrl parse(String url) {
        if ((url.startsWith("https") || url.startsWith("http")) && url.contains("://bitbucket.org")) {
            String[] s = url.split("://")[1].split("/");
            String owner = s[1];
            String repository = s[2];
            return new BitBucketRepositoryUrl(owner, repository);
        } else {
            return null;
        }
    }

    private BitBucketRepositoryUrl(String owner, String repository) {
        this.owner = owner;
        this.repository = repository;
    }

    @Override
    public String getSourceUrl() {
        return "https://bitbucket.org/" + owner + "/" + repository + "/";
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
        return RepositoryType.BITBUCKET;
    }
}
