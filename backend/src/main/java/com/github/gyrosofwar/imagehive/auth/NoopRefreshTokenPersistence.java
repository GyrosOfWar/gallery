package com.github.gyrosofwar.imagehive.auth;

import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.event.RefreshTokenGeneratedEvent;
import io.micronaut.security.token.refresh.RefreshTokenPersistence;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;

@Singleton
public class NoopRefreshTokenPersistence implements RefreshTokenPersistence {

  @Override
  public void persistToken(RefreshTokenGeneratedEvent event) {}

  @Override
  public Publisher<Authentication> getAuthentication(String refreshToken) {
    return null;
  }
}
