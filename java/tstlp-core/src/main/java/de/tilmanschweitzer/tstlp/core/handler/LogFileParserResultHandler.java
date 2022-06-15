package de.tilmanschweitzer.tstlp.core.handler;

public interface LogFileParserResultHandler<T> {
    void handleResult(T result);
}
