package common;

import server.http.Request;
import server.http.Response;

public interface Application {
    Response handle(Request request);
}
