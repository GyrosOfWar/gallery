package com.github.gyrosofwar.imagehive.dto.user.settings;

public record UpdatePasswordDTO(
  String oldPassword,
  String newPassword,
  String confirmNewPassword
) {}
