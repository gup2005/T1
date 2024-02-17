import reactor.core.publisher.Flux;
import transer.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class ClientImpl implements Client {
    @Override
    public Event readData() {
        List<Address> addresses = new ArrayList<>();

        IntStream.range(0, 100).mapToObj(a -> addresses.add(new Address(String.valueOf(a), String.valueOf(a)))).forEach(a->{});

        return new Event(addresses, new Payload("origin", new byte[0]));
    }

    @Override
    public Result sendData(Address dest, Payload payload) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            return Result.REJECTED;
        }
        return Result.ACCEPTED;
    }
}
