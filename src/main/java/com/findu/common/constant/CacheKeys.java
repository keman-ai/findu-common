package com.findu.common.constant;

/**
 * 缓存Key枚举
 */
public class CacheKeys {

    public static final String USER_WORKS_CHANGE_REQUESTS_SUBMIT = "user:works:change-requests:submit:%s:%s";

    public static final String USER_PROFILE_CREATING = "user:profile:creating:%s";

    /**
     * 用户 Agent 密钥缓存 Key，占位符依次为 userId、agentId。Redis 层会按环境加后缀区分。
     */
    public static final String USER_AGENT_SECRET = "user:agent:secret:%s:%s";

    /**
     * 用户 Agent 按 userId 缓存 Key，占位符为 userId，值为 agentId+secret 的 JSON。Redis 层会按环境加后缀区分。
     */
    public static final String USER_AGENT_BY_USER_ID = "user:agent:by_user:%s";

    /**
     * 用户 Agent 创建防重复提交 Key，占位符为 userId，3 秒内同一用户仅允许提交一次。
     */
    public static final String USER_AGENT_CREATE_SUBMIT = "user:agent:create:submit:%s";

    /**
     * 用户 Agent 按 agentId 缓存 Key，占位符为 agentId，值为 userId。Redis 层会按环境加后缀区分。
     */
    public static final String USER_AGENT_BY_AGENT_ID = "user:agent:by_agent:%s";

    /**
     * 审核重试定时任务分布式锁 Key，用于多实例下唯一调度，避免重复执行。
     */
    public static final String MODERATION_RETRY_SCHEDULER_LOCK = "moderation:retry:scheduler:lock";

    /**
     * 用户 Agent 授权码临时缓存 Key，占位符为 code。用于一次性换取 Agent 信息，有效时间由配置决定。
     */
    public static final String USER_AGENT_AUTH_CODE = "user:agent:auth-code:%s";

    /**
     * 用户 Agent 授权码待写入标记 Key，占位符为 userId。值为 userId + "_" + code，表示待将 Agent 信息写入的 USER_AGENT_AUTH_CODE 对应关系，10 分钟有效。
     */
    public static final String USER_AGENT_AUTH_CODE_PENDING = "user:agent:auth-code-pending:%s";

}
