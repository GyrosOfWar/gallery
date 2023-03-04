package com.github.gyrosofwar.imagehive.dto;

import io.micronaut.core.annotation.Nullable;
import java.util.UUID;
import javax.validation.constraints.NotNull;

public record ImageUpdateDTO(
  @NotNull UUID uuid,
  @Nullable String title,
  @Nullable String description,
  @Nullable String[] tags
) {}
