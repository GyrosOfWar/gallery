package com.github.gyrosofwar.imagehive.service.geo;

import com.github.gyrosofwar.imagehive.sql.tables.pojos.Image;
import jakarta.inject.Singleton;
import mil.nga.sf.geojson.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class GeoCodingService {

  private static final Logger log = LoggerFactory.getLogger(GeoCodingService.class);
  private final PhotonApiClient apiClient;

  public GeoCodingService(PhotonApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public FeatureCollection getGeoInformation(Image image) {
    try {
      if (image.gpsLatitude() != null && image.gpsLongitude() != null) {
        return apiClient.reverseGeoCode(image.gpsLatitude(), image.gpsLongitude());
      } else {
        return null;
      }
    } catch (Exception e) {
      log.info("failed to fetch geo information for image:", e);
      return null;
    }
  }
}
