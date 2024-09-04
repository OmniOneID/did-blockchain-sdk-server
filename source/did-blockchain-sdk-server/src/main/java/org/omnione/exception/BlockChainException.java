/*
 * Copyright 2024 Raonsecure
 */

package org.omnione.exception;

import lombok.Getter;


/**
 * Custom exception class {@code BlockChainException} that extends {@code Exception}.
 * <p>
 * This exception is used to represent errors specific to blockchain operations. It includes an error code and a message
 * that provide additional context about the error.
 * </p>
 */
@Getter
public class BlockChainException extends Exception {

  protected String errorCode;
  protected String errorMsg;

  public BlockChainException(BlockchainErrorCode errorCode, Throwable throwable) {
    super("ErrorCode: " + errorCode.getCode() + ", Message: " + errorCode.getMessage() + ", Reason: " + throwable);
    this.errorCode = errorCode.getCode();
    this.errorMsg = errorCode.getMessage();
  }

}