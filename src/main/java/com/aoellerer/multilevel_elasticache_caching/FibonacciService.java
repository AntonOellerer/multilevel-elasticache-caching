package com.aoellerer.multilevel_elasticache_caching;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class FibonacciService {
  @Cacheable(cacheNames = "fibonacci", cacheManager = "cacheManager")
  public long fib(long n) {
    if (n <= 1) {
      return n;
    }
    return fib(n - 1) + fib(n - 2);
  }
}
