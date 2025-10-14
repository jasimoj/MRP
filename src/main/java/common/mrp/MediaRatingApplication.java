package common.mrp;

import common.Application;
import common.exception.EntityNotFoundException;
import common.exception.ExceptionMapper;
import common.exception.JsonConversionException;
import common.exception.NotJsonBodyException;
import common.mrp.auth.AuthService;
import common.mrp.leaderboard.LeaderboardController;
import common.mrp.leaderboard.LeaderboardService;
import common.mrp.media.MediaRepository;
import common.mrp.media.MediaService;
import common.mrp.rating.RatingRepository;
import common.mrp.rating.RatingService;
import common.mrp.user.User;
import common.mrp.user.UserRepository;
import common.mrp.user.UserService;
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
        router.registerController(new MediaController(new MediaService(new MediaRepository())))
                .registerController(new UserController(new UserService(new UserRepository()), new AuthService(new UserRepository())))
                .registerController(new RatingController(new RatingService(new RatingRepository())))
                .registerController(new LeaderboardController(new LeaderboardService()))
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
            return exceptionMapper.toResponse(e);
        }
    }
}
