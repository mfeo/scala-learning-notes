# Scala Tutorial - Part 6: Pattern Matching

> [📚 Table of Contents](../../README.md) | [« Prev: Collections](scala_part5_collections.md) | [Next: Error Handling »](scala_part7_error_handling.md)

---

## Table of Contents
1. [Pattern Matching Basics](#1-pattern-matching-basics)
2. [Constant Patterns](#2-constant-patterns)
3. [Variable Patterns](#3-variable-patterns)
4. [Constructor Patterns](#4-constructor-patterns)
5. [Sequence Patterns](#5-sequence-patterns)
6. [Tuple Patterns](#6-tuple-patterns)
7. [Type Patterns](#7-type-patterns)
8. [Guard Conditions](#8-guard-conditions)
9. [Pattern Binding](#9-pattern-binding)
10. [Regex Patterns](#10-regex-patterns)
11. [Partial Functions](#11-partial-functions)
12. [Option Pattern Matching](#12-option-pattern-matching)
13. [Practice Exercises](#13-practice-exercises)

---

## 1. Pattern Matching Basics

### 1.1 match Expression

```scala
// Basic syntax
val x = 5

x match {
  case 1 => "one"
  case 2 => "two"
  case 3 => "three"
  case _ => "other"  // _ is the wildcard
}

// match is an expression and returns a value
val result = x match {
  case 1 => "one"
  case 2 => "two"
  case _ => "many"
}
println(result)  // "many"
```

### 1.2 Differences from switch

```scala
// Java switch (only works with primitive types and strings)
// switch(x) {
//   case 1: return "one";
//   case 2: return "two";
//   default: return "other";
// }

// Scala match (much more powerful)
// - Can match any type
// - Can match complex structures
// - Is an expression that returns a value
// - Does not require break
// - Checks exhaustiveness

def describe(x: Any): String = x match {
  case 1 => "integer one"
  case "hello" => "string hello"
  case true => "boolean true"
  case List(1, 2, 3) => "list of 1, 2, 3"
  case _ => "something else"
}
```

### 1.3 Exhaustiveness Check

```scala
sealed trait Color
case object Red extends Color
case object Green extends Color
case object Blue extends Color

// The compiler will warn: Blue case is missing
def colorName(color: Color): String = color match {
  case Red => "red"
  case Green => "green"
  // Missing Blue - compiler warning!
}

// Correct version
def colorNameComplete(color: Color): String = color match {
  case Red => "red"
  case Green => "green"
  case Blue => "blue"
}
```

---

## 2. Constant Patterns

### 2.1 Literal Matching

```scala
// Numeric matching
def matchNumber(n: Int): String = n match {
  case 0 => "zero"
  case 1 => "one"
  case 2 => "two"
  case _ => "many"
}

// String matching
def matchString(s: String): String = s match {
  case "hello" => "greeting"
  case "bye" => "farewell"
  case "" => "empty"
  case _ => "other"
}

// Boolean matching
def matchBoolean(b: Boolean): String = b match {
  case true => "yes"
  case false => "no"
}

// Character matching
def matchChar(c: Char): String = c match {
  case 'a' | 'e' | 'i' | 'o' | 'u' => "vowel"
  case _ => "consonant"
}
```

### 2.2 Constant Value Matching

```scala
val MaxSize = 100
val MinSize = 0

def validateSize(size: Int): String = size match {
  case MinSize => "minimum"
  case MaxSize => "maximum"
  case s if s < MinSize => "too small"
  case s if s > MaxSize => "too large"
  case _ => "valid"
}

// Using constants from an object
object Constants {
  val SUCCESS = 200
  val NOT_FOUND = 404
  val SERVER_ERROR = 500
}

def handleStatus(code: Int): String = code match {
  case Constants.SUCCESS => "OK"
  case Constants.NOT_FOUND => "Not Found"
  case Constants.SERVER_ERROR => "Server Error"
  case _ => "Unknown"
}
```

---

## 3. Variable Patterns

### 3.1 Variable Binding

```scala
// A variable pattern matches any value and binds it
def describe(x: Any): String = x match {
  case 0 => "zero"
  case n: Int => s"integer: $n"  // n is bound to the matched value
  case s: String => s"string: $s"
  case other => s"something: $other"
}

describe(42)      // "integer: 42"
describe("hi")    // "string: hi"
describe(true)    // "something: true"
```

### 3.2 Variables vs Constants

```scala
val x = 10

// This matches any value because x is treated as a variable pattern
5 match {
  case x => s"matched: $x"  // always matches, x = 5
}

// To match the constant x, use backticks
5 match {
  case `x` => "equals to x"  // only matches when the value is 10
  case _ => "not equals to x"
}

// Practical example
object Color {
  val Red = "#FF0000"
  val Green = "#00FF00"
  val Blue = "#0000FF"
}

def identifyColor(hex: String): String = hex match {
  case Color.Red => "red"      // matches the constant
  case Color.Green => "green"
  case Color.Blue => "blue"
  case color => s"unknown: $color"  // variable pattern
}
```

---

## 4. Constructor Patterns

### 4.1 Case Class Destructuring

```scala
case class Person(name: String, age: Int)

def greet(person: Person): String = person match {
  case Person("Alice", _) => "Hi Alice!"
  case Person(name, age) if age < 18 => s"Hi young $name!"
  case Person(name, age) => s"Hello $name, you are $age"
}

val alice = Person("Alice", 25)
val bob = Person("Bob", 15)

greet(alice)  // "Hi Alice!"
greet(bob)    // "Hi young Bob!"
```

### 4.2 Nested Destructuring

```scala
case class Address(city: String, country: String)
case class Person(name: String, age: Int, address: Address)

def describe(person: Person): String = person match {
  case Person(name, _, Address("Taipei", "Taiwan")) =>
    s"$name lives in Taipei, Taiwan"
  
  case Person(name, age, Address(city, "Taiwan")) =>
    s"$name, $age years old, lives in $city, Taiwan"
  
  case Person(name, _, Address(city, country)) =>
    s"$name lives in $city, $country"
}

val p1 = Person("Alice", 25, Address("Taipei", "Taiwan"))
val p2 = Person("Bob", 30, Address("Tokyo", "Japan"))

describe(p1)  // "Alice lives in Taipei, Taiwan"
describe(p2)  // "Bob lives in Tokyo, Japan"
```

### 4.3 Complex Structure Destructuring

```scala
case class Company(name: String, employees: List[Person])

def analyzeCompany(company: Company): String = company match {
  case Company(name, Nil) =>
    s"$name has no employees"
  
  case Company(name, List(person)) =>
    s"$name has one employee: ${person.name}"
  
  case Company(name, List(p1, p2)) =>
    s"$name has two employees: ${p1.name} and ${p2.name}"
  
  case Company(name, employees) =>
    s"$name has ${employees.length} employees"
}

// Deep nesting
case class Department(name: String, manager: Person, team: List[Person])
case class Organization(name: String, departments: List[Department])

def findManager(org: Organization, deptName: String): Option[String] = org match {
  case Organization(_, departments) =>
    departments.collectFirst {
      case Department(`deptName`, Person(managerName, _, _), _) => managerName
    }
}
```

---

## 5. Sequence Patterns

### 5.1 List Patterns

```scala
// Empty list
def isEmpty(list: List[Int]): String = list match {
  case Nil => "empty"
  case _ => "not empty"
}

// Fixed elements
def matchList(list: List[Int]): String = list match {
  case List(1, 2, 3) => "exactly 1, 2, 3"
  case List(1, _, 3) => "1, something, 3"
  case List(1, _*) => "starts with 1"
  case _ => "other"
}

// head :: tail pattern
def processHead(list: List[Int]): String = list match {
  case Nil => "empty"
  case head :: Nil => s"only $head"
  case head :: tail => s"head: $head, tail: $tail"
}

// More complex patterns
def analyze(list: List[Int]): String = list match {
  case Nil => "empty"
  case x :: Nil => s"single element: $x"
  case x :: y :: Nil => s"two elements: $x, $y"
  case x :: y :: rest => s"starts with $x, $y, has ${rest.length} more"
}

analyze(List())           // "empty"
analyze(List(1))          // "single element: 1"
analyze(List(1, 2))       // "two elements: 1, 2"
analyze(List(1, 2, 3, 4)) // "starts with 1, 2, has 2 more"
```

### 5.2 Variable-Length Sequences

```scala
// Use _* to match any number of elements
def matchAny(list: List[Int]): String = list match {
  case List(1, 2, _*) => "starts with 1, 2"
  case List(_, _, 3) => "ends with 3, has 3 elements"
  case List(first, _*) => s"first is $first"
  case _ => "other"
}

// Extract first few elements and the rest
def splitList(list: List[Int]): String = list match {
  case first :: second :: rest => 
    s"first: $first, second: $second, rest: $rest"
  case _ => "less than 2 elements"
}

// Practical example: command parsing
def parseCommand(args: List[String]): String = args match {
  case "help" :: Nil => "Showing help"
  case "create" :: name :: Nil => s"Creating: $name"
  case "delete" :: name :: Nil => s"Deleting: $name"
  case "list" :: Nil => "Listing all"
  case "search" :: query :: Nil => s"Searching for: $query"
  case cmd :: _ => s"Unknown command: $cmd"
  case Nil => "No command"
}
```

### 5.3 Array and Vector Patterns

```scala
// Array patterns (uses Array.apply)
def matchArray(arr: Array[Int]): String = arr match {
  case Array() => "empty"
  case Array(x) => s"one element: $x"
  case Array(x, y) => s"two elements: $x, $y"
  case _ => "more elements"
}

// Vector is similar
def matchVector(vec: Vector[Int]): String = vec match {
  case Vector() => "empty"
  case Vector(x) => s"one element: $x"
  case x +: xs => s"head: $x, tail: $xs"
  case _ => "other"
}
```

---

## 6. Tuple Patterns

### 6.1 Tuple Destructuring

```scala
// Pair
def describePair(pair: (Int, String)): String = pair match {
  case (1, "one") => "perfect match"
  case (n, "one") => s"$n and one"
  case (1, s) => s"one and $s"
  case (n, s) => s"$n and $s"
}

// Triple
def describe3(triple: (Int, String, Boolean)): String = triple match {
  case (1, "one", true) => "all match"
  case (n, s, true) => s"$n, $s, and true"
  case (n, s, false) => s"$n, $s, and false"
}

// Nested tuple
def nested(t: ((Int, Int), String)): String = t match {
  case ((x, y), s) => s"point ($x, $y) with label $s"
}
```

### 6.2 Practical Examples

```scala
// Coordinate processing
type Point = (Double, Double)

def classifyPoint(p: Point): String = p match {
  case (0, 0) => "origin"
  case (0, y) => s"on y-axis at $y"
  case (x, 0) => s"on x-axis at $x"
  case (x, y) if x == y => s"on diagonal at ($x, $y)"
  case (x, y) if x > 0 && y > 0 => "quadrant I"
  case (x, y) if x < 0 && y > 0 => "quadrant II"
  case (x, y) if x < 0 && y < 0 => "quadrant III"
  case (x, y) => "quadrant IV"
}

// Map iteration
val scores = Map("Alice" -> 95, "Bob" -> 87, "Charlie" -> 92)

scores.foreach {
  case (name, score) if score >= 90 => println(s"$name: Excellent")
  case (name, score) => println(s"$name: $score")
}

// Swap elements
def swap[A, B](pair: (A, B)): (B, A) = pair match {
  case (a, b) => (b, a)
}
```

---

## 7. Type Patterns

### 7.1 Basic Type Matching

```scala
def typeMatch(x: Any): String = x match {
  case i: Int => s"integer: $i"
  case s: String => s"string: $s"
  case d: Double => s"double: $d"
  case b: Boolean => s"boolean: $b"
  case _ => "unknown type"
}

typeMatch(42)       // "integer: 42"
typeMatch("hello")  // "string: hello"
typeMatch(3.14)     // "double: 3.14"
typeMatch(true)     // "boolean: true"
```

### 7.2 Collection Type Matching

```scala
def collectionMatch(x: Any): String = x match {
  case list: List[?] => s"list of ${list.length} elements"
  case set: Set[?] => s"set of ${set.size} elements"
  case map: Map[?, ?] => s"map of ${map.size} entries"
  case arr: Array[?] => s"array of ${arr.length} elements"
  case _ => "other type"
}

collectionMatch(List(1, 2, 3))        // "list of 3 elements"
collectionMatch(Set("a", "b"))        // "set of 2 elements"
collectionMatch(Map("x" -> 1))        // "map of 1 entries"
```

### 7.3 Type Erasure Caveats

```scala
// ⚠️ Warning: type erasure
// The JVM erases generic type information at runtime

def buggyMatch(x: Any): String = x match {
  case list: List[Int] => "list of ints"      // Warning!
  case list: List[String] => "list of strings" // This branch will never be reached
  case _ => "other"
}

buggyMatch(List(1, 2, 3))      // "list of ints"
buggyMatch(List("a", "b"))     // "list of ints" - incorrect!

// Correct approach: check the element type
def correctMatch(x: Any): String = x match {
  case list: List[?] if list.nonEmpty && list.head.isInstanceOf[Int] =>
    "list of ints"
  case list: List[?] if list.nonEmpty && list.head.isInstanceOf[String] =>
    "list of strings"
  case list: List[?] => "empty list"
  case _ => "other"
}
```

### 7.4 Hierarchical Type Matching

```scala
abstract class Animal
case class Dog(name: String) extends Animal
case class Cat(name: String) extends Animal
case class Bird(name: String) extends Animal

def sound(animal: Animal): String = animal match {
  case Dog(name) => s"$name says Woof!"
  case Cat(name) => s"$name says Meow!"
  case Bird(name) => s"$name says Tweet!"
  case _ => "Unknown animal"
}

sound(Dog("Buddy"))    // "Buddy says Woof!"
sound(Cat("Whiskers")) // "Whiskers says Meow!"
```

---

## 8. Guard Conditions

### 8.1 Basic Guards

```scala
// An if condition restricts the match
def classify(n: Int): String = n match {
  case x if x < 0 => "negative"
  case 0 => "zero"
  case x if x > 0 && x < 10 => "small positive"
  case x if x >= 10 => "large positive"
}

classify(-5)  // "negative"
classify(5)   // "small positive"
classify(15)  // "large positive"
```

### 8.2 Complex Guards

```scala
case class Person(name: String, age: Int, city: String)

def categorize(person: Person): String = person match {
  case Person(name, age, "Taipei") if age < 18 =>
    s"Young $name from Taipei"
  
  case Person(name, age, "Taipei") if age >= 18 && age < 65 =>
    s"Adult $name from Taipei"
  
  case Person(name, age, "Taipei") if age >= 65 =>
    s"Senior $name from Taipei"
  
  case Person(name, age, city) if age < 18 =>
    s"Young $name from $city"
  
  case Person(name, _, city) =>
    s"$name from $city"
}
```

### 8.3 Function Calls in Guards

```scala
def isPrime(n: Int): Boolean = {
  if (n <= 1) false
  else if (n == 2) true
  else !(2 to math.sqrt(n).toInt).exists(i => n % i == 0)
}

def classifyNumber(n: Int): String = n match {
  case x if x < 0 => "negative"
  case 0 => "zero"
  case 1 => "one"
  case x if isPrime(x) => s"$x is prime"
  case x if x % 2 == 0 => s"$x is even"
  case x => s"$x is odd composite"
}

classifyNumber(7)   // "7 is prime"
classifyNumber(8)   // "8 is even"
classifyNumber(9)   // "9 is odd composite"
```

### 8.4 Multiple Conditions

```scala
case class Student(name: String, score: Int, attendance: Int)

def evaluateStudent(student: Student): String = student match {
  case Student(name, score, attendance) 
    if score >= 90 && attendance >= 80 =>
    s"$name: Excellent (A)"
  
  case Student(name, score, attendance) 
    if score >= 80 && attendance >= 70 =>
    s"$name: Good (B)"
  
  case Student(name, score, attendance) 
    if score >= 60 && attendance >= 60 =>
    s"$name: Pass (C)"
  
  case Student(name, _, _) =>
    s"$name: Fail (F)"
}
```

---

## 9. Pattern Binding

### 9.1 Binding with @

```scala
// Use @ to bind the entire matched value
case class Address(city: String, country: String)
case class Person(name: String, address: Address)

def describe(person: Person): String = person match {
  case Person(name, addr @ Address("Taipei", _)) =>
    s"$name lives in Taipei: $addr"
  
  case Person(name, addr @ Address(_, "Taiwan")) =>
    s"$name lives in Taiwan: $addr"
  
  case Person(name, address) =>
    s"$name lives at $address"
}

val p = Person("Alice", Address("Taipei", "Taiwan"))
describe(p)  // "Alice lives in Taipei: Address(Taipei,Taiwan)"
```

### 9.2 Nested Binding

```scala
sealed trait Tree
case class Leaf(value: Int) extends Tree
case class Branch(left: Tree, right: Tree) extends Tree

def analyze(tree: Tree): String = tree match {
  case leaf @ Leaf(value) =>
    s"Leaf with value $value: $leaf"
  
  case branch @ Branch(left @ Leaf(_), right @ Leaf(_)) =>
    s"Branch with two leaves: $branch"
  
  case Branch(left, right) =>
    s"Branch with subtrees"
}
```

### 9.3 Practical Example

```scala
// HTTP response handling
sealed trait Response
case class Success(code: Int, body: String) extends Response
case class Error(code: Int, message: String) extends Response

def handleResponse(response: Response): String = response match {
  case success @ Success(200, _) =>
    s"OK: $success"
  
  case success @ Success(201, _) =>
    s"Created: $success"
  
  case error @ Error(404, _) =>
    s"Not Found: $error"
  
  case error @ Error(code, _) if code >= 500 =>
    s"Server Error: $error"
  
  case other =>
    s"Other response: $other"
}
```

---

## 10. Regex Patterns

### 10.1 Basic Regex Matching

```scala
val EmailPattern = "(.+)@(.+)\\.(.+)".r

def validateEmail(email: String): String = email match {
  case EmailPattern(user, domain, tld) =>
    s"User: $user, Domain: $domain, TLD: $tld"
  case _ =>
    "Invalid email"
}

validateEmail("alice@example.com")
// "User: alice, Domain: example, TLD: com"

validateEmail("invalid")
// "Invalid email"
```

### 10.2 Complex Patterns

```scala
// Phone number
val PhonePattern = """(\d{3})-(\d{4})""".r

def parsePhone(phone: String): Option[(String, String)] = phone match {
  case PhonePattern(prefix, number) => Some((prefix, number))
  case _ => None
}

parsePhone("123-4567")  // Some((123,4567))
parsePhone("invalid")   // None

// Date
val DatePattern = """(\d{4})-(\d{2})-(\d{2})""".r

def parseDate(date: String): String = date match {
  case DatePattern(year, month, day) =>
    s"Year: $year, Month: $month, Day: $day"
  case _ =>
    "Invalid date format"
}

parseDate("2024-01-15")
// "Year: 2024, Month: 01, Day: 15"
```

### 10.3 URL Parsing

```scala
val UrlPattern = """(https?)://([^/]+)(/.*)?""".r

def parseUrl(url: String): String = url match {
  case UrlPattern(protocol, domain, path) =>
    s"Protocol: $protocol, Domain: $domain, Path: ${Option(path).getOrElse("/")}"
  case _ =>
    "Invalid URL"
}

parseUrl("https://example.com/path/to/page")
// "Protocol: https, Domain: example.com, Path: /path/to/page"

parseUrl("http://example.com")
// "Protocol: http, Domain: example.com, Path: /"
```

### 10.4 Log Parsing

```scala
val LogPattern = """(\d{4}-\d{2}-\d{2}) (\d{2}:\d{2}:\d{2}) \[(\w+)\] (.+)""".r

case class LogEntry(date: String, time: String, level: String, message: String)

def parseLog(line: String): Option[LogEntry] = line match {
  case LogPattern(date, time, level, message) =>
    Some(LogEntry(date, time, level, message))
  case _ =>
    None
}

val log = "2024-01-15 14:30:45 [ERROR] Connection failed"
parseLog(log)
// Some(LogEntry(2024-01-15,14:30:45,ERROR,Connection failed))
```

---

## 11. Partial Functions

### 11.1 PartialFunction Basics

```scala
// A partial function is only defined for a subset of its input
val divide: PartialFunction[(Int, Int), Int] = {
  case (x, y) if y != 0 => x / y
}

// Check if defined
divide.isDefinedAt((10, 2))  // true
divide.isDefinedAt((10, 0))  // false

// Usage
divide((10, 2))  // 5
// divide((10, 0))  // MatchError!

// Safe usage
divide.lift((10, 0))  // None
divide.lift((10, 2))  // Some(5)
```

### 11.2 The collect Method

```scala
val numbers = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// collect uses a partial function
val evens = numbers.collect {
  case x if x % 2 == 0 => x * 2
}
// List(4, 8, 12, 16, 20)

// Equivalent to filter + map
val evens2 = numbers.filter(_ % 2 == 0).map(_ * 2)
```

### 11.3 Practical Examples

```scala
// Processing a mixed-type list
val mixed: List[Any] = List(1, "hello", 2.5, true, 42, "world")

val strings = mixed.collect {
  case s: String => s.toUpperCase
}
// List("HELLO", "WORLD")

val numbers = mixed.collect {
  case i: Int => i * 2
  case d: Double => d * 2
}
// List(2, 5.0, 84)

// Option handling
val maybeNumbers = List(Some(1), None, Some(2), None, Some(3))

val values = maybeNumbers.collect {
  case Some(x) => x * 2
}
// List(2, 4, 6)
```

### 11.4 Composing Partial Functions

```scala
val handleInt: PartialFunction[Any, String] = {
  case i: Int => s"integer: $i"
}

val handleString: PartialFunction[Any, String] = {
  case s: String => s"string: $s"
}

// Compose partial functions
val handleBoth = handleInt orElse handleString

handleBoth(42)      // "integer: 42"
handleBoth("hi")    // "string: hi"
// handleBoth(true)  // MatchError

// With a default fallback
val handleAll = handleBoth orElse {
  case x => s"other: $x"
}

handleAll(true)  // "other: true"
```

---

## 12. Option Pattern Matching

### 12.1 Basic Option Matching

```scala
def describe(opt: Option[Int]): String = opt match {
  case Some(value) => s"has value: $value"
  case None => "no value"
}

describe(Some(42))  // "has value: 42"
describe(None)      // "no value"
```

### 12.2 Nested Options

```scala
def process(opt: Option[Option[Int]]): String = opt match {
  case Some(Some(value)) => s"nested value: $value"
  case Some(None) => "outer Some, inner None"
  case None => "outer None"
}

process(Some(Some(42)))  // "nested value: 42"
process(Some(None))      // "outer Some, inner None"
process(None)            // "outer None"
```

### 12.3 Options and for Comprehensions

```scala
def divide(a: Int, b: Int): Option[Int] = {
  if (b != 0) Some(a / b) else None
}

// Chained Option operations
val result = for {
  a <- divide(10, 2)   // Some(5)
  b <- divide(a, 2)    // Some(2)
  c <- divide(b, 1)    // Some(2)
} yield c

println(result)  // Some(2)

// Short-circuit behaviour
val result2 = for {
  a <- divide(10, 2)
  b <- divide(a, 0)    // None - short-circuits here
  c <- divide(b, 1)
} yield c

println(result2)  // None
```

### 12.4 Practical Example

```scala
case class User(id: Int, name: String, email: Option[String])

val users = List(
  User(1, "Alice", Some("alice@example.com")),
  User(2, "Bob", None),
  User(3, "Charlie", Some("charlie@example.com"))
)

// Extract users who have an email
val withEmail = users.collect {
  case User(_, name, Some(email)) => (name, email)
}
// List((Alice,alice@example.com), (Charlie,charlie@example.com))

// Look up a user
def findUser(id: Int): Option[User] = {
  users.find(_.id == id)
}

findUser(1) match {
  case Some(User(_, name, Some(email))) =>
    println(s"Found $name with email $email")
  case Some(User(_, name, None)) =>
    println(s"Found $name without email")
  case None =>
    println("User not found")
}
```

---

## 13. Practice Exercises

### Exercise 1: Calculator

```scala
// Expression tree
sealed trait Expr
case class Number(value: Double) extends Expr
case class Add(left: Expr, right: Expr) extends Expr
case class Subtract(left: Expr, right: Expr) extends Expr
case class Multiply(left: Expr, right: Expr) extends Expr
case class Divide(left: Expr, right: Expr) extends Expr

object Calculator {
  def eval(expr: Expr): Double = expr match {
    case Number(value) => value
    
    case Add(left, right) =>
      eval(left) + eval(right)
    
    case Subtract(left, right) =>
      eval(left) - eval(right)
    
    case Multiply(left, right) =>
      eval(left) * eval(right)
    
    case Divide(left, right) =>
      val divisor = eval(right)
      if (divisor != 0) eval(left) / divisor
      else throw new ArithmeticException("Division by zero")
  }
  
  // Simplify expressions
  def simplify(expr: Expr): Expr = expr match {
    // Addition simplifications
    case Add(Number(0), x) => simplify(x)
    case Add(x, Number(0)) => simplify(x)
    case Add(Number(a), Number(b)) => Number(a + b)
    
    // Multiplication simplifications
    case Multiply(Number(0), _) => Number(0)
    case Multiply(_, Number(0)) => Number(0)
    case Multiply(Number(1), x) => simplify(x)
    case Multiply(x, Number(1)) => simplify(x)
    case Multiply(Number(a), Number(b)) => Number(a * b)
    
    // Recursive simplification
    case Add(left, right) => 
      Add(simplify(left), simplify(right))
    case Multiply(left, right) => 
      Multiply(simplify(left), simplify(right))
    case Subtract(left, right) => 
      Subtract(simplify(left), simplify(right))
    case Divide(left, right) => 
      Divide(simplify(left), simplify(right))
    
    case other => other
  }
  
  // Test
  def main(args: Array[String]): Unit = {
    // (3 + 5) * 2
    val expr1 = Multiply(Add(Number(3), Number(5)), Number(2))
    println(s"Result: ${eval(expr1)}")  // 16.0
    
    // 0 * (3 + 5)
    val expr2 = Multiply(Number(0), Add(Number(3), Number(5)))
    println(s"Original: $expr2")
    println(s"Simplified: ${simplify(expr2)}")  // Number(0)
    
    // 1 * x + 0
    val expr3 = Add(Multiply(Number(1), Number(42)), Number(0))
    println(s"Simplified: ${simplify(expr3)}")  // Number(42)
  }
}
```

### Exercise 2: JSON Parser

```scala
// Simple JSON AST
sealed trait Json
case object JsonNull extends Json
case class JsonBool(value: Boolean) extends Json
case class JsonNumber(value: Double) extends Json
case class JsonString(value: String) extends Json
case class JsonArray(values: List[Json]) extends Json
case class JsonObject(fields: Map[String, Json]) extends Json

object JsonParser {
  // Pretty print
  def pretty(json: Json, indent: Int = 0): String = {
    val spaces = "  " * indent
    json match {
      case JsonNull => "null"
      case JsonBool(value) => value.toString
      case JsonNumber(value) => value.toString
      case JsonString(value) => s""""$value""""
      
      case JsonArray(Nil) => "[]"
      case JsonArray(values) =>
        val elements = values.map(v => pretty(v, indent + 1))
        s"[\n${elements.map(spaces + "  " + _).mkString(",\n")}\n$spaces]"
      
      case JsonObject(fields) if fields.isEmpty => "{}"
      case JsonObject(fields) =>
        val pairs = fields.map { case (key, value) =>
          s""""$key": ${pretty(value, indent + 1)}"""
        }
        s"{\n${pairs.map(spaces + "  " + _).mkString(",\n")}\n$spaces}"
    }
  }
  
  // Find by path
  def find(json: Json, path: List[String]): Option[Json] = (json, path) match {
    case (value, Nil) => Some(value)
    
    case (JsonObject(fields), key :: rest) =>
      fields.get(key).flatMap(find(_, rest))
    
    case (JsonArray(values), indexStr :: rest) =>
      indexStr.toIntOption
        .flatMap(i => values.lift(i))
        .flatMap(find(_, rest))
    
    case _ => None
  }
  
  // Update value
  def update(json: Json, path: List[String], newValue: Json): Json = {
    (json, path) match {
      case (_, Nil) => newValue
      
      case (JsonObject(fields), key :: rest) =>
        val updatedValue = fields.get(key) match {
          case Some(value) => update(value, rest, newValue)
          case None => newValue
        }
        JsonObject(fields + (key -> updatedValue))
      
      case (JsonArray(values), indexStr :: rest) =>
        indexStr.toIntOption match {
          case Some(i) if i >= 0 && i < values.length =>
            val updated = update(values(i), rest, newValue)
            JsonArray(values.updated(i, updated))
          case _ => json
        }
      
      case _ => json
    }
  }
  
  // Test
  def main(args: Array[String]): Unit = {
    val json = JsonObject(Map(
      "name" -> JsonString("Alice"),
      "age" -> JsonNumber(25),
      "active" -> JsonBool(true),
      "address" -> JsonObject(Map(
        "city" -> JsonString("Taipei"),
        "country" -> JsonString("Taiwan")
      )),
      "hobbies" -> JsonArray(List(
        JsonString("reading"),
        JsonString("coding")
      ))
    ))
    
    println("=== Original JSON ===")
    println(pretty(json))
    
    println("\n=== Find city ===")
    println(find(json, List("address", "city")))
    
    println("\n=== Update age ===")
    val updated = update(json, List("age"), JsonNumber(26))
    println(pretty(updated))
  }
}
```

### Exercise 3: State Machine

```scala
// Vending machine
sealed trait VendingMachineState
case object Idle extends VendingMachineState
case class AcceptingMoney(amount: Int) extends VendingMachineState
case class Dispensing(item: String) extends VendingMachineState

sealed trait Input
case class InsertCoin(amount: Int) extends Input
case class SelectItem(item: String, price: Int) extends Input
case object Cancel extends Input
case object Collect extends Input

case class VendingMachine(
  state: VendingMachineState,
  inventory: Map[String, Int],
  revenue: Int
) {
  def process(input: Input): (VendingMachine, String) = (state, input) match {
    // Idle state
    case (Idle, InsertCoin(amount)) =>
      (copy(state = AcceptingMoney(amount)), s"Inserted $$$amount")
    
    case (Idle, _) =>
      (this, "Please insert a coin first")
    
    // Accepting money state
    case (AcceptingMoney(current), InsertCoin(amount)) =>
      val total = current + amount
      (copy(state = AcceptingMoney(total)), s"Total: $$$total")
    
    case (AcceptingMoney(amount), SelectItem(item, price)) 
      if amount >= price && inventory.getOrElse(item, 0) > 0 =>
      val change = amount - price
      val newInventory = inventory.updated(item, inventory(item) - 1)
      val message = if (change > 0) 
        s"Selected $item, change: $$$change" 
      else 
        s"Selected $item"
      (copy(
        state = Dispensing(item),
        inventory = newInventory,
        revenue = revenue + price
      ), message)
    
    case (AcceptingMoney(amount), SelectItem(item, price)) 
      if amount < price =>
      (this, s"Insufficient funds, need $$${ price - amount} more")
    
    case (AcceptingMoney(amount), SelectItem(item, _)) =>
      (this, s"$item is sold out")
    
    case (AcceptingMoney(amount), Cancel) =>
      (copy(state = Idle), s"Cancelled, returning $$$amount")
    
    // Dispensing state
    case (Dispensing(item), Collect) =>
      (copy(state = Idle), s"Collected $item, transaction complete")
    
    case (Dispensing(_), _) =>
      (this, "Please collect your item first")
    
    case _ =>
      (this, "Invalid operation")
  }
}

object VendingMachineDemo {
  def main(args: Array[String]): Unit = {
    var machine = VendingMachine(
      state = Idle,
      inventory = Map("Cola" -> 5, "Coffee" -> 3, "Tea" -> 10),
      revenue = 0
    )
    
    def execute(input: Input): Unit = {
      val (newMachine, message) = machine.process(input)
      machine = newMachine
      println(s">>> $message")
      println(s"State: ${machine.state}\n")
    }
    
    println("=== Vending Machine Test ===\n")
    
    execute(InsertCoin(10))
    execute(InsertCoin(10))
    execute(SelectItem("Cola", 15))
    execute(Collect)
    
    println("\n=== Second Purchase ===\n")
    execute(InsertCoin(20))
    execute(SelectItem("Coffee", 25))
    execute(InsertCoin(10))
    execute(SelectItem("Coffee", 25))
    execute(Collect)
    
    println(s"\nTotal revenue: $${machine.revenue}")
    println(s"Inventory: ${machine.inventory}")
  }
}
```

### Exercise 4: Command Parser

```scala
// Command AST
sealed trait Command
case object Help extends Command
case object Exit extends Command
case class Echo(message: String) extends Command
case class Set(key: String, value: String) extends Command
case class Get(key: String) extends Command
case class Delete(key: String) extends Command
case class List(prefix: Option[String]) extends Command
case class Unknown(input: String) extends Command

object CommandParser {
  def parse(input: String): Command = {
    val parts = input.trim.split("\\s+").toList
    
    parts match {
      case "help" :: Nil => Help
      case "exit" :: Nil | "quit" :: Nil => Exit
      
      case "echo" :: message => 
        Echo(message.mkString(" "))
      
      case "set" :: key :: value :: Nil => 
        Set(key, value)
      
      case "get" :: key :: Nil => 
        Get(key)
      
      case "delete" :: key :: Nil | "del" :: key :: Nil => 
        Delete(key)
      
      case "list" :: Nil => 
        List(None)
      
      case "list" :: prefix :: Nil => 
        List(Some(prefix))
      
      case _ => 
        Unknown(input)
    }
  }
  
  def execute(
    command: Command,
    storage: Map[String, String]
  ): (Map[String, String], String) = command match {
    
    case Help =>
      (storage, """Available commands:
        |  help              - Show this help
        |  exit/quit         - Exit
        |  echo <message>    - Print a message
        |  set <key> <value> - Set a value
        |  get <key>         - Get a value
        |  delete <key>      - Delete a value
        |  list [prefix]     - List all keys (optional prefix filter)
        |""".stripMargin)
    
    case Exit =>
      (storage, "Goodbye!")
    
    case Echo(message) =>
      (storage, message)
    
    case Set(key, value) =>
      (storage + (key -> value), s"Set $key = $value")
    
    case Get(key) =>
      storage.get(key) match {
        case Some(value) => (storage, s"$key = $value")
        case None => (storage, s"Key $key not found")
      }
    
    case Delete(key) =>
      if (storage.contains(key))
        (storage - key, s"Deleted $key")
      else
        (storage, s"Key $key not found")
    
    case List(None) =>
      val keys = storage.keys.toList.sorted
      (storage, if (keys.isEmpty) "No data" else keys.mkString(", "))
    
    case List(Some(prefix)) =>
      val keys = storage.keys.filter(_.startsWith(prefix)).toList.sorted
      (storage, if (keys.isEmpty) s"No keys with prefix $prefix" else keys.mkString(", "))
    
    case Unknown(input) =>
      (storage, s"Unknown command: $input (type 'help' for available commands)")
  }
  
  // REPL
  def repl(): Unit = {
    var storage = Map.empty[String, String]
    var running = true
    
    println("Command-line interface (type 'help' for commands)")
    
    while (running) {
      print("\n> ")
      val input = scala.io.StdIn.readLine()
      
      if (input != null && input.nonEmpty) {
        val command = parse(input)
        val (newStorage, message) = execute(command, storage)
        storage = newStorage
        println(message)
        
        if (command == Exit) {
          running = false
        }
      }
    }
  }
  
  def main(args: Array[String]): Unit = {
    repl()
  }
}
```

### Exercise 5: Binary Tree Operations

```scala
sealed trait Tree[+A]
case object Empty extends Tree[Nothing]
case class Node[A](value: A, left: Tree[A], right: Tree[A]) extends Tree[A]

object TreeOps {
  // Size
  def size[A](tree: Tree[A]): Int = tree match {
    case Empty => 0
    case Node(_, left, right) => 1 + size(left) + size(right)
  }
  
  // Depth
  def depth[A](tree: Tree[A]): Int = tree match {
    case Empty => 0
    case Node(_, left, right) => 1 + math.max(depth(left), depth(right))
  }
  
  // Map
  def map[A, B](tree: Tree[A])(f: A => B): Tree[B] = tree match {
    case Empty => Empty
    case Node(value, left, right) =>
      Node(f(value), map(left)(f), map(right)(f))
  }
  
  // Fold
  def fold[A, B](tree: Tree[A])(z: B)(f: (B, A, B) => B): B = tree match {
    case Empty => z
    case Node(value, left, right) =>
      f(fold(left)(z)(f), value, fold(right)(z)(f))
  }
  
  // Traversal
  def inOrder[A](tree: Tree[A]): List[A] = tree match {
    case Empty => Nil
    case Node(value, left, right) =>
      inOrder(left) ++ List(value) ++ inOrder(right)
  }
  
  def preOrder[A](tree: Tree[A]): List[A] = tree match {
    case Empty => Nil
    case Node(value, left, right) =>
      value :: preOrder(left) ++ preOrder(right)
  }
  
  def postOrder[A](tree: Tree[A]): List[A] = tree match {
    case Empty => Nil
    case Node(value, left, right) =>
      postOrder(left) ++ postOrder(right) ++ List(value)
  }
  
  // Search
  def contains[A](tree: Tree[A], target: A)(using ord: Ordering[A]): Boolean = {
    tree match {
      case Empty => false
      case Node(value, left, right) =>
        if (ord.equiv(value, target)) true
        else if (ord.lt(target, value)) contains(left, target)
        else contains(right, target)
    }
  }
  
  // Insert (BST)
  def insert[A](tree: Tree[A], elem: A)(using ord: Ordering[A]): Tree[A] = {
    tree match {
      case Empty => Node(elem, Empty, Empty)
      case Node(value, left, right) =>
        if (ord.lteq(elem, value))
          Node(value, insert(left, elem), right)
        else
          Node(value, left, insert(right, elem))
    }
  }
  
  // Test
  def main(args: Array[String]): Unit = {
    val tree = Node(5,
      Node(3,
        Node(1, Empty, Empty),
        Node(4, Empty, Empty)
      ),
      Node(8,
        Node(7, Empty, Empty),
        Node(9, Empty, Empty)
      )
    )
    
    println(s"Size: ${size(tree)}")
    println(s"Depth: ${depth(tree)}")
    println(s"In-order: ${inOrder(tree)}")
    println(s"Pre-order: ${preOrder(tree)}")
    println(s"Post-order: ${postOrder(tree)}")
    println(s"Contains 7: ${contains(tree, 7)}")
    println(s"Contains 10: ${contains(tree, 10)}")
    
    val doubled = map(tree)(_ * 2)
    println(s"Doubled: ${inOrder(doubled)}")
    
    val sum = fold(tree)(0)((l, v, r) => l + v + r)
    println(s"Sum: $sum")
  }
}
```

---

## 14. Key Takeaways

### Pattern Matching Characteristics
- **Powerful and flexible**: can match values, types, and structures
- **Exhaustiveness checking**: the compiler warns about missing cases
- **Expression**: returns a value and is composable

### Common Patterns
- **Constant patterns**: literal value matching
- **Variable patterns**: bind the matched value
- **Constructor patterns**: destructure case classes
- **Sequence patterns**: destructure List, Array
- **Type patterns**: type checking

### Advanced Techniques
- **Guard conditions**: `if` clauses that restrict matches
- **Pattern binding**: the `@` symbol
- **Regular expressions**: string parsing
- **Partial functions**: the `collect` method

### Best Practices
- Use `sealed trait` to ensure exhaustiveness
- Match specific cases first, use `_` last
- Use guards to simplify logic
- Combine with `Option` to handle potentially absent values

---

## Next Steps

After completing Part 6, you have mastered:
- Pattern matching basic and advanced usage
- Application scenarios for various patterns
- Guard conditions and pattern binding
- Using partial functions

**Up next:**
- [Part 7: Error Handling](scala_part7_error_handling.md) - Deep dive into Option, Either, and Try

Ready to continue?

---

> [📚 Table of Contents](../../README.md) | [« Prev: Collections](scala_part5_collections.md) | [Next: Error Handling »](scala_part7_error_handling.md)
