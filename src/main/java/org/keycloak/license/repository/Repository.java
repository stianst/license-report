package org.keycloak.license.repository;

import java.io.IOException;
import java.util.List;

public interface Repository {

    List<String> listTags(RepositoryUrl url) throws IOException;

    List<String> listFiles(RepositoryUrl url) throws IOException;

}
