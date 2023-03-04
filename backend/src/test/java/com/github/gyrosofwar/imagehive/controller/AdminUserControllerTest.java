package com.github.gyrosofwar.imagehive.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.gyrosofwar.imagehive.BaseTest;
import com.github.gyrosofwar.imagehive.dto.user.UserCreateDTO;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

@MicronautTest
class AdminUserControllerTest extends BaseTest {

  @Test
  void testCreateUser() {
    var body = new UserCreateDTO("cool-user", "example@email.com", "random", false, false);

    var token = appClient.login(new UsernamePasswordCredentials(username, password));
    var header = String.format("Bearer %s", token.getAccessToken());
    var response = appClient.adminCreateUser(body, header);
    assertEquals(200, response.code());
  }
}
