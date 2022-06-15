package de.tilmanschweitzer.tstlp.core.parser;

import de.tilmanschweitzer.tstlp.core.handler.LogFileParserResultHandler;
import de.tilmanschweitzer.tstlp.core.handler.StuckThreadHandler;
import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFile;

import java.util.function.Supplier;

public abstract class AbstractTomcatLogParser<T> implements TomcatLogParser<T> {

    public static final String STUCK_THREAD_MARKER = "org.apache.catalina.valves.StuckThreadDetectionValve.notifyStuckThreadDetected";

    private final Supplier<StuckThreadHandler<T>> stuckThreadHandlerSuppliers;
    protected final LogFileParserResultHandler<T> resultHandler;

    public AbstractTomcatLogParser(Supplier<StuckThreadHandler<T>> stuckThreadHandlerSuppliers, LogFileParserResultHandler<T> resultHandler) {
        this.stuckThreadHandlerSuppliers = stuckThreadHandlerSuppliers;
        this.resultHandler = resultHandler;
    }

    public T parseFile(TomcatLogFile tomcatLogFile) {
        final StuckThreadHandler<T> stuckThreadHandlers = stuckThreadHandlerSuppliers.get();

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
