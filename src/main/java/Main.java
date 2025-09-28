import common.Router;
import common.echo.EchoController;
import common.mrp.MediaRatingApplication;
import common.mrp.auth.AuthController;
import common.mrp.media.MediaController;
import common.mrp.rating.RatingController;
import common.mrp.user.UserController;
import server.Server;

public class Main {
    public static void main(String[] args) {

        Server server = new Server(8080, new MediaRatingApplication());
        System.out.println("Server Started");
        server.start();
    }
}