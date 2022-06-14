package de.tilmanschweitzer.tstlp.shell;

import de.tilmanschweitzer.tstlp.core.handler.LogFileParserResultHandler;
import de.tilmanschweitzer.tstlp.core.handler.StuckThreadHandler;
import de.tilmanschweitzer.tstlp.core.handler.codelineranking.CodeLineRankingStuckThreadHandler;
import de.tilmanschweitzer.tstlp.core.handler.counting.CountingStuckThreadHandler;
import de.tilmanschweitzer.tstlp.core.parser.AsyncTomcatLogParser;
import de.tilmanschweitzer.tstlp.core.parser.SyncTomcatLogParser;
import de.tilmanschweitzer.tstlp.core.parser.TomcatLogParser;
import de.tilmanschweitzer.tstlp.core.parser.logfile.DefaultTomcatLogfileProvider;
import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFile;
import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFileProvider;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class App {

    public static void main(String[] args) {
        final Arguments arguments = Arguments.parseArguments(args);

        final LogFileParserResultHandler resultHandler = result -> { System.out.println(result.getPrintableResult()); };
        final Supplier<StuckThreadHandler> stuckThreadHandlerSupplier;
        if (arguments.analyseCodeLineOccurrences()) {
            stuckThreadHandlerSupplier = CodeLineRankingStuckThreadHandler::new;
        } else {
            stuckThreadHandlerSupplier = CountingStuckThreadHandler::new;
        }

        final TomcatLogParser tomcatLogFolderParser;
        if (arguments.isRunAsync()) {
            tomcatLogFolderParser = new AsyncTomcatLogParser(stuckThreadHandlerSupplier, resultHandler);
        } else {
            tomcatLogFolderParser = new SyncTomcatLogParser(stuckThreadHandlerSupplier, resultHandler);
        }

        final Predicate<TomcatLogFile> filterPredicate = (tomcatLogFile -> tomcatLogFile.getFilename().contains(arguments.getFileFilter()));
        final TomcatLogFileProvider tomcatLogFileProvider = new DefaultTomcatLogfileProvider(arguments.getFolder(), filterPredicate);

        tomcatLogFolderParser.parse(tomcatLogFileProvider);
    }
}
