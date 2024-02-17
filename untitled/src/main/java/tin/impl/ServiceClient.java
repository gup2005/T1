package tin.impl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;
import tin.snip.Client;
import tin.snip.Response;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public class ServiceClient implements Client {

    private final Sinks.Many<Response> dispatch = newEmitterSink();

    @Override
    public Response getApplicationStatus1(String id) {
        call(id);
        return dispatch.asFlux().blockFirst();
    }

    @Override
    public Response getApplicationStatus2(String id) {
        call(id);
        return dispatch.asFlux().blockFirst();
    }

    private static <T> Sinks.Many<T> newEmitterSink() {
        return Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
    }

    public void call(String id) {
        switch (ThreadLocalRandom.current().nextInt(0, 3)) {
            case 0 -> dispatch.tryEmitNext(new Response.Success("1", id));

            case 1 -> {
                Flux
                        .interval(Duration.ofMillis(20000L))
                        .take(1)
                        .subscribe(__ -> {
                            dispatch.tryEmitNext(new Response.RetryAfter(Duration.ofMillis(1000L)));
                        });
            }
            case 2 -> {
                dispatch.tryEmitNext(new Response.Failure(new RuntimeException(id)));
            }
        }
    }

    public Flux<Response> asFlux() {
        return dispatch.asFlux();
    }


}
