package com.findu.common.autoconfigure;

import com.findu.common.mapper.UserExtraInfoMapper;
import com.findu.common.repository.UserExtraInfoRepository;
import com.findu.common.repository.impl.UserExtraInfoRepositoryImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.mybatis.spring.annotation.MapperScan;

@AutoConfiguration
@ConditionalOnClass(name = "org.apache.ibatis.session.SqlSessionFactory")
@ConditionalOnProperty(name = "findu.data.enabled", havingValue = "true", matchIfMissing = true)
@MapperScan("com.findu.common.mapper")
public class FinduDataAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(UserExtraInfoRepository.class)
    public UserExtraInfoRepositoryImpl userExtraInfoRepository(UserExtraInfoMapper mapper) {
        return new UserExtraInfoRepositoryImpl(mapper);
    }
}
