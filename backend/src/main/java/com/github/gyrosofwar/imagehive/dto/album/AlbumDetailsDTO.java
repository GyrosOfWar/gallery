package com.github.gyrosofwar.imagehive.dto.album;

import com.github.gyrosofwar.imagehive.sql.tables.pojos.Album;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public record AlbumDetailsDTO(
  long id,
  String name,
  String description,
  OffsetDateTime createdOn,
  List<String> tags,
  UUID thumbnailImage
) {
  public static AlbumDetailsDTO from(Album album) {
    return new AlbumDetailsDTO(
      album.id(),
      album.name(),
      album.description(),
      album.createdOn(),
      Arrays.asList(album.tags()),
      album.thumbnailId()
    );
  }
}
