package org.keycloak.license.repository.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.keycloak.license.http.SimpleHttp;
import org.keycloak.license.repository.Repository;
import org.keycloak.license.repository.RepositoryUrl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GitHubRepository implements Repository {

    private final SimpleHttp simpleHttp;

    public GitHubRepository(SimpleHttp simpleHttp) {
        this.simpleHttp = simpleHttp;
    }

    @Override
    public List<String> listTags(RepositoryUrl url) throws IOException {
        Tag[] tags = simpleHttp.get(Tag[].class, url.getApiUrl() + "git/refs/tags", getHeaders());
        return Arrays.stream(tags).map(t -> t.getRef().replace("refs/tags/", "")).toList();
    }

    @Override
    public List<String> listFiles(RepositoryUrl url) throws IOException {
        Content[] contents = simpleHttp.get(Content[].class, url.getApiUrl() + "contents/", getHeaders());
        return Arrays.stream(contents).filter(c -> c.getType().equals("file")).map(Content::getDownload_url).toList();
    }

    private Map<String, String> getHeaders() {
        String token = getToken();
        if (token != null) {
            return Map.of("Authorization", "Bearer " + token);
        }
        return null;
    }

    private String getToken() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"gh", "auth", "token"});
            process.waitFor();
            if (process.exitValue() == 0) {
                return new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            }
        } catch (Exception e) {
        }
        return null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Tag {

        private String ref;

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Content {

        private String path;
        private String type;
        private String download_url;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDownload_url() {
            return download_url;
        }

        public void setDownload_url(String download_url) {
            this.download_url = download_url;
        }
    }

}
