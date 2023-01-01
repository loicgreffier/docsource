# External

## Success

[Google](https://www.google.fr/).

<a href="https://www.google.com"> Google </a>.

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

![Image relative](content/folderOne/images/image.jpg)

![Image absolute](/content/folderOne/images/image.jpg)

![Image with title 1](/content/folderOne/images/image.jpg "Image 1")

![Image with title 2](/content/folderOne/images/image.jpg 'Image 2')

## Broken

[./folderTwo/does-not-exist](./folderTwo/does-not-exist).

# Mailto

## Success

[mailto:test@gmail](mailto:test@gmail).

## Broken

[mailto:testgmail](mailto:testgmail).
