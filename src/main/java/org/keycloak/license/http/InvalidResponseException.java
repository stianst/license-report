package org.keycloak.license.http;

import java.io.IOException;

public class InvalidResponseException extends IOException {

    private final int statusCode;

    public InvalidResponseException(String url, int statusCode) {
        super("Request to " + url + " failed: " + statusCode);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
