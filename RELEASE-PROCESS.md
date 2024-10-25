# Release Process

This document describes the Release Process for QA validation and deployment of feature additions and modifications for each repository. It covers version management and QA validation procedures for each module, as well as the process for managing the integrated Release of all modules.

## 1. Versioning

The project follows a versioning rule in the format "X.Y.Z" where:
- X (Major): Significant changes that are not backward compatible
- Y (Minor): New features that are backward compatible
- Z (Patch): Bug fixes or minor improvements that are backward compatible

> When the Major version is updated, both Minor and Patch are reset to 0.
> <br>
> When the Minor version is updated, the Patch is reset to 0.

The integrated module version follows a four-digit format "W.X.Y.Z" assigned after QA approval.

- W.X: Official product number
- Y: Release
- Z: Bug fix

## 2. Release Procedure for Each Repository

Each module (repository) is managed independently, following these steps:

1. **Change Log Review**  
   Review the [CHANGE LOG](CHANGELOG.md) for each module to ensure all changes are recorded.

2. **Create a Release Branch**  
   If there are changes or modifications, create a branch "release/QA-VX.Y.Z" for QA validation.
   - Example: If there are bug fixes or minor improvements for V1.0.0, create a branch "release/QA-V1.0.1".

   For modules without changes, use the existing version (V1.0.0) and the already distributed JAR or library.

3. **QA Validation**
   - Perform QA validation on the Release branch, addressing any issues identified during the process.
   - Approve the Release once QA validation is complete.

4. **Merge into Main and Develop Branches**
   - Merge the validated Release branch (release/QA-VX.Y.Z) into both `main` and `develop` branches.

5. **Create a Release for Each Repository**
   - When the validated branch is merged into `main`, trigger the [CI/CD pipeline](https://github.com/OmniOneID/did-release/blob/main/docs/CI_CD_PIPELINE.md) using GitHub Actions to create the Release and perform version tagging. The generated [Release](https://github.com/OmniOneID/did-blockchain-sdk-server/releases) includes the following:
      - Version name
      - Summary of the changelog
      - Source code archive
      - Distributed files
   - Delete the release/QA-VX.Y.Z branch after the release.

## 3. Integrated Release Management (did-release Repository)

After QA approval, manage the complete version control of all modules in a separate repository called [did-release](https://github.com/OmniOneID/did-release/).

1. **Managing QA Request Branches**
   - Create a directory for the QA request version (format: W.X.Y.Z, e.g., V1.0.1.0). The directory name should be /release-VW.X.Y.Z (e.g., /release-V1.0.1.0).
   - Gather the version names of the branches created for QA validation (release/QA-VX.Y.Z) and document version information and modifications in a table within the directory. Name the file 'QA-PLAN-VW.X.Y.Z.md' and register it in the issue menu of the `did-release` repository.
   - Include versions of unchanged modules as well.

2. **Individual Releases After QA Approval**
   - Once all modules have passed QA validation, and their respective `release/QA-VX.Y.Z` branches are merged into `main` and `develop`, create releases for each repository.

3. **Publishing the Integrated Release**
   - After all modules are approved and released, manage the integrated Release in the `did-release` repository.
   - Retrieve the latest version tags for each module and post them in the Release menu of the `did-release`.
   - Document the release information in a file named release-VW.X.Y.Z.md within the previously created directory.

   - Example:

   ## Release Note V1.0.1.0

   | Repository                | Version         | Changelog                                                                                  | Release                    |
   |---------------------------| --------------- |--------------------------------------------------------------------------------------------| --------------------------- |
   | did-blockchain-sdk-server | V1.0.1          | [Changelog](https://github.com/OmniOneID/did-blockchain-sdk-server/blob/main/CHANGELOG.md) |
   | ..                        | ..              | ..                                                                                         |

<br>

## 4. Automating Integrated Release Script

Use GitHub Actions or Shell scripts to automate the following tasks:
- Retrieve the release tag information for each module and publish the integrated Release.
   - Once all module releases are completed, gather the latest Release information for each module and publish the final version in the `did-release` repository to manage the overall project release.
   - The integrated release version follows the "W.X.Y.Z" format, based on the most significant changes.

This process enables efficient management of individual module versions and the overall integrated project release.
