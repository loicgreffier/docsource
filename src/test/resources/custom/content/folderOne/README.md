# External Link

## Success

This is an external link to [Google](https://www.google.fr/).

## Broken

This is an external broken link to [a wrong URL](https://www.gogle.fr/).

## Redirect

This is an external link to [Google being redirected](https://google.fr/).

# Relative Link

## Relative

### Success

<!-- This not works in Docsify mode, but it has to in custom mode. -->
<!-- Relative link paths should be considered as absolute paths and auto-completed by "content" when necessary -->

This is a relative link to [./folderTwo/README](./folderTwo/README).

This is another relative link to [folderTwo/README](folderTwo/README).

![This is a relative link to an image](content/folderOne/images/spring-boot-logo.png)

### Broken

This is a relative broken link to [a page that does not exist](./folderTwo/does-not-exist).

## Absolute

### Success

<!-- This not works in Docsify mode, but it has to in custom mode. -->
<!-- Relative link paths should be considered as absolute paths and auto-completed by "content" when necessary -->

This is an absolute link to [/folderTwo/README](/folderTwo/README).

![This is a relative link to an image](/content/folderOne/images/spring-boot-logo.png)

# Mailto Link

## Success

This is a mailto link to [mailto:test@gmail](mailto:test@gmail).

## Broken

This is a broken mailto link to [mailto:testgmail](mailto:testgmail).
