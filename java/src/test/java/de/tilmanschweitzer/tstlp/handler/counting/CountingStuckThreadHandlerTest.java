package de.tilmanschweitzer.tstlp.handler.counting;

import de.tilmanschweitzer.tstlp.parser.DummyTomcatLogFolderParser;
import de.tilmanschweitzer.tstlp.parser.TomcatLogFolderParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.tilmanschweitzer.tstlp.TestUtils.linesFromTomcatLogExamplesFile;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CountingStuckThreadHandlerTest {
    public static final List<String> stuckThreadExampleWithOneStuckThread = linesFromTomcatLogExamplesFile("example-with-one-stuck-thread.log");
    public static final List<String> stuckThreadExampleWithMultipleStuckThreads = linesFromTomcatLogExamplesFile("example-with-multiple-stuck-threads.log");

    private TomcatLogFolderParser abstractTomcatLogFolderParser;
    private CountingStuckThreadHandler countingStuckThreadHandler;

    @BeforeEach
    void setUp() {
        countingStuckThreadHandler = new CountingStuckThreadHandler();
        abstractTomcatLogFolderParser = new DummyTomcatLogFolderParser(() -> countingStuckThreadHandler);
    }

    @Test
    @DisplayName("analyze parses the stack trace of one stuck thread")
    void analyze_parsesOneStackTrace() {
        final String filename = "tomcat-logs/catalina.2022-06-05";
        abstractTomcatLogFolderParser.parseFile(filename, stuckThreadExampleWithOneStuckThread);

        final CountingLogFileParserResult result = countingStuckThreadHandler.getCountingResult();
        assertEquals(filename, result.getFilename());
        assertEquals(1, result.getStuckThreadsCount());
    }

    @Test
    @DisplayName("analyze parses the stack trace of multiple stuck threads")
    void analyze_parsesMultipleStackTraces() {
        final String filename = "tomcat-logs/catalina.2022-06-06";
        abstractTomcatLogFolderParser.parseFile(filename, stuckThreadExampleWithMultipleStuckThreads);
        final CountingLogFileParserResult result = countingStuckThreadHandler.getCountingResult();

        assertEquals(10, result.getStuckThreadsCount());
    }
}
