package de.tilmanschweitzer.tstlp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class SyncTomcatStuckThreadLogParser extends AbstractTomcatStuckThreadLogParser implements TomcatStuckThreadLogParser {
    @Override
    public void parse(String folder, String fileFilter) throws IOException {
        try (Stream<Path> paths = matchingFilesInFolder(folder, fileFilter)) {
            paths.forEach((filename) -> {
                try {
                    System.out.println(analyze(filename.toString(), Files.readAllLines(filename)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
