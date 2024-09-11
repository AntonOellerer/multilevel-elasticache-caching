package com.aoellerer.multilevel_elasticache_caching.auth;

import io.lettuce.core.RedisCredentials;
import io.lettuce.core.RedisCredentialsProvider;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * Implementation of {@link RedisCredentialsProvider}, supplying the correct username and password to the redis client for authenticating against the
 * elastichache redis instance with IAM. Requires {@link ApplicationRedisCacheConfiguration} to retrieve the necessary base values, and uses
 * {@link IamAuthTokenCreator} to create the temporary password from those values.
 */
public class IamRedisAuthCredentialsProvider implements RedisCredentialsProvider, AutoCloseable {
  private static final Logger log = LoggerFactory.getLogger(IamRedisAuthCredentialsProvider.class);

  private final ApplicationRedisCacheConfiguration applicationRedisCacheConfiguration;
  private final IamAuthTokenCreator iamAuthTokenCreator;

  private Instant lastExecution = Instant.EPOCH;
  private String currentToken = "";

  public IamRedisAuthCredentialsProvider(ApplicationRedisCacheConfiguration applicationRedisCacheConfiguration) {
    if (applicationRedisCacheConfiguration == null) {
      throw new IllegalArgumentException("applicationRedisCacheConfiguration must not be null");
    }
    this.applicationRedisCacheConfiguration = applicationRedisCacheConfiguration;
    this.iamAuthTokenCreator = new IamAuthTokenCreator(applicationRedisCacheConfiguration);
  }

  @Override
  public Mono<RedisCredentials> resolveCredentials() {
    return Mono.just(RedisCredentials.just(applicationRedisCacheConfiguration.userName, getIamAuthToken()));
  }

  private String getIamAuthToken() {
    log.trace("Getting token");
    if (Duration.between(lastExecution, Instant.now()).compareTo(IamAuthTokenCreator.TOKEN_EXPIRY_DURATION) > 0) {
      currentToken = iamAuthTokenCreator.createToken();
      log.debug("Token: {}", currentToken);
      lastExecution = Instant.now();
    }
    return currentToken;
  }

  @Override
  public void close() {
    this.iamAuthTokenCreator.close();
  }
}