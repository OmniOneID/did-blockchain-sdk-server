/*
 * Copyright 2024 Raonsecure
 */

package org.omnione.exception;

import lombok.Getter;

/**
 * The {@code BlockchainErrorCode} enum defines a set of error codes and corresponding messages
 * for various exceptions that may occur within the application.
 *
 * <p>Each error code is prefixed with a predefined string ("SSDKBCS") and can be used to
 * identify specific types of errors related to the fabric network, transaction execution,
 * or DID key URL parsing.</p>
 */
@Getter
public enum BlockchainErrorCode {

  /**
   * Error code for a connection error, which occurs when the application fails to connect
   * to the fabric network.
   */
  CONNECTION_ERROR("00001", "Failed to connect to fabric network"),

  /**
   * Error code for a transaction error, which occurs when the application fails to execute
   * a smart contract transaction.
   */
  TRANSACTION_ERROR("00002", "Failed to execute smart contract transaction"),

  /**
   * Error code for a DID key URL parsing error, which occurs when the provided DID key URL
   * is invalid or cannot be parsed.
   */
  DID_KEY_URL_PARSING_ERROR("00003", "Invalid did key url");

  private final String PREFIX_CODE = "SSDKBCS";

  private String code;
  private String message;

  BlockchainErrorCode(String code, String message) {
    this.code = PREFIX_CODE + code;
    this.message = message;
  }
}
