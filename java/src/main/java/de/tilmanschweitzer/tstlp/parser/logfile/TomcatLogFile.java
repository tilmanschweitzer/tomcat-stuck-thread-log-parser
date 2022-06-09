package de.tilmanschweitzer.tstlp.parser.logfile;

import java.util.List;

public interface TomcatLogFile {
    String getFilename();

    List<String> getLines();
}
