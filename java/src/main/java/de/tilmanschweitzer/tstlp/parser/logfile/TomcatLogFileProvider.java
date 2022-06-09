package de.tilmanschweitzer.tstlp.parser.logfile;

import java.util.stream.Stream;

public interface TomcatLogFileProvider {
    Stream<TomcatLogFile> provideLogFiles();
}
