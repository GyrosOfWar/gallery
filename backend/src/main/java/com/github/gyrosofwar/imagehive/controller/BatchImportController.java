package com.github.gyrosofwar.imagehive.controller;

import static com.github.gyrosofwar.imagehive.controller.ControllerHelper.getUserId;
import static com.github.gyrosofwar.imagehive.factory.ImageHiveFactory.ZIP_UPLOAD_SERVICE;

import com.github.f4b6a3.ulid.Ulid;
import com.github.gyrosofwar.imagehive.helper.TaskHelper;
import com.github.gyrosofwar.imagehive.service.importer.GoogleTakeoutImporter;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/api/batch-import")
@Secured({ SecurityRule.IS_AUTHENTICATED })
public class BatchImportController extends AbstractUploadController {

  private static final Logger log = LoggerFactory.getLogger(BatchImportController.class);

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
        var uploadId = uploadInfo.getMetadata().get("id");
        var tempFile = getUploadFolder(uploadId).resolve(uploadInfo.getFileName());
        log.info("Writing takeout archive to {}", tempFile);

        try (var outputStream = Files.newOutputStream(tempFile); inputStream) {
          inputStream.transferTo(outputStream);
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  private Path getUploadFolder(String uploadId) throws IOException {
    var path = Path.of("upload-temp", uploadId);
    if (!Files.isDirectory(path)) {
      Files.createDirectories(path);
    }
    return path;
  }

  @Override
  protected TusFileUploadService uploadService() {
    return fileUploadService;
  }

  @Get(value = "/start", produces = MediaType.APPLICATION_JSON)
  public NewBatchUpload startBatch() {
    return new NewBatchUpload(Ulid.fast().toString());
  }

  @Post("{uploadId}/finish")
  public void finishBatch(@PathVariable String uploadId, Authentication authentication) {
    TaskHelper.runInBackground(() -> {
      try {
        log.info("kicking off import for upload ID {}", uploadId);
        var folder = getUploadFolder(uploadId).toAbsolutePath();
        log.info("found folder {}", folder);
        var zipFiles = Files
          .list(folder)
          .filter(p -> p.getFileName().toString().endsWith(".zip"))
          .toList();
        log.info("found zip files {}", zipFiles);

        googleTakeoutImporter.importBatch(zipFiles, uploadId, getUserId(authentication));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Post("/upload")
  @Hidden
  public void postUpload(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication
  ) throws IOException, TusException {
    handleTusUpload(request, response, authentication);
  }

  @Patch(value = "/upload/{id}", consumes = "application/offset+octet-stream")
  @Hidden
  public void patchUpload(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication,
    @PathVariable String id
  ) throws IOException, TusException {
    handleTusUpload(request, response, authentication);
  }

  @Head("/upload/{id}")
  @Hidden
  public void headUpload(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication,
    @Nullable @PathVariable String id
  ) throws IOException, TusException {
    handleTusUpload(request, response, authentication);
  }

  @Delete("/upload/{id}")
  @Hidden
  public void deleteUpload(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication,
    @Nullable @PathVariable String id
  ) throws IOException, TusException {
    handleTusUpload(request, response, authentication);
  }

  record NewBatchUpload(String id) {}
}
