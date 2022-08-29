package de.tilmanschweitzer.tstlp.core.handler;

public interface StuckThreadHandler<T> {

    void startLogFile(String filename);

    void startStuckThread(String line);

    void lineInStuckThread(String line);

    void endStuckThread();

    T getResult();
}
