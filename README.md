<div align="center">
  <img src="https://github.com/DeweyReed/ClipboardCleaner/blob/master/image/ic_launcher-web.png?raw=true" height="128" />
</div>

<h1 align="center">Clipboard Cleaner</h1>

<div align="center">
  <strong>Check and clean your clipboard using service, widget, shortcut and quick setting tile.</strong>
</div>
<div align="center">
    <a href="https://play.google.com/store/apps/details?id=io.github.deweyreed.clipboardcleaner">
        <img src="https://img.shields.io/badge/Download-PlayStore-green.svg"/>
    </a>
    <a href="https://www.coolapk.com/apk/180063">
        <img src="https://img.shields.io/badge/Download-CoolApk-green.svg"/>
    </a>
    <a href="https://github.com/DeweyReed/ClipboardCleaner/releases">
        <img src="https://img.shields.io/badge/Download-Github-green.svg"/>
    </a>
</div>
</br>

[Chinese Doc 中文文档](https://github.com/DeweyReed/ClipboardCleaner/blob/master/README_ZH.md)

## Screenshot

||||
|:-:|:-:|:-:|
|![screenshot1](https://github.com/DeweyReed/ClipboardCleaner/blob/master/image/screenshot_1.png?raw=true)|![screenshot2](https://github.com/DeweyReed/ClipboardCleaner/blob/master/image/screenshot_2.png?raw=true)|![screenshot3](https://github.com/DeweyReed/ClipboardCleaner/blob/master/image/screenshot_3.png?raw=true)|

## Why

This is an app I personally use to prevent my clipboard being r***d and clean sensitive info timely.

As you may know, Android clipboard content and its changes can be got by any app, which is a security hole if you care about it(more info is mentioned [here](https://github.com/grepx/android-clipboard-security)).
Any app can get your copied password, credit card numbers and more.

However, ClipboardCleaner can't protect your passwords from being got by any app(after all it's a security hole). But it gives you some ways to check and clean your clipboard.

Fortunatelly, newer Android versions use AutoFill or other mechanism to handle sensitive info.

## Known issues and TODOs

1. Service won't always work for Android O(Background restraint)
1. Xposed support(I'm not good at this one. Waiting for someone who knows about it)

## Update

1.1 => Use keyword/regex to determine if clipboard should be cleaned.

## License
[MIT License](https://github.com/DeweyReed/ClipboardCleaner/blob/master/LICENSE)