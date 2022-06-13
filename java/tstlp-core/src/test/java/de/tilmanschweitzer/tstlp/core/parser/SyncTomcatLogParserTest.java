package de.tilmanschweitzer.tstlp.core.parser;

import de.tilmanschweitzer.tstlp.core.handler.LogFileParserResult;
import de.tilmanschweitzer.tstlp.core.handler.StuckThreadHandler;
import de.tilmanschweitzer.tstlp.core.mock.DummyTomcatLogFile;
import de.tilmanschweitzer.tstlp.core.handler.counting.CountingLogFileParserResult;
import de.tilmanschweitzer.tstlp.core.mock.DummyTomcatLogFileProvider;
import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFile;
import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFileProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static de.tilmanschweitzer.tstlp.core.parser.SharedTestData.expectedCodeLinesInExampleWithMultipleStuckThreads;
import static de.tilmanschweitzer.tstlp.core.parser.SharedTestData.stuckThreadExampleWithMultipleStuckThreads;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncTomcatLogParserTest {

    @Mock
    StuckThreadHandler stuckThreadHandler;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    private SyncTomcatLogParser syncTomcatLogParser;

    @BeforeEach
    void beforeEach() {
        syncTomcatLogParser = new SyncTomcatLogParser(() -> stuckThreadHandler);

    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(stuckThreadHandler);
    }

    @Test
    @DisplayName("parse from provider parses one given filename")
    void parse_parsesOneProvidedFilename() {
        final TomcatLogFile tomcatLogFile = new DummyTomcatLogFile("test-file.log", singletonList(""));
        final TomcatLogFileProvider provider = new DummyTomcatLogFileProvider(tomcatLogFile);
        when(stuckThreadHandler.getResult()).thenReturn(new CountingLogFileParserResult(tomcatLogFile.getFilename(), 0));

        syncTomcatLogParser.parse(provider);

        verify(stuckThreadHandler).startLogFile(tomcatLogFile.getFilename());
        verify(stuckThreadHandler).getResult();
    }

    @Test
    @DisplayName("parse from provider parses multiple given filenames")
    void parse_parsesMultipleProvidedFilenames() {
        final List<String> testFilenames = Arrays.asList("test-file-1.log", "test-file-2.log", "test-file-3.log", "test-file-4.log");
        final List<TomcatLogFile> tomcatLogFiles = testFilenames.stream().map(filename -> new DummyTomcatLogFile(filename, singletonList(""))).collect(toList());
        final TomcatLogFileProvider provider = new DummyTomcatLogFileProvider(tomcatLogFiles);
        when(stuckThreadHandler.getResult()).thenReturn(Mockito.mock(LogFileParserResult.class));

        syncTomcatLogParser.parse(provider);

        verify(stuckThreadHandler, times(4)).startLogFile(stringArgumentCaptor.capture());
        verify(stuckThreadHandler, times(4)).getResult();
        assertEquals(testFilenames, stringArgumentCaptor.getAllValues());
    }


    @Test
    @DisplayName("parse detects multiple stuck thread in realistic log example")
    void parse_detectsMultipleStuckThreadsInRealisticLogExample() {
        final TomcatLogFile tomcatLogFile = new DummyTomcatLogFile("test-file.log", stuckThreadExampleWithMultipleStuckThreads);
        final TomcatLogFileProvider provider = new DummyTomcatLogFileProvider(tomcatLogFile);
        when(stuckThreadHandler.getResult()).thenReturn(new CountingLogFileParserResult(tomcatLogFile.getFilename(), 0));

        syncTomcatLogParser.parse(provider);

        verify(stuckThreadHandler).startLogFile(tomcatLogFile.getFilename());
        verify(stuckThreadHandler, times(10)).startStuckThread(stringArgumentCaptor.capture());
        verify(stuckThreadHandler, times(5705)).lineInStuckThread(any());
        verify(stuckThreadHandler, times(10)).endStuckThread();
        verify(stuckThreadHandler).getResult();

        assertEquals(stringArgumentCaptor.getAllValues(), expectedCodeLinesInExampleWithMultipleStuckThreads);
    }
}
