package com.findu.common.cache.impl;

import com.findu.common.cache.CacheService;
import com.findu.common.util.FastJsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis 缓存服务实现。
 * 基础设施层提供缓存能力，不关心具体的业务数据类型。
 */
@Component
public class RedisCacheService implements CacheService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheService.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    /**
     * 根据 spring.profiles.active 在 key 后追加环境后缀，如 ":stable"、":prod"、":dev"。
     *
     * @param key 原始 key
     * @return 处理后的 key
     */
    private String processKey(String key) {
        if (activeProfile == null || activeProfile.isEmpty()) {
            return key;
        }
        return key + ":" + activeProfile;
    }

    /**
     * 批量处理 key。
     *
     * @param keys 原始 key 集合
     * @return 处理后的 key 集合
     */
    private Set<String> processKeys(Set<String> keys) {
        if (activeProfile == null || activeProfile.isEmpty()) {
            return keys;
        }
        return keys.stream()
                .map(this::processKey)
                .collect(Collectors.toSet());
    }

    @Override
    public void set(String key, Object value) {
        try {
            String processedKey = processKey(key);
            redisTemplate.opsForValue().set(processedKey, value);
        } catch (Exception e) {
            LOGGER.error("Redis set操作失败: key={}", key, e);
            throw new RuntimeException("Redis set操作失败", e);
        }
    }

    @Override
    public void set(String key, Object value, Duration duration) {
        try {
            String processedKey = processKey(key);
            redisTemplate.opsForValue().set(processedKey, value, duration);
        } catch (Exception e) {
            LOGGER.error("Redis set操作失败: key={}, duration={}", key, duration, e);
            throw new RuntimeException("Redis set操作失败", e);
        }
    }

    @Override
    public Boolean setIfAbsent(String key, Object value, Duration duration) {
        try {
            String processedKey = processKey(key);
            // 使用 RedisTemplate 原生的 setIfAbsent 方法，该方法底层使用 Redis 的 SET NX EX 命令
            // Spring Data Redis 3.x 的 setIfAbsent(key, value, duration) 是原子的
            return redisTemplate.opsForValue().setIfAbsent(processedKey, value, duration);
        } catch (Exception e) {
            LOGGER.error("Redis setIfAbsent操作失败: key={}, duration={}", key, duration, e);
            throw new RuntimeException("Redis setIfAbsent操作失败", e);
        }
    }

    @Override
    public String get(String key) {
        try {
            String processedKey = processKey(key);
            Object value = redisTemplate.opsForValue().get(processedKey);
            return value != null ? value.toString() : null;
        } catch (org.springframework.data.redis.serializer.SerializationException e) {
            LOGGER.warn("Redis 反序列化失败，删除脏缓存并回源: key={}", key, e);
            try {
                String processedKey = processKey(key);
                redisTemplate.delete(processedKey);
            } catch (Exception deleteEx) {
                LOGGER.error("删除脏缓存失败: key={}", key, deleteEx);
            }
            return null;
        } catch (Exception e) {
            LOGGER.error("Redis get操作失败: key={}", key, e);
            throw new RuntimeException("Redis get操作失败", e);
        }
    }

    @Override
    public String getAndDelete(String key) {
        try {
            String processedKey = processKey(key);
            Object value = redisTemplate.opsForValue().getAndDelete(processedKey);
            return value != null ? value.toString() : null;
        } catch (org.springframework.data.redis.serializer.SerializationException e) {
            LOGGER.warn("Redis 反序列化失败，删除脏缓存: key={}", key, e);
            try {
                String processedKey = processKey(key);
                redisTemplate.delete(processedKey);
            } catch (Exception deleteEx) {
                LOGGER.error("删除脏缓存失败: key={}", key, deleteEx);
            }
            return null;
        } catch (Exception e) {
            LOGGER.error("Redis getAndDelete操作失败: key={}", key, e);
            throw new RuntimeException("Redis getAndDelete操作失败", e);
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        try {
            String processedKey = processKey(key);
            Object value = redisTemplate.opsForValue().get(processedKey);
            if (value == null) {
                return null;
            }
            if (clazz.isInstance(value)) {
                return clazz.cast(value);
            }
            // 如果是字符串，尝试反序列化
            if (value instanceof String strValue) {
                if (StringUtils.hasText(strValue)) {
                    return FastJsonHelper.parseObject(strValue, clazz);
                }
            }
            return FastJsonHelper.parseObject(FastJsonHelper.toJson(value), clazz);
        } catch (org.springframework.data.redis.serializer.SerializationException e) {
            LOGGER.warn("Redis 反序列化失败，删除脏缓存并回源: key={}, clazz={}", key, clazz.getName(), e);
            try {
                String processedKey = processKey(key);
                redisTemplate.delete(processedKey);
            } catch (Exception deleteEx) {
                LOGGER.error("删除脏缓存失败: key={}", key, deleteEx);
            }
            return null;
        } catch (Exception e) {
            LOGGER.error("Redis get操作失败: key={}, clazz={}", key, clazz.getName(), e);
            throw new RuntimeException("Redis get操作失败", e);
        }
    }

    @Override
    public Boolean delete(String key) {
        try {
            String processedKey = processKey(key);
            return redisTemplate.delete(processedKey);
        } catch (Exception e) {
            LOGGER.error("Redis delete操作失败: key={}", key, e);
            throw new RuntimeException("Redis delete操作失败", e);
        }
    }

    @Override
    public Long delete(Set<String> keys) {
        try {
            Set<String> processedKeys = processKeys(keys);
            return redisTemplate.delete(processedKeys);
        } catch (Exception e) {
            LOGGER.error("Redis delete操作失败: keys={}", keys, e);
            throw new RuntimeException("Redis delete操作失败", e);
        }
    }

    @Override
    public Boolean exists(String key) {
        try {
            String processedKey = processKey(key);
            return redisTemplate.hasKey(processedKey);
        } catch (Exception e) {
            LOGGER.error("Redis exists操作失败: key={}", key, e);
            throw new RuntimeException("Redis exists操作失败", e);
        }
    }

    @Override
    public Boolean expire(String key, Duration duration) {
        try {
            String processedKey = processKey(key);
            return redisTemplate.expire(processedKey, duration);
        } catch (Exception e) {
            LOGGER.error("Redis expire操作失败: key={}, duration={}", key, duration, e);
            throw new RuntimeException("Redis expire操作失败", e);
        }
    }

    @Override
    public Long getExpire(String key) {
        try {
            String processedKey = processKey(key);
            return redisTemplate.getExpire(processedKey, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error("Redis getExpire操作失败: key={}", key, e);
            throw new RuntimeException("Redis getExpire操作失败", e);
        }
    }

    @Override
    public Long increment(String key) {
        try {
            String processedKey = processKey(key);
            return redisTemplate.opsForValue().increment(processedKey);
        } catch (Exception e) {
            LOGGER.error("Redis increment操作失败: key={}", key, e);
            throw new RuntimeException("Redis increment操作失败", e);
        }
    }

    @Override
    public Long increment(String key, long delta) {
        try {
            String processedKey = processKey(key);
            return redisTemplate.opsForValue().increment(processedKey, delta);
        } catch (Exception e) {
            LOGGER.error("Redis increment操作失败: key={}, delta={}", key, delta, e);
            throw new RuntimeException("Redis increment操作失败", e);
        }
    }

    @Override
    public Long decrement(String key) {
        try {
            String processedKey = processKey(key);
            return redisTemplate.opsForValue().decrement(processedKey);
        } catch (Exception e) {
            LOGGER.error("Redis decrement操作失败: key={}", key, e);
            throw new RuntimeException("Redis decrement操作失败", e);
        }
    }

    @Override
    public Long decrement(String key, long delta) {
        try {
            String processedKey = processKey(key);
            return redisTemplate.opsForValue().decrement(processedKey, delta);
        } catch (Exception e) {
            LOGGER.error("Redis decrement操作失败: key={}, delta={}", key, delta, e);
            throw new RuntimeException("Redis decrement操作失败", e);
        }
    }

    @Override
    public void hSet(String key, String field, Object value) {
        try {
            String processedKey = processKey(key);
            redisTemplate.opsForHash().put(processedKey, field, value);
        } catch (Exception e) {
            LOGGER.error("Redis hSet操作失败: key={}, field={}", key, field, e);
            throw new RuntimeException("Redis hSet操作失败", e);
        }
    }

    @Override
    public void hSetAll(String key, Map<String, Object> map) {
        try {
            String processedKey = processKey(key);
            redisTemplate.opsForHash().putAll(processedKey, map);
        } catch (Exception e) {
            LOGGER.error("Redis hSetAll操作失败: key={}", key, e);
            throw new RuntimeException("Redis hSetAll操作失败", e);
        }
    }

    @Override
    public Object hGet(String key, String field) {
        try {
            String processedKey = processKey(key);
            return redisTemplate.opsForHash().get(processedKey, field);
        } catch (Exception e) {
            LOGGER.error("Redis hGet操作失败: key={}, field={}", key, field, e);
            throw new RuntimeException("Redis hGet操作失败", e);
        }
    }

    @Override
    public Map<Object, Object> hGetAll(String key) {
        try {
            String processedKey = processKey(key);
            return redisTemplate.opsForHash().entries(processedKey);
        } catch (Exception e) {
            LOGGER.error("Redis hGetAll操作失败: key={}", key, e);
            throw new RuntimeException("Redis hGetAll操作失败", e);
        }
    }

    @Override
    public Long hDelete(String key, Object... fields) {
        try {
            String processedKey = processKey(key);
            return redisTemplate.opsForHash().delete(processedKey, fields);
        } catch (Exception e) {
            LOGGER.error("Redis hDelete操作失败: key={}, fields={}", key, fields, e);
            throw new RuntimeException("Redis hDelete操作失败", e);
        }
    }

    @Override
    public Boolean hExists(String key, String field) {
        try {
            String processedKey = processKey(key);
            return redisTemplate.opsForHash().hasKey(processedKey, field);
        } catch (Exception e) {
            LOGGER.error("Redis hExists操作失败: key={}, field={}", key, field, e);
            throw new RuntimeException("Redis hExists操作失败", e);
        }
    }

    @Override
    public Long lPush(String key, Object value) {
        try {
            String processedKey = processKey(key);
            return redisTemplate.opsForList().leftPush(processedKey, value);
        } catch (Exception e) {
            LOGGER.error("Redis lPush操作失败: key={}", key, e);
            throw new RuntimeException("Redis lPush操作失败", e);
        }
    }

    @Override
    public Long rPush(String key, Object value) {
        try {
            String processedKey = processKey(key);
            return redisTemplate.opsForList().rightPush(processedKey, value);
        } catch (Exception e) {
            LOGGER.error("Redis rPush操作失败: key={}", key, e);
            throw new RuntimeException("Redis rPush操作失败", e);
        }
    }

    @Override
    public Object lPop(String key) {
        try {
            String processedKey = processKey(key);
            return redisTemplate.opsForList().leftPop(processedKey);
        } catch (Exception e) {
            LOGGER.error("Redis lPop操作失败: key={}", key, e);
            throw new RuntimeException("Redis lPop操作失败", e);
        }
    }

    @Override
    public Object rPop(String key) {
        try {
            String processedKey = processKey(key);
            return redisTemplate.opsForList().rightPop(processedKey);
        } catch (Exception e) {
            LOGGER.error("Redis rPop操作失败: key={}", key, e);
            throw new RuntimeException("Redis rPop操作失败", e);
        }
    }

    @Override
    public List<Object> lRange(String key, long start, long end) {
        try {
            String processedKey = processKey(key);
            return redisTemplate.opsForList().range(processedKey, start, end);
        } catch (Exception e) {
            LOGGER.error("Redis lRange操作失败: key={}, start={}, end={}", key, start, end, e);
            throw new RuntimeException("Redis lRange操作失败", e);
        }
    }

    @Override
    public Long lLen(String key) {
        try {
            String processedKey = processKey(key);
            return redisTemplate.opsForList().size(processedKey);
        } catch (Exception e) {
            LOGGER.error("Redis lLen操作失败: key={}", key, e);
            throw new RuntimeException("Redis lLen操作失败", e);
        }
    }

    @Override
    public Long sAdd(String key, Object... values) {
        try {
            String processedKey = processKey(key);
            return redisTemplate.opsForSet().add(processedKey, values);
        } catch (Exception e) {
            LOGGER.error("Redis sAdd操作失败: key={}", key, e);
            throw new RuntimeException("Redis sAdd操作失败", e);
        }
    }

    @Override
    public Long sRemove(String key, Object... values) {
        try {
            String processedKey = processKey(key);
            return redisTemplate.opsForSet().remove(processedKey, values);
        } catch (Exception e) {
            LOGGER.error("Redis sRemove操作失败: key={}", key, e);
            throw new RuntimeException("Redis sRemove操作失败", e);
        }
    }

    @Override
    public Boolean sIsMember(String key, Object value) {
        try {
            String processedKey = processKey(key);
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(processedKey, value));
        } catch (Exception e) {
            LOGGER.error("Redis sIsMember操作失败: key={}", key, e);
            throw new RuntimeException("Redis sIsMember操作失败", e);
        }
    }

    @Override
    public Set<Object> sMembers(String key) {
        try {
            String processedKey = processKey(key);
            return redisTemplate.opsForSet().members(processedKey);
        } catch (Exception e) {
            LOGGER.error("Redis sMembers操作失败: key={}", key, e);
            throw new RuntimeException("Redis sMembers操作失败", e);
        }
    }

    @Override
    public Long sSize(String key) {
        try {
            String processedKey = processKey(key);
            return redisTemplate.opsForSet().size(processedKey);
        } catch (Exception e) {
            LOGGER.error("Redis sSize操作失败: key={}", key, e);
            throw new RuntimeException("Redis sSize操作失败", e);
        }
    }

}

