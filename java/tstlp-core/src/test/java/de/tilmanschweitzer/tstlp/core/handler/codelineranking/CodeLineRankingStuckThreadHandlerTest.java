package de.tilmanschweitzer.tstlp.core.handler.codelineranking;

import de.tilmanschweitzer.tstlp.core.mock.DummyTomcatLogFile;
import de.tilmanschweitzer.tstlp.core.mock.DummyTomcatLogParser;
import de.tilmanschweitzer.tstlp.core.parser.TomcatLogParser;
import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static de.tilmanschweitzer.tstlp.core.TestUtils.linesFromTomcatLogExamplesFile;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CodeLineRankingStuckThreadHandlerTest {

    public static final List<String> stuckThreadExampleWithOneStuckThread = linesFromTomcatLogExamplesFile("example-with-one-stuck-thread.log");
    public static final List<String> stuckThreadExampleWithMultipleStuckThreads = linesFromTomcatLogExamplesFile("example-with-multiple-stuck-threads.log");

    private TomcatLogParser abstractTomcatLogFolderParser;
    private CodeLineRankingStuckThreadHandler codeLineRankingStuckThreadHandler;

    @BeforeEach
    void setUp() {
        codeLineRankingStuckThreadHandler = new CodeLineRankingStuckThreadHandler();
        abstractTomcatLogFolderParser = new DummyTomcatLogParser(() -> codeLineRankingStuckThreadHandler, result -> { });
    }

    @Test
    @DisplayName("parseFile parses the stack trace of one stuck thread")
    void parseFile_parsesOneStackTrace() {
        final TomcatLogFile tomcatLogFile = new DummyTomcatLogFile("tomcat-logs/catalina.2022-06-05", stuckThreadExampleWithOneStuckThread);

        abstractTomcatLogFolderParser.parseFile(tomcatLogFile);

        final CodeLineRankingLogFileParserResult result = codeLineRankingStuckThreadHandler.getCodeLineResult();
        assertEquals(tomcatLogFile.getFilename(), result.getFilename());
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
    @DisplayName("parseFile parses the stack trace of multiple stuck threads")
    void parseFile_parsesMultipleStackTraces() {
        final TomcatLogFile tomcatLogFile = new DummyTomcatLogFile("tomcat-logs/catalina.2022-06-06", stuckThreadExampleWithMultipleStuckThreads);

        abstractTomcatLogFolderParser.parseFile(tomcatLogFile);

        final CodeLineRankingLogFileParserResult result = codeLineRankingStuckThreadHandler.getCodeLineResult();

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

        assertEquals(expectedTimestamps.toString(), result.getStuckThreads().stream().map(StuckThread::getTimestamp).collect(toList()).toString());

        final CodeLineByOccurrence codeLineWithMostOccurrences = result.getCodeLineBy(1).get(0);
        assertEquals(414, codeLineWithMostOccurrences.getCount());
        assertEquals("org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162)", codeLineWithMostOccurrences.getLine());


        final CodeLineByOccurrence meaningfulCodeLineWithMostOccurrences = result.getMeaningfulCodeLineByOccurrences(1).get(0);
        assertEquals(9, meaningfulCodeLineWithMostOccurrences.getCount());
        assertEquals("java.base@11.0.14.1/java.net.SocketInputStream.socketRead0(Native Method)", meaningfulCodeLineWithMostOccurrences.getLine());


        final CodeLineByOccurrence highestWeightedCodeLines = result.getCodeLineBy(result::filterAmbiguousAndIgnoredCodeLines, CodeLineByOccurrence::compareToByWeight, 1).get(0);
        assertEquals(9, highestWeightedCodeLines.getCount());
        assertEquals("java.base@11.0.14.1/java.net.SocketInputStream.socketRead0(Native Method)", meaningfulCodeLineWithMostOccurrences.getLine());
    }
}
