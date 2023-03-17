package com.github.gyrosofwar.imagehive.service;

import io.micronaut.http.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public record ImageData(
  InputStream inputStream,
  MediaType contentType,
  FileTime lastModified,
  long contentLength
) {
  public static ImageData from(Path path) throws IOException {
    var inputStream = Files.newInputStream(path);
    var attributes = Files.readAttributes(path, BasicFileAttributes.class);
    var contentLength = attributes.size();
    var lastModified = attributes.lastModifiedTime();
    var contentType = MediaType.forFilename(path.getFileName().toString());

    return new ImageData(inputStream, contentType, lastModified, contentLength);
  }
}
