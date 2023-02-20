package com.github.gyrosofwar.imagehive;

import com.github.gyrosofwar.imagehive.dto.UserCreateDTO;
import com.github.gyrosofwar.imagehive.service.UserService;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;

@MicronautTest
public abstract class BaseTest {

  @Inject
  protected EmbeddedServer server;

  @Inject
  protected AppClient appClient;

  @Inject
  protected UserService userService;

  protected final String username = "admin";
  protected final String password = "cool-password-123";
  protected String authHeader;

  protected BearerAccessRefreshToken login() {
    return appClient.login(new UsernamePasswordCredentials(username, password));
  }

  @BeforeEach
  void beforeEach() {
    if (userService.getUserCount() == 0) {
      userService.create(
        new UserCreateDTO(
          username,
          "example@example.com",
          password,
          true,
          false
        )
      );
    }
    // var token = login().getAccessToken();
    // authHeader = String.format("Bearer %s", token);
  }
}
