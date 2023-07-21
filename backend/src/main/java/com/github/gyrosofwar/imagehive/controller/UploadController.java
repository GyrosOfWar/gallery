package com.github.gyrosofwar.imagehive.controller;

import static com.github.gyrosofwar.imagehive.controller.ControllerHelper.getUserId;
import static com.github.gyrosofwar.imagehive.factory.ImageHiveFactory.IMAGE_UPLOAD_SERVICE;

import com.github.gyrosofwar.imagehive.service.image.ImageCreationService;
import com.github.gyrosofwar.imagehive.service.image.ImageService;
import com.github.gyrosofwar.imagehive.service.image.NewImage;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/api/images/upload")
@Secured({ SecurityRule.IS_AUTHENTICATED })
public class UploadController extends AbstractUploadController {

  private static final Logger log = LoggerFactory.getLogger(UploadController.class);

  private final TusFileUploadService fileUploadService;
  private final ImageCreationService imageCreationService;
  private final ImageService imageService;

  public UploadController(
    @Named(IMAGE_UPLOAD_SERVICE) TusFileUploadService fileUploadService,
    ImageCreationService imageCreationService,
    ImageService imageService
  ) {
    this.fileUploadService = fileUploadService;
    this.imageCreationService = imageCreationService;
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

  @Override
  protected void handleUploadedFile(
    InputStream inputStream,
    UploadInfo uploadInfo,
    Authentication authentication
  ) throws Exception {
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
      tags,
      true
    );
    var image = imageCreationService.create(newImage);
    imageService.setGeneratedDescriptionAsync(image);
  }

  @Override
  protected TusFileUploadService uploadService() {
    return fileUploadService;
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
