package com.github.gyrosofwar.imagehive.service;

import static com.github.gyrosofwar.imagehive.sql.Tables.ALBUM;
import static com.github.gyrosofwar.imagehive.sql.Tables.ALBUM_IMAGE;

import com.github.gyrosofwar.imagehive.dto.AlbumDTO;
import com.github.gyrosofwar.imagehive.dto.CreateAlbumDTO;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.Album;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.AlbumImage;
import io.micronaut.data.model.Pageable;
import java.time.OffsetDateTime;
import java.util.List;
import org.jooq.DSLContext;

public class AlbumService {

  private final DSLContext dsl;

  public AlbumService(DSLContext dsl) {
    this.dsl = dsl;
  }

  public List<AlbumDTO> listAlbums(Pageable pageable, int numberOfImages, Long userId) {
    return dsl
      .selectFrom(ALBUM)
      .where(ALBUM.OWNER_ID.eq(userId))
      .orderBy(ALBUM.CREATED_ON.desc())
      .offset(pageable.getOffset())
      .limit(pageable.getSize())
      .fetchInto(Album.class)
      .stream()
      .map(album -> AlbumDTO.from(album, null))
      .toList();
  }

  public AlbumDTO createAlbum(CreateAlbumDTO albumDTO) {
    var album = dsl
      .insertInto(ALBUM)
      .columns(ALBUM.NAME, ALBUM.OWNER_ID, ALBUM.TAGS, ALBUM.CREATED_ON)
      .values(
        albumDTO.name(),
        albumDTO.ownerId(),
        albumDTO.tags().toArray(new String[0]),
        OffsetDateTime.now()
      )
      .returningResult()
      .fetchOneInto(Album.class);

    var records = albumDTO
      .imageIds()
      .stream()
      .map(imageId -> dsl.newRecord(ALBUM_IMAGE, new AlbumImage(album.id(), imageId)))
      .toList();

    dsl.batchInsert(records).execute();

    return AlbumDTO.from(album, null);
  }
}
