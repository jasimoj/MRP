import common.Router;
import common.echo.EchoController;
import server.Server;

public class Main {
    public static void main(String[] args) {
        Router router = new Router().registerController(new EchoController());

        Server server = new Server(8080, router);
        System.out.println("Server Started");
        server.start();
    }
}