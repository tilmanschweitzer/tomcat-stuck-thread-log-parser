package de.tilmanschweitzer.tstlp;

import de.tilmanschweitzer.tstlp.result.CodeLine;
import de.tilmanschweitzer.tstlp.result.StuckThread;
import de.tilmanschweitzer.tstlp.result.StuckThreadsAnalyzerResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

        StuckThread currentStuckThread = new StuckThread("");
        boolean inStuckThread = false;


        for (String line : lines) {
            if (line.contains(STUCK_THREAD_MARKER)) {
                final String timestamp = timestampFromStuckThreadsFirstLine(line);

                final StuckThread newStuckThread = new StuckThread(timestamp);

                if (inStuckThread) {
                    stuckThreadsAnalyzerResult.addStuckThread(currentStuckThread);
                }

                currentStuckThread = newStuckThread;
                inStuckThread = true;
            } else if (inStuckThread){
                if (line.startsWith(" ") || line.startsWith("\t")) {
                    final Optional<CodeLine> codeLineOptional = currentStuckThread.addLineToStacktrace(line);
                    codeLineOptional.ifPresent(stuckThreadsAnalyzerResult::addCodeLine);
                } else {
                    inStuckThread = false;
                    stuckThreadsAnalyzerResult.addStuckThread(currentStuckThread);
                }
            }
        }

        return stuckThreadsAnalyzerResult;
    }

    protected static String timestampFromStuckThreadsFirstLine(String line) {
        final String[] splittedLine = line.split(" ");
        return splittedLine[0] + " " + splittedLine[1];
    }

    protected static long countLinesWithString(List<String> lines, String searchString) {
        return lines.stream()
                .filter(line -> line.contains(searchString))
                .count();
    }
}
