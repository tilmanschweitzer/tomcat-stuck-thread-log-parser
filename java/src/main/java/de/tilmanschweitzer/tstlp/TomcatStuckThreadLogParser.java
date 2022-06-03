package de.tilmanschweitzer.tstlp;

import java.io.IOException;

public interface TomcatStuckThreadLogParser {
    void parse(String folder, String fileFilter) throws IOException;
}
