package de.tilmanschweitzer.tstlp.core.parser.logfile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DefaultTomcatLogFile implements TomcatLogFile {
    final String filename;

    public DefaultTomcatLogFile(String filename) {
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public List<String> getLines() {
        try {
            return Files.readAllLines(Path.of(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
