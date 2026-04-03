package com.findu.common.mapper;

import com.findu.common.repository.po.UserExtraInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户扩展信息 MyBatis Mapper。
 */
@Mapper
public interface UserExtraInfoMapper {

    /**
     * 根据用户标识查询扩展信息列表。
     *
     * @param userId 用户标识
     * @return 扩展信息列表
     */
    List<UserExtraInfoPO> selectByUserId(@Param("userId") String userId);

    /**
     * 批量新增扩展信息。
     *
     * @param extraInfos 扩展信息列表
     * @return 影响行数
     */
    int batchInsert(@Param("items") List<UserExtraInfoPO> extraInfos);

    /**
     * 删除指定用户的扩展信息。
     *
     * @param userId 用户标识
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") String userId);
}

