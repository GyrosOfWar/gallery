package com.github.gyrosofwar.imagehive.dto.image;

import io.micronaut.core.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ImageDTO(
  UUID id,
  @Nullable String title,
  @Nullable String description,
  int height,
  int width,
  OffsetDateTime createdOn,
  @Nullable OffsetDateTime capturedOn,
  @Nullable Double latitude,
  @Nullable Double longitude,
  @Nullable List<String> tags,
  String extension,
  @Nullable ImageMetadata metadata
) {}
