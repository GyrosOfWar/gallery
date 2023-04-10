package com.github.gyrosofwar.imagehive.dto.image;

import io.micronaut.core.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ImageListDTO(
  UUID id,
  int height,
  int width,
  OffsetDateTime createdOn,
  @Nullable OffsetDateTime capturedOn,
  String extension,
  boolean favorite
) {}
