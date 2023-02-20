package com.github.gyrosofwar.imagehive.controller;

import com.github.gyrosofwar.imagehive.dto.UserCreateDTO;
import com.github.gyrosofwar.imagehive.service.UserService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Secured({ SecurityRule.IS_AUTHENTICATED, "ADMIN" })
@Controller("/api/admin/user")
public class AdminUserController {

  private static final Logger log = LoggerFactory.getLogger(
    AdminUserController.class
  );
  private final UserService userService;

  public AdminUserController(UserService userService) {
    this.userService = userService;
  }

  @Post("/create")
  public HttpResponse<HttpStatus> create(@Body UserCreateDTO userCreate) {
    try {
      userService.create(userCreate);
      return HttpResponse.ok();
    } catch (Exception e) {
      log.error("Error creating user", e);
    }
    return HttpResponse.serverError();
  }
}
