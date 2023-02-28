package com.github.gyrosofwar.imagehive.dto;

public record UserCreateDTO(
  String username,
  String email,
  String password,
  boolean admin,
  boolean generatePassword
) {}
