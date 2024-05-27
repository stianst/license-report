package org.keycloak.license.report;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.license.config.Config;
import org.keycloak.license.dependencies.Dependency;
import org.keycloak.license.dependencies.DependencyManager;
import org.keycloak.license.licenses.LicenseContentManager;
import org.keycloak.license.licenses.spdx.SpdxLicenses;
import org.keycloak.license.report.beans.ReportBean;
import org.keycloak.license.repository.RepositoryManager;

import java.io.PrintWriter;

@ApplicationScoped
public class LicenseReport {

    @Location("third-party-notice.html")
    Template thirdPartyNotice;

    @Location("cncf-report.html")
    Template cncfReport;

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
        ReportBean reportBean = new ReportBean(repositoryManager, licenseContentManager, spdxLicenses);
        for (Dependency d : dependencyManager.getDependencies()) {
            reportBean.add(d);
        }

        try {
            PrintWriter pw = new PrintWriter("third-party-notice-" + config.getProfile() + ".html");
            String report = thirdPartyNotice.data("report", reportBean).render();
            pw.write(report);
            pw.close();

            PrintWriter pw2 = new PrintWriter("cncf-report-" + config.getProfile() + ".html");
            String report2 = cncfReport.data("report", reportBean).render();
            pw2.write(report2);
            pw2.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
