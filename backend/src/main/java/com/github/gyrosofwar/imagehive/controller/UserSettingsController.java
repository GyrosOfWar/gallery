package com.github.gyrosofwar.imagehive.controller;

import static com.github.gyrosofwar.imagehive.controller.ControllerHelper.getUserId;

import com.github.gyrosofwar.imagehive.dto.user.settings.UpdatePasswordDTO;
import com.github.gyrosofwar.imagehive.service.UserService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/api/user/settings")
@Secured({ SecurityRule.IS_AUTHENTICATED })
public class UserSettingsController {

  private static final Logger log = LoggerFactory.getLogger(UserSettingsController.class);
  private final UserService userService;

  public UserSettingsController(UserService userService) {
    this.userService = userService;
  }

  @Post("/updatePassword")
  public HttpResponse<HttpStatus> create(
    @Body UpdatePasswordDTO updatePassword,
    Authentication authentication
  ) {
    try {
      Long userId = getUserId(authentication);
      userService.updatePassword(userId, updatePassword);
      return HttpResponse.ok();
    } catch (Exception e) {
      log.error("Error updating password", e);
      return HttpResponse.serverError();
    }
  }
}
