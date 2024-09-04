/*
 * Copyright 2024 Raonsecure
 */

package org.omnione.sender;

/**
 * Interface for sending transactions to a blockchain network.
 * <p>
 * This interface defines the contract for sending transactions to a blockchain network, using the provided
 * {@code ServerInformation} and {@code ContractData}.
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
