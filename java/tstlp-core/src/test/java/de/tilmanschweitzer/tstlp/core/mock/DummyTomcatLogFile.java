package de.tilmanschweitzer.tstlp.core.mock;

import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFile;

import java.util.List;

public class DummyTomcatLogFile implements TomcatLogFile {

    private final String filename;
    private final List<String> lines;

    public DummyTomcatLogFile(String filename, List<String> lines) {
        this.filename = filename;
        this.lines = lines;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public List<String> getLines() {
        return lines;
    }
}
