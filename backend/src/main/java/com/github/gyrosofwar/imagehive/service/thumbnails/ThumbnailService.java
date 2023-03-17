package com.github.gyrosofwar.imagehive.service.thumbnails;

import com.github.f4b6a3.ulid.Ulid;
import com.github.gyrosofwar.imagehive.service.ImageData;
import io.micronaut.core.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import javax.validation.constraints.NotNull;

public interface ThumbnailService {
  ImageData getThumbnail(Request request) throws IOException;

  record Request(
    Path imagePath,
    int width,
    int height,
    @Nullable FileType fileType,
    @Nullable Boolean crop
  ) {}

  enum FileType {
    WEBP,
    JPEG,
    AVIF,
  }
}
