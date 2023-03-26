package com.github.gyrosofwar.imagehive.controller;

import static com.github.gyrosofwar.imagehive.controller.ControllerHelper.getUserId;

import com.github.f4b6a3.ulid.Ulid;
import com.github.gyrosofwar.imagehive.service.MediaService;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Hidden;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import me.desair.tus.server.HttpHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/api/media")
@Secured({ SecurityRule.IS_ANONYMOUS })
public class MediaController {

  private static final Logger log = LoggerFactory.getLogger(MediaController.class);
  private final MediaService mediaService;

  public MediaController(MediaService mediaService) {
    this.mediaService = mediaService;
  }

  @Get("{uuid}")
  @Hidden
  public StreamedFile getImageBytes(
    @PathVariable UUID uuid,
    @QueryValue String extension,
    Authentication authentication
  ) throws IOException {
    log.info("getting image {}", uuid);
    var userId = getUserId(authentication);
    var imageData = mediaService.getImageBytes(Ulid.from(uuid), extension, userId);
    if (imageData == null) {
      log.info("no image found for uuid {}", uuid);
      return null;
    } else {
      return new StreamedFile(
        imageData.inputStream(),
        imageData.contentType(),
        imageData.lastModified(),
        imageData.contentLength()
      );
    }
  }

  @Get("/thumbnail/{width}/{height}/{uuid}")
  @Hidden
  public HttpResponse<StreamedFile> getThumbnail(
    @PathVariable int width,
    @PathVariable int height,
    @PathVariable UUID uuid,
    @QueryValue String extension,
    HttpHeaders httpHeaders,
    Authentication authentication
  ) throws IOException {
    log.debug("getting image thumbnail {}, ({}x{} px)", uuid, width, height);
    var userId = getUserId(authentication);

    var imageData = mediaService.getImageThumbnail(
      Ulid.from(uuid),
      extension,
      width,
      height,
      null,
      httpHeaders,
      userId
    );
    if (imageData == null) {
      log.info("no image found for uuid {}", uuid);
      return HttpResponse.notFound();
    } else {
      var file = new StreamedFile(
        imageData.inputStream(),
        imageData.contentType(),
        imageData.lastModified(),
        imageData.contentLength()
      );
      return HttpResponse.ok(file).headers(imageData.headers());
    }
  }
}
