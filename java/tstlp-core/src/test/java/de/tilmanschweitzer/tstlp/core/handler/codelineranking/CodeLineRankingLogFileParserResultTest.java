package de.tilmanschweitzer.tstlp.core.handler.codelineranking;

import de.tilmanschweitzer.tstlp.core.TestUtils;
import de.tilmanschweitzer.tstlp.core.mock.DummyTomcatLogParser;
import de.tilmanschweitzer.tstlp.core.parser.TomcatLogParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CodeLineRankingLogFileParserResultTest {

    public static final List<String> stuckThreadExampleWithOneStuckThread = TestUtils.linesFromTomcatLogExamplesFile("example-with-one-stuck-thread.log");
    public static final List<String> stuckThreadExampleWithMultipleStuckThreads = TestUtils.linesFromTomcatLogExamplesFile("example-with-multiple-stuck-threads.log");

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



    }

}
