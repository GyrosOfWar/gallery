package com.github.gyrosofwar.imagehive.controller;

import static com.github.gyrosofwar.imagehive.controller.ControllerHelper.getUserId;

import com.github.f4b6a3.ulid.Ulid;
import com.github.gyrosofwar.imagehive.service.MediaService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Controller("/api/media")
@Secured({ SecurityRule.IS_AUTHENTICATED })
public class MediaController {

  private final MediaService mediaService;

  public MediaController(MediaService mediaService) {
    this.mediaService = mediaService;
  }

  @Get("{uuid}")
  public HttpResponse<InputStream> getImageBytes(
    @PathVariable UUID uuid,
    @QueryValue String extension,
    Authentication authentication
  ) throws IOException {
    var inputStream = mediaService.getImageBytes(
      Ulid.from(uuid),
      extension,
      getUserId(authentication)
    );
    if (inputStream == null) {
      return HttpResponse.notFound();
    } else {
      return HttpResponse.ok(inputStream);
    }
  }
}
