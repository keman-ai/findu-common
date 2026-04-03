package com.findu.common.trace;

/**
 * TraceId 上下文持有者，基于 ThreadLocal 保存当前线程的 TraceId。
 */
public final class TraceIdHolder {

    /**
     * 在日志 MDC 中使用的键名。
     */
    public static final String MDC_KEY = "traceId";

    /**
     * 请求头中传递 TraceId 所使用的默认键名。
     */
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    private static final ThreadLocal<String> TRACE_ID_LOCAL = new ThreadLocal<>();

    private TraceIdHolder() {
        // utility class
    }

    /**
     * 绑定 TraceId。
     *
     * @param traceId traceId
     */
    public static void bind(String traceId) {
        TRACE_ID_LOCAL.set(traceId);
    }

    /**
     * 获取当前线程 TraceId。
     *
     * @return traceId
     */
    public static String get() {
        return TRACE_ID_LOCAL.get();
    }

    /**
     * 清理当前线程 TraceId。
     */
    public static void clear() {
        TRACE_ID_LOCAL.remove();
    }
}


