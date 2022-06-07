package de.tilmanschweitzer.tstlp.parser;

import de.tilmanschweitzer.tstlp.handler.LogFileParserResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class SyncTomcatLogFolderParser extends AbstractTomcatLogFolderParser implements TomcatLogFolderParser {
    @Override
    public void parseFolder(String folder, String fileFilter) throws IOException {
        try (Stream<Path> paths = matchingFilesInFolder(folder, fileFilter)) {
            paths.forEach((filename) -> {
                try {
                    parseFile(filename.toString(), Files.readAllLines(filename))
                            .stream()
                            .map(LogFileParserResult::getPrintableResult)
                            .forEach(System.out::println);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
