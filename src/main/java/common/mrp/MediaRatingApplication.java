package common.mrp;

import common.Application;
import common.Router;
import common.echo.EchoController;
import common.mrp.auth.AuthController;
import common.mrp.media.MediaController;
import common.mrp.rating.RatingController;
import common.mrp.user.UserController;
import server.http.Request;
import server.http.Response;

public class MediaRatingApplication implements Application {

    private final Router router;

    public MediaRatingApplication() {
        this.router = new Router();
        router.registerController(new MediaController())
                .registerController(new UserController())
                .registerController(new EchoController())
                .registerController(new AuthController())
                .registerController(new RatingController());
    }

    @Override
    public Response handle(Request request) {
        return router.handle(request);
    }
}
