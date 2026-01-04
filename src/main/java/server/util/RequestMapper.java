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
        //Extrahiert infos aus dem exchange objekt
        Request request = new Request();
        request.setMethod(exchange.getRequestMethod());
        request.setPath(exchange.getRequestURI().getPath());
        String rawQuery = exchange.getRequestURI().getRawQuery();
        request.setQueryParams(parseQueryParams(rawQuery));
        byte[] bytes = exchange.getRequestBody().readAllBytes();
        String body = bytes.length == 0 ? "" : new String(bytes, StandardCharsets.UTF_8);
        request.setBody(body);
        //Auth header auslesen: "Bearer <token>
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        //Authentifizieren und Userdaten im Request speichern
        authenticator.authenticate(authHeader).ifPresent(p -> {
            request.setAuthUserId(p.getUserId());
            request.setAuthUsername(p.getUsername());
        });
        return request;
    }

    private Map<String, List<String>> parseQueryParams(String rawQuery) {
        //Parsed den Query-String (ohne "?") in eine Map
        Map<String, List<String>> map = new HashMap<>();
        if (rawQuery == null || rawQuery.isBlank()) {
            return map;
        }

        for (String pair : rawQuery.split("&")) {
            if (!pair.isBlank()) {
                // Split in key/value, max. 2 Teile (falls value selbst '=' enthält)
                String[] pairs = pair.split("=", 2);
                // Key ist immer vorhanden (auch wenn value fehlt)
                String key = urlDecode(pairs[0]);
                // Wenn kein "=" vorhanden ist: value = "" (Parameter ohne Wert)
                String value = pairs.length > 1 ? urlDecode(pairs[1]) : "";
                // computeIfAbsent: legt Liste an, falls key noch nicht existiert
                map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            }
        }
        return map;

    }

    private String urlDecode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

}
