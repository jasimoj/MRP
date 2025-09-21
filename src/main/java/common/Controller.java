package common;

import server.http.ContentType;
import server.http.Request;
import server.http.Response;
import server.http.Status;

public abstract class Controller {

    private final String basePath;

    protected Controller(String basePath) {
        this.basePath = basePath;
    }

    public String getBasePath() {
        return basePath;
    }

    // subPath ist z. B. "/", "/42", "/42/edit" – also NUR der Teil hinter basePath.
    public abstract Response handle(Request request, String subPath);

    protected Response ok(String body) {
        return info(Status.OK, body);
    }

    protected Response info(Status status, String body) {
        Response r = new Response();
        r.setStatus(status);
        r.setContentType(ContentType.PLAIN_TEXT);
        r.setBody(body);
        return r;
    }
}