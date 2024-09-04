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

- 주제: Blockchain SDK API
- 작성: 김정헌, 김민용
- 일자: 2024-08-29
- 버전: v1.0.0

| 버전   | 일자       | 변경 내용                 |
| ------ | ---------- | --------------------------|
| v1.0.0 | 2024-08-29 | 초기 작성                 |


<div style="page-break-after: always;"></div>

# 목차
- [1. DID document 등록](#1-did-document-등록)
- [2. DID document 조회](#2-did-document-조회)
- [3. DID document 상태 변경](#3-did-document-상태-변경)
  - [3.1. ACTIVATED, DEACTIVATED, REVOKED](#31-activated-deactivated-revoked)
  - [3.2. TERMINATED](#32-terminated)
- [4. VC metadata 등록](#4-vc-metadata-등록)
- [5. VC metadata 조회](#5-vc-metadata-조회)
- [6. VC 상태 변경](#6-vc-상태-변경)


# 기능 목록
## 1. DID document 등록

### Class Name
`FabricContractApi`

### Function Name
`registDidDoc`

### Function Introduction
`DID document 최초 등록 또는 상태를 제외한 DID document 업데이트`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| invokedDocument    | InvokedDocument    | DidDoc 등록 및 업데이트 요청 문서 |M||
| roleType    | RoleType | 사업자 종류 Enum |M| |

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
    
    // blockchain network 정보 설정 및 api 객체 생성
    FabricContractApi contractApi = (FabricContractApi) ContractFactory.FABRIC.create("junit-platform.properties");

    // call
    contractApi.registDidDoc(invokedDocument, RoleType.TAS);
}
```

<br>

## 2. DID document 조회

### Class Name
`FabricContractApi`

### Function Name
`getDidDoc`

### Function Introduction
`특정 DID document 조회`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| didKeyUrl    | string    | DID 키 식별 주소 |M||

### Output Parameters

| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| DidDocAndStatus  | DidDocAndStatus 객체 |M| | 

### Function Declaration

```java
// Function declaration in Java
DidDocAndStatus getDidDoc(String didKeyUrl) throws BlockChainException;
```

### Function Usage
```java
void getDidDocTest() throws Exception {
    
    String didKeyUrl = "did:odi:tas?versionId=1";

    // blockchain network 정보 설정 및 api 객체 생성
    FabricContractApi contractApi = (FabricContractApi) ContractFactory.FABRIC.create("junit-platform.properties");

    // call
    DidDocAndStatus didDocAndStatus = contractApi.getDidDoc(didKeyUrl);
}
```

<br>

## 3. DID document 상태 변경

### Class Name
`FabricContractApi`

### Function Name
`updateDidDocStatus`

### Function Introduction
`DID document 상태 변경`<br>
`DID document 생애주기에 대한 상세 내용은 아래 표 참고 ('TERMINATED'로 상태 변경 시에는 terminatedTime 인자 필요)`

| Status Value | Name   | Description |
|--------------|--------|-------------|
| ACTIVATED   | 활성   | &bull; DEACTIVATED, REVOKED 상태로 전이 가능 |
| DEACTIVATED | 비활성 | &bull; ACTIVATED, REVOEKD 상태로 전이 가능   |
| REVOKED     | 폐기   | &bull; TERMINATED 상태로 전이 가능 |
| TERMINATED  | 말소   | &bull; 다른 상태로 전이 불가능 </br> &bull; 말소 시작일자 필요 |

### 3.1. ACTIVATED, DEACTIVATED, REVOKED
### Input Parameters
| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| didKeyUrl    | string    | DID 키 식별 주소 |M||
| didDocStatus | DidDocStatus | DID 문서 상태 Enum |M||

### Output Parameters

| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| DidDocument | DidDoc 객체 |M| 

### Function Declaration

```java
// Function declaration in Java
DidDocument updateDidDocStatus(String didUrl, DidDocStatus didDocStatus) throws BlockChainException;
```

### Function Usage
```java
void updateDidDocStatusTest() throws Exception {
    
    String didKeyUrl = "did:odi:tas?versionId=1";

    // blockchain network 정보 설정 및 api 객체 생성
    FabricContractApi contractApi = (FabricContractApi) ContractFactory.FABRIC.create("junit-platform.properties");

    // call
    DidDoc didDoc = contractApi.updateDidDoc(didKeyUrl, DidDocStatus.DEACTIVATED);
}
```

<br>

### 3.2. TERMINATED
### Input Parameters
| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| didKeyUrl    | string    | DID 키 식별 주소 |M||
| didDocStatus | DidDocStatus | DID 문서 상태 Enum |M||
| terminatedTime | LocalDateTime | 말소 시작 시간 |M||

### Output Parameters

| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| DidDocument | DidDoc 객체 |M||

### Function Declaration

```java
// Function declaration in Java
DidDocument updateDidDocStatus(String didUrl, DidDocStatus didDocStatus, LocalDateTime terminatedTime) throws BlockChainException;
```

### Function Usage
```java
void updateDidDocStatusTest() throws Exception {
    
    String didKeyUrl = "did:odi:tas?versionId=1";

    // blockchain network 정보 설정 및 api 객체 생성
    FabricContractApi contractApi = (FabricContractApi) ContractFactory.FABRIC.create("junit-platform.properties");

    // call
    DidDoc didDoc = contractApi.updateDidDoc(didKeyUrl, DidDocStatus.TERMINATED, LocalDateTime.now());
}
```

<br>

## 4. VC metadata 등록

### Class Name
`FabricContractApi`

### Function Name
`registVcMetadata`

### Function Introduction
`VC metadata 등록`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| vcMeta    | VcMeta    | VC 메타데이터 |M||


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


    // blockchain network 정보 설정 및 api 객체 생성
    FabricContractApi contractApi = (FabricContractApi) ContractFactory.FABRIC.create("application.properties");

    // call
    contractApi.registVcMetadata(vcMeta);
}
```

<br>

## 5. VC metadata 조회

### Class Name
`FabricContractApi`

### Function Name
`getVcMetadata`

### Function Introduction
`특정 VC metadata 조회`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| vcId    | string    | VC 아이디 |M||


### Output Parameters

| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| VcMeta  | VcMeta 객체 |M| |


### Function Declaration

```Java
// Function declaration in Java
VcMeta getVcMetadata(String vcId) throws BlockChainException;
```

### Function Usage
```java
void getVcMetadataTest() throws Exception {

    String vcId = "vcId";

    // blockchain network 정보 설정 및 api 객체 생성
    FabricContractApi contractApi = (FabricContractApi) ContractFactory.FABRIC.create("junit-platform.properties");

    // call
    VcMeta vcMeta = contractApi.getVcMetadata(vcId);
}
```

<br>

## 6. VC 상태 변경

### Class Name
`FabricContractApi`

### Function Name
`updateVcStatus`

### Function Introduction
`특정 VC 상태 변경`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| vcId    | string    | VC 아이디 |M||
| vcStatus    | VcStatus | VC 상태 Enum |M| |

> VC 상태 Enum
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

### Function Usage
```java
void updateVcStatusTest() throws Exception {

    String vcId = "vcId";

    // blockchain network 정보 설정 및 api 객체 생성
    FabricContractApi contractApi = (FabricContractApi) ContractFactory.FABRIC.create("junit-platform.properties");

    // call
    contractApi.updateVcStatus(vcId, VcStatus.INACTIVE);
}
```