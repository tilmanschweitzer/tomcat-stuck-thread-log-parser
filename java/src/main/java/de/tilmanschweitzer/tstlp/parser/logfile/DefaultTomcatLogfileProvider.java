package de.tilmanschweitzer.tstlp.parser.logfile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DefaultTomcatLogfileProvider implements TomcatLogFileProvider {

    private final String folder;
    private Predicate<TomcatLogFile> fileFilter;

    public DefaultTomcatLogfileProvider(final String folder, final Predicate<TomcatLogFile> fileFilter) {
        this.folder = folder;
        this.fileFilter = fileFilter;
    }

    @Override
    public Stream<TomcatLogFile> provideLogFiles() {
        try {
            return Files.walk(Paths.get(folder))
                    .filter(Files::isRegularFile)
                    .sorted()
                    .map(Path::toString)
                    .map(DefaultTomcatLogFile::new)
                    .map(TomcatLogFile.class::cast)
                    .filter(fileFilter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
