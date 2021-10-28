package de.dhbw.parprog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNullElseGet;
import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.stream.Collectors.toUnmodifiableList;

public final class DirSize {

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
                        .collect(toUnmodifiableList())
                );
    }

    public static CompletableFuture<DirStats> dirStats(File dir) {
        return CompletableFuture
                .supplyAsync(() -> requireNonNullElseGet(dir.listFiles(), () -> new File[0]))
                .thenCompose(dirContent -> {
                    var recursiveResults = new ArrayList<CompletableFuture<DirStats>>();

                    for (var element : dirContent) {
                        if (element.isDirectory() && !element.getName().equals(".")) {
                            recursiveResults.add(dirStats(element));
                        } else if (element.isFile()) {
                            recursiveResults.add(completedFuture(new DirStats(1, element.length())));
                        }
                    }

                    return sequence(recursiveResults)
                            .thenApply(list -> list.stream()
                                    .reduce(DirStats::add)
                                    .orElse(new DirStats())
                            );
                });
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Benötigter Parameter: Startverzeichnis");
            System.exit(1);
        }
        var startDir = new File(args[0]);
        if (!startDir.isDirectory()) {
            System.out.println("Dies ist kein Verzeichnis!");
            System.exit(1);
        }
        var result = dirStats(startDir).join();
        System.out.println(result.fileCount + " Dateien, " + result.totalSize + " Bytes.");
    }


    private DirSize() {}
}
