# Multilevel Elasticache Caching

This repository contains a small prototype project demonstrating how to employ
[Spring Boot multi-level cache starter](https://github.com/SuppieRK/spring-boot-multilevel-cache-starter)
together with AWS Elasticache, and how the Redis part of the cache needs to be configured so that
the [AWS IAM authentication](https://docs.aws.amazon.com/AmazonElastiCache/latest/red-ug/auth-iam.html) for Redis can be used.
