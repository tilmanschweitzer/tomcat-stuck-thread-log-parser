package de.tilmanschweitzer.tstlp.core.handler.counting;

public class CountingLogFileParserResult {

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
}
