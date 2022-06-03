package de.tilmanschweitzer.tstlp;

public class StuckThreadsCounterResult {
    private final String filename;
    private final long stuckThreads;

    public StuckThreadsCounterResult(String filename, long stuckThreads) {
        this.filename = filename;
        this.stuckThreads = stuckThreads;
    }

    @Override
    public String toString() {
        return filename + ":" + stuckThreads;
    }

}
