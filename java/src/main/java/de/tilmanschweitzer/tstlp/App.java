package de.tilmanschweitzer.tstlp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class App {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        System.out.println(Arrays.stream(args).collect(Collectors.toList()));

        if (args.length < 1) {
            throw new IllegalArgumentException("Missing argument folder");
        }

        final String folder = args[0];
        final String filterString = args.length > 1 ? args[1] : "";

        runAsync(folder, filterString);
    }

    public static void runAsync(String folder, String filterString) throws IOException, ExecutionException, InterruptedException {
        final int threads = Runtime.getRuntime().availableProcessors() - 1;
        final ExecutorService executorService = Executors.newFixedThreadPool(threads);

        try (Stream<Path> paths = matchingFilesInFolder(folder, filterString)) {
            final List<Future<Result>> collect = paths.map((filename) -> executorService.submit(() -> {
                try {
                    final long countStuckThreads = countLinesWithString(Files.readAllLines(filename), "notifyStuckThreadDetected");
                    return new Result(filename.toString(), countStuckThreads);
                } catch (IOException e) {
                    throw new RuntimeException("Error counting stuck threads", e);
                }
            })).collect(Collectors.toList());

            for (Future<Result> resultFuture : collect) {
                printResult(resultFuture.get());
            }

            executorService.shutdown();
        }
    }

    private static void printResult(Result result) {
        System.out.println(result.filename + ":" + result.stuckThreads);
    }

    private static class Result {
        private final String filename;
        private final long stuckThreads;

        private Result(String filename, long stuckThreads) {
            this.filename = filename;
            this.stuckThreads = stuckThreads;
        }
    }

    private static Stream<Path> matchingFilesInFolder(String folder, String filterString) throws IOException {
        return Files.walk(Paths.get(folder))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().contains(filterString))
                .sorted();
    }

    private static long countLinesWithString(List<String> lines, String searchString) {
        return lines.stream()
                .filter(line -> line.contains(searchString))
                .count();
    }

}
