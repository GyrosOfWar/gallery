package com.github.gyrosofwar.imagehive.dto;

import io.micronaut.core.annotation.Nullable;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public record ImageUpdateDTO(
  @NotNull UUID uuid,
  @Nullable String title,
  @Nullable String description,
  @Nullable String[] tags
) {
}
