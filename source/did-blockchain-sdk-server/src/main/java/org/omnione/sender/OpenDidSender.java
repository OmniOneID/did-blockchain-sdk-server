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

package org.omnione.sender;

/**
 * Interface for sending transactions to a blockchain network.
 * <p>
 * This interface defines the contract for sending transactions to a blockchain network, using the
 * provided {@code ServerInformation} and {@code ContractData}.
 * </p>
 */
public interface OpenDidSender {

  /**
   * Sends a transaction to the specified blockchain network.
   *
   * @param serverInformation information about the blockchain network
   * @param data              the contract data for the transaction
   * @return the result of the transaction as a byte array
   * @throws Exception if an error occurs during the transaction
   */
  byte[] sendTransaction(ServerInformation serverInformation, ContractData data) throws Exception;
}
