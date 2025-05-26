package org.omnione.did.ethereum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.zkp.datamodel.definition.CredentialDefinition;
import org.omnione.did.zkp.datamodel.schema.AttributeDef.ATTR_TYPE;
import org.omnione.did.zkp.datamodel.schema.CredentialSchema;
import org.omnione.did.zkp.datamodel.schema.Namespace;
import org.omnione.generated.OpenDID;
import org.omnione.generated.OpenDID.AttributeType;
import org.omnione.generated.OpenDID.ZKPLibrary_CredentialSchema;

final class EvmDataConverter {

  private EvmDataConverter() {
    throw new AssertionError("Utility class");
  }

  static OpenDID.Document convertToContractObject(DidDocument didDocument) {

    var verificationMethodList = didDocument.getVerificationMethod()
        .stream()
        .map(value -> new OpenDID.VerificationMethod(
            value.getId(),
            new BigInteger(value.getType()),
            value.getController(),
            value.getPublicKeyMultibase(),
            new BigInteger(String.valueOf(value.getAuthType()))
        ))
        .toList();
    var servicesList = didDocument.getService()
        .stream()
        .map(value -> new OpenDID.Service(
            value.getId(),
            value.getType(),
            value.getServiceEndpoint()
        ))
        .toList();

    return new OpenDID.Document(
        didDocument.getContext(),
        didDocument.getId(),
        didDocument.getController(),
        didDocument.getCreated(),
        didDocument.getUpdated(),
        didDocument.getVersionId(),
        didDocument.getDeactivated(),
        verificationMethodList,
        didDocument.getAssertionMethod(),
        didDocument.getAuthentication(),
        didDocument.getKeyAgreement(),
        didDocument.getCapabilityInvocation(),
        didDocument.getCapabilityDelegation(),
        servicesList
    );
  }

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

  private static AttributeType convertToAttributeType(
      org.omnione.did.zkp.datamodel.schema.AttributeType attributeType
  ) {
    var namespace = new OpenDID.AttributeNamespace(
        attributeType.getNamespace()
            .getId(),
        attributeType.getNamespace()
            .getName(),
        attributeType.getNamespace()
            .getRef()
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
    }
    return new OpenDID.AttributeItem(
        attributeItem.getLabel(),
        attributeItem.getCaption(),
        attributeItem.getType()
            .getValue(),
        i18n
    );
  }

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

  private static List<org.omnione.did.zkp.datamodel.schema.AttributeType> convertToJavaAttributeTypeList(
      List<OpenDID.AttributeType> attributeTypes
  ) {
    List<org.omnione.did.zkp.datamodel.schema.AttributeType> result = new ArrayList<>();
    if (attributeTypes != null) {
      for (var attributeType : attributeTypes) {
        var namespace = new Namespace();
        namespace.setId(attributeType.namespace.id);
        namespace.setRef(attributeType.namespace.ref);
        var attributeTypeObj = new org.omnione.did.zkp.datamodel.schema.AttributeType();
        attributeTypeObj.setNamespace(namespace);
        List<org.omnione.did.zkp.datamodel.schema.AttributeDef> items = new ArrayList<>();
        if (attributeType.items != null) {
          for (var attributeItem : attributeType.items) {
            var attributeDef = new org.omnione.did.zkp.datamodel.schema.AttributeDef();
            attributeDef.setLabel(attributeItem.label);
            attributeDef.setCaption(attributeItem.caption);
            attributeDef.setType(ATTR_TYPE.valueOf(attributeItem._type));
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

  static OpenDID.CredentialDefinition convertToContractObject(
      CredentialDefinition credentialDefinition
  ) {

    ObjectMapper objectMapper = new ObjectMapper();
    String value = "";
    try {
      value = objectMapper.writeValueAsString(credentialDefinition.getValue()
          .getPrimary());
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }

    return new OpenDID.CredentialDefinition(
        credentialDefinition.getId(),
        credentialDefinition.getSchemaId(),
        credentialDefinition.getVer(),
        Integer.toString(credentialDefinition.getType()
            .getValue()),
        value,
        credentialDefinition.getTag()
    );
  }

  static CredentialDefinition convertToJavaObject(OpenDID.CredentialDefinition credentialDefinition
  ) {
    var result = new CredentialDefinition();
    result.setId(credentialDefinition.id);
    result.setSchemaId(credentialDefinition.schemaId);
    result.setTag(credentialDefinition.tag);
    return result;
  }
}
