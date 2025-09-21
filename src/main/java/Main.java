import common.Router;
import common.echo.EchoController;
import common.mrp.auth.AuthController;
import common.mrp.media.MediaController;
import common.mrp.rating.RatingController;
import common.mrp.user.UserController;
import server.Server;

public class Main {
    public static void main(String[] args) {
        Router router = new Router()
                .registerController(new UserController())
                .registerController(new RatingController())
                .registerController(new MediaController())
                .registerController(new AuthController())
                .registerController(new EchoController());

        Server server = new Server(8080, router);
        System.out.println("Server Started");
        server.start();
    }
}