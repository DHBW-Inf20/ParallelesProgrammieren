package de.dhbw.parprog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class DirSize {

    /**
     * Hilsfmethode: Wandelt eine Liste von Futures (desselben Typs) in ein einzelnes
     * Future einer Liste der Ergebnisse
     *
     * @param futures Liste der Futures
     * @param <T>     Gemeinsamer Typ
     * @return Future der Ergebnisliste
     */
    private static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
        return allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.<T>toList())
                );
    }

    public static CompletableFuture<DirStats> dirStats(File dir) {
        return CompletableFuture
                .supplyAsync(() -> dir.isDirectory() ? dir.listFiles() : new File[0])
                .thenCompose(dirContent -> {
                    var recursiveResults = new ArrayList<CompletableFuture<DirStats>>();

                    for (var content : dirContent) {
                        if (content.isDirectory() && !content.getName().equals(".")) {
                            recursiveResults.add(dirStats(content));
                        } else if (content.isFile()) {
                            recursiveResults.add(completedFuture(new DirStats(1, content.length())));
                        }
                    }

                    return sequence(recursiveResults).thenApply(list -> list.stream()
                            .reduce((accumulated, next) -> new DirStats(
                                    accumulated.fileCount + next.fileCount,
                                    accumulated.totalSize + next.totalSize
                            ))
                            .orElse(new DirStats())
                    );
                });
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        if (args.length < 1) {
            System.out.println("Benötigter Parameter: Startverzeichnis");
            System.exit(1);
        }
        var startDir = new File(args[0]);
        if (!startDir.isDirectory()) {
            System.out.println("Dies ist kein Verzeichnis!");
            System.exit(1);
        }
        var result = dirStats(startDir).get();
        System.out.println(result.fileCount + " Dateien, " + result.totalSize + " Bytes.");
    }
}
