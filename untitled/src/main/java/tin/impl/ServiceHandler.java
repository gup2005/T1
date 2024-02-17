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
                .merge(r1, r2)
                //.timeout(Duration.ofMillis(15000L))
                .map(sw)
                .blockLast();
        return null;
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

}
