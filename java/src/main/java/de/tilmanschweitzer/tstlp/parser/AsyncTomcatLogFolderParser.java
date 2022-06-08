package de.tilmanschweitzer.tstlp.parser;

import de.tilmanschweitzer.tstlp.handler.LogFileParserResult;
import de.tilmanschweitzer.tstlp.handler.StuckThreadHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AsyncTomcatLogFolderParser extends AbstractTomcatLogFolderParser implements TomcatLogFolderParser {

    public AsyncTomcatLogFolderParser(Supplier<StuckThreadHandler> stuckThreadHandlerSuppliers) {
        super(stuckThreadHandlerSuppliers);
    }

    @Override
    public void parseFolder(String folder, String fileFilter) throws IOException {
        final int threads = Runtime.getRuntime().availableProcessors() - 1;
        final ExecutorService executorService = Executors.newFixedThreadPool(threads);

        try (Stream<Path> paths = matchingFilesInFolder(folder, fileFilter)) {
            final List<Future<LogFileParserResult>> collect = paths.map((filename) -> executorService.submit(() -> {
                try {
                    return parseFile(filename.toString(), Files.readAllLines(filename));
                } catch (IOException e) {
                    throw new RuntimeException("Error counting stuck threads", e);
                }
            })).collect(Collectors.toList());

            for (Future<LogFileParserResult> resultFuture : collect) {
                System.out.println(resultFuture.get().getPrintableResult());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
}
