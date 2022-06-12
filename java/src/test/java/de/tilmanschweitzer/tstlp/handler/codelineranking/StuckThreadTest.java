package de.tilmanschweitzer.tstlp.handler.codelineranking;

import de.tilmanschweitzer.tstlp.mock.DummyTomcatLogParser;
import de.tilmanschweitzer.tstlp.parser.TomcatLogParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static de.tilmanschweitzer.tstlp.TestUtils.linesFromTomcatLogExamplesFile;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StuckThreadTest {


    @Test
    @DisplayName("StuckThread.fromLine creates a stuck thread and extracts the timestamp ")
    void fromLine_parsesTheTimestamp() {
        final StuckThread stuckThread = StuckThread.fromLine("26-May-2022 02:16:31.715 WARNING [Catalina-utility-1] org.apache.catalina.valves.StuckThreadDetectionValve.notifyStuckThreadDetected Thread [http-nio-8090-exec-925] (id=[201733]) has been active for [37,589] milliseconds (since [5/26/22 2:15 AM]) to serve the same request for [https://confluence.example.com/pages/viewpage.action?spaceKey=SPACE&title=PAGE_NAME] and may be stuck (configured threshold for this StuckThreadDetectionValve is [30] seconds). There is/are [1] thread(s) in total that are monitored by this Valve and may be stuck.");

        assertEquals("26-May-2022 02:16:31.715", stuckThread.getTimestamp());
    }

    @Test
    @DisplayName("addLineToStacktrace normalizes the lines")
    void addLineToStacktrace_normalizesTheLines() {
        final StuckThread stuckThread = StuckThread.fromLine("26-May-2022 02:16:31.715 WARNING [Catalina-utility-1] org.apache.catalina.valves.StuckThreadDetectionValve.notifyStuckThreadDetected Thread [http-nio-8090-exec-925] (id=[201733]) has been active for [37,589] milliseconds (since [5/26/22 2:15 AM]) to serve the same request for [https://confluence.example.com/pages/viewpage.action?spaceKey=SPACE&title=PAGE_NAME] and may be stuck (configured threshold for this StuckThreadDetectionValve is [30] seconds). There is/are [1] thread(s) in total that are monitored by this Valve and may be stuck.");

        stuckThread.addLineToStacktrace("        java.lang.Throwable");
        stuckThread.addLineToStacktrace("                at java.base@11.0.14.1/java.io.UnixFileSystem.getBooleanAttributes0(Native Method)");
        stuckThread.addLineToStacktrace("                at java.base@11.0.14.1/java.io.UnixFileSystem.getBooleanAttributes(UnixFileSystem.java:248)");
        stuckThread.addLineToStacktrace("                at java.base@11.0.14.1/java.io.File.isDirectory(File.java:861)");
        stuckThread.addLineToStacktrace("                at com.atlassian.confluence.pages.persistence.dao.filesystem.HierarchicalContentFileSystemHelper.createDirectoryHierarchy(HierarchicalContentFileSystemHelper.java:27)");
        stuckThread.addLineToStacktrace("                at com.atlassian.confluence.plugins.conversion.impl.FileSystemConversionState.getStorageFolder(FileSystemConversionState.java:114)");

        final List<String> expectedCodeLines = Arrays.asList(
                "java.base@11.0.14.1/java.io.UnixFileSystem.getBooleanAttributes0(Native Method)",
                "java.base@11.0.14.1/java.io.UnixFileSystem.getBooleanAttributes(UnixFileSystem.java:248)",
                "java.base@11.0.14.1/java.io.File.isDirectory(File.java:861)",
                "com.atlassian.confluence.pages.persistence.dao.filesystem.HierarchicalContentFileSystemHelper.createDirectoryHierarchy(HierarchicalContentFileSystemHelper.java:27)",
                "com.atlassian.confluence.plugins.conversion.impl.FileSystemConversionState.getStorageFolder(FileSystemConversionState.java:114)"
        );

        assertEquals(expectedCodeLines, stuckThread.getStackTraceLines());
    }

    @Test
    @DisplayName("addLineToStacktrace creates a bidirectional link between code lines")
    void addLineToStacktrace_createBidirectionalLinkBetweenCodeLineAndStuckThread() {
        final StuckThread stuckThread = StuckThread.fromLine("26-May-2022 02:16:31.715 WARNING [Catalina-utility-1] org.apache.catalina.valves.StuckThreadDetectionValve.notifyStuckThreadDetected Thread [http-nio-8090-exec-925] (id=[201733]) has been active for [37,589] milliseconds (since [5/26/22 2:15 AM]) to serve the same request for [https://confluence.example.com/pages/viewpage.action?spaceKey=SPACE&title=PAGE_NAME] and may be stuck (configured threshold for this StuckThreadDetectionValve is [30] seconds). There is/are [1] thread(s) in total that are monitored by this Valve and may be stuck.");
        stuckThread.addLineToStacktrace("        java.lang.Throwable");
        stuckThread.addLineToStacktrace("                at java.base@11.0.14.1/java.io.UnixFileSystem.getBooleanAttributes0(Native Method)");
        stuckThread.addLineToStacktrace("                at java.base@11.0.14.1/java.io.UnixFileSystem.getBooleanAttributes(UnixFileSystem.java:248)");

        StuckThread.CodeLine firstCodeLine = stuckThread.getStackTrace().get(0);
        StuckThread.CodeLine secondCodeLine = stuckThread.getStackTrace().get(1);

        assertEquals(stuckThread, firstCodeLine.stuckThread);
        assertEquals(1, firstCodeLine.getLineNumberInStuckThread());

        assertEquals(stuckThread, secondCodeLine.stuckThread);
        assertEquals(2, secondCodeLine.getLineNumberInStuckThread());
    }

}
