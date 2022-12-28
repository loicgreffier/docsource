# Docsource

[![GitHub Build](https://img.shields.io/github/actions/workflow/status/loicgreffier/docsource/continuous_integration.yml?branch=main&logo=github&style=for-the-badge)](https://github.com/loicgreffier/docsource/actions/workflows/continuous_integration.yml)
[![GitHub Stars](https://img.shields.io/github/stars/loicgreffier/docsource?logo=github&style=for-the-badge)](https://github.com/loicgreffier/docsource)
[![GitHub Watch](https://img.shields.io/github/watchers/loicgreffier/docsource?logo=github&style=for-the-badge)](https://github.com/loicgreffier/docsource)
[![Docker Pulls](https://img.shields.io/docker/pulls/loicgreffier/docsource?label=Pulls&logo=docker&style=for-the-badge)](https://hub.docker.com/r/loicgreffier/docsource/tags)
[![Docker Stars](https://img.shields.io/docker/stars/loicgreffier/docsource?label=Stars&logo=docker&style=for-the-badge)](https://hub.docker.com/r/loicgreffier/docsource)

**Docsource** is a CLI that detects broken links inside Markdown documentations from the source code.

# Table of Contents

* [Download](#download)
* [Usage](#usage)
  * [Scan](#scan)
  * [Overview](#overview)
    * [External Links](#external-links)
    * [Relative Links](#relative-links)
    * [Mailto Links](#mailto-links)
  * [CLI](#cli)
    * [Directory](#directory)
* [Motivation](#motivation)

# Download

# Usage

## Scan 


# Functioning 

Docsource can check the 3 kinds of Markdown links:
- External links
- Relative links
- Mailto links

## External Links

External links are links pointing to an external domain.

For those links, Docsource sends an HTTP request and checks the HTTP return code:
- the link is broken when the return code is 400 and higher.
- the link is valid when the return code is strictly lower than 400.
As it can be complicated to verify redirected links (code 3xx) because of several reasons (e.g. authentication before accessing the resource), they are always considered as valid.

## Relative Links

Relative links are used for links within the same domain.

For those links, Docsource checks the linked resource actually exists:
- the link is broken if the linked resource does not exist.
- the link is valid if the linked resource exist.

A relative link can be:
- absolute: the link is checked from the user current directory. It can be overridden with `-c` option.
- relative: the link is checked from the file it belongs.

## Mailto Links

Mailto links are used to include a link with an email address.

For those links, Docsource checks the format of the linked email address:
- the link is broken if the format is wrong.
- the link is valid if the format is good.

# Motivation

Maintaining a documentation made of tens or hundreds of pages can be painful. 
And nothing more frustrating for readers than falling onto a broken link.

Docsource is helping to maintain a documentation up-to-date and detect links that may be broken.
