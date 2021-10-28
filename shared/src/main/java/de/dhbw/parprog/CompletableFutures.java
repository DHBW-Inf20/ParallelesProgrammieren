package de.dhbw.parprog;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.stream.Collectors.toUnmodifiableList;

@SuppressWarnings("SpellCheckingInspection")
public final class CompletableFutures {

    /**
     * Hilsfmethode: Wandelt eine Liste von Futures (desselben Typs) in ein einzelnes
     * Future einer Liste der Ergebnisse
     *
     * @param futures Liste der Futures
     * @param <T>     Gemeinsamer Typ
     * @return Future der Ergebnisliste
     */
    public static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
        return allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(toUnmodifiableList())
                );
    }


    private CompletableFutures() {}
}
