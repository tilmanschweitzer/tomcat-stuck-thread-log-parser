package de.tilmanschweitzer.tstlp.core.parser;

import de.tilmanschweitzer.tstlp.core.handler.LogFileParserResult;
import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFile;
import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFileProvider;

public interface TomcatLogParser {

    void parse(TomcatLogFileProvider tomcatLogFileProvider);

    LogFileParserResult parseFile(TomcatLogFile tomcatLogFile);
}
