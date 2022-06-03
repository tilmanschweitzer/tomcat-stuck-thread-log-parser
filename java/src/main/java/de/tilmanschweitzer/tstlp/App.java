package de.tilmanschweitzer.tstlp;

import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {
        final Arguments arguments = Arguments.parseArguments(args);

        final TomcatStuckThreadLogParser tomcatStuckThreadLogParser;

        if (arguments.isRunAsync()) {
            tomcatStuckThreadLogParser = new AsyncTomcatStuckThreadLogParser();
        } else {
            tomcatStuckThreadLogParser = new SyncTomcatStuckThreadLogParser();
        }

        tomcatStuckThreadLogParser.parse(arguments.getFolder(), arguments.getFileFilter());
    }
}
