package com.github.gyrosofwar.imagehive.controller;

import io.micronaut.security.authentication.Authentication;

public class ControllerHelper {

  private ControllerHelper() {}

  public static Long getUserId(Authentication authentication) {
    var obj = authentication.getAttributes().get("userId");
    if (obj instanceof Long id) {
      return id;
    } else {
      return null;
    }
  }
}
