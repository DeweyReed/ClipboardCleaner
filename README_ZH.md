<div align="center">
  <img src="https://github.com/DeweyReed/ClipboardCleaner/blob/master/image/ic_launcher-web.png?raw=true" height="128" />
</div>

<h1 align="center">剪贴板守护</h1>

<div align="center">
  <strong>使用服务、小控件、快捷方式和快捷设置查看并清空你的剪贴板</strong>
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

## 截图

||||
|:-:|:-:|:-:|
|![screenshot1](https://github.com/DeweyReed/ClipboardCleaner/blob/master/image/screenshot_4.png?raw=true)|![screenshot2](https://github.com/DeweyReed/ClipboardCleaner/blob/master/image/screenshot_5.png?raw=true)|![screenshot3](https://github.com/DeweyReed/ClipboardCleaner/blob/master/image/screenshot_6.png?raw=true)|

## Why

这是我的一个自用应用，保护手机的剪贴板不被随意强*并及时清理敏感信息。

Android的剪贴板内容和变化是可以被任何应用获取的，这算一个安全漏洞（如果你在意的话，更多信息在[这儿](https://github.com/grepx/android-clipboard-security)）。任何应用可以得到你复制的密码、银行卡信息等等等等。

但是，剪贴板守护不能保护你的剪贴板内容被窃取（不然就不叫安全漏洞了），只是给你了一些查看和清理剪贴板的方法。

好消息是新版本的Android系统使用AutoFill来处理敏感信息，绕过了不安全的剪贴板。

## 已知的问题和TODOs

1. XPosed支持（等一位懂XPosed开发的有缘人帮助）

## 更新

- 1.2.1 => 修复了奥利奥及以后系统上，重启系统后应用崩溃的漏洞
- 1.2 => 使用前台服务监控剪贴板
- 1.1 => 使用关键字/正则选择性清空剪贴板
``
## License

[MIT License](https://github.com/DeweyReed/ClipboardCleaner/blob/master/LICENSE)