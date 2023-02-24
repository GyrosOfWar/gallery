package com.github.gyrosofwar.imagehive.service;

import static com.github.gyrosofwar.imagehive.sql.Tables.*;

import com.github.gyrosofwar.imagehive.dto.AlbumDTO;
import com.github.gyrosofwar.imagehive.dto.CreateAlbumDTO;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.Album;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.AlbumImage;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import java.time.OffsetDateTime;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AlbumService {

  private static final Logger log = LoggerFactory.getLogger(AlbumService.class);

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
      .columns(ALBUM.NAME, ALBUM.DESCRIPTION, ALBUM.OWNER_ID, ALBUM.TAGS, ALBUM.CREATED_ON)
      .values(
        albumDTO.name(),
        albumDTO.description(),
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

  public AlbumDTO getAlbumWithImages(long id, Long userId) {
    if (userId == null) {
      return null;
    }

    var rows = dsl
      .select(ALBUM.asterisk(), IMAGE.asterisk())
      .from(ALBUM)
      .innerJoin(ALBUM_IMAGE)
      .on(ALBUM_IMAGE.ALBUM_ID.eq(ALBUM.ID))
      .innerJoin(IMAGE)
      .on(ALBUM_IMAGE.IMAGE_ID.eq(IMAGE.ID))
      .where(ALBUM.ID.eq(id).and(ALBUM.OWNER_ID.eq(userId)))
      .fetchInto(AlbumRow.class);
    log.info("returned rows {}", rows);

    throw new NotImplementedException();
  }

  private record AlbumRow() {}
}
