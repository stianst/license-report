package org.keycloak.license.repository.unknown;

import org.keycloak.license.repository.RepositoryType;
import org.keycloak.license.repository.RepositoryUrl;

public class UnknownRepositoryUrl implements RepositoryUrl {

    private final String sourceUrl;

    public UnknownRepositoryUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    @Override
    public String getSourceUrl() {
        return sourceUrl;
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
        return RepositoryType.UNKNOWN;
    }
}
