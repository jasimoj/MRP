package common.echo;

import common.Application;
import server.http.ContentType;
import server.http.Request;
import server.http.Response;
import server.http.Status;

public class EchoApplication implements Application {

    @Override
    public Response handle(Request request) {
        Response response = new Response();

        response.setStatus(Status.OK);
        response.setContentType(ContentType.PLAIN_TEXT);
        response.setBody(
                "Hallo test"
//                "%s %s".formatted(
//                        request.getMethod(),
//                        request.getPath()
//                )
        );

        return response;
    }
}
