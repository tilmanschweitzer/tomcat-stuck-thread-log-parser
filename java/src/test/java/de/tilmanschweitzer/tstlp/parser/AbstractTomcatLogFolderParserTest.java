package de.tilmanschweitzer.tstlp.parser;

import de.tilmanschweitzer.tstlp.handler.codelineranking.CodeLineRankingStuckThreadHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.tilmanschweitzer.tstlp.TestUtils.linesFromTomcatLogExamplesFile;
import static de.tilmanschweitzer.tstlp.parser.AbstractTomcatLogFolderParser.STUCK_THREAD_MARKER;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractTomcatLogFolderParserTest {

    public static final List<String> stuckThreadExampleWithOneStuckThread = linesFromTomcatLogExamplesFile("example-with-one-stuck-thread.log");

    private TomcatLogFolderParser abstractTomcatLogFolderParser;

    @BeforeEach
    void setUp() {
        abstractTomcatLogFolderParser = new DummyTomcatLogFolderParser(CodeLineRankingStuckThreadHandler::new);
    }

    @Test
    @DisplayName("countLinesWithString find one stuck thread in a realistic log example")
    void countLinesWithString_countsOneStuckThread() {
        assertEquals(1, AbstractTomcatLogFolderParser.countLinesWithString(stuckThreadExampleWithOneStuckThread, STUCK_THREAD_MARKER));
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

        assertEquals(4, AbstractTomcatLogFolderParser.countLinesWithString(lines, "test"));
    }

    @Test
    @DisplayName("countLinesWithString counts noting for an empty list")
    void countLinesWithString_countsZeroForEmptyList() {
        final List<String> lines = Collections.emptyList();
        assertEquals(0, AbstractTomcatLogFolderParser.countLinesWithString(lines, "test"));
    }


}
