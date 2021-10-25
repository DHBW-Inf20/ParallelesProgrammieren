package de.dhbw.parprog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toUnmodifiableList;

public class MapReduce {

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

    public static CompletableFuture<CalcResult> doAnalysis() {
        var futures = IntStream.range(0, 4)
                .mapToObj(i -> supplyAsync(() -> {
                    var persons = new ArrayList<Person>();
                    Person p;
                    while ((p = PersonArchive.getPerson()) != null) {
                        persons.add(p);
                    }
                    return persons;
                }))
                .collect(toUnmodifiableList());

        var dataFuture = sequence(futures)
                .thenApply(lists -> lists.stream()
                        .flatMap(Collection::stream)
                        .collect(toUnmodifiableList())
                );

        var maleCountFuture = dataFuture
                .thenApply(people -> (int) people.stream()
                        .filter(person -> person.male)
                        .count()
                );

        var maxLenFuture = dataFuture
                .thenApply(people -> people.stream()
                        .mapToInt(person -> person.name.length())
                        .max()
                        .orElse(0)
                );

        var avgAgeFuture = dataFuture
                .thenApply(people -> people.stream()
                        .mapToInt(person -> person.alter)
                        .average()
                        .orElse(0)
                );

        return allOf(maleCountFuture, maxLenFuture, avgAgeFuture)
                .thenApply(v ->
                        new CalcResult(avgAgeFuture.join(), maxLenFuture.join(), maleCountFuture.join())
                );
    }
}
