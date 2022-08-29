package de.tilmanschweitzer.tstlp.core.handler.codelineranking;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CodeLineRankingLogFileParserResult {

    final Map<String, CodeLineByOccurrence> codeLineByOccurrencesMap = new HashMap<>();
    private final String filename;
    private final List<StuckThread> stuckThreads = new ArrayList<>();

    public CodeLineRankingLogFileParserResult(String filename) {
        this.filename = filename;
    }

    public long getStuckThreadsCount() {
        return stuckThreads.size();
    }

    public String getFilename() {
        return filename;
    }

    public void addStuckThread(final StuckThread stuckThread) {
        stuckThreads.add(stuckThread);
        stuckThread.getStackTrace().forEach(this::addCodeLine);
    }

    public List<StuckThread> getStuckThreads() {
        return Collections.unmodifiableList(stuckThreads);
    }

    public int uniqueCodeLinesCount() {
        return codeLineByOccurrencesMap.keySet().size();
    }

    public void addCodeLine(StuckThread.CodeLine codeLine) {
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

    public List<CodeLineByOccurrence> getMeaningfulCodeLineByOccurrences(int limit) {
        return getCodeLineBy(this::filterAmbiguousAndIgnoredCodeLines, CodeLineByOccurrence::compareTo, limit);
    }

    public boolean filterAmbiguousAndIgnoredCodeLines(CodeLineByOccurrence line) {
        // ignore lines that occur more often than all stuck threads
        if (line.getCount() > getStuckThreadsCount()) {
            return false;
        }
        return !CodeLineByOccurrence.ignoreCodeLine(line.getLine());
    }

    public List<CodeLineByOccurrence> getCodeLineBy(final int limit) {
        return getCodeLineBy(codeLineByOccurrence -> true, CodeLineByOccurrence::compareTo, limit);
    }


    public List<CodeLineByOccurrence> getCodeLineBy(
            final Predicate<CodeLineByOccurrence> predicate,
            final Comparator<? super CodeLineByOccurrence> comparator,
            final int limit
    ) {
        return codeLineByOccurrencesMap.values()
                .stream()
                .filter(predicate)
                .sorted(comparator)
                .limit(limit)
                .collect(Collectors.toUnmodifiableList());
    }

}
