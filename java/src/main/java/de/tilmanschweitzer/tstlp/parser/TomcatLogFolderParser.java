package de.tilmanschweitzer.tstlp.parser;

import de.tilmanschweitzer.tstlp.handler.LogFileParserResult;
import de.tilmanschweitzer.tstlp.handler.StuckThreadHandler;

import java.io.IOException;
import java.util.List;

public interface TomcatLogFolderParser {
    void parseFolder(String folder, String fileFilter) throws IOException;

    List<LogFileParserResult> parseFile(String fileName, List<String> lines);

    void addStuckThreadHandler(StuckThreadHandler stuckThreadHandler);
}
