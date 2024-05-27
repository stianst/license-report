package org.keycloak.license.repository.sourceforge;

import org.keycloak.license.repository.RepositoryType;
import org.keycloak.license.repository.RepositoryUrl;

public class SourceForgeRepositoryUrl implements RepositoryUrl {

    private final String project;

    public static SourceForgeRepositoryUrl parse(String url) {
        if ((url.startsWith("https") || url.startsWith("http")) && url.contains("://sourceforge.net/p/")) {
            String project = url.split("://sourceforge.net/p/")[1].split("/")[0];
            return new SourceForgeRepositoryUrl(project);
        } else {
            return null;
        }
    }

    private SourceForgeRepositoryUrl(String project) {
        this.project = project;
    }

    @Override
    public String getSourceUrl() {
        return "https://sourceforge.net/p/" + project + "/source/";
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
        return RepositoryType.SOURCEFORGE;
    }
}
