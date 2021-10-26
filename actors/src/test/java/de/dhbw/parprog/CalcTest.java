package de.dhbw.parprog;

import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.testkit.typed.javadsl.TestProbe;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CalcTest {
    private static final ActorTestKit testKit = ActorTestKit.create();

    @AfterAll
    public static void teardown() {
        testKit.shutdownTestKit();
    }

    @Test
    public void calculationReturnsCorrectResult() {
        assertThat(ActorCalculation.doCalculation()).isEqualTo(2310);
    }

    @Test
    public void actorReturnsCorrectResult() {
        var calc = testKit.spawn(CalcActor.create(), "calc");
        TestProbe<CalcActor.Response> probe = testKit.createTestProbe();
        calc.tell(new CalcActor.Request(1, probe.ref()));
        var response = probe.receiveMessage();
        testKit.stop(calc);
        assertThat(response.getOutput()).isEqualTo(42);
    }
}
