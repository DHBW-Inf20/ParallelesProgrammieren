package de.dhbw.parprog;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;

import java.io.File;
import java.time.Duration;

public final class ActorDirSize {

    public static DirStats dirStats(File dir) {
        var system = ActorSystem.create(DirSizeActor.create(), "dir-size-system");
        var result = AskPattern.<DirSizeActor.Message, DirSizeActor.Response>ask(
                        system,
                        responseReceiver -> new DirSizeActor.Request(dir, responseReceiver),
                        Duration.ofSeconds(10),
                        system.scheduler()
                )
                .toCompletableFuture()
                .join()
                .dirStats;

        system.terminate();
        system.getWhenTerminated().toCompletableFuture().join();

        return result;
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
        var result = dirStats(startDir);
        System.out.println(result.fileCount + " Dateien, " + result.totalSize + " Bytes.");
    }


    private ActorDirSize() {}
}
