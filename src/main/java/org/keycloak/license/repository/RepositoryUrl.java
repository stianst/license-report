package org.keycloak.license.repository;

public interface RepositoryUrl {

    String getSourceUrl();

    String getApiUrl();

    String getTagWebUrl(String tag);

    RepositoryType getRepositoryType();

}
