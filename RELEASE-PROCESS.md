# Release Process

This document outlines the release process for the project, ensuring smooth QA validation and version management.

## Versioning

The project follows the "X.Y.Z" versioning format.

- X (Major): Incompatible changes or significant updates
- Y (Minor): Backward-compatible new features
- Z (Patch): Backward-compatible bug fixes or minor improvements

> When the Major version is incremented, both Minor and Patch are reset to 0.
> <br>
> When the Minor version is incremented, the Patch is reset to 0.

## Release Procedure for New Versions

1. Review the Change Log  
   Check the [CHANGE LOG](CHANGELOG.md) to ensure all changes for the release are documented.

2. Create a Release Branch  
   Create the "release/QA-VX.Y.Z" branch for QA validation from the GitHub interface (e.g., release/QA-V1.0.0):
   - On the repository's main page, click the branch dropdown menu and select the current "develop" branch.
   - Once switched to the develop branch, click the branch dropdown again and type "release/QA-VX.Y.Z" in the "Find or create a branch" input field.
   - Select "Create branch: release/QA-VX.Y.Z from develop" to create the branch.

3. QA Process  
   Perform QA validation on the release branch, addressing any issues found. Once the QA process is complete, the release is approved.

4. Merge into main and develop  
   After QA Process, merge the release/QA-VX.Y.Z branch into both the main and develop branches.

5. Release via GitHub Action  
   When the release version is merged into the main branch, the [CI/CD pipeline](https://github.com/OmniOneID/did-release/blob/main/docs/CI_CD_PIPELINE.md) will be triggered to tag the release and automatically publish it on GitHub with the following details:
   - Version (tag)
   - Summary of changes (from the change log)
   - Source code archive
   - Distribution files
