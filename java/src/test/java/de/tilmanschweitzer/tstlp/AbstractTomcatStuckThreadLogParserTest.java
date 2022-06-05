package de.tilmanschweitzer.tstlp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.tilmanschweitzer.tstlp.AbstractTomcatStuckThreadLogParser.STUCK_THREAD_MARKER;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractTomcatStuckThreadLogParserTest {

    @Test
    @DisplayName("countLinesWithString find one stuck thread in a realistic log example")
    void countLinesWithString_countsOneStuckThread() {
        final List<String> lines = Arrays.asList(
                "06-Dec-2021 10:04:19.911 WARNING [Catalina-utility-1] org.apache.catalina.valves.StuckThreadDetectionValve.notifyStuckThreadDetected Thread [http-nio-8090-exec-626] (id=[106039]) has been active for [60,495] milliseconds (since [12/6/21 10:03 AM]) to serve the same request for [https://confluence.example.com/display/SPACEKEY/Page+title?src=contextnavpagetreemode] and may be stuck (configured threshold for this StuckThreadDetectionValve is [60] seconds). There is/are [2] thread(s) in total that are monitored by this Valve and may be stuck.",
                "        java.lang.Throwable",
                "                at java.base@11.0.9.1/java.net.SocketInputStream.socketRead0(Native Method)",
                "        at java.base@11.0.9.1/java.net.SocketInputStream.socketRead(SocketInputStream.java:115)",
                "        at java.base@11.0.9.1/java.net.SocketInputStream.read(SocketInputStream.java:168)",
                "        at java.base@11.0.9.1/java.net.SocketInputStream.read(SocketInputStream.java:140)",
                "06-Dec-2021 10:04:28.110 WARNING [http-nio-8090-exec-447] com.sun.jersey.spi.container.servlet.WebComponent.filterFormParameters A servlet request, to the URI https://confluene.example.com/rest/table-filter/1.0/service/export-hash?pageId=753352737&id=1629366523170_-2025538085&atl_token=6e3c4beb7a440072d3373ba6dd1906de10194fe8&_=1638781449132, contains form parameters in the request body but the request body has been consumed by the servlet or a servlet filter accessing the request parameters. Only resource methods using @FormParam will work as expected. Resource methods consuming the request body by other means will not work as expected.",
                "..."
        );

        assertEquals(1, AbstractTomcatStuckThreadLogParser.countLinesWithString(lines, STUCK_THREAD_MARKER));
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
}
