package com.github.gyrosofwar.imagehive.service.thumbnails;

import com.github.gyrosofwar.imagehive.service.ImageData;
import io.micronaut.core.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;

public interface Thumbnailer {
  ImageData getThumbnail(Request request) throws IOException;

  record Request(
    Path imagePath,
    int width,
    @Nullable Integer height,
    FileType fileType,
    @Nullable Boolean crop,
    int dpr
  ) {}

  enum FileType {
    WEBP,
    JPEG,
    AVIF,
  }
}
