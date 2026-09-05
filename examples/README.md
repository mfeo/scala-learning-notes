# Runnable Examples

This module turns each learning-guide chapter into compiled Scala 3 code. Core behavior is kept
separate from console entry points so that tests can exercise observable results directly.

| Part | Topic | Source | Test |
|---|---|---|---|
| 1 | Introduction and Setup | [`introduction`](src/examples/introduction) | [`IntroductionSuite`](test/src/examples/introduction/IntroductionSuite.scala) |
| 2 | Basic Syntax | [`basics`](src/examples/basics) | [`BasicSyntaxSuite`](test/src/examples/basics/BasicSyntaxSuite.scala) |
| 3 | Functions and Methods | [`functions`](src/examples/functions) | [`FunctionsSuite`](test/src/examples/functions/FunctionsSuite.scala) |
| 4 | Object-Oriented Programming | [`oop`](src/examples/oop) | [`ObjectOrientedSuite`](test/src/examples/oop/ObjectOrientedSuite.scala) |
| 5 | Collection Operations | [`collections`](src/examples/collections) | [`CollectionsSuite`](test/src/examples/collections/CollectionsSuite.scala) |
| 6 | Pattern Matching | [`patterns`](src/examples/patterns) | [`PatternMatchingSuite`](test/src/examples/patterns/PatternMatchingSuite.scala) |
| 7 | Error Handling | [`errors`](src/examples/errors) | [`ErrorHandlingSuite`](test/src/examples/errors/ErrorHandlingSuite.scala) |
| 8 | Contextual Abstractions and Type Classes | [`contextual`](src/examples/contextual) | [`ContextualSuite`](test/src/examples/contextual/ContextualSuite.scala) |
| 9 | Scala 3 Macros | [`macros`](src/examples/macros) | [`MacrosSuite`](test/src/examples/macros/MacrosSuite.scala) |
| 10 | Modern Scala 3 | [`modern`](src/examples/modern) | [`ModernScala3Suite`](test/src/examples/modern/ModernScala3Suite.scala) |
| 11 | Mill and Runnable Examples | [`ExamplesCatalog`](src/examples/ExamplesCatalog.scala) | [`ExamplesCatalogSuite`](test/src/examples/ExamplesCatalogSuite.scala) |
| 12 | Testing | [`testing`](src/examples/testing) | [`TestingPatternsSuite`](test/src/examples/testing/TestingPatternsSuite.scala) |

Compile and test every example:

```bash
mill --no-server examples.compile
mill --no-server examples.test
```

List the available examples, run one topic, or run all topics:

```bash
mill --no-server examples.runMain examples.AllExamplesApp
mill --no-server examples.runMain examples.AllExamplesApp collections
mill --no-server examples.runMain examples.AllExamplesApp all
```

Individual chapter applications can also be run directly. For example:

```bash
mill --no-server examples.runMain examples.functions.FunctionsApp
mill --no-server examples.runMain examples.macros.MacrosApp
```
