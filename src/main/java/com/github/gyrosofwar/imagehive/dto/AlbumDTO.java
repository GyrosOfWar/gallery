package com.github.gyrosofwar.imagehive.dto;

import com.github.gyrosofwar.imagehive.sql.tables.pojos.Album;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

public record AlbumDTO(
  long id,
  String name,
  String description,
  OffsetDateTime createdOn,
  long ownerId,
  List<String> tags,
  List<ImageDTO> images
) {
  public static AlbumDTO from(Album album, List<ImageDTO> images) {
    return new AlbumDTO(
      album.id(),
      album.name(),
      album.description(),
      album.createdOn(),
      album.ownerId(),
      Arrays.asList(album.tags()),
      images
    );
  }
}
