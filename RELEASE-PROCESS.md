# Release Process

This document outlines the steps involved in the release process for the Wallet core SDK project.

## Versioning

The project follows the format `X.Y.Z` for versioning.

X (Major): In case of incompatibility with lower versions  
Y (Minor): When new features are added while being compatible with lower versions  
Z (Patch): Bug fixes and small changes while compatible with lower versions

> When the major version is changed, minor and patch are initialized to 0.  
  When the minor version is changed, the patch is initialized to 0.


## Releasing a New Version

1. Create an issue to define and track release-related activities. Choose a title that follows the
   format `X.Y.Z`.
2. Stop merging any new work into the main branch.
3. Check the release draft under the [CHANGE LOG](CHANGELOG.md) page to ensure that everything is in order.
4. Create and push the release tag in the format `X.Y.Z`:

    ```bash
    git tag -a vX.Y.Z -m "Release vX.Y.Z"
    git push origin vX.Y.Z
    ```

    As a result, the CI/CD pipeline will publish the release.