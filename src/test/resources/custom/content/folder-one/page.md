# External

## Success

[Google](https://www.google.fr/).

[Google being redirected](https://google.fr/).

<a href="https://www.google.com"> Google </a>.

## Broken

[Gogle](https://www.gogle.fr/).

# Relative

## Success

<!-- This not works in Docsify mode, but it has to in custom mode. -->
<!-- Relative link paths should be considered as absolute paths and auto-completed by "content" when necessary -->

[./folder-two/page](./folder-two/page).

[folder-two/page](folder-two/page).

[./folder-two/page.md](./folder-two/page.md).

[/folder-two/page](/folder-two/page).

![Image relative](content/folder-one/images/image.jpg)

![Image absolute](/content/folder-one/images/image.jpg)

![Image with title 1](/content/folder-one/images/image.jpg "Image 1")

![Image with title 2](/content/folder-one/images/image.jpg 'Image 2')

## Broken

[./folder-two/does-not-exist](./folder-two/does-not-exist).

# Mailto

## Success

[mailto:test@gmail](mailto:test@gmail).

## Broken

[mailto:testgmail](mailto:testgmail).
