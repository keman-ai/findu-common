package com.findu.common.repository.impl;

import com.findu.common.model.UserExtraInfo;
import com.findu.common.repository.UserExtraInfoRepository;
import com.findu.common.mapper.UserExtraInfoMapper;
import com.findu.common.repository.po.UserExtraInfoPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户扩展信息仓储实现。
 */
@Repository
public class UserExtraInfoRepositoryImpl implements UserExtraInfoRepository {

    /**
     * 扩展信息 Mapper。
     */
    private final UserExtraInfoMapper userExtraInfoMapper;

    /**
     * Mapper 注入。
     *
     * @param userExtraInfoMapper Mapper 实例
     */
    @Autowired
    public UserExtraInfoRepositoryImpl(UserExtraInfoMapper userExtraInfoMapper) {
        this.userExtraInfoMapper = userExtraInfoMapper;
    }

    @Override
    public List<UserExtraInfo> findByUserId(String userId) {
        List<UserExtraInfoPO> list = userExtraInfoMapper.selectByUserId(userId);
        return list.stream().map(this::convertToDomain).collect(Collectors.toList());
    }

    @Override
    public int batchInsert(List<UserExtraInfo> extraInfos) {
        List<UserExtraInfoPO> list = extraInfos.stream().map(this::convertToPO).collect(Collectors.toList());
        return userExtraInfoMapper.batchInsert(list);
    }

    @Override
    public int deleteByUserId(String userId) {
        return userExtraInfoMapper.deleteByUserId(userId);
    }

    private UserExtraInfo convertToDomain(UserExtraInfoPO po) {
        UserExtraInfo info = new UserExtraInfo();
        info.setId(po.getId());
        info.setUserId(po.getUserId());
        info.setKey(po.getInfoKey());
        info.setValue(po.getInfoValue());
        info.setGmtCreate(po.getGmtCreate());
        info.setGmtModified(po.getGmtModified());
        return info;
    }

    private UserExtraInfoPO convertToPO(UserExtraInfo info) {
        UserExtraInfoPO po = new UserExtraInfoPO();
        po.setId(info.getId());
        po.setUserId(info.getUserId());
        po.setInfoKey(info.getKey());
        po.setInfoValue(info.getValue());
        po.setGmtCreate(info.getGmtCreate());
        po.setGmtModified(info.getGmtModified());
        return po;
    }
}

