package de.tilmanschweitzer.tstlp.parser;

import de.tilmanschweitzer.tstlp.handler.LogFileParserResult;
import de.tilmanschweitzer.tstlp.parser.logfile.TomcatLogFile;
import de.tilmanschweitzer.tstlp.parser.logfile.TomcatLogFileProvider;

public interface TomcatLogParser {

    void parse(TomcatLogFileProvider tomcatLogFileProvider);

    LogFileParserResult parseFile(TomcatLogFile tomcatLogFile);
}
