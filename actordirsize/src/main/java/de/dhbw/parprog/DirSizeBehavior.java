package de.dhbw.parprog;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;

import java.io.File;

import static akka.actor.typed.javadsl.Behaviors.*;
import static java.util.Objects.requireNonNullElseGet;

public final class DirSizeBehavior extends AbstractBehavior<DirSizeBehavior.Message> {

    public interface Message {}

    public static final class Request implements Message {
        private final File dir;
        private final ActorRef<? super Response> responseReceiver;

        public Request(File dir, ActorRef<? super Response> responseReceiver) {
            this.dir = dir;
            this.responseReceiver = responseReceiver;
        }
    }

    public static final class Response implements Message {
        public final DirStats dirStats;

        private Response(DirStats dirStats) {
            this.dirStats = dirStats;
        }
    }


    private ActorRef<? super Response> parent = null;
    private int pendingChildResponses = 0;
    private DirStats accumulatedStats = new DirStats();

    public static Behavior<Message> create() {
        return setup(DirSizeBehavior::new);
    }

    private DirSizeBehavior(ActorContext<Message> context) {
        super(context);
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(Request.class, this::processFirstRequest)
                .build();
    }

    private Behavior<Message> processFirstRequest(Request request) {
        if (parent != null) {
            // can not happen, behavior changes after first request
            throw new IllegalStateException("received two requests");
        }
        parent = request.responseReceiver;

        var dirContent = requireNonNullElseGet(request.dir.listFiles(), () -> new File[0]);
        for (var element : dirContent) {
            if (element.isDirectory() && !element.getName().equals(".")) {
                pendingChildResponses++;
                // spawn child with self as responseReceiver
                getContext().spawnAnonymous(DirSizeBehavior.create())
                        .tell(new Request(element, getContext().getSelf()));
            } else if (element.isFile()) {
                addToAccumulatedStats(new DirStats(1, element.length()));
            }
        }
        return respondToParentIfDone()
                ? stopped()
                : newReceiveBuilder().onMessage(Response.class, this::processChildResponse).build();
    }

    private Behavior<Message> processChildResponse(Response response) {
        pendingChildResponses--;
        addToAccumulatedStats(response.dirStats);
        return respondToParentIfDone() ? stopped() : same();
    }

    private void addToAccumulatedStats(DirStats dirStats) {
        accumulatedStats = accumulatedStats.add(dirStats);
    }

    private boolean respondToParentIfDone() {
        if (pendingChildResponses == 0) {
            parent.tell(new Response(accumulatedStats));
            return true;
        } else {
            return false;
        }
    }
}
