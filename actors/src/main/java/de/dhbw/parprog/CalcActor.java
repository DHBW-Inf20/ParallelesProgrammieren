package de.dhbw.parprog;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class CalcActor extends AbstractBehavior<CalcActor.Request> {

    public static final class Request {
        private final int input;
        private final ActorRef<Response> responseReceiver;

        public Request(int input, ActorRef<Response> responseReceiver) {
            this.input = input;
            this.responseReceiver = responseReceiver;
        }
    }

    public static final class Response {
        private final int output;

        private Response(int output) {
            this.output = output;
        }

        public int getOutput() {
            return output;
        }
    }


    public static Behavior<Request> create() {
        return Behaviors.setup(CalcActor::new);
    }

    private CalcActor(ActorContext<Request> context) {
        super(context);
    }

    @Override
    public Receive<Request> createReceive() {
        return newReceiveBuilder()
                .onMessage(Request.class, this::calculate)
                .build();
    }

    private Behavior<Request> calculate(Request request) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        request.responseReceiver.tell(new Response(request.input * 42));
        return Behaviors.same();
    }
}
