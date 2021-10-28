package de.dhbw.parprog;

import java.util.ArrayList;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class ThreadPool {

    public static int doCalculation() {
        var pool = Executors.newFixedThreadPool(5);
        var futures = new ArrayList<Future<Integer>>(10);

        for (int i = 0; i < 10; i++) {
            futures.add(pool.submit(() -> {
                Thread.sleep(1000);
                return 42;
            }));
        }

        var result = futures.stream()
                .mapToInt(future -> {
                    try {
                        return future.get();
                    } catch (CancellationException | InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        return 0;
                    }
                })
                .sum();

        pool.shutdown();
        return result;
    }

    public static void main(String[] args) {
        System.out.println("Calculation started");
        var result = doCalculation();
        System.out.println("Result: " + result);
    }


    private ThreadPool() {}
}
