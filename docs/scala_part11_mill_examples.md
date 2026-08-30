# Scala 教學 - 第十一部分: Mill 與可執行範例

> [« 上一篇：Modern Scala 3](scala_part10_modern_scala3.md) | [📚 目錄](../README.md) | [下一篇：測試 »](scala_part12_testing.md)

---

## 目錄
1. [為什麼使用 Mill](#1-為什麼使用-mill)
2. [專案結構](#2-專案結構)
3. [常用指令](#3-常用指令)
4. [Example Module](#4-example-module)
5. [新增範例的方式](#5-新增範例的方式)
6. [疑難排解](#6-疑難排解)

---

## 1. 為什麼使用 Mill

Mill 是 Scala build tool，重點是快速 incremental builds、明確 modules，以及容易自動化的 CLI workflow。

本專案使用 Mill 管理可執行範例，原因是它能在保持學習專案輕量的同時，支援 compilation、tests、dependencies 與 multiple modules。

---

## 2. 專案結構

可執行 Scala 程式碼位於 `examples/`：

```text
.
├── build.mill
└── examples
    ├── src
    │   └── examples
    │       ├── ModernScala3App.scala
    │       └── modern
    │           ├── Domain.scala
    │           ├── Extensions.scala
    │           └── TypeClasses.scala
    └── test
        └── src
            └── examples
                └── modern
                    └── ModernScala3Suite.scala
```

source code 使用 Scala 3.3.8 示範第十部分的部分核心主題：
- 使用 `given` 與 `using` 的 contextual abstractions。
- extension methods。
- enums。
- opaque types。

---

## 3. 常用指令

編譯範例：

```bash
mill examples.compile
```

執行 sample application：

```bash
mill examples.runMain examples.ModernScala3App
```

執行測試：

```bash
mill examples.test
```

執行單一 test class：

```bash
mill examples.test.testOnly examples.modern.ModernScala3Suite
```

清除 build output：

```bash
mill clean
```

---

## 4. Example Module

`build.mill` 中的 `examples` module 將 `scalaVersion` 固定為 `3.3.8`，並定義：
- Scala version。
- source directories。
- test framework。
- test dependencies。

這個 module 刻意保持小型。它的目標是讓學習者能跑具體範例，而不是把本 repository 變成 production application。

---

## 5. 新增範例的方式

新增主題時可使用以下 checklist：

1. 將 source code 放在 `examples/src/examples/<topic>/`。
2. 只有在 console output 有助於理解時，才新增 runnable entry point。
3. 將 focused tests 放在 `examples/test/src/examples/<topic>/`。
4. 執行 `mill examples.compile`。
5. 執行 `mill examples.test`。
6. 從相關教學章節連結到範例。

適合新增的範例主題：
- collection transformations。
- 使用 `Either` 的 error handling。
- type class derivation。
- parsing and validation。
- simple HTTP client wrappers。

---

## 6. 疑難排解

如果尚未安裝 Mill，先安裝 Mill 再執行範例。

如果 dependency downloads 失敗，請確認網路連線後重試同一個指令。Mill 第一次執行時會下載 Scala、compiler artifacts 與 test libraries。

如果程式在 editor 中可編譯，但 Mill 無法編譯，請以 Mill 結果為準。本 repository 的 build file 是 source of truth。

---

## 下一步

範例能編譯後，下一步是用測試保護行為：
- [第十二部分：測試](scala_part12_testing.md)

---

> [« 上一篇：Modern Scala 3](scala_part10_modern_scala3.md) | [📚 目錄](../README.md) | [下一篇：測試 »](scala_part12_testing.md)
