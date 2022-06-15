package de.tilmanschweitzer.tstlp.core.parser;

import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFile;
import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFileProvider;

public interface TomcatLogParser<T> {

    void parse(TomcatLogFileProvider tomcatLogFileProvider);

    T parseFile(TomcatLogFile tomcatLogFile);
}
