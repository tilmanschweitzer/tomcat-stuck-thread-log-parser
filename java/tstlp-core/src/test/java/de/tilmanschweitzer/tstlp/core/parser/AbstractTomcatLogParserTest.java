package de.tilmanschweitzer.tstlp.core.parser;

import de.tilmanschweitzer.tstlp.core.handler.StuckThreadHandler;
import de.tilmanschweitzer.tstlp.core.mock.DummyTomcatLogFile;
import de.tilmanschweitzer.tstlp.core.mock.DummyTomcatLogParser;
import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFile;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractTomcatLogParserTest {

    @Mock
    StuckThreadHandler stuckThreadHandler;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    private TomcatLogParser abstractTomcatLogFolderParser;

    @BeforeEach
    void beforeEach() {
        abstractTomcatLogFolderParser = new DummyTomcatLogParser(() -> stuckThreadHandler);
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(stuckThreadHandler);
    }

    @Test
    @DisplayName("parseFile parses the filename for an empty log file")
    void parseFile_parsesTheFilenameForAnEmptyLogFile() {
        final List<String> logLines = Collections.singletonList("");
        final String filename = "test-file.log";
        final TomcatLogFile tomcatLogFile = new DummyTomcatLogFile(filename, logLines);

        abstractTomcatLogFolderParser.parseFile(tomcatLogFile);

        verify(stuckThreadHandler).startLogFile(filename);
        verify(stuckThreadHandler).getResult();
    }


    @Test
    @DisplayName("parseFile detects one isolated stuck thread")
    void parseFile_detectsOneIsolatedStuckThread() {
        final List<String> logLines = Collections.singletonList(AbstractTomcatLogParser.STUCK_THREAD_MARKER);
        final String filename = "test-file.log";
        final TomcatLogFile tomcatLogFile = new DummyTomcatLogFile(filename, logLines);

        abstractTomcatLogFolderParser.parseFile(tomcatLogFile);

        verify(stuckThreadHandler).startLogFile(filename);
        verify(stuckThreadHandler).startStuckThread(AbstractTomcatLogParser.STUCK_THREAD_MARKER);
        verify(stuckThreadHandler).endStuckThread();
        verify(stuckThreadHandler).getResult();
    }

    @Test
    @DisplayName("parseFile detects two isolated stuck threads ")
    void parseFile_detectsTwiIsolatedStuckThreads() {
        final List<String> logLines = Arrays.asList(AbstractTomcatLogParser.STUCK_THREAD_MARKER, AbstractTomcatLogParser.STUCK_THREAD_MARKER);
        final String filename = "test-file.log";
        final TomcatLogFile tomcatLogFile = new DummyTomcatLogFile(filename, logLines);

        abstractTomcatLogFolderParser.parseFile(tomcatLogFile);

        verify(stuckThreadHandler).startLogFile(filename);
        verify(stuckThreadHandler, times(2)).startStuckThread(AbstractTomcatLogParser.STUCK_THREAD_MARKER);
        verify(stuckThreadHandler, times(2)).endStuckThread();
        verify(stuckThreadHandler).getResult();
    }


    @Test
    @DisplayName("parseFile detects one stuck thread in realistic log example including its stack trace")
    void parseFile_detectsOneStuckThreadInRealisticLogExample() {
        final String filename = "test-file.log";
        final TomcatLogFile tomcatLogFile = new DummyTomcatLogFile(filename, SharedTestData.stuckThreadExampleWithOneStuckThread);

        abstractTomcatLogFolderParser.parseFile(tomcatLogFile);

        verify(stuckThreadHandler).startLogFile(filename);
        verify(stuckThreadHandler, times(1)).startStuckThread("06-Dec-2021 10:04:19.911 WARNING [Catalina-utility-1] org.apache.catalina.valves.StuckThreadDetectionValve.notifyStuckThreadDetected Thread [http-nio-8090-exec-626] (id=[106039]) has been active for [60,495] milliseconds (since [12/6/21 10:03 AM]) to serve the same request for [https://confluence.example.com/display/SPACEKEY/Page+title?src=contextnavpagetreemode] and may be stuck (configured threshold for this StuckThreadDetectionValve is [60] seconds). There is/are [2] thread(s) in total that are monitored by this Valve and may be stuck.");
        verify(stuckThreadHandler, times(5)).lineInStuckThread(stringArgumentCaptor.capture());
        verify(stuckThreadHandler, times(1)).endStuckThread();
        verify(stuckThreadHandler).getResult();

        final List<String> expectedCodeLines = Arrays.asList(
                "        java.lang.Throwable",
                "            at java.base@11.0.9.1/java.net.SocketInputStream.socketRead0(Native Method)",
                "            at java.base@11.0.9.1/java.net.SocketInputStream.socketRead(SocketInputStream.java:115)",
                "            at java.base@11.0.9.1/java.net.SocketInputStream.read(SocketInputStream.java:168)",
                "            at java.base@11.0.9.1/java.net.SocketInputStream.read(SocketInputStream.java:140)"
        );

        assertEquals(stringArgumentCaptor.getAllValues(), expectedCodeLines);
    }

    @Test
    @DisplayName("parseFile detects multiple stuck thread in realistic log example")
    void parseFile_detectsMultipleStuckThreadsInRealisticLogExample() {
        final String filename = "test-file.log";
        final TomcatLogFile tomcatLogFile = new DummyTomcatLogFile(filename, SharedTestData.stuckThreadExampleWithMultipleStuckThreads);

        abstractTomcatLogFolderParser.parseFile(tomcatLogFile);

        verify(stuckThreadHandler).startLogFile(filename);
        verify(stuckThreadHandler, times(10)).startStuckThread(stringArgumentCaptor.capture());
        verify(stuckThreadHandler, times(5705)).lineInStuckThread(any());
        verify(stuckThreadHandler, times(10)).endStuckThread();
        verify(stuckThreadHandler).getResult();

        Assertions.assertEquals(stringArgumentCaptor.getAllValues(), SharedTestData.expectedCodeLinesInExampleWithMultipleStuckThreads);
    }
}
