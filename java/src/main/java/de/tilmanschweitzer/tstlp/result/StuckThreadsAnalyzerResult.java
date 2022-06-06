package de.tilmanschweitzer.tstlp.result;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static de.tilmanschweitzer.tstlp.result.CodeLineByOccurrence.ignoreCodeLine;

public class StuckThreadsAnalyzerResult {

    private final String filename;
    private final List<StuckThread> stuckThreads = new ArrayList<>();
    final Map<String, CodeLineByOccurrence> codeLineByOccurrencesMap = new HashMap<>();

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
        final Optional<CodeLineByOccurrence> mostOftenCodeLine = getMeaningfulCodeLineByOccurrence(0);
        final String codeLineAppend = mostOftenCodeLine.map((codeLine) -> " (" + codeLine + ")").orElse("");
        return filename + ":" + getStuckThreadsCount() + codeLineAppend;
    }

    public int uniqueCodeLinesCount() {
        return codeLineByOccurrencesMap.keySet().size();
    }

    public void addCodeLine(CodeLine codeLine) {
        final String line = codeLine.getLine();
        final CodeLineByOccurrence codeLineByOccurrence;
        if (codeLineByOccurrencesMap.containsKey(line)) {
            codeLineByOccurrence = codeLineByOccurrencesMap.get(line);
        } else {
            codeLineByOccurrence = new CodeLineByOccurrence(line);
            codeLineByOccurrencesMap.put(line, codeLineByOccurrence);
        }
        codeLineByOccurrence.addCodeLine(codeLine);
    }

    public Optional<CodeLineByOccurrence> getMeaningfulCodeLineByOccurrence(int index) {
        return getCodeLineByOccurrence(index, (line) -> {
            // ignore lines that occur more often than all stuck threads
            if (line.getCount() > getStuckThreadsCount()) {
                return false;
            }
            return !ignoreCodeLine(line.getLine());
        });
    }

    public Optional<CodeLineByOccurrence> getCodeLineByOccurrence(int index) {
        return getCodeLineByOccurrence(index, (line) -> true);
    }

    public Optional<CodeLineByOccurrence> getCodeLineByOccurrence(int index,  Predicate<CodeLineByOccurrence> predicate) {
        final List<CodeLineByOccurrence> collect = codeLineByOccurrencesMap.values()
                .stream()
                .filter(predicate)
                .sorted()
                .collect(Collectors.toUnmodifiableList());

        if (index >= collect.size()) {
            return Optional.empty();
        }
        return Optional.of(collect.get(index));
    }
}
