package com.github.gyrosofwar.imagehive.service.thumbnails;

import com.github.gyrosofwar.imagehive.service.ImageData;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

@Singleton
@Requires(property = "imagehive.thumbnailer", value = "JAVA")
public class JavaThumbnailer implements Thumbnailer {

  private static final RenameStrategy RENAME_STRATEGY = new RenameStrategy();

  private static class RenameStrategy extends Rename {

    public String getName(String name, int width, int height) {
      return appendSuffix(name, String.format(".thumbnail-%s-%s", width, height));
    }

    @Override
    public String apply(String name, ThumbnailParameter param) {
      var width = (int) Math.round(param.getSize().getWidth());
      var height = (int) Math.round(param.getSize().getHeight());
      return getName(name, width, height);
    }
  }

  @Override
  public ImageData getThumbnail(Request request) throws IOException {
    // TODO use aspect ratio to calculate missing dimension
    var height = request.height() == null ? request.width() : request.height();

    var fileName = Path.of(
      RENAME_STRATEGY.getName(request.imagePath().getFileName().toString(), request.width(), height)
    );
    var existingFile = request.imagePath().getParent().resolve(fileName);
    if (Files.isRegularFile(existingFile)) {
      return ImageData.from(existingFile);
    }

    var image = Thumbnails
      .of(request.imagePath().toFile())
      // webp and avif aren't supported
      .outputFormat("jpg")
      .size(request.width(), height)
      .asFiles(RENAME_STRATEGY)
      .get(0);

    return ImageData.from(image.toPath());
  }
}
