package com.findu.common.autoconfigure;

import com.findu.common.moderation.ContentModerationClient;
import com.findu.common.moderation.ModerationProperties;
import com.findu.common.moderation.impl.MisContentModerationClient;
import com.findu.common.moderation.impl.StubContentModerationClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@AutoConfiguration
@ConditionalOnProperty(name = "findu.moderation.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(ModerationProperties.class)
public class FinduModerationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "misContentModerationClient")
    @Profile({"stable", "prod"})
    public MisContentModerationClient misContentModerationClient(
            RestTemplate restTemplate, ModerationProperties properties) {
        return new MisContentModerationClient(restTemplate, properties);
    }

    @Bean
    @ConditionalOnMissingBean(name = "stubContentModerationClient")
    @Profile({"dev", "test"})
    public StubContentModerationClient stubContentModerationClient() {
        return new StubContentModerationClient();
    }
}
