package de.tilmanschweitzer.tstlp.parser;

import de.tilmanschweitzer.tstlp.handler.LogFileParserResult;

import java.io.IOException;
import java.util.List;

public interface TomcatLogFolderParser {
    void parseFolder(String folder, String fileFilter) throws IOException;

    LogFileParserResult parseFile(String fileName, List<String> lines);
}
