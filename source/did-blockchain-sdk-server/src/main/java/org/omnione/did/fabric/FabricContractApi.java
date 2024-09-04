/*
 * Copyright 2024 Raonsecure
 */
package org.omnione.did.fabric;

import org.omnione.did.ContractApi;
import org.omnione.exception.BlockChainException;
import org.omnione.response.FabricResponse;
import org.omnione.sender.BlockChainType;
import org.omnione.sender.SenderFactory;
import org.omnione.sender.fabric.FabricContractData;
import org.omnione.sender.fabric.FabricSender;
import org.omnione.sender.fabric.FabricServerInformation;
import org.omnione.sender.fabric.FunctionName;
import org.omnione.util.DidKeyUrlParser;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.Base64;

import org.omnione.did.data.model.did.DidDocAndStatus;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.InvokedDidDoc;
import org.omnione.did.data.model.enums.did.DidDocStatus;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.data.model.vc.VcMeta;

/**
 * The {@code FabricContractApi} class provides an implementation of the {@link ContractApi} interface
 * for interacting with a Hyperledger Fabric blockchain network.
 *
 * <p>This class enables the registration and retrieval of DID documents and Verifiable Credential (VC) metadata,
 * as well as the updating of their statuses. It uses the {@link FabricServerInformation} to manage
 * the connection details and the {@link FabricSender} to send transactions to the blockchain.</p>
 */
public class FabricContractApi implements ContractApi {

    private final FabricServerInformation serverInformation;

    /**
     * Constructs a {@code FabricContractApi} with the specified resource path.
     *
     * @param resourcePath the path to the properties file containing the Fabric network configuration
     * @throws CertificateException if there's an issue with the certificate
     * @throws IOException          if there's an error reading the configuration file
     * @throws InvalidKeyException  if the key is invalid
     */
    public FabricContractApi(String resourcePath) throws CertificateException, IOException, InvalidKeyException {
        this.serverInformation = new FabricServerInformation(resourcePath);
    }

    /**
     * Registers a DID document on the Fabric blockchain.
     *
     * @param invokedDidDoc the DID document to be registered
     * @param roleType      the role type associated with the DID document
     * @throws BlockChainException if an error occurs during the transaction
     */
    @Override
    public void registDidDoc(InvokedDidDoc invokedDidDoc, RoleType roleType) throws BlockChainException {
        FabricContractData contractData = FabricContractData.Invoke(
                FunctionName.CREATE_DID_DOC,
                invokedDidDoc.toJson(),
                roleType.getRawValue()
        );
        send(contractData);
    }

    /**
     * Retrieves a DID document from the Fabric blockchain.
     *
     * @param didKeyUrl the URL of the DID key to retrieve the document
     * @return the DID document and its status
     * @throws BlockChainException if an error occurs during the transaction
     */
    @Override
    public DidDocAndStatus getDidDoc(String didKeyUrl) throws BlockChainException {
        DidKeyUrlParser parser = new DidKeyUrlParser(didKeyUrl);
        FabricContractData contractData = FabricContractData.Query(
                FunctionName.GET_DID_DOCUMENT,
                parser.getDid(),
                parser.getVersionId()
        );
        FabricResponse response = send(contractData);
        String payload = decodeBase64(response.getPayload());

        DidDocAndStatus didDocAndStatus = new DidDocAndStatus();
        didDocAndStatus.fromJson(payload);
        return didDocAndStatus;
    }

    /**
     * Updates the status of a DID document on the Fabric blockchain.
     *
     * @param didKeyUrl    the URL of the DID key
     * @param didDocStatus the new status for the DID document
     * @return the updated DID document
     * @throws BlockChainException      if an error occurs during the transaction
     * @throws IllegalArgumentException if the status is TERMINATED without a terminatedTime
     */
    @Override
    public DidDocument updateDidDocStatus(String didKeyUrl, DidDocStatus didDocStatus) throws BlockChainException {
        if (didDocStatus == DidDocStatus.TERMINATED) {
            throw new IllegalArgumentException("TERMINATED status requires a terminatedTime");
        }
        DidKeyUrlParser parser = new DidKeyUrlParser(didKeyUrl);

        FabricContractData contractData = didDocStatus != DidDocStatus.REVOKED ?
                FabricContractData.Invoke(FunctionName.UPDATE_DID_DOC_STATUS_IN_SERVICE, parser.getDid(), didDocStatus.getRawValue(), parser.getVersionId()) :
                FabricContractData.Invoke(FunctionName.UPDATE_DID_DOC_STATUS_REVOCATION, parser.getDid(), didDocStatus.getRawValue(), "");
        FabricResponse response = send(contractData);
        String payload = decodeBase64(response.getPayload());

        DidDocument didDocument = new DidDocument();
        didDocument.fromJson(payload);
        return didDocument;
    }

    /**
     * Updates the status of a DID document to TERMINATED on the Fabric blockchain.
     *
     * @param didKeyUrl      the URL of the DID key
     * @param didDocStatus   the new status for the DID document, must be TERMINATED
     * @param terminatedTime the time at which the DID document was terminated
     * @return the updated DID document
     * @throws BlockChainException      if an error occurs during the transaction
     * @throws IllegalArgumentException if the status is not TERMINATED
     */
    @Override
    public DidDocument updateDidDocStatus(String didKeyUrl, DidDocStatus didDocStatus, LocalDateTime terminatedTime) throws BlockChainException {
        if (didDocStatus != DidDocStatus.TERMINATED) {
            throw new IllegalArgumentException("Only TERMINATED status changes are allowed.");
        }
        DidKeyUrlParser parser = new DidKeyUrlParser(didKeyUrl);
        FabricContractData contractData = FabricContractData.Invoke(
                FunctionName.UPDATE_DID_DOC_STATUS_REVOCATION,
                parser.getDid(),
                didDocStatus.getRawValue(),
                terminatedTime.toString()
        );
        FabricResponse response = send(contractData);
        String payload = decodeBase64(response.getPayload());

        DidDocument didDocument = new DidDocument();
        didDocument.fromJson(payload);
        return didDocument;
    }

    /**
     * Registers Verifiable Credential metadata on the Fabric blockchain.
     *
     * @param vcMeta the VC metadata to be registered
     * @throws BlockChainException if an error occurs during the transaction
     */
    @Override
    public void registVcMetadata(VcMeta vcMeta) throws BlockChainException {
        FabricContractData contractData = FabricContractData.Invoke(FunctionName.CREATE_VC_METADATA, vcMeta.toJson());
        send(contractData);
    }

    /**
     * Updates the status of a Verifiable Credential on the Fabric blockchain.
     *
     * @param vcId     the ID of the VC to update
     * @param vcStatus the new status for the VC
     * @throws BlockChainException if an error occurs during the transaction
     */
    @Override
    public void updateVcStatus(String vcId, VcStatus vcStatus) throws BlockChainException {
        FabricContractData contractData = FabricContractData.Invoke(FunctionName.UPDATE_VC_STATUS, vcId, vcStatus.getRawValue());
        send(contractData);
    }

    /**
     * Retrieves Verifiable Credential metadata from the Fabric blockchain.
     *
     * @param vcId the ID of the VC to retrieve
     * @return the VC metadata
     * @throws BlockChainException if an error occurs during the transaction
     */
    @Override
    public VcMeta getVcMetadata(String vcId) throws BlockChainException {
        FabricContractData contractData = FabricContractData.Query(FunctionName.GET_VC_METADATA, vcId);
        FabricResponse response = send(contractData);
        String payload = decodeBase64(response.getPayload());

        VcMeta vcMeta = new VcMeta();
        vcMeta.fromJson(payload);
        return vcMeta;
    }

    /**
     * Removes a specific index from the Fabric blockchain.
     *
     * <p>This is a temporary function and may be removed in future updates.</p>
     *
     * @param index the index to remove
     * @return the response from the blockchain
     * @throws BlockChainException if an error occurs during the transaction
     */
    public FabricResponse removeIndex(String index) throws BlockChainException {
        FabricContractData contractData = FabricContractData.Invoke(FunctionName.REMOVE_INDEX, index);
        FabricResponse response = send(contractData);
        return response;
    }

    /**
     * Removes all indices from the Fabric blockchain.
     *
     * <p>This is a temporary function and may be removed in future updates.</p>
     *
     * @return the response from the blockchain
     * @throws BlockChainException if an error occurs during the transaction
     */
    public FabricResponse removeAll() throws BlockChainException {
        FabricContractData contractData = FabricContractData.Invoke(FunctionName.REMOVE_ALL);
        FabricResponse response = send(contractData);
        return response;
    }

    /**
     * Sends the contract data to the Fabric blockchain using the {@link FabricSender}.
     *
     * @param contractData the data to be sent to the blockchain
     * @return the response from the blockchain
     * @throws BlockChainException if an error occurs while sending the transaction
     */
    private FabricResponse send(FabricContractData contractData) throws BlockChainException {
        FabricSender sender = (FabricSender) SenderFactory.getSender(BlockChainType.HYPER_LEDGER_FABRIC);
        byte[] result = sender.sendTransaction(this.serverInformation, contractData);
        FabricResponse response = new FabricResponse();
        response.fromJson(new String(result));
        return response;
    }

    /**
     * Decodes a Base64 encoded string.
     *
     * @param encodedObj the Base64 encoded object, usually a string
     * @return the decoded string
     */
    private String decodeBase64(Object encodedObj) {
        return new String(Base64.getDecoder().decode(encodedObj.toString()));
    }
}
