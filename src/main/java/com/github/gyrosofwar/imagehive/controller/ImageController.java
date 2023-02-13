package com.github.gyrosofwar.imagehive.controller;

import com.drew.imaging.ImageProcessingException;
import com.github.gyrosofwar.imagehive.dto.ImageDTO;
import com.github.gyrosofwar.imagehive.service.ImageService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.multipart.StreamingFileUpload;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

@Controller("/api/images")
@Secured({ SecurityRule.IS_AUTHENTICATED })
public class ImageController {

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
  public List<ImageDTO> getImages() {
    return imageService.listImages();
  }

  @Put(consumes = MediaType.MULTIPART_FORM_DATA)
  @Transactional
  public HttpResponse<Void> uploadImages(
    Publisher<StreamingFileUpload> files,
    Authentication authentication
  ) throws ImageProcessingException, IOException {
    var fileUploads = Flux.from(files).collectList().block();
    if (fileUploads != null) {
      for (var file : fileUploads) {
        // TODO
        imageService.create(file, 1L);
      }
    }

    return HttpResponse.ok();
  }
}
