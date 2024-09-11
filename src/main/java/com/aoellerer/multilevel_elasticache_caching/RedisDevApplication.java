package com.aoellerer.multilevel_elasticache_caching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RedisDevApplication {

  public static void main(String[] args) {
    SpringApplication.run(RedisDevApplication.class, args);
  }

}
