# Scala Tutorial - Part 12: Testing

> [📚 Table of Contents](../../README.md) | [« Prev: Mill & Runnable Examples](scala_part11_mill_examples.md)

---

## Table of Contents
1. [Testing Goals](#1-testing-goals)
2. [MUnit Basics](#2-munit-basics)
3. [Testing Option and Either](#3-testing-option-and-either)
4. [Testing Type Classes](#4-testing-type-classes)
5. [Testing Extension Methods](#5-testing-extension-methods)
6. [Test Design Guidelines](#6-test-design-guidelines)
7. [Practice Exercises](#7-practice-exercises)

---

## 1. Testing Goals

Good Scala tests should verify behavior, not implementation details. Start with small pure functions because they are easy to test and require little setup.

For this repository, tests should:
- make tutorial examples executable;
- protect examples from accidental regressions;
- demonstrate idiomatic assertions;
- stay focused enough for beginners to read.

---

## 2. MUnit Basics

MUnit is a lightweight Scala testing framework with simple syntax.

```scala
class CalculatorSuite extends munit.FunSuite:
  test("adds two numbers"):
    assertEquals(1 + 2, 3)
```

Run all tests:

```bash
mill examples.test
```

Run one suite:

```bash
mill examples.test.testOnly examples.modern.ModernScala3Suite
```

---

## 3. Testing Option and Either

Prefer direct assertions for small values:

```scala
test("parses a valid number"):
  assertEquals(parseInt("42"), Right(42))

test("rejects an invalid number"):
  assert(parseInt("nope").isLeft)
```

For domain validation, test both success and failure paths:

```scala
test("validates email"):
  assertEquals(Email.from("alice@example.com").map(_.value), Some("alice@example.com"))
  assertEquals(Email.from("invalid"), None)
```

---

## 4. Testing Type Classes

Type class tests should verify the public behavior exposed through generic functions.

```scala
test("renders values with Show"):
  assertEquals(Render.render(42), "42")
  assertEquals(Render.render(User("u-1", "Alice")), "u-1:Alice")
```

This catches both missing instances and incorrect instance behavior.

---

## 5. Testing Extension Methods

Extension methods are ordinary methods from the caller's perspective. Test them through normal usage:

```scala
test("returns the second list item"):
  assertEquals(List("a", "b", "c").secondOption, Some("b"))
  assertEquals(List("a").secondOption, None)
```

Avoid tests that depend on how the extension method is implemented.

---

## 6. Test Design Guidelines

- Use one behavioral idea per test.
- Name tests with the expected behavior.
- Prefer pure functions and immutable test data.
- Test edge cases explicitly.
- Keep fixtures local unless several tests share the same setup.
- Avoid testing compiler features directly; test the behavior your code exposes.

---

## 7. Practice Exercises

1. Add tests for the `CheckoutState` enum from Part 10.
2. Add tests for a custom `Email` opaque type.
3. Add tests for one collection transformation from Part 5.
4. Add tests for one `Either` workflow from Part 7.
5. Add a failing test first, then implement the smallest code change to make it pass.

---

## Next Steps

After this chapter, the repository has three complementary learning modes:
- read the tutorials;
- run the examples;
- verify behavior with tests.

---

> [📚 Table of Contents](../../README.md) | [« Prev: Mill & Runnable Examples](scala_part11_mill_examples.md)
