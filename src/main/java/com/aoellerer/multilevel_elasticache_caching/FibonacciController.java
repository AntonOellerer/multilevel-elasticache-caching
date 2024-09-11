package com.aoellerer.multilevel_elasticache_caching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FibonacciController {
  private static final Logger logger = LoggerFactory.getLogger(FibonacciController.class);

  private final FibonacciService fibonacciService;

  public FibonacciController(FibonacciService fibonacciService) {
    this.fibonacciService = fibonacciService;
  }

  @GetMapping(path = "/fibonacci/{value}")
  public Long fibonacci(@PathVariable Long value) {
    long startTime = System.nanoTime();
    long result = fibonacciService.fib(value);
    long endTime = System.nanoTime();
    logger.info("Time taken: {}ms", (endTime - startTime) / 1_000_000);
    return result;
  }
}
