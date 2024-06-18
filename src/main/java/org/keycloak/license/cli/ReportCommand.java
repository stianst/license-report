package org.keycloak.license.cli;

import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.keycloak.license.dependencies.DependencyManager;
import org.keycloak.license.report.LicenseReport;
import org.keycloak.license.report.VerifyManager;
import org.keycloak.license.repository.RepositoryManager;
import picocli.CommandLine;

import java.io.File;
import java.util.List;

@CommandLine.Command(name = "report")
public class ReportCommand implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ReportCommand.class);

    @CommandLine.Option(names = {"-s", "--sbom"}, description = "One or more CycleoneDX SBOM files", required = true)
    List<File> sboms;

    @CommandLine.Option(names = {"--offline"}, description = "Don't fetch additional remote information", defaultValue = "false")
    boolean offline;

    @CommandLine.Option(names = {"--ignore-validation"}, description = "Build report even if validation fails", defaultValue = "false")
    boolean ignoreValidation;

    @Inject
    LicenseReport licenseReport;

    @Inject
    RepositoryManager repositoryManager;

    @Inject
    DependencyManager dependencyManager;

    @Inject
    VerifyManager verifyManager;

    @Override
    public void run() {
        dependencyManager.parseSboms(sboms);

        if (!offline) {
            LOGGER.info("Resolving repository information");
            repositoryManager.resolveRepositoryInfo();
            LOGGER.info("Resolved repository information");
        }

        LOGGER.info("Verifying information");
        boolean valid = verifyManager.verify();

        if (valid) {
            LOGGER.info("Information valid");
        } else {
            LOGGER.error("Found invalid information");
        }

        if (valid || ignoreValidation) {
            LOGGER.info("Creating reports");
            licenseReport.createReport();
        } else {
            System.exit(1);
        }
    }
}
