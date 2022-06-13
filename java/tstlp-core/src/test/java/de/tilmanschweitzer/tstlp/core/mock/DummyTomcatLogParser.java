package de.tilmanschweitzer.tstlp.core.mock;

import de.tilmanschweitzer.tstlp.core.handler.StuckThreadHandler;
import de.tilmanschweitzer.tstlp.core.parser.AbstractTomcatLogParser;
import de.tilmanschweitzer.tstlp.core.parser.TomcatLogParser;
import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFileProvider;

import java.util.function.Supplier;

public class DummyTomcatLogParser extends AbstractTomcatLogParser implements TomcatLogParser {

    public DummyTomcatLogParser(Supplier<StuckThreadHandler> stuckThreadHandlerSuppliers) {
        super(stuckThreadHandlerSuppliers);
    }

    @Override
    public void parse(TomcatLogFileProvider tomcatLogFileProvider) {

    }
}
