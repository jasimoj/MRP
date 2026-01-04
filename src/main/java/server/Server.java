package server;

import com.sun.net.httpserver.HttpServer;
import common.Application;
import common.mrp.MediaRatingApplication;
import common.mrp.auth.Authenticator;
import server.util.RequestMapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;

public class Server {
    private HttpServer httpServer;
    private final int port;
    private final Application application;

    public Server(int port, Application application) {
        this.port = port;
        this.application = application;
    }

    // Initialisiert und startet HTTP-Server mit passendem Authenticator
    public void start() {
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress("localhost", this.port), 0);
            //Authenticator nur dann verwenden, wenn die Applikation Authentifizierung unterstützt, sonst keine Authentifizierung
            Authenticator authenticator =
                    (application instanceof MediaRatingApplication app)
                            ? app.getAuthenticator()
                            : header -> Optional.empty(); // Fallback, falls andere App-Typen


            this.httpServer.createContext("/", new Handler(this.application, new RequestMapper(authenticator)));
            this.httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
