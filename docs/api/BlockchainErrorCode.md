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

BlockchainErrorCode
==

- Topic: BlockchainErrorCode
- Author: Kim Jeong-heon, Kim Min-yong
- Date: 2024-08-29
- Version: v1.0.0

| Version          | Date       | Changes                  |
| ---------------- | ---------- | ------------------------ |
| v1.0.0           | 2024-08-29 | Initial version          |

<div style="page-break-after: always;"></div>

# Table of Contents
- [BlockchainErrorCode](#blockchainerrorcode)
- [Table of Contents](#table-of-contents)
- [Model](#model)
  - [BlockchainErrorCode](#blockchainerrorcode-1)
    - [Description](#description)
    - [Declaration](#declaration)
    - [Property](#property)
- [Error Code](#error-code)
  - [1. Common](#1-common)

# Model
## BlockchainErrorCode

### Description
```
Error struct for Fabric Contract. It has code and message pair.
Code starts with SSDKBCS.
```

### Declaration
```java
// Declaration in Java
public enum BlockchainErrorCode {
    private final String PREFIX_CODE = "SSDKBCS";
    private String code;
    private String message;
}
```

### Property

| Name           | Type       | Description                            | **M/O** | **Note**         |
|----------------|------------|----------------------------------------|---------|------------------|
| code           | String     | Error code. It starts with SSDKBCS     |    M    |                  | 
| message        | String     | Error description                      |    M    |                  | 

<br>

# Error Code
## 1. Common

| Error Code   | Error Message                                  | Description                       | Action Required                   |
|--------------|------------------------------------------------|-----------------------------------|-----------------------------------|
| SSDKBCS00001 | Failed to connect to contract network            | -                                 | Check contract network configuration |
| SSDKBCS00002 | Failed to execute smart contract transaction   | -                                 | Verify the provided parameters     |
| SSDKBCS00003 | Invalid did key url                            | Unable to parse did key url       | Check the format of the did key url |
