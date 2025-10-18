package server.util;

import com.sun.net.httpserver.HttpExchange;
import common.mrp.auth.AuthService;
import common.mrp.auth.Authenticator;
import server.http.Request;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class RequestMapper {
    private final Authenticator authenticator;


    public RequestMapper(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public Request fromExchange(HttpExchange exchange) throws IOException {
        Request request = new Request();
        request.setMethod(exchange.getRequestMethod());
        request.setPath(exchange.getRequestURI().getPath());
        byte[] bytes = exchange.getRequestBody().readAllBytes();
        String body = bytes.length == 0 ? "" : new String(bytes, StandardCharsets.UTF_8);
        request.setBody(body);

        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        authenticator.authenticate(authHeader).ifPresent(p -> {
            request.setAuthUserId(p.getUserId());
            request.setAuthUsername(p.getUsername());
        });
        return request;
    }

    private String extractBearerToken(String auth) {
        if (auth == null) {
            return null;
        }
        String a = auth.toLowerCase();
        if (!a.startsWith("bearer ")) {
            return null;
        }
        String token = auth.substring(7).trim(); // 7 = "Bearer ".length()
        return token.isEmpty() ? null : token;
    }


}
