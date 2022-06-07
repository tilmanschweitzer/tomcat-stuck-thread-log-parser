package de.tilmanschweitzer.tstlp.handler.codelineranking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CodeLineByOccurrence implements Comparable<CodeLineByOccurrence> {

    public static final List<String> ignoredMethodNames = Arrays.asList(
            "doFilter",
            "doFilterInternal",
            "StuckThreadDetectionValve.invoke",
            "StandardHostValve.invoke",
            "AuthenticatorBase.invoke",
            "AbstractProcessorLight.process",
            "Thread.run",
            "invoke",
            "AbstractProtocol$ConnectionHandler.process",
            "TransformerChain.transform",
            "WebSudoInterceptor.intercept"
    );

    public static final List<String> ignoredFilterPackages = Arrays.asList(
            "com.atlassian.confluence.web.filter",
            "com.atlassian.confluence.plugin.servlet.filter",
            "com.atlassian.confluence.core.ConfluenceWorkflowInterceptor",
            "com.atlassian.xwork.interceptors",
            "com.atlassian.util.profiling.filters",
            "com.atlassian.gzipfilter",
            "org.apache.coyote",
            "org.apache.catalina",
            "org.apache.tomcat",
            "com.atlassian.confluence.impl.vcache.VCacheRequestContextFilter",
            "com.sun.jersey",
            "org.springframework.aop.framework"
    );
    private final String line;
    private final List<CodeLine> codeLines = new ArrayList<>();

    public CodeLineByOccurrence(String line) {
        this.line = line;
    }

    public static boolean ignoreCodeLine(String line) {
        return ignoredFilterPackages.stream().anyMatch(line::startsWith) || ignoredMethodNames.stream().map(methodName -> "." + methodName + "(").anyMatch(line::contains);
    }

    public String getLine() {
        return line;
    }

    public int getCount() {
        return codeLines.size();
    }

    public List<CodeLine> getCodeLines() {
        return Collections.unmodifiableList(codeLines);
    }

    public void addCodeLine(CodeLine codeLine) {
        if (!codeLine.getLine().equals(line)) {
            throw new IllegalArgumentException("Code line must match the line in the CodeLineByOccurrence object");
        }
        codeLines.add(codeLine);
    }

    @Override
    public String toString() {
        return getCount() + "x " + line;
    }

    @Override
    public int compareTo(CodeLineByOccurrence o) {
        // Order from often to rare occurrences
        return o.getCount() - getCount();
    }
}
