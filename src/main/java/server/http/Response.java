package server.http;

public class Response {
    private Status status;
    private ContentType contentType;
    private String body;

    public String getContentType() {
        return contentType.getMimeType();
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public int getStatus() {
        return status.getCode();
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
