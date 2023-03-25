package com.github.gyrosofwar.imagehive.service.thumbnails;

import com.github.gyrosofwar.imagehive.configuration.ImageHiveConfiguration;
import com.github.gyrosofwar.imagehive.service.ImageData;
import io.micronaut.context.annotation.Primary;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import jakarta.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Primary
public class ImgProxyThumbnailer implements Thumbnailer {

  private static final Logger log = LoggerFactory.getLogger(ImgProxyThumbnailer.class);

  private static final Set<CharSequence> FORWARDED_HEADERS = Set.of(
    "Accept",
    "Width",
    "Viewport-Width",
    "DPR"
  );

  private final ImgProxy imgProxy;
  private final HttpClient httpClient;

  public ImgProxyThumbnailer(ImageHiveConfiguration imageHiveConfiguration, HttpClient httpClient) {
    var config = imageHiveConfiguration.imgProxy();
    this.imgProxy = new ImgProxy(config.key(), config.salt(), config.uri().toString());
    this.httpClient = httpClient;
  }

  @Override
  public ImageData getThumbnail(Request request) throws IOException {
    var path = request.imagePath().toString().replace('\\', '/');
    var url = String.format("local://%s", path);
    var thumbnailUrl = imgProxy.generateUrl(url, request.width(), request.height(), 1, "");
    var headers = request.headers();

    Map<CharSequence, CharSequence> allowedHeaders = new HashMap<>();
    for (var header : headers.asMap().entrySet()) {
      if (FORWARDED_HEADERS.contains(header.getKey())) {
        allowedHeaders.put(header.getKey(), header.getValue().get(0));
      }
    }

    var httpRequest = HttpRequest.GET(thumbnailUrl).headers(allowedHeaders);

    var response = httpClient.toBlocking().exchange(httpRequest, byte[].class);
    long lastModified = response.getHeaders().findInt("Last-Modified").orElse(0);
    log.info("response headers: {}", response.getHeaders().asMap());

    return new ImageData(
      new ByteArrayInputStream(response.body()),
      response.getContentType().orElse(MediaType.APPLICATION_OCTET_STREAM_TYPE),
      lastModified,
      response.getContentLength()
    );
  }
}
