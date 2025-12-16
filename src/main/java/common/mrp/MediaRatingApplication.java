package common.mrp;

import common.Application;
import common.ConnectionPool;
import common.exception.*;
import common.mrp.auth.AuthService;
import common.mrp.auth.Authenticator;
import common.mrp.favorite.FavoriteRepository;
import common.mrp.favorite.FavoriteService;
import common.mrp.leaderboard.LeaderboardController;
import common.mrp.leaderboard.LeaderboardService;
import common.mrp.media.MediaRepository;
import common.mrp.media.MediaService;
import common.mrp.rating.RatingRepository;
import common.mrp.rating.RatingService;
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
    private final Authenticator authenticator;
    private final ConnectionPool connectionPool;



    public MediaRatingApplication() {
        this.connectionPool = new ConnectionPool(
                "postgresql",
                "127.0.0.1",
                5433,
                "test",
                "test123",
                "mrpdb"
        );
        // ---- Repositories (einmalig, shared) ----
        UserRepository  userRepo  = new UserRepository(connectionPool);
        MediaRepository mediaRepo = new MediaRepository(connectionPool);
        RatingRepository ratingRepo = new RatingRepository(connectionPool);
        FavoriteRepository favoriteRepo = new FavoriteRepository(connectionPool);

        // ---- Services (einmalig, mit shared Repos) ----
        UserService   userService   = new UserService(userRepo, mediaRepo);
        AuthService   authService   = new AuthService(userRepo);
        MediaService  mediaService  = new MediaService(mediaRepo);
        RatingService ratingService = new RatingService(ratingRepo, mediaRepo); //Macht das sinn so?
        LeaderboardService leaderboardService = new LeaderboardService(); // Repo nach DB entscheidung
        FavoriteService favoriteService = new FavoriteService(mediaRepo, userRepo, favoriteRepo); // Repo nach DB entscheidung

        this.authenticator = authService::verifyFromAuthorizationHeader;

        // ---- Controller (einmalig, mit Services) ----
        UserController   userController   = new UserController(userService, authService, ratingService);
        MediaController  mediaController  = new MediaController(mediaService, ratingService, favoriteService, authService);
        RatingController ratingController = new RatingController(ratingService, authService);
        LeaderboardController leaderboardController = new LeaderboardController(leaderboardService);
        EchoController   echoController   = new EchoController(); // Kann man eig weglassen

        // ---- Router ----
        this.router = new Router()
                .registerController(mediaController)
                .registerController(userController)
                .registerController(ratingController)
                .registerController(leaderboardController)
                .registerController(echoController);

        this.exceptionMapper = new ExceptionMapper();
        this.exceptionMapper.register(EntityNotFoundException.class, Status.NOT_FOUND);
        this.exceptionMapper.register(NotJsonBodyException.class, Status.BAD_REQUEST);
        this.exceptionMapper.register(JsonConversionException.class, Status.INTERNAL_SERVER_ERROR);
        this.exceptionMapper.register(CredentialMissmatchException.class, Status.UNAUTHORIZED);
        this.exceptionMapper.register(MissingRequiredFieldsException.class, Status.BAD_REQUEST);
        this.exceptionMapper.register(ForbiddenException.class, Status.FORBIDDEN);

    }

    public Authenticator getAuthenticator() {
        return authenticator;
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
