package com.github.gyrosofwar.imagehive.controller;

import com.github.gyrosofwar.imagehive.dto.ImageDTO;
import com.github.gyrosofwar.imagehive.service.ImageService;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;

import javax.transaction.Transactional;
import java.util.UUID;

@Controller("/api/images")
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
}
