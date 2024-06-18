package org.keycloak.license;

import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusMainTest
public class LicenseReportTest {

    @Test
    public void testLaunch(QuarkusMainLauncher launcher) {
        LaunchResult launch = launcher.launch(
                "-p=server",
                "report",
                "--sbom=/home/st/tmp/keycloak/quarkus/deployment/target/bom.json"
        );
        Assertions.assertEquals(0, launch.exitCode());

    }

    @Test
    public void generate(QuarkusMainLauncher launcher) {
        LaunchResult launch = launcher.launch(
                "-p=keycloak",
                "report",
                "--sbom=cache/repositories/keycloak/bom.json",
                "--sbom=cache/repositories/keycloak/quarkus/deployment/target/bom.json"
                ,"--sbom=cache/repositories/keycloak/operator/target/bom.json"
        );
        Assertions.assertEquals(0, launch.exitCode());

    }

}
