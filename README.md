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

# Usage

## Overview

The main capability of Docsource is to detect broken links inside Markdown documentations.

There are 3 kind of links:
- **External** - a link that points to an external domain.
- **Relative** - a link that points to a resource within the same domain.
- **Mailto** - a link that contains an email address. 

### External Links

External links are links pointing to an external domain. 
They contain text displayed to the user enclosed in square brackets [] and URL enclosed in parenthesis (). 

Docsource will ping all the external links and deduct a status according to the HTTP code:
- **400** and higher is a broken link
- **3xx** is a redirected link. 
As redirection can occur for multiple reasons (e.g. authentication required before accessing the resource), a redirected link is considered as a valid link.
- **2xx** is a valid link.

### Relative Links

### Mailto Links

## CLI

### Directory

Docsource will look for all Markdown files inside a given directory.

`docsource scan .`

# Motivation

Maintaining a documentation made of tens or hundreds of pages can be painful. 
And nothing more frustrating for readers than falling onto a broken link.

Docsource is helping to maintain a documentation up-to-date and detect links that may be broken.