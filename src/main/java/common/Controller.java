package common;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.exception.JsonConversionException;
import common.exception.NotJsonBodyException;
import server.http.ContentType;
import server.http.Request;
import server.http.Response;
import server.http.Status;

import java.nio.charset.StandardCharsets;

public abstract class Controller {
    private static final ObjectMapper mapper = new ObjectMapper();

    private final String basePath;

    protected Controller(String basePath) {
        this.basePath = basePath;
    }

    public String getBasePath() {
        return basePath;
    }

    // subPath ist z. B. "/", "/42", "/42/edit" – also NUR der Teil hinter basePath.
    public abstract Response handle(Request request, String subPath);

    protected Response text(String text) {
        return text(Status.OK, text);
    }

    protected Response text(Status status, String text) {
        return r(status, ContentType.PLAIN_TEXT, text);
    }

    protected Response status(Status status) {
        return text(status, status.getMessage());
    }

    protected String readBodyAsString(Request req) {
        String body = req.getBody();
        if (body == null || body.isBlank()) {
            throw new NotJsonBodyException(new IllegalArgumentException("Request body is empty"));
        }
        return body;
    }


    protected <T> T toObject(String content, Class<T> valueType) {
        try {
            if (content == null) {
                throw new IllegalArgumentException("content is null");
            }
            return mapper.readValue(content, valueType);
        } catch (Exception ex) {
            throw new NotJsonBodyException(ex);
        }
    }

    protected Response json(Object o, Status status) {
        try {
            String json = mapper.writeValueAsString(o);
            return r(status, ContentType.JSON, json);
        } catch (Exception ex) {
            throw new JsonConversionException(ex);
        }
    }


    private Response r(Status status, ContentType contentType, String body) {
        Response response = new Response();
        response.setStatus(status);
        response.setContentType(contentType);
        response.setBody(body);
        return response;
    }
}