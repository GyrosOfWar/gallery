package com.github.gyrosofwar.imagehive.controller;

import static com.github.gyrosofwar.imagehive.controller.ControllerHelper.getUserId;

import com.github.gyrosofwar.imagehive.dto.ImageDTO;
import com.github.gyrosofwar.imagehive.service.ImageService;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/api/images")
@Secured({ SecurityRule.IS_AUTHENTICATED })
public class ImageController {

  private static final Logger log = LoggerFactory.getLogger(ImageController.class);

  private final ImageService imageService;

  public ImageController(ImageService imageService) {
    this.imageService = imageService;
  }

  @Get(produces = MediaType.APPLICATION_JSON, uri = "/{uuid}")
  @Transactional
  public ImageDTO getImage(@PathVariable UUID uuid) {
    var image = imageService.getByUuid(uuid);
    if (image == null) {
      return null;
    }
    return imageService.toDto(image);
  }

  @Get(produces = MediaType.APPLICATION_JSON)
  @Transactional
  public List<ImageDTO> getImages(
    Pageable pageable,
    Authentication authentication,
    @QueryValue @Nullable String query
  ) {
    var userId = getUserId(authentication);
    if (userId == null) {
      return List.of();
    } else {
      return imageService.listImages(query, pageable, userId);
    }
  }
}
