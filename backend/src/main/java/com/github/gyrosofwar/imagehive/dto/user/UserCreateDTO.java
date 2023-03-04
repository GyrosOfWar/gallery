package com.github.gyrosofwar.imagehive.dto.user;

public record UserCreateDTO(
  String username,
  String email,
  String password,
  boolean admin,
  boolean generatePassword
) {}
