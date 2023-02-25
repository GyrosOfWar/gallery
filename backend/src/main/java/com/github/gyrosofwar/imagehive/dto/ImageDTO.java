package com.github.gyrosofwar.imagehive.dto;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Serdeable
public record ImageDTO(
  UUID id,
  @Nullable String title,
  int height,
  int width,
  OffsetDateTime createdOn,
  @Nullable List<String> tags,
  String extension
) {}
