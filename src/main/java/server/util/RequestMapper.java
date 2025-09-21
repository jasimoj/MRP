package server.util;

import com.sun.net.httpserver.HttpExchange;
import server.http.Request;


public class RequestMapper {
    public Request fromExchange(HttpExchange exchange){
        Request request = new Request();
        request.setMethod(exchange.getRequestMethod());
        request.setPath(exchange.getRequestURI().getPath());
        return request;
    }
}
