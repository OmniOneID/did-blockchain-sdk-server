package org.omnione.did.ethereum

import org.bouncycastle.util.encoders.Hex
import org.omnione.did.data.model.did.DidDocument
import org.omnione.did.data.model.did.InvokedDidDoc
import org.omnione.did.data.model.did.Proof
import org.omnione.did.data.model.enums.did.DidDocStatus
import org.omnione.did.data.model.enums.vc.RoleType
import org.omnione.did.data.model.provider.Provider
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Hash
import org.web3j.crypto.Keys
import org.web3j.crypto.Sign
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class EvmContractApiSpec extends Specification {

    private static final String RESOURCE_PATH = '/Users/mykim/Workspace/OpenSource/did-blockchain-sdk-server/source/did-blockchain-sdk-server/src/test/resources/application-test.properties'
    private static final String JSON_DOCUMENT_PATH = '/Users/mykim/Workspace/OpenSource/did-blockchain-sdk-server/source/did-blockchain-sdk-server/src/test/resources/data/document.json'
    private ECKeyPair keyPair

    def "registerDidDoc should not throw any exception"() {
        given: 'An instance of EvmContractApi and a new InvokedDidDoc'
        def evmContractApi = new EvmContractApi(RESOURCE_PATH)
        def document = newInvokedDidDocument()

        when: 'registDidDoc is called'
        evmContractApi.registDidDoc(document, RoleType.APP_PROVIDER)

        then: 'No exception is thrown'
        noExceptionThrown()
    }

    def "getDidDoc should return a valid DidDocument"() {
        given: 'An instance of EvmContractApi'
        def evmContractApi = new EvmContractApi(RESOURCE_PATH)

        when: 'getDidDoc is called with a valid DID'
        def document = evmContractApi.getDidDoc('did:example:123456789abcdefghi?versionId=1')
                as DidDocument

        then: 'The returned DidDocument has the expected context'
        document.context[0] == 'https://www.w3.org/ns/did/v1'
    }

    def "updateDidDoc should not throw any exception"() {
        given: 'An instance of EvmContractApi and a new InvokedDidDoc'
        def evmContractApi = new EvmContractApi(RESOURCE_PATH)
        def didKeyUrl = 'did:example:123456789abcdefghi?versionId=1#key1'

        when: 'updateDidDoc is called'
        evmContractApi.updateDidDocStatus(didKeyUrl, DidDocStatus.DEACTIVATED)

        then: 'No exception is thrown'
        noExceptionThrown()
    }

    def setup() {
        keyPair = Keys.createEcKeyPair()
    }

    private InvokedDidDoc newInvokedDidDocument() {
        def document = readDocument()
        assert document != null

        def proof = newProof()
        def provider = newProvider('did:example:123456789abcdefghi', 'certVcRef')
        def nonce = generateNonce()

        return new InvokedDidDoc(document, proof, provider, nonce)
    }

    private static String readDocument() {
        try {
            return new String(Files.readAllBytes(Paths.get(JSON_DOCUMENT_PATH)))
        } catch (IOException e) {
            throw new RuntimeException("Failed to read document from path: $JSON_DOCUMENT_PATH", e)
        }
    }

    private Proof newProof() {
        def proof = new Proof()
        proof.type = 'Secp256k1Signature2018'
        proof.created = getCurrentTimeInIsoFormat()
        proof.proofPurpose = 'verificationMethod'
        proof.verificationMethod = 'did:example:123456789abcdefghi?versionId=1#key1'

        def messageHash = Hash.sha3(proof.toJson().bytes)
        def signature = Sign.signMessage(messageHash, keyPair, false)
        proof.proofValue = Hex.toHexString(signature.v) + Hex.toHexString(signature.r) + Hex.toHexString(signature.s)

        return proof
    }

    private static String getCurrentTimeInIsoFormat() {
        return DateTimeFormatter.ISO_INSTANT.format(Instant.now()
                .atOffset(ZoneOffset.UTC))
    }

    private static Provider newProvider(String did, String certVcRef) {
        def provider = new Provider()
        provider.did = did
        provider.certVcRef = certVcRef
        return provider
    }

    private static String generateNonce() {
        return UUID.randomUUID()
                .toString()
    }

}
