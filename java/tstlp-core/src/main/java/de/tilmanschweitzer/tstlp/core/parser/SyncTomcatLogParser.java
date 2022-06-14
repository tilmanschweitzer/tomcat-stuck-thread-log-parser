package de.tilmanschweitzer.tstlp.core.parser;

import de.tilmanschweitzer.tstlp.core.handler.LogFileParserResult;
import de.tilmanschweitzer.tstlp.core.handler.LogFileParserResultHandler;
import de.tilmanschweitzer.tstlp.core.handler.StuckThreadHandler;
import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFile;
import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFileProvider;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class SyncTomcatLogParser extends AbstractTomcatLogParser implements TomcatLogParser {

    public SyncTomcatLogParser(Supplier<StuckThreadHandler> stuckThreadHandlerSuppliers, LogFileParserResultHandler resultHandler) {
        super(stuckThreadHandlerSuppliers, resultHandler);
    }

    @Override
    public void parse(TomcatLogFileProvider provider) {
        try (Stream<TomcatLogFile> tomcatLogFiles = provider.provideLogFiles()) {
            tomcatLogFiles.forEach((tomcatLogFile) -> {
                final LogFileParserResult logFileParserResult = parseFile(tomcatLogFile);
                resultHandler.handleResult(logFileParserResult);
            });
        }
    }
}
