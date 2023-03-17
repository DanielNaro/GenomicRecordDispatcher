package es.upc.dmag.dispatcher;

import es.upc.dmag.dispatcher.DecodeSubcommand;
import es.upc.dmag.dispatcher.EncodeSubcommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "subcommands", subcommands = { EncodeSubcommand.class,
        DecodeSubcommand.class })
public class Main {

    public static void main(String[] args) {
        int rc = new CommandLine(new Main()).execute(args);
        System.exit(rc);
    }

}