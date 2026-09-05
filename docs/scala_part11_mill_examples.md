# Scala 教學 - 第十一部分：Mill and Runnable Examples（Mill 與可執行範例）

> [« 上一篇：Modern Scala 3](scala_part10_modern_scala3.md) | [📚 目錄](../README.md) | [下一篇：測試 »](scala_part12_testing.md)

---

## 目錄
1. [為什麼使用 Mill](#1-為什麼使用-mill)
2. [Project Structure](#2-project-structure專案結構)
3. [常用指令](#3-常用指令)
4. [Example Module](#4-example-module範例模組)
5. [新增範例的方式](#5-新增範例的方式)
6. [疑難排解](#6-疑難排解)

---

## 1. 為什麼使用 Mill

Mill 是 Scala Build Tool（建置工具），重點是快速 Incremental Build（增量建置）、
明確的 Module（模組），以及容易自動化的 CLI（Command-Line Interface，命令列介面）
Workflow（工作流程）。

本專案使用 Mill 管理 Runnable Examples（可執行範例），並支援 Compilation（編譯）、
Testing（測試）、Dependency（依賴套件）與 Multiple Modules（多模組）。

---

## 2. Project Structure（專案結構）

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

Source Code（原始碼）使用 Scala 3.3.8 示範第十部分的部分核心主題：
- 使用 `given` 與 `using` 的 Contextual Abstractions（上下文抽象）。
- Extension Methods（擴充方法）。
- Enums（列舉）。
- Opaque Types（不透明型別）。

---

## 3. 常用指令

編譯範例：

```bash
mill examples.compile
```

執行 Sample Application（範例應用程式）：

```bash
mill examples.runMain examples.ModernScala3App
```

執行測試：

```bash
mill examples.test
```

執行單一 Test Class（測試類別）：

```bash
mill examples.test.testOnly examples.modern.ModernScala3Suite
```

清除 Build Output（建置輸出）：

```bash
mill clean
```

---

## 4. Example Module（範例模組）

`build.mill` 中的 `examples` Module 將 `scalaVersion` 固定為 `3.3.8`，並定義：
- Scala Version（Scala 版本）。
- Source Directories（原始碼目錄）。
- Test Framework（測試框架）。
- Test Dependencies（測試依賴套件）。

這個 module 刻意保持小型。它的目標是讓學習者能跑具體範例，而不是把本 repository 變成 production application。

---

## 5. 新增範例的方式

新增主題時可使用以下 checklist：

1. 將 Source Code 放在 `examples/src/examples/<topic>/`。
2. 只有在 Console Output（終端輸出）有助於理解時，才新增 Runnable Entry Point
   （可執行程式進入點）。
3. 將 Focused Tests（聚焦測試）放在 `examples/test/src/examples/<topic>/`。
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
