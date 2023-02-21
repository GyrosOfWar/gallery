package com.github.gyrosofwar.imagehive.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("imagehive.application")
public record ImageHiveConfiguration(
  String imageBasePath
) {}
