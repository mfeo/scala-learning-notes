# Scala Tutorial - Part 11: Mill & Runnable Examples

> [📚 Table of Contents](../../README.md) | [« Prev: Modern Scala 3](scala_part10_modern_scala3.md) | [Next: Testing »](scala_part12_testing.md)

---

## Table of Contents
1. [Why Use Mill](#1-why-use-mill)
2. [Project Layout](#2-project-layout)
3. [Common Commands](#3-common-commands)
4. [The Example Module](#4-the-example-module)
5. [Adding New Examples](#5-adding-new-examples)
6. [Troubleshooting](#6-troubleshooting)

---

## 1. Why Use Mill

Mill is a Scala build tool designed around fast incremental builds, explicit modules, and command-line workflows that are easy to automate.

This repository uses Mill for runnable examples because it keeps the learning project small while still supporting compilation, tests, dependencies, and multiple modules.

---

## 2. Project Layout

The runnable Scala code lives under `examples/`:

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

The source code mirrors Part 10:
- contextual abstractions with `given` and `using`;
- extension methods;
- enums;
- opaque types.

---

## 3. Common Commands

Compile the examples:

```bash
mill examples.compile
```

Run the sample application:

```bash
mill examples.runMain examples.ModernScala3App
```

Run tests:

```bash
mill examples.test
```

Run one test class:

```bash
mill examples.test.testOnly examples.modern.ModernScala3Suite
```

Clean build output:

```bash
mill clean
```

---

## 4. The Example Module

The `examples` module in `build.mill` defines:
- the Scala version;
- source directories;
- the test framework;
- test dependencies.

The module is intentionally small. It is meant to help learners run concrete examples without turning this repository into a production application.

---

## 5. Adding New Examples

Use this checklist when adding a new topic:

1. Add source code under `examples/src/examples/<topic>/`.
2. Add a small runnable entry point only when the example benefits from console output.
3. Add focused tests under `examples/test/src/examples/<topic>/`.
4. Run `mill examples.compile`.
5. Run `mill examples.test`.
6. Link the example from the relevant tutorial chapter.

Example topics that fit well:
- collection transformations;
- error handling with `Either`;
- type class derivation;
- parsing and validation;
- simple HTTP client wrappers.

---

## 6. Troubleshooting

If Mill is not installed, install it before running the examples.

If dependency downloads fail, check network access and retry the same command. Mill downloads Scala, compiler artifacts, and test libraries on first use.

If code compiles in the editor but not in Mill, trust the Mill result. The build file is the source of truth for this repository.

---

## Next Steps

Once the examples compile, add tests around the behavior:
- [Part 12: Testing](scala_part12_testing.md)

---

> [📚 Table of Contents](../../README.md) | [« Prev: Modern Scala 3](scala_part10_modern_scala3.md) | [Next: Testing »](scala_part12_testing.md)
