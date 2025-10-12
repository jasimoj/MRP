package common.exception;

import server.http.Response;

public abstract  class HttpStatusException extends RuntimeException {

    public abstract Response getResponse();

}