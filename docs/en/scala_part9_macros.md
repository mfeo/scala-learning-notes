# Scala Tutorial - Macros

> [📚 Table of Contents](../../README.md) | [« Prev: Contextual Abstractions and Type Classes](scala_part8_advanced_topics.md)

---

## Table of Contents
1. [Macro Overview](#1-macro-overview)
2. [Scala 3 Macro Model](#2-scala-3-macro-model)
3. [Compile-Time Computation](#3-compile-time-computation)
4. [Scala 3 Inline](#4-scala-3-inline)
5. [Quotes and Splicing](#5-quotes-and-splicing)
6. [Macro Implementation Examples](#6-macro-implementation-examples)
7. [Reflection and Type Operations](#7-reflection-and-type-operations)
8. [Practical Macro Examples](#8-practical-macro-examples)
9. [Debugging and Testing](#9-debugging-and-testing)
10. [Best Practices](#10-best-practices)

---

## 1. Macro Overview

### 1.1 What Are Macros?

```scala
// Macros (Macro) are code that executes at compile time
// They can inspect, generate, and transform code

// Ordinary function: executes at runtime
def square(x: Int): Int = x * x

// Macro: executes at compile time, generates code
// The compiler replaces macro calls with the generated code

// Example: debug macro
debug(x + y)
// Expanded at compile time to:
// println(s"x + y = ${x + y}")
```

### 1.2 Why Use Macros?

```scala
// 1. Compile-time validation
// Check if a SQL string is valid at compile time
sql"SELECT * FROM users WHERE id = $id"

// 2. Performance optimization
// Eliminate runtime reflection overhead
case class Person(name: String, age: Int)
// Automatically generate efficient serialization code

// 3. Code generation
// Automatically generate boilerplate code
@JsonCodec
case class User(id: Int, name: String)
// Automatically generate JSON encoder/decoder

// 4. DSL implementation
// Create domain-specific languages
html {
  head {
    title("My Page")
  }
  body {
    h1("Hello")
  }
}

// 5. Type-level programming
// Perform computations at the type level
```

### 1.3 Limitations of Macros

```scala
// ⚠️ Drawbacks of macros:

// 1. Complexity
// - Requires understanding compiler internals
// - Difficult to debug
// - Steep learning curve

// 2. Compilation time
// - Increases compilation time
// - May impact development efficiency

// 3. Tooling support
// - Limited IDE support
// - Error messages may be unclear

// 4. Version compatibility
// - Scala 2 and Scala 3 macros are incompatible
// - Must be maintained separately

// Therefore: only use macros when necessary!
```

---

## 2. Scala 3 Macro Model

Scala 3 macros are based on `inline`, quotes, splices, and typed expressions. They are incompatible
with the earlier reflection-based macro system, so this chapter shows only Scala 3.3.8 syntax.

```scala
// 1. Inline
inline def square(x: Int): Int = x * x

// 2. Compile-time operations
import scala.quoted.*

inline def debug[T](inline x: T): T = ${debugImpl('x)}

def debugImpl[T: Type](x: Expr[T])(using Quotes): Expr[T] = {
  import quotes.reflect.*
  
  val valueExpr = x
  val valueString = x.show
  
  '{
    val value = $valueExpr
    println(s"$valueString = " + value)
    value
  }
}

// Usage
val result = debug(10 + 20)  // Output: "10 + 20 = 30"

// Advantages:
// - Type safe
// - Cleaner API
// - Macro definitions and call sites can share a project, but a macro cannot
//   be called from the same source file in which it is defined
```

---

## 3. Compile-Time Computation

### 3.1 Inline Basics

```scala
// The inline keyword tells the compiler to expand code at the call site

// Ordinary function
def add(x: Int, y: Int): Int = x + y

val result1 = add(1, 2)
// Generated:
// val result1 = add(1, 2)  // runtime call

// Inline function
inline def addInline(x: Int, y: Int): Int = x + y

val result2 = addInline(1, 2)
// Generated:
// val result2 = 1 + 2  // expanded at compile time
// Further optimized:
// val result2 = 3  // constant folding

// Advantages of inline:
// - Eliminates function call overhead
// - Enables further optimization
// - Can be used with macros
```

### 3.2 Inline Match

```scala
// inline match evaluates at compile time

inline def selectColor(inline choice: Int): String = inline choice match {
  case 1 => "red"
  case 2 => "green"
  case 3 => "blue"
  case _ => "unknown"
}

val color = selectColor(2)
// Expanded at compile time to:
// val color = "green"

// Error checking
// val badColor = selectColor(x)  // Compile error: must be a constant!

// Practical example: compile-time configuration
inline def getConfig(inline key: String): String = inline key match {
  case "env" => "production"
  case "host" => "localhost"
  case "port" => "8080"
  case _ => compiletime.error("Unknown config key")
}

val env = getConfig("env")  // "production"
// val bad = getConfig("invalid")  // Compile error!
```

### 3.3 Compiletime Operations

```scala
import scala.compiletime.*

// 1. Compile-time error
inline def requirePositive(inline x: Int): Int = {
  inline if (x <= 0) {
    error("Value must be positive")
  }
  x
}

val good = requirePositive(10)  // OK
// val bad = requirePositive(-5)  // Compile error!

// 2. Type operations
import scala.compiletime.erasedValue

inline def typeString[T]: String = {
  inline erasedValue[T] match {
    case _: Int => "Integer"
    case _: String => "Text"
    case _ => "Unknown"
  }
}

// 3. Tuple operations
type MyTuple = (Int, String, Boolean)

inline def tupleSize[T <: Tuple]: Int = {
  constValue[Tuple.Size[T]]
}

val size = tupleSize[MyTuple]  // 3

// 4. Sum types through the compiler-provided Mirror
import scala.deriving.Mirror

enum Color:
  case Red, Green, Blue

inline def enumSize[E](using m: Mirror.SumOf[E]): Int =
  constValue[Tuple.Size[m.MirroredElemTypes]]

val colorCount = enumSize[Color] // 3
```

---

## 4. Scala 3 Inline

### 4.1 Basic Inline

```scala
// inline parameters
inline def power(x: Double, inline n: Int): Double = {
  inline if (n == 0) 1.0
  else inline if (n == 1) x
  else x * power(x, n - 1)
}

val result = power(2.0, 3)
// Expanded at compile time to:
// val result = 2.0 * 2.0 * 2.0

// inline conditional
inline def max(inline a: Int, inline b: Int): Int = {
  inline if (a > b) a else b
}

val m = max(10, 20)
// Expanded to: val m = 20
```

### 4.2 Transparent Inline

```scala
// transparent inline preserves precise types

// Ordinary inline: fixed return type
inline def identify[T](x: T): T = x

val x1 = identify(42)    // x1: Int
val x2 = identify("hi")  // x2: String

// transparent inline: returns the most specific type
transparent inline def select(inline choice: Boolean): Any = {
  inline if (choice) 42 else "text"
}

val y1 = select(true)   // y1: Int (not Any!)
val y2 = select(false)  // y2: String

// Practical example: type-level selection
transparent inline def chooseType[A, B](inline useA: Boolean): Any = {
  inline if (useA) 
    summon[A]
  else 
    summon[B]
}
```

---

## 5. Quotes and Splicing

### 5.1 Quotes

```scala
import scala.quoted.*

// Quotes '{...} represent code fragments
// Expr[T] represents code with type T

def exampleQuote(using Quotes): Expr[Int] = {
  '{42}  // Expr[Int]
}

def exampleQuote2(using Quotes): Expr[String] = {
  '{"hello"}  // Expr[String]
}

// Composing expressions
def addExprs(a: Expr[Int], b: Expr[Int])(using Quotes): Expr[Int] = {
  '{$a + $b}  // splice $a, $b into the quote
}

// Example: generating a list
def makeList(using Quotes): Expr[List[Int]] = {
  '{List(1, 2, 3, 4, 5)}
}
```

### 5.2 Splicing

```scala
import scala.quoted.*

// $expr splices an expression into a quote

def doubleExpr(x: Expr[Int])(using Quotes): Expr[Int] = {
  '{$x * 2}
}

// Used in a macro
inline def double(x: Int): Int = ${doubleImpl('x)}

def doubleImpl(x: Expr[Int])(using Quotes): Expr[Int] = {
  '{$x * 2}
}

val result = double(21)  // 42

// Multiple splices
def sumExprs(exprs: Seq[Expr[Int]])(using Quotes): Expr[Int] = {
  exprs.reduce((a, b) => '{$a + $b})
}

// Conditional splice
def conditionalExpr(
  cond: Expr[Boolean], 
  thenBranch: Expr[Int], 
  elseBranch: Expr[Int]
)(using Quotes): Expr[Int] = {
  '{if ($cond) $thenBranch else $elseBranch}
}
```

### 5.3 Pattern Matching on Quotes

```scala
import scala.quoted.*

// Match code structure
def analyzeExpr(expr: Expr[Int])(using Quotes): String = {
  expr match {
    case '{42} => "literal 42"
    case '{($x: Int) + ($y: Int)} => s"addition: $x + $y"
    case '{($x: Int) * ($y: Int)} => s"multiplication: $x * $y"
    case _ => "other expression"
  }
}

// Extract sub-expressions
def extractAddition(expr: Expr[Int])(using Quotes): Option[(Expr[Int], Expr[Int])] = {
  expr match {
    case '{($a: Int) + ($b: Int)} => Some((a, b))
    case _ => None
  }
}

// Recursive analysis
def simplify(expr: Expr[Int])(using Quotes): Expr[Int] = {
  expr match {
    case '{0 + ($x: Int)} => x
    case '{($x: Int) + 0} => x
    case '{0 * ($x: Int)} => '{0}
    case '{($x: Int) * 0} => '{0}
    case '{1 * ($x: Int)} => x
    case '{($x: Int) * 1} => x
    case _ => expr
  }
}
```

---

## 6. Macro Implementation Examples

### 6.1 Debug Macro

```scala
import scala.quoted.*

// Display expression and its value
inline def debug[T](inline expr: T): T = ${debugImpl('expr)}

def debugImpl[T: Type](expr: Expr[T])(using Quotes): Expr[T] = {
  import quotes.reflect.*
  
  // Get the string representation of the expression
  val exprStr = Expr(expr.show)
  
  // Generate code
  '{
    val value = $expr
    println(s"${$exprStr} = " + value)
    value
  }
}

// Usage
val x = 10
val y = 20
val result = debug(x + y)
// Output: "x + y = 30"
// result = 30
```

### 6.2 Assert Macro

```scala
import scala.quoted.*

// Compile-time checked assert
inline def staticAssert(inline condition: Boolean, inline msg: String): Unit = {
  inline if (!condition) {
    scala.compiletime.error(msg)
  }
}

// Runtime assert with detailed information
inline def assert(inline condition: Boolean): Unit = {
  ${assertImpl('condition)}
}

def assertImpl(condition: Expr[Boolean])(using Quotes): Expr[Unit] = {
  import quotes.reflect.*
  
  val condStr = Expr(condition.show)
  
  '{
    if (!$condition) {
      throw new AssertionError(s"Assertion failed: ${$condStr}")
    }
  }
}

// Usage
val x = 5
assert(x > 0)  // OK
// assert(x > 10)  // Throws: Assertion failed: x > 10

// Compile-time check
staticAssert(1 + 1 == 2, "Math broken!")  // OK
// staticAssert(1 + 1 == 3, "Math broken!")  // Compile error!
```

### 6.3 Enum Values Macro

```scala
import scala.quoted.*
import scala.compiletime.*

// Enumerate all values
enum Color:
  case Red, Green, Blue

// Get all enum values
inline def enumValues[E](using m: deriving.Mirror.SumOf[E]): List[E] = {
  ${enumValuesImpl[E]}
}

def enumValuesImpl[E: Type](using Quotes): Expr[List[E]] = {
  import quotes.reflect.*
  
  // Use reflection to get all cases
  val tpe = TypeRepr.of[E]
  val sym = tpe.typeSymbol
  
  if (!sym.flags.is(Flags.Enum)) {
    report.error(s"${sym.name} is not an enum")
    return '{Nil}
  }
  
  val children = sym.children.filter(_.flags.is(Flags.Case))
  
  val values = children.map { child =>
    Ref(child).asExprOf[E]
  }
  
  Expr.ofList(values)
}

// Usage
val colors = enumValues[Color]
// List(Red, Green, Blue)
```

### 6.4 Show Macro

```scala
import scala.quoted.*

// Automatically generate Show instances
trait Show[T] {
  def show(value: T): String
}

object Show {
  inline def derived[T]: Show[T] = ${derivedImpl[T]}
  
  def derivedImpl[T: Type](using Quotes): Expr[Show[T]] = {
    import quotes.reflect.*
    
    val tpe = TypeRepr.of[T]
    val typeName = tpe.typeSymbol.name
    
    tpe.asType match {
      case '[t] =>
        '{
          new Show[t] {
            def show(value: t): String = {
              // Simplified version: just display the type name
              s"$typeName(...)"
            }
          }
        }
    }
  }
}

// Usage
case class Person(name: String, age: Int) derives Show

val p = Person("Alice", 25)
val shower = summon[Show[Person]]
println(shower.show(p))  // "Person(...)"
```

---

## 7. Reflection and Type Operations

### 7.1 Type Reflection

```scala
import scala.quoted.*

def inspectType[T: Type](using Quotes): Unit = {
  import quotes.reflect.*
  
  val tpe = TypeRepr.of[T]
  
  println(s"Type: ${tpe.show}")
  println(s"Symbol: ${tpe.typeSymbol.name}")
  println(s"Is case class: ${tpe.typeSymbol.flags.is(Flags.Case)}")
  
  // Get fields
  if (tpe.typeSymbol.flags.is(Flags.Case)) {
    val fields = tpe.typeSymbol.caseFields
    println("Fields:")
    fields.foreach { field =>
      println(s"  - ${field.name}: ${field.tree}")
    }
  }
}

// Usage
case class Person(name: String, age: Int)

inline def inspect[T]: Unit = ${inspectTypeImpl[T]}

def inspectTypeImpl[T: Type](using Quotes): Expr[Unit] = {
  inspectType[T]
  '{()}
}

inspect[Person]
// Output:
// Type: Person
// Symbol: Person
// Is case class: true
// Fields:
//   - name: ...
//   - age: ...
```

### 7.2 Case Class Reflection

```scala
import scala.quoted.*

// Get case class field names
inline def fieldNames[T]: List[String] = ${fieldNamesImpl[T]}

def fieldNamesImpl[T: Type](using Quotes): Expr[List[String]] = {
  import quotes.reflect.*
  
  val tpe = TypeRepr.of[T]
  val fields = tpe.typeSymbol.caseFields
  
  val names = fields.map(f => Expr(f.name))
  
  Expr.ofList(names)
}

// Usage
case class User(id: Int, name: String, email: String)

val names = fieldNames[User]
// List("id", "name", "email")

// Get field values
inline def fieldValues[T](value: T): List[Any] = ${fieldValuesImpl('value)}

def fieldValuesImpl[T: Type](value: Expr[T])(using Quotes): Expr[List[Any]] = {
  import quotes.reflect.*
  
  val tpe = TypeRepr.of[T]
  val fields = tpe.typeSymbol.caseFields
  
  val values = fields.map { field =>
    val select = Select(value.asTerm, field).asExpr
    '{$select: Any}
  }
  
  Expr.ofList(values)
}

val user = User(1, "Alice", "alice@example.com")
val values = fieldValues(user)
// List(1, "Alice", "alice@example.com")
```

### 7.3 Generic Type Operations

```scala
import scala.quoted.*

// Inspect type parameters
def analyzeGeneric[F[_], A](using
  Quotes, 
  Type[F], 
  Type[A]
): String = {
  import quotes.reflect.*
  
  val fTpe = TypeRepr.of[F]
  val aTpe = TypeRepr.of[A]
  
  s"Container: ${fTpe.show}, Element: ${aTpe.show}"
}

inline def analyze[F[_], A]: String = ${analyzeGenericImpl[F, A]}

def analyzeGenericImpl[F[_], A](using
  Quotes, 
  Type[F], 
  Type[A]
): Expr[String] = {
  Expr(analyzeGeneric[F, A])
}

val result = analyze[List, Int]
// "Container: List, Element: Int"
```

---

## 8. Practical Macro Examples

### 8.1 JSON Macro

```scala
import scala.quoted.*

// Automatically generate JSON encoder
trait JsonEncoder[T] {
  def encode(value: T): String
}

object JsonEncoder {
  inline def derived[T]: JsonEncoder[T] = ${derivedImpl[T]}
  
  def derivedImpl[T: Type](using Quotes): Expr[JsonEncoder[T]] = {
    import quotes.reflect.*
    
    val tpe = TypeRepr.of[T]
    
    if (!tpe.typeSymbol.flags.is(Flags.Case)) {
      report.error("Only case classes supported")
      return '{new JsonEncoder[T] { def encode(value: T) = "{}" }}
    }
    
    val fields = tpe.typeSymbol.caseFields
    
    '{
      new JsonEncoder[T] {
        def encode(value: T): String = {
          val parts = List(
            ${Expr.ofList(fields.map { field =>
              val fieldName = field.name
              val getter = Select('{value}.asTerm, field).asExpr
              '{s""""$fieldName":${$getter.toString}"""}
            })}
          )
          parts.mkString("{", ",", "}")
        }
      }
    }
  }
}

// Usage
case class Person(name: String, age: Int) derives JsonEncoder

val encoder = summon[JsonEncoder[Person]]
val json = encoder.encode(Person("Alice", 25))
// {"name":"Alice","age":25}
```

### 8.2 SQL Macro

```scala
import scala.quoted.*

// Compile-time SQL checking
case class SQL(query: String)

object SQL {
  inline def apply(inline query: String): SQL = ${sqlImpl('query)}
  
  def sqlImpl(query: Expr[String])(using Quotes): Expr[SQL] = {
    import quotes.reflect.*
    
    // Get the string constant
    query.value match {
      case Some(sql) =>
        // Simple validation
        if (!sql.toLowerCase.startsWith("select")) {
          report.error("SQL must start with SELECT")
        }
        if (sql.contains(";")) {
          report.error("Multiple statements not allowed")
        }
        
        '{new SQL($query)}
      
      case None =>
        report.error("SQL query must be a string literal")
        '{new SQL("")}
    }
  }
}

// Usage
val query1 = SQL("SELECT * FROM users")  // OK
// val query2 = SQL("DROP TABLE users")  // Compile error!
// val query3 = SQL("SELECT * FROM users; DELETE FROM users")  // Compile error!
```

### 8.3 Test Macro

```scala
import scala.quoted.*

// Automatically generate tests
inline def autoTest[T](inline value: T, inline expected: T): Unit = {
  ${autoTestImpl('value, 'expected)}
}

def autoTestImpl[T: Type](
  value: Expr[T], 
  expected: Expr[T]
)(using Quotes): Expr[Unit] = {
  import quotes.reflect.*
  
  val valueStr = value.show
  val expectedStr = expected.show
  
  '{
    val actual = $value
    val exp = $expected
    
    if (actual != exp) {
      throw new AssertionError(
        s"Test failed:\n" +
        s"  Expression: $valueStr\n" +
        s"  Expected: $exp\n" +
        s"  Actual: $actual"
      )
    } else {
      println(s"✓ $valueStr")
    }
  }
}

// Usage
autoTest(1 + 1, 2)  // ✓ 1 + 1
// autoTest(1 + 1, 3)  // throws detailed error
```

### 8.4 Performance Timing Macro

```scala
import scala.quoted.*

// Measure execution time
inline def time[T](inline expr: T): T = ${timeImpl('expr)}

def timeImpl[T: Type](expr: Expr[T])(using Quotes): Expr[T] = {
  import quotes.reflect.*
  
  val exprStr = Expr(expr.show)
  
  '{
    val start = System.nanoTime()
    val result = $expr
    val end = System.nanoTime()
    val duration = (end - start) / 1_000_000.0
    
    println(f"${$exprStr} took $duration%.2f ms")
    result
  }
}

// Usage
val result = time {
  (1 to 1000000).sum
}
// Output: "(1 to 1000000).sum took 15.43 ms"
```

---

## 9. Debugging and Testing

### 9.1 Displaying Generated Code

```scala
import scala.quoted.*

// Use the show method to display generated code
def debugMacro[T: Type](expr: Expr[T])(using Quotes): Unit = {
  import quotes.reflect.*
  
  println("Expression:")
  println(expr.show)
  
  println("\nTree:")
  println(expr.asTerm.show(using Printer.TreeStructure))
}

// Used inside a macro
def myMacroImpl[T: Type](expr: Expr[T])(using Quotes): Expr[T] = {
  debugMacro(expr)  // display debug information
  expr
}
```

### 9.2 Compiler Options

```scala
// Add compiler diagnostics to the ScalaModule in build.mill
def scalacOptions = Seq(
  "-Xprint:typer",  // show trees after type checking
  "-Xprint-types",  // include type information
  "-Ycheck:all",    // check compiler invariants after every phase
  "-Vimplicits"     // explain implicit resolution
)
```

### 9.3 Unit Testing

```scala
import scala.quoted.*
import org.junit.Test
import org.junit.Assert.*

class MacroTest {
  // Test macro expansion
  @Test
  def testDebugMacro(): Unit = {
    val result = debug(1 + 2)
    assertEquals(3, result)
  }
  
  // Test compile-time errors
  @Test
  def testCompileTimeError(): Unit = {
    // Use compile-time testing
    assertDoesNotCompile("""
      val x = "not a number"
      staticAssert(x.toInt > 0, "Must be positive")
    """)
  }
  
  // Test generated code
  @Test
  def testCodeGeneration(): Unit = {
    case class Person(name: String) derives JsonEncoder
    
    val encoder = summon[JsonEncoder[Person]]
    val json = encoder.encode(Person("Alice"))
    
    assertTrue(json.contains("Alice"))
  }
}
```

---

## 10. Best Practices

### 10.1 When to Use Macros

```scala
// Good use cases:

// 1. Eliminating boilerplate code
case class User(id: Int, name: String) derives JsonCodec

// 2. Compile-time validation
sql"SELECT * FROM users WHERE id = $id"

// 3. Performance-critical paths
// Eliminate runtime reflection
inline def fastSerializer[T]: Serializer[T] = ...

// 4. DSL implementation
html {
  body {
    h1("Title")
  }
}

// Scenarios to avoid:

// 1. When an ordinary function can do the job
// Not needed: inline def add(x: Int, y: Int) = x + y
// Use instead: def add(x: Int, y: Int) = x + y

// 2. Premature optimization
// Don't use macros to optimize unless you've confirmed a bottleneck

// 3. Complex logic
// Macros should be simple; put complex logic in ordinary functions
```

### 10.2 Error Handling

```scala
import scala.quoted.*

def safeMacroImpl[T: Type](expr: Expr[T])(using Quotes): Expr[T] = {
  import quotes.reflect.*
  
  try {
    // Macro logic
    expr
  } catch {
    case e: Exception =>
      report.error(s"Macro failed: ${e.getMessage}")
      expr  // return the original expression
  }
}

// Provide clear error messages
def validateImpl(expr: Expr[String])(using Quotes): Expr[String] = {
  import quotes.reflect.*
  
  expr.value match {
    case Some(s) if s.isEmpty =>
      report.error(
        "String cannot be empty",
        expr  // point to the error location
      )
      expr
    
    case Some(s) =>
      expr
    
    case None =>
      report.error("Expected string literal")
      expr
  }
}
```

### 10.3 Performance Considerations

```scala
// 1. Avoid excessive inlining
// Bad: inlining a large function
inline def processLargeData(data: List[Int]): List[Int] = {
  // 100 lines of code...
  data.map(_ * 2).filter(_ > 0).sorted
}

// Good: only inline small functions
inline def double(x: Int): Int = x * 2

def processLargeData(data: List[Int]): List[Int] = {
  data.map(double).filter(_ > 0).sorted
}

// 2. Compilation time vs runtime
// Weigh the increase in compilation time against the runtime performance gain

// 3. Cache macro results
// If possible, cache expensive compile-time computations
```

### 10.4 Documentation and Testing

```scala
/**
 * Debug macro that prints expression and its value.
 * 
 * Example:
 * {{{
 * val x = 10
 * debug(x + 5)  // Output: "x + 5 = 15"
 * }}}
 * 
 * @param expr The expression to debug
 * @return The value of the expression
 */
inline def debug[T](inline expr: T): T = ${debugImpl('expr)}

// Provide tests
class DebugMacroTest {
  @Test
  def testSimpleExpression(): Unit = {
    val result = debug(1 + 2)
    assertEquals(3, result)
  }
  
  @Test
  def testComplexExpression(): Unit = {
    val x = 10
    val result = debug(x * 2 + 5)
    assertEquals(25, result)
  }
}
```

---

## 11. Practice Exercises

### Exercise 1: Logging Macro

```scala
import scala.quoted.*

// Log levels
enum LogLevel:
  case Debug, Info, Warn, Error

// Conditionally compiled logging
object Logger {
  // Compile-time log level
  inline val compiledLevel: LogLevel = LogLevel.Info
  
  inline def debug(inline msg: String): Unit = {
    inline if (shouldLog(LogLevel.Debug)) {
      ${logImpl('msg, '{LogLevel.Debug})}
    }
  }
  
  inline def info(inline msg: String): Unit = {
    inline if (shouldLog(LogLevel.Info)) {
      ${logImpl('msg, '{LogLevel.Info})}
    }
  }
  
  inline def warn(inline msg: String): Unit = {
    inline if (shouldLog(LogLevel.Warn)) {
      ${logImpl('msg, '{LogLevel.Warn})}
    }
  }
  
  inline def error(inline msg: String): Unit = {
    inline if (shouldLog(LogLevel.Error)) {
      ${logImpl('msg, '{LogLevel.Error})}
    }
  }
  
  private inline def shouldLog(inline level: LogLevel): Boolean = {
    level.ordinal >= compiledLevel.ordinal
  }
  
  private def logImpl(
    msg: Expr[String], 
    level: Expr[LogLevel]
  )(using Quotes): Expr[Unit] = {
    import quotes.reflect.*
    
    val position = Position.ofMacroExpansion
    val file = Expr(position.sourceFile.name)
    val line = Expr(position.startLine + 1)
    
    '{
      val timestamp = java.time.LocalDateTime.now()
      println(s"[$timestamp] [${$level}] ${$file}:${$line} - ${$msg}")
    }
  }
}

// Usage
object App {
  def main(args: Array[String]): Unit = {
    Logger.debug("This won't show")  // removed at compile time
    Logger.info("Application started")
    Logger.warn("Low memory")
    Logger.error("Connection failed")
  }
}
```

### Exercise 2: Builder Macro

```scala
import scala.quoted.*

// Automatically generate builder
trait Builder[T] {
  def build(): T
}

object Builder {
  inline def derived[T]: BuilderFactory[T] = ${derivedImpl[T]}
  
  def derivedImpl[T: Type](using Quotes): Expr[BuilderFactory[T]] = {
    import quotes.reflect.*
    
    val tpe = TypeRepr.of[T]
    val fields = tpe.typeSymbol.caseFields
    
    val className = tpe.typeSymbol.name
    val builderName = s"${className}Builder"
    
    // Generate builder class
    '{
      new BuilderFactory[T] {
        def create(): Builder[T] = new Builder[T] {
          private var values = Map.empty[String, Any]
          
          def set(field: String, value: Any): this.type = {
            values = values + (field -> value)
            this
          }
          
          def build(): T = {
            // Simplified version: use reflection to create an instance
            // In practice, should generate type-safe code
            ???
          }
        }
      }
    }
  }
}

trait BuilderFactory[T] {
  def create(): Builder[T]
}

// Usage
case class Person(name: String, age: Int, email: String) derives Builder

val factory = summon[BuilderFactory[Person]]
val person = factory.create()
  .set("name", "Alice")
  .set("age", 25)
  .set("email", "alice@example.com")
  .build()
```

### Exercise 3: Enum Macro

```scala
import scala.quoted.*

// Enum helper functions
object EnumMacros {
  // Get all values
  inline def values[E]: Array[E] = ${valuesImpl[E]}
  
  def valuesImpl[E: Type](using Quotes): Expr[Array[E]] = {
    import quotes.reflect.*
    
    val tpe = TypeRepr.of[E]
    val sym = tpe.typeSymbol
    
    if (!sym.flags.is(Flags.Enum)) {
      report.error(s"${sym.name} is not an enum")
      return '{Array.empty[E]}
    }
    
    val cases = sym.children
      .filter(_.flags.is(Flags.Case))
      .map(c => Ref(c).asExprOf[E])
    
    Expr.ofArray(cases)
  }
  
  // Parse from string
  inline def fromString[E](s: String): Option[E] = ${fromStringImpl[E]('s)}
  
  def fromStringImpl[E: Type](s: Expr[String])(using Quotes): Expr[Option[E]] = {
    import quotes.reflect.*
    
    val tpe = TypeRepr.of[E]
    val sym = tpe.typeSymbol
    val cases = sym.children.filter(_.flags.is(Flags.Case))
    
    val branches = cases.map { c =>
      val name = c.name
      CaseDef(
        Literal(StringConstant(name)),
        None,
        '{Some(${Ref(c).asExprOf[E]})}.asTerm
      )
    }
    
    val defaultCase = CaseDef(
      Wildcard(),
      None,
      '{None}.asTerm
    )
    
    Match(s.asTerm, branches :+ defaultCase).asExprOf[Option[E]]
  }
  
  // Get name
  inline def nameOf[E](e: E): String = ${nameOfImpl('e)}
  
  def nameOfImpl[E: Type](e: Expr[E])(using Quotes): Expr[String] = {
    import quotes.reflect.*
    
    e.asTerm match {
      case Ident(name) => Expr(name)
      case Select(_, name) => Expr(name)
      case _ => 
        report.error("Cannot determine enum name")
        Expr("")
    }
  }
}

// Usage
enum Color:
  case Red, Green, Blue

val all = EnumMacros.values[Color]
// Array(Red, Green, Blue)

val red = EnumMacros.fromString[Color]("Red")
// Some(Red)

val name = EnumMacros.nameOf(Color.Blue)
// "Blue"
```

### Exercise 4: Validation Macro

```scala
import scala.quoted.*
import scala.compiletime.*

// Compile-time string validation
object Validators {
  // Email validation
  inline def email(inline s: String): String = {
    ${emailImpl('s)}
  }
  
  def emailImpl(s: Expr[String])(using Quotes): Expr[String] = {
    import quotes.reflect.*
    
    s.value match {
      case Some(email) =>
        val emailRegex = """^[\w\.-]+@[\w\.-]+\.\w+$"""
        if (!email.matches(emailRegex)) {
          report.error(s"Invalid email: $email")
        }
        s
      
      case None =>
        // Runtime validation
        '{
          val email = $s
          val emailRegex = """^[\w\.-]+@[\w\.-]+\.\w+$"""
          require(email.matches(emailRegex), s"Invalid email: $email")
          email
        }
    }
  }
  
  // URL validation
  inline def url(inline s: String): String = {
    ${urlImpl('s)}
  }
  
  def urlImpl(s: Expr[String])(using Quotes): Expr[String] = {
    import quotes.reflect.*
    
    s.value match {
      case Some(url) =>
        try {
          new java.net.URL(url)
          s
        } catch {
          case _: Exception =>
            report.error(s"Invalid URL: $url")
            s
        }
      
      case None =>
        '{
          val url = $s
          try {
            new java.net.URL(url)
            url
          } catch {
            case e: Exception =>
              throw new IllegalArgumentException(s"Invalid URL: $url", e)
          }
        }
    }
  }
  
  // Range validation
  inline def inRange(inline value: Int, inline min: Int, inline max: Int): Int = {
    inline if (value < min || value > max) {
      error(s"Value $value not in range [$min, $max]")
    }
    value
  }
}

// Usage
val email = Validators.email("alice@example.com")  // OK
// val bad = Validators.email("invalid")  // Compile error!

val url = Validators.url("https://example.com")  // OK
// val badUrl = Validators.url("not a url")  // Compile error!

val age = Validators.inRange(25, 0, 150)  // OK
// val badAge = Validators.inRange(200, 0, 150)  // Compile error!
```

### Exercise 5: Serialization Macro

```scala
import scala.quoted.*

// Binary serialization
trait BinaryCodec[T] {
  def encode(value: T): Array[Byte]
  def decode(bytes: Array[Byte]): T
}

object BinaryCodec {
  inline def derived[T]: BinaryCodec[T] = ${derivedImpl[T]}
  
  def derivedImpl[T: Type](using Quotes): Expr[BinaryCodec[T]] = {
    import quotes.reflect.*
    
    val tpe = TypeRepr.of[T]
    
    if (!tpe.typeSymbol.flags.is(Flags.Case)) {
      report.error("Only case classes supported")
      return '{
        new BinaryCodec[T] {
          def encode(value: T): Array[Byte] = Array.empty
          def decode(bytes: Array[Byte]): T = ???
        }
      }
    }
    
    val fields = tpe.typeSymbol.caseFields
    
    // Generate encoding logic
    val encodeExprs = fields.map { field =>
      val getter = Select('{???}.asTerm, field)
      val fieldType = field.tree match {
        case ValDef(_, tpt, _) => tpt.tpe
        case _ => TypeRepr.of[Any]
      }
      
      // Generate serialization code based on type
      fieldType.asType match {
        case '[Int] => 
          '{
            val value = ${getter.asExprOf[Int]}
            Array(
              (value >> 24).toByte,
              (value >> 16).toByte,
              (value >> 8).toByte,
              value.toByte
            )
          }
        
        case '[String] =>
          '{
            val value = ${getter.asExprOf[String]}
            value.getBytes("UTF-8")
          }
        
        case _ =>
          report.error(s"Unsupported field type: ${fieldType.show}")
          '{Array.empty[Byte]}
      }
    }
    
    '{
      new BinaryCodec[T] {
        def encode(value: T): Array[Byte] = {
          // Combine bytes from all fields
          ${Expr.ofList(encodeExprs)}.flatten.toArray
        }
        
        def decode(bytes: Array[Byte]): T = {
          // Simplified version
          ???
        }
      }
    }
  }
}

// Usage
case class Point(x: Int, y: Int) derives BinaryCodec

val codec = summon[BinaryCodec[Point]]
val bytes = codec.encode(Point(10, 20))
val point = codec.decode(bytes)
```

---

## 12. Key Summary

### Core Concepts of Macros
- **Compile-time execution**: Generate or transform code at compile time
- **Type safety**: Scala 3 macros provide better type safety
- **Performance**: Eliminate runtime overhead

### Scala 3 Macro System
- **Inline**: Inline expansion
- **Quotes**: Represent code with quotes `'{...}`
- **Splicing**: Insert code with splicing `${...}`
- **Reflection**: Inspect and manipulate types

### Use Cases
- Eliminating boilerplate code
- Compile-time validation
- Performance optimization
- DSL implementation

### Best Practices
- Use only when necessary
- Keep it simple
- Provide clear error messages
- Test thoroughly
- Maintain good documentation

---

## Reference Resources

### Official Documentation
- Scala 3 Metaprogramming: https://docs.scala-lang.org/scala3/guides/macros/
- Scala 3 Quotes API: https://dotty.epfl.ch/docs/reference/metaprogramming/

### Learning Resources
- "Programming in Scala" - Martin Odersky
- Scala 3 Macro Tutorials
- Open-source project examples

### Tools
- Scala 3 compiler
- Metals (LSP server)
- IntelliJ IDEA

---

Congratulations on completing your study of Scala macros! Macros are powerful but complex tools. Recommendations:
1. First master Scala basics and advanced features
2. Understand when macros are truly needed
3. Start practicing with simple examples
4. Study macro implementations in high-quality open-source projects

Remember: in most cases, ordinary Scala features are sufficient!

---

> [📚 Table of Contents](../../README.md) | [« Prev: Contextual Abstractions and Type Classes](scala_part8_advanced_topics.md)
