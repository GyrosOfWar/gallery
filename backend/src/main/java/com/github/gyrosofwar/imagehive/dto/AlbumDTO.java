package com.github.gyrosofwar.imagehive.dto;

import com.github.gyrosofwar.imagehive.sql.tables.pojos.Album;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public record AlbumDTO(
  long id,
  String name,
  String description,
  OffsetDateTime createdOn,
  List<String> tags,
  List<UUID> imageIds
) {
  public static AlbumDTO from(Album album, List<UUID> imageIds) {
    return new AlbumDTO(
      album.id(),
      album.name(),
      album.description(),
      album.createdOn(),
      Arrays.asList(album.tags()),
      imageIds
    );
  }
}
