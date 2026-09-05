# Scala Tutorial - Part 4: Object-Oriented Programming

> [📚 Table of Contents](../../README.md) | [« Prev: Functions](scala_part3_functions.md) | [Next: Collections »](scala_part5_collections.md)

---

## Table of Contents
1. [Class Basics](#1-class-basics)
2. [Constructors](#2-constructors)
3. [Objects](#3-objects)
4. [Companion Objects](#4-companion-objects)
5. [Case Class](#5-case-class)
6. [Traits](#6-traits)
7. [Inheritance](#7-inheritance)
8. [Abstract Classes](#8-abstract-classes)
9. [Polymorphism](#9-polymorphism)
10. [Access Modifiers](#10-access-modifiers)
11. [Practice Exercises](#11-practice-exercises)

---

## 1. Class Basics

### 1.1 Defining a Class

```scala
// Simplest class
class Person {
  // empty class
}

val person = new Person

// Class with fields
class Person {
  var name: String = ""
  var age: Int = 0
}

val alice = new Person
alice.name = "Alice"
alice.age = 25
```

### 1.2 Primary Constructor

Scala's primary constructor is defined directly after the class name:

```scala
class Person(var name: String, var age: Int) {
  println(s"Creating Person: $name, $age")
}

val alice = new Person("Alice", 25)
// Output: Creating Person: Alice, 25

println(alice.name)  // Alice
alice.age = 26       // can be modified because it is var
```

**Parameter Modifiers:**

```scala
class Person(
  val name: String,      // public, immutable (recommended)
  var age: Int,          // public, mutable
  private val id: String,  // private, immutable
  address: String        // only available in the constructor, not a field
) {
  // address parameter can only be used in the constructor and initializer blocks
  println(s"Address: $address")
  
  // Cannot be used inside methods
  // def printAddress() = println(address)  // Error!
}

val person = new Person("Alice", 25, "001", "Taipei")
person.name          // OK
person.age           // OK
// person.id         // Error! private
// person.address    // Error! not a field
```

### 1.3 Class Body

The class body is the execution content of the primary constructor:

```scala
class Person(val name: String, var age: Int) {
  // Initialization code - executed at construction time
  println(s"Initializing Person: $name")
  
  // Fields
  val createdAt: Long = System.currentTimeMillis()
  var email: String = s"${name.toLowerCase}@example.com"
  
  // Computed field
  def isAdult: Boolean = age >= 18
  
  // Methods
  def greet(): String = s"Hello, I'm $name, $age years old"
  
  def haveBirthday(): Unit = {
    age += 1
    println(s"Happy birthday! Now $age years old")
  }
  
  // Override toString
  override def toString: String = s"Person($name, $age)"
}

val alice = new Person("Alice", 25)
// Output: Initializing Person: Alice

println(alice.greet())
alice.haveBirthday()
```

### 1.4 Getters and Setters

Scala automatically generates getters and setters for fields:

```scala
class Person(private var _name: String) {
  // Custom getter
  def name: String = _name
  
  // Custom setter (note the _= syntax)
  def name_=(newName: String): Unit = {
    if (newName.nonEmpty) {
      _name = newName
    } else {
      throw new IllegalArgumentException("Name cannot be empty")
    }
  }
}

val person = new Person("Alice")
println(person.name)     // calls getter
person.name = "Bob"      // calls setter
// person.name = ""      // throws exception

// More practical example: validating age
class Person(private var _age: Int) {
  def age: Int = _age
  
  def age_=(newAge: Int): Unit = {
    if (newAge >= 0 && newAge <= 150) {
      _age = newAge
    } else {
      throw new IllegalArgumentException("Age must be between 0 and 150")
    }
  }
}
```

---

## 2. Constructors

### 2.1 Primary Constructor

Every class has one primary constructor, defined after the class name:

```scala
class Person(val name: String, var age: Int) {
  // The entire class body is the primary constructor
  println(s"Creating Person: $name, $age")
  
  require(age >= 0, "Age cannot be negative")
  require(name.nonEmpty, "Name cannot be empty")
}
```

**Making the Primary Constructor Private:**

```scala
// Prevent direct instantiation, force use of factory methods on the companion object
class Person private(val name: String, val age: Int)

// val p = new Person("Alice", 25)  // Error! Constructor is private

object Person {
  def apply(name: String, age: Int): Person = {
    if (age >= 0) new Person(name, age)
    else throw new IllegalArgumentException("Age cannot be negative")
  }
}

val person = Person("Alice", 25)  // uses the apply method
```

### 2.2 Auxiliary Constructors

Define auxiliary constructors using the `this` keyword:

```scala
class Person(val name: String, var age: Int) {
  // Auxiliary constructors must call the primary constructor or another auxiliary constructor
  def this(name: String) = {
    this(name, 0)  // calls primary constructor
  }
  
  def this() = {
    this("Unknown")  // calls the auxiliary constructor above
  }
  
  override def toString = s"Person($name, $age)"
}

// Using different constructors
val p1 = new Person("Alice", 25)
val p2 = new Person("Bob")
val p3 = new Person()

println(p1)  // Person(Alice, 25)
println(p2)  // Person(Bob, 0)
println(p3)  // Person(Unknown, 0)
```

**A More Complex Example:**

```scala
class Rectangle(val width: Double, val height: Double) {
  // Square constructor
  def this(side: Double) = {
    this(side, side)
  }
  
  // Default constructor (unit square)
  def this() = {
    this(1.0)
  }
  
  def area: Double = width * height
  def perimeter: Double = 2 * (width + height)
  
  override def toString = s"Rectangle($width x $height)"
}

val rect1 = new Rectangle(5.0, 3.0)    // rectangle
val rect2 = new Rectangle(4.0)          // square
val rect3 = new Rectangle()             // unit square

println(s"$rect1, Area: ${rect1.area}")
println(s"$rect2, Area: ${rect2.area}")
println(s"$rect3, Area: ${rect3.area}")
```

---

## 3. Objects

`object` is Scala's way to implement the singleton pattern.

### 3.1 Singleton Object

```scala
object DatabaseConnection {
  private var connection: String = "Not connected"
  private var connectionCount: Int = 0
  
  def connect(): Unit = {
    connectionCount += 1
    connection = s"Connected (#$connectionCount)"
    println(connection)
  }
  
  def disconnect(): Unit = {
    connection = "Disconnected"
    println(connection)
  }
  
  def status: String = connection
}

// Usage (no new required)
DatabaseConnection.connect()
println(DatabaseConnection.status)
DatabaseConnection.disconnect()

// Only one instance
val db1 = DatabaseConnection
val db2 = DatabaseConnection
println(db1 eq db2)  // true (same instance)
```

### 3.2 Utility Object

```scala
object MathUtils {
  val PI: Double = 3.14159265359
  val E: Double = 2.71828182846
  
  def square(x: Double): Double = x * x
  
  def cube(x: Double): Double = x * x * x
  
  def isPrime(n: Int): Boolean = {
    if (n <= 1) false
    else if (n == 2) true
    else !(2 to math.sqrt(n).toInt).exists(i => n % i == 0)
  }
  
  def gcd(a: Int, b: Int): Int = {
    if (b == 0) a else gcd(b, a % b)
  }
}

// Usage
println(MathUtils.square(5))
println(MathUtils.isPrime(17))
println(MathUtils.gcd(48, 18))
```

### 3.3 Application Object

```scala
@main def myApp(args: String*): Unit = {
  println("Hello, Scala!")
  println(s"Args: ${args.mkString(", ")}")
}

// A Java-compatible main method is also supported
object MyApp {
  def main(args: Array[String]): Unit = {
    println("Hello, Scala!")
  }
}
```

---

## 4. Companion Objects

A Companion Object has the same name as its class and lives in the same file.

### 4.1 Basic Concept

```scala
class BankAccount(val accountNumber: String, private var balance: Double) {
  def deposit(amount: Double): Unit = {
    require(amount > 0, "Amount must be greater than 0")
    balance += amount
  }
  
  def withdraw(amount: Double): Boolean = {
    if (amount > 0 && amount <= balance) {
      balance -= amount
      true
    } else {
      false
    }
  }
  
  def getBalance: Double = balance
  
  override def toString = s"Account($accountNumber, $$${balance})"
}

object BankAccount {
  // Companion object can access the class's private members
  private var accountCounter = 0
  
  // Factory method
  def apply(initialBalance: Double): BankAccount = {
    accountCounter += 1
    new BankAccount(s"ACC${accountCounter.toString.padTo(6, '0')}", initialBalance)
  }
  
  // Create a special type of account
  def createSavingsAccount(initialBalance: Double): BankAccount = {
    val account = apply(initialBalance)
    println(s"Creating savings account: ${account.accountNumber}")
    account
  }
  
  // Utility method: calculate total balance
  def totalBalance(accounts: List[BankAccount]): Double = {
    accounts.map(_.balance).sum
  }
}

// Usage
val account1 = BankAccount(1000.0)  // uses apply
val account2 = BankAccount.createSavingsAccount(5000.0)

account1.deposit(500.0)
account1.withdraw(200.0)

println(account1)
println(account2)
```

### 4.2 The apply Method

The `apply` method allows calling an object like a function:

```scala
class Person(val name: String, val age: Int)

object Person {
  // apply method lets us omit new
  def apply(name: String, age: Int): Person = {
    new Person(name, age)
  }
  
  // Multiple apply methods are allowed (overloading)
  def apply(name: String): Person = {
    new Person(name, 0)
  }
}

val alice = Person("Alice", 25)  // equivalent to Person.apply("Alice", 25)
val bob = Person("Bob")          // equivalent to Person.apply("Bob")
```

### 4.3 The unapply Method (Extractor)

`unapply` is used for pattern matching:

```scala
class Email(val user: String, val domain: String)

object Email {
  def apply(user: String, domain: String): Email = {
    new Email(user, domain)
  }
  
  // unapply is used for destructuring
  def unapply(email: Email): Option[(String, String)] = {
    Some((email.user, email.domain))
  }
  
  // Can also create from a string
  def fromString(emailStr: String): Option[Email] = {
    emailStr.split("@") match {
      case Array(user, domain) => Some(Email(user, domain))
      case _ => None
    }
  }
}

// Usage
val email = Email("alice", "example.com")

// Pattern matching (uses unapply)
email match {
  case Email(user, domain) => println(s"User: $user, Domain: $domain")
}

// Destructuring assignment
val Email(u, d) = email
println(s"$u @ $d")
```

---

## 5. Case Class

Case Class is a special Scala class that automatically provides many useful features.

### 5.1 Basic Usage

```scala
case class Person(name: String, age: Int)

// Automatic features:
// 1. No need for the new keyword
val alice = Person("Alice", 25)

// 2. Automatic toString generation
println(alice)  // Person(Alice,25)

// 3. Automatic equals and hashCode generation
val alice2 = Person("Alice", 25)
println(alice == alice2)  // true (compares by value, not reference)

// 4. Automatic copy method generation
val olderAlice = alice.copy(age = 26)
println(olderAlice)  // Person(Alice,26)

// 5. Supports pattern matching
alice match {
  case Person(name, age) => println(s"$name is $age years old")
}

// 6. Companion object's apply and unapply are automatically generated
```

### 5.2 Case Class Features

```scala
case class Point(x: Double, y: Double) {
  // Methods can be defined
  def distance(other: Point): Double = {
    math.sqrt(math.pow(x - other.x, 2) + math.pow(y - other.y, 2))
  }
  
  // Operator methods
  def +(other: Point): Point = Point(x + other.x, y + other.y)
  def -(other: Point): Point = Point(x - other.x, y - other.y)
  def *(scalar: Double): Point = Point(x * scalar, y * scalar)
}

val p1 = Point(0, 0)
val p2 = Point(3, 4)

println(p1.distance(p2))  // 5.0
println(p1 + p2)          // Point(3.0, 4.0)
println(p2 * 2)           // Point(6.0, 8.0)
```

### 5.3 Copying and Modifying

```scala
case class User(
  id: Int,
  username: String,
  email: String,
  active: Boolean = true
)

val user = User(1, "alice", "alice@example.com")

// Modify a single field
val updatedUser = user.copy(email = "newalice@example.com")

// Modify multiple fields
val deactivatedUser = user.copy(
  email = "old@example.com",
  active = false
)

println(user)            // original object is unchanged
println(updatedUser)
println(deactivatedUser)
```

### 5.4 Nested Case Classes

```scala
case class Address(street: String, city: String, country: String)
case class Person(name: String, age: Int, address: Address)

val alice = Person(
  "Alice",
  25,
  Address("123 Main St", "Taipei", "Taiwan")
)

// Modify nested object
val movedAlice = alice.copy(
  address = alice.address.copy(city = "Tokyo")
)

// Pattern matching on nested structures
alice match {
  case Person(name, _, Address(_, city, "Taiwan")) =>
    println(s"$name lives in $city, Taiwan")
}
```

### 5.5 Case Class vs Regular Class

```scala
// Case Class
case class CasePerson(name: String, age: Int)

// Regular Class
class RegularPerson(val name: String, val age: Int) {
  override def toString: String = s"RegularPerson($name, $age)"
  
  override def equals(obj: Any): Boolean = obj match {
    case p: RegularPerson => name == p.name && age == p.age
    case _ => false
  }
  
  override def hashCode(): Int = (name, age).hashCode()
  
  def copy(name: String = this.name, age: Int = this.age): RegularPerson = {
    new RegularPerson(name, age)
  }
}

// Case Class provides all of the above automatically!
```

**When to Use Case Class:**
- Immutable data models
- Pattern matching is needed
- copy method is needed
- DTO (Data Transfer Object)
- Value objects

**When to Use a Regular Class:**
- Mutable state is needed
- Custom equals/hashCode is needed
- Implementation details must be hidden

---

## 6. Traits

Traits are similar to Java interfaces but can contain implementations.

### 6.1 Basic Trait

```scala
trait Greeter {
  def greet(name: String): String = s"Hello, $name!"
}

class Person(val name: String) extends Greeter

val person = new Person("Alice")
println(person.greet("Bob"))  // "Hello, Bob!"
```

### 6.2 Abstract Members

```scala
trait Animal {
  // Abstract property
  def name: String
  
  // Abstract method
  def makeSound(): String
  
  // Concrete method
  def describe(): String = s"$name says ${makeSound()}"
}

class Dog(val name: String) extends Animal {
  def makeSound(): String = "Woof!"
}

class Cat(val name: String) extends Animal {
  def makeSound(): String = "Meow!"
}

val dog = new Dog("Buddy")
val cat = new Cat("Whiskers")

println(dog.describe())  // "Buddy says Woof!"
println(cat.describe())  // "Whiskers says Meow!"
```

### 6.3 Mixing In Multiple Traits

```scala
trait Swimmer {
  def swim(): String = "Swimming"
}

trait Flyer {
  def fly(): String = "Flying"
}

trait Walker {
  def walk(): String = "Walking"
}

// Mix in multiple traits
class Duck extends Animal with Swimmer with Flyer with Walker {
  val name = "Duck"
  def makeSound() = "Quack!"
}

val duck = new Duck
println(duck.swim())      // "Swimming"
println(duck.fly())       // "Flying"
println(duck.walk())      // "Walking"
println(duck.describe())  // "Duck says Quack!"
```

### 6.4 Trait Initialization Order

```scala
trait A {
  println("A initialized")
  def message = "A"
}

trait B extends A {
  println("B initialized")
  override def message = "B -> " + super.message
}

trait C extends A {
  println("C initialized")
  override def message = "C -> " + super.message
}

class D extends B with C {
  println("D initialized")
  override def message = "D -> " + super.message
}

val d = new D
// Output order:
// A initialized
// B initialized
// C initialized
// D initialized

println(d.message)
// Output: D -> C -> B -> A
// Linearization order: D -> C -> B -> A
```

### 6.5 Self Types

```scala
trait User {
  def username: String
}

trait Tweeter {
  // Self type: requires that any class mixing in this trait must also mix in User
  self: User =>
  
  def tweet(message: String): String = {
    s"$username: $message"
  }
}

// Must extend both User and Tweeter
class TwitterUser(val username: String) extends User with Tweeter

val user = new TwitterUser("alice")
println(user.tweet("Hello, World!"))  // "alice: Hello, World!"

// Error example
// class InvalidTweeter extends Tweeter  // Compile error! User is required
```

### 6.6 Practical Trait Examples

**Logger Trait:**

```scala
trait Logger {
  def log(message: String): Unit = {
    println(s"[${java.time.LocalDateTime.now()}] $message")
  }
}

trait ErrorLogger extends Logger {
  def logError(message: String): Unit = {
    log(s"ERROR: $message")
  }
}

class Application extends Logger with ErrorLogger {
  def start(): Unit = {
    log("Application starting")
  }
  
  def processData(): Unit = {
    try {
      // processing data
      log("Processing data")
    } catch {
      case e: Exception => logError(e.getMessage)
    }
  }
}
```

**Timestamped Trait:**

```scala
trait Timestamped {
  val createdAt: Long = System.currentTimeMillis()
  
  def age: Long = System.currentTimeMillis() - createdAt
}

case class Document(title: String, content: String) extends Timestamped

val doc = Document("Report", "...")
Thread.sleep(1000)
println(s"Document age: ${doc.age} ms")
```

---

## 7. Inheritance

### 7.1 Basic Inheritance

```scala
class Animal(val name: String) {
  def makeSound(): String = "Some sound"
  
  def move(): String = "Moving"
}

class Dog(name: String, val breed: String) extends Animal(name) {
  override def makeSound(): String = "Woof!"
  
  def fetch(): String = s"$name is fetching"
}

val dog = new Dog("Buddy", "Golden Retriever")
println(dog.makeSound())  // "Woof!"
println(dog.move())       // "Moving" (inherited from Animal)
println(dog.fetch())      // "Buddy is fetching"
```

### 7.2 Overriding Methods

```scala
class Shape {
  def area: Double = 0.0
  def perimeter: Double = 0.0
  
  // final methods cannot be overridden
  final def description: String = "This is a shape"
}

class Circle(val radius: Double) extends Shape {
  override def area: Double = math.Pi * radius * radius
  override def perimeter: Double = 2 * math.Pi * radius
  
  // Error! Cannot override a final method
  // override def description: String = "Circle"
}

class Rectangle(val width: Double, val height: Double) extends Shape {
  override def area: Double = width * height
  override def perimeter: Double = 2 * (width + height)
}
```

### 7.3 The super Keyword

```scala
class Employee(val name: String, val id: Int) {
  def info: String = s"Employee: $name (ID: $id)"
}

class Manager(name: String, id: Int, val department: String) 
    extends Employee(name, id) {
  
  override def info: String = {
    super.info + s", Department: $department"
  }
  
  def teamInfo: String = s"Manager of $department"
}

val manager = new Manager("Alice", 123, "Engineering")
println(manager.info)      // "Employee: Alice (ID: 123), Department: Engineering"
println(manager.teamInfo)  // "Manager of Engineering"
```

### 7.4 Type Testing and Casting

```scala
class Animal
class Dog extends Animal
class Cat extends Animal

val animal: Animal = new Dog

// isInstanceOf - type test
if (animal.isInstanceOf[Dog]) {
  println("This is a dog")
}

// asInstanceOf - type cast (unsafe!)
val dog = animal.asInstanceOf[Dog]

// Safer approach: pattern matching
animal match {
  case d: Dog => println("This is a dog")
  case c: Cat => println("This is a cat")
  case _ => println("Unknown animal")
}
```

---

## 8. Abstract Classes

### 8.1 Defining an Abstract Class

```scala
abstract class Animal {
  // Abstract property
  def name: String
  
  // Abstract method
  def makeSound(): String
  
  // Concrete method
  def describe(): String = s"$name says ${makeSound()}"
  
  // Concrete property
  val kingdom: String = "Animalia"
}

class Dog(val name: String) extends Animal {
  def makeSound(): String = "Woof!"
  
  def fetch(): String = s"$name is fetching"
}

// val animal = new Animal  // Error! Cannot instantiate an abstract class
val dog = new Dog("Buddy")
println(dog.describe())
```

### 8.2 Abstract Class vs Trait

**Abstract Class:**
```scala
abstract class Vehicle(val maxSpeed: Int) {  // can have constructor parameters
  def describe: String
}

class Car(maxSpeed: Int, val brand: String) extends Vehicle(maxSpeed) {
  def describe: String = s"$brand car, max speed: $maxSpeed km/h"
}
```

**Trait:**
```scala
trait Drivable {  // cannot have constructor parameters
  def drive(): String
}

trait Flyable {
  def fly(): String
}

// Multiple traits can be mixed in
class FlyingCar extends Vehicle(300) with Drivable with Flyable {
  def describe: String = "Flying car"
  def drive(): String = "Driving on road"
  def fly(): String = "Flying in air"
}
```

**Selection Guide:**
- Need constructor parameters -> use abstract class
- Need multiple inheritance -> use Trait
- Represents "what it is" -> use abstract class
- Represents "what it can do" -> use Trait

### 8.3 Complex Example

```scala
abstract class Document {
  def title: String
  def author: String
  def content: String
  
  def wordCount: Int = content.split("\\s+").length
  
  def summary(maxLength: Int): String = {
    if (content.length <= maxLength) content
    else content.take(maxLength) + "..."
  }
  
  override def toString: String = {
    s"""Document: $title
       |Author: $author
       |Words: $wordCount
       |Preview: ${summary(50)}""".stripMargin
  }
}

class Article(
  val title: String,
  val author: String,
  val content: String,
  val publishedDate: String
) extends Document {
  def isRecent: Boolean = {
    // simplified date check
    publishedDate >= "2024-01-01"
  }
}

class Book(
  val title: String,
  val author: String,
  val content: String,
  val isbn: String,
  val publisher: String
) extends Document {
  def citation: String = {
    s"$author. $title. $publisher. ISBN: $isbn"
  }
}
```

---

## 9. Polymorphism

### 9.1 Subtype Polymorphism

```scala
abstract class Shape {
  def area: Double
  def perimeter: Double
}

class Circle(val radius: Double) extends Shape {
  def area: Double = math.Pi * radius * radius
  def perimeter: Double = 2 * math.Pi * radius
}

class Rectangle(val width: Double, val height: Double) extends Shape {
  def area: Double = width * height
  def perimeter: Double = 2 * (width + height)
}

class Triangle(val a: Double, val b: Double, val c: Double) extends Shape {
  def area: Double = {
    val s = (a + b + c) / 2
    math.sqrt(s * (s - a) * (s - b) * (s - c))
  }
  def perimeter: Double = a + b + c
}

// Polymorphism: use the parent type
def printShapeInfo(shape: Shape): Unit = {
  println(f"Area: ${shape.area}%.2f")
  println(f"Perimeter: ${shape.perimeter}%.2f")
}

val shapes: List[Shape] = List(
  new Circle(5),
  new Rectangle(4, 6),
  new Triangle(3, 4, 5)
)

shapes.foreach(printShapeInfo)

// Calculate total area
val totalArea = shapes.map(_.area).sum
println(f"Total area: $totalArea%.2f")
```

### 9.2 Parametric Polymorphism (Generics)

```scala
class Box[T](val content: T) {
  def get: T = content
  
  def map[U](f: T => U): Box[U] = {
    new Box(f(content))
  }
}

val intBox = new Box(42)
val stringBox = new Box("hello")

println(intBox.get)      // 42
println(stringBox.get)   // "hello"

val doubledBox = intBox.map(_ * 2)
println(doubledBox.get)  // 84
```

**Type Bounds:**

```scala
// Upper Bound: T must be Animal or a subclass of it
class Zoo[T <: Animal](val animal: T) {
  def sound: String = animal.makeSound()
}

// Lower Bound: T must be Dog or a superclass of it
class DogList[T >: Dog](val dogs: List[T])

// Scala 3 explicitly requests Conversion[T, U] with a using parameter
```

### 9.3 Type Variance

**Covariance - `+T`:**

```scala
class Animal
class Dog extends Animal
class Cat extends Animal

// Box is covariant
class Box[+T](val content: T)

val dogBox: Box[Dog] = new Box(new Dog)
val animalBox: Box[Animal] = dogBox  // OK! Covariance allows this

// List is covariant
val dogs: List[Dog] = List(new Dog, new Dog)
val animals: List[Animal] = dogs  // OK!
```

**Contravariance - `-T`:**

```scala
trait Printer[-T] {
  def print(value: T): Unit
}

val animalPrinter: Printer[Animal] = new Printer[Animal] {
  def print(animal: Animal): Unit = println("Animal")
}

// Contravariance: an Animal Printer can be used to handle Dog
val dogPrinter: Printer[Dog] = animalPrinter  // OK!
```

**Invariance:**

```scala
// Invariant by default
class Container[T](var content: T) {
  def get: T = content
  def set(newContent: T): Unit = content = newContent
}

val dogContainer: Container[Dog] = new Container(new Dog)
// val animalContainer: Container[Animal] = dogContainer  // Error! Invariant
```

---

## 10. Access Modifiers

### 10.1 public, private, protected

```scala
class Example {
  // public (default)
  val publicField = "public"
  def publicMethod() = "public"
  
  // private (visible only inside the class)
  private val privateField = "private"
  private def privateMethod() = "private"
  
  // protected (visible in the class and its subclasses)
  protected val protectedField = "protected"
  protected def protectedMethod() = "protected"
  
  def access(): Unit = {
    println(publicField)     // OK
    println(privateField)    // OK
    println(protectedField)  // OK
  }
}

class SubExample extends Example {
  def accessParent(): Unit = {
    println(publicField)      // OK
    // println(privateField)  // Error!
    println(protectedField)   // OK
  }
}

val example = new Example
println(example.publicField)     // OK
// println(example.privateField) // Error!
// println(example.protectedField)  // Error!
```

### 10.2 Scoped Access

```scala
package com.example.app

class User {
  // visible within the app package
  private[app] val internalId = "123"
  
  // visible within the example package
  private[example] val companyId = "ABC"
  
  // visible only inside the User class
  private val secretKey = "XYZ"
}

class Admin extends User {
  def canAccess(): Unit = {
    println(internalId)  // OK
    println(companyId)   // OK
    // println(secretKey)  // Error: private to User
  }
}
```

### 10.3 Object-Private Inference in Scala 3

Scala 3.3.8 deprecates `private[this]`. Use `private`; the compiler infers when a
member is only accessed through `this` and can apply the same optimization.

```scala
class Counter {
  private var count = 0
  
  def increment(): Unit = count += 1
  
  def isGreaterThan(other: Counter): Boolean = {
    count > other.count  // class-private access is allowed
  }
}
```

---

## 11. Practice Exercises

### Exercise 1: Library System

```scala
// Base classes and traits
abstract class LibraryItem(val id: String, val title: String) {
  def itemType: String
  def canBorrow: Boolean = true
  
  override def toString: String = s"$itemType: $title (ID: $id)"
}

trait Borrowable {
  private var _borrower: Option[String] = None
  private var _dueDate: Option[String] = None
  
  def borrow(borrower: String, dueDate: String): Boolean = {
    if (_borrower.isEmpty) {
      _borrower = Some(borrower)
      _dueDate = Some(dueDate)
      true
    } else {
      false
    }
  }
  
  def returnItem(): Unit = {
    _borrower = None
    _dueDate = None
  }
  
  def borrower: Option[String] = _borrower
  def dueDate: Option[String] = _dueDate
  def isAvailable: Boolean = _borrower.isEmpty
}

// Concrete classes
class Book(
  id: String,
  title: String,
  val author: String,
  val isbn: String
) extends LibraryItem(id, title) with Borrowable {
  def itemType: String = "Book"
  
  override def toString: String = {
    val base = super.toString
    val status = if (isAvailable) "Available" else s"Borrowed by ${borrower.get}"
    s"$base by $author - $status"
  }
}

class Magazine(
  id: String,
  title: String,
  val issue: String
) extends LibraryItem(id, title) with Borrowable {
  def itemType: String = "Magazine"
}

class ReferenceBook(
  id: String,
  title: String,
  val subject: String
) extends LibraryItem(id, title) {
  def itemType: String = "Reference Book"
  override def canBorrow: Boolean = false  // Reference books cannot be borrowed
}

// Library class
class Library {
  private var items = List.empty[LibraryItem]
  
  def addItem(item: LibraryItem): Unit = {
    items = items :+ item
    println(s"Added: $item")
  }
  
  def findById(id: String): Option[LibraryItem] = {
    items.find(_.id == id)
  }
  
  def listAvailable(): Unit = {
    println("\n=== Available Items ===")
    items.collect {
      case item: Borrowable if item.isAvailable => item
    }.foreach(println)
  }
  
  def listBorrowed(): Unit = {
    println("\n=== Borrowed Items ===")
    items.collect {
      case item: Borrowable if !item.isAvailable => item
    }.foreach(println)
  }
}

// Test
@main def librarySystem(): Unit = {
  val library = new Library
  
  // Add items
  library.addItem(new Book("B001", "Programming in Scala", "Martin Odersky", "123-456"))
  library.addItem(new Book("B002", "Functional Programming in Scala", "Paul Chiusano", "789-012"))
  library.addItem(new Magazine("M001", "Programmer Monthly", "2024-01"))
  library.addItem(new ReferenceBook("R001", "Scala API Reference", "Programming"))
  
  // Borrow
  library.findById("B001") match {
    case Some(item: Borrowable) =>
      if (item.borrow("Alice", "2024-02-01")) {
        println(s"\n${item.asInstanceOf[LibraryItem].title} borrowed by Alice")
      }
    case _ => println("Cannot borrow")
  }
  
  // List status
  library.listAvailable()
  library.listBorrowed()
}
```

### Exercise 2: Banking System

```scala
// Account base class
abstract class Account(val accountNumber: String, protected var balance: Double) {
  def deposit(amount: Double): Unit = {
    require(amount > 0, "Deposit amount must be greater than 0")
    balance += amount
    println(f"Deposited $$${amount}%.2f, Balance: $$${balance}%.2f")
  }
  
  def withdraw(amount: Double): Boolean
  
  def getBalance: Double = balance
  
  override def toString: String = 
    s"${this.getClass.getSimpleName}($accountNumber, $$${balance}%.2f)"
}

// Checking account (can overdraft)
class CheckingAccount(
  accountNumber: String,
  balance: Double,
  val overdraftLimit: Double
) extends Account(accountNumber, balance) {
  
  def withdraw(amount: Double): Boolean = {
    require(amount > 0, "Withdrawal amount must be greater than 0")
    if (balance - amount >= -overdraftLimit) {
      balance -= amount
      println(f"Withdrew $$${amount}%.2f, Balance: $$${balance}%.2f")
      true
    } else {
      println("Insufficient funds (overdraft limit exceeded)")
      false
    }
  }
}

// Savings account (earns interest)
class SavingsAccount(
  accountNumber: String,
  balance: Double,
  val interestRate: Double
) extends Account(accountNumber, balance) {
  
  def withdraw(amount: Double): Boolean = {
    require(amount > 0, "Withdrawal amount must be greater than 0")
    if (amount <= balance) {
      balance -= amount
      println(f"Withdrew $$${amount}%.2f, Balance: $$${balance}%.2f")
      true
    } else {
      println("Insufficient funds")
      false
    }
  }
  
  def addInterest(): Unit = {
    val interest = balance * interestRate
    balance += interest
    println(f"Interest $$${interest}%.2f added, New balance: $$${balance}%.2f")
  }
}

// Bank customer
case class Customer(id: String, name: String) {
  private var accounts = List.empty[Account]
  
  def addAccount(account: Account): Unit = {
    accounts = accounts :+ account
  }
  
  def getAccounts: List[Account] = accounts
  
  def totalBalance: Double = accounts.map(_.getBalance).sum
}

// Bank
class Bank(val name: String) {
  private var customers = Map.empty[String, Customer]
  private var accountCounter = 0
  
  def createCustomer(id: String, name: String): Customer = {
    val customer = Customer(id, name)
    customers = customers + (id -> customer)
    customer
  }
  
  def createCheckingAccount(customerId: String, initialDeposit: Double): Option[CheckingAccount] = {
    customers.get(customerId).map { customer =>
      accountCounter += 1
      val account = new CheckingAccount(
        s"CHK${accountCounter.toString.padTo(6, '0')}",
        initialDeposit,
        1000.0  // overdraft limit $1000
      )
      customer.addAccount(account)
      account
    }
  }
  
  def createSavingsAccount(customerId: String, initialDeposit: Double): Option[SavingsAccount] = {
    customers.get(customerId).map { customer =>
      accountCounter += 1
      val account = new SavingsAccount(
        s"SAV${accountCounter.toString.padTo(6, '0')}",
        initialDeposit,
        0.02  // 2% interest rate
      )
      customer.addAccount(account)
      account
    }
  }
  
  def transfer(from: Account, to: Account, amount: Double): Boolean = {
    if (from.withdraw(amount)) {
      to.deposit(amount)
      println(s"Transferred $$${amount}%.2f from ${from.accountNumber} to ${to.accountNumber}")
      true
    } else {
      false
    }
  }
}

// Test
@main def bankingSystem(): Unit = {
  val bank = new Bank("MyBank")
  
  // Create customers
  val alice = bank.createCustomer("C001", "Alice")
  val bob = bank.createCustomer("C002", "Bob")
  
  // Create accounts
  val aliceChecking = bank.createCheckingAccount("C001", 1000.0).get
  val aliceSavings = bank.createSavingsAccount("C001", 5000.0).get
  val bobChecking = bank.createCheckingAccount("C002", 2000.0).get
  
  println("\n=== Initial State ===")
  println(aliceChecking)
  println(aliceSavings)
  println(bobChecking)
  
  println("\n=== Transactions ===")
  aliceChecking.deposit(500.0)
  aliceChecking.withdraw(200.0)
  aliceSavings.addInterest()
  
  println("\n=== Transfer ===")
  bank.transfer(aliceChecking, bobChecking, 300.0)
  
  println("\n=== Final State ===")
  println(aliceChecking)
  println(aliceSavings)
  println(bobChecking)
  println(f"Alice total balance: $$${alice.totalBalance}%.2f")
  println(f"Bob total balance: $$${bob.totalBalance}%.2f")
}
```

### Exercise 3: Game Character System

```scala
// Base traits
trait Character {
  def name: String
  var health: Int
  var maxHealth: Int
  
  def isAlive: Boolean = health > 0
  
  def takeDamage(damage: Int): Unit = {
    health = math.max(0, health - damage)
    println(s"$name takes $damage damage, HP remaining: $health/$maxHealth")
    if (!isAlive) println(s"$name has died!")
  }
  
  def heal(amount: Int): Unit = {
    if (isAlive) {
      health = math.min(maxHealth, health + amount)
      println(s"$name heals $amount HP, current HP: $health/$maxHealth")
    }
  }
}

trait Attacker {
  def attack(target: Character): Unit
}

trait MagicUser {
  var mana: Int
  var maxMana: Int
  
  def castSpell(target: Character, manaCost: Int, damage: Int): Boolean = {
    if (mana >= manaCost) {
      mana -= manaCost
      target.takeDamage(damage)
      println(s"Spell cast! Consumed $manaCost mana, remaining: $mana/$maxMana")
      true
    } else {
      println("Not enough mana!")
      false
    }
  }
}

trait Defensive {
  var armor: Int
  
  def calculateDamage(damage: Int): Int = {
    val reduced = math.max(1, damage - armor)
    println(s"Armor absorbs ${damage - reduced} damage")
    reduced
  }
}

// Concrete classes
class Warrior(
  val name: String,
  var health: Int,
  var maxHealth: Int,
  var armor: Int
) extends Character with Attacker with Defensive {
  
  private val attackPower = 15
  
  def attack(target: Character): Unit = {
    println(s"$name attacks ${target.name}!")
    target.takeDamage(attackPower)
  }
  
  override def takeDamage(damage: Int): Unit = {
    super.takeDamage(calculateDamage(damage))
  }
  
  def rage(): Unit = {
    println(s"$name enters a rage!")
    // logic to increase attack power
  }
}

class Mage(
  val name: String,
  var health: Int,
  var maxHealth: Int,
  var mana: Int,
  var maxMana: Int
) extends Character with Attacker with MagicUser {
  
  private val staffDamage = 5
  
  def attack(target: Character): Unit = {
    println(s"$name attacks ${target.name} with a staff!")
    target.takeDamage(staffDamage)
  }
  
  def fireball(target: Character): Boolean = {
    println(s"$name casts Fireball!")
    castSpell(target, 20, 30)
  }
  
  def regenerateMana(amount: Int): Unit = {
    mana = math.min(maxMana, mana + amount)
    println(s"$name regenerates $amount mana, current: $mana/$maxMana")
  }
}

class Healer(
  val name: String,
  var health: Int,
  var maxHealth: Int,
  var mana: Int,
  var maxMana: Int
) extends Character with MagicUser {
  
  def healSpell(target: Character): Boolean = {
    if (mana >= 15) {
      mana -= 15
      target.heal(25)
      println(s"$name casts Heal! Consumed 15 mana")
      true
    } else {
      println("Not enough mana!")
      false
    }
  }
  
  def massHeal(targets: List[Character]): Unit = {
    if (mana >= 30) {
      mana -= 30
      targets.foreach(_.heal(15))
      println(s"$name casts Mass Heal!")
    } else {
      println("Not enough mana!")
    }
  }
}

// Game management
class Battle {
  def simulate(attacker: Character with Attacker, defender: Character): Unit = {
    println(s"\n=== ${attacker.name} vs ${defender.name} ===")
    var round = 1
    
    while (attacker.isAlive && defender.isAlive && round <= 5) {
      println(s"\n--- Round $round ---")
      attacker.attack(defender)
      
      if (defender.isAlive) {
        defender match {
          case d: Attacker => d.attack(attacker)
          case _ =>
        }
      }
      
      round += 1
    }
    
    println("\n=== Battle Over ===")
    if (attacker.isAlive) println(s"${attacker.name} wins!")
    else if (defender.isAlive) println(s"${defender.name} wins!")
    else println("Draw!")
  }
}

// Test
@main def gameSystem(): Unit = {
  val warrior = new Warrior("Warrior", 100, 100, 10)
  val mage = new Mage("Mage", 70, 70, 100, 100)
  val healer = new Healer("Healer", 80, 80, 120, 120)
  
  println("=== Character Status ===")
  println(s"${warrior.name}: HP ${warrior.health}/${warrior.maxHealth}, Armor ${warrior.armor}")
  println(s"${mage.name}: HP ${mage.health}/${mage.maxHealth}, MP ${mage.mana}/${mage.maxMana}")
  println(s"${healer.name}: HP ${healer.health}/${healer.maxHealth}, MP ${healer.mana}/${healer.maxMana}")
  
  println("\n=== Combat Test ===")
  warrior.attack(mage)
  mage.fireball(warrior)
  healer.healSpell(warrior)
  
  println("\n=== Simulated Battle ===")
  val battle = new Battle
  
  // Reset health
  warrior.health = warrior.maxHealth
  mage.health = mage.maxHealth
  mage.mana = mage.maxMana
  
  battle.simulate(warrior, mage)
}
```

### Exercise 4: Shape Hierarchy

```scala
// Abstract base class
abstract class Shape {
  def area: Double
  def perimeter: Double
  
  def describe: String = {
    f"${this.getClass.getSimpleName}: Area = ${area}%.2f, Perimeter = ${perimeter}%.2f"
  }
}

// 2D shape
trait Shape2D {
  def vertices: Int
}

trait Drawable {
  def draw(): String = {
    s"Drawing ${this.getClass.getSimpleName}"
  }
}

trait Scalable {
  def scale(factor: Double): Shape
}

// Concrete shapes
class Circle(val radius: Double) extends Shape with Shape2D with Drawable with Scalable {
  require(radius > 0, "Radius must be greater than 0")
  
  def area: Double = math.Pi * radius * radius
  def perimeter: Double = 2 * math.Pi * radius
  def vertices: Int = 0
  
  def scale(factor: Double): Circle = new Circle(radius * factor)
  
  def diameter: Double = 2 * radius
}

class Rectangle(val width: Double, val height: Double) 
    extends Shape with Shape2D with Drawable with Scalable {
  
  require(width > 0 && height > 0, "Width and height must be greater than 0")
  
  def area: Double = width * height
  def perimeter: Double = 2 * (width + height)
  def vertices: Int = 4
  
  def scale(factor: Double): Rectangle = 
    new Rectangle(width * factor, height * factor)
  
  def isSquare: Boolean = width == height
  def diagonal: Double = math.sqrt(width * width + height * height)
}

class Triangle(val a: Double, val b: Double, val c: Double)
    extends Shape with Shape2D with Drawable with Scalable {
  
  require(a + b > c && b + c > a && a + c > b, "Not a valid triangle")
  
  def area: Double = {
    val s = perimeter / 2
    math.sqrt(s * (s - a) * (s - b) * (s - c))
  }
  
  def perimeter: Double = a + b + c
  def vertices: Int = 3
  
  def scale(factor: Double): Triangle = 
    new Triangle(a * factor, b * factor, c * factor)
  
  def isEquilateral: Boolean = a == b && b == c
  def isIsosceles: Boolean = a == b || b == c || a == c
  def isRight: Boolean = {
    val sides = List(a, b, c).sorted
    math.abs(sides(0) * sides(0) + sides(1) * sides(1) - sides(2) * sides(2)) < 0.0001
  }
}

// Composite shape
class CompositeShape(val shapes: List[Shape]) extends Shape {
  def area: Double = shapes.map(_.area).sum
  def perimeter: Double = shapes.map(_.perimeter).sum
  
  override def describe: String = {
    s"""Composite shape containing ${shapes.length} shapes:
       |${shapes.map(_.describe).mkString("\n")}
       |Total area: ${area}%.2f
       |Total perimeter: ${perimeter}%.2f""".stripMargin
  }
}

// Shape factory
object ShapeFactory {
  def createCircle(radius: Double): Circle = new Circle(radius)
  
  def createSquare(side: Double): Rectangle = new Rectangle(side, side)
  
  def createRectangle(width: Double, height: Double): Rectangle = 
    new Rectangle(width, height)
  
  def createEquilateralTriangle(side: Double): Triangle = 
    new Triangle(side, side, side)
  
  def createRightTriangle(a: Double, b: Double): Triangle = {
    val c = math.sqrt(a * a + b * b)
    new Triangle(a, b, c)
  }
}

// Test
@main def shapeSystem(): Unit = {
  println("=== Creating Shapes ===")
  val circle = ShapeFactory.createCircle(5.0)
  val square = ShapeFactory.createSquare(4.0)
  val rectangle = ShapeFactory.createRectangle(6.0, 3.0)
  val triangle = ShapeFactory.createRightTriangle(3.0, 4.0)
  
  val shapes = List(circle, square, rectangle, triangle)
  
  println("\n=== Shape Info ===")
  shapes.foreach(s => println(s.describe))
  
  println("\n=== Drawable Shapes ===")
  shapes.collect { case d: Drawable => d }.foreach(d => println(d.draw()))
  
  println("\n=== Scale Test ===")
  val scaledCircle = circle.scale(2.0)
  println(s"Original circle: ${circle.describe}")
  println(s"Scaled 2x: ${scaledCircle.describe}")
  
  println("\n=== Composite Shape ===")
  val composite = new CompositeShape(shapes)
  println(composite.describe)
  
  println("\n=== Triangle Type Check ===")
  println(s"Equilateral? ${triangle.isEquilateral}")
  println(s"Isosceles? ${triangle.isIsosceles}")
  println(s"Right-angled? ${triangle.isRight}")
}
```

---

## 12. Key Takeaways

### Classes and Objects
- **Class**: defined with `class`, supports primary and auxiliary constructors
- **Object**: defined with `object` as a singleton
- **Companion Object**: provides factory methods and utility functions
- **Case Class**: automatically generates equals, hashCode, toString, copy

### Traits
- Can contain both abstract and concrete members
- Support multiple mixin composition
- Used to define reusable behaviors

### Inheritance
- Use `extends` to inherit a class or mixin a trait
- Use `override` to override methods
- Use `super` to call parent class methods
- Supports abstract classes

### Polymorphism
- Subtype polymorphism (inheritance)
- Parametric polymorphism (generics)
- Type variance (covariance, contravariance, invariance)

### Access Control
- public (default)
- private, protected
- Scoped access (private[package])

---

## Next Steps

After completing Part 4, you have mastered:
- Classes, objects, and companion objects
- Case Class features and applications
- Trait definition and mixin composition
- Inheritance and polymorphism
- Object-oriented design principles

**Continue learning:**
- [Part 5: Collections](scala_part5_collections.md) - Deep dive into List, Set, Map, and more
- [Part 6: Pattern Matching](scala_part6_pattern_matching.md) - Powerful control flow tools

Ready to continue?

---

> [📚 Table of Contents](../../README.md) | [« Prev: Functions](scala_part3_functions.md) | [Next: Collections »](scala_part5_collections.md)
