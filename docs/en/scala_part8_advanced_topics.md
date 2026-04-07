# Scala Tutorial - Part 8: Advanced Topics

> [📚 Table of Contents](../../README.md) | [« Prev: Error Handling](scala_part7_error_handling.md) | [Next: Macros »](scala_part9_macros.md)

---

## Table of Contents
1. [Overview of the Implicit System](#1-overview-of-the-implicit-system)
2. [Implicit Parameters](#2-implicit-parameters)
3. [Implicit Conversions](#3-implicit-conversions)
4. [Implicit Classes](#4-implicit-classes)
5. [Type Classes](#5-type-classes)
6. [Context Bounds](#6-context-bounds)
7. [Implicit Resolution Rules](#7-implicit-resolution-rules)
8. [Advanced Type Classes](#8-advanced-type-classes)
9. [Best Practices](#9-best-practices)
10. [Practice Exercises](#10-practice-exercises)

---

## 1. Overview of the Implicit System

### 1.1 What Are Implicits?

```scala
// The implicit system allows the compiler to automatically fill in certain
// parameters or perform conversions

// Problem: repeatedly passing the same argument
def greet(name: String, greeting: String): String = {
  s"$greeting, $name!"
}

greet("Alice", "Hello")  // Hello, Alice!
greet("Bob", "Hello")    // Hello, Bob!
greet("Charlie", "Hello") // Hello, Charlie!

// Solution: use implicit parameters
def greetImplicit(name: String)(implicit greeting: String): String = {
  s"$greeting, $name!"
}

implicit val defaultGreeting: String = "Hello"

greetImplicit("Alice")   // Hello, Alice! - automatically uses defaultGreeting
greetImplicit("Bob")     // Hello, Bob!
```

### 1.2 Three Uses of the Implicit System

```scala
// 1. Implicit Parameters
//    - Automatically pass contextual information
def process(data: String)(implicit config: Config): String = {
  // use config
  data.toUpperCase
}

// 2. Implicit Conversions
//    - Automatic type conversion
implicit def intToString(x: Int): String = x.toString
val s: String = 42  // automatically converted to "42"

// 3. Implicit Classes
//    - Extend methods on existing types
implicit class RichInt(val x: Int) extends AnyVal {
  def times(f: => Unit): Unit = {
    (1 to x).foreach(_ => f)
  }
}

3.times {
  println("Hello")
}
// Prints "Hello" three times
```

---

## 2. Implicit Parameters

### 2.1 Basic Usage

```scala
// Define a function with an implicit parameter
def greet(name: String)(implicit greeting: String): String = {
  s"$greeting, $name!"
}

// Provide an implicit value
implicit val defaultGreeting: String = "Hello"

// Implicit value is used automatically
greet("Alice")  // "Hello, Alice!"

// Can also be passed explicitly
greet("Bob")("Hi")  // "Hi, Bob!"

// Multiple implicit parameters
def format(value: Double)(implicit precision: Int, symbol: String): String = {
  s"$symbol${value.formatted(s"%.${precision}f")}"
}

implicit val defaultPrecision: Int = 2
implicit val defaultSymbol: String = "$"

format(123.456)  // "$123.46"
```

### 2.2 Common Uses of Implicit Parameters

```scala
// 1. Configuration passing
case class DatabaseConfig(host: String, port: Int)

def connect()(implicit config: DatabaseConfig): String = {
  s"Connecting to ${config.host}:${config.port}"
}

implicit val dbConfig: DatabaseConfig = DatabaseConfig("localhost", 5432)
connect()  // "Connecting to localhost:5432"

// 2. Execution context
import scala.concurrent.{Future, ExecutionContext}

def asyncTask(data: String)(implicit ec: ExecutionContext): Future[String] = {
  Future {
    data.toUpperCase
  }
}

implicit val ec: ExecutionContext = ExecutionContext.global
asyncTask("hello")  // automatically uses the global execution context

// 3. Ordering
def sortList[T](list: List[T])(implicit ordering: Ordering[T]): List[T] = {
  list.sorted
}

sortList(List(3, 1, 4, 1, 5))  // List(1, 1, 3, 4, 5)
// Ordering[Int] is a predefined implicit value

case class Person(name: String, age: Int)

implicit val personOrdering: Ordering[Person] = Ordering.by(_.age)
val people = List(Person("Alice", 25), Person("Bob", 20))
sortList(people)  // sorted by age
```

### 2.3 Differences Between Implicit Parameters and Default Parameters

```scala
// Default parameters: specified at the definition site
def greetDefault(name: String, greeting: String = "Hello"): String = {
  s"$greeting, $name!"
}

// Implicit parameters: specified at the call site's scope
def greetImplicit(name: String)(implicit greeting: String): String = {
  s"$greeting, $name!"
}

// Default parameter: fixed
greetDefault("Alice")  // always "Hello, Alice!"

// Implicit parameter: can vary across different scopes
{
  implicit val g1: String = "Hi"
  greetImplicit("Alice")  // "Hi, Alice!"
}

{
  implicit val g2: String = "Hey"
  greetImplicit("Alice")  // "Hey, Alice!"
}
```

---

## 3. Implicit Conversions

### 3.1 Basic Implicit Conversions

```scala
// Define an implicit conversion
implicit def intToString(x: Int): String = x.toString

// Use the implicit conversion
val s: String = 42  // compiler inserts: intToString(42)

def printString(s: String): Unit = println(s)
printString(123)  // automatically converted

// A more practical example: date handling
import java.time.LocalDate

implicit def stringToDate(s: String): LocalDate = {
  LocalDate.parse(s)
}

def daysBetween(start: LocalDate, end: LocalDate): Long = {
  java.time.temporal.ChronoUnit.DAYS.between(start, end)
}

// Strings can be passed directly
daysBetween("2024-01-01", "2024-01-10")  // 9
```

### 3.2 The Dangers of Implicit Conversions

```scala
// ⚠️ Implicit conversions can lead to unexpected behavior

// Defining an overly broad conversion
implicit def anyToString[T](x: T): String = x.toString

val x: String = List(1, 2, 3)  // "List(1, 2, 3)" - may not be what you want

// Causing ambiguity
implicit def intToDouble(x: Int): Double = x.toDouble
implicit def intToFloat(x: Int): Float = x.toFloat

// val d: Number = 42  // Compile error: ambiguous!

// Therefore, Scala 2.13+ recommends:
// - Avoid using implicit conversions
// - Use implicit classes instead (see next section)
// - Use explicit conversion methods
```

### 3.3 View Bounds (Deprecated)

```scala
// Scala 2.x View Bounds - deprecated
// def process[T <% String](value: T): String = value

// Modern approach: use an implicit conversion function
def process[T](value: T)(implicit conv: T => String): String = conv(value)

implicit def intToStr(x: Int): String = x.toString
process(42)  // "42"
```

---

## 4. Implicit Classes

### 4.1 Basic Usage

```scala
// Implicit classes extend existing types
implicit class RichInt(val x: Int) extends AnyVal {
  def times(f: => Unit): Unit = {
    (1 to x).foreach(_ => f)
  }
  
  def squared: Int = x * x
  
  def isEven: Boolean = x % 2 == 0
}

// Use the extension methods
5.times {
  println("Hello")
}

println(3.squared)  // 9
println(4.isEven)   // true

// What the compiler actually does:
// new RichInt(5).times { println("Hello") }
```

### 4.2 Value Classes

```scala
// Use AnyVal to avoid runtime overhead
implicit class RichString(val s: String) extends AnyVal {
  def titleCase: String = {
    s.split(" ").map(_.capitalize).mkString(" ")
  }
  
  def isEmail: Boolean = {
    s.contains("@") && s.contains(".")
  }
}

"hello world".titleCase  // "Hello World"
"test@example.com".isEmail  // true

// Restrictions:
// 1. Can only have one parameter
// 2. Cannot define other val/var members
// 3. Cannot extend other classes (except AnyVal)
```

### 4.3 Useful Extensions

```scala
// String extensions
implicit class StringOps(val s: String) extends AnyVal {
  def toIntOption: Option[Int] = {
    try Some(s.toInt)
    catch { case _: NumberFormatException => None }
  }
  
  def repeat(n: Int): String = s * n
  
  def truncate(maxLength: Int): String = {
    if (s.length <= maxLength) s
    else s.take(maxLength - 3) + "..."
  }
}

"42".toIntOption      // Some(42)
"abc".toIntOption     // None
"Hi".repeat(3)        // "HiHiHi"
"Long text".truncate(7)  // "Long..."

// Collection extensions
implicit class ListOps[T](val list: List[T]) extends AnyVal {
  def secondOption: Option[T] = list match {
    case _ :: second :: _ => Some(second)
    case _ => None
  }
  
  def split(n: Int): (List[T], List[T]) = {
    list.splitAt(n)
  }
}

List(1, 2, 3, 4, 5).secondOption  // Some(2)
List(1, 2, 3).split(2)             // (List(1, 2), List(3))

// Option extensions
implicit class OptionOps[T](val opt: Option[T]) extends AnyVal {
  def orThrow(ex: => Exception): T = opt.getOrElse(throw ex)
  
  def toEither[L](left: => L): Either[L, T] = {
    opt.toRight(left)
  }
}

Some(42).orThrow(new Exception("Missing"))  // 42
// None.orThrow(new Exception("Missing"))   // throws exception

Some(42).toEither("error")  // Right(42)
None.toEither("error")      // Left("error")
```

---

## 5. Type Classes

### 5.1 What Is a Type Class?

```scala
// A Type Class is a design pattern used to provide
// behavior for types without modifying the types themselves

// Problem: how to support JSON serialization for different types?

// ❌ Traditional OOP approach: modify each class
trait JsonSerializable {
  def toJson: String
}

case class Person(name: String) extends JsonSerializable {
  def toJson: String = s"""{"name":"$name"}"""
}
// But this requires modifying source code, and you cannot add it
// to built-in types like Int or String

// ✅ Type class approach: define external behavior
trait JsonSerializer[T] {
  def toJson(value: T): String
}

// Provide instances for different types
implicit val intSerializer: JsonSerializer[Int] = new JsonSerializer[Int] {
  def toJson(value: Int): String = value.toString
}

implicit val stringSerializer: JsonSerializer[String] = new JsonSerializer[String] {
  def toJson(value: String): String = s""""$value""""
}

// Usage
def serialize[T](value: T)(implicit serializer: JsonSerializer[T]): String = {
  serializer.toJson(value)
}

serialize(42)      // "42"
serialize("hello") // "\"hello\""
```

### 5.2 Defining a Type Class

```scala
// 1. Define the type class trait
trait Show[T] {
  def show(value: T): String
}

// 2. Provide instances
object Show {
  // Helper method to summon an instance
  def apply[T](implicit instance: Show[T]): Show[T] = instance
  
  // Factory method to create instances
  def instance[T](f: T => String): Show[T] = new Show[T] {
    def show(value: T): String = f(value)
  }
}

// 3. Provide instances for concrete types
implicit val intShow: Show[Int] = Show.instance(_.toString)

implicit val stringShow: Show[String] = Show.instance(s => s""""$s"""")

implicit val booleanShow: Show[Boolean] = Show.instance {
  case true => "yes"
  case false => "no"
}

// 4. Use the type class
def print[T](value: T)(implicit shower: Show[T]): Unit = {
  println(shower.show(value))
}

print(42)       // "42"
print("hello")  // "\"hello\""
print(true)     // "yes"
```

### 5.3 Providing Instances for Custom Types

```scala
case class Person(name: String, age: Int)

// Define the implicit instance in the companion object
object Person {
  implicit val personShow: Show[Person] = Show.instance { p =>
    s"Person(${p.name}, ${p.age})"
  }
}

print(Person("Alice", 25))  // "Person(Alice, 25)"

// Provide instances for collection types
implicit def listShow[T](implicit itemShow: Show[T]): Show[List[T]] = {
  Show.instance { list =>
    list.map(itemShow.show).mkString("[", ", ", "]")
  }
}

print(List(1, 2, 3))  // "[1, 2, 3]"
print(List("a", "b"))  // "[\"a\", \"b\"]"
```

### 5.4 Standard Type Class Examples

```scala
// 1. Ordering - sorting
trait MyOrdering[T] {
  def compare(x: T, y: T): Int
}

implicit val intOrdering: MyOrdering[Int] = new MyOrdering[Int] {
  def compare(x: Int, y: Int): Int = x - y
}

def sort[T](list: List[T])(implicit ord: MyOrdering[T]): List[T] = {
  list.sortWith((a, b) => ord.compare(a, b) < 0)
}

// 2. Numeric - numeric operations
trait MyNumeric[T] {
  def plus(x: T, y: T): T
  def times(x: T, y: T): T
  def zero: T
}

implicit val intNumeric: MyNumeric[Int] = new MyNumeric[Int] {
  def plus(x: Int, y: Int): Int = x + y
  def times(x: Int, y: Int): Int = x * y
  def zero: Int = 0
}

def sum[T](list: List[T])(implicit num: MyNumeric[T]): T = {
  list.foldLeft(num.zero)(num.plus)
}

sum(List(1, 2, 3, 4, 5))  // 15

// 3. Equality
trait Eq[T] {
  def eqv(x: T, y: T): Boolean
}

implicit val intEq: Eq[Int] = new Eq[Int] {
  def eqv(x: Int, y: Int): Boolean = x == y
}

def contains[T](list: List[T], item: T)(implicit eq: Eq[T]): Boolean = {
  list.exists(eq.eqv(_, item))
}
```

---

## 6. Context Bounds

### 6.1 Basic Syntax

```scala
// Traditional style: implicit parameter
def print[T](value: T)(implicit shower: Show[T]): Unit = {
  println(shower.show(value))
}

// Context Bound syntactic sugar
def printCB[T: Show](value: T): Unit = {
  val shower = implicitly[Show[T]]  // retrieve the implicit instance
  println(shower.show(value))
}

// Or use Show.apply
def printCB2[T: Show](value: T): Unit = {
  println(Show[T].show(value))
}

// Equivalent to:
// def printCB[T](value: T)(implicit evidence$1: Show[T]): Unit
```

### 6.2 Multiple Context Bounds

```scala
// A type parameter can have multiple context bounds
def process[T: Show: Ordering](value: T): String = {
  val shower = implicitly[Show[T]]
  val ord = implicitly[Ordering[T]]
  
  shower.show(value)
}

// Equivalent to:
// def process[T](value: T)(implicit shower: Show[T], ord: Ordering[T])
```

### 6.3 Practical Example

```scala
// JSON serialization
trait JsonWriter[T] {
  def write(value: T): String
}

object JsonWriter {
  def apply[T](implicit instance: JsonWriter[T]): JsonWriter[T] = instance
}

implicit val intWriter: JsonWriter[Int] = new JsonWriter[Int] {
  def write(value: Int): String = value.toString
}

implicit val stringWriter: JsonWriter[String] = new JsonWriter[String] {
  def write(value: String): String = s""""$value""""
}

// Use a context bound
def toJson[T: JsonWriter](value: T): String = {
  JsonWriter[T].write(value)
}

toJson(42)      // "42"
toJson("hello") // "\"hello\""

// Provide an instance for List
implicit def listWriter[T: JsonWriter]: JsonWriter[List[T]] = {
  new JsonWriter[List[T]] {
    def write(values: List[T]): String = {
      val writer = JsonWriter[T]
      values.map(writer.write).mkString("[", ",", "]")
    }
  }
}

toJson(List(1, 2, 3))  // "[1,2,3]"
```

---

## 7. Implicit Resolution Rules

### 7.1 Implicit Search Scope

```scala
// The order in which the compiler searches for implicit values:

// 1. Current scope
def example1(): Unit = {
  implicit val x: Int = 42
  
  def needsImplicit(implicit i: Int): Int = i
  
  needsImplicit  // finds x
}

// 2. Explicitly imported implicits
object Implicits {
  implicit val greeting: String = "Hello"
}

def example2(): Unit = {
  import Implicits._
  
  def needsGreeting(implicit g: String): String = g
  
  needsGreeting  // finds greeting
}

// 3. Companion objects
trait Show[T] {
  def show(value: T): String
}

object Show {
  // defined in the companion object
  implicit val intShow: Show[Int] = new Show[Int] {
    def show(value: Int): String = value.toString
  }
}

def print[T](value: T)(implicit shower: Show[T]): Unit = {
  println(shower.show(value))
}

print(42)  // automatically finds Show.intShow

// 4. Companion object of a type parameter
case class Person(name: String)

object Person {
  implicit val personShow: Show[Person] = new Show[Person] {
    def show(value: Person): String = s"Person(${value.name})"
  }
}

print(Person("Alice"))  // automatically finds Person.personShow
```

### 7.2 Implicit Priority

```scala
// When there are multiple candidates, the compiler selects the most specific one

trait Animal
class Dog extends Animal
class Puppy extends Dog

object Implicits {
  implicit val animalValue: Animal = new Animal {}
  implicit val dogValue: Dog = new Dog
}

import Implicits._

def needsAnimal(implicit a: Animal): Animal = a

// dogValue is chosen because Dog is more specific than Animal
needsAnimal  // dogValue

// Locally defined implicits take priority over imported ones
def example(): Unit = {
  import Implicits._
  
  implicit val localDog: Dog = new Dog
  
  needsAnimal  // uses localDog (local definition takes priority)
}
```

### 7.3 Avoiding Ambiguity

```scala
// ❌ Ambiguity error
object Bad {
  implicit val int1: Int = 1
  implicit val int2: Int = 2
  
  def needsInt(implicit i: Int): Int = i
  
  // needsInt  // Compile error: ambiguous implicit values
}

// ✅ Use distinct types
object Good {
  case class UserId(value: Int)
  case class OrderId(value: Int)
  
  implicit val userId: UserId = UserId(1)
  implicit val orderId: OrderId = OrderId(2)
  
  def processUser(implicit id: UserId): Int = id.value
  def processOrder(implicit id: OrderId): Int = id.value
  
  processUser   // 1
  processOrder  // 2
}
```

---

## 8. Advanced Type Classes

### 8.1 Monoid

```scala
// Monoid: a type class with an associative binary operation and an identity element
trait Monoid[T] {
  def empty: T                          // identity element
  def combine(x: T, y: T): T           // associative operation
}

object Monoid {
  def apply[T](implicit instance: Monoid[T]): Monoid[T] = instance
}

// Int addition monoid
implicit val intAddMonoid: Monoid[Int] = new Monoid[Int] {
  def empty: Int = 0
  def combine(x: Int, y: Int): Int = x + y
}

// Int multiplication monoid
val intMultiplyMonoid: Monoid[Int] = new Monoid[Int] {
  def empty: Int = 1
  def combine(x: Int, y: Int): Int = x * y
}

// String monoid
implicit val stringMonoid: Monoid[String] = new Monoid[String] {
  def empty: String = ""
  def combine(x: String, y: String): String = x + y
}

// List monoid
implicit def listMonoid[T]: Monoid[List[T]] = new Monoid[List[T]] {
  def empty: List[T] = Nil
  def combine(x: List[T], y: List[T]): List[T] = x ++ y
}

// Using the monoid
def combineAll[T: Monoid](values: List[T]): T = {
  val m = Monoid[T]
  values.foldLeft(m.empty)(m.combine)
}

combineAll(List(1, 2, 3, 4, 5))           // 15
combineAll(List("Hello", " ", "World"))   // "Hello World"
combineAll(List(List(1, 2), List(3, 4)))  // List(1, 2, 3, 4)
```

### 8.2 Functor

```scala
// Functor: a type that can be mapped over
trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

object Functor {
  def apply[F[_]](implicit instance: Functor[F]): Functor[F] = instance
}

// List functor
implicit val listFunctor: Functor[List] = new Functor[List] {
  def map[A, B](fa: List[A])(f: A => B): List[B] = fa.map(f)
}

// Option functor
implicit val optionFunctor: Functor[Option] = new Functor[Option] {
  def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)
}

// Usage
def increment[F[_]: Functor](container: F[Int]): F[Int] = {
  Functor[F].map(container)(_ + 1)
}

increment(List(1, 2, 3))     // List(2, 3, 4)
increment(Some(42))          // Some(43)
increment(None)              // None
```

### 8.3 Serializable Type Class

```scala
// A complete serialization framework
trait Encoder[T] {
  def encode(value: T): String
}

trait Decoder[T] {
  def decode(s: String): Option[T]
}

trait Codec[T] extends Encoder[T] with Decoder[T]

object Codec {
  def apply[T](implicit instance: Codec[T]): Codec[T] = instance
  
  def instance[T](enc: T => String, dec: String => Option[T]): Codec[T] = {
    new Codec[T] {
      def encode(value: T): String = enc(value)
      def decode(s: String): Option[T] = dec(s)
    }
  }
}

// Codecs for primitive types
implicit val intCodec: Codec[Int] = Codec.instance(
  _.toString,
  s => s.toIntOption
)

implicit val stringCodec: Codec[String] = Codec.instance(
  identity,
  Some(_)
)

implicit val booleanCodec: Codec[Boolean] = Codec.instance(
  _.toString,
  s => s.toBooleanOption
)

// Composing codecs
implicit def listCodec[T: Codec]: Codec[List[T]] = {
  val itemCodec = Codec[T]
  Codec.instance(
    list => list.map(itemCodec.encode).mkString(","),
    s => {
      val items = s.split(",").toList
      val decoded = items.map(itemCodec.decode)
      if (decoded.forall(_.isDefined)) Some(decoded.flatten)
      else None
    }
  )
}

// Usage
def serialize[T: Codec](value: T): String = {
  Codec[T].encode(value)
}

def deserialize[T: Codec](s: String): Option[T] = {
  Codec[T].decode(s)
}

serialize(42)              // "42"
serialize(List(1, 2, 3))   // "1,2,3"
deserialize[Int]("42")     // Some(42)
deserialize[List[Int]]("1,2,3")  // Some(List(1, 2, 3))
```

---

## 9. Best Practices

### 9.1 When to Use Implicits

```scala
// ✅ Good use cases:

// 1. Type classes
trait JsonWriter[T] {
  def write(value: T): String
}

// 2. Contextual information
def query(sql: String)(implicit connection: DatabaseConnection): Result = ???

// 3. Extension methods (using implicit classes)
implicit class RichString(s: String) extends AnyVal {
  def isPalindrome: Boolean = s == s.reverse
}

// ❌ Scenarios to avoid:

// 1. Implicit conversions (can cause confusion)
// implicit def intToString(i: Int): String = i.toString

// 2. Too many implicit parameters
// def bad(a: String)(implicit b: Int, c: String, d: Double, e: Boolean): Unit

// 3. Implicits that are too generic
// implicit val x: Int = 42  // too broadly applicable
```

### 9.2 Naming Conventions

```scala
// ✅ Good naming

// Type class instances: use descriptive names
implicit val personJsonWriter: JsonWriter[Person] = ???
implicit val intOrdering: Ordering[Int] = ???

// Implicit classes: Rich* or *Ops
implicit class RichInt(val x: Int) extends AnyVal
implicit class StringOps(val s: String) extends AnyVal

// ❌ Poor naming
// implicit val x: JsonWriter[Person] = ???  // unclear
// implicit val impl: Ordering[Int] = ???    // too generic
```

### 9.3 Organizing Implicits

```scala
// Approach 1: in companion objects
case class Person(name: String, age: Int)

object Person {
  implicit val ordering: Ordering[Person] = Ordering.by(_.age)
  implicit val jsonWriter: JsonWriter[Person] = ???
}

// Approach 2: in a dedicated Implicits object
object JsonWriters {
  implicit val intWriter: JsonWriter[Int] = ???
  implicit val stringWriter: JsonWriter[String] = ???
  implicit val personWriter: JsonWriter[Person] = ???
}

// Import explicitly when needed
import JsonWriters._

// Approach 3: in a package object (use with caution)
package object myapp {
  implicit val defaultTimeout: Timeout = Timeout(30.seconds)
}
```

### 9.4 Debugging Implicits

```scala
// Inspect what implicit the compiler selects
import scala.language.implicitConversions

// Use implicitly to check
val writer = implicitly[JsonWriter[Int]]

// Enable compiler options in sbt
// scalacOptions += "-Xlog-implicits"

// Manual resolution
def debug[T](value: T)(implicit writer: JsonWriter[T]): Unit = {
  println(s"Using writer: ${writer.getClass.getName}")
  println(s"Result: ${writer.write(value)}")
}
```

---

## 10. Practice Exercises

### Exercise 1: Custom Show Type Class

```scala
// A complete Show type class implementation
trait Show[T] {
  def show(value: T): String
}

object Show {
  // Summon an implicit instance
  def apply[T](implicit instance: Show[T]): Show[T] = instance
  
  // Create an instance
  def instance[T](f: T => String): Show[T] = new Show[T] {
    def show(value: T): String = f(value)
  }
  
  // Syntactic sugar
  implicit class ShowOps[T](val value: T) extends AnyVal {
    def show(implicit s: Show[T]): String = s.show(value)
  }
  
  // Instances for primitive types
  implicit val intShow: Show[Int] = instance(_.toString)
  implicit val stringShow: Show[String] = instance(s => s""""$s"""")
  implicit val booleanShow: Show[Boolean] = instance(_.toString)
  implicit val doubleShow: Show[Double] = instance(d => f"$d%.2f")
  
  // Instances for container types
  implicit def optionShow[T: Show]: Show[Option[T]] = instance {
    case Some(value) => s"Some(${value.show})"
    case None => "None"
  }
  
  implicit def listShow[T: Show]: Show[List[T]] = instance { list =>
    list.map(_.show).mkString("List(", ", ", ")")
  }
  
  implicit def mapShow[K: Show, V: Show]: Show[Map[K, V]] = instance { map =>
    map.map { case (k, v) => s"${k.show} -> ${v.show}" }
      .mkString("Map(", ", ", ")")
  }
  
  // Tuple instance
  implicit def tuple2Show[A: Show, B: Show]: Show[(A, B)] = instance {
    case (a, b) => s"(${a.show}, ${b.show})"
  }
}

// Test
object ShowDemo {
  import Show._
  
  case class Person(name: String, age: Int)
  
  object Person {
    implicit val personShow: Show[Person] = Show.instance { p =>
      s"Person(name=${p.name.show}, age=${p.age.show})"
    }
  }
  
  def main(args: Array[String]): Unit = {
    println(42.show)
    println("hello".show)
    println(true.show)
    println(3.14159.show)
    
    println(Some(42).show)
    println(None.show)
    println(List(1, 2, 3).show)
    println(Map("a" -> 1, "b" -> 2).show)
    println((42, "hello").show)
    
    val person = Person("Alice", 25)
    println(person.show)
  }
}
```

### Exercise 2: Equal Type Class

```scala
// The Equal type class for type-safe equality comparison
trait Equal[T] {
  def eqv(x: T, y: T): Boolean
  def neqv(x: T, y: T): Boolean = !eqv(x, y)
}

object Equal {
  def apply[T](implicit instance: Equal[T]): Equal[T] = instance
  
  def instance[T](f: (T, T) => Boolean): Equal[T] = new Equal[T] {
    def eqv(x: T, y: T): Boolean = f(x, y)
  }
  
  // Syntactic sugar
  implicit class EqualOps[T](val x: T) extends AnyVal {
    def ===(y: T)(implicit eq: Equal[T]): Boolean = eq.eqv(x, y)
    def =/=(y: T)(implicit eq: Equal[T]): Boolean = eq.neqv(x, y)
  }
  
  // Instances for primitive types
  implicit val intEqual: Equal[Int] = instance(_ == _)
  implicit val stringEqual: Equal[String] = instance(_ == _)
  implicit val booleanEqual: Equal[Boolean] = instance(_ == _)
  
  // Instances for container types
  implicit def optionEqual[T: Equal]: Equal[Option[T]] = instance {
    case (Some(x), Some(y)) => x === y
    case (None, None) => true
    case _ => false
  }
  
  implicit def listEqual[T: Equal]: Equal[List[T]] = instance { (xs, ys) =>
    xs.length == ys.length && xs.zip(ys).forall { case (x, y) => x === y }
  }
}

// Test
object EqualDemo {
  import Equal._
  
  case class Person(name: String, age: Int)
  
  object Person {
    implicit val personEqual: Equal[Person] = Equal.instance { (p1, p2) =>
      p1.name === p2.name && p1.age === p2.age
    }
  }
  
  def main(args: Array[String]): Unit = {
    println(1 === 1)          // true
    println(1 === 2)          // false
    println("a" === "a")      // true
    
    println(Some(1) === Some(1))    // true
    println(Some(1) === Some(2))    // false
    println(Some(1) === None)       // false
    
    println(List(1, 2) === List(1, 2))  // true
    println(List(1, 2) === List(2, 1))  // false
    
    val p1 = Person("Alice", 25)
    val p2 = Person("Alice", 25)
    val p3 = Person("Bob", 30)
    
    println(p1 === p2)  // true
    println(p1 === p3)  // false
    
    // Type-safe: cannot compare different types
    // println(1 === "1")  // Compile error!
  }
}
```

### Exercise 3: Monoid Implementation

```scala
// A complete Monoid type class
trait Semigroup[T] {
  def combine(x: T, y: T): T
}

trait Monoid[T] extends Semigroup[T] {
  def empty: T
}

object Monoid {
  def apply[T](implicit instance: Monoid[T]): Monoid[T] = instance
  
  def instance[T](emptyValue: T)(combineFunc: (T, T) => T): Monoid[T] = {
    new Monoid[T] {
      def empty: T = emptyValue
      def combine(x: T, y: T): T = combineFunc(x, y)
    }
  }
  
  // Syntactic sugar
  implicit class MonoidOps[T](val x: T) extends AnyVal {
    def |+|(y: T)(implicit m: Monoid[T]): T = m.combine(x, y)
  }
  
  // Instances for primitive types
  implicit val intAdditionMonoid: Monoid[Int] = instance(0)(_ + _)
  
  implicit val intMultiplicationMonoid: Monoid[Int] = instance(1)(_ * _)
  
  implicit val stringMonoid: Monoid[String] = instance("")(_ + _)
  
  implicit val booleanAndMonoid: Monoid[Boolean] = instance(true)(_ && _)
  
  implicit val booleanOrMonoid: Monoid[Boolean] = instance(false)(_ || _)
  
  // Instances for container types
  implicit def optionMonoid[T: Semigroup]: Monoid[Option[T]] = {
    new Monoid[Option[T]] {
      def empty: Option[T] = None
      def combine(x: Option[T], y: Option[T]): Option[T] = (x, y) match {
        case (Some(a), Some(b)) => Some(implicitly[Semigroup[T]].combine(a, b))
        case (Some(a), None) => Some(a)
        case (None, Some(b)) => Some(b)
        case (None, None) => None
      }
    }
  }
  
  implicit def listMonoid[T]: Monoid[List[T]] = instance(Nil)(_ ++ _)
  
  implicit def mapMonoid[K, V: Semigroup]: Monoid[Map[K, V]] = {
    new Monoid[Map[K, V]] {
      def empty: Map[K, V] = Map.empty
      def combine(x: Map[K, V], y: Map[K, V]): Map[K, V] = {
        val sg = implicitly[Semigroup[V]]
        y.foldLeft(x) { case (acc, (k, v)) =>
          acc.updated(k, acc.get(k).fold(v)(sg.combine(_, v)))
        }
      }
    }
  }
  
  // Utility functions
  def combineAll[T: Monoid](values: List[T]): T = {
    val m = Monoid[T]
    values.foldLeft(m.empty)(m.combine)
  }
  
  def combineN[T: Monoid](value: T, n: Int): T = {
    val m = Monoid[T]
    (1 to n).foldLeft(m.empty)((acc, _) => m.combine(acc, value))
  }
}

// Test
object MonoidDemo {
  import Monoid._
  
  def main(args: Array[String]): Unit = {
    // Numbers
    println(combineAll(List(1, 2, 3, 4, 5)))  // 15
    println(1 |+| 2 |+| 3)                     // 6
    
    // Strings
    println(combineAll(List("Hello", " ", "World")))  // "Hello World"
    println("Hello" |+| " " |+| "Scala")              // "Hello Scala"
    
    // Lists
    println(combineAll(List(List(1, 2), List(3, 4), List(5))))
    // List(1, 2, 3, 4, 5)
    
    // Maps (requires a Semigroup for the value type)
    implicit val intAddSemigroup: Semigroup[Int] = new Semigroup[Int] {
      def combine(x: Int, y: Int): Int = x + y
    }
    
    val map1 = Map("a" -> 1, "b" -> 2)
    val map2 = Map("b" -> 3, "c" -> 4)
    println(map1 |+| map2)  // Map(a -> 1, b -> 5, c -> 4)
    
    // combineN
    println(combineN("Hi", 3))  // "HiHiHi"
    println(combineN(5, 4))     // 20
  }
}
```

### Exercise 4: Functor and Applicative

```scala
// Functor type class
trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

object Functor {
  def apply[F[_]](implicit instance: Functor[F]): Functor[F] = instance
  
  implicit class FunctorOps[F[_], A](val fa: F[A]) extends AnyVal {
    def map[B](f: A => B)(implicit functor: Functor[F]): F[B] = {
      functor.map(fa)(f)
    }
    
    def as[B](b: B)(implicit functor: Functor[F]): F[B] = {
      functor.map(fa)(_ => b)
    }
    
    def void(implicit functor: Functor[F]): F[Unit] = {
      functor.map(fa)(_ => ())
    }
  }
  
  // Instances
  implicit val listFunctor: Functor[List] = new Functor[List] {
    def map[A, B](fa: List[A])(f: A => B): List[B] = fa.map(f)
  }
  
  implicit val optionFunctor: Functor[Option] = new Functor[Option] {
    def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)
  }
  
  implicit def eitherFunctor[L]: Functor[Either[L, *]] = {
    new Functor[Either[L, *]] {
      def map[A, B](fa: Either[L, A])(f: A => B): Either[L, B] = fa.map(f)
    }
  }
}

// Applicative type class
trait Applicative[F[_]] extends Functor[F] {
  def pure[A](a: A): F[A]
  def ap[A, B](ff: F[A => B])(fa: F[A]): F[B]
  
  // Implement map in terms of ap
  def map[A, B](fa: F[A])(f: A => B): F[B] = {
    ap(pure(f))(fa)
  }
  
  def map2[A, B, C](fa: F[A], fb: F[B])(f: (A, B) => C): F[C] = {
    ap(map(fa)(a => (b: B) => f(a, b)))(fb)
  }
}

object Applicative {
  def apply[F[_]](implicit instance: Applicative[F]): Applicative[F] = instance
  
  // Instances
  implicit val listApplicative: Applicative[List] = new Applicative[List] {
    def pure[A](a: A): List[A] = List(a)
    def ap[A, B](ff: List[A => B])(fa: List[A]): List[B] = {
      for {
        f <- ff
        a <- fa
      } yield f(a)
    }
  }
  
  implicit val optionApplicative: Applicative[Option] = new Applicative[Option] {
    def pure[A](a: A): Option[A] = Some(a)
    def ap[A, B](ff: Option[A => B])(fa: Option[A]): Option[B] = {
      (ff, fa) match {
        case (Some(f), Some(a)) => Some(f(a))
        case _ => None
      }
    }
  }
}

// Test
object FunctorDemo {
  import Functor._
  import Applicative._
  
  def main(args: Array[String]): Unit = {
    // Functor
    val list = List(1, 2, 3)
    println(list.map(_ * 2))      // List(2, 4, 6)
    println(list.as("x"))          // List(x, x, x)
    println(list.void)             // List((), (), ())
    
    val opt = Some(42)
    println(opt.map(_ * 2))        // Some(84)
    
    // Applicative
    val app = Applicative[Option]
    println(app.pure(42))          // Some(42)
    println(app.map2(Some(2), Some(3))(_ + _))  // Some(5)
    
    val listApp = Applicative[List]
    println(listApp.map2(List(1, 2), List(10, 20))(_ + _))
    // List(11, 21, 12, 22)
  }
}
```

### Exercise 5: Validation Type Class

```scala
// Validation result
sealed trait Validated[+E, +A]
case class Valid[+A](value: A) extends Validated[Nothing, A]
case class Invalid[+E](errors: List[E]) extends Validated[E, Nothing]

object Validated {
  // Applicative instance
  implicit def validatedApplicative[E]: Applicative[Validated[E, *]] = {
    new Applicative[Validated[E, *]] {
      def pure[A](a: A): Validated[E, A] = Valid(a)
      
      def ap[A, B](ff: Validated[E, A => B])(fa: Validated[E, A]): Validated[E, B] = {
        (ff, fa) match {
          case (Valid(f), Valid(a)) => Valid(f(a))
          case (Invalid(e1), Invalid(e2)) => Invalid(e1 ++ e2)
          case (Invalid(e), _) => Invalid(e)
          case (_, Invalid(e)) => Invalid(e)
        }
      }
    }
  }
}

// Validator
case class Validator[E, A](run: A => Validated[E, A]) {
  def apply(value: A): Validated[E, A] = run(value)
  
  def and(other: Validator[E, A]): Validator[E, A] = Validator { value =>
    (this.run(value), other.run(value)) match {
      case (Valid(_), Valid(_)) => Valid(value)
      case (Invalid(e1), Invalid(e2)) => Invalid(e1 ++ e2)
      case (Invalid(e), _) => Invalid(e)
      case (_, Invalid(e)) => Invalid(e)
    }
  }
}

object Validators {
  def nonEmpty(field: String): Validator[String, String] = Validator { value =>
    if (value.isEmpty) Invalid(List(s"$field cannot be empty"))
    else Valid(value)
  }
  
  def minLength(field: String, min: Int): Validator[String, String] = Validator { value =>
    if (value.length < min) Invalid(List(s"$field must be at least $min characters"))
    else Valid(value)
  }
  
  def maxLength(field: String, max: Int): Validator[String, String] = Validator { value =>
    if (value.length > max) Invalid(List(s"$field must be at most $max characters"))
    else Valid(value)
  }
  
  def matches(field: String, regex: String): Validator[String, String] = Validator { value =>
    if (!value.matches(regex)) Invalid(List(s"$field has an invalid format"))
    else Valid(value)
  }
  
  def range(field: String, min: Int, max: Int): Validator[String, Int] = Validator { value =>
    if (value < min || value > max) 
      Invalid(List(s"$field must be between $min and $max"))
    else Valid(value)
  }
}

// Usage example
object ValidationDemo {
  import Validators._
  
  case class User(username: String, email: String, age: Int)
  
  val usernameValidator = 
    nonEmpty("Username")
      .and(minLength("Username", 3))
      .and(maxLength("Username", 20))
  
  val emailValidator = 
    nonEmpty("Email")
      .and(matches("Email", """^[\w\.-]+@[\w\.-]+\.\w+$"""))
  
  val ageValidator = range("Age", 13, 120)
  
  def validateUser(username: String, email: String, age: Int): Validated[String, User] = {
    val usernameResult = usernameValidator(username)
    val emailResult = emailValidator(email)
    val ageResult = ageValidator(age)
    
    (usernameResult, emailResult, ageResult) match {
      case (Valid(_), Valid(_), Valid(_)) => Valid(User(username, email, age))
      case _ =>
        val errors = List(usernameResult, emailResult, ageResult).collect {
          case Invalid(errs) => errs
        }.flatten
        Invalid(errors)
    }
  }
  
  def main(args: Array[String]): Unit = {
    // Valid input
    validateUser("alice", "alice@example.com", 25) match {
      case Valid(user) => println(s"✓ Valid: $user")
      case Invalid(errors) => 
        println("✗ Errors:")
        errors.foreach(e => println(s"  - $e"))
    }
    
    // Invalid input - accumulates all errors
    validateUser("ab", "invalid", 200) match {
      case Valid(user) => println(s"✓ Valid: $user")
      case Invalid(errors) => 
        println("✗ Errors:")
        errors.foreach(e => println(s"  - $e"))
    }
  }
}
```

---

## 11. Key Takeaways

### Implicit System
- **Implicit Parameters**: automatically pass contextual information
- **Implicit Conversions**: automatic type conversion (use with caution)
- **Implicit Classes**: extend existing types

### Type Classes
- **Define behavior**: without modifying the original type
- **Polymorphism**: provide a uniform interface for different types
- **Composability**: multiple type classes can be composed

### Context Bounds
- Syntactic sugar: `[T: TypeClass]`
- Simplifies implicit parameter declarations
- Requires `implicitly` or `TypeClass.apply` to access the instance

### Best Practices
- Prefer implicit classes over implicit conversions
- Place implicit instances in companion objects
- Use descriptive naming
- Use implicits judiciously — avoid overuse

---

## Next Steps

After completing Part 8, you have mastered:
- Implicit parameters and implicit classes
- The type class design pattern
- Context Bounds syntax
- Standard type classes (Monoid, Functor, etc.)

**Note**: Scala 3 introduces new implicit syntax (`given`/`using`), but the core concepts are the same. This tutorial focuses on Scala 2 syntax, and this knowledge remains applicable in Scala 3.

Recommended next topics:
- Functional programming libraries such as Cats/Scalaz
- Concurrent programming (Future, Akka)
- Advanced type system features
- [Part 9: Macros](scala_part9_macros.md)

Congratulations on completing the Advanced Topics section!

---

> [📚 Table of Contents](../../README.md) | [« Prev: Error Handling](scala_part7_error_handling.md) | [Next: Macros »](scala_part9_macros.md)
