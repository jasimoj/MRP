package common.mrp;

import common.Application;
import common.exception.EntityNotFoundException;
import common.exception.ExceptionMapper;
import common.exception.JsonConversionException;
import common.exception.NotJsonBodyException;
import common.mrp.leaderboard.LeaderboardController;
import common.routing.Router;
import common.echo.EchoController;
import common.mrp.media.MediaController;
import common.mrp.rating.RatingController;
import common.mrp.user.UserController;
import server.http.Request;
import server.http.Response;
import server.http.Status;

public class MediaRatingApplication implements Application {

    private final Router router;
    private final ExceptionMapper exceptionMapper;
    public MediaRatingApplication() {
        this.router = new Router();
        router.registerController(new MediaController())
                .registerController(new UserController())
                .registerController(new RatingController())
                .registerController(new LeaderboardController())
                .registerController(new EchoController());

        this.exceptionMapper = new ExceptionMapper();
        this.exceptionMapper.register(EntityNotFoundException.class, Status.NOT_FOUND);
        this.exceptionMapper.register(NotJsonBodyException.class, Status.BAD_REQUEST);
        this.exceptionMapper.register(JsonConversionException.class, Status.INTERNAL_SERVER_ERROR);
    }

    @Override
    public Response handle(Request request) {
        try {
            return router.handle(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
