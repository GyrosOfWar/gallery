package com.github.gyrosofwar.imagehive.auth;

import com.github.gyrosofwar.imagehive.service.UserService;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Singleton
public class UserPasswordAuthenticationProvider implements AuthenticationProvider<HttpRequest<?>> {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  public UserPasswordAuthenticationProvider(
    UserService userService,
    PasswordEncoder passwordEncoder
  ) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public Publisher<AuthenticationResponse> authenticate(
    @Nullable HttpRequest<?> httpRequest,
    AuthenticationRequest<?, ?> authenticationRequest
  ) {
    String identity = authenticationRequest.getIdentity().toString();
    String secret = authenticationRequest.getSecret().toString();
    return Mono
      .fromSupplier(() -> userService.getByNameOrEmail(identity))
      .flatMap(user -> {
        if (user != null && passwordEncoder.matches(secret, user.passwordHash())) {
          var roles = List.of(user.admin() ? UserRole.ADMIN.name() : UserRole.USER.name());
          Map<String, Object> attributes = Map.of("userId", user.id(), "email", user.email());
          return Mono.just(AuthenticationResponse.success(identity, roles, attributes));
        } else {
          return Mono.error(AuthenticationResponse.exception());
        }
      })
      .subscribeOn(Schedulers.boundedElastic());
  }
}
