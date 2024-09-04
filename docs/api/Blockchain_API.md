---
puppeteer:
    pdf:
        format: A4
        displayHeaderFooter: true
        landscape: false
        scale: 0.8
        margin:
            top: 1.2cm
            right: 1cm
            bottom: 1cm
            left: 1cm
    image:
        quality: 100
        fullPage: false
---

Blockchain SDK API
==

- Topic: Blockchain SDK API
- Author: Kim Jeong-heon, Kim Min-yong
- Date: 2024-08-29
- Version: v1.0.0

| Version | Date       | Changes                                   |
| ------- | ---------- | ----------------------------------------- |
| v1.0.0  | 2024-08-29 | Initial version                           |

<div style="page-break-after: always;"></div>

# Table of Contents
- [1. DID Document Registration](#1-did-document-registration)
- [2. DID Document Retrieval](#2-did-document-retrieval)
- [3. DID Document Status Change](#3-did-document-status-change)
  - [3.1. ACTIVATED, DEACTIVATED, REVOKED](#31-activated-deactivated-revoked)
  - [3.2. TERMINATED](#32-terminated)
- [4. VC Metadata Registration](#4-vc-metadata-registration)
- [5. VC Metadata Retrieval](#5-vc-metadata-retrieval)
- [6. VC Status Change](#6-vc-status-change)


# Feature List
## 1. DID Document Registration

### Class Name
`FabricContractApi`

### Function Name
`registDidDoc`

### Function Description
`Initial registration of a DID document or updating a DID document excluding its status`

### Input Parameters

| Parameter | Type            | Description                               | **M/O** | **Notes** |
|-----------|-----------------|-------------------------------------------|---------|-----------|
| invokedDocument | InvokedDocument | Document for DID registration and update request |  M       |           |
| roleType       | RoleType       | Enum for provider type                     | M       | See Data Specification |

### Output Parameters

void

### Function Declaration

```java
// Function declaration in Java
void registDidDoc(InvokedDocument invokedDocument, RoleType roleType) throws BlockchainException;
```

### Function Usage
```java
void registDidDocTest() throws Exception {
    // encoding did document
    String document = "z29KbMzaxCukd5iyCozP3CQEHiR...CkRAC8usDbDrsvxPc5";
    Provider controller = Provider.builder()...build();
    Proof proof = Proof.builder()...build();

    InvokedDocument invokedDocument = InvokedDocument.builder()
                                        .didDoc(document)
                                        .controller(controller)
                                        .proof(proof)
                                        .nonce("12345")
                                        .build();
    
    // Set up blockchain network information and create API object
    FabricContractApi contractApi = (FabricContractApi) ContractFactory.FABRIC.create("junit-platform.properties");

    // call
    contractApi.registDidDoc(invokedDocument, RoleType.TAS);
}
```

<br>

## 2. DID Document Retrieval

### Class Name
`FabricContractApi`

### Function Name
`getDidDoc`

### Function Description
`Retrieve a specific DID document`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **Notes** |
|-----------|--------|----------------------------|---------|-------------|
| didKeyUrl | string | DID key identifier URL     | M       |             |

### Output Parameters

| Type           | Description                | **M/O** | **Notes** |
|----------------|----------------------------|---------|-------------|
| DidDocAndStatus | DidDocAndStatus object     | M       | As of v0.0.1, it is specified as a FabricResponse object <br/> DidDocAndStatus will be reflected later |

### Function Declaration

```java
// Function declaration in Java
DidDocAndStatus getDidDoc(String didKeyUrl) throws BlockChainException;
```

# Function Usage
```java
void getDidDocTest() throws Exception {
    
    String didKeyUrl = "did:odi:das/segment?versionId=1#public-key-0";

    // Set up blockchain network information and create api object
    FabricContractApi contractApi = (FabricContractApi) ContractFactory.FABRIC.create("junit-platform.properties");

    // call
    DidDocAndStatus didDocAndStatus = contractApi.getDidDoc(didKeyUrl);
}
```

<br>

## 3. DID Document Status Change

### Class Name
`FabricContractApi`

### Function Name
`updateDidDocStatus`

### Function Description
`Change the status of a specific DID document`<br>
`For detailed information about the DID document lifecycle, refer to the table below (the parameter terminatedTime is required when changing the status to 'TERMINATED').`

| Status Value | Name       | Description |
|--------------|------------|-------------|
| ACTIVATED    | Active     | &bull; Can transition to DEACTIVATED or REVOKED status |
| DEACTIVATED  | Inactive   | &bull; Can transition to ACTIVATED or REVOKED status |
| REVOKED      | Revoked    | &bull; Can transition to TERMINATED status |
| TERMINATED   | Terminated | &bull; Cannot transition to any other status </br> &bull; Termination start date is required |

### 3.1. ACTIVATED, DEACTIVATED, REVOKED
### Input Parameters
| Parameter    | Type         | Description                    | **M/O** | **Remarks** |
|--------------|--------------|--------------------------------|---------|-------------|
| didKeyUrl    | string       | DID key identifier URL         | M       |             |
| didDocStatus | DidDocStatus | DID document status Enum       | M       |             |

### Output Parameters

| Type            | Description                | **M/O** | **Notes** |
|-----------------|----------------------------|---------|-----------|
| DidDocument     | DidDoc object              | M       |  |

### Function Declaration

```java
// Function declaration in Java
DidDocument updateDidDocStatus(String didUrl, DidDocStatus didDocStatus) throws BlockChainException;
```

### Function Usage
```java
void updateDidDocStatusTest() throws Exception {
    
    String didKeyUrl = "did:odi:das/segment?versionId=1#public-key-0";

    // Set up blockchain network information and create API object
    FabricContractApi contractApi = (FabricContractApi) ContractFactory.FABRIC.create("junit-platform.properties");

    // Call
    DidDoc didDoc = contractApi.updateDidDoc(didKeyUrl, DidDocStatus.TERMINATED, LocalDateTime.now());
}
```

### Function Usage
```java
void updateDidDocStatusTest() throws Exception {
    
    String didKeyUrl = "did:odi:tas?versionId=1";

    // Set up blockchain network information and create API object
    FabricContractApi contractApi = (FabricContractApi) ContractFactory.FABRIC.create("junit-platform.properties");

    // Call
    DidDoc didDoc = contractApi.updateDidDoc(didKeyUrl, DidDocStatus.DEACTIVATED);
}
```

<br>

### 3.2. TERMINATED
### Input Parameters
| Parameter        | Type           | Description                | **M/O** | **Notes** |
|------------------|----------------|----------------------------|---------|-----------|
| didKeyUrl        | string         | DID key identifier URL     | M       |           |
| didDocStatus     | DidDocStatus   | DID document status Enum   | M       |           |
| terminatedTime   | LocalDateTime | Start time of termination   | M       |           |



### Output Parameters

| Type            | Description                | **M/O** | **Notes** |
|-----------------|----------------------------|---------|-----------|
| DidDocument     | DidDoc object              | M       |  |

### Function Declaration

```java
// Function declaration in Java
DidDocument updateDidDocStatus(String didUrl, DidDocStatus didDocStatus, LocalDateTime terminatedTime) throws BlockChainException;
```

### Function Usage
```java
void updateDidDocStatusTest() throws Exception {
    
    String didKeyUrl = "did:odi:tas?versionId=1";

    // Set up blockchain network information and create API object
    FabricContractApi contractApi = (FabricContractApi) ContractFactory.FABRIC.create("junit-platform.properties");

    // Call
    DidDoc didDoc = contractApi.updateDidDoc(didKeyUrl, DidDocStatus.TERMINATED, LocalDateTime.now());
}
```

<br>

## 4. VC Metadata Registration

### Class Name
`FabricContractApi`

### Function Name
`registVcMetadata`

### Function Description
`VC metadata registration`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **Notes** |
|-----------|--------|----------------------------|---------|-----------|
| vcMeta    | VcMeta | VC metadata                 | M       |           |


### Output Parameters
void

### Function Declaration

```java
// Function declaration in Java
void registVcMetadata(VcMeta vcMeta) throws BlockChainException;
```

### Function Usage
```java
void registVcMetadataTest() throws Exception {

    ProviderDetail issuer = ProviderDetail.builder()...build();
    Credential credentialSchema = CredentialSchema.builder()...build();

    VcMeta vcMeta = VcMeta.builder()
                        .id("vcId")
                        .status(VcStatus.ACTIVE.getRawValue())
                        .issuer(issuer)
                        .credentialSchema(credentialSchema)...build();


    // Set blockchain network information and create API object
    FabricContractApi contractApi = (FabricContractApi) ContractFactory.FABRIC.create("application.properties");

    // Call
    contractApi.registVcMetadata(vcMeta);
}
```
<br>

## 5. VC metadata Retrieval

### Class Name
`FabricContractApi`

### Function Name
`getVcMetadata`

### Function Description
`Specific VC metadata Retrieval`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **Notes** |
|-----------|--------|----------------------------|---------|---------|
| vcId    | string    | VC ID |M||


### Output Parameters

| Type | Description                |**M/O** | **Notes** |
|------|----------------------------|---------|---------|
| VcMeta  | VcMeta Object |M| |


### Function Declaration

```Java
// Function declaration in Java
VcMeta getVcMetadata(String vcId) throws BlockChainException;
```

### Function Usage
```java
void getVcMetadataTest() throws Exception {

    String vcId = "vcId";

    // Set up blockchain network information and create API object
    FabricContractApi contractApi = (FabricContractApi) ContractFactory.FABRIC.create("junit-platform.properties");

    // Call
    VcMeta vcMeta = contractApi.getVcMetadata(vcId);
}
```

<br>

## 6. VC Status Change

### Class Name
`FabricContractApi`

### Function Name
`updateVcStatus`

### Function Description
`Change the status of a specific VC`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **Notes** |
|-----------|--------|----------------------------|---------|-----------|
| vcId      | string | VC ID                       | M       |           |
| vcStatus  | VcStatus | VC status Enum            | M       |           |

> VC Status Enum
>
> - ACTIVE
> - INACTIVE
> - REVOKED

### Output Parameters

void


### Function Declaration

```java
// Function declaration in Java
void updateVcStatus(String vcId, VcStatus vcStatus) throws BlockChainException;
```

# Function Usage
```java
void updateVcStatusTest() throws Exception {

    String vcId = "vcId";

    // Set blockchain network information and create API object
    FabricContractApi contractApi = (FabricContractApi) ContractFactory.FABRIC.create("junit-platform.properties");

    // Call
    contractApi.updateVcStatus(vcId, VcStatus.INACTIVE);
}
```