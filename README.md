<div align="center">

<img src=".readme/logo.svg" alt="Docsource"/>

# Docsource

[![GitHub Build](https://img.shields.io/github/actions/workflow/status/loicgreffier/docsource/push_main.yml?branch=main&logo=github&style=for-the-badge)](https://github.com/loicgreffier/docsource/actions/workflows/push_main.yml)
[![GitHub Release](https://img.shields.io/github/v/release/loicgreffier/docsource?logo=github&style=for-the-badge)](https://github.com/loicgreffier/docsource/releases)
[![GitHub Stars](https://img.shields.io/github/stars/loicgreffier/docsource?logo=github&style=for-the-badge)](https://github.com/loicgreffier/docsource)
[![Docker Pulls](https://img.shields.io/docker/pulls/loicgreffier/docsource?label=Pulls&logo=docker&style=for-the-badge)](https://hub.docker.com/r/loicgreffier/docsource/tags)
[![SonarCloud Coverage](https://img.shields.io/sonar/coverage/loicgreffier_docsource?logo=sonarcloud&server=https%3A%2F%2Fsonarcloud.io&style=for-the-badge)](https://sonarcloud.io/component_measures?id=loicgreffier_docsource&metric=coverage&view=list)
[![SonarCloud Tests](https://img.shields.io/sonar/tests/loicgreffier_docsource/main?server=https%3A%2F%2Fsonarcloud.io&style=for-the-badge&logo=sonarcloud)](https://sonarcloud.io/component_measures?metric=tests&view=list&id=loicgreffier_docsource)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?logo=apache&style=for-the-badge)](https://opensource.org/licenses/Apache-2.0)

[Download](https://github.com/loicgreffier/docsource/releases) • [Getting Started](#getting-started)

Command-line interface for detecting broken links in Markdown documentation files.

![](.readme/demo.gif)

</div>

## Table of Contents

* [Download](#download)
* [Getting Started](#getting-started)
    * [Supported Frameworks](#supported-frameworks)
    * [Links](#links)
        * [External Links](#external-links)
        * [Relative Links](#relative-links)
        * [Mailto Links](#mailto-links)
* [Usage](#usage)
    * [Scan](#scan)
        * [All Absolute](#all-absolute---all-absolute)
        * [Content Directory](#content-directory---content-directory)
        * [Image Directory](#image-directory---image-directory)
        * [Index Filename](#index-filename---index-filename)
* [Continuous Integration](#continuous-integration)
    * [GitLab CI/CD](#gitlab-cicd)
* [FAQ](#frequently-asked-questions-faq)
* [Motivation](#motivation)

## Download

You can download Docsource from the [GitHub releases page](https://github.com/loicgreffier/docsource/releases).
It is available in four different formats:

- JAR (requires Java 21)
- Windows
- Linux, statically linked with Musl
- MacOS

Additionally, Docker images are available on [Docker Hub](https://hub.docker.com/r/loicgreffier/docsource):

## Getting Started

Docsource should be run from the root directory of your documentation.

```bash
docsource scan --recursive . 
```

### Supported Frameworks

Docsource tries to detect the framework used to generate the documentation and applies the appropriate configuration.
The supported and tested frameworks are:

- [Docsify](https://docsify.js.org)
- [Hugo](https://gohugo.io)

### Links

Docsource can check three types of Markdown links: external links, relative links, and mailto links.

#### External Links

External links point to an external domain.
Docsource sends an HTTP request and check the HTTP return code:

- The link is considered broken if the return code is 404.
- The link is considered valid if the return code is anything other than 404.

#### Relative Links

Relative links point to the same domain.
Docsource checks if the linked resource actually exists:

- The link is considered broken if the linked resource does not exist.
- The link is considered valid if the linked resource exists.

A relative link can be either absolute or relative:

- Absolute links are checked from the current user directory (i.e., the root directory of the documentation)
  concatenated with the content directory if provided (`--content-directory`) or the image directory for images if
  provided (`--image-directory`).
- Relative links are checked from the file the link belongs to.

#### Mailto Links

Mailto links point to an email address.
Docsource checks the format of the email address:

- The link is considered broken if the format is incorrect.
- The link is considered valid if the format is correct.

## Usage

### Scan

```console
  ____
 (|   \
  |    | __   __   ,   __          ,_    __   _
 _|    |/  \_/    / \_/  \_|   |  /  |  /    |/
(/\___/ \__/ \___/ \/ \__/  \_/|_/   |_/\___/|__/

Usage: docsource scan [-AhIkrvV] [--skip-external] [--skip-mailto] [--skip-relative] [--content-directory=<contentDirectory>]
                [--image-directory=<imageDirectory>] [--index-filename=<indexFilename>] [files...]

Description:

Scan documentation.

Parameters:
      [files...]         Root directories or files to scan.

Options:
  -A, --all-absolute     Consider relative paths as absolute paths.
      --content-directory=<contentDirectory>
                         Specify a sub-directory of the root directory containing the Markdown files. E.g., 'content' for Hugo.
  -h, --help             Show this help message and exit.
  -I, --image-absolute   Consider relative image paths as absolute paths.
      --image-directory=<imageDirectory>
                         Specify a sub-directory of the root directory containing the images. E.g., 'static' for Hugo.
      --index-filename=<indexFilename>
                         Specify the filename to use as an index file. E.g., '_index.md' for Hugo.
  -k, --insecure         Turn off hostname and certificate chain verification.
  -r, --recursive        Scan directories recursively.
      --skip-external    Skip external links.
      --skip-mailto      Skip mailto links.
      --skip-relative    Skip relative links.
  -v, --verbose          Enable the verbose mode.
  -V, --version          Print version information and exit.
```

`Scan` is used to scan Markdown files in your documentation to detect broken links.
The command should be run from the root folder of your documentation.

#### All Absolute (`--all-absolute`)

If you need to check relative link paths as absolute paths rather than from the file the link belongs to, you can use
the `--all-absolute` option.

#### Content Directory (`--content-directory`)

If the documentation is located in a subdirectory of the root directory, you can specify the content directory using
the `--content-directory` option.

E.g., for Hugo, the content directory is `content`.

#### Image Directory (`--image-directory`)

If the images are located in a subdirectory of the root directory, you can specify the image directory using the
`--image-directory` option.

E.g., for Hugo, the image directory is `static`.

#### Index Filename (`--index-filename`)

If the documentation uses a specific filename as an index file, you can specify the index filename using the
`--index-filename` option.

E.g., for Hugo, the index filename is `_index.md`.

## Continuous Integration

### GitLab CI/CD

Docsource can be run in a GitLab pipeline using the [Docker image](https://hub.docker.com/r/loicgreffier/docsource).

```yaml
check links:
  stage: verify 🩺
  image: loicgreffier/docsource
  script:
    - docsource scan --recursive .
```

## Frequently Asked Questions (FAQ)

[How can Docsource trust my SSL certificates](#how-can-docsource-trust-my-ssl-certificates)

### How can Docsource trust my SSL certificates

You can make Docsource trust your SSL certificates if you need it.

If you use the JAR, add your SSL certificates in the JVM cacerts.

If you use the native executables, you can load a trust store dynamically at runtime
as [specified by GraalVM](https://www.graalvm.org/22.1/reference-manual/native-image/CertificateManagement/).

```console
docsource scan . -Djavax.net.ssl.trustStore=pathToTheTrustStore -Djavax.net.ssl.trustStorePassword=trustStorePassword
```

## Motivation

Maintaining documentation with tens or hundreds of pages can be a pain, and there's nothing more frustrating for readers
than encountering broken links. Docsource helps keep documentation up-to-date and detects broken links.

It was also an opportunity to test
out [Spring Native](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/)!
