/*
 * This file is generated by jOOQ.
 */
package com.github.gyrosofwar.imagehive.sql.tables;

import com.github.gyrosofwar.imagehive.sql.Keys;
import com.github.gyrosofwar.imagehive.sql.Public;
import com.github.gyrosofwar.imagehive.sql.tables.records.ImageRecord;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function15;
import org.jooq.JSONB;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row15;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Image extends TableImpl<ImageRecord> {

  private static final long serialVersionUID = 1L;

  /**
   * The reference instance of <code>public.image</code>
   */
  public static final Image IMAGE = new Image();

  /**
   * The class holding records for this type
   */
  @Override
  public Class<ImageRecord> getRecordType() {
    return ImageRecord.class;
  }

  /**
   * The column <code>public.image.id</code>.
   */
  public final TableField<ImageRecord, UUID> ID = createField(
    DSL.name("id"),
    SQLDataType.UUID.nullable(false),
    this,
    ""
  );

  /**
   * The column <code>public.image.title</code>.
   */
  public final TableField<ImageRecord, String> TITLE = createField(
    DSL.name("title"),
    SQLDataType.VARCHAR,
    this,
    ""
  );

  /**
   * The column <code>public.image.description</code>.
   */
  public final TableField<ImageRecord, String> DESCRIPTION = createField(
    DSL.name("description"),
    SQLDataType.VARCHAR,
    this,
    ""
  );

  /**
   * The column <code>public.image.created_on</code>.
   */
  public final TableField<ImageRecord, OffsetDateTime> CREATED_ON = createField(
    DSL.name("created_on"),
    SQLDataType.TIMESTAMPWITHTIMEZONE(6).nullable(false),
    this,
    ""
  );

  /**
   * The column <code>public.image.captured_on</code>.
   */
  public final TableField<ImageRecord, OffsetDateTime> CAPTURED_ON = createField(
    DSL.name("captured_on"),
    SQLDataType.TIMESTAMPWITHTIMEZONE(6),
    this,
    ""
  );

  /**
   * The column <code>public.image.owner_id</code>.
   */
  public final TableField<ImageRecord, Long> OWNER_ID = createField(
    DSL.name("owner_id"),
    SQLDataType.BIGINT,
    this,
    ""
  );

  /**
   * The column <code>public.image.width</code>.
   */
  public final TableField<ImageRecord, Integer> WIDTH = createField(
    DSL.name("width"),
    SQLDataType.INTEGER.nullable(false),
    this,
    ""
  );

  /**
   * The column <code>public.image.height</code>.
   */
  public final TableField<ImageRecord, Integer> HEIGHT = createField(
    DSL.name("height"),
    SQLDataType.INTEGER.nullable(false),
    this,
    ""
  );

  /**
   * The column <code>public.image.gps_latitude</code>.
   */
  public final TableField<ImageRecord, Double> GPS_LATITUDE = createField(
    DSL.name("gps_latitude"),
    SQLDataType.DOUBLE,
    this,
    ""
  );

  /**
   * The column <code>public.image.gps_longitude</code>.
   */
  public final TableField<ImageRecord, Double> GPS_LONGITUDE = createField(
    DSL.name("gps_longitude"),
    SQLDataType.DOUBLE,
    this,
    ""
  );

  /**
   * The column <code>public.image.metadata</code>.
   */
  public final TableField<ImageRecord, JSONB> METADATA = createField(
    DSL.name("metadata"),
    SQLDataType.JSONB.nullable(false),
    this,
    ""
  );

  /**
   * The column <code>public.image.tags</code>.
   */
  public final TableField<ImageRecord, String[]> TAGS = createField(
    DSL.name("tags"),
    SQLDataType.VARCHAR.nullable(false).array(),
    this,
    ""
  );

  /**
   * The column <code>public.image.favorite</code>.
   */
  public final TableField<ImageRecord, Boolean> FAVORITE = createField(
    DSL.name("favorite"),
    SQLDataType.BOOLEAN
      .nullable(false)
      .defaultValue(DSL.field(DSL.raw("false"), SQLDataType.BOOLEAN)),
    this,
    ""
  );

  /**
   * The column <code>public.image.file_path</code>.
   */
  public final TableField<ImageRecord, String> FILE_PATH = createField(
    DSL.name("file_path"),
    SQLDataType.VARCHAR.nullable(false),
    this,
    ""
  );

  /**
   * The column <code>public.image.geo_json</code>.
   */
  public final TableField<ImageRecord, JSONB> GEO_JSON = createField(
    DSL.name("geo_json"),
    SQLDataType.JSONB,
    this,
    ""
  );

  private Image(Name alias, Table<ImageRecord> aliased) {
    this(alias, aliased, null);
  }

  private Image(Name alias, Table<ImageRecord> aliased, Field<?>[] parameters) {
    super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
  }

  /**
   * Create an aliased <code>public.image</code> table reference
   */
  public Image(String alias) {
    this(DSL.name(alias), IMAGE);
  }

  /**
   * Create an aliased <code>public.image</code> table reference
   */
  public Image(Name alias) {
    this(alias, IMAGE);
  }

  /**
   * Create a <code>public.image</code> table reference
   */
  public Image() {
    this(DSL.name("image"), null);
  }

  public <O extends Record> Image(Table<O> child, ForeignKey<O, ImageRecord> key) {
    super(child, key, IMAGE);
  }

  @Override
  public Schema getSchema() {
    return aliased() ? null : Public.PUBLIC;
  }

  @Override
  public UniqueKey<ImageRecord> getPrimaryKey() {
    return Keys.IMAGE_PKEY;
  }

  @Override
  public List<ForeignKey<ImageRecord, ?>> getReferences() {
    return Arrays.asList(Keys.IMAGE__IMAGE_OWNER_ID_FKEY);
  }

  private transient User _user;

  /**
   * Get the implicit join path to the <code>public.user</code> table.
   */
  public User user() {
    if (_user == null) _user = new User(this, Keys.IMAGE__IMAGE_OWNER_ID_FKEY);

    return _user;
  }

  @Override
  public Image as(String alias) {
    return new Image(DSL.name(alias), this);
  }

  @Override
  public Image as(Name alias) {
    return new Image(alias, this);
  }

  @Override
  public Image as(Table<?> alias) {
    return new Image(alias.getQualifiedName(), this);
  }

  /**
   * Rename this table
   */
  @Override
  public Image rename(String name) {
    return new Image(DSL.name(name), null);
  }

  /**
   * Rename this table
   */
  @Override
  public Image rename(Name name) {
    return new Image(name, null);
  }

  /**
   * Rename this table
   */
  @Override
  public Image rename(Table<?> name) {
    return new Image(name.getQualifiedName(), null);
  }

  // -------------------------------------------------------------------------
  // Row15 type methods
  // -------------------------------------------------------------------------

  @Override
  public Row15<UUID, String, String, OffsetDateTime, OffsetDateTime, Long, Integer, Integer, Double, Double, JSONB, String[], Boolean, String, JSONB> fieldsRow() {
    return (Row15) super.fieldsRow();
  }

  /**
   * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
   */
  public <U> SelectField<U> mapping(
    Function15<? super UUID, ? super String, ? super String, ? super OffsetDateTime, ? super OffsetDateTime, ? super Long, ? super Integer, ? super Integer, ? super Double, ? super Double, ? super JSONB, ? super String[], ? super Boolean, ? super String, ? super JSONB, ? extends U> from
  ) {
    return convertFrom(Records.mapping(from));
  }

  /**
   * Convenience mapping calling {@link SelectField#convertFrom(Class,
   * Function)}.
   */
  public <U> SelectField<U> mapping(
    Class<U> toType,
    Function15<? super UUID, ? super String, ? super String, ? super OffsetDateTime, ? super OffsetDateTime, ? super Long, ? super Integer, ? super Integer, ? super Double, ? super Double, ? super JSONB, ? super String[], ? super Boolean, ? super String, ? super JSONB, ? extends U> from
  ) {
    return convertFrom(toType, Records.mapping(from));
  }
}