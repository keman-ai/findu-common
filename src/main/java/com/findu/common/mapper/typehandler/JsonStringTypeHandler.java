package com.findu.common.mapper.typehandler;

import com.findu.common.util.FastJsonHelper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * MyBatis JSON字符串类型处理器。
 * 用于处理MySQL TEXT类型字段与Java String之间的转换，确保JSON字符串格式正确。
 *
 * <p>功能：
 * <ul>
 *   <li>写入时：验证并规范化JSON字符串，确保格式正确</li>
 *   <li>读取时：处理可能存在的双重转义JSON字符串，返回标准JSON格式</li>
 * </ul>
 */
@MappedTypes({String.class})
@MappedJdbcTypes({JdbcType.VARCHAR, JdbcType.LONGVARCHAR})
public class JsonStringTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
            throws SQLException {
        // 写入时，验证并规范化JSON字符串格式
        String normalized = normalizeJsonStringForWrite(parameter);
        ps.setString(i, normalized);
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return normalizeJsonString(value);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return normalizeJsonString(value);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return normalizeJsonString(value);
    }

    /**
     * 写入时规范化JSON字符串，确保格式正确。
     * 如果输入是有效的JSON字符串，直接返回；如果是双重转义，先解析再返回。
     *
     * @param jsonStr JSON字符串
     * @return 规范化后的JSON字符串
     */
    private String normalizeJsonStringForWrite(String jsonStr) {
        if (jsonStr == null || jsonStr.isEmpty()) {
            return jsonStr;
        }

        String trimmed = jsonStr.trim();

        // 检查是否是双重转义的JSON字符串（以引号开头和结尾）
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            try {
                // 尝试解析一次，如果是双重转义，解析后会得到正常的JSON字符串
                String parsed = FastJsonHelper.parseObject(trimmed, String.class);
                if (parsed != null && !parsed.isEmpty()) {
                    // 验证解析后的字符串是否是有效的JSON
                    try {
                        FastJsonHelper.parseObject(parsed, Map.class);
                        return parsed;
                    } catch (Exception e) {
                        // 解析后的字符串不是有效的JSON，验证原字符串
                        return validateJsonString(jsonStr);
                    }
                }
            } catch (Exception e) {
                // 解析失败，验证原字符串是否是有效的JSON
                return validateJsonString(jsonStr);
            }
        }

        // 验证并返回标准JSON字符串
        return validateJsonString(jsonStr);
    }

    /**
     * 读取时规范化JSON字符串，处理双重转义的情况。
     * 如果字符串是双重转义的JSON（以引号开头和结尾），则解析一次。
     *
     * @param jsonStr JSON字符串
     * @return 规范化后的JSON字符串
     */
    private String normalizeJsonString(String jsonStr) {
        if (jsonStr == null || jsonStr.isEmpty()) {
            return jsonStr;
        }

        String trimmed = jsonStr.trim();

        // 检查是否是双重转义的JSON字符串（以引号开头和结尾）
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            try {
                // 尝试解析一次，如果是双重转义，解析后会得到正常的JSON字符串
                String parsed = FastJsonHelper.parseObject(trimmed, String.class);
                if (parsed != null && !parsed.isEmpty()) {
                    // 验证解析后的字符串是否是有效的JSON
                    try {
                        FastJsonHelper.parseObject(parsed, Map.class);
                        return parsed;
                    } catch (Exception e) {
                        // 解析后的字符串不是有效的JSON，返回原字符串
                        return jsonStr;
                    }
                }
            } catch (Exception e) {
                // 解析失败，说明不是双重转义，直接返回原字符串
            }
        }

        return jsonStr;
    }

    /**
     * 验证JSON字符串格式是否正确。
     * 如果格式正确，返回原字符串；如果格式错误，尝试修复或返回原字符串。
     *
     * @param jsonStr JSON字符串
     * @return 验证后的JSON字符串
     */
    private String validateJsonString(String jsonStr) {
        if (jsonStr == null || jsonStr.isEmpty()) {
            return jsonStr;
        }

        String trimmed = jsonStr.trim();
        
        // 空字符串或空白字符串，返回空字符串
        if (trimmed.isEmpty()) {
            return "";
        }

        // 空JSON对象或数组，直接返回
        if ("{}".equals(trimmed) || "[]".equals(trimmed)) {
            return trimmed;
        }

        try {
            // 尝试解析JSON对象，验证格式是否正确
            FastJsonHelper.parseObject(trimmed, Map.class);
            // 如果解析成功，说明格式正确，返回原字符串
            return trimmed;
        } catch (Exception e) {
            // 如果解析失败，尝试解析为数组
            try {
                FastJsonHelper.parseArray(trimmed, Object.class);
                return trimmed;
            } catch (Exception e2) {
                // 如果数组解析也失败，可能是其他有效的JSON值（如字符串、数字、布尔值、null）
                // 尝试解析为通用对象
                try {
                    FastJsonHelper.parseObject(trimmed, Object.class);
                    return trimmed;
                } catch (Exception e3) {
                    // 如果所有解析都失败，返回原字符串
                    // 注意：这里不抛出异常，因为可能是业务上允许的特殊情况
                    return jsonStr;
                }
            }
        }
    }
}

