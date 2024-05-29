package com.github.gyrosofwar.imagehive;

import com.github.gyrosofwar.imagehive.dto.admin.UserCreateDTO;
import com.github.gyrosofwar.imagehive.service.UserService;
import com.github.gyrosofwar.imagehive.sql.Public;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;

@MicronautTest
public abstract class BaseTest {

  @Inject
  protected EmbeddedServer server;

  @Inject
  protected AppClient appClient;

  @Inject
  protected UserService userService;

  @Inject
  protected DSLContext dsl;

  protected final String username = "admin";
  protected final String password = "cool-password-123";

  protected Long userId = null;

  protected BearerAccessRefreshToken login() {
    return appClient.login(new UsernamePasswordCredentials(username, password));
  }

  protected void cleanUpDatabase() {
    Public.PUBLIC.getTables()
      .forEach(table -> {
        dsl.truncate(table).cascade().execute();
      });
  }

  @BeforeEach
  void beforeEach() {
    cleanUpDatabase();

    userId = userService.create(
      new UserCreateDTO(username, "example@example.com", password, true, false)
    );
  }
}
