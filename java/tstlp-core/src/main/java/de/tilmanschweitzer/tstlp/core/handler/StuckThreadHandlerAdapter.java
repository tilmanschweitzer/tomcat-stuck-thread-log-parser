package de.tilmanschweitzer.tstlp.core.handler;

public class StuckThreadHandlerAdapter implements StuckThreadHandler {
    @Override
    public void startLogFile(String filename) {

    }

    @Override
    public void startStuckThread(String line) {

    }

    @Override
    public void lineInStuckThread(String line) {

    }

    @Override
    public void endStuckThread() {

    }

    @Override
    public LogFileParserResult getResult() {
        return () -> "StuckThreadHandlerAdapter does not produce printable results";
    }
}
