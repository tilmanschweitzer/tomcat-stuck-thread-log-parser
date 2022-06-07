package de.tilmanschweitzer.tstlp.parser;

import de.tilmanschweitzer.tstlp.handler.LogFileParserResult;
import de.tilmanschweitzer.tstlp.handler.StuckThreadHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractTomcatLogFolderParser implements TomcatLogFolderParser {

    public static final String STUCK_THREAD_MARKER = "org.apache.catalina.valves.StuckThreadDetectionValve.notifyStuckThreadDetected";

    private final List<StuckThreadHandler> stuckThreadHandlers = new ArrayList<>();

    protected static Stream<Path> matchingFilesInFolder(String folder, String filterString) throws IOException {
        return Files.walk(Paths.get(folder))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().contains(filterString))
                .sorted();
    }

    protected static long countLinesWithString(List<String> lines, String searchString) {
        return lines.stream()
                .filter(line -> line.contains(searchString))
                .count();
    }

    public List<LogFileParserResult> parseFile(String fileName, List<String> lines) {
        startLogFile(fileName);

        boolean inStuckThread = false;

        for (String line : lines) {
            if (line.contains(STUCK_THREAD_MARKER)) {
                if (inStuckThread) {
                    endStuckThread();
                }

                startStuckThread(line);
                inStuckThread = true;
            } else if (inStuckThread) {
                if (line.startsWith(" ") || line.startsWith("\t")) {
                    lineInStuckThread(line);
                } else {
                    inStuckThread = false;
                    endStuckThread();
                }
            }
        }

        return endLogFile();
    }

    private void startLogFile(String filename) {
        stuckThreadHandlers.forEach(stuckThreadHandler -> stuckThreadHandler.startLogFile(filename));
    }

    public void addStuckThreadHandler(StuckThreadHandler stuckThreadHandler) {
        stuckThreadHandlers.add(stuckThreadHandler);
    }

    private void startStuckThread(String line) {
        stuckThreadHandlers.forEach(stuckThreadHandler -> stuckThreadHandler.startStuckThread(line));
    }

    private void lineInStuckThread(String line) {
        stuckThreadHandlers.forEach(stuckThreadHandler -> stuckThreadHandler.lineInStuckThread(line));
    }

    private void endStuckThread() {
        stuckThreadHandlers.forEach(StuckThreadHandler::endStuckThread);
    }

    private List<LogFileParserResult> endLogFile() {
        return stuckThreadHandlers.stream().map(stuckThreadHandlers -> stuckThreadHandlers.getResult()).collect(Collectors.toUnmodifiableList());
    }
}
