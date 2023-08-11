package com.github.gyrosofwar.imagehive.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.github.gyrosofwar.imagehive.BaseTest;
import com.github.gyrosofwar.imagehive.dto.admin.UserCreateDTO;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

@MicronautTest
class UserServiceTest extends BaseTest {

  @Test
  void testGetUserCount() {
    final var username1 = "new-user1";
    final var username2 = "new-user2";

    userService.create(
      new UserCreateDTO(username1, "test@example.com", "cool-password", false, false)
    );

    assertEquals(2, userService.getUserCount());

    userService.create(
      new UserCreateDTO(username2, "test2@example.com", "cool-password", false, false)
    );

    assertEquals(3, userService.getUserCount());
  }

  @Test
  void testCreateUser() {
    final var username = "new-admin";

    assertNotEquals(
      0,
      userService.create(
        new UserCreateDTO(username, "test@example.com", "cool-password", true, false)
      )
    );
  }

  @Test
  void testDeleteById() {
    final var username = "new-admin";

    assertEquals(1, userService.getUserCount());

    long id = userService.create(
      new UserCreateDTO(username, "test@example.com", "cool-password", true, false)
    );

    assertEquals(2, userService.getUserCount());
    assertEquals(1, userService.deleteById(id));
    assertEquals(1, userService.getUserCount());
  }

  @Test
  void testDeleteByNameOrEmail() {
    final var username = "new-admin";
    final var email = "test@example.com";

    userService.create(new UserCreateDTO(username, email, "cool-password", true, false));

    assertEquals(2, userService.getUserCount());
    assertEquals(1, userService.deleteByNameOrEmail(username));
    assertEquals(1, userService.getUserCount());

    userService.create(new UserCreateDTO(username, email, "cool-password", true, false));

    assertEquals(2, userService.getUserCount());
    assertEquals(1, userService.deleteByNameOrEmail(email));
    assertEquals(1, userService.getUserCount());
  }

  @Test
  void testGetById() {
    final var username = "new-admin";

    long id = userService.create(
      new UserCreateDTO(username, "test@example.com", "cool-password", true, false)
    );

    var user = userService.getById(id);
    assertEquals(username, user.username());
    assertEquals("test@example.com", user.email());
    assertEquals(true, user.admin());
    assertEquals(id, user.id());
    assertThat(user.createdOn()).isCloseToUtcNow(within(1, ChronoUnit.SECONDS));
  }

  @Test
  void testGetByNameOrEmail() {
    final var username = "new-admin";

    userService.create(
      new UserCreateDTO(username, "test@example.com", "cool-password", true, false)
    );

    var user = userService.getByNameOrEmail(username);
    assertEquals(username, user.username());
    assertEquals("test@example.com", user.email());
    assertEquals(true, user.admin());
    assertThat(user.id()).isGreaterThanOrEqualTo(2);
    assertThat(user.createdOn()).isCloseToUtcNow(within(1, ChronoUnit.SECONDS));
  }
}
