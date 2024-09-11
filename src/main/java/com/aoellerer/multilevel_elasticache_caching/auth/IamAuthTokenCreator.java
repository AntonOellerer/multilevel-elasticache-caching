package com.aoellerer.multilevel_elasticache_caching.auth;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4FamilyHttpSigner;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner;
import software.amazon.awssdk.http.auth.spi.signer.SignedRequest;

/**
 * This class is responsible for creating an IAM auth token for a redis cache. The redis IAM auth takes a signed request uri without the request
 * protocol as a token. It follows the documentation provided at
 * <a href="https://docs.aws.amazon.com/AmazonElastiCache/latest/red-ug/auth-iam.html">Authenticating with IAM</a>
 */
public class IamAuthTokenCreator implements AutoCloseable {
  public static final Duration TOKEN_EXPIRY_DURATION = Duration.of(900, ChronoUnit.SECONDS);

  private static final String REQUEST_PROTOCOL = "http://";
  private static final AwsV4HttpSigner SIGNER = AwsV4HttpSigner.create();
  private static final DefaultCredentialsProvider DEFAULT_CREDENTIALS_PROVIDER = DefaultCredentialsProvider.create();

  private final ApplicationRedisCacheConfiguration applicationRedisCacheConfiguration;
  private final SdkHttpRequest request;

  public IamAuthTokenCreator(ApplicationRedisCacheConfiguration applicationRedisCacheConfiguration) {
    if (applicationRedisCacheConfiguration == null) {
      throw new IllegalArgumentException("applicationRedisCacheConfiguration must not be null");
    }
    this.applicationRedisCacheConfiguration = applicationRedisCacheConfiguration;
    this.request = buildRequest();
  }

  private static String buildToken(SignedRequest signedRequest) {
    return SdkHttpRequest.builder()
        .uri(signedRequest.request().getUri())
        .method(SdkHttpMethod.GET)
        .rawQueryParameters(signedRequest.request().rawQueryParameters())
        .build()
        .getUri()
        .toString()
        .replace(REQUEST_PROTOCOL, "");
  }

  private SdkHttpRequest buildRequest() {
    var httpRequestBuilder = SdkHttpRequest.builder()
        .uri(buildRequestUri())
        .method(SdkHttpMethod.GET)
        .putRawQueryParameter("Action", "connect")
        .putRawQueryParameter("User", applicationRedisCacheConfiguration.userName);
    if (applicationRedisCacheConfiguration.isServerless) {
      httpRequestBuilder.putRawQueryParameter("ResourceType", "ServerlessCache");
    }
    return httpRequestBuilder.build();
  }

  private URI buildRequestUri() {
    return URI.create("%s%s/".formatted(REQUEST_PROTOCOL, applicationRedisCacheConfiguration.name));
  }

  public String createToken() {
    var signedRequest = createSignedRequest();
    return buildToken(signedRequest);
  }

  private SignedRequest createSignedRequest() {
    return SIGNER.sign(awsCredentialsIdentityBuilder -> awsCredentialsIdentityBuilder.identity(DEFAULT_CREDENTIALS_PROVIDER.resolveCredentials())
        .request(request)
        .putProperty(AwsV4FamilyHttpSigner.SERVICE_SIGNING_NAME, "elasticache")
        .putProperty(AwsV4HttpSigner.REGION_NAME, applicationRedisCacheConfiguration.region)
        // By setting AUTH_LOCATION to QUERY_STRING, and an EXPIRATION_DURATION,a presigned url is generated
        // see https://github.com/aws/aws-sdk-java-v2/issues/5401#issuecomment-2266235066
        .putProperty(AwsV4FamilyHttpSigner.AUTH_LOCATION, AwsV4FamilyHttpSigner.AuthLocation.QUERY_STRING)
        .putProperty(AwsV4FamilyHttpSigner.EXPIRATION_DURATION, TOKEN_EXPIRY_DURATION));
  }

  @Override
  public void close() {
    DEFAULT_CREDENTIALS_PROVIDER.close();
  }
}