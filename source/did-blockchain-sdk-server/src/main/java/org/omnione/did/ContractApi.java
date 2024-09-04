/*
 * Copyright 2024 Raonsecure
 */

package org.omnione.did;

import org.omnione.exception.BlockChainException;
import java.time.LocalDateTime;
import org.omnione.did.data.model.did.InvokedDidDoc;
import org.omnione.did.data.model.enums.did.DidDocStatus;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.data.model.vc.VcMeta;


/**
 * Interface for interacting with a blockchain network. Provides methods to manage DID Documents and Verifiable
 * Credentials.
 */
public interface ContractApi {

  /**
   * Registers or updates a DID Document on the blockchain. This function is used for the initial registration of a DID
   * Document or for updating a DID Document excluding changes to the document's status.
   *
   * @param invokedDidDoc the DID Document to be registered or updated
   * @param roleType      the role type of the entity registering or updating the DID Document
   * @throws BlockChainException if an error occurs during the registration or update process
   */
  void registDidDoc(InvokedDidDoc invokedDidDoc, RoleType roleType) throws BlockChainException;

  /**
   * Retrieves a DID Document and its status from the blockchain.
   *
   * @param didKeyUrl the DID key URL
   * @return the DID Document and its status
   * @throws BlockChainException if an error occurs during the retrieval process
   */
  Object getDidDoc(String didKeyUrl) throws BlockChainException;

  /**
   * Updates the status of a DID Document to either ACTIVATED or DEACTIVATED on the blockchain.
   *
   * @param didKeyUrl    the DID key URL
   * @param didDocStatus the new status of the DID Document
   * @return the updated DID Document
   * @throws BlockChainException if an error occurs during the update process
   */
  Object updateDidDocStatus(String didKeyUrl, DidDocStatus didDocStatus) throws BlockChainException;

  /**
   * Updates the status of a DID Document to REVOKED or TERMINATED on the blockchain.
   *
   * @param didKeyUrl      the DID key URL
   * @param didDocStatus   the new status of the DID Document
   * @param terminatedTime the termination time of the DID Document, if applicable
   * @return the updated DID Document
   * @throws BlockChainException if an error occurs during the update process
   */
  Object updateDidDocStatus(String didKeyUrl, DidDocStatus didDocStatus, LocalDateTime terminatedTime)
      throws BlockChainException;

  /**
   * Registers VC Metadata on the blockchain.
   *
   * @param vcMeta the VC Metadata to be registered
   * @throws BlockChainException if an error occurs during the registration process
   */
  void registVcMetadata(VcMeta vcMeta) throws BlockChainException;

  /**
   * Retrieves VC Metadata from the blockchain.
   *
   * @param vcId the VC ID
   * @return the VC Metadata
   * @throws BlockChainException if an error occurs during the retrieval process
   */
  Object getVcMetadata(String vcId) throws BlockChainException;

  /**
   * Updates the status of VC Metadata on the blockchain.
   *
   * @param vcId     the VC ID
   * @param vcStatus the VC status to be updated
   * @throws BlockChainException if an error occurs during the update process
   */
  void updateVcStatus(String vcId, VcStatus vcStatus) throws BlockChainException;
}