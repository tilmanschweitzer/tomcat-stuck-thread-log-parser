package de.tilmanschweitzer.tstlp.handler.codelineranking;

public class CodeLine {
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
}
