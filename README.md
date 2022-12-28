# Docsource

[![GitHub Build](https://img.shields.io/github/actions/workflow/status/loicgreffier/docsource/continuous_integration.yml?branch=main&logo=github&style=for-the-badge)](https://github.com/loicgreffier/docsource/actions/workflows/continuous_integration.yml)
[![GitHub Stars](https://img.shields.io/github/stars/loicgreffier/docsource?logo=github&style=for-the-badge)](https://github.com/loicgreffier/docsource)
[![GitHub Watch](https://img.shields.io/github/watchers/loicgreffier/docsource?logo=github&style=for-the-badge)](https://github.com/loicgreffier/docsource)
[![Docker Pulls](https://img.shields.io/docker/pulls/loicgreffier/docsource?label=Pulls&logo=docker&style=for-the-badge)](https://hub.docker.com/r/loicgreffier/docsource/tags)
[![Docker Stars](https://img.shields.io/docker/stars/loicgreffier/docsource?label=Stars&logo=docker&style=for-the-badge)](https://hub.docker.com/r/loicgreffier/docsource)

Docsource is a CLI that validates all the links a Markdown documentation contains directly from the source code.

# Table of Contents

* [Download](#download)
* [Usage](#usage)
  * [Overview](#overview)
    * [External Links](#external-links)
    * [Relative Links](#relative-links)
    * [Mailto Links](#mailto-links)
  * [CLI](#cli)
    * [Directory](#directory)
* [Motivation](#motivation)

# Usage

## Overview

The main capability of Docsource is to detect broken links inside Markdown documentations.

There are 3 kinds of links:
- **External** - a link that points to an external domain.
- **Relative** - a link that points to a resource within the same domain.
- **Mailto** - a link that contains an email address. 

### External Links

External links are links pointing to an external domain. 

Docsource will check the status of external links according to the returned HTTP code:
- **400** and higher, the link is broken.
- **3xx**, the link is redirected. 
As redirection can occur for multiple reasons (e.g. authentication required before accessing the resource), a redirected link is considered as a valid link.
- **2xx**, the link is valid.

### Relative Links

Relative links are used for links within the same domain.

Docsource will check the status of relative links by verifying the linked resource actually exists at the specified path:
- if the path points to a resource that does not exist, the link is broken.
- if the paths points to a resource that actually exist, the link is valid.

### Mailto Links

Mailto links are used to include a link with an email address. 

Docsource will check the status of mailto links by verifying the format of the email addresses:
- if the format is wrong, the link is broken.
- if the format is valid, the link is valid.

## CLI

### Directory

Docsource will look for all Markdown files inside a given directory.

`docsource scan .`

# Motivation

Maintaining a documentation made of tens or hundreds of pages can be painful. 
And nothing more frustrating for readers than falling onto a broken link.

Docsource is helping to maintain a documentation up-to-date and detect links that may be broken.
