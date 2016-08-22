package services;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import play.api.Configuration;
import redis.clients.jedis.*;
import services.twitter.Word;

/**
 * Created by mahesh on 21/08/16.
 */
@Singleton
public class RedisService {
    private final JedisPool pool;

    @Inject
    public RedisService(Configuration conf) {
        JedisPoolConfig jedisConf = new JedisPoolConfig();
        jedisConf.setTestOnBorrow(true);
        jedisConf.setTestOnReturn(true);
        jedisConf.setTestWhileIdle(true);
        jedisConf.setMaxIdle(400);
        jedisConf.setMaxIdle(400);
        jedisConf.setMaxWaitMillis(120000);
        jedisConf.setMaxTotal(128);
        this.pool = new JedisPool(jedisConf, conf.underlying().getString("redis.host"), conf.underlying().getInt("redis.port"),conf.underlying().getInt("redis.timeout"));
    }
    public void incrementKey(String key, String member, double val) {
        try (Jedis jedis = pool.getResource()) {
            jedis.zincrby(key, val, member);
        }
    }

    public void incrementKeys(String key, Map<String, Long> wordCountUpdates) {
        try (Jedis jedis = pool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            wordCountUpdates.forEach((mem, incr) -> pipeline.zincrby(key, incr, mem));
            pipeline.sync();
        }
    }

    public List<Word> getTopN(String key, int n) {
        try (Jedis jedis = pool.getResource()) {
            Set<Tuple> words = jedis.zrevrangeByScoreWithScores(key, "+inf", "1", 0, n);
            List<Word> topKeys = words.stream().map(t -> new Word(t.getElement(), (long)t.getScore())).collect(Collectors.toList());
            return topKeys;
        }
    }

}
