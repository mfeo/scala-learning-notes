# Scala Tutorial - Part 10: Modern Scala 3

> [📚 Table of Contents](../../README.md) | [« Prev: Macros](scala_part9_macros.md) | [Next: Mill & Runnable Examples »](scala_part11_mill_examples.md)

---

## Table of Contents
1. [Why Modern Scala 3 Matters](#1-why-modern-scala-3-matters)
2. [Contextual Abstractions with given and using](#2-contextual-abstractions-with-given-and-using)
3. [Extension Methods](#3-extension-methods)
4. [Enums](#4-enums)
5. [Opaque Types](#5-opaque-types)
6. [Exports](#6-exports)
7. [Derives](#7-derives)
8. [Migration Notes](#8-migration-notes)
9. [Practice Exercises](#9-practice-exercises)

---

## 1. Why Modern Scala 3 Matters

Scala 3 keeps the core Scala model: concise syntax, strong static typing, object-oriented programming, and functional programming. The biggest practical change is that several Scala 2 idioms now have clearer names and safer syntax.

Use this chapter after learning Part 8. Part 8 explains the concepts behind implicits and type classes. This chapter shows the Scala 3 syntax you should prefer in new code.

Key upgrades:
- `given` defines contextual values.
- `using` requests contextual values.
- `extension` adds methods to existing types without wrapper classes.
- `enum` defines algebraic data types directly.
- `opaque type` creates zero-overhead domain types.
- `export` forwards members from an internal object.
- `derives` asks the compiler or a library to derive type class instances.

---

## 2. Contextual Abstractions with given and using

Scala 2 code often used `implicit val`, `implicit def`, and implicit parameter lists. Scala 3 keeps the same idea but separates intent more clearly.

```scala
trait Show[A]:
  def show(value: A): String

given Show[Int] with
  def show(value: Int): String = value.toString

given Show[String] with
  def show(value: String): String = value

def render[A](value: A)(using show: Show[A]): String =
  show.show(value)

val renderedNumber = render(42)
val renderedText = render("Scala")
```

Use `summon` when you need to access a contextual value directly:

```scala
def renderTwice[A](value: A)(using Show[A]): String =
  val show = summon[Show[A]]
  s"${show.show(value)}, ${show.show(value)}"
```

Context bounds still work:

```scala
def renderAll[A: Show](values: List[A]): List[String] =
  values.map(value => summon[Show[A]].show(value))
```

Guidelines:
- Prefer `given` and `using` in new Scala 3 code.
- Keep given instances close to the type or type class companion object.
- Avoid broad wildcard imports of givens unless the scope is intentionally small.
- Name givens when the name improves diagnostics or API clarity.

---

## 3. Extension Methods

Extension methods replace many Scala 2 implicit class use cases.

```scala
extension (text: String)
  def words: List[String] =
    text.trim.split("\\s+").toList.filter(_.nonEmpty)

  def titleCase: String =
    words.map(word => word.head.toUpper + word.tail.toLowerCase).mkString(" ")

val title = "modern scala 3".titleCase
```

Generic extension methods are useful for small, focused APIs:

```scala
extension [A](values: List[A])
  def secondOption: Option[A] =
    values.drop(1).headOption
```

Guidelines:
- Use extension methods for domain-specific readability.
- Avoid making every helper method an extension method.
- Keep extension methods in clearly named objects when they must be imported.

---

## 4. Enums

Scala 3 enums are a direct way to model a closed set of alternatives.

```scala
enum OrderStatus:
  case Draft, Submitted, Paid, Cancelled

def canPay(status: OrderStatus): Boolean =
  status match
    case OrderStatus.Submitted => true
    case _ => false
```

Enums can also carry data:

```scala
enum PaymentResult:
  case Approved(transactionId: String)
  case Declined(reason: String)
  case RequiresReview(score: Int)

def message(result: PaymentResult): String =
  result match
    case PaymentResult.Approved(id) => s"Approved: $id"
    case PaymentResult.Declined(reason) => s"Declined: $reason"
    case PaymentResult.RequiresReview(score) => s"Review score: $score"
```

Use enums for algebraic data types when:
- all cases are known at compile time;
- exhaustive pattern matching is useful;
- each case represents a meaningful business state.

---

## 5. Opaque Types

Opaque types provide stronger domain types without runtime allocation.

```scala
object Domain:
  opaque type UserId = String

  object UserId:
    def from(value: String): Option[UserId] =
      Option.when(value.nonEmpty)(value)

  extension (id: UserId)
    def value: String = id

import Domain.*

val id = UserId.from("u-123")
```

Outside the defining scope, `UserId` is not the same as `String`. Inside the defining scope, the compiler still represents it as a `String`.

Use opaque types for:
- identifiers;
- validated values;
- units of measure;
- small domain types where a case class wrapper would be noisy.

---

## 6. Exports

`export` forwards selected members from an inner object.

```scala
class UserService(repository: UserRepository):
  export repository.findById
  export repository.save
```

This is useful when composing modules, but it should not hide important boundaries. Prefer explicit methods when behavior or validation is added.

---

## 7. Derives

`derives` lets the compiler or a library generate type class instances.

```scala
trait JsonEncoder[A]

case class User(id: String, name: String) derives JsonEncoder
```

The actual derivation mechanism depends on the type class. Libraries such as Circe, Cats, and Tapir use this pattern to reduce repetitive boilerplate.

Use derivation when:
- the derived instance is obvious and predictable;
- generated behavior matches your domain rules;
- custom edge cases are still tested.

---

## 8. Migration Notes

Common Scala 2 to Scala 3 mappings:

| Scala 2 idiom | Scala 3 preference |
|---|---|
| `implicit val` instance | `given` instance |
| implicit parameter list | `using` parameter list |
| `implicitly[A]` | `summon[A]` |
| implicit class syntax extension | `extension` method |
| sealed trait plus case objects | `enum` |
| value class wrapper | `opaque type` when only type distinction is needed |

Migration strategy:
1. Keep behavior unchanged first.
2. Convert implicit parameters to `using`.
3. Convert type class instances to `given`.
4. Replace simple implicit classes with extension methods.
5. Convert sealed hierarchies to enums only when the API impact is acceptable.

---

## 9. Practice Exercises

1. Define a `Show[A]` type class with given instances for `Int`, `String`, and a `User` case class.
2. Add extension methods for `List[A]`: `secondOption`, `nonEmptyCount`, and `mapToSet`.
3. Model a `CheckoutState` enum with at least four states and write an exhaustive matcher.
4. Create an opaque `Email` type that validates the presence of `@`.
5. Refactor one Scala 2 implicit-class example from Part 8 into Scala 3 extension syntax.

---

## Next Steps

After this chapter, practice the syntax in a real project:
- [Part 11: Mill & Runnable Examples](scala_part11_mill_examples.md)
- [Part 12: Testing](scala_part12_testing.md)

---

> [📚 Table of Contents](../../README.md) | [« Prev: Macros](scala_part9_macros.md) | [Next: Mill & Runnable Examples »](scala_part11_mill_examples.md)
