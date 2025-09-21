package server.http;

public enum Status {
    OK(200, "OK"),
    CREATED(201, "CREATED"),
    ACCEPTED(202, "Accepted"),
    NO_CONTENT(204, "No Content"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    CONFLICT(409, "Conflict"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented");

    private final int code;
    private final String message;

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
    Status(int code, String message) {
        this.code = code;
        this.message = message;
    }
}


//controller weiterleitung -> router hat liste von controllern und basierend auf pfad wird controller ausgewählt
