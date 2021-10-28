package de.dhbw.parprog;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;

import java.io.File;

import static akka.actor.typed.javadsl.Behaviors.*;
import static java.util.Objects.requireNonNullElseGet;

public final class DirSizeActor extends AbstractBehavior<DirSizeActor.Message> {

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
        return setup(DirSizeActor::new);
    }

    private DirSizeActor(ActorContext<Message> context) {
        super(context);
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(Request.class, this::processFirstRequest)
                .onMessage(Response.class, this::processChildResponse)
                .build();
    }

    private Behavior<Message> processFirstRequest(Request request) {
        if (parent != null) {
            return unhandled(); // only process first request
        }
        parent = request.responseReceiver;

        var dirContent = requireNonNullElseGet(request.dir.listFiles(), () -> new File[0]);
        for (var element : dirContent) {
            if (element.isDirectory() && !element.getName().equals(".")) {
                pendingChildResponses++;
                // spawn child with self as responseReceiver
                getContext().spawnAnonymous(DirSizeActor.create())
                        .tell(new Request(element, getContext().getSelf()));
            } else if (element.isFile()) {
                addToAccumulatedStats(new DirStats(1, element.length()));
            }
        }
        return respondToParentIfDone();
    }

    private Behavior<Message> processChildResponse(Response response) {
        pendingChildResponses--;
        addToAccumulatedStats(response.dirStats);
        return respondToParentIfDone();
    }

    private void addToAccumulatedStats(DirStats dirStats) {
        accumulatedStats = accumulatedStats.add(dirStats);
    }

    private Behavior<Message> respondToParentIfDone() {
        if (pendingChildResponses == 0) {
            parent.tell(new Response(accumulatedStats));
            return stopped();
        } else {
            return same();
        }
    }
}
