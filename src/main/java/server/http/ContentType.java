package server.http;

public enum ContentType {
    PLAIN_TEXT("text/plain"),
    HTML("text/html"),
    JSON("application/json");

    private final String mimeType;
    ContentType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }
}
