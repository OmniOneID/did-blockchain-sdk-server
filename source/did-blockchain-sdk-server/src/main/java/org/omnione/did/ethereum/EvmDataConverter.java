package org.omnione.did.ethereum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.validation.Valid;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.omnione.did.data.model.did.DidDocAndStatus;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.Service;
import org.omnione.did.data.model.did.VerificationMethod;
import org.omnione.did.data.model.enums.did.DidDocStatus;
import org.omnione.did.data.model.schema.ClaimDef;
import org.omnione.did.data.model.schema.MetaData;
import org.omnione.did.data.model.schema.SchemaClaims;
import org.omnione.did.data.model.schema.SchemaCredentialSubject;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.zkp.datamodel.definition.CredentialDefinition;
import org.omnione.did.zkp.datamodel.schema.AttributeDef.ATTR_TYPE;
import org.omnione.did.zkp.datamodel.schema.CredentialSchema;
import org.omnione.did.zkp.datamodel.schema.Namespace;
import org.omnione.generated.OpenDID;
import org.omnione.generated.OpenDID.AttributeType;
import org.omnione.generated.OpenDID.ClaimNamespace;
import org.omnione.generated.OpenDID.CredentialSubject;
import org.omnione.generated.OpenDID.SchemaClaimItem;
import org.omnione.generated.OpenDID.ZKPLibrary_CredentialSchema;

final class EvmDataConverter {

  private EvmDataConverter() {
    throw new AssertionError("Utility class");
  }

  /**
   * Converts a DID document to a smart contract object.
   * This method transforms verification methods, services, and other DID document components
   * to their blockchain-compatible representations.
   *
   * @param didDocument The DID document to convert
   * @return OpenDID.Document object for smart contract usage
   */
  static OpenDID.Document convertToContractObject(DidDocument didDocument) {
    List<String> capabilityDelegation = didDocument.getCapabilityDelegation();
    if (capabilityDelegation == null || capabilityDelegation.isEmpty()) {
      capabilityDelegation = new ArrayList<>();
    }

    List<String> capabilityInvocation = didDocument.getCapabilityInvocation();
    if (capabilityInvocation == null || capabilityInvocation.isEmpty()) {
      capabilityInvocation = new ArrayList<>();
    }

    List<String> keyAgreement = didDocument.getKeyAgreement();
    if (keyAgreement == null || keyAgreement.isEmpty()) {
      keyAgreement = new ArrayList<>();
    }

    List<String> authentication = didDocument.getAuthentication();
    if (authentication == null || authentication.isEmpty()) {
      authentication = new ArrayList<>();
    }

    List<String> assertionMethod = didDocument.getAssertionMethod();
    if (assertionMethod == null) {
      assertionMethod = new ArrayList<>();
    }

    List<OpenDID.VerificationMethod> verificationMethods = new ArrayList<>();
    if (didDocument.getVerificationMethod() != null) {
      for (VerificationMethod originalMethod : didDocument.getVerificationMethod()) {
        Integer keyType = 0;
        String type = originalMethod.getType();

        if (type != null) {
          if ("RsaVerificationKey2018".equals(type)) {
            keyType = 0;
          } else if ("Secp256k1VerificationKey2018".equals(type)) {
            keyType = 1;
          } else if ("Secp256r1VerificationKey2018".equals(type)) {
            keyType = 2;
          }
        }

        OpenDID.VerificationMethod newMethod = new OpenDID.VerificationMethod(
            originalMethod.getId(),
            BigInteger.valueOf(keyType),
            originalMethod.getController(),
            originalMethod.getPublicKeyMultibase(),
            BigInteger.valueOf(originalMethod.getAuthType())
        );

        verificationMethods.add(newMethod);
      }
    }

    List<OpenDID.Service> services = new ArrayList<>();
    if (didDocument.getService() != null) {
      for (org.omnione.did.data.model.did.Service originalService : didDocument.getService()) {
        OpenDID.Service newService = new OpenDID.Service(
            originalService.getId(),
            originalService.getType(),
            originalService.getServiceEndpoint()
        );

        services.add(newService);
      }
    }

    return new OpenDID.Document(
        didDocument.getContext(),
        didDocument.getId(),
        didDocument.getController(),
        didDocument.getCreated(),
        didDocument.getUpdated(),
        didDocument.getVersionId(),
        didDocument.getDeactivated(),
        verificationMethods,
        assertionMethod,
        didDocument.getAuthentication(),
        didDocument.getKeyAgreement(),
        capabilityInvocation,
        capabilityDelegation,
        services
    );
  }

  /**
   * Converts a smart contract DID document to a Java object.
   * This method transforms blockchain DID document representations back to standard Java objects,
   * including proper status mapping and verification method type conversion.
   *
   * @param documentAndStatus The smart contract DID document with status information
   * @return DidDocAndStatus object for Java usage
   */
  static DidDocAndStatus convertToJavaObject(OpenDID.DocumentAndStatus documentAndStatus
  ) {
    DidDocument didDocument = new DidDocument();
    OpenDID.Document document = documentAndStatus.diddoc;

    didDocument.setContext(document.context);
    didDocument.setId(document.id);
    didDocument.setController(document.controller);
    didDocument.setCreated(document.created);
    didDocument.setUpdated(document.updated);
    didDocument.setVersionId(document.versionId);
    didDocument.setDeactivated(document.deactivated);

    if (document.verificationMethod != null && !document.verificationMethod.isEmpty()) {
      List<VerificationMethod> verificationMethods = new ArrayList<>();
      for (OpenDID.VerificationMethod vm : document.verificationMethod) {
        VerificationMethod newVm = new VerificationMethod();
        newVm.setId(vm.id);
        newVm.setController(vm.controller);
        newVm.setPublicKeyMultibase(vm.publicKeyMultibase);
        newVm.setAuthType(vm.authType.intValue());
        if (vm.keyType != null) {
          if (vm.keyType.intValue() == 0) {
            newVm.setType("RsaVerificationKey2018");
          } else if (vm.keyType.intValue() == 1) {
            newVm.setType("Secp256k1VerificationKey2018");
          } else if (vm.keyType.intValue() == 2) {
            newVm.setType("Secp256r1VerificationKey2018");
          }
        }
        verificationMethods.add(newVm);
      }
      didDocument.setVerificationMethod(verificationMethods);
    }

    didDocument.setAssertionMethod(document.assertionMethod);
    didDocument.setAuthentication(document.authentication);
    didDocument.setKeyAgreement(document.keyAgreement);
    didDocument.setCapabilityInvocation(document.capabilityInvocation);
    didDocument.setCapabilityDelegation(document.capabilityDelegation);

    if (document.services != null && !document.services.isEmpty()) {
      List<Service> services = new ArrayList<>();
      for (OpenDID.Service svc : document.services) {
        Service newSvc = new Service();
        newSvc.setId(svc.id);
        newSvc.setType(svc.serviceType);
        newSvc.setServiceEndpoint(svc.serviceEndpoint);
        services.add(newSvc);
      }
      didDocument.setService(services);
    }

    DidDocStatus didDocStatus;
    BigInteger status = documentAndStatus.status;

    switch (status.intValue()) {
      case 0:
        didDocStatus = DidDocStatus.ACTIVATED;
      case 1:
        didDocStatus = DidDocStatus.DEACTIVATED;
      case 2:
        didDocStatus = DidDocStatus.REVOKED;
      case 3:
        didDocStatus = DidDocStatus.TERMINATED;
      default:
        didDocStatus = DidDocStatus.ACTIVATED;
    }

    return new DidDocAndStatus(
        didDocument,
        didDocStatus
    );
  }

  /**
   * Converts VC metadata to a smart contract object.
   * This method transforms verifiable credential metadata including issuer information,
   * credential schema references, and validity periods for blockchain storage.
   *
   * @param vcMeta The VC metadata to convert
   * @return OpenDID.VcMeta object for smart contract usage
   */
  static OpenDID.VcMeta convertToContractObject(VcMeta vcMeta) {
    return new OpenDID.VcMeta(
        vcMeta.getId(),
        new OpenDID.Provider(
            vcMeta.getIssuer()
                .getDid(),
            vcMeta.getIssuer()
                .getCertVcRef()
        ),
        vcMeta.getSubject(),
        new OpenDID.CredentialSchemaLibrary_CredentialSchema(
            vcMeta.getCredentialSchema()
                .getId(),
            vcMeta.getCredentialSchema()
                .getType()
        ),
        vcMeta.getStatus(),
        vcMeta.getIssuanceDate(),
        vcMeta.getValidFrom(),
        vcMeta.getValidUntil(),
        vcMeta.getFormatVersion(),
        vcMeta.getLanguage()
    );
  }

  /**
   * Converts smart contract VC metadata to a Java object.
   * This method transforms blockchain VC metadata representations back to standard Java objects,
   * reconstructing issuer and credential schema information.
   *
   * @param vcMetaContract The smart contract VC metadata to convert
   * @return VcMeta object for Java usage
   */
  static VcMeta convertToJavaObject(OpenDID.VcMeta vcMetaContract
  ) {
    VcMeta vcMeta = new VcMeta();

    vcMeta.setId(vcMetaContract.id);
    vcMeta.setSubject(vcMetaContract.subject);
    vcMeta.setStatus(vcMetaContract.status);
    vcMeta.setIssuanceDate(vcMetaContract.issuanceDate);
    vcMeta.setValidFrom(vcMetaContract.validFrom);
    vcMeta.setValidUntil(vcMetaContract.validUntil);
    vcMeta.setFormatVersion(vcMetaContract.formatVersion);
    vcMeta.setLanguage(vcMetaContract.language);

    org.omnione.did.data.model.provider.Provider provider = new org.omnione.did.data.model.provider.Provider();
    provider.setDid(vcMetaContract.issuer.did);
    provider.setCertVcRef(vcMetaContract.issuer.certVcReference);
    vcMeta.setIssuer(provider);

    org.omnione.did.data.model.vc.CredentialSchema credentialSchema = new org.omnione.did.data.model.vc.CredentialSchema();
    credentialSchema.setId(vcMetaContract.credentialSchema.id);
    credentialSchema.setType(vcMetaContract.credentialSchema.credentialSchemaType);

    vcMeta.setCredentialSchema(credentialSchema);

    return vcMeta;
  }

  /**
   * Converts a VC schema to a smart contract object.
   * This method transforms verifiable credential schemas including metadata,
   * claim definitions, and namespace information for blockchain storage.
   *
   * @param vcSchema The VC schema to convert
   * @return OpenDID.VcSchema object for smart contract usage
   */
  static OpenDID.VcSchema convertToContractObject(VcSchema vcSchema) {
    var contractMetaData = new OpenDID.MetaData(
        vcSchema.getMetadata()
            .getFormatVersion(),
        vcSchema.getMetadata()
            .getLanguage()
    );
    var claimsList = vcSchema.getCredentialSubject()
        .getClaims();

    List<CredentialSubject> contractCredentialSubjectList = new ArrayList<>();
    List<OpenDID.VCSchemaClaim> vcSchemaClaimList = new ArrayList<>();
    for (SchemaClaims claims : claimsList) {

      List<SchemaClaimItem> contractClaimItems = new ArrayList<>();
      for (ClaimDef claimDef : claims.getItems()) {
        var item = new OpenDID.SchemaClaimItem(
            claimDef.getCaption(),
            claimDef.getFormat(),
            claimDef.isHideValue(),
            claimDef.getId(),
            claimDef.getType()
        );
        contractClaimItems.add(item);
      }

      org.omnione.did.data.model.schema.Namespace namespace = claims.getNamespace();
      OpenDID.ClaimNamespace contractNameSpace = new ClaimNamespace(
          namespace.getId(),
          namespace.getName(),
          namespace.getRef()
      );
      OpenDID.VCSchemaClaim vcSchemaClaim = new OpenDID.VCSchemaClaim(
          contractClaimItems,
          contractNameSpace
      );

      vcSchemaClaimList.add(vcSchemaClaim);
    }
    OpenDID.CredentialSubject credentialSubject =
        new OpenDID.CredentialSubject(vcSchemaClaimList);

    var contractVcSchema = new OpenDID.VcSchema(
        vcSchema.getId(),
        vcSchema.getSchema(),
        vcSchema.getTitle(),
        vcSchema.getDescription(),
        contractMetaData,
        credentialSubject
    );

    return contractVcSchema;
  }

  /**
   * Converts smart contract VC schema to a Java object.
   * This method transforms blockchain VC schema representations back to standard Java objects,
   * reconstructing claim definitions and namespace structures.
   *
   * @param schema The smart contract VC schema to convert
   * @return VcSchema object for Java usage
   */
  static VcSchema convertToJavaObject(OpenDID.VcSchema schema
  ) {
    VcSchema vcSchema = new VcSchema();
    vcSchema.setId(schema.id);
    vcSchema.setSchema(schema.schema);
    vcSchema.setTitle(schema.title);
    vcSchema.setDescription(schema.description);

    MetaData metaData = new MetaData();
    metaData.setLanguage(schema.metadata.language);
    metaData.setFormatVersion(schema.metadata.formatVersion);
    vcSchema.setMetadata(metaData);

    SchemaCredentialSubject credentialSubject = new SchemaCredentialSubject();
    List<SchemaClaims> claims = new ArrayList<>();

    for (var resultClaim : schema.credentialSubject.claims) {
      SchemaClaims claim = new SchemaClaims();

      org.omnione.did.data.model.schema.Namespace namespace = new org.omnione.did.data.model.schema.Namespace();
      namespace.setId(resultClaim.namespace.id);
      namespace.setName(resultClaim.namespace.name);
      namespace.setRef(resultClaim.namespace.ref);
      claim.setNamespace(namespace);

      List<@Valid ClaimDef> items = new ArrayList<>();
      for (var resultItem : resultClaim.items) {
        ClaimDef claimDef = new ClaimDef();
        claimDef.setCaption(resultItem.caption);
        claimDef.setFormat(resultItem.format);
        claimDef.setHideValue(resultItem.hideValue);
        claimDef.setId(resultItem.id);
        claimDef.setType(resultItem._type);
        items.add(claimDef);
      }
      claim.setItems(items);
      claims.add(claim);
    }

    credentialSubject.setClaims(claims);
    vcSchema.setCredentialSubject(credentialSubject);

    return vcSchema;
  }

  /**
   * Converts a credential schema to a smart contract object for ZKP operations.
   * This method transforms ZKP credential schemas including attribute names,
   * types, and internationalization data for blockchain storage.
   *
   * @param credentialSchema The credential schema to convert
   * @return ZKPLibrary_CredentialSchema object for smart contract usage
   */
  static ZKPLibrary_CredentialSchema convertToContractObject(CredentialSchema credentialSchema) {
    return new ZKPLibrary_CredentialSchema(
        credentialSchema.getId(),
        credentialSchema.getName(),
        credentialSchema.getVersion(),
        credentialSchema.getAttrNames(),
        convertToAttributeTypeList(credentialSchema.getAttrTypes()),
        credentialSchema.getTag()
    );
  }

  /**
   * Converts a list of attribute types to smart contract AttributeType list.
   * This helper method processes attribute type collections for blockchain compatibility.
   *
   * @param attributeTypes The list of attribute types to convert
   * @return List of smart contract AttributeType objects
   */
  private static List<AttributeType> convertToAttributeTypeList(
      List<org.omnione.did.zkp.datamodel.schema.AttributeType> attributeTypes
  ) {
    List<AttributeType> result = new ArrayList<>();
    if (attributeTypes != null) {
      for (var attributeType : attributeTypes) {
        result.add(convertToAttributeType(attributeType));
      }
    }
    return result;
  }

  /**
   * Converts a single attribute type to smart contract AttributeType.
   * This method transforms attribute type objects including namespace and item definitions
   * for blockchain storage.
   *
   * @param attributeType The attribute type to convert
   * @return Smart contract AttributeType object
   */
  private static AttributeType convertToAttributeType(
      org.omnione.did.zkp.datamodel.schema.AttributeType attributeType
  ) {
    var namespace = new OpenDID.AttributeNamespace(
        attributeType.getNamespace()
            .getId(),
        attributeType.getNamespace()
            .getName(),
        attributeType.getNamespace()
            .getRef() != null ? attributeType.getNamespace().getRef() : ""
    );
    List<OpenDID.AttributeItem> attributeItems = new ArrayList<>();
    if (attributeType.getItems() != null) {
      for (var attributeItem : attributeType.getItems()) {
        attributeItems.add(convertToAttributeItem(attributeItem));
      }
    }
    return new AttributeType(
        namespace,
        attributeItems
    );
  }

  /**
   * Converts an attribute definition to smart contract AttributeItem.
   * This method transforms attribute definitions including internationalization data
   * and type information for blockchain storage.
   *
   * @param attributeItem The attribute definition to convert
   * @return Smart contract AttributeItem object
   */
  private static OpenDID.AttributeItem convertToAttributeItem(
      org.omnione.did.zkp.datamodel.schema.AttributeDef attributeItem
  ) {
    List<OpenDID.Internationalization> i18n = new ArrayList<>();
    Map<String, String> i18nMap = attributeItem.getI18n();
    if (i18nMap != null) {
      for (var entry : i18nMap.entrySet()) {
        i18n.add(new OpenDID.Internationalization(
            entry.getKey(),
            entry.getValue()
        ));
      }
    } else {
      i18n = Collections.emptyList();
    }

    return new OpenDID.AttributeItem(
        attributeItem.getLabel(),
        attributeItem.getCaption(),
        attributeItem.getType() != null ? attributeItem.getType().getValue() : ATTR_TYPE.STRING.getValue(),
        i18n
    );
  }

  /**
   * Converts smart contract credential schema to a Java object.
   * This method transforms blockchain ZKP credential schema representations back to
   * standard Java objects, reconstructing attribute types and internationalization data.
   *
   * @param credentialSchema The smart contract credential schema to convert
   * @return CredentialSchema object for Java usage
   */
  static CredentialSchema convertToJavaObject(OpenDID.ZKPLibrary_CredentialSchema credentialSchema
  ) {
    var result = new CredentialSchema();
    result.setId(credentialSchema.id);
    result.setName(credentialSchema.name);
    result.setVersion(credentialSchema.version);
    result.setAttrNames(credentialSchema.attrNames);
    result.setAttrTypes(convertToJavaAttributeTypeList(credentialSchema.attrTypes));
    result.setTag(credentialSchema.tag);
    return result;
  }

  /**
   * Converts smart contract AttributeType list to Java attribute type list.
   * This helper method processes blockchain attribute type collections back to
   * standard Java representations.
   *
   * @param attributeTypes The list of smart contract AttributeType objects to convert
   * @return List of Java attribute type objects
   */
  private static List<org.omnione.did.zkp.datamodel.schema.AttributeType> convertToJavaAttributeTypeList(
      List<OpenDID.AttributeType> attributeTypes
  ) {
    List<org.omnione.did.zkp.datamodel.schema.AttributeType> result = new ArrayList<>();
    if (attributeTypes != null) {
      for (var attributeType : attributeTypes) {
        var namespace = new Namespace();
        namespace.setId(attributeType.namespace.id);
        namespace.setName(attributeType.namespace.name);
        namespace.setRef(attributeType.namespace.ref);
        var attributeTypeObj = new org.omnione.did.zkp.datamodel.schema.AttributeType();
        attributeTypeObj.setNamespace(namespace);
        List<org.omnione.did.zkp.datamodel.schema.AttributeDef> items = new ArrayList<>();
        if (attributeType.items != null) {
          for (var attributeItem : attributeType.items) {
            var attributeDef = new org.omnione.did.zkp.datamodel.schema.AttributeDef();
            attributeDef.setLabel(attributeItem.label);
            attributeDef.setCaption(attributeItem.caption);
            attributeDef.setType(ATTR_TYPE.valueOf(attributeItem._type.toUpperCase()));
            Map<String, String> i18nMap = new HashMap<>();
            if (attributeItem.i18n != null) {
              for (var i18n : attributeItem.i18n) {
                i18nMap.put(
                    i18n.languageType,
                    i18n.value
                );
              }
            }
            attributeDef.setI18n(i18nMap);
            items.add(attributeDef);
          }
        }
        attributeTypeObj.setItems(items);
        result.add(attributeTypeObj);
      }
    }
    return result;
  }

  /**
   * Converts a credential definition to a smart contract object.
   * This method transforms credential definitions including JSON serialization
   * of complex value objects for blockchain storage.
   *
   * @param credentialDefinition The credential definition to convert
   * @return Smart contract CredentialDefinition object
   * @throws IllegalArgumentException If JSON serialization fails
   */
  static OpenDID.CredentialDefinition convertToContractObject(
      CredentialDefinition credentialDefinition
  ) {

    ObjectMapper objectMapper = new ObjectMapper();
    String value = "";
    try {
      value = objectMapper.writeValueAsString(credentialDefinition.getValue());

    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }

    return new OpenDID.CredentialDefinition(
        credentialDefinition.getId(),
        credentialDefinition.getSchemaId(),
        credentialDefinition.getVer(),
        credentialDefinition.getType().name(),
        value,
        credentialDefinition.getTag()
    );
  }

  /**
   * Converts smart contract credential definition to a Java object.
   * This method transforms blockchain credential definition representations back to
   * standard Java objects, including JSON parsing of value objects.
   *
   * @param credentialDefinition The smart contract credential definition to convert
   * @return CredentialDefinition object for Java usage
   */
  static CredentialDefinition convertToJavaObject(OpenDID.CredentialDefinition credentialDefinition
  ) {
    JsonObject credDefjson = new JsonObject();
    credDefjson.addProperty("id", credentialDefinition.id);
    credDefjson.addProperty("schemaId", credentialDefinition.schemaId);
    credDefjson.addProperty("ver", credentialDefinition.ver);
    credDefjson.addProperty("type", credentialDefinition._type);

    JsonObject valueObject = JsonParser.parseString(credentialDefinition.value).getAsJsonObject();
    credDefjson.add("value", valueObject);
    credDefjson.addProperty("tag", credentialDefinition.tag);

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    var result = new Gson().fromJson(gson.toJson(credDefjson), CredentialDefinition.class);
    return result;
  }
}