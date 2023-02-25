package com.github.gyrosofwar.imagehive.controller;

import static com.github.gyrosofwar.imagehive.controller.ControllerHelper.getUserId;

import com.drew.imaging.ImageProcessingException;
import com.github.gyrosofwar.imagehive.service.ImageService;
import com.github.gyrosofwar.imagehive.service.NewImage;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Hidden;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
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

  private List<String> parseTags(@Nullable String input) {
    if (input == null) {
      return List.of();
    } else {
      var tokenizer = new StringTokenizer(input, " ,;");
      List<String> tokens = new ArrayList<>();
      while (tokenizer.hasMoreTokens()) {
        tokens.add(tokenizer.nextToken());
      }
      return tokens;
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
        var title = uploadInfo.getMetadata().get("title");
        var description = uploadInfo.getMetadata().get("description");
        var tagString = uploadInfo.getMetadata().get("tags");
        var tags = parseTags(tagString);
        var newImage = new NewImage(
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
  @Hidden
  public void postUpload(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication
  ) throws IOException, TusException {
    handleTusUpload(request, response, authentication);
  }

  @Patch(value = "{id}", consumes = "application/offset+octet-stream")
  @Hidden
  public void patchUpload(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication,
    @PathVariable String id
  ) throws IOException, TusException {
    handleTusUpload(request, response, authentication);
  }

  @Head("{id}")
  @Hidden
  public void headUpload(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication,
    @Nullable @PathVariable String id
  ) throws IOException, TusException {
    handleTusUpload(request, response, authentication);
  }

  @Delete("{id}")
  @Hidden
  public void deleteUpload(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication,
    @Nullable @PathVariable String id
  ) throws IOException, TusException {
    handleTusUpload(request, response, authentication);
  }
}
