package de.tilmanschweitzer.tstlp.handler.codelineranking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StuckThread {
    private final String timestamp;
    private final List<CodeLine> stackTrace = new ArrayList<>();

    public StuckThread(String timestamp) {
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
        final CodeLine newCodeLine = new CodeLine(normalizedLine, stackTrace.size() + 1, this);
        stackTrace.add(newCodeLine);
        return Optional.of(newCodeLine);
    }
}
