package com.github.gyrosofwar.imagehive.service;

import static org.junit.jupiter.api.Assertions.*;

import com.github.gyrosofwar.imagehive.dto.UserCreateDTO;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@MicronautTest
class UserServiceTest {

  @Inject
  UserService userService;

  @Test
  void testCreateUser() {
    userService.create(new UserCreateDTO("admin", "", "cool-password", true, false));
    assertEquals(1, userService.getUserCount());
  }
}
