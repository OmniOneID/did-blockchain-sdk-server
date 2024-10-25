/*
 * Copyright 2024 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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