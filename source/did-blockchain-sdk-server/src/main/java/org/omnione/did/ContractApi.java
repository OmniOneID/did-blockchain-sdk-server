/*
 * Copyright 2024 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did;

import java.time.LocalDateTime;
import org.omnione.did.data.model.did.InvokedDidDoc;
import org.omnione.did.data.model.enums.did.DidDocStatus;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.data.model.zkp.ZKPCredentialDefinition;
import org.omnione.did.data.model.zkp.ZKPCredentialSchema;
import org.omnione.exception.BlockChainException;


/**
 * Interface for interacting with a blockchain network. Provides methods to manage DID Documents and
 * Verifiable Credentials.
 */
public interface ContractApi {

  /**
   * Registers or updates a DID Document on the blockchain. This function is used for the initial
   * registration of a DID Document or for updating a DID Document excluding changes to the
   * document's status.
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
  Object updateDidDocStatus(
      String didKeyUrl, DidDocStatus didDocStatus, LocalDateTime terminatedTime
  ) throws BlockChainException;

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

  void registVcSchema(VcSchema vcSchema) throws BlockChainException;

  Object getVcSchema(String schemaId) throws BlockChainException;

  void registZKPCredential(ZKPCredentialSchema credentialSchema) throws BlockChainException;

  Object getZKPCredential(String schemaId) throws BlockChainException;

  void registZKPCredentialDefinition(ZKPCredentialDefinition credentialDefinition)
      throws BlockChainException;

  Object getZKPCredentialDefinition(String definitionId) throws BlockChainException;
}
