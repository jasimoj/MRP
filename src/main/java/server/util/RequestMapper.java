package server.util;

import com.sun.net.httpserver.HttpExchange;
import common.mrp.auth.AuthService;
import common.mrp.auth.Authenticator;
import server.http.Request;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RequestMapper {
    private final Authenticator authenticator;


    public RequestMapper(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public Request fromExchange(HttpExchange exchange) throws IOException {
        Request request = new Request();
        request.setMethod(exchange.getRequestMethod());
        request.setPath(exchange.getRequestURI().getPath());
        String rawQuery = exchange.getRequestURI().getRawQuery();
        request.setQueryParams(parseQueryParams(rawQuery));
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

    private Map<String, List<String>> parseQueryParams(String rawQuery) {
        Map<String, List<String>> map = new HashMap<>();
        if(rawQuery == null || rawQuery.isBlank()){
            return map;
        }

        for(String pair : rawQuery.split("&")){
            if(!pair.isBlank()){
                String[] pairs = pair.split("=", 2);
                String key = urlDecode(pairs[0]);
                String value = pairs.length > 1 ? urlDecode(pairs[1]) : "";
                map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            }
        }
        return map;

    }

    private String urlDecode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

}
