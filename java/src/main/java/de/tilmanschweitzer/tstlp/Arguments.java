package de.tilmanschweitzer.tstlp;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toUnmodifiableList;

public class Arguments {

    private static final String RUN_ASYNC_FLAG = "--async";
    private static final List<String> ALL_FLAGS = Stream.of(RUN_ASYNC_FLAG).collect(toUnmodifiableList());

    private final boolean runAsync;
    private final String folder;
    private final String fileFilter;

    public Arguments(boolean runAsync, String folder, String fileFilter) {
        this.runAsync = runAsync;
        this.folder = folder;
        this.fileFilter = fileFilter;
    }

    public static Arguments parseArguments(String[] args) {
        final List<String> rawArguments = stream(args).collect(toUnmodifiableList());

        final boolean runAsync = rawArguments.contains(RUN_ASYNC_FLAG);

        final List<String> remainingArguments = rawArguments.stream().filter(not(ALL_FLAGS::contains)).collect(toUnmodifiableList());

        if (remainingArguments.size() < 1) {
            throw new IllegalArgumentException("Missing argument folder");
        }

        final String folder = remainingArguments.get(0);
        final String fileFilter = remainingArguments.size() > 1 ? rawArguments.get(1) : "";

        return new Arguments(runAsync, folder, fileFilter);
    }

    public boolean isRunAsync() {
        return runAsync;
    }

    public String getFolder() {
        return folder;
    }

    public String getFileFilter() {
        return fileFilter;
    }
}
