# External

## Success

[Google](https://www.google.fr/).

## Broken

[Gogle](https://www.gogle.fr/).

## Redirect

[Google being redirected](https://google.fr/).

# Relative

## Success

<!-- This not works in Docsify mode, but it has to in custom mode. -->
<!-- Relative link paths should be considered as absolute paths and auto-completed by "content" when necessary -->

[./folderTwo/page](./folderTwo/page).

[folderTwo/page](folderTwo/page).

[./folderTwo/page.md](./folderTwo/page.md).

[/folderTwo/page](/folderTwo/page).

![This is a relative link to an image](content/folderOne/images/spring-boot-logo.png).

![This is a relative link to an image](/content/folderOne/images/spring-boot-logo.png)

## Broken

[./folderTwo/does-not-exist](./folderTwo/does-not-exist).

# Mailto

## Success

[mailto:test@gmail](mailto:test@gmail).

## Broken

[mailto:testgmail](mailto:testgmail).
