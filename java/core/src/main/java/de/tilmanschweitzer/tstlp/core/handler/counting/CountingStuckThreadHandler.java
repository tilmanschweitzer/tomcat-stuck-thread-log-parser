package de.tilmanschweitzer.tstlp.core.handler.counting;

import de.tilmanschweitzer.tstlp.core.handler.StuckThreadHandler;
import de.tilmanschweitzer.tstlp.core.handler.StuckThreadHandlerAdapter;

public class CountingStuckThreadHandler extends StuckThreadHandlerAdapter<CountingLogFileParserResult> implements StuckThreadHandler<CountingLogFileParserResult> {

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
    public CountingLogFileParserResult getResult() {
        return new CountingLogFileParserResult(filename, stuckThreadsCounter);
    }
}
