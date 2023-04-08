package com.github.gyrosofwar.imagehive.service;

import static com.github.gyrosofwar.imagehive.sql.Tables.*;

import com.github.gyrosofwar.imagehive.converter.ImageDTOConverter;
import com.github.gyrosofwar.imagehive.dto.album.AlbumDetailsDTO;
import com.github.gyrosofwar.imagehive.dto.album.AlbumListDTO;
import com.github.gyrosofwar.imagehive.dto.album.CreateAlbumDTO;
import com.github.gyrosofwar.imagehive.dto.image.ImageDTO;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.Album;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.AlbumImage;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.Image;
import com.github.gyrosofwar.imagehive.sql.tables.records.AlbumImageRecord;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AlbumService {

  private static final Logger log = LoggerFactory.getLogger(AlbumService.class);

  private final DSLContext dsl;
  private final ImageDTOConverter imageDTOConverter;

  public AlbumService(DSLContext dsl, ImageDTOConverter imageDTOConverter) {
    this.dsl = dsl;
    this.imageDTOConverter = imageDTOConverter;
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
  public AlbumDetailsDTO createAlbum(CreateAlbumDTO albumDTO, Long userId) {
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

    return AlbumDetailsDTO.from(album);
  }

  @Transactional
  public AlbumDetailsDTO getAlbum(long id, Long userId) {
    if (userId == null) {
      return null;
    }

    var album = dsl
      .selectFrom(ALBUM)
      .where(ALBUM.ID.eq(id).and(ALBUM.OWNER_ID.eq(userId)))
      .fetchOneInto(Album.class);

    if (album == null) {
      return null;
    }

    return new AlbumDetailsDTO(
      album.id(),
      album.name(),
      album.description(),
      album.createdOn(),
      List.of(album.tags()),
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
  public void updateAlbumImages(long id, SortedSet<UUID> albumImages, Long userId) {
    if (userId == null || !isAlbumOwner(id, userId)) {
      // todo throw the right exception
      return;
    }
    log.info("updating album {} with images {}", id, albumImages);

    Set<UUID> existingImages = getImages(id, userId)
      .stream()
      .map(ImageDTO::id)
      .collect(Collectors.toSet());
    log.info("found {} existing images", existingImages.size());

    Set<UUID> idsToAdd = new HashSet<>(albumImages);
    idsToAdd.removeAll(existingImages);
    log.info("adding {} new images", idsToAdd.size());

    Set<UUID> idsToDelete = new HashSet<>(existingImages);
    idsToDelete.removeAll(albumImages);
    log.info("deleting {} existing images", idsToDelete.size());

    List<AlbumImageRecord> records = idsToAdd
      .stream()
      .map(imageId -> dsl.newRecord(ALBUM_IMAGE, new AlbumImage(id, imageId)))
      .toList();
    dsl.batchInsert(records).execute();

    dsl
      .deleteFrom(ALBUM_IMAGE)
      .where(ALBUM_IMAGE.IMAGE_ID.in(idsToDelete).and(ALBUM_IMAGE.ALBUM_ID.eq(id)))
      .execute();

    if (!albumImages.isEmpty()) {
      var first = albumImages.first();
      dsl.update(ALBUM).set(ALBUM.THUMBNAIL_ID, first).where(ALBUM.ID.eq(id)).execute();
    }
  }

  @Transactional
  public List<ImageDTO> getImages(long id, Long userId) {
    return dsl
      .select(IMAGE.asterisk())
      .from(ALBUM_IMAGE)
      .innerJoin(IMAGE)
      .on(ALBUM_IMAGE.IMAGE_ID.eq(IMAGE.ID))
      .where(ALBUM_IMAGE.ALBUM_ID.eq(id).and(IMAGE.OWNER_ID.eq(userId)))
      .fetchStreamInto(Image.class)
      .map(imageDTOConverter::convert)
      .toList();
  }
}
