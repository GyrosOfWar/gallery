package com.github.gyrosofwar.imagehive.dto;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Serdeable
public record ImageDTO(
  UUID id,
  int height,
  int width,
  OffsetDateTime createdOn,
  @Nullable List<String> tags
) {}
