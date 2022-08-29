package de.tilmanschweitzer.tstlp.core.handler.codelineranking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StuckThread {
    private final String timestamp;
    private final List<CodeLine> stackTrace = new ArrayList<>();

    private StuckThread(String timestamp) {
        this.timestamp = timestamp;
    }

    private static String normalizedLine(String line) {
        final String trimmedLine = line.trim();
        if (trimmedLine.startsWith("at ")) {
            return trimmedLine.replaceFirst("at ", "");
        }
        // ignore all other lines
        return "";
    }

    public static StuckThread fromLine(String line) {
        final String timstamp = timestampFromStuckThreadsFirstLine(line);
        return new StuckThread(timstamp);
    }

    public List<CodeLine> getStackTrace() {
        return Collections.unmodifiableList(stackTrace);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public List<String> getStackTraceLines() {
        return stackTrace.stream().map(CodeLine::getLine).collect(Collectors.toUnmodifiableList());
    }

    public Optional<CodeLine> addLineToStacktrace(String line) {
        final String normalizedLine = normalizedLine(line);
        if (normalizedLine.isEmpty()) {
            return Optional.empty();
        }
        // Start to count code lines at 1 to avoid divide by zero errors
        final CodeLine newCodeLine = new CodeLine(normalizedLine, stackTrace.size() + 1, this);
        stackTrace.add(newCodeLine);
        return Optional.of(newCodeLine);
    }

    static String timestampFromStuckThreadsFirstLine(String line) {
        final String[] splittedLine = line.split(" ");
        return splittedLine[0] + " " + splittedLine[1];
    }

    public static class CodeLine {
        final String line;
        final int lineNumberInStuckThread;
        final StuckThread stuckThread;

        public CodeLine(String line, int lineNumberInStuckThread, StuckThread stuckThread) {
            this.line = line;
            this.lineNumberInStuckThread = lineNumberInStuckThread;
            this.stuckThread = stuckThread;
        }

        public String getLine() {
            return line;
        }

        public int getLineNumberInStuckThread() {
            return lineNumberInStuckThread;
        }
    }
}
