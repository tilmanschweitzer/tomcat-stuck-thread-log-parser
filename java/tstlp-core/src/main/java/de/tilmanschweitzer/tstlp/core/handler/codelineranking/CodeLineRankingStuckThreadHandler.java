package de.tilmanschweitzer.tstlp.core.handler.codelineranking;

import de.tilmanschweitzer.tstlp.core.handler.LogFileParserResult;
import de.tilmanschweitzer.tstlp.core.handler.StuckThreadHandler;

public class CodeLineRankingStuckThreadHandler implements StuckThreadHandler {
    private StuckThread currentStuckThread;
    private CodeLineRankingLogFileParserResult currentResult;

    @Override
    public void startLogFile(String filename) {
        currentResult = new CodeLineRankingLogFileParserResult(filename);
    }

    @Override
    public void startStuckThread(String line) {
        currentStuckThread = StuckThread.fromLine(line);
    }

    @Override
    public void lineInStuckThread(String line) {
        currentStuckThread.addLineToStacktrace(line);
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
