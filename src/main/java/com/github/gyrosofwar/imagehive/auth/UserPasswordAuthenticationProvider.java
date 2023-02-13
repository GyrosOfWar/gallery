package com.github.gyrosofwar.imagehive.auth;

import static com.github.gyrosofwar.imagehive.sql.Tables.USER;

import com.github.gyrosofwar.imagehive.sql.tables.pojos.User;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Singleton;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Singleton
public class UserPasswordAuthenticationProvider implements AuthenticationProvider {

  private final DSLContext dsl;
  private final BCryptPasswordEncoderService encoderService;

  public UserPasswordAuthenticationProvider(
    DSLContext dsl,
    BCryptPasswordEncoderService encoderService
  ) {
    this.dsl = dsl;
    this.encoderService = encoderService;
  }

  @Override
  public Publisher<AuthenticationResponse> authenticate(
    @Nullable HttpRequest<?> httpRequest,
    AuthenticationRequest<?, ?> authenticationRequest
  ) {
    String identity = authenticationRequest.getIdentity().toString();
    try (SelectWhereStep<?> selectFrom = dsl.selectFrom(USER)) {
      User user = selectFrom
        .where(USER.USERNAME.eq(identity).or(USER.EMAIL.eq(identity)))
        .fetchOneInto(User.class);
      String secret = authenticationRequest.getSecret().toString();

      return Flux.create(
        emitter -> {
          if (user != null && encoderService.matches(secret, user.passwordHash())) {
            emitter.next(
              AuthenticationResponse.success((String) authenticationRequest.getIdentity())
            );
            emitter.complete();
          } else {
            emitter.error(AuthenticationResponse.exception());
          }
        },
        FluxSink.OverflowStrategy.ERROR
      );
    }
  }
}
