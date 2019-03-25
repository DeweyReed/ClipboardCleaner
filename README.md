<div align="center">
  <img src="https://github.com/DeweyReed/ClipboardCleaner/blob/master/image/ic_launcher-web.png?raw=true" height="128" />
</div>

<h1 align="center">Clipboard Cleaner</h1>

<div align="center">
  <strong>Check and clean your clipboard using service, widget, shortcut and quick setting tile.</strong>
</div>
</br>
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

## Screenshot

||||
|:-:|:-:|:-:|
|![screenshot1](https://github.com/DeweyReed/ClipboardCleaner/blob/master/image/screenshot_1.png?raw=true)|![screenshot2](https://github.com/DeweyReed/ClipboardCleaner/blob/master/image/screenshot_2.png?raw=true)|![screenshot3](https://github.com/DeweyReed/ClipboardCleaner/blob/master/image/screenshot_3.png?raw=true)|

## Why

This is an app I personally use to protect my clipboard and clean sensitive info.

As you may know, Android clipboard content and its changes can be got by any app, which is a security hole if you care about it(more info is mentioned [here](https://github.com/grepx/android-clipboard-security)).
Any app can get your copied password, credit card numbers and more.

However, ClipboardCleaner can't protect your passwords from being got by any app(after all it's a security hole). But it gives you some ways to check and clean your clipboard.

Fortunately, newer Android versions use AutoFill or other mechanism to handle sensitive info. Android Q will also provide more security enhancement about the clipboard.

## Known issues and TODOs

1. [Help Wanted] XPosed support
1. [Can't Fix] Many soft keyboard apps and third-party ROMs/vendors store clipboard history. Clipboard Cleaner can nothing about this.

## Update

- 1.3.0

    - Added a timeout option for the service
    - Added a help icon which explains why this app may not work.
    - Upgraded the target sdk to Android Pie

- 1.2.1 => Fix app crash after system restarting on Android O or later
- 1.2 => Use foreground service to monitor clipboard changes
- 1.1 => Use keyword/regex to determine if clipboard should be cleaned.

## License

[MIT License](https://github.com/DeweyReed/ClipboardCleaner/blob/master/LICENSE)
