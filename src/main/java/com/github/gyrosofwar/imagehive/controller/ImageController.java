package com.github.gyrosofwar.imagehive.controller;

import static com.github.gyrosofwar.imagehive.controller.ControllerHelper.getUserId;

import com.drew.imaging.ImageProcessingException;
import com.github.gyrosofwar.imagehive.dto.ImageDTO;
import com.github.gyrosofwar.imagehive.service.ImageService;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
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
    return imageService.toDto(imageService.getByUuid(uuid));
  }

  @Get(produces = MediaType.APPLICATION_JSON)
  @Transactional
  public Page<ImageDTO> getImages(Pageable pageable, Authentication authentication) {
    var userId = getUserId(authentication);
    if (userId == null) {
      return Page.empty();
    } else {
      return imageService.listImages(pageable, userId);
    }
  }

}
