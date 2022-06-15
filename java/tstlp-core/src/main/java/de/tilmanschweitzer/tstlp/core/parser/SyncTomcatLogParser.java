package de.tilmanschweitzer.tstlp.core.parser;

import de.tilmanschweitzer.tstlp.core.handler.LogFileParserResultHandler;
import de.tilmanschweitzer.tstlp.core.handler.StuckThreadHandler;
import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFile;
import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFileProvider;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class SyncTomcatLogParser<T> extends AbstractTomcatLogParser<T> implements TomcatLogParser<T> {

    public SyncTomcatLogParser(Supplier<StuckThreadHandler<T>> stuckThreadHandlerSuppliers, LogFileParserResultHandler<T> resultHandler) {
        super(stuckThreadHandlerSuppliers, resultHandler);
    }

    @Override
    public void parse(TomcatLogFileProvider provider) {
        try (Stream<TomcatLogFile> tomcatLogFiles = provider.provideLogFiles()) {
            tomcatLogFiles.forEach((tomcatLogFile) -> {
                final T logFileParserResult = parseFile(tomcatLogFile);
                resultHandler.handleResult(logFileParserResult);
            });
        }
    }
}
