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
                    final long countStuckThreads = countLinesWithString(Files.readAllLines(filename), "notifyStuckThreadDetected");
                    System.out.println(new StuckThreadsCounterResult(filename.toString(), countStuckThreads));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
