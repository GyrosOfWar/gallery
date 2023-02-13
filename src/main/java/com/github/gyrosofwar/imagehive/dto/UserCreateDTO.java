package com.github.gyrosofwar.imagehive.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record UserCreateDTO(
  String username,
  String email,
  String password,
  boolean admin,
  boolean generatePassword
) {}
