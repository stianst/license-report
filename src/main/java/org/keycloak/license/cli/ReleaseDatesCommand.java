package org.keycloak.license.cli;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.keycloak.license.dependencies.Dependency;
import org.keycloak.license.dependencies.DependencyManager;
import org.keycloak.license.http.SimpleHttp;
import org.keycloak.license.report.LicenseReport;
import org.keycloak.license.report.VerifyManager;
import org.keycloak.license.repository.RepositoryManager;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandLine.Command(name = "dates")
public class ReleaseDatesCommand implements Runnable {
    private static final Pattern JAVA_PATTERN = Pattern.compile("pkg:maven/([^/]*)/([^@]*)@([^?]*)[?].*");

    private static final Logger LOGGER = Logger.getLogger(ReleaseDatesCommand.class);

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
        List<Dependency> dependencies = dependencyManager.getDependencies();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/YYYY");
        SimpleHttp simpleHttp = new SimpleHttp();

        for (Dependency dependency : dependencies) {
            if (dependency.getPurl().contains("npm")) {
                String packageName = dependency.getPurl().substring(0, dependency.getPurl().indexOf('@')).replace("pkg:npm/", "");
                try {
                    NpmRegistryEntry npmRegistryEntry = simpleHttp.get(NpmRegistryEntry.class, "https://registry.npmjs.org/" + packageName, null);
                    Date date = npmRegistryEntry.getTime().get(dependency.getVersion());
                    System.out.println("npm," + dependency.getIdentifier() + "," + simpleDateFormat.format(date));
                } catch (IOException e) {
                    System.out.println(dependency.getIdentifier() + "," + e.getMessage());
                }
            } else if (dependency.getPurl().contains("maven")) {

                Matcher matcher = JAVA_PATTERN.matcher(dependency.getPurl());

                if (matcher.matches()) {

                    String g = matcher.group(1);
                    String a = matcher.group(2);
                    String v = matcher.group(3);

                    String url = "https://search.maven.org/solrsearch/select?q=g:" + g + "+AND+a:" + a + "+AND+v:" + v + "&core=gav&rows=10&wt=json";
                    try {
                        MvnRegistryEntry mvnRegistryEntry = simpleHttp.get(MvnRegistryEntry.class, url, null);

                        long timestamp = mvnRegistryEntry.getResponse().getDocs()[0].getTimestamp();
                        Date date = new Date(timestamp);

                        System.out.println("maven," + dependency.getIdentifier() + "," + simpleDateFormat.format(date));
                    } catch (IOException e) {
                        System.err.println(dependency.getIdentifier() + "," + e.getMessage());
                    }
                } else {
                    System.err.println("Failed to parse: " + dependency.getPurl());
                }
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class NpmRegistryEntry {

        public Map<String, Date> time;

        public Map<String, Date> getTime() {
            return time;
        }

        public void setTime(Map<String, Date> time) {
            this.time = time;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class MvnRegistryEntry {

        public  MvnRegistryResponse response;

        public MvnRegistryResponse getResponse() {
            return response;
        }

        public void setResponse(MvnRegistryResponse response) {
            this.response = response;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class MvnRegistryResponse {

        private MvnRegistryDoc[] docs;

        public MvnRegistryDoc[] getDocs() {
            return docs;
        }

        public void setDocs(MvnRegistryDoc[] docs) {
            this.docs = docs;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class MvnRegistryDoc {

        private long timestamp;

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

}
