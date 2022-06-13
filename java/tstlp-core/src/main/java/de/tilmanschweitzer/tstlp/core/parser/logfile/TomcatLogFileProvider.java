package de.tilmanschweitzer.tstlp.core.parser.logfile;

import java.util.stream.Stream;

public interface TomcatLogFileProvider {
    Stream<TomcatLogFile> provideLogFiles();
}
