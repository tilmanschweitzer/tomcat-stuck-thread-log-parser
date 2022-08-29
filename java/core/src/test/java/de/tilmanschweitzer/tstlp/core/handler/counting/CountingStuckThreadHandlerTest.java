package de.tilmanschweitzer.tstlp.core.handler.counting;

import de.tilmanschweitzer.tstlp.core.mock.DummyTomcatLogFile;
import de.tilmanschweitzer.tstlp.core.mock.DummyTomcatLogParser;
import de.tilmanschweitzer.tstlp.core.parser.TomcatLogParser;
import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.tilmanschweitzer.tstlp.core.TestUtils.linesFromTomcatLogExamplesFile;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CountingStuckThreadHandlerTest {
    public static final List<String> stuckThreadExampleWithOneStuckThread = linesFromTomcatLogExamplesFile("example-with-one-stuck-thread.log");
    public static final List<String> stuckThreadExampleWithMultipleStuckThreads = linesFromTomcatLogExamplesFile("example-with-multiple-stuck-threads.log");

    private TomcatLogParser abstractTomcatLogFolderParser;
    private CountingStuckThreadHandler countingStuckThreadHandler;

    @BeforeEach
    void setUp() {
        countingStuckThreadHandler = new CountingStuckThreadHandler();
        abstractTomcatLogFolderParser = new DummyTomcatLogParser(() -> countingStuckThreadHandler, result -> { } );
    }

    @Test
    @DisplayName("analyze parses the stack trace of one stuck thread")
    void analyze_parsesOneStackTrace() {
        final TomcatLogFile tomcatLogFile = new DummyTomcatLogFile("tomcat-logs/catalina.2022-06-05", stuckThreadExampleWithOneStuckThread);

        abstractTomcatLogFolderParser.parseFile(tomcatLogFile);

        final CountingLogFileParserResult result = countingStuckThreadHandler.getResult();
        assertEquals(tomcatLogFile.getFilename(), result.getFilename());
        assertEquals(1, result.getStuckThreadsCount());
    }

    @Test
    @DisplayName("analyze parses the stack trace of multiple stuck threads")
    void analyze_parsesMultipleStackTraces() {
        final TomcatLogFile tomcatLogFile = new DummyTomcatLogFile("tomcat-logs/catalina.2022-06-06", stuckThreadExampleWithMultipleStuckThreads);

        abstractTomcatLogFolderParser.parseFile(tomcatLogFile);

        final CountingLogFileParserResult result = countingStuckThreadHandler.getResult();

        assertEquals(10, result.getStuckThreadsCount());
    }
}
