package de.tilmanschweitzer.tstlp.parser;

import de.tilmanschweitzer.tstlp.handler.LogFileParserResult;
import de.tilmanschweitzer.tstlp.handler.StuckThreadHandler;
import de.tilmanschweitzer.tstlp.parser.logfile.TomcatLogFile;
import de.tilmanschweitzer.tstlp.parser.logfile.TomcatLogFileProvider;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class SyncTomcatLogParser extends AbstractTomcatLogParser implements TomcatLogParser {

    public SyncTomcatLogParser(Supplier<StuckThreadHandler> stuckThreadHandlerSuppliers) {
        super(stuckThreadHandlerSuppliers);
    }

    @Override
    public void parse(TomcatLogFileProvider provider) {
        try (Stream<TomcatLogFile> tomcatLogFiles = provider.provideLogFiles()) {
            tomcatLogFiles.forEach((tomcatLogFile) -> {
                final LogFileParserResult logFileParserResult = parseFile(tomcatLogFile);
                System.out.println(logFileParserResult.getPrintableResult());
            });
        }
    }
}
