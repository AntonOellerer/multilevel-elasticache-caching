package com.aoellerer.multilevel_elasticache_caching.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnExpression("'${spring.data.redis.host:}'.endsWith('amazonaws.com')")
public class ApplicationRedisCacheConfiguration {
  @Value("${spring.data.redis.username}")
  String userName;
  @Value("${application.cache.redis.name}")
  String name;
  @Value("${application.cache.redis.region}")
  String region;
  @Value("${application.cache.redis.isServerless}")
  boolean isServerless;
}
