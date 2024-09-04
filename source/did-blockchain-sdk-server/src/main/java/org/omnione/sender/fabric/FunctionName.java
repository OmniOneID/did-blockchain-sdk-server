/*
 * Copyright 2024 Raonsecure
 */

package org.omnione.sender.fabric;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The {@code FunctionName} enum defines the function names used in smart contract transactions.
 *
 * <p>Each enum constant corresponds to a specific function that can be invoked on the blockchain,
 * and it holds the string representation of that function's name.</p>
 */
@Getter
@RequiredArgsConstructor
public enum FunctionName {

  /**
   * (Temporary) Function name for removing a specific index.
   * <p>This function is intended for temporary use and may be subject to removal in future updates.</p>
   */
  REMOVE_INDEX("remove"),

  /**
   * (Temporary) Function name for removing all indices.
   * <p>This function is intended for temporary use and may be subject to removal in future updates.</p>
   */
  REMOVE_ALL("removeAll"),

  /**
   * Function name for creating a DID document.
   */
  CREATE_DID_DOC("document_registDidDoc"),

  /**
   * Function name for retrieving a DID document.
   */
  GET_DID_DOCUMENT("document_getDidDoc"),

  /**
   * Function name for updating the status of a DID document between In-Service Lists.
   */
  UPDATE_DID_DOC_STATUS_IN_SERVICE("document_updateDidDocStatusInService"),

  /**
   * Function name for updating the status of a DID document to Revocation Lists.
   */
  UPDATE_DID_DOC_STATUS_REVOCATION("document_updateDidDocStatusRevocation"),

  /**
   * Function name for retrieving Verifiable Credential metadata.
   */
  GET_VC_METADATA("vcMeta_getVcMetadata"),

  /**
   * Function name for creating Verifiable Credential metadata.
   */
  CREATE_VC_METADATA("vcMeta_registVcMetadata"),

  /**
   * Function name for updating the status of a Verifiable Credential.
   */
  UPDATE_VC_STATUS("vcMeta_updateVcStatus");

  private final String functionName;

}
