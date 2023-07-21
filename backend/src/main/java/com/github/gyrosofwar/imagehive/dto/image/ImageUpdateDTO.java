package com.github.gyrosofwar.imagehive.dto.image;

import io.micronaut.core.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ImageUpdateDTO(
  @NotNull UUID uuid,
  @Nullable String title,
  @Nullable String description,
  @Nullable String[] tags
) {}
