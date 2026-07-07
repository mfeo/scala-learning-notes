# Scala 教學 - 第十二部分: 測試

> [« 上一篇：Mill 與可執行範例](scala_part11_mill_examples.md) | [📚 目錄](../README.md)

---

## 目錄
1. [測試目標](#1-測試目標)
2. [MUnit 基礎](#2-munit-基礎)
3. [測試 Option 與 Either](#3-測試-option-與-either)
4. [測試 Type Classes](#4-測試-type-classes)
5. [測試 Extension Methods](#5-測試-extension-methods)
6. [測試設計建議](#6-測試設計建議)
7. [實作練習](#7-實作練習)

---

## 1. 測試目標

好的 Scala tests 應該驗證行為，而不是鎖死實作細節。純函數是最適合開始測試的對象，因為它們容易驗證，也不需要複雜 setup。

在本 repository 中，測試應該：
- 讓教學範例可以執行。
- 避免範例被意外改壞。
- 示範 idiomatic assertions。
- 保持足夠聚焦，讓初學者可以閱讀。

---

## 2. MUnit 基礎

MUnit 是輕量的 Scala testing framework，語法簡單。

```scala
class CalculatorSuite extends munit.FunSuite:
  test("adds two numbers"):
    assertEquals(1 + 2, 3)
```

執行所有測試：

```bash
mill examples.test
```

執行單一 suite：

```bash
mill examples.test.testOnly examples.modern.ModernScala3Suite
```

---

## 3. 測試 Option 與 Either

小型值可以直接 assert：

```scala
test("parses a valid number"):
  assertEquals(parseInt("42"), Right(42))

test("rejects an invalid number"):
  assert(parseInt("nope").isLeft)
```

領域驗證應同時測成功與失敗路徑：

```scala
test("validates email"):
  assertEquals(Email.from("alice@example.com").map(_.value), Some("alice@example.com"))
  assertEquals(Email.from("invalid"), None)
```

---

## 4. 測試 Type Classes

Type class tests 應驗證 generic functions 對外呈現的行為。

```scala
test("renders values with Show"):
  assertEquals(Render.render(42), "42")
  assertEquals(Render.render(User("u-1", "Alice")), "u-1:Alice")
```

這可以同時抓到 missing instances 與 instance behavior 錯誤。

---

## 5. 測試 Extension Methods

從使用者角度看，extension methods 就是一般方法。用一般呼叫方式測試即可：

```scala
test("returns the second list item"):
  assertEquals(List("a", "b", "c").secondOption, Some("b"))
  assertEquals(List("a").secondOption, None)
```

避免測試 extension method 的內部實作方式。

---

## 6. 測試設計建議

- 每個 test 聚焦一個 behavior。
- test name 描述預期行為。
- 優先測 pure functions 與 immutable test data。
- 明確測 edge cases。
- 除非多個 tests 共用同樣 setup，否則 fixtures 保持 local。
- 不要直接測 compiler features；測你的程式對外暴露的行為。

---

## 7. 實作練習

1. 替第十部分的 `CheckoutState` enum 新增 tests。
2. 替自訂 `Email` opaque type 新增 tests。
3. 替第五部分的一個 collection transformation 新增 tests。
4. 替第七部分的一個 `Either` workflow 新增 tests。
5. 先寫一個 failing test，再用最小實作讓它通過。

---

## 下一步

完成本章後，本 repository 會有三種互補的學習模式：
- 閱讀教學。
- 執行範例。
- 用測試驗證行為。

---

> [« 上一篇：Mill 與可執行範例](scala_part11_mill_examples.md) | [📚 目錄](../README.md)
