package services.twitter;
import services.RedisService;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by mahesh on 21/08/16.
 */
public class TwitterPublicStreamHandler implements StatusListener{
    public static final String TWITTER_WC= "TWITTER_WC";

    private final RedisService redisService;
    private static final Pattern pattern = Pattern.compile("\\w+");
    private final Set<String> blacklistedWords;

    public TwitterPublicStreamHandler(RedisService redisService, Set<String> blacklistedWords) {
        this.redisService = redisService;
        this.blacklistedWords = blacklistedWords;
    }
    @Override
    public void onStatus(Status status) {
        List<String> words = new ArrayList<String>();
        Matcher matcher = pattern.matcher(status.getText());

        while (matcher.find()) {words.add(matcher.group());}

        Map<String, Long> microWC = words.stream()
                .filter(word -> word != null && word.length() > 3 && !blacklistedWords.contains(word))
                .map(word -> word.toLowerCase())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        redisService.incrementKeys(TWITTER_WC, microWC);
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {
    }

    @Override
    public void onStallWarning(StallWarning warning) {

    }

    @Override
    public void onException(Exception ex) {
        ex.printStackTrace();
    }
}
