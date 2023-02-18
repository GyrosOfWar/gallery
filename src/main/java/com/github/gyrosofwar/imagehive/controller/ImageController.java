package com.github.gyrosofwar.imagehive.controller;

import static com.github.gyrosofwar.imagehive.controller.ControllerHelper.getUserId;

import com.drew.imaging.ImageProcessingException;
import com.github.gyrosofwar.imagehive.dto.ImageDTO;
import com.github.gyrosofwar.imagehive.service.ImageService;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.multipart.StreamingFileUpload;
import io.micronaut.http.server.multipart.MultipartBody;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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

  @Put(consumes = MediaType.MULTIPART_FORM_DATA)
  public HttpResponse<ImageDTO> uploadImages(
    StreamingFileUpload file,
    Authentication authentication
  ) throws ImageProcessingException, IOException {
    var userId = (Long) authentication.getAttributes().get("userId");
    var createdImage = imageService.create(file, userId);
    return HttpResponse.ok(createdImage);
  }
}
