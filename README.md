# credit-appsec-demo

A small Spring Boot credit application API, built with a full GitHub Actions security pipeline wrapped around it: static analysis, secret scanning, dependency scanning, container scanning, and dynamic analysis, each catching a different layer of risk.

## Why this exists

Reading about SAST, DAST, and SCA is one thing. Actually wiring up Semgrep, Gitleaks, Dependency-Check, Trivy, and OWASP ZAP into working pipelines, hitting their real bugs, fixing real environment issues, and watching them catch real findings, builds a different kind of understanding. This repo is the result of that process, including some of the debugging history, because the debugging taught as much as the final green checkmarks did.

## Threat model, briefly

This is a small API for creating and searching credit applications, plus a login endpoint. The realistic risks here are injection through user-supplied fields reaching the database or the response HTML, credentials or secrets ending up in logs or source, vulnerable third-party dependencies shipping known CVEs, and an insecure container image or runtime configuration undermining otherwise clean application code.

## The pipeline

Three GitHub Actions workflows, each scanning a different layer.

| Workflow | Stage | Tools | What it catches |
|---|---|---|---|
| `ci.yml` | Code | Semgrep | Java level bug patterns, including a custom rule for credential logging |
| `ci.yml` | Code | Gitleaks | Committed secrets and credentials, regardless of file type |
| `ci.yml` | Dependencies | OWASP Dependency-Check | Known CVEs in declared Maven dependencies (`pom.xml`) |
| `container.yml` | Container | Trivy | Known CVEs in the built jar and the underlying base image |
| `dast.yml` | Runtime | OWASP ZAP (baseline) | Live behavior of the running app, including missing security headers |

All four scanning tools upload their findings to GitHub's native Security tab through SARIF, so results from every tool live in one place regardless of which one produced them.

### Why each tool

Semgrep is fast, reads source code directly, is cheap to run on every push, and supports custom rules. Gitleaks pattern matches raw text across any file type, which matters because Semgrep only parses actual programming languages and can't meaningfully scan `.properties` or `.yml` files for secrets. OWASP Dependency-Check reads the declared dependency tree straight from `pom.xml` and checks it against the NVD. Trivy inspects the actual built artifact and base OS image, a layer Dependency-Check has no visibility into since it never opens a container. OWASP ZAP is the only tool here that tests the app's actual runtime behavior instead of reading files, which lets it catch things static analysis structurally can't, and also miss things static analysis can catch.

## Findings this pipeline actually caught

CVE-2022-42889 in `commons-text` 1.9 (Text4Shell, critical severity) was flagged independently by both Dependency-Check and Trivy, from two different inputs: the declared dependency list and the built jar. It's a good concrete example of defense in depth, since two tools with two different mechanisms landed on the same real finding.

A SQL injection in the search endpoint, caused by raw JDBC string concatenation, was flagged by Semgrep's `spring-sqli` rule.

Unpinned GitHub Actions in the workflow files themselves, using mutable tags like `@v4` and `@v2` instead of fixed commit SHAs, were flagged by Semgrep's own supply chain rule set. This is a real, known attack vector, the same one used in incidents like the `tj-actions/changed-files` compromise.

Missing security headers, including CSP, X-Content-Type-Options, and anti-clickjacking headers, were flagged by OWASP ZAP against the live running container.


## Local development

```bash
./mvnw -B verify          # build and test
./mvnw spring-boot:run    # run locally on :8080
docker build -t credit-appsec-demo .
docker run -p 8080:8080 credit-appsec-demo
```
