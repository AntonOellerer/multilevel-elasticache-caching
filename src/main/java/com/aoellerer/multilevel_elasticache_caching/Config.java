package com.aoellerer.multilevel_elasticache_caching;

import com.aoellerer.multilevel_elasticache_caching.auth.IamRedisCredentialsProviderFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnExpression("'${spring.data.redis.host:}'.endsWith('amazonaws.com')")
public class Config {
  @Bean
  public LettuceClientConfigurationBuilderCustomizer lettuceClientConfiguration(
      IamRedisCredentialsProviderFactory iamRedisCredentialsProviderFactory) {
    return clientConfigurationBuilder -> clientConfigurationBuilder.redisCredentialsProviderFactory(iamRedisCredentialsProviderFactory);
  }
}
