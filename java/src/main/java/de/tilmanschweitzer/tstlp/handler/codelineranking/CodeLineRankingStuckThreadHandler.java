package de.tilmanschweitzer.tstlp.handler.codelineranking;

import de.tilmanschweitzer.tstlp.handler.LogFileParserResult;
import de.tilmanschweitzer.tstlp.handler.StuckThreadHandler;

import java.util.Optional;

public class CodeLineRankingStuckThreadHandler implements StuckThreadHandler {
    private StuckThread currentStuckThread;
    private CodeLineRankingLogFileParserResult currentResult;

    private static String timestampFromStuckThreadsFirstLine(String line) {
        final String[] splittedLine = line.split(" ");
        return splittedLine[0] + " " + splittedLine[1];
    }

    @Override
    public void startLogFile(String filename) {
        currentResult = new CodeLineRankingLogFileParserResult(filename);
    }

    @Override
    public void startStuckThread(String line) {
        currentStuckThread = new StuckThread(timestampFromStuckThreadsFirstLine(line));
    }

    @Override
    public void lineInStuckThread(String line) {
        final Optional<CodeLine> codeLine = currentStuckThread.addLineToStacktrace(line);
        codeLine.ifPresent(currentResult::addCodeLine);
    }

    @Override
    public void endStuckThread() {
        currentResult.addStuckThread(currentStuckThread);
    }

    @Override
    public LogFileParserResult getResult() {
        return currentResult;
    }

    public CodeLineRankingLogFileParserResult getCodeLineResult() {
        return currentResult;
    }
}
