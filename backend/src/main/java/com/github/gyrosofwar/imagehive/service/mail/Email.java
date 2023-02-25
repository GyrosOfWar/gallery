package com.github.gyrosofwar.imagehive.service.mail;

import io.micronaut.core.annotation.NonNull;

public record Email(
  @NonNull String recipient,
  String cc,
  String bcc,
  @NonNull String subject,
  String htmlBody,
  String textBody,
  @NonNull String from
) {}
