package de.tilmanschweitzer.tstlp.core.parser;

import de.tilmanschweitzer.tstlp.core.handler.LogFileParserResult;
import de.tilmanschweitzer.tstlp.core.handler.StuckThreadHandler;
import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFile;
import de.tilmanschweitzer.tstlp.core.parser.logfile.TomcatLogFileProvider;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AsyncTomcatLogParser extends AbstractTomcatLogParser implements TomcatLogParser {

    public AsyncTomcatLogParser(Supplier<StuckThreadHandler> stuckThreadHandlerSuppliers) {
        super(stuckThreadHandlerSuppliers);
    }

    @Override
    public void parse(TomcatLogFileProvider provider) {
        final int threads = Runtime.getRuntime().availableProcessors() - 1;
        final ExecutorService executorService = Executors.newFixedThreadPool(threads);

        try (Stream<TomcatLogFile> tomcatLogfiles = provider.provideLogFiles()) {
            final List<Future<LogFileParserResult>> collect = tomcatLogfiles
                    .map((tomcatLogFile) -> {
                        return executorService.submit(() -> {
                            return parseFile(tomcatLogFile);
                        });
                    }).collect(Collectors.toList());

            for (Future<LogFileParserResult> resultFuture : collect) {
                System.out.println(resultFuture.get().getPrintableResult());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }
}
