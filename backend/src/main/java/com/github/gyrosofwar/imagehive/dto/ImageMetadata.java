package com.github.gyrosofwar.imagehive.dto;

import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import java.util.Map;

@Serdeable
public record ImageMetadata(
  @Nullable String camera,
  @Nullable String lens,
  @Nullable String exposure,
  @Nullable String focalLength,
  @Nullable String aperture,
  @Nullable String iso
) {
  public static ImageMetadata from(Map<String, Map<String, String>> metadata) {
    if (metadata == null) {
      return null;
    }

    var exifIfd0 = metadata.get(new ExifIFD0Directory().getName());
    if (exifIfd0 == null) {
      return null;
    }
    var cameraMake = exifIfd0.get("Make");
    var cameraModel = exifIfd0.get("Model");
    var camera = String.format("%s %s", cameraMake, cameraModel);

    var exifSubIfd0 = metadata.get(new ExifSubIFDDirectory().getName());
    var lensMake = exifSubIfd0.get("Lens Make");
    var lensModel = exifSubIfd0.get("Lens Model");
    var lens = String.format("%s %s", lensMake, lensModel);

    var exposure = exifSubIfd0.get("Exposure Time");
    var focalLength = exifSubIfd0.get("Focal Length");
    var aperture = exifSubIfd0.get("Aperture Value");
    var iso = exifSubIfd0.get("ISO Speed Ratings");

    return new ImageMetadata(camera, lens, exposure, focalLength, aperture, iso);
  }
}
