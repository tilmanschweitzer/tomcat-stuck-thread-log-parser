package de.tilmanschweitzer.tstlp;

import de.tilmanschweitzer.tstlp.result.CodeLineByOccurrence;
import de.tilmanschweitzer.tstlp.result.StuckThread;
import de.tilmanschweitzer.tstlp.result.StuckThreadsAnalyzerResult;
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
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractTomcatStuckThreadLogParserTest {

    public static final List<String> stuckThreadExampleWithOneStuckThread = linesFromTomcatLogExamplesFile("example-with-one-stuck-thread.log");
    public static final List<String> stuckThreadExampleWithMultipleStuckThreads = linesFromTomcatLogExamplesFile("example-with-multiple-stuck-threads.log");

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
        assertEquals(4, result.uniqueCodeLinesCount());

        final StuckThread firstStuckThread = result.getStuckThreads().get(0);

        final List<String> expectedStackTrace = Arrays.asList(
                "java.base@11.0.9.1/java.net.SocketInputStream.socketRead0(Native Method)",
                "java.base@11.0.9.1/java.net.SocketInputStream.socketRead(SocketInputStream.java:115)",
                "java.base@11.0.9.1/java.net.SocketInputStream.read(SocketInputStream.java:168)",
                "java.base@11.0.9.1/java.net.SocketInputStream.read(SocketInputStream.java:140)"
        );

        assertEquals(expectedStackTrace, firstStuckThread.getStackTraceLines());
    }

    @Test
    @DisplayName("analyze parses the stack trace of multiple stuck threads")
    void analyze_parsesMultipleStackTraces() {
        final String filename = "tomcat-logs/catalina.2022-06-06";
        final StuckThreadsAnalyzerResult result = AbstractTomcatStuckThreadLogParser.analyze(filename, stuckThreadExampleWithMultipleStuckThreads);

        assertEquals(10, result.getStuckThreadsCount());

        final List<String> expectedTimestamps = Arrays.asList(
                "26-May-2022 02:16:31.715",
                "26-May-2022 11:24:26.625",
                "26-May-2022 11:25:46.747",
                "26-May-2022 13:21:44.669",
                "26-May-2022 14:35:49.869",
                "26-May-2022 22:01:00.696",
                "26-May-2022 22:58:04.220",
                "26-May-2022 22:58:04.221",
                "26-May-2022 23:23:25.797",
                "26-May-2022 23:23:25.798"
        );

        assertEquals(expectedTimestamps, result.getStuckThreads().stream().map(StuckThread::getTimestamp).collect(toList()));

        final CodeLineByOccurrence codeLineWithMostOccurrences = result.getCodeLineByOccurrence(0).get();
        assertEquals(414, codeLineWithMostOccurrences.getCount());
        assertEquals("org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162)", codeLineWithMostOccurrences.getLine());


        final CodeLineByOccurrence meaningfulCodeLineWithMostOccurrences = result.getMeaningfulCodeLineByOccurrence(0).get();
        assertEquals(9, meaningfulCodeLineWithMostOccurrences.getCount());
        assertEquals("java.base@11.0.14.1/java.net.SocketInputStream.socketRead0(Native Method)", meaningfulCodeLineWithMostOccurrences.getLine());

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
