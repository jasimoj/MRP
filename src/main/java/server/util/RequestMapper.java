package server.util;

import com.sun.net.httpserver.HttpExchange;
import server.http.Request;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class RequestMapper {
    public Request fromExchange(HttpExchange exchange) throws IOException {
        Request request = new Request();
        request.setMethod(exchange.getRequestMethod());
        request.setPath(exchange.getRequestURI().getPath());
        byte[] bytes = exchange.getRequestBody().readAllBytes();
        String body = bytes.length == 0 ? "" : new String(bytes, StandardCharsets.UTF_8);
        request.setBody(body);
        return request;
    }
}
