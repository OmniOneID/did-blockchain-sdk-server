# Contribution Guidelines
We appreciate your contributions! We want to make contributing to this project as easy and transparent as possible, whether it's:

- Reporting a bug
- Discussing the current state of the code
- Submitting a fix
- Proposing new features

## Contribution Process
Thank you for contributing to the project! This guide explains how to contribute to the project, the code style rules, and the review criteria. Please familiarize yourself with the following content before making any contributions.

1. **Creating an Issue**
  - Before starting any work, create an issue related to the task you wish to work on. All work, including bug fixes, feature improvements, and new proposals, should be initiated after the issue is registered.
  - When creating an issue, use the provided issue template and fill in accurate information.
2. **Issue Assignment**
  - Once the issue is created, the administrator will review and may assign the issue to an external contributor.
  - Before being assigned, make sure that the same task is not already in progress.
3. **Fork and Branch Creation**
  - Refer to [Pull Request Guidelines](#pull-request-guidelines)
4. **Writing Code**
  - Ensure that the code strictly follows the project's [Coding Style](#coding-style) guidelines.
  - Submit the PR after confirming that all tests pass successfully.
5. **Creating a Pull Request (PR)**
  - Commit your work and create a PR.
  - In the PR description, provide detailed information about the changes and link the related issue number to connect it to the issue.
  - Write concise and clear commit messages.
    - Refer to [Commit Message Guideline](#commit-message-guidelines)
6. **Code Review**
  - Once the PR is created, the administrator will review the code. Update the PR by incorporating the reviewer’s feedback.
  - Once the review is complete, the administrator will merge the PR.

## Detailed Contribution Guide via GitHub
Please refer to [Detailed Contribution Guide via GitHub](https://github.com/OmniOneID/did-doc-architecture/blob/main/how_to_contribute_to_open_did.md) for detailed instructions on how to contribute to the project. This guide includes command examples and screenshots to help you better understand the process.

## Code Review Standards
We conduct code reviews based on the following standards to ensure that your contribution maintains consistency with the project and upholds quality:

1. **Code Quality**
  - Write code that is easy to read and maintain.
  - Avoid overly complex logic, and if possible, suggest better solutions.
  - Minimize duplicated code and check if the code can be refactored into reusable modules.
2. **Feature Verification**
  - Test the new feature or bug fix to ensure it works as intended.
  - All tests must pass before submitting the PR.
  - Include any additional necessary tests in the PR.
3. **Code Style Compliance**
  - Ensure that your code follows the project's [Coding Style](#coding-style).
  - Feedback will be given if there are formatting issues or violations of naming conventions.
4. **Commit Messages**
  - Ensure that commit messages clearly describe the changes made.
  - Avoid including too many modifications in a single commit. If possible, divide changes into smaller commits.
5. **Documentation**
  - If new features, APIs, or configuration changes are introduced, make sure to update or add relevant documentation.
  - Write appropriate comments in the code, especially for explaining complex logic.

##  Code of Conduct
We are committed to fostering a contribution environment where everyone is treated with respect. Please make sure to read and follow the [Code of Conduct](CODE_OF_CONDUCT.md) before starting your contribution.

## Issue Reporting Guidelines
We use GitHub Issues to track and manage bugs.
If you encounter a bug, please open a new issue on GitHub. When reporting an issue, please use the provided issue template to clearly describe the problem. This helps us resolve the issue more quickly.

### Writing a Good Bug Report
A good bug report includes the following:
- A quick summary or background of the issue.
- Steps to reproduce the bug (be specific and detailed).
- Expected vs. actual results.
- Any additional context or information that could help us diagnose the issue.

## Pull Request Guidelines
1. **Fork** from the `develop` branch of the remote repository.
2. **Develop the feature**, and ensure the code has been properly tested.
3. If necessary, update the **documentation** to reflect the changes.
4. Verify that all tests pass and that the code adheres to our coding style and linting rules.
5. Once the work is complete, submit a **pull request** to the `develop` branch.
6. When creating the pull request, assign an appropriate **reviewer** to review the code changes.  
   It is recommended to choose someone familiar with the codebase, considering their availability for providing feedback.
7. Also, assign **assignees** to clarify responsibility for the task.  
   Assign **all maintainers of the repository** as assignees to ensure accountability and a smooth review process.

> **Note**: Assignees are responsible for ensuring the pull request is reviewed and merged appropriately. Assigning all maintainers as assignees is a crucial step to ensure they are aware of the changes and can respond quickly.

## Coding Style
Our coding style is based on the [OpenDID Coding Style](https://github.com/OmniOneID/did-doc-architecture/blob/main/docs/rules/coding_style.md). Adhering to these guidelines ensures that the code is clean, readable, and maintainable.

## Document Creation and Editing Guide
Our document creation process follows the [OpenDID Document Guide](https://github.com/OmniOneID/did-doc-architecture/blob/main/docs/guide/docs/write_document_guide.md). Most of the design documents we create use the Markdown (*.md) format, ensuring consistency and ease of collaboration.

## Commit Message Guidelines
Our commit messages follow the [OpenDID Commit Rule](https://github.com/OmniOneID/did-doc-architecture/blob/main/docs/rules/git_code_commit_rule.md). Well-structured commit messages help others understand the intent behind your changes and make it easier to navigate the code history.

## Signing the CLA
Before we can accept your pull request, you must submit a CLA. This only needs to be done once. Complete your CLA here: [Contributor License Agreement](CLA.md)

## License
[Apache 2.0](LICENSE)