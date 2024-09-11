package com.aoellerer.multilevel_elasticache_caching.auth;

import io.lettuce.core.RedisCredentialsProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.lettuce.RedisCredentialsProviderFactory;
import org.springframework.stereotype.Component;

/**
 * The `IamRedisCredentialsProviderFactory` is the entry point for authenticating with the elasticache redis instance. The instance is configured to
 * accept temporary IAM tokens as passwords for specific users, to maximize access security.
 * <p>
 * The `IamRedisCredentialsProviderFactory` is a spring component which creates an instance of the {@link IamRedisAuthCredentialsProvider} with the
 * injected {@link ApplicationRedisCacheConfiguration}. The {@link IamRedisAuthCredentialsProvider} is supplied to the redis triver, and turn uses the
 * information stored in the {@link ApplicationRedisCacheConfiguration} and the{@link IamAuthTokenCreator} to create the temporary passwords for
 * authentication.
 */
@Component
@ConditionalOnExpression("'${spring.data.redis.host:}'.endsWith('amazonaws.com')")
public class IamRedisCredentialsProviderFactory implements RedisCredentialsProviderFactory {
  private final ApplicationRedisCacheConfiguration applicationRedisCacheConfiguration;

  public IamRedisCredentialsProviderFactory(ApplicationRedisCacheConfiguration applicationRedisCacheConfiguration) {
    this.applicationRedisCacheConfiguration = applicationRedisCacheConfiguration;
  }

  @Override
  public RedisCredentialsProvider createCredentialsProvider(RedisConfiguration redisConfiguration) {
    return new IamRedisAuthCredentialsProvider(applicationRedisCacheConfiguration);
  }
}
