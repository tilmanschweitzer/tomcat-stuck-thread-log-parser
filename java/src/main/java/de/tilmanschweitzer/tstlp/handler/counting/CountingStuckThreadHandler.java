package de.tilmanschweitzer.tstlp.handler.counting;

import de.tilmanschweitzer.tstlp.handler.LogFileParserResult;
import de.tilmanschweitzer.tstlp.handler.StuckThreadHandler;

public class CountingStuckThreadHandler implements StuckThreadHandler {

    private String filename;
    private int stuckThreadsCounter;

    @Override
    public void startLogFile(String filename) {
        this.filename = filename;
        this.stuckThreadsCounter = 0;
    }

    @Override
    public void startStuckThread(String line) {
        this.stuckThreadsCounter++;
    }

    @Override
    public void lineInStuckThread(String line) {
        // Ignore
    }

    @Override
    public void endStuckThread() {
        // Ignore
    }

    @Override
    public LogFileParserResult getResult() {
        return new CountingLogFileParserResult(filename, stuckThreadsCounter);
    }
}
