package de.tilmanschweitzer.tstlp.core.handler.codelineranking;

import java.util.*;

public class CodeLineByOccurrence implements Comparable<CodeLineByOccurrence> {

    private static double WEIGHT_FACTOR_FOR_COUNT = 10000.0;

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
    private final List<StuckThread.CodeLine> codeLines = new ArrayList<>();

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

    public List<StuckThread.CodeLine> getCodeLines() {
        return Collections.unmodifiableList(codeLines);
    }

    public void addCodeLine(StuckThread.CodeLine codeLine) {
        if (!codeLine.getLine().equals(line)) {
            throw new IllegalArgumentException("Code line must match the line in the CodeLineByOccurrence object");
        }
        codeLines.add(codeLine);
    }

    @Override
    public String toString() {
        return getCount() + "x " + line;
    }

    public double getWeight() {
        return getCount() * WEIGHT_FACTOR_FOR_COUNT / getAverageLineNumber();
    }

    public int getAverageLineNumber() {
        return getCodeLines().stream().map(StuckThread.CodeLine::getLineNumberInStuckThread).reduce(Integer::sum).orElseGet(() -> 0) / getCodeLines().size();
    }

    @Override
    public int compareTo(CodeLineByOccurrence o) {
        // Order from often to rare occurrences
        return o.getCount() - getCount();
    }

    public int compareToByWeight(CodeLineByOccurrence o) {
        return (int) (o.getWeight() - getWeight());
    }
}
