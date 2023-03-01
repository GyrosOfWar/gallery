package com.github.gyrosofwar.imagehive.controller;

import static com.github.gyrosofwar.imagehive.controller.ControllerHelper.getUserId;

import com.github.gyrosofwar.imagehive.converter.ImageDTOConverter;
import com.github.gyrosofwar.imagehive.dto.ImageDTO;
import com.github.gyrosofwar.imagehive.service.image.ImageService;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import java.util.UUID;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/api/images")
@Secured({ SecurityRule.IS_AUTHENTICATED })
public class ImageController {

  private static final Logger log = LoggerFactory.getLogger(ImageController.class);

  private final ImageService imageService;
  private final ImageDTOConverter imageDTOConverter;

  public ImageController(ImageService imageService, ImageDTOConverter imageDTOConverter) {
    this.imageService = imageService;
    this.imageDTOConverter = imageDTOConverter;
  }

  @Get(produces = MediaType.APPLICATION_JSON, uri = "/{uuid}")
  public ImageDTO getImage(@PathVariable UUID uuid, Authentication authentication) {
    var userId = getUserId(authentication);
    var image = imageService.getByUuid(uuid, userId);
    if (image == null) {
      return null;
    }
    return imageDTOConverter.convert(image);
  }

  @Delete("/{uuid}")
  public HttpResponse<Void> deleteImage(@PathVariable UUID uuid, Authentication authentication) {
    var userId = getUserId(authentication);
    imageService.delete(uuid, userId);
    return HttpResponse.noContent();
  }

  @Get(produces = MediaType.APPLICATION_JSON)
  @Transactional
  public Page<ImageDTO> getImages(
    Pageable pageable,
    Authentication authentication,
    @QueryValue @Nullable String query
  ) {
    var userId = getUserId(authentication);
    if (userId == null) {
      return Page.empty();
    } else {
      log.info("fetching images for user {} and page {}", userId, pageable);
      return imageService.listImages(query, pageable, userId);
    }
  }
}
