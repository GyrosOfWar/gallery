package com.github.gyrosofwar.imagehive.service.thumbnails;

import com.github.gyrosofwar.imagehive.configuration.ImageHiveConfiguration;
import com.github.gyrosofwar.imagehive.service.ImageData;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import jakarta.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Requires(property = "imagehive.thumbnailer", value = "IMG_PROXY")
public class ImgProxyThumbnailer implements Thumbnailer {

  private static final Logger log = LoggerFactory.getLogger(ImgProxyThumbnailer.class);

  private static final Set<CharSequence> FORWARDED_REQUEST_HEADERS = Set.of(
    "Accept",
    "Width",
    "Viewport-Width",
    "DPR"
  );

  private static final Set<CharSequence> FORWARDED_RESPONSE_HEADERS = Set.of(
    "Cache-Control",
    "Etag",
    "Expires",
    "Vary"
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
      if (FORWARDED_REQUEST_HEADERS.contains(header.getKey())) {
        allowedHeaders.put(header.getKey(), header.getValue().get(0));
      }
    }

    var httpRequest = HttpRequest.GET(thumbnailUrl).headers(allowedHeaders);

    var response = httpClient.toBlocking().exchange(httpRequest, byte[].class);
    long lastModified = response.getHeaders().findInt("Last-Modified").orElse(0);
    Map<CharSequence, CharSequence> responseHeaders = new HashMap<>();
    for (var header : response.getHeaders().asMap().entrySet()) {
      if (FORWARDED_RESPONSE_HEADERS.contains(header.getKey())) {
        responseHeaders.put(header.getKey(), header.getValue().get(0));
      }
    }
    log.debug("sending response with headers {}", responseHeaders);

    return new ImageData(
      new ByteArrayInputStream(response.body()),
      response.getContentType().orElse(MediaType.APPLICATION_OCTET_STREAM_TYPE),
      lastModified,
      response.getContentLength(),
      responseHeaders
    );
  }
}
