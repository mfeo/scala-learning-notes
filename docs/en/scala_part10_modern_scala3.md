# Scala Tutorial - Part 10: Modern Scala 3.3.8 LTS

> [Runnable example and tests](../../examples/src/examples/modern) | [📚 Table of Contents](../../README.md) | [« Prev: Macros](scala_part9_macros.md) | [Next: Mill & Runnable Examples »](scala_part11_mill_examples.md)

---

## Table of Contents
1. [Why Modern Scala 3 Matters](#1-why-modern-scala-3-matters)
2. [Contextual Abstractions with given and using](#2-contextual-abstractions-with-given-and-using)
3. [Extension Methods](#3-extension-methods)
4. [Enums](#4-enums)
5. [Opaque Types](#5-opaque-types)
6. [Exports](#6-exports)
7. [Derives](#7-derives)
8. [Scala 3.3.8 Conventions](#8-scala-338-conventions)
9. [Practice Exercises](#9-practice-exercises)

---

## 1. Why Modern Scala 3 Matters

This guide targets Scala 3.3.8 LTS. LTS means Long-Term Support: the 3.3 line
receives compatibility-focused maintenance for longer than ordinary releases.
Scala 3 keeps the core Scala model—static typing, object-oriented programming,
and functional programming—but adds clearer syntax and a more expressive type
system.

Key upgrades include contextual abstractions with `given` and `using`, extension
methods, enums, opaque types, exports, type class derivation, new types, optional
braces, top-level definitions, and principled compile-time metaprogramming.

### 1.1 Syntax and Definitions

Scala 3.3.8 supports indentation-based optional braces, new control syntax,
top-level definitions, `end` markers for long definitions, and `@main` entry
points:

```scala
def classify(value: Int): String =
  if value < 0 then "negative"
  else if value == 0 then "zero"
  else "positive"

@main def hello(name: String): Unit =
  println(s"Hello, $name")
```

Classes can normally be constructed without `new`, traits can take parameters,
and a concrete class intended for extension from another source file must be
declared `open`:

```scala
class User(val name: String)
val user = User("Ada")

trait Named(prefix: String):
  def name: String
  def displayName: String = s"$prefix$name"

open class PublicBase
```

Scala 3 import, wildcard-type, and vararg-splice syntax is also more explicit:

```scala
import java.time.{LocalDate as Date}
import scala.collection.mutable.*

val unknownNumbers: List[? <: Number] = List(Integer.valueOf(1))
val values = List(1, 2, 3)
val copied = List(values*)
```

Use `infix` for alphanumeric methods intended for infix calls, and give symbolic
methods a stable Java-facing name with `@targetName`:

```scala
import scala.annotation.targetName

case class Count(value: Int)

extension (left: Count)
  infix def plus(right: Count): Count = Count(left.value + right.value)

  @targetName("timesCount")
  def *(factor: Int): Count = Count(left.value * factor)
```

### 1.2 New Types

Scala 3.3.8 includes intersection types, union types, type lambdas, match types,
dependent function types, and polymorphic function types:

```scala
trait Resettable:
  def reset(): Unit

trait Closeable:
  def close(): Unit

def resetAndClose(value: Resettable & Closeable): Unit =
  value.reset()
  value.close()

type StringOrInt = String | Int
type MapValues[K] = [V] =>> Map[K, V]

type Element[X] = X match
  case String => Char
  case Array[t] => t
  case Iterable[t] => t

trait Entry:
  type Key
  def key: Key

val keyOf: (entry: Entry) => entry.Key =
  (entry: Entry) => entry.key

val identity: [A] => A => A =
  [A] => (value: A) => value
```

The standard tuple operations, match types, and kind-polymorphic `Tuple` and
`Function` abstractions allow generic programming without the Scala 2 limit of
22 elements.

---

## 2. Contextual Abstractions with given and using

Scala 3.3.8 uses `given` to define contextual instances, `using` to declare
context requirements, and `summon` to retrieve an instance from the current scope.

```scala
trait Show[A]:
  def show(value: A): String

given Show[Int] with
  def show(value: Int): String = value.toString

given Show[String] with
  def show(value: String): String = value

def render[A](value: A)(using show: Show[A]): String =
  show.show(value)

def renderAll[A: Show](values: List[A]): List[String] =
  values.map(value => summon[Show[A]].show(value))
```

Given imports are separate from ordinary wildcard imports:

```scala
object Formats:
  given Show[Double] with
    def show(value: Double): String = f"$value%.2f"

import Formats.given
```

Context functions make an available context part of a function type. A by-name
context parameter delays evaluation and is useful for recursive contextual
definitions:

```scala
trait Logger:
  def log(message: String): Unit

type Logged[A] = Logger ?=> A

def announce(message: String): Logged[Unit] =
  summon[Logger].log(message)

def delayed(using logger: => Logger): Logger = logger
```

Use the standard `Conversion` type class for intentional contextual conversions:

```scala
import scala.Conversion
import scala.language.implicitConversions

given Conversion[Int, String] = _.toString
```

With `-language:strictEquality`, equality requires `CanEqual` evidence. Case
classes and enums can derive it when strict equality is part of the design:

```scala
case class UserId(value: String) derives CanEqual
```

Keep givens near the companion object of the provided type or type class, import
givens explicitly, and avoid broad conversions.

---

## 3. Extension Methods

Extension methods add operations to existing types without changing those types:

```scala
extension (text: String)
  def words: List[String] =
    text.trim.split("\\s+").toList.filter(_.nonEmpty)

  def titleCase: String =
    words.map(word => s"${word.head.toUpper}${word.tail.toLowerCase}").mkString(" ")

extension [A](values: List[A])
  def secondOption: Option[A] = values.drop(1).headOption
```

Scala 3.3.8 also supports collective extensions with multiple type parameters,
context parameters, operator extensions, and right-associative extensions whose
names end in `:`. Keep imported extensions in clearly named objects and prefer a
normal method when extension syntax does not improve the caller's code.

---

## 4. Enums

Enums model both simple enumerations and algebraic data types:

```scala
enum OrderStatus derives CanEqual:
  case Draft, Submitted, Paid, Cancelled

enum PaymentResult[+A]:
  case Approved(value: A)
  case Declined(reason: String)

def message(result: PaymentResult[String]): String =
  result match
    case PaymentResult.Approved(id) => s"Approved: $id"
    case PaymentResult.Declined(reason) => s"Declined: $reason"
```

Enums may have parameters, members, generic cases, and Java-compatible cases.
Use them when all alternatives are known at compile time and exhaustive pattern
matching is valuable.

---

## 5. Opaque Types

Opaque type aliases provide an abstraction boundary without allocating a wrapper:

```scala
object Domain:
  opaque type UserId = String

  object UserId:
    def from(value: String): Option[UserId] =
      Option.when(value.trim.nonEmpty)(value.trim)

  extension (id: UserId)
    def value: String = id

import Domain.*
```

Outside the defining scope, `UserId` is distinct from `String`; inside it, the
representation is visible. Opaque aliases can be generic, bounded, top-level, or
members of a class. A member opaque type is path-dependent, so two instances can
define distinct abstract types with the same representation.

---

## 6. Exports

`export` creates forwarding members and supports selection, wildcard export,
renaming, and given export:

```scala
class UserService(repository: UserRepository):
  export repository.{findById, save}

class PublicService(repository: UserRepository):
  export repository.findById as findUser
```

Exports are useful for composition and façade APIs. Prefer explicit methods when
forwarding must add validation, authorization, or other behavior.

---

## 7. Derives

`derives` asks a type class companion object for a `derived` implementation. The
compiler supplies a `Mirror` describing the fields or alternatives of the type:

```scala
import scala.deriving.Mirror

trait JsonEncoder[A]:
  def encode(value: A): String

object JsonEncoder:
  def derived[A](using Mirror.Of[A]): JsonEncoder[A] =
    new JsonEncoder[A]:
      def encode(value: A): String = value.toString

case class User(id: String, name: String) derives JsonEncoder
```

The example is deliberately small; a production encoder would inspect
`MirroredElemTypes` and recursively summon encoders. Derivation only works when
the type class provides the required `derived` method or the compiler has
special support, as it does for `CanEqual`.

---

## 8. Scala 3.3.8 Conventions

### 8.1 Syntax Checklist

- Use `given` for contextual instances and `using` for context parameters.
- Use `summon[A]` to retrieve an `A` instance from the current scope.
- Use `extension` to add operations to existing types.
- Write wildcard types as `List[?]` and vararg splices as `values*`.
- Use `import a.*` for wildcard imports and `import a.{x as y}` for renaming.
- Pass a method directly as a function value without an extra eta-expansion marker.
- Use `@main` for a small program entry point.
- Give method bodies an `=` and give public methods an explicit result type.
- Use a `while` with a block condition when the body must run before the check.
- Put shared definitions at the top level; XML literals require the separate Scala XML library.

### 8.2 Additional Scala 3.3.8 Features

The following features are more specialized but are part of the 3.3 language
surface and should be recognized when reading code:

- trait parameters, transparent traits and classes, `open` classes, universal apply methods,
  top-level definitions, parameter untupling, and kind polymorphism;
- programmatic structural types through `Selectable`, the `Matchable` marker,
  and safe erased type tests through `TypeTest`;
- improved overload resolution, implicit resolution, type inference, pattern
  bindings, match exhaustivity, lazy-value initialization, and interpolator
  escape checking;
- `@threadUnsafe` for opting a lazy value out of thread-safe initialization and
  binary integer literals such as `0b1010`;
- explicit nulls with `-Yexplicit-nulls` and safe initialization warnings with
  `-Wsafe-init`; both are opt-in checks in Scala 3.3.8;
- `inline`, `transparent inline`, compile-time operations, quotes, splices, and
  reflection for metaprogramming, covered in Part 9; TASTy inspection lets tools
  inspect the compiler's typed abstract syntax trees from compiled code.

Scala 3.3.8 itself also adds JDK 26 support, `@uncheckedOverride`, the
`-Yfuture-lazy-vals` compatibility option, local coverage on/off markers, and
REPL interrupt handling improvements. JDK means Java Development Kit, and REPL
means Read-Eval-Print Loop, the interactive Scala prompt. These are release
capabilities rather than new core Scala 3 syntax.

---

## 9. Practice Exercises

1. Define a `Show[A]` type class with given instances and explicit given imports.
2. Add extension methods for `List[A]`, including one that requires an `Ordering[A]` context.
3. Model a generic `CheckoutState[+A]` enum and write an exhaustive matcher.
4. Create an opaque `Email` type that validates the presence of `@`.
5. Define and use a union type, an intersection type, and a match type.
6. Derive a small type class through `Mirror`, then test product and sum types.
7. Build composable formatting utilities with `given`, `using`, and `extension`.

---

## Next Steps

Practice the syntax in the repository's Mill project and keep the compiler
version at `3.3.8` while following this guide:

- [Part 11: Mill & Runnable Examples](scala_part11_mill_examples.md)
- [Part 12: Testing](scala_part12_testing.md)
- [Official Scala 3 Reference](https://docs.scala-lang.org/scala3/reference/)

---

> [📚 Table of Contents](../../README.md) | [« Prev: Macros](scala_part9_macros.md) | [Next: Mill & Runnable Examples »](scala_part11_mill_examples.md)
