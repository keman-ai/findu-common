package com.findu.common.repository;

import com.findu.common.model.UserExtraInfo;

import java.util.List;

/**
 * 用户扩展信息仓储，提供扩展键值对的读写能力。
 */
public interface UserExtraInfoRepository {

    /**
     * 根据用户标识查询扩展信息列表。
     *
     * @param userId 用户唯一标识
     * @return 扩展信息列表
     */
    List<UserExtraInfo> findByUserId(String userId);

    /**
     * 批量新增扩展信息。
     *
     * @param extraInfos 扩展信息列表
     * @return 影响行数
     */
    int batchInsert(List<UserExtraInfo> extraInfos);

    /**
     * 按用户标识删除扩展信息。
     *
     * @param userId 用户唯一标识
     * @return 影响行数
     */
    int deleteByUserId(String userId);
}

