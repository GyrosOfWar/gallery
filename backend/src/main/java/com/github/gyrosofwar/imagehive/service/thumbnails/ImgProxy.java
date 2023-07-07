package com.github.gyrosofwar.imagehive.service.thumbnails;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ImgProxy {

  private final String key;
  private final String salt;
  private final String baseUrl;

  public ImgProxy(String key, String salt, String url) {
    this.key = key;
    this.salt = salt;
    this.baseUrl = url;
  }

  private static byte[] hexStringToByteArray(String hex) {
    if (hex.length() % 2 != 0) {
      throw new IllegalArgumentException("Even-length string required");
    }
    byte[] res = new byte[hex.length() / 2];
    for (int i = 0; i < res.length; i++) {
      res[i] =
        (byte) (
          (Character.digit(hex.charAt(i * 2), 16) << 4) |
          (Character.digit(hex.charAt(i * 2 + 1), 16))
        );
    }
    return res;
  }

  public Builder builder(String sourceUrl) {
    return new Builder(sourceUrl);
  }

  public enum ResizeMethod {
    FIT,
    FILL,
    FILL_DOWN,
    FORCE,
    AUTO;

    @Override
    public String toString() {
      return switch (this) {
        case FIT -> "fit";
        case AUTO -> "auto";
        case FILL -> "fill";
        case FORCE -> "force";
        case FILL_DOWN -> "fill-down";
      };
    }
  }

  public enum Gravity {
    NORTH("no"),
    EAST("ea"),
    SOUTH("so"),
    WEST("we"),
    SMART("sm"),
    CENTER("ce");

    Gravity(String value) {
      this.value = value;
    }

    private String value;

    @Override
    public String toString() {
      return this.value;
    }
  }

  public class Builder {

    private final String sourceUrl;
    private ResizeMethod resizingType;
    private Integer width;
    private Integer height;
    private Integer dpr;
    private Boolean enlarge;
    private Gravity gravity;
    private Integer quality;
    private String format;

    Builder(String sourceUrl) {
      this.sourceUrl = sourceUrl;
    }

    public Builder resizingType(ResizeMethod method) {
      this.resizingType = method;
      return this;
    }

    public Builder width(int width) {
      this.width = width;
      return this;
    }

    public Builder height(int height) {
      this.height = height;
      return this;
    }

    public Builder dpr(int dpr) {
      this.dpr = dpr;
      return this;
    }

    public Builder enlarge(boolean enlarge) {
      this.enlarge = enlarge;
      return this;
    }

    public Builder gravity(Gravity gravity) {
      this.gravity = gravity;
      return this;
    }

    public Builder quality(int quality) {
      this.quality = quality;
      return this;
    }

    public Builder format(String format) {
      this.format = format;
      return this;
    }

    public URI build() {
      final var algorithm = "HmacSHA256";

      var components = new ArrayList<String>();

      if (resizingType != null) {
        components.add("rt:" + resizingType);
      }

      if (width != null) {
        components.add("w:" + width);
      }

      if (height != null) {
        components.add("h:" + height);
      }

      if (dpr != null) {
        components.add("dpr:" + dpr);
      }

      if (enlarge != null && enlarge) {
        components.add("el:" + enlarge);
      }

      if (gravity != null) {
        components.add("g:" + gravity);
      }

      if (quality != null) {
        components.add("q:" + quality);
      }

      if (format != null) {
        components.add("f:" + format);
      }

      var encodedUrl = Base64
        .getUrlEncoder()
        .encodeToString(sourceUrl.getBytes(StandardCharsets.UTF_8));

      var path = "/" + String.join("/", components) + "/" + encodedUrl;
      byte[] key = hexStringToByteArray(ImgProxy.this.key);
      byte[] salt = hexStringToByteArray(ImgProxy.this.salt);

      try {
        var sha256hmac = Mac.getInstance(algorithm);
        var secretKey = new SecretKeySpec(key, algorithm);
        sha256hmac.init(secretKey);
        sha256hmac.update(salt);

        String hash = Base64
          .getUrlEncoder()
          .withoutPadding()
          .encodeToString(sha256hmac.doFinal(path.getBytes(StandardCharsets.UTF_8)));

        return URI.create(baseUrl + "/" + hash + path);
      } catch (InvalidKeyException | NoSuchAlgorithmException ex) {
        throw new RuntimeException(ex);
      }
    }
  }
}
