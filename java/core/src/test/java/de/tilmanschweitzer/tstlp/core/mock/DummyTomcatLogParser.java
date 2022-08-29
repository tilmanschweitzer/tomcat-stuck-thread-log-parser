package de.tilmanschweitzer.tstlp.core.mock;

import de.tilmanschweitzer.tstlp.core.handler.LogFileParserResultHandler;
import de.tilmanschweitzer.tstlp.core.handler.StuckThreadHandler;
import de.tilmanschweitzer.tstlp.core.parser.AbstractTomcatLogParser;
import de.tilmanschweitzer.tstlp.core.parser.TomcatLogParser;
import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFileProvider;

import java.util.function.Supplier;

public class DummyTomcatLogParser<T> extends AbstractTomcatLogParser<T> implements TomcatLogParser<T> {

    public DummyTomcatLogParser(Supplier<StuckThreadHandler<T>> stuckThreadHandlerSuppliers, LogFileParserResultHandler<T> resultHandler) {
        super(stuckThreadHandlerSuppliers, resultHandler);
    }

    @Override
    public void parse(TomcatLogFileProvider tomcatLogFileProvider) {

    }
}
