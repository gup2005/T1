import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import tin.impl.ServiceClient;
import transer.*;

import java.time.Duration;


public class HandleImpl implements Handler {
    Client client;
    public HandleImpl() {
       client = new ClientImpl();
       performOperation();

    }



    @Override
    public Duration timeout() {
        return Duration.ofMillis(1000L);
    }

    @Override
    public void performOperation() {

        Event event = client.readData();

        Flux
                .fromStream(event.recipients().stream())
                .flatMap(address -> Mono.just(calc(address, event.payload())))
                .map(tuple -> {
                    System.out.println("---" + tuple);

                    if (tuple.getT2() == Result.REJECTED) {
                        System.out.println("Rejected "+ tuple.getT1());
                        Flux.interval(timeout()).take(1)
                                .subscribe(__ -> calc(tuple.getT1(), event.payload()));
                    }
                    if (tuple.getT2() == Result.ACCEPTED) {
                        System.out.println("accepted "+ tuple.getT1());
                        return tuple.getT1();
                    }
                    return null;
                }).subscribe();


    }

    Tuple2<Address, Result> calc(Address address, Payload payload) {

        Result result = client.sendData(address, payload);

        switch (result) {
            case ACCEPTED -> {
                return Tuples.of(address, Result.ACCEPTED);
            }
            default -> {
                return Tuples.of(address, Result.REJECTED);
            }
        }
    }
}
