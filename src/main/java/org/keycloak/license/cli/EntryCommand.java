package org.keycloak.license.cli;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine;

import java.io.File;

@TopCommand
@CommandLine.Command(mixinStandardHelpOptions = true, subcommands = { ReportCommand.class, CheckDistCommand.class, ReleaseDatesCommand.class })
public class EntryCommand implements Runnable {

    @CommandLine.Option(names = {"-p", "--profile"}, description = "Profile", required = true)
    String profile;

    @CommandLine.Option(names = {"-c", "--config"}, description = "Config directory", defaultValue = "conf")
    File configDir;

    @Override
    public void run() {

    }
}
