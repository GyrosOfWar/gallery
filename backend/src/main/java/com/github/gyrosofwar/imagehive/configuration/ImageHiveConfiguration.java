package com.github.gyrosofwar.imagehive.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.annotation.Nullable;
import java.net.URI;

@ConfigurationProperties("imagehive.application")
public record ImageHiveConfiguration(
  String imageBasePath,
  @Nullable LavisServiceConfiguration lavis
) {
  public record LavisServiceConfiguration(boolean enabled, @Nullable URI url) {}
}
