import common.mrp.MediaRatingApplication;
import server.Server;

public class Main {
    public static void main(String[] args) {

        Server server = new Server(8080, new MediaRatingApplication());
        System.out.println("Server Started");
        server.start();
    }
}