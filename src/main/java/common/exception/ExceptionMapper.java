package common.exception;

import server.http.ContentType;
import server.http.Response;
import server.http.Status;

import java.util.HashMap;
import java.util.Map;

public class ExceptionMapper {

    private final Map<Class<? extends Throwable>, Status> map = new HashMap<>();

    public ExceptionMapper register(Class<? extends Throwable> type, Status status) {
        map.put(type, status);
        return this;
    }

    public Response toResponse(Throwable ex) {
        Status status = map.getOrDefault(ex.getClass(), Status.INTERNAL_SERVER_ERROR);

        Response r = new Response();
        r.setStatus(status);
        r.setContentType(ContentType.PLAIN_TEXT);
        r.setBody(ex.getMessage() != null ? ex.getMessage() : status.getMessage());
        return r;
    }
}