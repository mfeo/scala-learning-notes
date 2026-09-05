# Scala Tutorial - Part 2: Basic Syntax

> [Runnable example and tests](../../examples/src/examples/basics) | [📚 Table of Contents](../../README.md) | [« Prev: Introduction](scala_part1_introduction.md) | [Next: Functions »](scala_part3_functions.md)

---

## Table of Contents
1. [Variable Declaration](#1-variable-declaration)
2. [Basic Data Types](#2-basic-data-types)
3. [String Operations](#3-string-operations)
4. [Operators](#4-operators)
5. [Conditionals](#5-conditionals)
6. [Loops](#6-loops)
7. [Expressions vs Statements](#7-expressions-vs-statements)
8. [Code Blocks](#8-code-blocks)
9. [Practice Exercises](#9-practice-exercises)

---

## 1. Variable Declaration

### 1.1 val - Immutable Variables (Recommended)

Variables defined with `val` cannot be reassigned after initialization, similar to `final` in Java or `const` in JavaScript.

```scala
val name: String = "Alice"
val age: Int = 25

// Error! Cannot reassign
// name = "Bob"  // Compile error: reassignment to val

// Type inference - compiler automatically infers the type
val city = "Taipei"        // inferred as String
val population = 2600000   // inferred as Int
val temperature = 25.5     // inferred as Double
val isCapital = true       // inferred as Boolean
```

**Why prefer val?**
- Programs are easier to understand and maintain
- Avoids accidental modifications
- Supports functional programming
- Safer in multi-threaded environments

### 1.2 var - Mutable Variables

Variables defined with `var` can be reassigned.

```scala
var counter: Int = 0
counter = 1     // OK
counter = 2     // OK

var message = "Hello"
message = "Hi"  // OK

// But the type cannot be changed
// message = 123  // Error! Type mismatch
```

**When to use var?**
```scala
// Counter
var count = 0
for (i <- 1 to 10) {
  count += i
}

// Accumulator
var sum = 0.0
val numbers = List(1.5, 2.5, 3.5)
numbers.foreach(n => sum += n)

// State management
var isRunning = true
while (isRunning) {
  // under some condition
  isRunning = false
}
```

**Best practices:**
```scala
// ❌ Bad practice
var result = 0
for (i <- 1 to 10) {
  result += i
}

// ✅ Better practice (functional style)
val result = (1 to 10).sum

// ❌ Bad practice
var list = List(1, 2, 3)
list = list :+ 4

// ✅ Better practice
val list1 = List(1, 2, 3)
val list2 = list1 :+ 4  // creates a new list
```

### 1.3 lazy val - Lazy Initialization

`lazy val` is only initialized on first access, suitable for expensive computations.

```scala
lazy val expensiveComputation: Int = {
  println("Performing expensive computation...")
  Thread.sleep(2000)  // simulate a time-consuming operation
  42
}

println("Definition complete")
// Output: Definition complete

println(expensiveComputation)
// Output: Performing expensive computation...
//         42

println(expensiveComputation)
// Output: 42 (not computed again)
```

**Practical usage examples:**
```scala
// Database connection (created only when needed)
lazy val dbConnection = {
  println("Establishing database connection...")
  // create and return connection
  createConnection()
}

// Config file loading
lazy val config = {
  println("Reading config file...")
  readConfigFile("config.json")
}

// Large dataset loading
lazy val largeDataset = {
  println("Loading large dataset...")
  loadDataFromFile("large_file.csv")
}
```

### 1.4 Constant Definitions

Scala has no dedicated `const` keyword, but you can use `val` with naming conventions:

```scala
// Constants are typically named in uppercase
object Constants {
  val PI: Double = 3.14159265359
  val E: Double = 2.71828182846
  val SPEED_OF_LIGHT: Int = 299792458  // m/s
  
  val MAX_RETRY: Int = 3
  val TIMEOUT_MS: Long = 5000
}

// Usage
println(Constants.PI)
```

---

## 2. Basic Data Types

All types in Scala are objects; there are no primitive types.

### 2.1 Numeric Types

```scala
// Integer types
val byteVal: Byte = 127           // 8-bit, -128 to 127
val shortVal: Short = 32767       // 16-bit, -32768 to 32767
val intVal: Int = 2147483647      // 32-bit
val longVal: Long = 9223372036854775807L  // 64-bit, requires L suffix

// Floating-point types
val floatVal: Float = 3.14f       // 32-bit, requires f suffix
val doubleVal: Double = 3.14159   // 64-bit

// Different ways to write numeric literals
val decimal = 42
val hex = 0x2A          // hexadecimal
val binary = 0b101010   // binary literal, supported by Scala 3.3.8
val octal = 42          // Scala 3 does not support legacy octal literals such as 052

// Use underscores for readability
val million = 1_000_000
val billion = 1_000_000_000L
```

**Numeric operation examples:**
```scala
val a = 10
val b = 3

// Basic operations
val sum = a + b         // 13
val diff = a - b        // 7
val product = a * b     // 30
val quotient = a / b    // 3 (integer division)
val remainder = a % b   // 1

// Type promotion
val intVal: Int = 10
val doubleVal: Double = 3.5
val result = intVal + doubleVal  // 13.5 (Int is automatically promoted to Double)

// Type conversion
val x: Int = 10
val y: Double = x.toDouble    // 10.0
val z: String = x.toString    // "10"
val w: Long = x.toLong        // 10L
```

### 2.2 Boolean Type

```scala
val isTrue: Boolean = true
val isFalse: Boolean = false

// Boolean operations
val and = true && false   // false
val or = true || false    // true
val not = !true          // false

// Short-circuit evaluation
def expensiveCheck(): Boolean = {
  println("Running expensive check")
  true
}

val result1 = false && expensiveCheck()  // expensiveCheck() will not be called
val result2 = true || expensiveCheck()   // expensiveCheck() will not be called
```

### 2.3 Char Type

```scala
val char: Char = 'A'
val digit: Char = '5'
val unicode: Char = '\u0041'  // Unicode for 'A'

// Special characters
val newline: Char = '\n'
val tab: Char = '\t'
val backslash: Char = '\\'
val quote: Char = '\''

// Char operations
val a: Char = 'A'
val next: Char = (a + 1).toChar  // 'B'

println(a.isUpper)      // true
println(a.isLower)      // false
println(a.isDigit)      // false
println(a.toLower)      // 'a'
```

### 2.4 String Type

```scala
val str: String = "Hello, Scala"

// Strings are immutable
val s1 = "Hello"
val s2 = s1 + ", World"  // creates a new string
// s1 is still "Hello"

// Multi-line string
val multiLine = """
  This is a
  multi-line
  string
"""

val formatted = """
  |First line
  |Second line
  |Third line
  """.stripMargin  // removes whitespace before |
```

### 2.5 Unit Type

`Unit` is similar to `void` in Java, representing no meaningful return value.

```scala
def printMessage(msg: String): Unit = {
  println(msg)
}

val result: Unit = printMessage("Hello")
println(result)  // ()

// The only value of Unit
val unit: Unit = ()
```

### 2.6 Nothing and Null

**Nothing:**
```scala
// Nothing is a subtype of all types
// Used for functions that never return normally

def error(message: String): Nothing = {
  throw new RuntimeException(message)
}

def infiniteLoop(): Nothing = {
  while (true) {}
}

// Useful in the type system
val list: List[Nothing] = List()  // empty list
```

**Null:**
```scala
// Null is a subtype of all reference types
// Best to avoid; use Option instead

val nullString: String = null  // possible, but not recommended
// val nullInt: Int = null     // Error! Value types cannot be null

// ❌ Not recommended
def findUser(id: Int): String = {
  if (id > 0) "User" else null
}

// ✅ Recommended
def findUserSafe(id: Int): Option[String] = {
  if (id > 0) Some("User") else None
}
```

### 2.7 Any, AnyVal, AnyRef

```scala
// Any is the root type of all types
val any1: Any = 42
val any2: Any = "Hello"
val any3: Any = true

// AnyVal is the parent type of all value types
val anyVal: AnyVal = 42
// val anyVal2: AnyVal = "Hello"  // Error! String is not AnyVal

// AnyRef is the parent type of all reference types (equivalent to Java's Object)
val anyRef: AnyRef = "Hello"
val anyRef2: AnyRef = List(1, 2, 3)
```

**Type hierarchy:**
```
        Any
       /   \
   AnyVal  AnyRef
   /  |  \    |  \
Int Double Char String List ...
      |
   Boolean
      |
   Unit
      |
   Nothing
```

---

## 3. String Operations

### 3.1 String Interpolation

Scala provides three string interpolators:

**s interpolator (most common):**
```scala
val name = "Alice"
val age = 25

// Simple interpolation
val greeting = s"Hello, $name!"
println(greeting)  // Hello, Alice!

// Expression interpolation
val message = s"$name is $age years old"
val nextYear = s"Next year, $name will be ${age + 1}"

// Can contain any expression
val calculation = s"10 + 20 = ${10 + 20}"

// Calling methods
val upper = s"${name.toUpperCase} is shouting!"
```

**f interpolator (formatting):**
```scala
val pi = 3.14159265359

// Floating-point formatting
val formatted1 = f"Pi is approximately $pi%.2f"  // Pi is approximately 3.14
val formatted2 = f"Pi is $pi%.4f"                 // Pi is 3.1416

// Other formats
val num = 42
val hex = f"$num%x"        // 2a (hexadecimal)
val padded = f"$num%05d"   // 00042 (zero-padded to 5 digits)

// Multiple variables
val name = "Alice"
val score = 95.5
val report = f"$name%s scored $score%.1f points"
```

**raw interpolator (no escape processing):**
```scala
// Normal strings process escape characters
val normal = s"Line1\nLine2"
println(normal)
// Line1
// Line2

// raw does not process escape characters
val raw = raw"Line1\nLine2"
println(raw)  // Line1\nLine2

// Commonly used for regular expressions and file paths
val path = raw"C:\Users\Documents\file.txt"
val regex = raw"\d{3}-\d{4}"
```

**Custom interpolators:**
```scala
// Advanced topic - you can create your own interpolators
extension (sc: StringContext) {
  def json(args: Any*): String = {
    // custom logic
    sc.parts.zip(args).map { case (p, a) => p + a }.mkString
  }
}

val key = "name"
val value = "Alice"
val jsonStr = json"""{"$key": "$value"}"""
```

### 3.2 String Methods

```scala
val str = "Hello, Scala Programming"

// Length
str.length              // 24

// Case conversion
str.toLowerCase         // "hello, scala programming"
str.toUpperCase         // "HELLO, SCALA PROGRAMMING"
str.capitalize          // "Hello, scala programming"

// Checks
str.isEmpty             // false
str.nonEmpty            // true
str.startsWith("Hello") // true
str.endsWith("ing")     // true
str.contains("Scala")   // true

// Search
str.indexOf("Scala")    // 7
str.indexOf("Java")     // -1 (not found)
str.lastIndexOf("a")    // 20

// Substrings
str.substring(7)        // "Scala Programming"
str.substring(7, 12)    // "Scala"
str.take(5)             // "Hello"
str.drop(7)             // "Scala Programming"
str.takeRight(11)       // "Programming"
str.dropRight(11)       // "Hello, Scala"

// Split
str.split(" ")          // Array("Hello,", "Scala", "Programming")
str.split(",").map(_.trim)  // Array("Hello", "Scala Programming")

// Replace
str.replace("Scala", "Java")     // "Hello, Java Programming"
str.replaceAll("[aeiou]", "*")   // "H*ll*, Sc*l* Pr*gr*mm*ng"
str.replaceFirst("a", "A")       // "Hello, ScAla Programming"

// Strip whitespace
val padded = "  Hello  "
padded.trim             // "Hello"
padded.stripPrefix("  ")  // "Hello  "
padded.stripSuffix("  ")  // "  Hello"

// Repeat
"Ha" * 3                // "HaHaHa"

// Reverse
str.reverse             // "gnimmargorP alacS ,olleH"

// Comparison
"abc".compareTo("abd")  // -1 (negative means less than)
"abc" == "abc"          // true
"abc".equals("abc")     // true
"abc".equalsIgnoreCase("ABC")  // true
```

### 3.3 Multi-line String Handling

```scala
// Triple-quoted string
val poem = """
  |Roses are red,
  |Violets are blue,
  |Scala is awesome,
  |And so are you!
  """.stripMargin

println(poem)

// Custom margin character
val code = """
  #def hello(): Unit = {
  #  println("Hello")
  #}
  """.stripMargin('#')

// Preserve indentation
val indented = """
    First line
    Second line
      Indented line
  """.trim

// Strip leading/trailing whitespace from each line
val lines = """
  Line 1
  Line 2
  Line 3
""".split("\n").map(_.trim).mkString("\n")
```

### 3.4 String and Other Type Conversions

```scala
// String to number
val numStr = "42"
val num = numStr.toInt           // 42
val double = "3.14".toDouble     // 3.14
val long = "1000000".toLong      // 1000000

// Safe conversion (with error handling)
def safeToInt(str: String): Option[Int] = {
  try {
    Some(str.toInt)
  } catch {
    case _: NumberFormatException => None
  }
}

safeToInt("42")      // Some(42)
safeToInt("abc")     // None

// Using Try
import scala.util.{Try, Success, Failure}

Try("42".toInt)      // Success(42)
Try("abc".toInt)     // Failure(NumberFormatException)

// Number to string
val n = 42
n.toString           // "42"
s"$n"               // "42"

// Other conversions
val bool = "true".toBoolean     // true
val list = "1,2,3".split(",").map(_.toInt).toList  // List(1, 2, 3)
```

### 3.5 StringBuilder

For heavy string manipulation, using `StringBuilder` is more efficient:

```scala
val sb = new StringBuilder

sb.append("Hello")
sb.append(" ")
sb.append("World")

val result = sb.toString  // "Hello World"

// Chained calls
val result2 = new StringBuilder()
  .append("Line 1")
  .append("\n")
  .append("Line 2")
  .toString

// Building large strings
def buildLargeString(n: Int): String = {
  val sb = new StringBuilder
  for (i <- 1 to n) {
    sb.append(s"Item $i\n")
  }
  sb.toString
}
```

---

## 4. Operators

### 4.1 Arithmetic Operators

```scala
val a = 10
val b = 3

// Basic operations
a + b    // 13 (addition)
a - b    // 7  (subtraction)
a * b    // 30 (multiplication)
a / b    // 3  (division, integer division)
a % b    // 1  (remainder)

// Floating-point division
val x = 10.0
val y = 3.0
x / y    // 3.3333...

// Compound assignment operators (only for var)
var count = 10
count += 5   // count = 15
count -= 3   // count = 12
count *= 2   // count = 24
count /= 4   // count = 6
count %= 4   // count = 2

// Unary operators
+a       // 10 (unary plus)
-a       // -10 (unary minus)
```

### 4.2 Relational Operators

```scala
val a = 10
val b = 20

a == b   // false (equal)
a != b   // true  (not equal)
a < b    // true  (less than)
a <= b   // true  (less than or equal)
a > b    // false (greater than)
a >= b   // false (greater than or equal)

// String comparison
"abc" == "abc"     // true
"abc" < "abd"      // true (lexicographic order)

// Reference comparison (rarely used)
val s1 = new String("hello")
val s2 = new String("hello")
s1 == s2           // true (value equality)
s1 eq s2           // false (different references)
```

### 4.3 Logical Operators

```scala
val t = true
val f = false

// AND
t && t   // true
t && f   // false
f && t   // false
f && f   // false

// OR
t || t   // true
t || f   // true
f || t   // true
f || f   // false

// NOT
!t       // false
!f       // true

// Short-circuit evaluation
def expensive(): Boolean = {
  println("Running expensive operation")
  true
}

false && expensive()  // expensive() will not be called
true || expensive()   // expensive() will not be called

// Bitwise operators
val x = 5   // 0101
val y = 3   // 0011

x & y    // 1  (0001, AND)
x | y    // 7  (0111, OR)
x ^ y    // 6  (0110, XOR)
~x       // -6 (1010, NOT)
x << 1   // 10 (1010, left shift)
x >> 1   // 2  (0010, right shift)
x >>> 1  // 2  (0010, unsigned right shift)
```

### 4.4 Operator Precedence

```scala
// From highest to lowest
// 1. All other special characters
// 2. * / %
// 3. + -
// 4. :
// 5. = !
// 6. < >
// 7. &
// 8. ^
// 9. |
// 10. All letters
// 11. All assignment operators

// Examples
val result1 = 2 + 3 * 4      // 14 (not 20)
val result2 = (2 + 3) * 4    // 20
val result3 = 10 - 5 - 2     // 3 (left-associative)
```

### 4.5 Operators Are Methods

Operators in Scala are actually method calls:

```scala
val a = 1
val b = 2

// These two are equivalent
a + b
a.+(b)

// These two are equivalent
a < b
a.<(b)

// String concatenation
"Hello" + " World"
"Hello".+(" World")

// Custom operators
class Vector2D(val x: Double, val y: Double) {
  def +(other: Vector2D): Vector2D = {
    new Vector2D(x + other.x, y + other.y)
  }
  
  def *(scalar: Double): Vector2D = {
    new Vector2D(x * scalar, y * scalar)
  }
  
  override def toString = s"Vector2D($x, $y)"
}

val v1 = new Vector2D(1, 2)
val v2 = new Vector2D(3, 4)
val v3 = v1 + v2        // Vector2D(4.0, 6.0)
val v4 = v1 * 2.0       // Vector2D(2.0, 4.0)
```

---

## 5. Conditionals

### 5.1 if-else Expressions

Scala's if-else is an expression that returns a value:

```scala
val age = 18

// Basic if-else
val status = if (age >= 18) "Adult" else "Minor"
println(status)  // Adult

// if-else can have different types
val result = if (age >= 18) "Adult" else 0
// result's type is Any (common supertype of String and Int)

// Single-line if (omitting else is not recommended)
if (age >= 18) println("Adult")

// Multi-line if-else
val grade = 85
val level = if (grade >= 90) {
  println("Excellent!")
  "A"
} else if (grade >= 80) {
  println("Good!")
  "B"
} else if (grade >= 70) {
  println("Passing")
  "C"
} else {
  println("Failing")
  "F"
}
```

### 5.2 Nested Conditionals

```scala
val score = 85
val attendance = 90

val finalGrade = if (score >= 60) {
  if (attendance >= 80) {
    "Pass (good attendance)"
  } else {
    "Pass (attendance needs improvement)"
  }
} else {
  if (attendance >= 80) {
    "Fail (retake recommended)"
  } else {
    "Fail (insufficient attendance)"
  }
}
```

### 5.3 Conditional Best Practices

```scala
// ❌ Avoid: using if without using the return value
var result = ""
if (condition) {
  result = "yes"
} else {
  result = "no"
}

// ✅ Recommended: use if as an expression
val result = if (condition) "yes" else "no"

// ❌ Avoid: complex nested conditionals
if (a) {
  if (b) {
    if (c) {
      // ...
    }
  }
}

// ✅ Recommended: use match or early return
def process(a: Boolean, b: Boolean, c: Boolean): String = {
  if (!a) return "a is false"
  if (!b) return "b is false"
  if (!c) return "c is false"
  "all true"
}

// ✅ Or use pattern matching
(a, b, c) match {
  case (true, true, true) => "all true"
  case (false, _, _) => "a is false"
  case (_, false, _) => "b is false"
  case (_, _, false) => "c is false"
}
```

---

## 6. Loops

### 6.1 for Loops

**Basic syntax:**
```scala
// Iterate over a range
for (i <- 1 to 5) {
  println(i)
}
// Output: 1 2 3 4 5

// until (excludes the end value)
for (i <- 1 until 5) {
  println(i)
}
// Output: 1 2 3 4

// Specify step
for (i <- 1 to 10 by 2) {
  println(i)
}
// Output: 1 3 5 7 9

// Reverse order
for (i <- 10 to 1 by -1) {
  println(i)
}
// Output: 10 9 8 ... 1
```

**Iterating over collections:**
```scala
val fruits = List("apple", "banana", "cherry")

for (fruit <- fruits) {
  println(fruit)
}

// With index
for (i <- fruits.indices) {
  println(s"$i: ${fruits(i)}")
}

// Using zipWithIndex
for ((fruit, index) <- fruits.zipWithIndex) {
  println(s"$index: $fruit")
}
```

**Multiple generators:**
```scala
// Nested loops
for (i <- 1 to 3; j <- 1 to 2) {
  println(s"i=$i, j=$j")
}
// Output:
// i=1, j=1
// i=1, j=2
// i=2, j=1
// i=2, j=2
// i=3, j=1
// i=3, j=2

// Multiplication table
for (i <- 1 to 9; j <- 1 to 9) {
  print(f"${i * j}%4d")
  if (j == 9) println()
}
```

**for with guards:**
```scala
// Process only even numbers
for (i <- 1 to 10 if i % 2 == 0) {
  println(i)
}
// Output: 2 4 6 8 10

// Multiple conditions
for {
  i <- 1 to 100
  if i % 3 == 0
  if i % 5 == 0
} {
  println(i)
}
// Output: 15 30 45 60 75 90

// Complex filtering
val numbers = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
for {
  n <- numbers
  if n % 2 == 0
  if n > 5
} {
  println(s"Even and greater than 5: $n")
}
```

### 6.2 for Comprehensions

for comprehensions produce a new collection:

```scala
// Basic usage
val doubled = for (i <- 1 to 5) yield i * 2
// doubled: IndexedSeq[Int] = Vector(2, 4, 6, 8, 10)

// Transform a collection
val fruits = List("apple", "banana", "cherry")
val upperFruits = for (fruit <- fruits) yield fruit.toUpperCase
// upperFruits: List[String] = List(APPLE, BANANA, CHERRY)

// With condition
val evenDoubled = for (i <- 1 to 10 if i % 2 == 0) yield i * 2
// evenDoubled: IndexedSeq[Int] = Vector(4, 8, 12, 16, 20)

// Multiple generators
val pairs = for {
  x <- 1 to 3
  y <- 1 to 2
} yield (x, y)
// pairs: IndexedSeq[(Int, Int)] = Vector((1,1), (1,2), (2,1), (2,2), (3,1), (3,2))

// Complex transformation
case class Person(name: String, age: Int)
val people = List(
  Person("Alice", 25),
  Person("Bob", 30),
  Person("Charlie", 20)
)

val names = for {
  person <- people
  if person.age >= 25
} yield person.name
// names: List[String] = List(Alice, Bob)
```

**for comprehensions vs map/filter:**
```scala
val numbers = List(1, 2, 3, 4, 5)

// Using for comprehension
val result1 = for {
  n <- numbers
  if n % 2 == 0
} yield n * 2

// Using filter and map
val result2 = numbers.filter(_ % 2 == 0).map(_ * 2)

// Both produce the same result: List(4, 8)
```

### 6.3 while Loops

```scala
var i = 0
while (i < 5) {
  println(i)
  i += 1
}

// Practical example: read input until a specific condition
import scala.io.StdIn

var input = ""
while (input != "quit") {
  print("Enter command (quit to exit): ")
  input = StdIn.readLine()
  println(s"You entered: $input")
}
```

### 6.4 Repeating a Body Before Testing

```scala
// Scala 3 removed do-while syntax. Put the body in the while condition when
// the body must execute before the condition is tested.
var count = 0
while {
  println(s"Count: $count")
  count += 1
  count < 5
} do ()

// Executes at least once
var x = 10
while {
  println("Executed once")
  x < 5
} do ()  // condition is false, but the condition block executed once
```

### 6.5 Loop Control

Scala has no `break` or `continue`, but there are alternatives:

**Using return (inside a method):**
```scala
def findFirst(numbers: List[Int], target: Int): Option[Int] = {
  for (i <- numbers.indices) {
    if (numbers(i) == target) {
      return Some(i)  // early return
    }
  }
  None
}
```

**Using scala.util.control.Breaks:**
```scala
import scala.util.control.Breaks.*

// break example
breakable {
  for (i <- 1 to 10) {
    println(i)
    if (i == 5) break  // exit the loop
  }
}

// continue example (using nested breakable)
for (i <- 1 to 5) {
  breakable {
    if (i == 3) break  // equivalent to continue
    println(i)
  }
}
// Output: 1 2 4 5
```

**Functional alternatives (recommended):**
```scala
// Use takeWhile instead of break
val numbers = (1 to 100).takeWhile(_ < 50)

// Use filter instead of continue
(1 to 10).filter(_ != 5).foreach(println)

// Use find instead of early return
val firstEven = (1 to 10).find(_ % 2 == 0)  // Some(2)
```

---

## 7. Expressions vs Statements

### 7.1 Understanding the Difference

**Expression:** evaluates and returns a result
**Statement:** performs an action but does not return a meaningful value

```scala
// Expression examples
val x = 10          // 10 is an expression
val y = x + 5       // x + 5 is an expression
val z = if (x > 5) "big" else "small"  // if-else is an expression

// In Scala, almost everything is an expression
val result = {
  val a = 10
  val b = 20
  a + b  // the value of the last expression is the value of the block
}
// result = 30
```

### 7.2 Code Blocks

```scala
// A block is an expression
val area = {
  val width = 10
  val height = 20
  width * height  // returns 200
}

// Variables in a block are local
{
  val temp = 100
  println(temp)
}
// println(temp)  // Error! temp does not exist here

// Complex computation
val discount = {
  val price = 1000
  val customerType = "VIP"
  
  if (customerType == "VIP") {
    price * 0.8
  } else if (customerType == "Member") {
    price * 0.9
  } else {
    price
  }
}
```

### 7.3 Unit Type Statements

```scala
// println returns Unit
val result: Unit = println("Hello")

// Assignment returns Unit
var x = 10
val assignment: Unit = (x = 20)

// while loop returns Unit
val loop: Unit = while (x < 30) {
  x += 1
}
```

---

## 8. Code Blocks

### 8.1 Block Syntax

```scala
// Basic block
{
  val x = 10
  val y = 20
  x + y
}

// Block as an argument
List(1, 2, 3).map { x =>
  val doubled = x * 2
  val squared = doubled * doubled
  squared
}

// Function definition using a block
def calculate(a: Int, b: Int): Int = {
  val sum = a + b
  val product = a * b
  sum + product
}
```

### 8.2 Block Scope

```scala
val outer = "outer"

{
  val inner = "inner"
  println(outer)  // OK
  println(inner)  // OK
}

// println(inner)  // Error!

// Variable shadowing
val x = 10
{
  val x = 20  // new x, shadows the outer x
  println(x)  // 20
}
println(x)    // 10
```

### 8.3 Practical Uses of Blocks

```scala
// Initializing a complex object
val config = {
  val env = sys.env.getOrElse("ENV", "dev")
  val port = if (env == "prod") 8080 else 3000
  val host = if (env == "prod") "0.0.0.0" else "localhost"
  
  Map(
    "env" -> env,
    "port" -> port,
    "host" -> host
  )
}

// Conditional initialization
val logger = {
  val debug = true
  if (debug) {
    println("Debug mode enabled")
    new DebugLogger()
  } else {
    new ProductionLogger()
  }
}

// Resource management
val data = {
  val file = scala.io.Source.fromFile("data.txt")
  try {
    file.getLines().toList
  } finally {
    file.close()
  }
}
```

---

## 9. Practice Exercises

### Exercise 1: Temperature Converter

```scala
@main def temperatureConverter(): Unit = {
  def celsiusToFahrenheit(c: Double): Double = {
    c * 9 / 5 + 32
  }
  
  def fahrenheitToCelsius(f: Double): Double = {
    (f - 32) * 5 / 9
  }
  
  // Test
  val tempC = 25.0
  val tempF = celsiusToFahrenheit(tempC)
  println(f"$tempC%.1f°C = $tempF%.1f°F")
  
  val tempF2 = 77.0
  val tempC2 = fahrenheitToCelsius(tempF2)
  println(f"$tempF2%.1f°F = $tempC2%.1f°C")
}
```

**Expected output:**
```
25.0°C = 77.0°F
77.0°F = 25.0°C
```

### Exercise 2: Grade Classification System

```scala
@main def gradeSystem(): Unit = {
  def getGrade(score: Int): String = {
    if (score < 0 || score > 100) {
      "Invalid score"
    } else if (score >= 90) {
      "A"
    } else if (score >= 80) {
      "B"
    } else if (score >= 70) {
      "C"
    } else if (score >= 60) {
      "D"
    } else {
      "F"
    }
  }
  
  def getComment(grade: String): String = grade match {
    case "A" => "Excellent!"
    case "B" => "Good!"
    case "C" => "Passing"
    case "D" => "Needs improvement"
    case "F" => "Failing"
    case _ => ""
  }
  
  // Test with multiple scores
  val scores = List(95, 85, 75, 65, 55, 105)
  
  for (score <- scores) {
    val grade = getGrade(score)
    val comment = getComment(grade)
    println(f"Score: $score%3d => Grade: $grade ($comment)")
  }
}
```

### Exercise 3: Simple Calculator

```scala
@main def simpleCalculator(): Unit = {
  def calculate(a: Double, b: Double, operator: String): Option[Double] = {
    operator match {
      case "+" => Some(a + b)
      case "-" => Some(a - b)
      case "*" => Some(a * b)
      case "/" if b != 0 => Some(a / b)
      case "/" => None  // division by zero
      case _ => None    // unsupported operator
    }
  }
  
  // Test
  val tests = List(
    (10.0, 5.0, "+"),
    (10.0, 5.0, "-"),
    (10.0, 5.0, "*"),
    (10.0, 5.0, "/"),
    (10.0, 0.0, "/"),
    (10.0, 5.0, "%")
  )
  
  for ((a, b, op) <- tests) {
    calculate(a, b, op) match {
      case Some(result) => println(f"$a%.1f $op $b%.1f = $result%.2f")
      case None => println(f"$a%.1f $op $b%.1f = Error!")
    }
  }
}
```

### Exercise 4: FizzBuzz

The classic FizzBuzz problem:

```scala
@main def runFizzBuzz(): Unit = {
  def fizzBuzz(n: Int): String = {
    if (n % 15 == 0) "FizzBuzz"
    else if (n % 3 == 0) "Fizz"
    else if (n % 5 == 0) "Buzz"
    else n.toString
  }
  
  // Method 1: using a for loop
  println("=== Method 1 ===")
  for (i <- 1 to 30) {
    println(s"$i: ${fizzBuzz(i)}")
  }
  
  // Method 2: using a for comprehension
  println("\n=== Method 2 ===")
  val results = for (i <- 1 to 30) yield fizzBuzz(i)
  results.zipWithIndex.foreach { case (result, index) =>
    println(s"${index + 1}: $result")
  }
}
```

### Exercise 5: Prime Number Check

```scala
@main def primeChecker(): Unit = {
  def isPrime(n: Int): Boolean = {
    if (n <= 1) {
      false
    } else if (n == 2) {
      true
    } else if (n % 2 == 0) {
      false
    } else {
      // Only need to check up to sqrt(n)
      val sqrt = math.sqrt(n).toInt
      !(3 to sqrt by 2).exists(i => n % i == 0)
    }
  }
  
  // Find all prime numbers from 1 to 100
  val primes = for {
    n <- 1 to 100
    if isPrime(n)
  } yield n
  
  println(s"Primes from 1 to 100: ${primes.mkString(", ")}")
  println(s"Total: ${primes.length} primes")
}
```

### Exercise 6: String Processing

```scala
@main def stringProcessor(): Unit = {
  // Count characters in a string
  def charCount(str: String): Map[Char, Int] = {
    str.groupBy(identity).view.mapValues(_.length).toMap
  }
  
  // Check if a string is a palindrome
  def isPalindrome(str: String): Boolean = {
    val cleaned = str.toLowerCase.replaceAll("[^a-z0-9]", "")
    cleaned == cleaned.reverse
  }
  
  // Word count
  def wordCount(str: String): Int = {
    str.split("\\s+").filter(_.nonEmpty).length
  }
  
  // Test
  val text = "Hello, World! This is a test."
  
  println(s"Original text: $text")
  println(s"Character count: ${charCount(text.toLowerCase.replaceAll("[^a-z]", ""))}")
  println(s"Word count: ${wordCount(text)}")
  
  val palindromes = List("A man a plan a canal Panama", "Hello", "racecar")
  palindromes.foreach { str =>
    println(s"'$str' is a palindrome? ${isPalindrome(str)}")
  }
}
```

### Exercise 7: Sequence Generation

```scala
@main def sequenceGenerator(): Unit = {
  // Fibonacci sequence
  def fibonacci(n: Int): List[Int] = {
    def fib(count: Int, a: Int, b: Int, acc: List[Int]): List[Int] = {
      if (count == 0) acc.reverse
      else fib(count - 1, b, a + b, a :: acc)
    }
    fib(n, 0, 1, List())
  }
  
  // Factorial
  def factorial(n: Int): BigInt = {
    if (n <= 1) 1
    else (1 to n).map(BigInt(_)).product
  }
  
  // Perfect squares
  def perfectSquares(limit: Int): List[Int] = {
    (1 to limit).map(x => x * x).toList
  }
  
  // Test
  println(s"First 10 Fibonacci numbers: ${fibonacci(10).mkString(", ")}")
  println(s"Factorial of 10: ${factorial(10)}")
  println(s"First 10 perfect squares: ${perfectSquares(10).mkString(", ")}")
}
```

---

## 10. Key Takeaways

### Variable Declaration
- Prefer `val` (immutable)
- Use `var` (mutable) only when necessary
- `lazy val` for deferred initialization

### Data Types
- All types are objects
- Type inference keeps code concise
- Avoid `null`; use `Option` instead

### Strings
- Use the s interpolator for string interpolation
- Use the f interpolator for formatting
- Use the raw interpolator to suppress escape processing

### Conditionals and Loops
- if-else is an expression that returns a value
- for comprehensions produce new collections
- Prefer functional methods over imperative loops

### Expressions Over Statements
- Scala encourages the use of expressions
- A code block returns the value of its last expression
- This makes code more concise and composable

---

## Next Steps

After completing Part 2, you have mastered:
- ✅ Scala's basic syntax
- ✅ Variables and data types
- ✅ String operations
- ✅ Control flow

**Up next:**
- [Part 3: Functions and Methods](scala_part3_functions.md) - Deep dive into functional programming
- [Part 4: Object-Oriented Programming](scala_part4_oop.md)

Ready to continue?

---

> [📚 Table of Contents](../../README.md) | [« Prev: Introduction](scala_part1_introduction.md) | [Next: Functions »](scala_part3_functions.md)
