package com.github.gyrosofwar.imagehive.controller;

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
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.UUID;

import static com.github.gyrosofwar.imagehive.controller.ControllerHelper.getUserId;

@Controller("/api/images")
@Secured({ SecurityRule.IS_AUTHENTICATED })
public class ImageController {

  private static final Logger log = LoggerFactory.getLogger(
    ImageController.class
  );

  private final ImageService imageService;
  private final TusFileUploadService fileUploadService;

  public ImageController(
    ImageService imageService,
    TusFileUploadService fileUploadService
  ) {
    this.imageService = imageService;
    this.fileUploadService = fileUploadService;
  }

  @Get(produces = MediaType.APPLICATION_JSON, uri = "/{uuid}")
  @Transactional
  public ImageDTO getImage(@PathVariable UUID uuid) {
    return imageService.toDto(imageService.getByUuid(uuid));
  }

  @Get(produces = MediaType.APPLICATION_JSON)
  @Transactional
  public Page<ImageDTO> getImages(
    Pageable pageable,
    Authentication authentication
  ) {
    var userId = getUserId(authentication);
    if (userId == null) {
      return Page.empty();
    } else {
      return imageService.listImages(pageable, userId);
    }
  }

  private void handleTusUpload(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication
  ) throws IOException, TusException {
    fileUploadService.process(request, response);

    var uploadUri = request.getRequestURI();
    try {
      var uploadInfo = fileUploadService.getUploadInfo(uploadUri);
      if (uploadInfo != null && !uploadInfo.isUploadInProgress()) {
        var inputStream = fileUploadService.getUploadedBytes(uploadUri);
        imageService.create(inputStream, uploadInfo, getUserId(authentication));
      }
    } catch (IOException | TusException | ImageProcessingException e) {
      log.error("encountered upload error", e);
    } finally {
      fileUploadService.deleteUpload(uploadUri);
    }
  }

  @Post("upload")
  public void postUpload(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication
  ) throws IOException, TusException {
    handleTusUpload(request, response, authentication);
  }

  @Patch("upload/{id}")
  public void patchUpload(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication,
    @Nullable @PathVariable String id
  ) throws IOException, TusException {
    handleTusUpload(request, response, authentication);
  }

  @Head("/upload/{id}")
  public void headUpload(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication,
    @Nullable @PathVariable String id
  ) throws IOException, TusException {
    handleTusUpload(request, response, authentication);
  }

  @Delete("upload/{id}")
  public void deleteUpload(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication,
    @Nullable @PathVariable String id
  ) throws IOException, TusException {
    handleTusUpload(request, response, authentication);
  }

}
