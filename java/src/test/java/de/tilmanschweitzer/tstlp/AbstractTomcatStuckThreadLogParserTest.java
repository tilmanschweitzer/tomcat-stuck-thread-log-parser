package de.tilmanschweitzer.tstlp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.tilmanschweitzer.tstlp.AbstractTomcatStuckThreadLogParser.STUCK_THREAD_MARKER;
import static java.lang.ClassLoader.getSystemResource;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractTomcatStuckThreadLogParserTest {

    public static final List<String> stuckThreadExampleWithOneStuckThread = linesFromTomcatLogExamplesFile("example-with-one-stuck-thread.log");

    @Test
    @DisplayName("countLinesWithString find one stuck thread in a realistic log example")
    void countLinesWithString_countsOneStuckThread() {
        assertEquals(1, AbstractTomcatStuckThreadLogParser.countLinesWithString(stuckThreadExampleWithOneStuckThread, STUCK_THREAD_MARKER));
    }

    @Test
    @DisplayName("countLinesWithString counts the search string only once per line")
    void countLinesWithString_countsStringOnlyOncePerLine() {
        final List<String> lines = Arrays.asList(
                "test test test test",
                "     test   ",
                "        ",
                "test",
                "",
                "test",
                "..."
        );

        assertEquals(4, AbstractTomcatStuckThreadLogParser.countLinesWithString(lines, "test"));
    }

    @Test
    @DisplayName("countLinesWithString counts noting for an empty list")
    void countLinesWithString_countsZeroForEmptyList() {
        final List<String> lines = Collections.emptyList();
        assertEquals(0, AbstractTomcatStuckThreadLogParser.countLinesWithString(lines, "test"));
    }

    @Test
    @DisplayName("analyze parses the stack trace of one stuck thread")
    void analyze_parsesOneStackTrace() {
        final String filename = "tomcat-logs/catalina.2022-06-05";
        final StuckThreadsAnalyzerResult result = AbstractTomcatStuckThreadLogParser.analyze(filename, stuckThreadExampleWithOneStuckThread);

        assertEquals(filename, result.getFilename());
        assertEquals(1, result.getStuckThreadsCount());

        final StuckThreadsAnalyzerResult.StuckThread firstStuckThread = result.getStuckThreads().get(0);

        final List<String> expectedStackTrace = Arrays.asList(
                "java.base@11.0.9.1/java.net.SocketInputStream.socketRead0(Native Method)",
                "java.base@11.0.9.1/java.net.SocketInputStream.socketRead(SocketInputStream.java:115)",
                "java.base@11.0.9.1/java.net.SocketInputStream.read(SocketInputStream.java:168)",
                "java.base@11.0.9.1/java.net.SocketInputStream.read(SocketInputStream.java:140)"
        );

        assertEquals(expectedStackTrace, firstStuckThread.getStackTrace());
    }

    static List<String> linesFromTomcatLogExamplesFile(final String filename) {
        try {
            final Path exampleFilePath = Path.of(getSystemResource("tomcat-log-examples/" + filename).getPath());
            return Files.readAllLines(exampleFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
