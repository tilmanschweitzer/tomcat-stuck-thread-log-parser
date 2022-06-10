package de.tilmanschweitzer.tstlp.mock;

import de.tilmanschweitzer.tstlp.parser.logfile.TomcatLogFile;
import de.tilmanschweitzer.tstlp.parser.logfile.TomcatLogFileProvider;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class DummyTomcatLogFileProvider implements TomcatLogFileProvider {

    private final List<TomcatLogFile> tomcatLogFiles;

    public DummyTomcatLogFileProvider(final List<TomcatLogFile> tomcatLogFiles) {
        this.tomcatLogFiles = tomcatLogFiles;
    }

    public DummyTomcatLogFileProvider(final TomcatLogFile... tomcatLogFiles) {
        this(Arrays.asList(tomcatLogFiles));
    }

    @Override
    public Stream<TomcatLogFile> provideLogFiles() {
        return tomcatLogFiles.stream();
    }
}
