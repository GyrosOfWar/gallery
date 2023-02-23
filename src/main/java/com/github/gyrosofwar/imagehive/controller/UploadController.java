package com.github.gyrosofwar.imagehive.controller;

import static com.github.gyrosofwar.imagehive.controller.ControllerHelper.getUserId;

import com.drew.imaging.ImageProcessingException;
import com.github.gyrosofwar.imagehive.service.ImageService;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/api/images/upload")
@Secured({ SecurityRule.IS_AUTHENTICATED })
public class UploadController {

  private static final Logger log = LoggerFactory.getLogger(UploadController.class);

  private final TusFileUploadService fileUploadService;
  private final ImageService imageService;

  public UploadController(TusFileUploadService fileUploadService, ImageService imageService) {
    this.fileUploadService = fileUploadService;
    this.imageService = imageService;
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
        var title = uploadInfo.getMetadata().get("title");
        var description = uploadInfo.getMetadata().get("description");
        var tags = List.of(uploadInfo.getMetadata().get("tags").split("\\s+"));
        var newImage = new ImageService.NewImage(
          inputStream,
          getUserId(authentication),
          uploadInfo.getFileName(),
          uploadInfo.getFileMimeType(),
          title,
          description,
          tags
        );
        imageService.create(newImage);
      }
    } catch (IOException | TusException | ImageProcessingException e) {
      log.error("encountered upload error", e);
    } finally {
      fileUploadService.deleteUpload(uploadUri);
    }
  }

  @Post
  public void postUpload(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication
  ) throws IOException, TusException {
    handleTusUpload(request, response, authentication);
  }

  @Patch(value = "{id}", consumes = "application/offset+octet-stream")
  public void patchUpload(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication,
    @PathVariable String id
  ) throws IOException, TusException {
    handleTusUpload(request, response, authentication);
  }

  @Head("{id}")
  public void headUpload(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication,
    @Nullable @PathVariable String id
  ) throws IOException, TusException {
    handleTusUpload(request, response, authentication);
  }

  @Delete("{id}")
  public void deleteUpload(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication,
    @Nullable @PathVariable String id
  ) throws IOException, TusException {
    handleTusUpload(request, response, authentication);
  }
}
