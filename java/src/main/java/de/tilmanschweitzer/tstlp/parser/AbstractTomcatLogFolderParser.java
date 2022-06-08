package de.tilmanschweitzer.tstlp.parser;

import de.tilmanschweitzer.tstlp.handler.LogFileParserResult;
import de.tilmanschweitzer.tstlp.handler.StuckThreadHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class AbstractTomcatLogFolderParser implements TomcatLogFolderParser {

    public static final String STUCK_THREAD_MARKER = "org.apache.catalina.valves.StuckThreadDetectionValve.notifyStuckThreadDetected";

    private final Supplier<StuckThreadHandler> stuckThreadHandlerSuppliers;

    public AbstractTomcatLogFolderParser(Supplier<StuckThreadHandler> stuckThreadHandlerSuppliers) {
        this.stuckThreadHandlerSuppliers = stuckThreadHandlerSuppliers;
    }

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

    public LogFileParserResult parseFile(String fileName, List<String> lines) {
        final StuckThreadHandler stuckThreadHandlers = stuckThreadHandlerSuppliers.get();

        stuckThreadHandlers.startLogFile(fileName);

        boolean inStuckThread = false;

        for (String line : lines) {
            if (line.contains(STUCK_THREAD_MARKER)) {
                if (inStuckThread) {
                    stuckThreadHandlers.endStuckThread();
                }

                stuckThreadHandlers.startStuckThread(line);
                inStuckThread = true;
            } else if (inStuckThread) {
                if (line.startsWith(" ") || line.startsWith("\t")) {
                    stuckThreadHandlers.lineInStuckThread(line);
                } else {
                    inStuckThread = false;
                    stuckThreadHandlers.endStuckThread();
                }
            }
        }

        return stuckThreadHandlers.getResult();
    }

}
