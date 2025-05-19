package org.omnione.sender.ethereum;

import java.io.IOException;
import java.util.Properties;
import lombok.Getter;
import org.omnione.sender.ServerInformation;

@Getter
public class EvmServerInformation extends ServerInformation {

  private final String networkURL;
  private final long chainId;
  private final long gasLimit;
  private final long gasPrice;
  private final int connectionTimeout;

  public EvmServerInformation(String resource) throws IOException {
    super();

    // Load properties from the resource file and initialize the fields
    Properties properties = loadProperties(resource);
    this.networkURL = properties.getProperty("evm.network.url");
    if (this.networkURL == null || this.networkURL.isEmpty()) {
      throw new IllegalArgumentException("Property 'evm.network.url' is missing or empty");
    }

    this.chainId = parseLongProperty(
        properties,
        "evm.chainId"
    );
    this.gasLimit = parseLongProperty(
        properties,
        "evm.gas.limit"
    );
    this.gasPrice = parseLongProperty(
        properties,
        "evm.gas.price"
    );
    this.connectionTimeout = parseIntProperty(
        properties,
        "evm.connection.timeout"
    );
  }

  private long parseLongProperty(Properties properties, String key) {
    String value = properties.getProperty(key);
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException("Property '" + key + "' is missing or empty");
    }
    return Long.parseLong(value);
  }

  private int parseIntProperty(Properties properties, String key) {
    String value = properties.getProperty(key);
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException("Property '" + key + "' is missing or empty");
    }
    return Integer.parseInt(value);
  }
}
