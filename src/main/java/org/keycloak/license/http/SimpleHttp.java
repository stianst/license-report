package org.keycloak.license.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@ApplicationScoped
public class SimpleHttp {

    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public SimpleHttp() {
        client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        objectMapper = new ObjectMapper();
    }

    public boolean checkUrl(String url) {
        try {
            URI uri = new URI(url);
            HttpRequest request = HttpRequest.newBuilder(uri).GET().version(HttpClient.Version.HTTP_1_1).build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public <T> T get(Class<T> type, String url, Map<String, String> headers) throws IOException {
        try {
            URI uri = new URI(url);

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(uri);
            if (headers != null) {
                for (Map.Entry<String, String> e : headers.entrySet()) {
                    requestBuilder.header(e.getKey(), e.getValue());
                }
            }

            HttpRequest request = requestBuilder.GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IOException("Request to " + url + " failed: " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), type);
        } catch (Exception e) {
            throw new IOException("Request to " + url + " failed", e);
        }
    }


}
