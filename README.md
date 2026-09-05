# Scala 學習筆記

A comprehensive Scala 3.3.8 LTS learning guide covering fundamentals through advanced topics.
Available in Traditional Chinese (繁體中文) and English.

## 目錄 / Table of Contents

| # | 主題 | 繁體中文 | English |
|---|------|----------|---------|
| 1 | Introduction and Setup（簡介與環境設置） | [中文](docs/scala_part1_introduction.md) | [EN](docs/en/scala_part1_introduction.md) |
| 2 | Basic Syntax（基本語法） | [中文](docs/scala_part2_basic_syntax.md) | [EN](docs/en/scala_part2_basic_syntax.md) |
| 3 | Functions and Methods（函數與方法） | [中文](docs/scala_part3_functions.md) | [EN](docs/en/scala_part3_functions.md) |
| 4 | Object-Oriented Programming（物件導向程式設計） | [中文](docs/scala_part4_oop.md) | [EN](docs/en/scala_part4_oop.md) |
| 5 | Collection Operations（集合操作） | [中文](docs/scala_part5_collections.md) | [EN](docs/en/scala_part5_collections.md) |
| 6 | Pattern Matching（模式比對） | [中文](docs/scala_part6_pattern_matching.md) | [EN](docs/en/scala_part6_pattern_matching.md) |
| 7 | Error Handling（錯誤處理） | [中文](docs/scala_part7_error_handling.md) | [EN](docs/en/scala_part7_error_handling.md) |
| 8 | Contextual Abstractions and Type Classes（上下文抽象與型別類別） | [中文](docs/scala_part8_advanced_topics.md) | [EN](docs/en/scala_part8_advanced_topics.md) |
| 9 | Scala 3 Macros（Scala 3 巨集） | [中文](docs/scala_part9_macros.md) | [EN](docs/en/scala_part9_macros.md) |
| 10 | Modern Scala 3 | [中文](docs/scala_part10_modern_scala3.md) | [EN](docs/en/scala_part10_modern_scala3.md) |
| 11 | Mill and Runnable Examples（Mill 與可執行範例） | [中文](docs/scala_part11_mill_examples.md) | [EN](docs/en/scala_part11_mill_examples.md) |
| 12 | Testing（測試） | [中文](docs/scala_part12_testing.md) | [EN](docs/en/scala_part12_testing.md) |

## 學習路徑 / Learning Path

- **核心基礎 (Parts 1–7)**：從環境設置到錯誤處理，建立完整的 Scala 基礎
- **進階主題 (Parts 8–10)**：深入上下文抽象、型別類別、Scala 3 巨集與 Modern Scala 3
- **實務路線 (Parts 11–12)**：使用 Mill 執行範例、編譯專案並撰寫測試

## Runnable Examples

This repository includes a small Mill-based Scala 3.3.8 project under `examples/`.

```bash
mill examples.compile
mill examples.runMain examples.ModernScala3App
mill examples.test
```
