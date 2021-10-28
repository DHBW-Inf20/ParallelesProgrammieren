package de.dhbw.parprog;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AskPattern;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Routers;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.IntStream;

public final class ActorCalculation {

    public static int doCalculation() {
        var system = ActorSystem.create(
                Routers.pool(
                        5,
                        Behaviors.supervise(CalculationBehavior.create())
                                .onFailure(SupervisorStrategy.restart())
                ),
                "calc-pool-system"
        );

        int result = IntStream.range(1, 11)
                .mapToObj(input -> AskPattern.<CalculationBehavior.Request, CalculationBehavior.Response>ask(
                        system,
                        responseReceiver -> new CalculationBehavior.Request(input, responseReceiver),
                        Duration.ofSeconds(3),
                        system.scheduler()
                ))
                .map(response -> response.thenApply(CalculationBehavior.Response::getOutput))
                .reduce((reducedResponses, nextResponse) -> reducedResponses.thenCombine(nextResponse, Integer::sum))
                .map(CompletionStage::toCompletableFuture)
                .map(CompletableFuture::join)
                .orElse(0);

        system.terminate();
        system.getWhenTerminated()
                .thenRun(() -> System.out.println("Bye bye!"))
                .toCompletableFuture()
                .join();

        return result;
    }

    public static void main(String[] args) {
        System.out.println("Important calculation - with actors");
        System.out.println("The result is " + ActorCalculation.doCalculation());
    }


    private ActorCalculation() {}
}
