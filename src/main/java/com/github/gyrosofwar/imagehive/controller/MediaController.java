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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Controller("/api/media")
@Secured({ SecurityRule.IS_ANONYMOUS })
public class MediaController {

  private static final Logger log = LoggerFactory.getLogger(MediaController.class);
  private final MediaService mediaService;

  public MediaController(MediaService mediaService) {
    this.mediaService = mediaService;
  }

  @Get("/{userId}/{uuid}")
  public HttpResponse<InputStream> getImageBytes(
    @PathVariable long userId,
    @PathVariable UUID uuid,
    @QueryValue String extension
  ) throws IOException {
    log.info("getting image {}", uuid);
    var inputStream = mediaService.getImageBytes(
      Ulid.from(uuid),
      extension,
      userId
    );
    if (inputStream == null) {
      log.info("no image found for uuid {}", uuid);
      return HttpResponse.notFound();
    } else {
      return HttpResponse.ok(inputStream);
    }
  }
}
