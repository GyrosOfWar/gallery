package com.github.gyrosofwar.imagehive.auth;

import jakarta.inject.Singleton;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Singleton
public class BCryptPasswordEncoderService extends BCryptPasswordEncoder {

  @Override
  public String encode(CharSequence rawPassword) {
    return super.encode(rawPassword);
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    return super.matches(rawPassword, encodedPassword);
  }
}
