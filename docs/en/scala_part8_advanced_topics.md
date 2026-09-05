# Scala Tutorial - Part 8: Contextual Abstractions and Type Classes

> [« Prev: Error Handling](scala_part7_error_handling.md) | [📚 Table of Contents](../../README.md) | [Next: Macros »](scala_part9_macros.md)

---

## Table of Contents

1. [Context Parameters](#1-context-parameters)
2. [Given Instances](#2-given-instances)
3. [Contextual Conversions](#3-contextual-conversions)
4. [Extension Methods](#4-extension-methods)
5. [Type Classes](#5-type-classes)
6. [Context Bounds](#6-context-bounds)
7. [Instance Search and Scope](#7-instance-search-and-scope)
8. [Composing Type Classes](#8-composing-type-classes)
9. [Best Practices](#9-best-practices)
10. [Exercises](#10-exercises)

---

## 1. Context Parameters

Scala 3 uses `using` to declare parameters supplied by the caller's context. This is useful for
configuration, ordering rules, execution environments, and type class instances.

```scala
def greet(name: String)(using greeting: String): String =
  s"$greeting, $name!"

given defaultGreeting: String = "Hello"

greet("Alice")             // "Hello, Alice!"
greet("Bob")(using "Hi") // "Hi, Bob!"
```

One `using` clause can contain multiple context parameters:

```scala
def format(value: Double)(using precision: Int, symbol: String): String =
  s"$symbol${value.formatted(s"%.${precision}f")}"

given defaultPrecision: Int = 2
given defaultSymbol: String = "$"

format(123.456) // "$123.46"
```

Context parameters can also pass capabilities from the standard library:

```scala
def sortValues[A](values: List[A])(using ordering: Ordering[A]): List[A] =
  values.sorted

case class Person(name: String, age: Int)

given Ordering[Person] = Ordering.by(_.age)

sortValues(List(Person("Alice", 25), Person("Bob", 20)))
```

---

## 2. Given Instances

`given` creates a contextual value that the compiler can locate by type. An instance can be named
or identified only by its type.

```scala
case class DatabaseConfig(host: String, port: Int)

given productionConfig: DatabaseConfig =
  DatabaseConfig("db.example.com", 5432)

def connectionLabel(using config: DatabaseConfig): String =
  s"${config.host}:${config.port}"
```

Use `with` when implementing an interface:

```scala
trait Encoder[A]:
  def encode(value: A): String

given Encoder[Int] with
  def encode(value: Int): String = value.toString
```

Parameterized instances can be composed from other instances:

```scala
given [A](using encoder: Encoder[A]): Encoder[List[A]] with
  def encode(values: List[A]): String =
    values.map(encoder.encode).mkString("[", ",", "]")
```

---

## 3. Contextual Conversions

`Conversion[A, B]` lets the compiler convert an `A` when a `B` is required. Automatic conversion
can hide costs or errors, so keep its scope narrow and prefer named methods for ordinary data
conversion.

```scala
import scala.Conversion
import scala.language.implicitConversions

final case class UserId(value: String)

given Conversion[String, UserId] = UserId(_)

def loadUser(id: UserId): String = id.value

loadUser("user-123")
```

When conversion can fail, represent the result explicitly with `Option` or `Either`:

```scala
final case class Port private (value: Int)

object Port:
  def from(value: Int): Either[String, Port] =
    Either.cond(value >= 1 && value <= 65535, Port(value), "invalid port")
```

---

## 4. Extension Methods

`extension` adds methods to an existing type without changing that type.

```scala
extension (value: Int)
  def squared: Int = value * value
  def isEven: Boolean = value % 2 == 0

3.squared // 9
4.isEven  // true
```

Generic extension methods preserve the element type:

```scala
extension [A](values: List[A])
  def secondOption: Option[A] = values.drop(1).headOption
  def toEither(error: => String): Either[String, List[A]] =
    Either.cond(values.nonEmpty, values, error)

List(1, 2, 3).secondOption       // Some(2)
List.empty[Int].toEither("empty") // Left("empty")
```

---

## 5. Type Classes

A type class describes a capability with a generic interface and supplies instances for individual
types. An algorithm depends on the capability without changing the data type or requiring an
inheritance relationship.

```scala
trait Show[A]:
  def show(value: A): String

object Show:
  def apply[A](using instance: Show[A]): Show[A] = instance

  def instance[A](render: A => String): Show[A] =
    new Show[A]:
      def show(value: A): String = render(value)

  given Show[Int] = instance(_.toString)
  given Show[String] = instance(value => s"\"$value\"")

def render[A](value: A)(using show: Show[A]): String =
  show.show(value)

render(42)      // "42"
render("Scala") // "\"Scala\""
```

Put an instance in the companion object of the type class or data type when it should be found
without an additional import.

```scala
case class Person(name: String, age: Int)

object Person:
  given Show[Person] =
    Show.instance(person => s"${person.name} (${person.age})")
```

Syntax operations can be exposed as extension methods:

```scala
extension [A](value: A)
  def show(using instance: Show[A]): String = instance.show(value)

Person("Alice", 25).show
```

---

## 6. Context Bounds

A context bound is shorthand for requiring a type class instance. `[A: Show]` means that a
`Show[A]` must be available in the current scope.

```scala
def renderAll[A: Show](values: List[A]): List[String] =
  values.map(value => summon[Show[A]].show(value))
```

`summon[A]` retrieves an `A` instance from the current scope. Chain bounds when a method needs more
than one capability:

```scala
def sortedLabels[A: Show: Ordering](values: List[A]): List[String] =
  val show = summon[Show[A]]
  values.sorted.map(show.show)
```

A named `using` parameter is usually clearer when the method uses the instance repeatedly.

---

## 7. Instance Search and Scope

The compiler searches the current scope, explicit imports, and companion objects of related types
for `given` instances. Import only the given instances supplied by an object when needed:

```scala
object AgeOrdering:
  given Ordering[Person] = Ordering.by(_.age)

import AgeOrdering.given

List(Person("Alice", 25), Person("Bob", 20)).sorted
```

A local instance can temporarily change behavior:

```scala
def descending(values: List[Int]): List[Int] =
  given Ordering[Int] = Ordering.Int.reverse
  values.sorted
```

Multiple candidates of the same type in one scope can make instance selection ambiguous. Narrow
the import scope or pass an instance explicitly:

```scala
val byName: Ordering[Person] = Ordering.by(_.name)

sortValues(List(Person("Bob", 20), Person("Alice", 25)))(using byName)
```

---

## 8. Composing Type Classes

Type class instances can be composed recursively. A list has `Show` whenever its elements have
`Show`:

```scala
object ShowInstances:
  given [A](using itemShow: Show[A]): Show[List[A]] =
    Show.instance { values =>
      values.map(itemShow.show).mkString("[", ", ", "]")
    }

import ShowInstances.given

render(List(1, 2, 3)) // "[1, 2, 3]"
```

Algebraic structures use the same model:

```scala
trait Monoid[A]:
  def empty: A
  def combine(left: A, right: A): A

object Monoid:
  def apply[A](using instance: Monoid[A]): Monoid[A] = instance

  given Monoid[Int] with
    def empty: Int = 0
    def combine(left: Int, right: Int): Int = left + right

  given Monoid[String] with
    def empty: String = ""
    def combine(left: String, right: String): String = left + right

def combineAll[A: Monoid](values: List[A]): A =
  val monoid = summon[Monoid[A]]
  values.foldLeft(monoid.empty)(monoid.combine)
```

If one type needs several combination rules, place each instance in a named object and let callers
import one explicitly. This avoids conflicts between instances of the same type.

---

## 9. Best Practices

- Use `using` for contextual capabilities passed through a call chain.
- Put default `given` instances in the companion object of the type class or data type.
- Use `extension` for syntax operations and keep the core type class small.
- Reserve automatic `Conversion` for safe, lossless, unsurprising conversions.
- Use `Option` or `Either` for conversions that can fail.
- Avoid several `given` instances of the same type in a broad scope.
- Prefer small, composable type classes.
- Test the observable results of generic functions instead of instance-search internals.

---

## 10. Exercises

1. Define an `Eq[A]` type class and provide `given` instances for `Int`, `String`, and `List[A]`.
2. Add `===` and `=/=` extension methods for `Eq[A]`.
3. Define `JsonEncoder[A]` and compose a `JsonEncoder[Option[A]]`.
4. Implement a generic maximum function with `using Ordering[A]` and return `Option[A]` for an
   empty list.
5. Put two named `Ordering[Person]` instances in separate objects for name and age sorting.

---

## Summary

This chapter uses Scala 3.3.8 `given`, `using`, `summon`, context bounds, `extension`, and
`Conversion` to build contextual abstractions. These tools distinguish instance definitions,
context requirements, syntax extensions, and type conversions while keeping type class programs
composable and testable.

---

> [« Prev: Error Handling](scala_part7_error_handling.md) | [📚 Table of Contents](../../README.md) | [Next: Macros »](scala_part9_macros.md)
