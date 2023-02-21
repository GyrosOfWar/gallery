package com.github.gyrosofwar.imagehive.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("imagehive.smtpmail")
public record SmtpMailConfiguration(
  String host,
  String port,
  boolean auth,
  String username,
  String password
) {}
