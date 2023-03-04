package com.github.gyrosofwar.imagehive.dto.album;

import com.github.gyrosofwar.imagehive.sql.tables.pojos.Album;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AlbumListDTO(long id, String name, OffsetDateTime createdOn, UUID thumbnailImage) {
  public static AlbumListDTO from(Album album) {
    return new AlbumListDTO(album.id(), album.name(), album.createdOn(), album.thumbnailId());
  }
}
