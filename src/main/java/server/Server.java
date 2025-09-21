package server;

import com.sun.net.httpserver.HttpServer;
import common.Application;
import server.util.RequestMapper;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    private HttpServer httpServer;
    private final int port;
    private final Application application;

    public Server(int port, Application application) {
        this.port = port;
        this.application = application;
    }

    public void start() {
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress("localhost", this.port), 0);
            this.httpServer.createContext("/", new Handler(this.application, new RequestMapper()));
            this.httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
