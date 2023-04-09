package com.github.gyrosofwar.imagehive.service.importer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipHelper {

  private static final Logger log = LoggerFactory.getLogger(ZipHelper.class);

  public static void unzip(Path zipFilePath, Path destDir) throws IOException {
    log.info("Unzipping archive {} to {}", zipFilePath, destDir);
    if (!Files.isDirectory(destDir)) {
      Files.createDirectories(destDir);
    }

    try (
      var inputStream = Files.newInputStream(zipFilePath);
      var zipInputStream = new ZipInputStream(inputStream)
    ) {
      ZipEntry entry;

      while ((entry = zipInputStream.getNextEntry()) != null) {
        var fileName = entry.getName();
        var newFile = destDir.resolve(fileName);
        Files.createDirectories(newFile.getParent());

        try (var outputStream = Files.newOutputStream(newFile)) {
          zipInputStream.transferTo(outputStream);
        }
      }

      zipInputStream.closeEntry();
    }
  }
}
