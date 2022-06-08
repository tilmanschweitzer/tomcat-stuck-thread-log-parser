package de.tilmanschweitzer.tstlp.parser;

import de.tilmanschweitzer.tstlp.handler.LogFileParserResult;
import de.tilmanschweitzer.tstlp.handler.StuckThreadHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SyncTomcatLogFolderParser extends AbstractTomcatLogFolderParser implements TomcatLogFolderParser {

    public SyncTomcatLogFolderParser(Supplier<StuckThreadHandler> stuckThreadHandlerSuppliers) {
        super(stuckThreadHandlerSuppliers);
    }

    @Override
    public void parseFolder(String folder, String fileFilter) throws IOException {
        try (Stream<Path> paths = matchingFilesInFolder(folder, fileFilter)) {
            paths.forEach((filename) -> {
                try {
                    final LogFileParserResult logFileParserResult = parseFile(filename.toString(), Files.readAllLines(filename));
                    System.out.println(logFileParserResult.getPrintableResult());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
