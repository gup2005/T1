package tin.impl;

import tin.snip.Client;
import tin.snip.Response;

public class ServiceClient2 implements Client {

    @Override
    public Response getApplicationStatus1(String id) {
        System.out.println(id);
       return new Response.Failure(new RuntimeException(id));
    }

    @Override
    public Response getApplicationStatus2(String id) {
        System.out.println("2"+id);
        return new Response.Success(id, id);
    }
}
