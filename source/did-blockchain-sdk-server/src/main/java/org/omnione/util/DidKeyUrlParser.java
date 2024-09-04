/*
 * Copyright 2024 Raonsecure
 */

package org.omnione.util;

import org.omnione.exception.BlockChainException;
import org.omnione.exception.BlockchainErrorCode;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.Getter;
import lombok.ToString;

/**
 * Parses and validates a DID key URL to extract DID, version ID, and key ID components.
 * <p>
 * This class provides functionality to parse a DID key URL based on the URI syntax for DID documents. It ensures that
 * the URL conforms to the expected format and extracts the necessary components: DID, version ID, and key ID.
 * </p>
 */
@Getter
@ToString
public class DidKeyUrlParser {

  private String did;
  private String versionId;

  /**
   * Constructs a {@code DidKeyUrlParser} instance and parses the given DID key URL.
   *
   * @param didKeyUrl the DID key URL to be parsed
   * @throws URISyntaxException if the DID key URL is malformed or does not conform to the expected syntax
   */
  public DidKeyUrlParser(String didKeyUrl) throws BlockChainException {
    try {
      parseDidKeyUrl(didKeyUrl);
    } catch (URISyntaxException e) {
      throw new BlockChainException(BlockchainErrorCode.DID_KEY_URL_PARSING_ERROR, e);
    }
  }

  /**
   * Parses the DID key URL to extract the DID, version ID, and key ID.
   *
   * @param didKeyUrl the DID key URL to be parsed
   * @throws URISyntaxException if the DID key URL is malformed or does not conform to the expected syntax
   */
  private void parseDidKeyUrl(String didKeyUrl) throws URISyntaxException {
    URI uri = new URI(didKeyUrl);

    // Validate the URI schema
    String schema = uri.getScheme();
    if (schema == null || !schema.equals("did")) {
      throw new URISyntaxException(didKeyUrl, "Invalid didKeyUrl syntax - schema must be 'did'");
    }

    String[] sspArr = uri.getSchemeSpecificPart().split("\\?");

    // Extract and validate the DID method
    String[] didMethodArr = sspArr[0].split(":", 2);
    if (didMethodArr.length < 2) {
      throw new URISyntaxException(didKeyUrl, "Invalid didKeyUrl syntax - invalid did method");
    }
    this.did = schema + ":" + sspArr[0];

    if (sspArr.length > 1) {
      // Extract and validate the version ID
      String[] versionIdArr = sspArr[1].split("=", 2);
      if (!versionIdArr[0].equals("versionId") || versionIdArr.length < 2) {
        throw new URISyntaxException(didKeyUrl, "Invalid didKeyUrl syntax - invalid query");
      }
      this.versionId = versionIdArr[1];
    } else {
      versionId = "";
    }
  }
}