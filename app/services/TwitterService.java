package services;

import org.apache.commons.lang3.StringUtils;
import play.api.Configuration;
import services.twitter.TwitterPublicStreamHandler;
import services.twitter.TwitterUserStreamHandler;
import services.twitter.Word;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.getProperty;

/**
 * Created by mahesh on 21/08/16.
 */
@Singleton
public class TwitterService {

    private static final String FILTER_KEYWORDS = "twitter.stream.keywords";
    private static final String FILTER_LOCATIONS = "twitter.stream.locations";
    private static final String CONSUMER_KEY = "twitter.consumer.key";
    private static final String CONSUMER_SECRET = "twitter.consumer.secret";
    private static final String ACCESS_TOKEN = "stream.access.token";
    private static final String ACCESS_SECRET = "stream.access.secret";
    private static final Pattern pattern = Pattern.compile("\\w+");
    private static final String BLACKLIST_FILE = "conf/twitter_stream_blacklisted.csv";
    private static final Set<String> blacklistedWords = new HashSet<>();
    private static RedisService redisService;
    private final Configuration conf;
    private TwitterUserStreamHandler twitterEventHandler;
    private TwitterPublicStreamHandler twitterHandler;

    @Inject
    public TwitterService(RedisService redisService, Configuration conf) {
        this.redisService = redisService;
        this.conf = conf;
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(conf.underlying().getString(CONSUMER_KEY))
                .setOAuthConsumerSecret(conf.underlying().getString(CONSUMER_SECRET));

        TwitterStreamFactory factory = new TwitterStreamFactory(cb.build());
        setupTwitterPublicStream(factory);
        setupTwitterEventStream(factory);
        setupBlacklist();
    }

    private void setupTwitterPublicStream(TwitterStreamFactory factory) {
        String prefix = "twitter.public.";
        TwitterStream twitterStream = factory.getInstance(new AccessToken(conf.underlying().getString(prefix + ACCESS_TOKEN), conf.underlying().getString(prefix + ACCESS_SECRET)));

        this.twitterHandler = new TwitterPublicStreamHandler(redisService, blacklistedWords);
        twitterStream.addListener(twitterHandler);
        FilterQuery tweetFilterQuery = new FilterQuery();
        setFilterKeywords(tweetFilterQuery);
        setFilterLocations(tweetFilterQuery);

        tweetFilterQuery.language(new String[]{"en"});
        twitterStream.filter(tweetFilterQuery);
    }

    private void setupTwitterEventStream(TwitterStreamFactory factory) {
        String prefix = "twitter.user.";
        TwitterStream twitterStream = factory.getInstance(new AccessToken(conf.underlying().getString(prefix + ACCESS_TOKEN), conf.underlying().getString(prefix + ACCESS_SECRET)));
        this.twitterEventHandler = new TwitterUserStreamHandler(redisService);
        twitterStream.addListener(twitterEventHandler);

        twitterStream.user();
    }

    private void setFilterKeywords(FilterQuery tweetFilterQuery) {
        if(StringUtils.isBlank(getProperty(FILTER_KEYWORDS)))
            return;
        tweetFilterQuery.track(getProperty(FILTER_KEYWORDS).split(","));
    }

    private void setFilterLocations(FilterQuery tweetFilterQuery) {
        if(StringUtils.isBlank(getProperty(FILTER_LOCATIONS)))
            return;
        String[] arr = getProperty(FILTER_LOCATIONS).split(",");
        double[][] ret = new double[arr.length][2];
        int idx = 0;
        for(String str : getProperty(FILTER_LOCATIONS).split(",")) {
            String[] split = str.split("/");
            ret[idx][0] = Double.parseDouble(split[0]);
            ret[idx][1] = Double.parseDouble(split[1]);
            idx++;
        }
        tweetFilterQuery.locations(ret);
    }

    private void setupBlacklist() {
        try {
            String blacklist = null;
            blacklist = new String(Files.readAllBytes(Paths.get(BLACKLIST_FILE)));
            System.out.println("Blacklist file : " +new File(BLACKLIST_FILE).getAbsolutePath());
            ;
            Matcher matcher = pattern.matcher(blacklist);
            while(matcher.find()) blacklistedWords.add(matcher.group());
            System.out.println("Blacklist Word Count : "+blacklistedWords.size());

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while setting up black list. "+e.getMessage());
        }

    }

    public List<Word> getTopNWords(int n) {
        return redisService.getTopN(twitterHandler.TWITTER_WC, n);
    }
    public List<Word> getEvents() {
        return redisService.getTopN(twitterEventHandler.TWITTER_EVENTS, Integer.MAX_VALUE);
    }



}
