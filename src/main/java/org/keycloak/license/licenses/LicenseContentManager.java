package org.keycloak.license.licenses;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.license.config.Config;
import org.keycloak.license.config.LicenseContentMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class LicenseContentManager {

    @Inject
    Config config;

    private File licenseCache = new File("license-cache");

    @PostConstruct
    public void init() {
        licenseCache = new File(config.getGlobalCacheDir(), "licenses");
        licenseCache.mkdirs();
    }

    public String getLicenseContent(String licenseUrl) throws URISyntaxException, IOException {
        if (licenseUrl == null) {
            return null;
        }

        licenseUrl = getMappedUrl(licenseUrl);

        String id = URLEncoder.encode(licenseUrl, StandardCharsets.UTF_8);
        File file = new File(licenseCache, id);
        if (!file.isFile()) {
            InputStream inputStream = new URI(licenseUrl).toURL().openStream();
            FileOutputStream outputStream = new FileOutputStream(file);
            inputStream.transferTo(outputStream);
            outputStream.close();
        }

        try (InputStream is = new FileInputStream(file)) {
            return new String(is.readAllBytes(),StandardCharsets.UTF_8);
        }
    }

    private String getMappedUrl(String licenseUrl) {
        return config.getLicenseContentMappings().stream().filter(m -> m.getLicenseUrl().equals(licenseUrl)).map(LicenseContentMapping::getMapsTo).findFirst().orElse(licenseUrl);
    }

}
