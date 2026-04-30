# Security Policy

## Supported Versions
We actively provide security updates for the following versions:


| Version | Supported          |
|---------| ------------------ |
| 5.3.x   | :white_check_mark: |
| < 5.3.0 | :x:                |

## KRITIS Compliance & SBOM (BSI TR-03183)
This project aims for compliance with the German BSI Technical Guideline **TR-03183** for Cyber Resilience.

- **SBOM:** A Software Bill of Materials in **CycloneDX (Schema 1.6)** format is generated for every release.
- **Availability:** You can find the `bom.json` as a release asset on GitHub or as a signed artifact on Maven Central.
- **Integrity:** All official SBOMs are GPG-signed to ensure software supply chain integrity.

## Reporting a Vulnerability
We take the security of Jexxa Adapters seriously. If you believe you have found a security vulnerability, please help us fix it by reporting it responsibly.

**Please do not report security vulnerabilities via public GitHub issues.**

### How to report
1. Send an email to **[security@jexxa.io](mailto:security@jexxa.io)**.
2. Include a detailed description of the vulnerability, steps to reproduce, and the potential impact.
3. We will acknowledge receipt of your report within 48 hours and provide a timeline for the fix.

### Our Commitment
- We will coordinate a fix and a public disclosure with you.
- We follow the principle of **Coordinated Vulnerability Disclosure (CVD)**.
- We do not pursue legal action against researchers who act in good faith and follow this policy.

## Security Advisories
- **To report a vulnerability:** Please go to the [Security tab](https://github.com/jexxa-projects/JLegMed/security) of this repository and select "Report a vulnerability".
- **Official Advisories:** Once published, security advisories will be listed [here](https://github.com/jexxa-projects/JLegMed/security/advisories).