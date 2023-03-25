package com.github.gyrosofwar.imagehive.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.annotation.Nullable;
import java.net.URI;

@ConfigurationProperties("imagehive")
public record ImageHiveConfiguration(
  String imageBasePath,
  @Nullable LavisServiceConfiguration lavis,
  @Nullable ImgProxyConfiguration imgProxy
) {
  @ConfigurationProperties("lavis")
  public record LavisServiceConfiguration(boolean enabled, @Nullable URI url) {}

  @ConfigurationProperties("img-proxy")
  public record ImgProxyConfiguration(boolean enabled, URI uri, String key, String salt) {}
}
