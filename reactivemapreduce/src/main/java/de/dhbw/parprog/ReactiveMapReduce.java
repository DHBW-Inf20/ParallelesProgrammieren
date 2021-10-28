package de.dhbw.parprog;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;

public final class ReactiveMapReduce {

    @SuppressWarnings({"BusyWait", "ResultOfMethodCallIgnored"})
    public static void main(String[] args) throws InterruptedException {
        var publisher = new SubmissionPublisher<Person>();

        // Pipeline initialisieren
        publisher.subscribe(new Subscriber<>() {
            private int count = 0;
            private int maleCount = 0;
            private int maxLen = 0;
            private long summedAges;

            @Override
            public void onSubscribe(Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Person person) {
                count++;
                summedAges += person.alter;
                if (person.male) maleCount++;
                if (person.name.length() > maxLen) maxLen = person.name.length();

                System.out.println("People processed: " + count +
                        "\nMale count: " + maleCount +
                        "\nAverage age: " + ((double) summedAges / count) +
                        "\nMax name length: " + maxLen +
                        "\n-----------------------");
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("Complete!");
            }
        });

        // Alle Personen abrufen
        Person p;
        while ((p = PersonArchive.getPerson()) != null) {
            publisher.submit(p);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Stream schließen
        publisher.close();

        // Auf Pool warten
        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(1, TimeUnit.SECONDS);
    }


    private ReactiveMapReduce() {}
}
