package org.omnione.sender;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Logger;
import org.omnione.sender.fabric.FabricServerInformation;


class SenderUtils {

  private static final Logger LOG = Logger.getLogger(SenderUtils.class.getName());

  private SenderUtils() {
    // Prevent instantiation
    throw new IllegalAccessError("sender-utils is a utility class and cannot be instantiated");
  }

  /**
   * Loads properties from the specified resource.
   * <p>
   * This method supports both absolute file paths and classpath resources. If the given resource is
   * an absolute path, it loads the properties file directly from the file system. Otherwise, it
   * attempts to load the properties from the classpath.
   *
   * @param resource the absolute file path or classpath resource name of the properties file
   * @return a {@code Properties} object containing the configuration
   * @throws IOException if the properties file is not found or cannot be loaded
   */
  protected static Properties loadProperties(String resource) throws IOException {
    Properties properties = new Properties();
    Path path = Paths.get(resource);
    try (InputStream inputStream = path
        .isAbsolute() ? Files.newInputStream(path)
        : FabricServerInformation.class.getClassLoader()
            .getResourceAsStream(resource)) {
      properties.load(inputStream);
    }

    return properties;
  }
}
