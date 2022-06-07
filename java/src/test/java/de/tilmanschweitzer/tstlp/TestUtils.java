package de.tilmanschweitzer.tstlp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.lang.ClassLoader.getSystemResource;

public class TestUtils {

    public static List<String> linesFromTomcatLogExamplesFile(final String filename) {
        try {
            final Path exampleFilePath = Path.of(getSystemResource("tomcat-log-examples/" + filename).getPath());
            return Files.readAllLines(exampleFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
