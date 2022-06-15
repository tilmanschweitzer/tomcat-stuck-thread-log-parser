package de.tilmanschweitzer.tstlp.shell;

import de.tilmanschweitzer.tstlp.core.handler.LogFileParserResultHandler;
import de.tilmanschweitzer.tstlp.core.handler.StuckThreadHandler;
import de.tilmanschweitzer.tstlp.core.handler.codelineranking.CodeLineByOccurrence;
import de.tilmanschweitzer.tstlp.core.handler.codelineranking.CodeLineRankingLogFileParserResult;
import de.tilmanschweitzer.tstlp.core.handler.codelineranking.CodeLineRankingStuckThreadHandler;
import de.tilmanschweitzer.tstlp.core.handler.counting.CountingLogFileParserResult;
import de.tilmanschweitzer.tstlp.core.handler.counting.CountingStuckThreadHandler;
import de.tilmanschweitzer.tstlp.core.parser.AsyncTomcatLogParser;
import de.tilmanschweitzer.tstlp.core.parser.SyncTomcatLogParser;
import de.tilmanschweitzer.tstlp.core.parser.TomcatLogParser;
import de.tilmanschweitzer.tstlp.core.parser.logfile.DefaultTomcatLogfileProvider;
import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFile;
import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFileProvider;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class App {

    public static void main(String[] args) {
        final Arguments arguments = Arguments.parseArguments(args);

        if (arguments.analyseCodeLineOccurrences()) {
            parse(arguments, CodeLineRankingStuckThreadHandler::new, App::printResult);
        } else {
            parse(arguments, CountingStuckThreadHandler::new, App::printResult);
        }
    }

    private static <T> void parse(Arguments arguments, Supplier<StuckThreadHandler<T>> stuckThreadHandlerSupplier, LogFileParserResultHandler<T> resultHandler) {
        final TomcatLogParser<T> tomcatLogFolderParser;
        if (arguments.isRunAsync()) {
            tomcatLogFolderParser = new AsyncTomcatLogParser<>(stuckThreadHandlerSupplier, resultHandler);
        } else {
            tomcatLogFolderParser = new SyncTomcatLogParser<>(stuckThreadHandlerSupplier, resultHandler);
        }

        final Predicate<TomcatLogFile> filterPredicate = (tomcatLogFile -> tomcatLogFile.getFilename().contains(arguments.getFileFilter()));
        final TomcatLogFileProvider tomcatLogFileProvider = new DefaultTomcatLogfileProvider(arguments.getFolder(), filterPredicate);

        tomcatLogFolderParser.parse(tomcatLogFileProvider);
    }

    public static void printResult(CountingLogFileParserResult result) {
        System.out.println(result.getFilename() + ":" + result.getStuckThreadsCount());
    }

    public static void printResult(CodeLineRankingLogFileParserResult result) {
        final Optional<CodeLineByOccurrence> mostOftenCodeLine = indexOf(result.getMeaningfulCodeLineByOccurrences(1), 0);
        final String codeLineAppend = mostOftenCodeLine.map((codeLine) -> " (" + codeLine + ")").orElse("");
        System.out.println(result.getFilename() + ":" + result.getStuckThreadsCount() + codeLineAppend);
    }

    private static <T> Optional<T> indexOf(List<T> list, int index) {
        if (list.size() <= index) {
            return Optional.empty();
        }
        return Optional.of(list.get(index));
    }
}
