package com.github.gyrosofwar.imagehive.controller;

import static com.github.gyrosofwar.imagehive.controller.ControllerHelper.getUserId;

import com.github.gyrosofwar.imagehive.service.ImageService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Controller("/api/media")
@Secured({ SecurityRule.IS_AUTHENTICATED })
public class MediaController {

  private final ImageService imageService;

  public MediaController(ImageService imageService) {
    this.imageService = imageService;
  }

  @Get("{uuid}")
  public HttpResponse<InputStream> getImageBytes(
    @PathVariable UUID uuid,
    Authentication authentication
  ) throws IOException {
    var inputStream = imageService.getImageBytes(uuid, getUserId(authentication));
    if (inputStream == null) {
      return HttpResponse.notFound();
    } else {
      return HttpResponse.ok(inputStream);
    }
  }
}
