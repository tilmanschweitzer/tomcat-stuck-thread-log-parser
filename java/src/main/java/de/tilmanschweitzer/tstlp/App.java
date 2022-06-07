package de.tilmanschweitzer.tstlp;

import de.tilmanschweitzer.tstlp.handler.codelineranking.CodeLineRankingStuckThreadHandler;
import de.tilmanschweitzer.tstlp.handler.counting.CountingStuckThreadHandler;
import de.tilmanschweitzer.tstlp.parser.AsyncTomcatLogFolderParser;
import de.tilmanschweitzer.tstlp.parser.SyncTomcatLogFolderParser;
import de.tilmanschweitzer.tstlp.parser.TomcatLogFolderParser;

import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {
        final Arguments arguments = Arguments.parseArguments(args);

        final TomcatLogFolderParser tomcatLogFolderParser;
        if (arguments.isRunAsync()) {
            tomcatLogFolderParser = new AsyncTomcatLogFolderParser();
        } else {
            tomcatLogFolderParser = new SyncTomcatLogFolderParser();
        }

        if (arguments.analyseCodeLineOccurrences()) {
            tomcatLogFolderParser.addStuckThreadHandler(new CodeLineRankingStuckThreadHandler());
        } else {
            tomcatLogFolderParser.addStuckThreadHandler(new CountingStuckThreadHandler());
        }

        tomcatLogFolderParser.parseFolder(arguments.getFolder(), arguments.getFileFilter());
    }
}
