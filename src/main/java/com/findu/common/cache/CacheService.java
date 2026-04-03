package com.findu.common.cache;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 缓存服务接口。
 * 基础设施层提供缓存能力，不关心具体的业务数据类型。
 */
public interface CacheService {

    /**
     * 设置键值对，不过期。
     *
     * @param key   键
     * @param value 值
     */
    void set(String key, Object value);

    /**
     * 设置键值对，带过期时间。
     *
     * @param key      键
     * @param value    值
     * @param duration 过期时间
     */
    void set(String key, Object value, Duration duration);

    /**
     * 原子操作：如果键不存在则设置键值对并设置过期时间。
     * 这是一个原子操作，确保检查和设置是原子的。
     *
     * @param key      键
     * @param value    值
     * @param duration 过期时间
     * @return 如果键不存在并成功设置返回 true，如果键已存在返回 false
     */
    Boolean setIfAbsent(String key, Object value, Duration duration);

    /**
     * 获取值。
     *
     * @param key 键
     * @return 值，如果不存在返回 null
     */
    String get(String key);

    /**
     * 原子获取并删除：返回 key 对应的值后删除该 key。
     *
     * @param key 键
     * @return 值，如果不存在返回 null
     */
    String getAndDelete(String key);

    /**
     * 获取值并转换为指定类型。
     *
     * @param key   键
     * @param clazz 目标类型
     * @param <T>   类型参数
     * @return 值，如果不存在返回 null
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 删除键。
     *
     * @param key 键
     * @return 是否删除成功
     */
    Boolean delete(String key);

    /**
     * 批量删除键。
     *
     * @param keys 键集合
     * @return 删除的数量
     */
    Long delete(Set<String> keys);

    /**
     * 判断键是否存在。
     *
     * @param key 键
     * @return 是否存在
     */
    Boolean exists(String key);

    /**
     * 设置过期时间。
     *
     * @param key      键
     * @param duration 过期时间
     * @return 是否设置成功
     */
    Boolean expire(String key, Duration duration);

    /**
     * 获取剩余过期时间（秒）。
     *
     * @param key 键
     * @return 剩余时间（秒），-1 表示永不过期，-2 表示键不存在
     */
    Long getExpire(String key);

    /**
     * 递增。
     *
     * @param key 键
     * @return 递增后的值
     */
    Long increment(String key);

    /**
     * 递增指定值。
     *
     * @param key   键
     * @param delta 增量
     * @return 递增后的值
     */
    Long increment(String key, long delta);

    /**
     * 递减。
     *
     * @param key 键
     * @return 递减后的值
     */
    Long decrement(String key);

    /**
     * 递减指定值。
     *
     * @param key   键
     * @param delta 减量
     * @return 递减后的值
     */
    Long decrement(String key, long delta);

    /**
     * 设置哈希字段值。
     *
     * @param key   键
     * @param field 字段
     * @param value 值
     */
    void hSet(String key, String field, Object value);

    /**
     * 批量设置哈希字段值。
     *
     * @param key 键
     * @param map 字段值映射
     */
    void hSetAll(String key, Map<String, Object> map);

    /**
     * 获取哈希字段值。
     *
     * @param key   键
     * @param field 字段
     * @return 值
     */
    Object hGet(String key, String field);

    /**
     * 获取所有哈希字段值。
     *
     * @param key 键
     * @return 字段值映射
     */
    Map<Object, Object> hGetAll(String key);

    /**
     * 删除哈希字段。
     *
     * @param key    键
     * @param fields 字段数组
     * @return 删除的数量
     */
    Long hDelete(String key, Object... fields);

    /**
     * 判断哈希字段是否存在。
     *
     * @param key   键
     * @param field 字段
     * @return 是否存在
     */
    Boolean hExists(String key, String field);

    /**
     * 向列表左侧添加元素。
     *
     * @param key   键
     * @param value 值
     * @return 列表长度
     */
    Long lPush(String key, Object value);

    /**
     * 向列表右侧添加元素。
     *
     * @param key   键
     * @param value 值
     * @return 列表长度
     */
    Long rPush(String key, Object value);

    /**
     * 从列表左侧弹出元素。
     *
     * @param key 键
     * @return 元素值
     */
    Object lPop(String key);

    /**
     * 从列表右侧弹出元素。
     *
     * @param key 键
     * @return 元素值
     */
    Object rPop(String key);

    /**
     * 获取列表范围。
     *
     * @param key   键
     * @param start 起始位置
     * @param end   结束位置
     * @return 元素列表
     */
    List<Object> lRange(String key, long start, long end);

    /**
     * 获取列表长度。
     *
     * @param key 键
     * @return 列表长度
     */
    Long lLen(String key);

    /**
     * 向集合添加元素。
     *
     * @param key    键
     * @param values 值数组
     * @return 添加的数量
     */
    Long sAdd(String key, Object... values);

    /**
     * 从集合移除元素。
     *
     * @param key    键
     * @param values 值数组
     * @return 移除的数量
     */
    Long sRemove(String key, Object... values);

    /**
     * 判断元素是否在集合中。
     *
     * @param key   键
     * @param value 值
     * @return 是否存在
     */
    Boolean sIsMember(String key, Object value);

    /**
     * 获取集合所有成员。
     *
     * @param key 键
     * @return 成员集合
     */
    Set<Object> sMembers(String key);

    /**
     * 获取集合大小。
     *
     * @param key 键
     * @return 集合大小
     */
    Long sSize(String key);
}

