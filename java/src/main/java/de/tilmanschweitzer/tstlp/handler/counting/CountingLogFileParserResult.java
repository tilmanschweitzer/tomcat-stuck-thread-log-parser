package de.tilmanschweitzer.tstlp.handler.counting;

import de.tilmanschweitzer.tstlp.handler.LogFileParserResult;

public class CountingLogFileParserResult implements LogFileParserResult {

    private final String filename;
    private final int stuckThreadsCount;

    public CountingLogFileParserResult(String filename, int stuckThreadsCount) {
        this.filename = filename;
        this.stuckThreadsCount = stuckThreadsCount;
    }

    public String getFilename() {
        return filename;
    }

    public int getStuckThreadsCount() {
        return stuckThreadsCount;
    }

    @Override
    public String getPrintableResult() {
        return filename + ":" + stuckThreadsCount;
    }
}
