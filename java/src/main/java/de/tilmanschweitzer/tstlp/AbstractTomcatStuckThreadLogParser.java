package de.tilmanschweitzer.tstlp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class AbstractTomcatStuckThreadLogParser {

    public static final String STUCK_THREAD_MARKER = "org.apache.catalina.valves.StuckThreadDetectionValve.notifyStuckThreadDetected";

    protected static Stream<Path> matchingFilesInFolder(String folder, String filterString) throws IOException {
        return Files.walk(Paths.get(folder))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().contains(filterString))
                .sorted();
    }

    protected static StuckThreadsAnalyzerResult analyze(String fileName, List<String> lines) {
        final StuckThreadsAnalyzerResult stuckThreadsAnalyzerResult = new StuckThreadsAnalyzerResult(fileName);

        StuckThreadsAnalyzerResult.StuckThread currentStuckThread = new StuckThreadsAnalyzerResult.StuckThread();
        boolean inStuckThread = false;
        for (String line : lines) {
            if (!inStuckThread) {
                if (line.contains(STUCK_THREAD_MARKER)) {
                    inStuckThread = true;
                }
            } else {
                if (line.startsWith(" ")) {
                    currentStuckThread.addLineToStacktrace(line);
                } else {
                    inStuckThread = false;
                    stuckThreadsAnalyzerResult.addStuckThread(currentStuckThread);
                    currentStuckThread = new StuckThreadsAnalyzerResult.StuckThread();
                }
            }
        }

        return stuckThreadsAnalyzerResult;
    }

    protected static long countLinesWithString(List<String> lines, String searchString) {
        return lines.stream()
                .filter(line -> line.contains(searchString))
                .count();
    }
}
