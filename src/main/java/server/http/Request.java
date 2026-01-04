package server.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {

    private String method;
    private String path;
    private String body;
    private Integer authUserId;
    private String authUsername;

    private Map<String, List<String>> queryParams = new HashMap<>();

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


    public Integer getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(Integer authUserId) {
        this.authUserId = authUserId;
    }

    public String getAuthUsername() {
        return authUsername;
    }

    public void setAuthUsername(String authUsername) {
        this.authUsername = authUsername;
    }

    public void setQueryParams(Map<String, List<String>> queryParams) {
        this.queryParams = queryParams;
    }

    public String getQueryParam(String key) {
        //Zugriff auf einzelnen Wert
        List<String> values = queryParams.get(key);
        return (values == null || values.isEmpty()) ? null : values.get(0);
    }

    public List<String> getQueryParams(String key) {
        //Zugriff auf alle werte
        return queryParams.getOrDefault(key, List.of());
    }
}
