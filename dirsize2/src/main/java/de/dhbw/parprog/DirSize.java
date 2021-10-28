package de.dhbw.parprog;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static de.dhbw.parprog.CompletableFutures.sequence;
import static java.util.Objects.requireNonNullElseGet;
import static java.util.concurrent.CompletableFuture.completedFuture;

public final class DirSize {

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
