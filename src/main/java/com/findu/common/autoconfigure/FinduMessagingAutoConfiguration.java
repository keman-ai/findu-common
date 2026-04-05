package com.findu.common.autoconfigure;

import com.findu.common.mq.MQProducer;
import com.findu.common.mq.impl.LoggingMQProducer;
import com.findu.common.mq.impl.SNSProducer;
import com.findu.common.mq.impl.SNSProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@AutoConfiguration
@ConditionalOnProperty(name = "findu.messaging.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SNSProperties.class)
public class FinduMessagingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "snsProducer")
    @Profile({"stable", "prod"})
    public SNSProducer snsProducer(SNSProperties properties) {
        return new SNSProducer(properties);
    }

    @Bean
    @ConditionalOnMissingBean(name = "loggingMQProducer")
    @Profile({"test", "dev"})
    public LoggingMQProducer loggingMQProducer() {
        return new LoggingMQProducer();
    }
}
