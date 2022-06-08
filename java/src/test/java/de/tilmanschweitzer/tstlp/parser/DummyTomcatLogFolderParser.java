package de.tilmanschweitzer.tstlp.parser;

import de.tilmanschweitzer.tstlp.handler.StuckThreadHandler;

import java.io.IOException;
import java.util.function.Supplier;

public class DummyTomcatLogFolderParser extends AbstractTomcatLogFolderParser implements TomcatLogFolderParser {

    public DummyTomcatLogFolderParser(Supplier<StuckThreadHandler> stuckThreadHandlerSuppliers) {
        super(stuckThreadHandlerSuppliers);
    }

    @Override
    public void parseFolder(String folder, String fileFilter) throws IOException {

    }
}
