package de.tilmanschweitzer.tstlp.handler.counting;

import de.tilmanschweitzer.tstlp.handler.LogFileParserResult;
import de.tilmanschweitzer.tstlp.handler.StuckThreadHandler;
import de.tilmanschweitzer.tstlp.handler.StuckThreadHandlerAdapter;

public class CountingStuckThreadHandler extends StuckThreadHandlerAdapter implements StuckThreadHandler {

    private String filename;
    private int stuckThreadsCounter;

    @Override
    public void startLogFile(String filename) {
        this.filename = filename;
        this.stuckThreadsCounter = 0;
    }

    @Override
    public void startStuckThread(String line) {
        stuckThreadsCounter++;
    }

    @Override
    public LogFileParserResult getResult() {
        return getCountingResult();
    }

    public CountingLogFileParserResult getCountingResult() {
        return new CountingLogFileParserResult(filename, stuckThreadsCounter);
    }
}
