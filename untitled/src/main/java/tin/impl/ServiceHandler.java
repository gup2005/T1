package tin.impl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tin.snip.ApplicationStatusResponse;
import tin.snip.Handler;
import tin.snip.Response;

import java.time.Duration;
import java.util.function.Function;

public class ServiceHandler implements Handler {

    ServiceClient2 client = new ServiceClient2();

    @Override
    public ApplicationStatusResponse performOperation(String id) {

        Mono<Response> r1 = Mono.just(client.getApplicationStatus1(id));
        Mono<Response> r2 = Mono.just(client.getApplicationStatus2(id));

        Flux
                .merge(r1,r2)
                .timeout(Duration.ofMillis(15000L))
                        .map(response -> {
                            switch (response) {
                                case Response.Failure ignored -> new ApplicationStatusResponse.Failure(Duration.ofMillis(0), 1);
                                case Response.RetryAfter retryAfter -> new ApplicationStatusResponse.Failure(Duration.ofMillis(0), 1);
                                case Response.Success success -> new ApplicationStatusResponse.Success(response.applicationId);
                                default -> throw new IllegalStateException("Unexpected value: " + response);
                            }
                        });



    }



    Function<Response, ApplicationStatusResponse> sw = new Function<Response, ApplicationStatusResponse>() {
        @Override
        public ApplicationStatusResponse apply(Response response) {
            switch (response) {
                case Response.Failure ignored -> new ApplicationStatusResponse.Failure(Duration.ofMillis(0), 1);
                case Response.RetryAfter retryAfter -> new ApplicationStatusResponse.Failure(Duration.ofMillis(0), 1);
              //  case Response.Success success -> new ApplicationStatusResponse.Success(response.applicationId);
                default -> throw new IllegalStateException("Unexpected value: " + response);
            }
            return null;
        }
    };

   /*     return webClient.get()
                .uri("/employees")
            .retrieve()
            .bodyToFlux(Employee.class)
            .transform(m -> retryWithBackoffTimeout(m, Duration.ofMillis(50), Duration.ofMillis(500), 10));
}

    public <T> Flux<T> retryWithBackoffTimeout(Flux<T> flux, Duration timeout, Duration maxTimeout, int factor) {
        return mono.timeout(timeout)
                .onErrorResume(e -> timeout.multipliedBy(factor).compareTo(maxTimeout) < 1,
                        e -> retryWithBackoffTimeout(mono, timeout.multipliedBy(factor), maxTimeout, factor));
    }*/
}
