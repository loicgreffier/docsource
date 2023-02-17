# Docsource

[![GitHub Build](https://img.shields.io/github/actions/workflow/status/loicgreffier/docsource/continuous_integration.yml?branch=main&logo=github&style=for-the-badge)](https://github.com/loicgreffier/docsource/actions/workflows/continuous_integration.yml)
[![GitHub Stars](https://img.shields.io/github/stars/loicgreffier/docsource?logo=github&style=for-the-badge)](https://github.com/loicgreffier/docsource)
[![GitHub Watch](https://img.shields.io/github/watchers/loicgreffier/docsource?logo=github&style=for-the-badge)](https://github.com/loicgreffier/docsource)
[![Docker Pulls](https://img.shields.io/docker/pulls/loicgreffier/docsource?label=Pulls&logo=docker&style=for-the-badge)](https://hub.docker.com/r/loicgreffier/docsource/tags)
[![Docker Stars](https://img.shields.io/docker/stars/loicgreffier/docsource?label=Stars&logo=docker&style=for-the-badge)](https://hub.docker.com/r/loicgreffier/docsource)

**Docsource** is a CLI that detects broken links inside Markdown documentations from the source code.

![](.readme/demo.gif)

# Table of Contents

* [Download](#download)
* [Overview](#overview)
  * [External Links](#external-links)
  * [Relative Links](#relative-links)
  * [Mailto Links](#mailto-links)
* [Usage](#usage)
  * [Scan](#scan)
* [Continuous Integration](#continuous-integration)
  * [GitLab CI/CD](#gitlab-cicd)
* [FAQ](#frequently-asked-questions-faq)
* [Motivation](#motivation)

# Download

Docsource can be downloaded at https://github.com/loicgreffier/docsource/releases and is available in 3 different formats:
- JAR (Java 17 required)
- Windows
- Linux

# Overview

Docsource can check the 3 kinds of Markdown links:
- External links
- Relative links
- Mailto links

## External Links

External links are links pointing to an external domain.

For those links, Docsource sends an HTTP request and checks the HTTP return code:
- the link is broken when the return code is 404.
- the link is valid when the return code is different from 404.

## Relative Links

Relative links are used for links within the same domain.

For those links, Docsource checks the linked resource actually exists:
- the link is broken if the linked resource does not exist.
- the link is valid if the linked resource exist.

A relative link can be:
- absolute: the link is checked from the user current directory.
- relative: the link is checked from the file it belongs, unless the `--all-absolute` has been enabled.

## Mailto Links

Mailto links are used to include a link with an email address.

For those links, Docsource checks the format of the linked email address:
- the link is broken if the format is wrong.
- the link is valid if the format is good.

# Usage

## Scan 

```console
   ____
  (|   \
   |    | __   __   ,   __          ,_    __   _
  _|    |/  \_/    / \_/  \_|   |  /  |  /    |/
 (/\___/ \__/ \___/ \/ \__/  \_/|_/   |_/\___/|__/


Description:

Scan documentation.

Parameters:
      [files...]        Directories or files to scan.

Options:
  -A, --all-absolute    Consider relative link paths as absolute paths.
  -c, --current-dir=<currentDir>
                        Override the current directory.
  -h, --help            Show this help message and exit.
  -k, --insecure        Turn off hostname and certificate chain verification.
  -p, --path-prefix=<pathPrefix>
                        Prefix the beginning of relative links with a partial path.
  -r, --recursive       Scan directories recursively.
      --skip-external   Skip external links.
      --skip-mailto     Skip mailto links.
      --skip-relative   Skip relative links.
  -V, --version         Print version information and exit.
```

Scan needs to be run at the root folder of your documentation.

### Multiple folders/files

Multiple folders or files can be given in input.

```console
docsource scan directory1 directory2 file1.md file2.md
```

### All absolute and Path prefix

Depending on how your documentation is built (e.g., a custom Angular/React project that parses Markdown files) you may need to:
- consider relative link paths as absolute paths. 
E.g., you may want `[link](./folder-two/README)` to be checked from your current directory rather than the "folderTwo" directory.
- add a prefix to your relative paths. 
E.g., your link is `[link](./folder-two/README)` but the actual path is `/content/folderTwo/README` where `content` is handled automatically by your parser.

For these cases:
- `--all-absolute` checks relative link paths as absolute paths
- `--path-prefix` allows to add a partial path at the beginning of each relative links

# Continuous Integration

## GitLab CI/CD

Docsource can be run in a GitLab pipeline using the [Docker image](https://hub.docker.com/r/loicgreffier/docsource).

```yaml
check links:
  stage: verify 🩺
  image: loicgreffier/docsource:latest
  script:
    - docsource scan --recursive .
```

# Frequently Asked Questions (FAQ)

[How can Docsource trust my SSL certificates](#how-can-docsource-trust-my-ssl-certificates)

## How can Docsource trust my SSL certificates

You can make Docsource trust your SSL certificates if you need it.

If you use the JAR, add your SSL certificates in the JVM cacerts.

If you use the native executables, you can load a trust store dynamically at runtime as [specified by GraalVM](https://www.graalvm.org/22.1/reference-manual/native-image/CertificateManagement/).

```console
docsource scan . -Djavax.net.ssl.trustStore=pathToTheTrustStore -Djavax.net.ssl.trustStorePassword=trustStorePassword
```

# Motivation

Maintaining a documentation made of tens or hundreds of pages can be painful. 
And nothing more frustrating for readers than falling onto a broken link.

Docsource is helping to maintain a documentation up-to-date and detect links that may be broken.

It was also an opportunity to test [Spring Native](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/)!
