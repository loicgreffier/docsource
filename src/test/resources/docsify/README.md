# External

## Success

[Google](https://www.google.fr/).

[Google being redirected](https://google.fr/).

<a href="https://www.google.com"> Google </a>.

<a target="_blank" href="https://www.google.com"> Google </a>.

<a href="https://www.google.com" target="_blank"> Google </a>.

<!-- Specific use-cases. Should be ignored by regex -->
<a href="${variable.name}"> Google </a>.
<a href="${variable.name"> Google </a>.
<a href="variable.name}"> Google </a>.

## Broken

[Gogle](https://www.gogle.fr/).

[https://www.testingmcafeesites.com/](https://www.testingmcafeesites.com/).

# Relative 

## Success

[./folder-one/page](./folder-one/page).

[folder-one/page](folder-one/page).

[./folder-one/page.md](./folder-one/page.md).

[/folder-one/page](/folder-one/page).

[/folder-one/page#success](/folder-one/page#success).

![Image](images/image.jpg)

![Image with title 1](images/image.jpg "Image 1")

![Image with title 2](images/image.jpg 'Image 2')

![Image with space](images/image%20with%20spaces.jpg "Image with spaces")

<img src="images/image.jpg"/>

<img src="images/image.jpg" title="Image with title 1" alt="Image with title 1"/>

<img title="Image with title 2" src="images/image.jpg" alt="Image with title 2"/>

<!-- Specific use-cases. Should be ignored by regex -->
<img src="${variable.name}"/>
<img src="${variable.name"/>
<img src="variable.name}"/>

## Broken

[./does-not-exist](./does-not-exist).

[/doesNotExist/folder-one/page](/doesNotExist/folder-one/page).

[/docsify/README](/docsify/README).

# Mailto

## Success

[mailto:test@gmail](mailto:test@gmail).

## Broken

[mailto:testgmail](mailto:testgmail).
