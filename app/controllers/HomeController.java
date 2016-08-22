package controllers;

import akka.actor.ActorSystem;
import play.api.Configuration;
import play.mvc.*;

import scala.concurrent.ExecutionContextExecutor;
import services.TwitterService;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import views.html.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    private final Configuration conf;
    private TwitterService twitterService;
    private ActorSystem actorSystem;
    private ExecutionContextExecutor exec;

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    @Inject
    public HomeController(TwitterService twitterService, ActorSystem actorSystem, ExecutionContextExecutor exec, Configuration conf) {
        this.twitterService = twitterService;
        this.actorSystem = actorSystem;
        this.exec = exec;
        this.conf = conf;
    }
    public CompletableFuture<Result> getWordCount() {
        CompletableFuture<Result> future = CompletableFuture
                .supplyAsync(() -> twitterService.getTopNWords(50))
                .thenApply(lst -> ok(wc.render(lst, "Twitter Word Count")));

        return future;
    }
    public CompletableFuture<Result> getEventsCount() {
        CompletableFuture<Result> future = CompletableFuture
                .supplyAsync(() -> twitterService.getEvents())
                .thenApply(lst -> ok(wc.render(lst, "Twitter Events")));

        return future;
    }
}
