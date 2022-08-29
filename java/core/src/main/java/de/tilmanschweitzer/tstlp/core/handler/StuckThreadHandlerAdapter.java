package de.tilmanschweitzer.tstlp.core.handler;

public abstract class StuckThreadHandlerAdapter<T> implements StuckThreadHandler<T> {
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
}
