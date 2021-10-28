package de.dhbw.parprog;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static de.dhbw.parprog.CompletableFutures.sequence;
import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toUnmodifiableList;

public final class MapReduce {

    public static CompletableFuture<CalcResult> doAnalysis() {
        var futures = IntStream.range(0, 4)
                .mapToObj(i -> supplyAsync(() -> Stream
                        .iterate(PersonArchive.getPerson(), Objects::nonNull, prev -> PersonArchive.getPerson())
                        .collect(toUnmodifiableList())
                ))
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


    private MapReduce() {}
}
