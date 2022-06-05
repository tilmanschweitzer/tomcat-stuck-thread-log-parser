package de.tilmanschweitzer.tstlp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StuckThreadsAnalyzerResult {
    private final String filename;
    private final List<StuckThread> stuckThreads = new ArrayList<>();

    public StuckThreadsAnalyzerResult(String filename) {
        this.filename = filename;
    }

    public long getStuckThreadsCount() {
        return stuckThreads.size();
    }

    public String getFilename() {
        return filename;
    }

    public void addStuckThread(StuckThread stuckThread) {
        stuckThreads.add(stuckThread);
    }

    public List<StuckThread> getStuckThreads() {
        return Collections.unmodifiableList(stuckThreads);
    }

    @Override
    public String toString() {
        return filename + ":" + getStuckThreadsCount();
    }

    public static class StuckThread {
        private final List<String> stackTrace = new ArrayList<>();

        public List<String> getStackTrace() {
            return Collections.unmodifiableList(stackTrace);
        }

        public void addLineToStacktrace(String line) {
            final String trimmedLine = line.trim();
            if (trimmedLine.startsWith("at ")) {
                stackTrace.add(trimmedLine.replaceFirst("at ", ""));
            }
        }
    }
}
