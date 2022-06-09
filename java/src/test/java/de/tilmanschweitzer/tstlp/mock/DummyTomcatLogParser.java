package de.tilmanschweitzer.tstlp.mock;

import de.tilmanschweitzer.tstlp.handler.StuckThreadHandler;
import de.tilmanschweitzer.tstlp.parser.AbstractTomcatLogParser;
import de.tilmanschweitzer.tstlp.parser.TomcatLogParser;
import de.tilmanschweitzer.tstlp.parser.logfile.TomcatLogFileProvider;

import java.util.function.Supplier;

public class DummyTomcatLogParser extends AbstractTomcatLogParser implements TomcatLogParser {

    public DummyTomcatLogParser(Supplier<StuckThreadHandler> stuckThreadHandlerSuppliers) {
        super(stuckThreadHandlerSuppliers);
    }

    @Override
    public void parse(TomcatLogFileProvider tomcatLogFileProvider) {

    }
}
