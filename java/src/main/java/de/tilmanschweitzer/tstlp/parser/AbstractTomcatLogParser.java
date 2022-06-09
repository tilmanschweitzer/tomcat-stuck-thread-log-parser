package de.tilmanschweitzer.tstlp.parser;

import de.tilmanschweitzer.tstlp.handler.LogFileParserResult;
import de.tilmanschweitzer.tstlp.handler.StuckThreadHandler;
import de.tilmanschweitzer.tstlp.parser.logfile.TomcatLogFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class AbstractTomcatLogParser implements TomcatLogParser {

    public static final String STUCK_THREAD_MARKER = "org.apache.catalina.valves.StuckThreadDetectionValve.notifyStuckThreadDetected";

    private final Supplier<StuckThreadHandler> stuckThreadHandlerSuppliers;

    public AbstractTomcatLogParser(Supplier<StuckThreadHandler> stuckThreadHandlerSuppliers) {
        this.stuckThreadHandlerSuppliers = stuckThreadHandlerSuppliers;
    }

    protected static Stream<Path> matchingFilesInFolder(String folder, String filterString) throws IOException {
        return Files.walk(Paths.get(folder))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().contains(filterString))
                .sorted();
    }

    public LogFileParserResult parseFile(TomcatLogFile tomcatLogFile) {
        final StuckThreadHandler stuckThreadHandlers = stuckThreadHandlerSuppliers.get();

        stuckThreadHandlers.startLogFile(tomcatLogFile.getFilename());

        boolean inStuckThread = false;

        for (final String line : tomcatLogFile.getLines()) {
            if (line.contains(STUCK_THREAD_MARKER)) {
                // End stuck threads that directly follows a previous stuck thread
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

        // Edge-case where two following stuck threads have no stack trace
        if (inStuckThread) {
            stuckThreadHandlers.endStuckThread();
        }

        return stuckThreadHandlers.getResult();
    }

}
