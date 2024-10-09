package com.github.gyrosofwar.imagehive.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

public class TikaUtil {

  public static String determineFormat(BufferedInputStream inputStream) throws IOException {
    Detector detector = new DefaultDetector();
    Metadata metadata = new Metadata();
    MediaType mediaType = detector.detect(inputStream, metadata);

    return mediaType.toString();
  }

  public static String getExtensionForMimetype(String mimeType) throws MimeTypeException {
    MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
    MimeType type = allTypes.forName(mimeType);

    return type.getAcronym();
  }

  public static String getExtensionByMimeType(BufferedInputStream inputStream)
    throws IOException, MimeTypeException {
    return getExtensionForMimetype(determineFormat(inputStream));
  }
}
