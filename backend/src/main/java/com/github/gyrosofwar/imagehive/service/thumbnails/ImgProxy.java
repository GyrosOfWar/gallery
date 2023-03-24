package com.github.gyrosofwar.imagehive.service.thumbnails;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

// TODO use for generating thumbnails
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

  public URI generateUrl(String sourceUrl, int width, int height, int dpr, String extension) {
    final var algorithm = "HmacSHA256";
    var encodedUrl = Base64
      .getUrlEncoder()
      .encodeToString(sourceUrl.getBytes(StandardCharsets.UTF_8));

    var path = String.format("/resize:fill:%s:%s:1/gravity:sm", width, height);
    if (dpr > 1) {
      path += "/dpr:" + dpr;
    }

    path += "/" + encodedUrl + extension;

    byte[] key = hexStringToByteArray(this.key);
    byte[] salt = hexStringToByteArray(this.salt);

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
