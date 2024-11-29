# Docsource

[![GitHub Build](https://img.shields.io/github/actions/workflow/status/loicgreffier/docsource/push_main.yml?branch=main&logo=github&style=for-the-badge)](https://github.com/loicgreffier/docsource/actions/workflows/push_main.yml)
[![GitHub Stars](https://img.shields.io/github/stars/loicgreffier/docsource?logo=github&style=for-the-badge)](https://github.com/loicgreffier/docsource)
[![GitHub Watch](https://img.shields.io/github/watchers/loicgreffier/docsource?logo=github&style=for-the-badge)](https://github.com/loicgreffier/docsource)
[![Docker Pulls](https://img.shields.io/docker/pulls/loicgreffier/docsource?label=Pulls&logo=docker&style=for-the-badge)](https://hub.docker.com/r/loicgreffier/docsource/tags)
[![Docker Stars](https://img.shields.io/docker/stars/loicgreffier/docsource?label=Stars&logo=docker&style=for-the-badge)](https://hub.docker.com/r/loicgreffier/docsource)

Docsource is a command-line interface (CLI) tool that detects broken links within Markdown documentation files.

![](.readme/demo.gif)

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
        * [Multiple Folders or Files](#multiple-folders-or-files)
        * [All Absolute](#all-absolute)
* [Continuous Integration](#continuous-integration)
    * [GitLab CI/CD](#gitlab-cicd)
* [FAQ](#frequently-asked-questions-faq)
* [Motivation](#motivation)

## Download

You can download Docsource from the [GitHub releases page](https://github.com/loicgreffier/docsource/releases). It is
available in three different formats:

- JAR (requires Java 21)
- Windows
- Linux

## Getting Started

Docsource should be run from the root directory of your documentation.

```bash
docsource scan --recursive . 
```

### Supported Frameworks

Docsource tries to detect the framework used to generate the documentation and applies the appropriate configuration.
The supported frameworks are:

- [Docsify](https://docsify.js.org)
- [Hugo](https://gohugo.io)

You can also provide the `--content-directory` and `--image-directory` flags manually to specify the directories
containing the
Markdown files and images, respectively.

## Links

Docsource can check three types of Markdown links: external links, relative links, and mailto links.

### External Links

External links are links that point to an external domain. Docsource sends an HTTP request to check the HTTP return
code:

- The link is considered broken if the return code is 404.
- The link is considered valid if the return code is anything other than 404.

### Relative Links

Relative links are used for links within the same domain. Docsource checks whether the linked resource actually exists:

- The link is considered broken if the linked resource does not exist.
- The link is considered valid if the linked resource exists.

A relative link can be either absolute or relative:

- Absolute links are checked from the root directory of the documentation concatenated with the content directory
  provided by the `--content-directory` flag or with the image directory provided by the `--image-directory` flag.
- Relative links are checked from the file to which the link belongs, unless the `--all-absolute` flag is enabled.

### Mailto Links

Mailto links are used to include a link with an email address. Docsource checks the format of the linked email address:

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

Usage: docsource scan [-AhkrvV] [--skip-external] [--skip-mailto] [--skip-relative]
                [--content-directory=<contentDirectory>]
                [--image-directory=<imageDirectory>] [files...]

Description:

Scan documentation.

Parameters:
      [files...]        Root directories or files to scan.

Options:
  -A, --all-absolute    Consider relative link paths as absolute paths.
      --content-directory=<contentDirectory>
                        If different from the root directory, the directory containing the Markdown files. E.g., 'content' for Hugo.
  -h, --help            Show this help message and exit.
      --image-directory=<imageDirectory>
                        If existing, the root directory of the images. E.g., 'static' for Hugo.
  -k, --insecure        Turn off hostname and certificate chain verification.
  -r, --recursive       Scan directories recursively.
      --skip-external   Skip external links.
      --skip-mailto     Skip mailto links.
      --skip-relative   Skip relative links.
  -v, --verbose         Enable the verbose mode.
  -V, --version         Print version information and exit.
```

`Scan` is used to scan Markdown files in your documentation to detect broken links.
The command should be run from the root folder of your documentation.

#### Multiple Folders or Files

You can provide multiple folders or files as input to `Scan` by specifying them after the command:

```console
docsource scan directory1 directory2 file1.md file2.md
```

#### All Absolute

Depending on how your documentation is built (e.g., a custom Angular/React project that parses Markdown files), you may
need to consider relative link paths as absolute paths.

- E.g., you may want `[link](./folder-two/README)` to be checked from the content directory rather than the "
  folder-two" directory.

To handle such cases, you can use the `--all-absolute` option.

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
