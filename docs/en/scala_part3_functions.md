# Scala Tutorial - Part 3: Functions and Methods

> [📚 Table of Contents](../../README.md) | [« Prev: Basic Syntax](scala_part2_basic_syntax.md) | [Next: OOP »](scala_part4_oop.md)

---

## Table of Contents
1. [Function Basics](#1-function-basics)
2. [Methods vs Functions](#2-methods-vs-functions)
3. [Parameters](#3-parameters)
4. [Higher-Order Functions](#4-higher-order-functions)
5. [Anonymous Functions and Lambdas](#5-anonymous-functions-and-lambdas)
6. [Closures](#6-closures)
7. [Currying](#7-currying)
8. [Partial Application](#8-partial-application)
9. [Recursive Functions](#9-recursive-functions)
10. [Function Composition](#10-function-composition)
11. [Practice Exercises](#11-practice-exercises)

---

## 1. Function Basics

### 1.1 Defining Functions

In Scala, functions are defined using the `def` keyword:

```scala
// Basic syntax
def functionName(param1: Type1, param2: Type2): ReturnType = {
  // function body
  // the value of the last expression is the return value
}

// Simple example
def add(x: Int, y: Int): Int = {
  x + y
}

val result = add(3, 4)  // 7

// Single-line functions can omit curly braces
def multiply(x: Int, y: Int): Int = x * y

// Function with no parameters
def getCurrentTime(): Long = {
  System.currentTimeMillis()
}

// Empty parentheses can be omitted
def getCurrentTime: Long = System.currentTimeMillis()
```

### 1.2 Return Type Inference

Scala can automatically infer return types, but it is recommended to specify them explicitly for public APIs:

```scala
// Return type inference
def square(x: Int) = x * x  // inferred as Int

// Explicit return type (recommended for public functions)
def square(x: Int): Int = x * x

// Explicit specification is recommended for complex functions
def processData(data: List[Int]): List[String] = {
  data.filter(_ > 0).map(_.toString)
}
```

### 1.3 Functions with No Return Value

Functions returning `Unit` are equivalent to Java's `void`:

```scala
def printMessage(msg: String): Unit = {
  println(msg)
}

// Scala 3 requires = before the method body
def printMessage(msg: String): Unit = {
  println(msg)
}
```

### 1.4 Procedure Syntax

```scala
def greet(name: String): Unit = {
  println(s"Hello, $name")
}
```

---

## 2. Methods vs Functions

### 2.1 Methods

Methods are members defined inside a class or object:

```scala
class Calculator {
  // This is a method
  def add(x: Int, y: Int): Int = x + y
  
  def multiply(x: Int, y: Int): Int = x * y
}

val calc = new Calculator
calc.add(3, 4)  // calling a method
```

### 2.2 Functions

Functions are objects that can be assigned to variables and passed as arguments:

```scala
// Function value
val add: (Int, Int) => Int = (x, y) => x + y

// Using the function
val result = add(3, 4)  // 7

// Functions can be assigned to variables
val myFunc = add
myFunc(5, 6)  // 11
```

### 2.3 Method-to-Function Conversion (Eta Expansion)

```scala
class Calculator {
  def add(x: Int, y: Int): Int = x + y
}

val calc = new Calculator

// Scala 3 performs eta expansion automatically
val addFunc: (Int, Int) => Int = calc.add
val addFunc2 = calc.add

// Can now be used like a function
List(1, 2, 3).map(addFunc(_, 10))  // List(11, 12, 13)
```

### 2.4 Key Differences

```scala
// Methods can have type parameters
def identity[T](x: T): T = x

// Methods can have multiple parameter lists
def add(x: Int)(y: Int): Int = x + y

// Methods can have default and named parameters
def greet(name: String, greeting: String = "Hello"): String = {
  s"$greeting, $name!"
}

// Function values lack these features but can be passed as objects
val func: Int => Int = x => x + 1
```

---

## 3. Parameters

### 3.1 Default Parameters

```scala
def greet(name: String, greeting: String = "Hello", punctuation: String = "!"): String = {
  s"$greeting, $name$punctuation"
}

greet("Alice")                    // "Hello, Alice!"
greet("Bob", "Hi")                // "Hi, Bob!"
greet("Charlie", "Hey", "?")      // "Hey, Charlie?"
```

### 3.2 Named Parameters

```scala
def createUser(name: String, age: Int, email: String, active: Boolean = true): String = {
  s"User($name, $age, $email, $active)"
}

// Named parameters can be passed in any order
val user1 = createUser(
  email = "alice@example.com",
  name = "Alice",
  age = 25
)

// Mix positional and named parameters
val user2 = createUser("Bob", 30, email = "bob@example.com")

// Skip parameters that have default values
val user3 = createUser(
  name = "Charlie",
  age = 35,
  email = "charlie@example.com"
)
```

### 3.3 Variable-Length Parameters (Varargs)

```scala
def sum(numbers: Int*): Int = {
  numbers.sum
}

sum(1, 2, 3)        // 6
sum(1, 2, 3, 4, 5)  // 15
sum()               // 0

// Varargs must be the last parameter
def printWithPrefix(prefix: String, items: String*): Unit = {
  items.foreach(item => println(s"$prefix: $item"))
}

printWithPrefix("Item", "apple", "banana", "cherry")

// Passing a sequence to a varargs function
val numbers = List(1, 2, 3, 4, 5)
sum(numbers*)  // use postfix * to expand the sequence
```

### 3.4 By-Name Parameters

By-name parameters are evaluated each time they are used:

```scala
// Regular parameter (by-value)
def byValue(x: Int): Unit = {
  println(s"First use: $x")
  println(s"Second use: $x")
}

// By-name parameter (note the => symbol)
def byName(x: => Int): Unit = {
  println(s"First use: $x")
  println(s"Second use: $x")
}

def expensive(): Int = {
  println("Running expensive computation")
  42
}

println("=== By-Value ===")
byValue(expensive())
// Output:
// Running expensive computation
// First use: 42
// Second use: 42

println("\n=== By-Name ===")
byName(expensive())
// Output:
// Running expensive computation
// First use: 42
// Running expensive computation
// Second use: 42
```

**Practical Example: Custom Control Structures**

```scala
// Implementing a while loop
def myWhile(condition: => Boolean)(body: => Unit): Unit = {
  if (condition) {
    body
    myWhile(condition)(body)
  }
}

var count = 0
myWhile(count < 5) {
  println(count)
  count += 1
}

// Implementing unless (the opposite of if)
def unless(condition: Boolean)(body: => Unit): Unit = {
  if (!condition) body
}

val x = 10
unless(x < 5) {
  println("x is not less than 5")
}

// Timing function
def time[T](block: => T): T = {
  val start = System.nanoTime()
  val result = block
  val end = System.nanoTime()
  println(f"Execution time: ${(end - start) / 1000000.0}%.2f ms")
  result
}

val result = time {
  (1 to 1000000).sum
}
```

---

## 4. Higher-Order Functions

Higher-order functions are functions that accept functions as parameters or return functions.

### 4.1 Functions as Parameters

```scala
// Accepting a function as a parameter
def applyOperation(a: Int, b: Int, op: (Int, Int) => Int): Int = {
  op(a, b)
}

// Defining some operations
def add(x: Int, y: Int): Int = x + y
def multiply(x: Int, y: Int): Int = x * y
def subtract(x: Int, y: Int): Int = x - y

// Usage
applyOperation(10, 5, add)       // 15
applyOperation(10, 5, multiply)  // 50
applyOperation(10, 5, subtract)  // 5

// Using an anonymous function
applyOperation(10, 5, (x, y) => x / y)  // 2
```

**Practical Example: Custom Filter**

```scala
def customFilter[T](list: List[T], predicate: T => Boolean): List[T] = {
  list match {
    case Nil => Nil
    case head :: tail =>
      if (predicate(head)) head :: customFilter(tail, predicate)
      else customFilter(tail, predicate)
  }
}

val numbers = List(1, 2, 3, 4, 5, 6)
customFilter(numbers, (x: Int) => x % 2 == 0)  // List(2, 4, 6)
customFilter(numbers, (x: Int) => x > 3)       // List(4, 5, 6)
```

### 4.2 Returning Functions

```scala
// Returning a function
def multiplier(factor: Int): Int => Int = {
  (x: Int) => x * factor
}

val double = multiplier(2)
val triple = multiplier(3)

double(5)  // 10
triple(5)  // 15

// More complex example
def createGreeter(greeting: String): String => String = {
  (name: String) => s"$greeting, $name!"
}

val sayHello = createGreeter("Hello")
val sayHi = createGreeter("Hi")

sayHello("Alice")  // "Hello, Alice!"
sayHi("Bob")       // "Hi, Bob!"
```

**Practical Example: Creating Validators**

```scala
def createValidator(min: Int, max: Int): Int => Boolean = {
  (value: Int) => value >= min && value <= max
}

val ageValidator = createValidator(0, 120)
val percentageValidator = createValidator(0, 100)

ageValidator(25)      // true
ageValidator(150)     // false
percentageValidator(50)   // true
percentageValidator(101)  // false
```

### 4.3 Combined Usage

```scala
// Accepting and returning a function
def compose[A, B, C](f: B => C, g: A => B): A => C = {
  (x: A) => f(g(x))
}

val addOne = (x: Int) => x + 1
val double = (x: Int) => x * 2

val addOneThenDouble = compose(double, addOne)
addOneThenDouble(5)  // 12 (first +1 gives 6, then *2)

// Using multiple higher-order functions
def transform[T](
  list: List[T],
  filter: T => Boolean,
  map: T => T
): List[T] = {
  list.filter(filter).map(map)
}

val numbers = List(1, 2, 3, 4, 5, 6)
transform(
  numbers,
  (x: Int) => x % 2 == 0,  // keep only even numbers
  (x: Int) => x * 10        // multiply by 10
)
// Result: List(20, 40, 60)
```

---

## 5. Anonymous Functions and Lambdas

### 5.1 Basic Syntax

```scala
// Full syntax
val add = (x: Int, y: Int) => x + y

// Usage
add(3, 4)  // 7

// Single parameter
val square = (x: Int) => x * x
square(5)  // 25

// No parameters
val random = () => math.random()
random()

// Multi-line anonymous function
val complexFunc = (x: Int, y: Int) => {
  val sum = x + y
  val product = x * y
  sum + product
}
```

### 5.2 Type Inference

```scala
val numbers = List(1, 2, 3, 4, 5)

// Full type annotation
numbers.map((x: Int) => x * 2)

// Type inference
numbers.map(x => x * 2)

// Underscore shorthand (when a parameter is used only once)
numbers.map(_ * 2)

// Underscore with multiple parameters
List(1, 2, 3).reduce((x, y) => x + y)
List(1, 2, 3).reduce(_ + _)
```

### 5.3 Underscore Syntax in Detail

```scala
val numbers = List(1, 2, 3, 4, 5)

// Single underscore
numbers.map(_ * 2)           // multiply each element by 2
numbers.filter(_ > 2)        // filter elements greater than 2
numbers.map(_.toString)      // convert to string

// Multiple underscores (correspond to parameters in order)
numbers.reduce(_ + _)        // first _ is x, second _ is y
numbers.reduce(_ - _)        // x - y
numbers.reduce((x, y) => x - y)  // equivalent form

// Cases where underscores cannot be used
numbers.map(_ + _)           // Error! Cannot determine number of parameters
numbers.map(x => x + x)      // Correct: parameter used twice

// Underscore for method calls
val strings = List("hello", "world")
strings.map(_.toUpperCase)   // equivalent to s => s.toUpperCase
strings.map(_.length)        // equivalent to s => s.length
```

### 5.4 Practical Uses of Anonymous Functions

```scala
// List processing
val words = List("apple", "banana", "cherry", "date")

// Filtering
words.filter(_.length > 5)  // List("banana", "cherry")

// Transforming
words.map(_.toUpperCase)

// Sorting
words.sortBy(_.length)  // sort by length
words.sortBy(-_.length) // sort by length in reverse

// Grouping
words.groupBy(_.head)  // group by first character
// Map(a -> List(apple), b -> List(banana), c -> List(cherry), d -> List(date))

// Chained operations
words
  .filter(_.length >= 5)
  .map(_.toUpperCase)
  .sortBy(_.length)
```

**Complex Example: Data Processing**

```scala
case class Person(name: String, age: Int, city: String)

val people = List(
  Person("Alice", 25, "Taipei"),
  Person("Bob", 30, "Tokyo"),
  Person("Charlie", 25, "Taipei"),
  Person("David", 35, "Seoul")
)

// Find the names of all people aged 25
people
  .filter(_.age == 25)
  .map(_.name)
// List("Alice", "Charlie")

// Group by city and compute average age
people
  .groupBy(_.city)
  .view
  .mapValues(ps => ps.map(_.age).sum.toDouble / ps.length)
  .toMap
// Map("Taipei" -> 25.0, "Tokyo" -> 30.0, "Seoul" -> 35.0)
```

---

## 6. Closures

A closure is a function that references free variables from the environment in which the function was defined.

### 6.1 Basic Concept

```scala
def makeAdder(x: Int): Int => Int = {
  (y: Int) => x + y  // x is a free variable from the outer scope
}

val add5 = makeAdder(5)
val add10 = makeAdder(10)

add5(3)   // 8
add10(3)  // 13

// x is "closed over" in the returned function
```

### 6.2 Closures Capturing Variables

```scala
var factor = 2

val multiplier = (x: Int) => x * factor

multiplier(5)  // 10

factor = 3
multiplier(5)  // 15 (uses the updated factor)

// Closures capture a reference to the variable, not its value
```

### 6.3 Practical Examples

**Counter:**

```scala
def makeCounter(): () => Int = {
  var count = 0
  () => {
    count += 1
    count
  }
}

val counter1 = makeCounter()
val counter2 = makeCounter()

counter1()  // 1
counter1()  // 2
counter1()  // 3

counter2()  // 1 (independent counter)
counter2()  // 2
```

**Bank Account:**

```scala
def createAccount(initialBalance: Double): (String, Double) => Double = {
  var balance = initialBalance
  
  (operation: String, amount: Double) => {
    operation match {
      case "deposit" =>
        balance += amount
        balance
      case "withdraw" =>
        if (amount <= balance) {
          balance -= amount
          balance
        } else {
          println("Insufficient funds")
          balance
        }
      case "balance" =>
        balance
    }
  }
}

val account = createAccount(1000.0)

account("deposit", 500.0)   // 1500.0
account("withdraw", 200.0)  // 1300.0
account("balance", 0)       // 1300.0
```

**Memoization:**

```scala
def memoize[A, B](f: A => B): A => B = {
  val cache = scala.collection.mutable.Map[A, B]()
  (x: A) => cache.getOrElseUpdate(x, f(x))
}

// Fibonacci sequence (naive version is slow)
def slowFib(n: Int): Int = {
  if (n <= 1) n
  else slowFib(n - 1) + slowFib(n - 2)
}

// Memoized version
val fastFib = memoize(slowFib)

// First call is slow
time { fastFib(40) }  // approximately 1-2 seconds

// Second call retrieves from cache, very fast
time { fastFib(40) }  // < 1 millisecond
```

---

## 7. Currying

Currying is the technique of converting a multi-parameter function into a series of single-parameter functions.

### 7.1 Basic Syntax

```scala
// Regular function
def add(x: Int, y: Int): Int = x + y

// Curried function
def addCurried(x: Int)(y: Int): Int = x + y

// Usage
add(3, 4)         // 7
addCurried(3)(4)  // 7

// Partial application
val add3 = addCurried(3) _
add3(4)  // 7
add3(10) // 13
```

### 7.2 Manual Currying

```scala
// Convert a regular function to a curried function
def curry[A, B, C](f: (A, B) => C): A => B => C = {
  (a: A) => (b: B) => f(a, b)
}

val add = (x: Int, y: Int) => x + y
val curriedAdd = curry(add)

val add5 = curriedAdd(5)
add5(3)  // 8

// Uncurrying
def uncurry[A, B, C](f: A => B => C): (A, B) => C = {
  (a: A, b: B) => f(a)(b)
}

val normalAdd = uncurry(curriedAdd)
normalAdd(3, 4)  // 7
```

### 7.3 Practical Examples

**Custom Filter:**

```scala
def customFilter[T](predicate: T => Boolean)(list: List[T]): List[T] = {
  list.filter(predicate)
}

// Create specialised filters
val filterEven = customFilter[Int](_ % 2 == 0) _
val filterPositive = customFilter[Int](_ > 0) _

val numbers = List(-2, -1, 0, 1, 2, 3, 4, 5)

filterEven(numbers)      // List(-2, 0, 2, 4)
filterPositive(numbers)  // List(1, 2, 3, 4, 5)
```

**HTML Generator:**

```scala
def htmlTag(tagName: String)(attributes: String)(content: String): String = {
  s"<$tagName $attributes>$content</$tagName>"
}

// Create generators for specific tags
val div = htmlTag("div") _
val span = htmlTag("span") _

div("class='container'")(
"Hello, World")
// <div class='container'>Hello, World</div>

span("id='message'")("Important!")
// <span id='message'>Important!</span>

// Further specialization
val containerDiv = div("class='container'") _
containerDiv("Content 1")
containerDiv("Content 2")
```

**Database Query:**

```scala
// Simulating a database query
case class User(id: Int, name: String, age: Int, city: String)

def query(table: String)(condition: User => Boolean)(users: List[User]): List[User] = {
  println(s"Querying table: $table")
  users.filter(condition)
}

val users = List(
  User(1, "Alice", 25, "Taipei"),
  User(2, "Bob", 30, "Tokyo"),
  User(3, "Charlie", 25, "Taipei")
)

// Create specialised queries
val userQuery = query("users") _

val findByAge = userQuery((u: User) => u.age == 25) _
val findByCity = userQuery((u: User) => u.city == "Taipei") _

findByAge(users)   // List(User(1, "Alice", 25, "Taipei"), User(3, "Charlie", 25, "Taipei"))
findByCity(users)  // List(User(1, "Alice", 25, "Taipei"), User(3, "Charlie", 25, "Taipei"))
```

---

## 8. Partial Application

Partial application fixes some parameters of a function to produce a new function.

### 8.1 Basic Concept

```scala
def sum(a: Int, b: Int, c: Int): Int = a + b + c

// Partial application: fix the first parameter
val add10 = sum(10, _: Int, _: Int)
add10(5, 3)  // 18

// Fix the first two parameters
val add15 = sum(10, 5, _: Int)
add15(3)  // 18

// Currying makes partial application easier
def sumCurried(a: Int)(b: Int)(c: Int): Int = a + b + c

val add10Curried = sumCurried(10) _
val add15Curried = sumCurried(10)(5) _

add10Curried(5)(3)  // 18
add15Curried(3)     // 18
```

### 8.2 Practical Examples

**Logger:**

```scala
def log(level: String)(timestamp: Long)(message: String): Unit = {
  println(s"[$level] [$timestamp] $message")
}

// Create loggers for specific levels
val info = log("INFO") _
val error = log("ERROR") _
val debug = log("DEBUG") _

val now = System.currentTimeMillis()

info(now)("Application started")
error(now)("An error occurred")
debug(now)("Debug message")

// Further fix the timestamp
val infoNow = info(now) _
infoNow("Message 1")
infoNow("Message 2")
```

**Mathematical Operations:**

```scala
def calculate(operation: String)(x: Double)(y: Double): Double = {
  operation match {
    case "add" => x + y
    case "subtract" => x - y
    case "multiply" => x * y
    case "divide" if y != 0 => x / y
    case _ => 0.0
  }
}

val add = calculate("add") _
val multiply = calculate("multiply") _

val add10 = add(10) _
val double = multiply(2) _

add10(5)    // 15.0
double(7)   // 14.0
```

**Formatter:**

```scala
def format(prefix: String)(suffix: String)(content: String): String = {
  s"$prefix$content$suffix"
}

val htmlBold = format("<b>")(</b>") _
val htmlItalic = format("<i>")(</i>") _
val quote = format("\"")(\"") _

htmlBold("Important")     // "<b>Important</b>"
htmlItalic("Emphasis")    // "<i>Emphasis</i>"
quote("Quoted text")      // "\"Quoted text\""
```

---

## 9. Recursive Functions

### 9.1 Basic Recursion

```scala
// Factorial
def factorial(n: Int): Int = {
  if (n <= 1) 1
  else n * factorial(n - 1)
}

factorial(5)  // 120

// Fibonacci sequence
def fibonacci(n: Int): Int = {
  if (n <= 1) n
  else fibonacci(n - 1) + fibonacci(n - 2)
}

fibonacci(10)  // 55

// Greatest Common Divisor (GCD)
def gcd(a: Int, b: Int): Int = {
  if (b == 0) a
  else gcd(b, a % b)
}

gcd(48, 18)  // 6
```

### 9.2 Tail Recursion Optimization

Tail recursion means the recursive call is the last operation in the function, which allows the compiler to optimize it into a loop.

```scala
import scala.annotation.tailrec

// Non-tail-recursive (may cause stack overflow)
def factorial(n: Int): Int = {
  if (n <= 1) 1
  else n * factorial(n - 1)  // not tail-recursive, because we still multiply by n
}

// Tail-recursive version
@tailrec
def factorialTail(n: Int, accumulator: Int = 1): Int = {
  if (n <= 1) accumulator
  else factorialTail(n - 1, n * accumulator)  // tail-recursive!
}

factorialTail(5)      // 120
factorialTail(10000)  // will not overflow the stack

// Tail-recursive Fibonacci
@tailrec
def fibonacciTail(n: Int, a: Int = 0, b: Int = 1): Int = {
  if (n == 0) a
  else fibonacciTail(n - 1, b, a + b)
}

fibonacciTail(10)  // 55
```

**The @tailrec Annotation:**

```scala
// @tailrec verifies that the function is truly tail-recursive
@tailrec
def sum(n: Int): Int = {
  if (n <= 0) 0
  else n + sum(n - 1)  // Compilation error! Not tail-recursive
}

// Correct tail-recursive version
@tailrec
def sumTail(n: Int, acc: Int = 0): Int = {
  if (n <= 0) acc
  else sumTail(n - 1, acc + n)
}
```

### 9.3 List Processing with Recursion

```scala
// Compute list length
@tailrec
def length[T](list: List[T], acc: Int = 0): Int = list match {
  case Nil => acc
  case _ :: tail => length(tail, acc + 1)
}

length(List(1, 2, 3, 4, 5))  // 5

// Reverse a list
@tailrec
def reverse[T](list: List[T], acc: List[T] = Nil): List[T] = list match {
  case Nil => acc
  case head :: tail => reverse(tail, head :: acc)
}

reverse(List(1, 2, 3, 4, 5))  // List(5, 4, 3, 2, 1)

// Sum a list
@tailrec
def sumList(list: List[Int], acc: Int = 0): Int = list match {
  case Nil => acc
  case head :: tail => sumList(tail, acc + head)
}

sumList(List(1, 2, 3, 4, 5))  // 15

// Filter a list
@tailrec
def filter[T](list: List[T], predicate: T => Boolean, acc: List[T] = Nil): List[T] = list match {
  case Nil => acc.reverse
  case head :: tail =>
    if (predicate(head)) filter(tail, predicate, head :: acc)
    else filter(tail, predicate, acc)
}

filter(List(1, 2, 3, 4, 5, 6), (x: Int) => x % 2 == 0)  // List(2, 4, 6)
```

### 9.4 Tree Recursion

```scala
// Binary tree definition
sealed trait Tree[+T]
case class Leaf[T](value: T) extends Tree[T]
case class Branch[T](left: Tree[T], right: Tree[T]) extends Tree[T]

// Compute tree size
def size[T](tree: Tree[T]): Int = tree match {
  case Leaf(_) => 1
  case Branch(left, right) => size(left) + size(right)
}

// Compute maximum depth of the tree
def maxDepth[T](tree: Tree[T]): Int = tree match {
  case Leaf(_) => 1
  case Branch(left, right) => 1 + math.max(maxDepth(left), maxDepth(right))
}

// Test
val tree = Branch(
  Branch(Leaf(1), Leaf(2)),
  Branch(Leaf(3), Branch(Leaf(4), Leaf(5)))
)

size(tree)      // 5
maxDepth(tree)  // 4
```

---

## 10. Function Composition

### 10.1 andThen and compose

```scala
val addOne = (x: Int) => x + 1
val double = (x: Int) => x * 2
val square = (x: Int) => x * x

// andThen: f andThen g = g(f(x))
val addThenDouble = addOne andThen double
addThenDouble(5)  // 12 (first +1 gives 6, then *2)

val doubleThenSquare = double andThen square
doubleThenSquare(3)  // 36 (first *2 gives 6, then squared)

// compose: f compose g = f(g(x))
val doubleAfterAdd = double compose addOne
doubleAfterAdd(5)  // 12 (first +1 gives 6, then *2)

val squareAfterDouble = square compose double
squareAfterDouble(3)  // 36 (first *2 gives 6, then squared)

// Chained composition
val pipeline = addOne andThen double andThen square
pipeline(2)  // 36 ((2+1)*2)^2 = 6^2 = 36
```

### 10.2 Custom Composition Functions

```scala
def compose[A, B, C](f: B => C, g: A => B): A => C = {
  (x: A) => f(g(x))
}

def andThen[A, B, C](f: A => B, g: B => C): A => C = {
  (x: A) => g(f(x))
}

// Usage
val addOne = (x: Int) => x + 1
val double = (x: Int) => x * 2

val composed = compose(double, addOne)
composed(5)  // 12

val chained = andThen(addOne, double)
chained(5)  // 12
```

### 10.3 Practical Examples

**Data Processing Pipeline:**

```scala
// String processing pipeline
val trim = (s: String) => s.trim
val lowercase = (s: String) => s.toLowerCase
val removeSpaces = (s: String) => s.replaceAll("\\s+", "")

val normalize = trim andThen lowercase andThen removeSpaces

normalize("  Hello World  ")  // "helloworld"

// Numeric processing pipeline
val input = List("  123  ", " 456 ", "789")

val processNumber = trim andThen (_.toInt) andThen (_ * 2)

input.map(processNumber)  // List(246, 912, 1578)
```

**Validation Pipeline:**

```scala
type Validator[T] = T => Either[String, T]

def minLength(n: Int): Validator[String] = { s =>
  if (s.length >= n) Right(s)
  else Left(s"Length must be at least $n characters")
}

def maxLength(n: Int): Validator[String] = { s =>
  if (s.length <= n) Right(s)
  else Left(s"Length cannot exceed $n characters")
}

def nonEmpty: Validator[String] = { s =>
  if (s.nonEmpty) Right(s)
  else Left("Cannot be empty")
}

// Combining validators
def validate[T](value: T, validators: Validator[T]*): Either[String, T] = {
  validators.foldLeft(Right(value): Either[String, T]) { (acc, validator) =>
    acc.flatMap(validator)
  }
}

validate("hello", nonEmpty, minLength(3), maxLength(10))  // Right("hello")
validate("hi", nonEmpty, minLength(3), maxLength(10))     // Left("Length must be at least 3 characters")
```

**Function Pipeline Operator:**

```scala
extension [A](value: A) {
  def |>[B](f: A => B): B = f(value)
}

// Usage
val result = 5 |> (_ + 1) |> (_ * 2) |> (_ - 3)
// equivalent to: ((5 + 1) * 2) - 3 = 9

// Practical example
val data = "  Hello, World!  "
val processed = data
  |> (_.trim)
  |> (_.toLowerCase)
  |> (_.split(","))
  |> (_.head)

println(processed)  // "hello"
```

---

## 11. Practice Exercises

### Exercise 1: Math Library

```scala
object MathLibrary {
  // Implement exponentiation (without using Math.pow)
  def power(base: Double, exp: Int): Double = {
    @tailrec
    def powerTail(base: Double, exp: Int, acc: Double): Double = {
      if (exp == 0) acc
      else if (exp > 0) powerTail(base, exp - 1, acc * base)
      else powerTail(base, exp + 1, acc / base)
    }
    powerTail(base, exp, 1.0)
  }
  
  // Square root (Newton's method)
  def sqrt(n: Double, epsilon: Double = 0.0001): Double = {
    @tailrec
    def improve(guess: Double): Double = {
      if (math.abs(guess * guess - n) < epsilon) guess
      else improve((guess + n / guess) / 2)
    }
    improve(1.0)
  }
  
  // Factorial
  def factorial(n: Int): BigInt = {
    @tailrec
    def factTail(n: Int, acc: BigInt): BigInt = {
      if (n <= 1) acc
      else factTail(n - 1, n * acc)
    }
    factTail(n, 1)
  }
  
  // Combinations C(n, k)
  def combination(n: Int, k: Int): BigInt = {
    factorial(n) / (factorial(k) * factorial(n - k))
  }
  
  // Test
  def main(args: Array[String]): Unit = {
    println(s"2^10 = ${power(2, 10)}")
    println(s"sqrt(16) = ${sqrt(16)}")
    println(s"10! = ${factorial(10)}")
    println(s"C(10, 3) = ${combination(10, 3)}")
  }
}
```

### Exercise 2: List Processing Functions

```scala
object ListOperations {
  // Implement map
  def map[A, B](list: List[A], f: A => B): List[B] = {
    @tailrec
    def mapTail(remaining: List[A], acc: List[B]): List[B] = remaining match {
      case Nil => acc.reverse
      case head :: tail => mapTail(tail, f(head) :: acc)
    }
    mapTail(list, Nil)
  }
  
  // Implement filter
  def filter[A](list: List[A], predicate: A => Boolean): List[A] = {
    @tailrec
    def filterTail(remaining: List[A], acc: List[A]): List[A] = remaining match {
      case Nil => acc.reverse
      case head :: tail =>
        if (predicate(head)) filterTail(tail, head :: acc)
        else filterTail(tail, acc)
    }
    filterTail(list, Nil)
  }
  
  // Implement foldLeft
  @tailrec
  def foldLeft[A, B](list: List[A], initial: B)(f: (B, A) => B): B = list match {
    case Nil => initial
    case head :: tail => foldLeft(tail, f(initial, head))(f)
  }
  
  // Implement foldRight (non-tail-recursive)
  def foldRight[A, B](list: List[A], initial: B)(f: (A, B) => B): B = list match {
    case Nil => initial
    case head :: tail => f(head, foldRight(tail, initial)(f))
  }
  
  // Implement flatMap
  def flatMap[A, B](list: List[A], f: A => List[B]): List[B] = {
    foldLeft(list, List[B]())((acc, elem) => acc ++ f(elem))
  }
  
  // Test
  def main(args: Array[String]): Unit = {
    val numbers = List(1, 2, 3, 4, 5)
    
    println(map(numbers, (x: Int) => x * 2))
    println(filter(numbers, (x: Int) => x % 2 == 0))
    println(foldLeft(numbers, 0)(_ + _))
    println(flatMap(numbers, (x: Int) => List(x, x * 2)))
  }
}
```

### Exercise 3: Functional Cache

```scala
object FunctionalCache {
  // Generic cache function
  def cached[A, B](f: A => B): A => B = {
    val cache = scala.collection.mutable.Map[A, B]()
    (x: A) => cache.getOrElseUpdate(x, f(x))
  }
  
  // Cache with expiry time
  def cachedWithExpiry[A, B](f: A => B, expiryMs: Long): A => B = {
    val cache = scala.collection.mutable.Map[A, (B, Long)]()
    (x: A) => {
      val now = System.currentTimeMillis()
      cache.get(x) match {
        case Some((value, timestamp)) if now - timestamp < expiryMs =>
          value
        case _ =>
          val value = f(x)
          cache(x) = (value, now)
          value
      }
    }
  }
  
  // Test
  def expensiveOperation(n: Int): Int = {
    println(s"Computing $n...")
    Thread.sleep(1000)  // simulate a time-consuming operation
    n * n
  }
  
  def main(args: Array[String]): Unit = {
    val cachedOp = cached(expensiveOperation)
    
    println(cachedOp(5))  // compute and cache
    println(cachedOp(5))  // retrieve from cache
    println(cachedOp(10)) // compute and cache
    println(cachedOp(5))  // retrieve from cache
  }
}
```

### Exercise 4: Function Pipeline

```scala
object FunctionPipeline {
  // Define the pipeline operator
  extension [A](value: A) {
    def |>[B](f: A => B): B = f(value)
  }
  
  // Define some transformation functions
  val trim = (s: String) => s.trim
  val lowercase = (s: String) => s.toLowerCase
  val words = (s: String) => s.split("\\s+").toList
  val nonEmpty = (list: List[String]) => list.filter(_.nonEmpty)
  val sort = (list: List[String]) => list.sorted
  val unique = (list: List[String]) => list.distinct
  
  // Composition example
  def processText(text: String): List[String] = {
    text
      |> trim
      |> lowercase
      |> words
      |> nonEmpty
      |> unique
      |> sort
  }
  
  // Test
  def main(args: Array[String]): Unit = {
    val text = "  Hello World  hello Scala  Scala Programming  "
    val result = processText(text)
    println(result)  // List(hello, programming, scala, world)
  }
}
```

### Exercise 5: Partial Application and Currying in Practice

```scala
object ConfigurableLogger {
  // Log levels
  sealed trait LogLevel
  case object DEBUG extends LogLevel
  case object INFO extends LogLevel
  case object WARN extends LogLevel
  case object ERROR extends LogLevel
  
  // Curried log function
  def log(minLevel: LogLevel)(level: LogLevel)(timestamp: Long)(message: String): Unit = {
    val levels = List(DEBUG, INFO, WARN, ERROR)
    if (levels.indexOf(level) >= levels.indexOf(minLevel)) {
      val levelStr = level match {
        case DEBUG => "DEBUG"
        case INFO => "INFO"
        case WARN => "WARN"
        case ERROR => "ERROR"
      }
      println(s"[$levelStr] [$timestamp] $message")
    }
  }
  
  // Create loggers with different configurations
  val productionLogger = log(INFO) _
  val developmentLogger = log(DEBUG) _
  
  val prodInfo = productionLogger(INFO) _
  val prodError = productionLogger(ERROR) _
  val devDebug = developmentLogger(DEBUG) _
  
  def main(args: Array[String]): Unit = {
    val now = System.currentTimeMillis()
    
    // Production environment will not show DEBUG
    productionLogger(DEBUG)(now)("This is a debug message")
    prodInfo(now)("Application started")
    prodError(now)("An error occurred")
    
    println()
    
    // Development environment shows all levels
    devDebug(now)("This is a debug message")
    developmentLogger(INFO)(now)("Application started")
    developmentLogger(ERROR)(now)("An error occurred")
  }
}
```

### Exercise 6: Recursive Tree Operations

```scala
object TreeOperations {
  // Binary search tree definition
  sealed trait BST[+A]
  case object Empty extends BST[Nothing]
  case class Node[A](value: A, left: BST[A], right: BST[A]) extends BST[A]
  
  // Insert an element
  def insert[A](tree: BST[A], elem: A)(using ord: Ordering[A]): BST[A] = tree match {
    case Empty => Node(elem, Empty, Empty)
    case Node(value, left, right) =>
      if (ord.lt(elem, value)) Node(value, insert(left, elem), right)
      else if (ord.gt(elem, value)) Node(value, left, insert(right, elem))
      else tree
  }
  
  // Search for an element
  def contains[A](tree: BST[A], elem: A)(using ord: Ordering[A]): Boolean = tree match {
    case Empty => false
    case Node(value, left, right) =>
      if (ord.lt(elem, value)) contains(left, elem)
      else if (ord.gt(elem, value)) contains(right, elem)
      else true
  }
  
  // In-order traversal
  def inOrder[A](tree: BST[A]): List[A] = tree match {
    case Empty => Nil
    case Node(value, left, right) => inOrder(left) ++ List(value) ++ inOrder(right)
  }
  
  // Tree size
  def size[A](tree: BST[A]): Int = tree match {
    case Empty => 0
    case Node(_, left, right) => 1 + size(left) + size(right)
  }
  
  // Tree height
  def height[A](tree: BST[A]): Int = tree match {
    case Empty => 0
    case Node(_, left, right) => 1 + math.max(height(left), height(right))
  }
  
  // Test
  def main(args: Array[String]): Unit = {
    val tree = List(5, 3, 7, 1, 9, 4, 6)
      .foldLeft(Empty: BST[Int])(insert)
    
    println(s"In-order traversal: ${inOrder(tree)}")
    println(s"Size: ${size(tree)}")
    println(s"Height: ${height(tree)}")
    println(s"Contains 4? ${contains(tree, 4)}")
    println(s"Contains 8? ${contains(tree, 8)}")
  }
}
```

### Exercise 7: Higher-Order Function Applications

```scala
object HigherOrderFunctions {
  // Retry mechanism
  def retry[T](maxAttempts: Int)(f: => T): Option[T] = {
    @tailrec
    def attempt(remaining: Int): Option[T] = {
      try {
        Some(f)
      } catch {
        case _: Exception if remaining > 1 =>
          println(s"Failed, attempts remaining: ${remaining - 1}")
          attempt(remaining - 1)
        case _: Exception =>
          None
      }
    }
    attempt(maxAttempts)
  }
  
  // Timer
  def timed[T](name: String)(block: => T): T = {
    val start = System.nanoTime()
    val result = block
    val end = System.nanoTime()
    println(f"$name execution time: ${(end - start) / 1000000.0}%.2f ms")
    result
  }
  
  // Conditional execution
  def when[T](condition: Boolean)(block: => T): Option[T] = {
    if (condition) Some(block) else None
  }
  
  // Repeated execution
  def times(n: Int)(block: => Unit): Unit = {
    (1 to n).foreach(_ => block)
  }
  
  // Test
  def main(args: Array[String]): Unit = {
    // Retry example
    var attempts = 0
    val result = retry(3) {
      attempts += 1
      if (attempts < 3) throw new Exception("Failed")
      else "Success"
    }
    println(s"Result: $result")
    
    // Timing example
    timed("Sorting") {
      (1 to 1000000).sorted
    }
    
    // Conditional execution
    when(5 > 3) {
      println("Condition is true")
    }
    
    // Repeated execution
    times(3) {
      println("Repeated execution")
    }
  }
}
```

---

## 12. Key Summary

### Function Definition
- Use `def` to define methods
- Return type inference is supported
- Single-line functions can omit curly braces
- The return value is the last expression

### Methods vs Functions
- Methods are class members
- Functions are objects that can be assigned and passed
- Methods can be converted to functions (Eta Expansion)

### Parameter Types
- Default parameters and named parameters
- Variable-length parameters (`*`)
- By-name parameters (`=>`)

### Higher-Order Functions
- Functions can be passed as parameters
- Functions can be returned as values
- Function composition is supported

### Anonymous Functions
- Lambda expressions
- Underscore shorthand
- Type inference

### Advanced Concepts
- Closures: capture external variables
- Currying: multiple parameter lists
- Partial application: fix some parameters
- Tail recursion: optimize recursion
- Function composition: andThen and compose

---

## Next Steps

After completing Part 3, you have mastered:
- Function and method definition and usage
- The concept and application of higher-order functions
- Core techniques of functional programming
- Recursion and optimization

**Next, learn:**
- [Part 4: Object-Oriented Programming](scala_part4_oop.md) - Classes, objects, inheritance, Traits

Ready to continue?

---

> [📚 Table of Contents](../../README.md) | [« Prev: Basic Syntax](scala_part2_basic_syntax.md) | [Next: OOP »](scala_part4_oop.md)
