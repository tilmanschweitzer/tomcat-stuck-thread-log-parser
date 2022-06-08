package de.tilmanschweitzer.tstlp.parser;

import de.tilmanschweitzer.tstlp.handler.StuckThreadHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.tilmanschweitzer.tstlp.TestUtils.linesFromTomcatLogExamplesFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractTomcatLogFolderParserTest {

    public static final List<String> stuckThreadExampleWithOneStuckThread = linesFromTomcatLogExamplesFile("example-with-one-stuck-thread.log");
    public static final List<String> stuckThreadExampleWithMultipleStuckThreads = linesFromTomcatLogExamplesFile("example-with-multiple-stuck-threads.log");

    @Mock
    StuckThreadHandler stuckThreadHandler;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    private TomcatLogFolderParser abstractTomcatLogFolderParser;

    @BeforeEach
    void beforeEach() {
        abstractTomcatLogFolderParser = new DummyTomcatLogFolderParser(() -> stuckThreadHandler);
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

        abstractTomcatLogFolderParser.parseFile(filename, logLines);

        verify(stuckThreadHandler).startLogFile(filename);
        verify(stuckThreadHandler).getResult();
    }


    @Test
    @DisplayName("parseFile detects one isolated stuck thread")
    void parseFile_detectsOneIsolatedStuckThread() {
        final List<String> logLines = Collections.singletonList(AbstractTomcatLogFolderParser.STUCK_THREAD_MARKER);
        final String filename = "test-file.log";

        abstractTomcatLogFolderParser.parseFile(filename, logLines);

        verify(stuckThreadHandler).startLogFile(filename);
        verify(stuckThreadHandler).startStuckThread(AbstractTomcatLogFolderParser.STUCK_THREAD_MARKER);
        verify(stuckThreadHandler).endStuckThread();
        verify(stuckThreadHandler).getResult();
    }

    @Test
    @DisplayName("parseFile detects two isolated stuck threads ")
    void parseFile_detectsTwiIsolatedStuckThreads() {
        final List<String> logLines = Arrays.asList(AbstractTomcatLogFolderParser.STUCK_THREAD_MARKER, AbstractTomcatLogFolderParser.STUCK_THREAD_MARKER);
        final String filename = "test-file.log";

        abstractTomcatLogFolderParser.parseFile(filename, logLines);

        verify(stuckThreadHandler).startLogFile(filename);
        verify(stuckThreadHandler, times(2)).startStuckThread(AbstractTomcatLogFolderParser.STUCK_THREAD_MARKER);
        verify(stuckThreadHandler, times(2)).endStuckThread();
        verify(stuckThreadHandler).getResult();
    }


    @Test
    @DisplayName("parseFile detects one stuck thread in realistic log example including its stack trace")
    void parseFile_detectsOneStuckThreadInRealisticLogExample() {
        final String filename = "test-file.log";

        abstractTomcatLogFolderParser.parseFile(filename, stuckThreadExampleWithOneStuckThread);

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
    void parseFile_detectsMutipleStuckThreadsInRealisticLogExample() {
        final String filename = "test-file.log";

        abstractTomcatLogFolderParser.parseFile(filename, stuckThreadExampleWithMultipleStuckThreads);

        verify(stuckThreadHandler).startLogFile(filename);
        verify(stuckThreadHandler, times(10)).startStuckThread(stringArgumentCaptor.capture());
        verify(stuckThreadHandler, times(5705)).lineInStuckThread(any());
        verify(stuckThreadHandler, times(10)).endStuckThread();
        verify(stuckThreadHandler).getResult();

        final List<String> expectedCodeLines = Arrays.asList(
                "26-May-2022 02:16:31.715 WARNING [Catalina-utility-1] org.apache.catalina.valves.StuckThreadDetectionValve.notifyStuckThreadDetected Thread [http-nio-8090-exec-925] (id=[201733]) has been active for [37,589] milliseconds (since [5/26/22 2:15 AM]) to serve the same request for [https://confluence.example.com/pages/viewpage.action?spaceKey=SPACE&title=TITLE] and may be stuck (configured threshold for this StuckThreadDetectionValve is [30] seconds). There is/are [1] thread(s) in total that are monitored by this Valve and may be stuck.",
                "26-May-2022 11:24:26.625 WARNING [Catalina-utility-1] org.apache.catalina.valves.StuckThreadDetectionValve.notifyStuckThreadDetected Thread [http-nio-8090-exec-1312] (id=[267161]) has been active for [34,236] milliseconds (since [5/26/22 11:23 AM]) to serve the same request for [https://confluence.example.com/display/xxx] thread(s) in total that are monitored by this Valve and may be stuck.",
                "26-May-2022 11:25:46.747 WARNING [Catalina-utility-1] org.apache.catalina.valves.StuckThreadDetectionValve.notifyStuckThreadDetected Thread [http-nio-8090-exec-1331] (id=[278449]) has been active for [32,598] milliseconds (since [5/26/22 11:25 AM]) to serve the same request for [https://confluence.example.com/display/xxx] thread(s) in total that are monitored by this Valve and may be stuck.",
                "26-May-2022 13:21:44.669 WARNING [Catalina-utility-4] org.apache.catalina.valves.StuckThreadDetectionValve.notifyStuckThreadDetected Thread [http-nio-8090-exec-1258] (id=[254522]) has been active for [37,930] milliseconds (since [5/26/22 1:21 PM]) to serve the same request for [https://confluence.example.com/rest/easyevents/2.0/events/listEvents] and may be stuck (configured threshold for this StuckThreadDetectionValve is [30] seconds). There is/are [1] thread(s) in total that are monitored by this Valve and may be stuck.",
                "26-May-2022 14:35:49.869 WARNING [Catalina-utility-3] org.apache.catalina.valves.StuckThreadDetectionValve.notifyStuckThreadDetected Thread [http-nio-8090-exec-1341] (id=[289791]) has been active for [35,715] milliseconds (since [5/26/22 2:35 PM]) to serve the same request for [https://confluence.example.com/rest/easyevents/2.0/events/listEvents] and may be stuck (configured threshold for this StuckThreadDetectionValve is [30] seconds). There is/are [1] thread(s) in total that are monitored by this Valve and may be stuck.",
                "26-May-2022 22:01:00.696 WARNING [Catalina-utility-1] org.apache.catalina.valves.StuckThreadDetectionValve.notifyStuckThreadDetected Thread [http-nio-8090-exec-1112] (id=[250918]) has been active for [34,539] milliseconds (since [5/26/22 10:00 PM]) to serve the same request for [https://confluence.example.com/rest/easyevents/2.0/events/listEvents] and may be stuck (configured threshold for this StuckThreadDetectionValve is [30] seconds). There is/are [1] thread(s) in total that are monitored by this Valve and may be stuck.",
                "26-May-2022 22:58:04.220 WARNING [Catalina-utility-1] org.apache.catalina.valves.StuckThreadDetectionValve.notifyStuckThreadDetected Thread [http-nio-8090-exec-1281] (id=[265500]) has been active for [39,227] milliseconds (since [5/26/22 10:57 PM]) to serve the same request for [https://confluence.example.com/rest/easyevents/2.0/events/listEvents] and may be stuck (configured threshold for this StuckThreadDetectionValve is [30] seconds). There is/are [1] thread(s) in total that are monitored by this Valve and may be stuck.",
                "26-May-2022 22:58:04.221 WARNING [Catalina-utility-1] org.apache.catalina.valves.StuckThreadDetectionValve.notifyStuckThreadDetected Thread [http-nio-8090-exec-1269] (id=[254533]) has been active for [39,234] milliseconds (since [5/26/22 10:57 PM]) to serve the same request for [https://confluence.example.com/rest/easyevents/2.0/events/listEvents] and may be stuck (configured threshold for this StuckThreadDetectionValve is [30] seconds). There is/are [2] thread(s) in total that are monitored by this Valve and may be stuck.",
                "26-May-2022 23:23:25.797 WARNING [Catalina-utility-1] org.apache.catalina.valves.StuckThreadDetectionValve.notifyStuckThreadDetected Thread [http-nio-8090-exec-1279] (id=[265498]) has been active for [32,499] milliseconds (since [5/26/22 11:22 PM]) to serve the same request for [https://confluence.example.com/rest/easyevents/2.0/events/listEvents] and may be stuck (configured threshold for this StuckThreadDetectionValve is [30] seconds). There is/are [1] thread(s) in total that are monitored by this Valve and may be stuck.",
                "26-May-2022 23:23:25.798 WARNING [Catalina-utility-1] org.apache.catalina.valves.StuckThreadDetectionValve.notifyStuckThreadDetected Thread [http-nio-8090-exec-1266] (id=[254530]) has been active for [32,488] milliseconds (since [5/26/22 11:22 PM]) to serve the same request for [https://confluence.example.com/rest/easyevents/2.0/events/listEvents] and may be stuck (configured threshold for this StuckThreadDetectionValve is [30] seconds). There is/are [2] thread(s) in total that are monitored by this Valve and may be stuck."
        );

        assertEquals(stringArgumentCaptor.getAllValues(), expectedCodeLines);
    }
}
