package org.keycloak.license.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;

public class ConfigProducer {

    @Produces
    @ApplicationScoped
    Config config(CommandLine.ParseResult parseResult) throws IOException {
        String profile = parseResult.matchedOptionValue("p", null);
        File configDir = parseResult.matchedOptionValue("c", new File("conf"));

        parseResult.matchedOptionValue("c", new File("conf"));

        return new Config(configDir, profile);
    }

}
