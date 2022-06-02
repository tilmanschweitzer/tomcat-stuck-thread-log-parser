package de.tilmanschweitzer.tstlp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {

    public static void main(String[] args) throws IOException {
        System.out.println(Arrays.stream(args).collect(Collectors.toList()));


        if (args.length < 1) {
            throw new IllegalArgumentException("TODO");
        }

        final String folder = args[0];
        final String filterString = args.length > 1 ? args[1] : "";

        runSync(folder, filterString);
    }

    public static void runSync(String folder, String filterString) throws IOException {
        try (Stream<Path> paths = matchingFilesInFolder(folder, filterString)) {
            paths.forEach((filename) -> {
                try {
                    final long countStuckThreads = countLinesWithString(Files.readAllLines(filename), "notifyStuckThreadDetected");
                    System.out.println(filename + ":" + countStuckThreads);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
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
