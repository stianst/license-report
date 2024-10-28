package org.keycloak.license.report;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.keycloak.license.config.Config;
import org.keycloak.license.dependencies.Dependency;
import org.keycloak.license.dependencies.DependencyManager;
import org.keycloak.license.licenses.LicenseContentManager;
import org.keycloak.license.licenses.spdx.SpdxLicenses;
import org.keycloak.license.report.beans.CncfBean;
import org.keycloak.license.report.beans.ReportBean;
import org.keycloak.license.repository.RepositoryManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

@ApplicationScoped
public class LicenseReport {

    private static final Logger LOGGER = Logger.getLogger(LicenseReport.class);

    @Location("third-party-notice.html")
    Template thirdPartyNotice;

    @Location("cncf-report.html")
    Template cncfReport;

    @Location("cncf-report.md")
    Template cncfReportMd;

    @Inject
    RepositoryManager repositoryManager;

    @Inject
    DependencyManager dependencyManager;

    @Inject
    LicenseContentManager licenseContentManager;

    @Inject
    SpdxLicenses spdxLicenses;

    @Inject
    Config config;

    public void createReport() {
        try {
            ReportBean reportBean = new ReportBean(repositoryManager, licenseContentManager, spdxLicenses, config.getCncfApproved());
            for (Dependency d : dependencyManager.getDependencies()) {
                reportBean.add(d);
            }

            CncfBean cncfBean = new CncfBean(reportBean);

            createReport(reportBean, cncfBean, thirdPartyNotice, "third-party-notice-" + config.getProfile() + ".html");
            createReport(reportBean, cncfBean, cncfReport, "cncf-report-" + config.getProfile() + ".html");
            createReport(reportBean, cncfBean, cncfReportMd, "cncf-report-" + config.getProfile() + ".md");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createReport(ReportBean reportBean, CncfBean cncfBean, Template template, String outputFilename) throws IOException {
        File outputFile = new File(outputFilename);
        PrintWriter pw = new PrintWriter(new FileWriter(outputFile));
        String report = template.data("report", reportBean, "cncf", cncfBean).render();
        pw.write(report);
        pw.close();

        LOGGER.infov("Created " + outputFile.getAbsoluteFile().toURI());
    }

}
