package com.github.gyrosofwar.imagehive.service;

import static com.github.gyrosofwar.imagehive.sql.Tables.*;

import com.github.gyrosofwar.imagehive.dto.album.AlbumDetailsDTO;
import com.github.gyrosofwar.imagehive.dto.album.AlbumListDTO;
import com.github.gyrosofwar.imagehive.dto.album.CreateAlbumDTO;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.Album;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.AlbumImage;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
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

  @Transactional
  public List<AlbumListDTO> listAlbums(Pageable pageable, Long userId) {
    return dsl
      .selectFrom(ALBUM)
      .where(ALBUM.OWNER_ID.eq(userId))
      .orderBy(ALBUM.CREATED_ON.desc())
      .offset(pageable.getOffset())
      .limit(pageable.getSize())
      .fetchInto(Album.class)
      .stream()
      .map(album -> AlbumListDTO.from(album))
      .toList();
  }

  @Transactional
  public AlbumDetailsDTO createAlbum(CreateAlbumDTO albumDTO, long userId) {
    var album = dsl
      .insertInto(ALBUM)
      .columns(ALBUM.NAME, ALBUM.DESCRIPTION, ALBUM.OWNER_ID, ALBUM.TAGS, ALBUM.CREATED_ON)
      .values(
        albumDTO.name(),
        albumDTO.description(),
        userId,
        albumDTO.tags().toArray(new String[0]),
        OffsetDateTime.now()
      )
      .returningResult()
      .fetchOneInto(Album.class);

    return AlbumDetailsDTO.from(album, List.of());
  }

  @Transactional
  public AlbumDetailsDTO getAlbumWithImages(long id, Long userId) {
    if (userId == null) {
      return null;
    }

    var rows = dsl
      .select(
        ALBUM.NAME,
        ALBUM.ID.as("albumId"),
        ALBUM.TAGS,
        ALBUM.DESCRIPTION,
        ALBUM.CREATED_ON,
        ALBUM.THUMBNAIL_ID,
        IMAGE.ID.as("imageId"),
        IMAGE.FILE_PATH
      )
      .from(ALBUM)
      .innerJoin(ALBUM_IMAGE)
      .on(ALBUM_IMAGE.ALBUM_ID.eq(ALBUM.ID))
      .innerJoin(IMAGE)
      .on(ALBUM_IMAGE.IMAGE_ID.eq(IMAGE.ID))
      .where(ALBUM.ID.eq(id).and(ALBUM.OWNER_ID.eq(userId)))
      .fetchInto(AlbumRow.class);
    log.info("returned rows {}", rows);

    if (rows.isEmpty()) {
      return null;
    }

    var album = rows.get(0);
    var imageIds = rows.stream().map(AlbumRow::imageId).toList();
    return new AlbumDetailsDTO(
      album.albumId(),
      album.name(),
      album.description(),
      album.createdOn(),
      List.of(album.tags()),
      imageIds,
      album.thumbnailId()
    );
  }

  @Transactional
  public boolean isAlbumOwner(long albumId, long userId) {
    var count = dsl
      .selectCount()
      .from(ALBUM)
      .where(ALBUM.ID.eq(albumId).and(ALBUM.OWNER_ID.eq(userId)))
      .fetchOne()
      .value1();
    return count == 1;
  }

  @Transactional
  public void addImages(long id, Set<UUID> imageIds, Long userId) {
    if (userId == null || !isAlbumOwner(id, userId)) {
      // todo throw the right exception
      return;
    }

    var records = imageIds
      .stream()
      .map(imageId -> dsl.newRecord(ALBUM_IMAGE, new AlbumImage(id, imageId)))
      .toList();

    dsl.batchInsert(records).execute();
  }

  private record AlbumRow(
    String name,
    long albumId,
    String[] tags,
    String description,
    OffsetDateTime createdOn,
    UUID imageId,
    String filePath,
    UUID thumbnailId
  ) {}
}
