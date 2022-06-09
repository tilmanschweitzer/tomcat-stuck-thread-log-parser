package de.tilmanschweitzer.tstlp;

import de.tilmanschweitzer.tstlp.handler.StuckThreadHandler;
import de.tilmanschweitzer.tstlp.handler.codelineranking.CodeLineRankingStuckThreadHandler;
import de.tilmanschweitzer.tstlp.handler.counting.CountingStuckThreadHandler;
import de.tilmanschweitzer.tstlp.parser.AsyncTomcatLogParser;
import de.tilmanschweitzer.tstlp.parser.SyncTomcatLogParser;
import de.tilmanschweitzer.tstlp.parser.TomcatLogParser;
import de.tilmanschweitzer.tstlp.parser.logfile.DefaultTomcatLogfileProvider;
import de.tilmanschweitzer.tstlp.parser.logfile.TomcatLogFile;
import de.tilmanschweitzer.tstlp.parser.logfile.TomcatLogFileProvider;

import java.io.IOException;
import java.util.function.Predicate;
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

        final TomcatLogParser tomcatLogFolderParser;
        if (arguments.isRunAsync()) {
            tomcatLogFolderParser = new AsyncTomcatLogParser(stuckThreadHandlerSupplier);
        } else {
            tomcatLogFolderParser = new SyncTomcatLogParser(stuckThreadHandlerSupplier);
        }

        final Predicate<TomcatLogFile> filterPredicate = (tomcatLogFile -> tomcatLogFile.getFilename().contains(arguments.getFileFilter()));
        final TomcatLogFileProvider tomcatLogFileProvider = new DefaultTomcatLogfileProvider(arguments.getFolder(), filterPredicate);

        tomcatLogFolderParser.parse(tomcatLogFileProvider);
    }
}
