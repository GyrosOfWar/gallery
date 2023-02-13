package com.github.gyrosofwar.imagehive.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.*;

import com.github.gyrosofwar.imagehive.BaseTest;
import com.github.gyrosofwar.imagehive.dto.UserCreateDTO;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

@MicronautTest
class UserServiceTest extends BaseTest {

  @Test
  void testCreateUser() {
    final var username = "new-admin";

    userService.create(
      new UserCreateDTO(username, "test@example.com", "cool-password", true, false)
    );
    assertEquals(2, userService.getUserCount());
    var user = userService.getByNameOrEmail(username);
    assertEquals(username, user.username());
    assertEquals("test@example.com", user.email());
    assertEquals(true, user.admin());
    assertEquals(2, user.id());
    assertThat(user.createdOn()).isCloseToUtcNow(within(1, ChronoUnit.SECONDS));
  }
}
