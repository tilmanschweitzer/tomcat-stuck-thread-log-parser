package de.tilmanschweitzer.tstlp;

import de.tilmanschweitzer.tstlp.handler.StuckThreadHandler;
import de.tilmanschweitzer.tstlp.handler.codelineranking.CodeLineRankingStuckThreadHandler;
import de.tilmanschweitzer.tstlp.handler.counting.CountingStuckThreadHandler;
import de.tilmanschweitzer.tstlp.parser.AsyncTomcatLogFolderParser;
import de.tilmanschweitzer.tstlp.parser.SyncTomcatLogFolderParser;
import de.tilmanschweitzer.tstlp.parser.TomcatLogFolderParser;

import java.io.IOException;
import java.util.function.Supplier;

public class App {

    public static void main(String[] args) throws IOException {
        final Arguments arguments = Arguments.parseArguments(args);


        final Supplier<StuckThreadHandler> stuckThreadHandlerSupplier;
        if (arguments.analyseCodeLineOccurrences()) {
            stuckThreadHandlerSupplier = CodeLineRankingStuckThreadHandler::new;
        } else {
            stuckThreadHandlerSupplier = CountingStuckThreadHandler::new;
        }

        final TomcatLogFolderParser tomcatLogFolderParser;
        if (arguments.isRunAsync()) {
            tomcatLogFolderParser = new AsyncTomcatLogFolderParser(stuckThreadHandlerSupplier);
        } else {
            tomcatLogFolderParser = new SyncTomcatLogFolderParser(stuckThreadHandlerSupplier);
        }

        tomcatLogFolderParser.parseFolder(arguments.getFolder(), arguments.getFileFilter());
    }
}
