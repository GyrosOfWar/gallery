package com.github.gyrosofwar.imagehive.service.image;

import java.io.InputStream;
import java.util.List;

public record NewImage(
  InputStream inputStream,
  Long userId,
  String fileName,
  String mimeType,
  String title,
  String description,
  List<String> tags,
  boolean geoCodeLocation
) {}
