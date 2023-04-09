package com.github.gyrosofwar.imagehive.controller;

import static com.github.gyrosofwar.imagehive.controller.ControllerHelper.getUserId;
import static com.github.gyrosofwar.imagehive.factory.ImageHiveFactory.ZIP_UPLOAD_SERVICE;

import com.github.gyrosofwar.imagehive.helper.TaskHelper;
import com.github.gyrosofwar.imagehive.service.importer.GoogleTakeoutImporter;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.*;
import io.micronaut.security.authentication.Authentication;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;

@Controller("/api/batch-import")
public class BatchImportController extends AbstractUploadController {

  private final GoogleTakeoutImporter googleTakeoutImporter;
  private final TusFileUploadService fileUploadService;

  public BatchImportController(
    GoogleTakeoutImporter googleTakeoutImporter,
    @Named(ZIP_UPLOAD_SERVICE) TusFileUploadService fileUploadService
  ) {
    this.googleTakeoutImporter = googleTakeoutImporter;
    this.fileUploadService = fileUploadService;
  }

  @Override
  protected void handleUploadedFile(
    InputStream inputStream,
    UploadInfo uploadInfo,
    Authentication authentication
  ) {
    TaskHelper.runInBackground(() -> {
      try {
        var tempFile = Files.createTempFile("takeout-import", "zip");
        try (var outputStream = Files.newOutputStream(tempFile); inputStream) {
          inputStream.transferTo(outputStream);
        }

        googleTakeoutImporter.importBatch(tempFile, getUserId(authentication));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
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
