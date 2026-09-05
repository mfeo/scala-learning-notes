# Scala Tutorial - Part 7: Error Handling

> [📚 Table of Contents](../../README.md) | [« Prev: Pattern Matching](scala_part6_pattern_matching.md) | [Next: Advanced Topics »](scala_part8_advanced_topics.md)

---

## Table of Contents
1. [Error Handling Overview](#1-error-handling-overview)
2. [The Option Type](#2-the-option-type)
3. [The Either Type](#3-the-either-type)
4. [The Try Type](#4-the-try-type)
5. [Exception Handling](#5-exception-handling)
6. [Error Handling Patterns](#6-error-handling-patterns)
7. [Combining Error Handling](#7-combining-error-handling)
8. [Custom Error Types](#8-custom-error-types)
9. [Validation](#9-validation)
10. [Best Practices](#10-best-practices)
11. [Practice Exercises](#11-practice-exercises)

---

## 1. Error Handling Overview

### 1.1 Comparing Error Handling Approaches

```scala
// ❌ Using null (not recommended)
def findUserBad(id: Int): String = {
  if (id > 0) "User" + id
  else null  // Dangerous!
}

val user = findUserBad(-1)
// user.length  // NullPointerException!

// ❌ Throwing exceptions (not functional)
def findUserException(id: Int): String = {
  if (id > 0) "User" + id
  else throw new IllegalArgumentException("Invalid ID")
}

// ✅ Using Option (recommended)
def findUserOption(id: Int): Option[String] = {
  if (id > 0) Some("User" + id)
  else None
}

// ✅ Using Either (more information)
def findUserEither(id: Int): Either[String, String] = {
  if (id > 0) Right("User" + id)
  else Left("Invalid ID: must be positive")
}

// ✅ Using Try (handling exceptions)
import scala.util.{Try, Success, Failure}

def findUserTry(id: Int): Try[String] = Try {
  if (id > 0) "User" + id
  else throw new IllegalArgumentException("Invalid ID")
}
```

### 1.2 Selection Guide

```scala
// Option: value may or may not exist
// - Lookup operations
// - Optional parameters
// - No error details needed

// Either: error information required
// - Validation
// - Business logic errors
// - Error messages needed

// Try: wrapping code that may throw exceptions
// - Calling Java code
// - I/O operations
// - Parsing operations
```

---

## 2. The Option Type

### 2.1 Basic Usage

```scala
// Creating an Option
val some: Option[Int] = Some(42)
val none: Option[Int] = None

// Creating from a potentially null value
val maybeNull: String = null
val opt: Option[String] = Option(maybeNull)  // None

val notNull: String = "hello"
val opt2: Option[String] = Option(notNull)   // Some("hello")

// Extracting the value
some.get           // 42 - Dangerous! May throw an exception
none.get           // NoSuchElementException!

// Safe extraction
some.getOrElse(0)  // 42
none.getOrElse(0)  // 0

// Checking
some.isDefined     // true
some.isEmpty       // false
none.isDefined     // false
none.isEmpty       // true
```

### 2.2 Option Operations

```scala
val maybeNumber: Option[Int] = Some(42)

// map - transform the value
maybeNumber.map(_ * 2)           // Some(84)
None.map((x: Int) => x * 2)      // None

// flatMap - chaining operations
def divide(a: Int, b: Int): Option[Int] = {
  if (b != 0) Some(a / b) else None
}

val result = maybeNumber.flatMap(n => divide(n, 2))  // Some(21)
val result2 = maybeNumber.flatMap(n => divide(n, 0)) // None

// filter - filtering
maybeNumber.filter(_ > 40)       // Some(42)
maybeNumber.filter(_ > 50)       // None

// foreach - execute when value is present
maybeNumber.foreach(n => println(s"Value: $n"))
None.foreach((n: Int) => println(s"Value: $n"))  // not executed

// fold - provide default behavior
maybeNumber.fold(0)(_ * 2)       // 84
None.fold(0)((x: Int) => x * 2)  // 0

// orElse - provide an alternative Option
maybeNumber.orElse(Some(0))      // Some(42)
None.orElse(Some(0))             // Some(0)
```

### 2.3 Pattern Matching

```scala
def describe(opt: Option[Int]): String = opt match {
  case Some(value) => s"Has value: $value"
  case None => "No value"
}

describe(Some(42))  // "Has value: 42"
describe(None)      // "No value"

// for comprehension
val opt1 = Some(10)
val opt2 = Some(20)

val result = for {
  a <- opt1
  b <- opt2
} yield a + b

println(result)  // Some(30)

// Short-circuit behavior
val result2 = for {
  a <- opt1
  b <- None
  c <- opt2
} yield a + b + c

println(result2)  // None
```

### 2.4 Practical Examples

```scala
// Collection operations
val numbers = List(1, 2, 3, 4, 5)

// find returns Option
val firstEven = numbers.find(_ % 2 == 0)  // Some(2)
val firstNegative = numbers.find(_ < 0)   // None

// headOption, lastOption
List(1, 2, 3).headOption     // Some(1)
List.empty[Int].headOption   // None

// Map lookup
val scores = Map("Alice" -> 95, "Bob" -> 87)

scores.get("Alice")          // Some(95)
scores.get("Charlie")        // None

// Safe string conversion
def toInt(s: String): Option[Int] = {
  try {
    Some(s.toInt)
  } catch {
    case _: NumberFormatException => None
  }
}

toInt("42")   // Some(42)
toInt("abc")  // None

// Or use the method provided by Scala
"42".toIntOption    // Some(42)
"abc".toIntOption   // None

// Chained lookups
case class Address(city: String)
case class Person(name: String, address: Option[Address])
case class Company(name: String, ceo: Option[Person])

def getCeoCity(company: Company): Option[String] = {
  company.ceo.flatMap(_.address).map(_.city)
}

val company1 = Company("TechCorp", Some(Person("Alice", Some(Address("Taipei")))))
val company2 = Company("StartUp", Some(Person("Bob", None)))
val company3 = Company("SmallCo", None)

getCeoCity(company1)  // Some("Taipei")
getCeoCity(company2)  // None
getCeoCity(company3)  // None
```

---

## 3. The Either Type

### 3.1 Basic Usage

```scala
// Either[L, R] - Left represents an error, Right represents success
// Convention: Left is the error, Right is the correct value

def divide(a: Int, b: Int): Either[String, Int] = {
  if (b == 0) Left("Divisor cannot be zero")
  else Right(a / b)
}

val result1 = divide(10, 2)   // Right(5)
val result2 = divide(10, 0)   // Left("Divisor cannot be zero")

// Checking
result1.isRight   // true
result1.isLeft    // false
result2.isRight   // false
result2.isLeft    // true

// Providing an explicit fallback for failure
result1.getOrElse(0) // 5

// Pattern matching
result1 match {
  case Right(value) => println(s"Success: $value")
  case Left(error) => println(s"Error: $error")
}
```

### 3.2 Either Operations

```scala
val right: Either[String, Int] = Right(42)
val left: Either[String, Int] = Left("Error")

// map - only applies to Right
right.map(_ * 2)     // Right(84)
left.map(_ * 2)      // Left("Error")

// flatMap - chaining operations
def increment(n: Int): Either[String, Int] = {
  if (n < 100) Right(n + 1)
  else Left("Number too large")
}

right.flatMap(increment)  // Right(43)
left.flatMap(increment)   // Left("Error")

// fold - handling both cases
val result = right.fold(
  error => s"Failed: $error",
  value => s"Success: $value"
)
// "Success: 42"

// getOrElse
right.getOrElse(0)   // 42
left.getOrElse(0)    // 0

// orElse
right.orElse(Right(0))   // Right(42)
left.orElse(Right(0))    // Right(0)

// swap - exchange Left and Right
right.swap   // Left(42)
left.swap    // Right("Error")
```

### 3.3 For Comprehension

```scala
def validateAge(age: Int): Either[String, Int] = {
  if (age < 0) Left("Age cannot be negative")
  else if (age > 150) Left("Age is unreasonable")
  else Right(age)
}

def validateName(name: String): Either[String, String] = {
  if (name.isEmpty) Left("Name cannot be empty")
  else if (name.length < 2) Left("Name is too short")
  else Right(name)
}

// Combining with for comprehension
def createPerson(name: String, age: Int): Either[String, (String, Int)] = {
  for {
    validName <- validateName(name)
    validAge <- validateAge(age)
  } yield (validName, validAge)
}

createPerson("Alice", 25)   // Right((Alice,25))
createPerson("", 25)        // Left("Name cannot be empty")
createPerson("Alice", -5)   // Left("Age cannot be negative")
createPerson("A", 200)      // Left("Name is too short") - short-circuits
```

### 3.4 Either vs Option

```scala
// Option: only knows present or absent
def findUser(id: Int): Option[String] = {
  if (id > 0) Some(s"User$id")
  else None  // don't know why it failed
}

// Either: knows the reason for failure
def findUserWithReason(id: Int): Either[String, String] = {
  if (id <= 0) Left("ID must be positive")
  else if (id > 1000) Left("ID out of range")
  else Right(s"User$id")
}

// Conversion
val opt: Option[String] = Some("value")
opt.toRight("Default error")      // Right("value")

val none: Option[String] = None
none.toRight("No value")          // Left("No value")

val either: Either[String, Int] = Right(42)
either.toOption              // Some(42)

val leftEither: Either[String, Int] = Left("error")
leftEither.toOption          // None
```

---

## 4. The Try Type

### 4.1 Basic Usage

```scala
import scala.util.{Try, Success, Failure}

// Wrapping code that may throw exceptions
val result1: Try[Int] = Try {
  "42".toInt
}
// Success(42)

val result2: Try[Int] = Try {
  "abc".toInt
}
// Failure(java.lang.NumberFormatException)

// Direct creation
val success: Try[Int] = Success(42)
val failure: Try[Int] = Failure(new Exception("Error"))

// Checking
result1.isSuccess   // true
result1.isFailure   // false
result2.isSuccess   // false
result2.isFailure   // true

// Extracting the value
result1.get         // 42 - unsafe!
result1.getOrElse(0)  // 42
result2.getOrElse(0)  // 0
```

### 4.2 Try Operations

```scala
val tryValue: Try[Int] = Try("42".toInt)

// map - transform the success value
tryValue.map(_ * 2)  // Success(84)

// flatMap - chaining operations
def divide(a: Int, b: Int): Try[Int] = Try {
  if (b == 0) throw new ArithmeticException("Division by zero")
  a / b
}

tryValue.flatMap(n => divide(n, 2))  // Success(21)
tryValue.flatMap(n => divide(n, 0))  // Failure(ArithmeticException)

// filter - filtering
tryValue.filter(_ > 40)  // Success(42)
tryValue.filter(_ > 50)  // Failure(NoSuchElementException)

// recover - recovering from an error
val failed: Try[Int] = Failure(new Exception("Error"))
failed.recover {
  case _: Exception => 0
}
// Success(0)

// recoverWith - returning another Try
failed.recoverWith {
  case _: Exception => Try(42)
}
// Success(42)

// fold - handling both cases
tryValue.fold(
  ex => s"Failed: ${ex.getMessage}",
  value => s"Success: $value"
)
// "Success: 42"

// toOption, toEither
tryValue.toOption  // Some(42)
tryValue.toEither  // Right(42)

val failed2: Try[Int] = Failure(new Exception("error"))
failed2.toOption   // None
failed2.toEither   // Left(java.lang.Exception: error)
```

### 4.3 Practical Examples

```scala
// Safe file reading
import scala.io.Source
import java.io.FileNotFoundException

def readFile(filename: String): Try[String] = Try {
  val source = Source.fromFile(filename)
  try source.mkString finally source.close()
}

readFile("data.txt") match {
  case Success(content) => println(s"File contents: $content")
  case Failure(ex: FileNotFoundException) => println("File not found")
  case Failure(ex) => println(s"Read error: ${ex.getMessage}")
}

// Safe JSON parsing (assuming a JSON library)
def parseJson(json: String): Try[Map[String, Any]] = Try {
  // Assumed parsing logic
  if (json.isEmpty) throw new IllegalArgumentException("Empty JSON")
  Map("result" -> "parsed")
}

// Chained operations
def processData(filename: String): Try[String] = {
  for {
    content <- readFile(filename)
    data <- parseJson(content)
  } yield s"Processing complete: $data"
}

// HTTP request
def httpGet(url: String): Try[String] = Try {
  scala.io.Source.fromURL(url).mkString
}

// Retry logic
def retry[T](n: Int)(fn: => T): Try[T] = {
  Try(fn) recoverWith {
    case _ if n > 1 => retry(n - 1)(fn)
  }
}

retry(3) {
  // An operation that may fail
  if (scala.util.Random.nextBoolean()) "Success"
  else throw new Exception("Failure")
}
```

---

## 5. Exception Handling

### 5.1 try-catch-finally

```scala
// Basic syntax
try {
  val result = 10 / 0
  println(result)
} catch {
  case e: ArithmeticException => println("Division by zero")
  case e: Exception => println(s"Other error: ${e.getMessage}")
} finally {
  println("Cleaning up resources")
}

// Returning a value
val result = try {
  "42".toInt
} catch {
  case _: NumberFormatException => 0
}
// result = 42

// Multiple exceptions
def processFile(filename: String): String = {
  try {
    val source = Source.fromFile(filename)
    try {
      source.mkString
    } finally {
      source.close()
    }
  } catch {
    case _: FileNotFoundException => "File not found"
    case _: IOException => "Read error"
    case e: Exception => s"Unknown error: ${e.getMessage}"
  }
}
```

### 5.2 Custom Exceptions

```scala
// Defining exceptions
class InvalidAgeException(message: String) extends Exception(message)
class InvalidNameException(message: String) extends Exception(message)

// Usage
def validatePerson(name: String, age: Int): Unit = {
  if (name.isEmpty) {
    throw new InvalidNameException("Name cannot be empty")
  }
  if (age < 0 || age > 150) {
    throw new InvalidAgeException(s"Age is unreasonable: $age")
  }
}

try {
  validatePerson("", 25)
} catch {
  case e: InvalidNameException => println(e.getMessage)
  case e: InvalidAgeException => println(e.getMessage)
}
```

### 5.3 Using Try Instead of try-catch

```scala
// ❌ Traditional approach
def parseIntOld(s: String): Int = {
  try {
    s.toInt
  } catch {
    case _: NumberFormatException => 0
  }
}

// ✅ Using Try (more functional)
def parseIntNew(s: String): Try[Int] = Try(s.toInt)

// Usage
parseIntNew("42") match {
  case Success(n) => println(s"Number: $n")
  case Failure(ex) => println(s"Error: ${ex.getMessage}")
}

// Or use getOrElse
val number = parseIntNew("42").getOrElse(0)
```

---

## 6. Error Handling Patterns

### 6.1 Chained Handling

```scala
// Option chain
def getUser(id: Int): Option[String] = 
  if (id > 0) Some(s"User$id") else None

def getEmail(user: String): Option[String] = 
  if (user.nonEmpty) Some(s"$user@example.com") else None

def sendEmail(email: String): Option[String] = 
  if (email.contains("@")) Some(s"Sent to $email") else None

// Combining
val result = for {
  user <- getUser(1)
  email <- getEmail(user)
  message <- sendEmail(email)
} yield message

println(result)  // Some("Sent to User1@example.com")

// Either chain
def validateUserId(id: Int): Either[String, Int] = 
  if (id > 0) Right(id) else Left("Invalid ID")

def validateUserExists(id: Int): Either[String, String] = 
  if (id <= 100) Right(s"User$id") else Left("User does not exist")

def validateUserActive(user: String): Either[String, String] = 
  if (!user.endsWith("0")) Right(user) else Left("User is not active")

// Combining
val result2 = for {
  id <- validateUserId(50)
  user <- validateUserExists(id)
  activeUser <- validateUserActive(user)
} yield activeUser

println(result2)  // Right("User50")
```

### 6.2 Accumulating Errors

```scala
// Using Either you can only get the first error
// To accumulate all errors, use a custom type

type ValidationResult[T] = Either[List[String], T]

def validateName(name: String): ValidationResult[String] = {
  if (name.isEmpty) Left(List("Name cannot be empty"))
  else if (name.length < 2) Left(List("Name is too short"))
  else Right(name)
}

def validateAge(age: Int): ValidationResult[Int] = {
  if (age < 0) Left(List("Age cannot be negative"))
  else if (age > 150) Left(List("Age is too large"))
  else Right(age)
}

def validateEmail(email: String): ValidationResult[String] = {
  if (!email.contains("@")) Left(List("Invalid email format"))
  else Right(email)
}

// Manually accumulating errors
case class Person(name: String, age: Int, email: String)

def createPerson(name: String, age: Int, email: String): ValidationResult[Person] = {
  val nameResult = validateName(name)
  val ageResult = validateAge(age)
  val emailResult = validateEmail(email)
  
  (nameResult, ageResult, emailResult) match {
    case (Right(n), Right(a), Right(e)) => 
      Right(Person(n, a, e))
    case _ =>
      val errors = List(nameResult, ageResult, emailResult).collect {
        case Left(errs) => errs
      }.flatten
      Left(errors)
  }
}

createPerson("", -5, "invalid")
// Left(List("Name cannot be empty", "Age cannot be negative", "Invalid email format"))
```

### 6.3 Graceful Degradation

```scala
// Providing multiple fallback options
def getPriceFromPrimaryDB(id: Int): Option[Double] = {
  // May fail
  None
}

def getPriceFromCache(id: Int): Option[Double] = {
  Some(99.99)
}

def getDefaultPrice(id: Int): Double = {
  0.0
}

def getPrice(id: Int): Double = {
  getPriceFromPrimaryDB(id)
    .orElse(getPriceFromCache(id))
    .getOrElse(getDefaultPrice(id))
}

println(getPrice(1))  // 99.99 (retrieved from cache)
```

---

## 7. Combining Error Handling

### 7.1 Converting Between Option, Either, and Try

```scala
// Option → Either
val opt: Option[Int] = Some(42)
opt.toRight("No value")  // Right(42)

val none: Option[Int] = None
none.toRight("No value")  // Left("No value")

// Option → Try
opt.fold(
  Failure(new NoSuchElementException("No value")): Try[Int]
)(Success(_))

// Either → Option
val either: Either[String, Int] = Right(42)
either.toOption  // Some(42)

val left: Either[String, Int] = Left("Error")
left.toOption  // None

// Either → Try
either.fold(
  error => Failure(new Exception(error)),
  Success(_)
)

// Try → Option
val tryValue: Try[Int] = Success(42)
tryValue.toOption  // Some(42)

val failure: Try[Int] = Failure(new Exception("Error"))
failure.toOption  // None

// Try → Either
tryValue.toEither  // Right(42)
failure.toEither   // Left(Exception)
```

### 7.2 Mixed Usage

```scala
import scala.util.{Try, Success, Failure}

// Real-world scenario: user registration
case class User(id: Int, name: String, email: String)

// Different validations return different types
def findExistingUser(email: String): Option[User] = {
  // Query the database
  None
}

def validateEmail(email: String): Either[String, String] = {
  if (email.contains("@")) Right(email)
  else Left("Invalid email format")
}

def saveUser(user: User): Try[User] = Try {
  // Database operation that may throw exceptions
  user
}

// Combining different error handling types
def registerUser(name: String, email: String): Either[String, User] = {
  for {
    validEmail <- validateEmail(email)
    _ <- findExistingUser(validEmail) match {
      case Some(_) => Left("Email is already in use")
      case None => Right(())
    }
    user = User(1, name, validEmail)
    savedUser <- saveUser(user).toEither.left.map(_.getMessage)
  } yield savedUser
}

registerUser("Alice", "alice@example.com") match {
  case Right(user) => println(s"Registration successful: $user")
  case Left(error) => println(s"Registration failed: $error")
}
```

---

## 8. Custom Error Types

### 8.1 ADT Error Types

```scala
// Defining errors using sealed trait
sealed trait UserError
case class UserNotFound(id: Int) extends UserError
case class InvalidEmail(email: String) extends UserError
case class DuplicateUser(email: String) extends UserError
case class DatabaseError(message: String) extends UserError

// Usage
def findUser(id: Int): Either[UserError, String] = {
  if (id <= 0) Left(UserNotFound(id))
  else Right(s"User$id")
}

def validateUserEmail(email: String): Either[UserError, String] = {
  if (!email.contains("@")) Left(InvalidEmail(email))
  else Right(email)
}

// Handling
def processUser(id: Int): String = {
  findUser(id) match {
    case Right(user) => s"User found: $user"
    case Left(UserNotFound(id)) => s"User $id not found"
    case Left(InvalidEmail(email)) => s"Email $email is invalid"
    case Left(DuplicateUser(email)) => s"Email $email is already in use"
    case Left(DatabaseError(msg)) => s"Database error: $msg"
  }
}
```

### 8.2 Error Hierarchies

```scala
// Layered errors
sealed trait AppError
sealed trait ValidationError extends AppError
sealed trait DatabaseError extends AppError
sealed trait NetworkError extends AppError

case class InvalidInput(field: String, message: String) extends ValidationError
case class MissingField(field: String) extends ValidationError

case class RecordNotFound(id: Int) extends DatabaseError
case class ConnectionFailed(message: String) extends DatabaseError

case class Timeout(seconds: Int) extends NetworkError
case class ServerError(code: Int) extends NetworkError

// Usage
def validateInput(input: String): Either[ValidationError, String] = {
  if (input.isEmpty) Left(MissingField("input"))
  else if (input.length < 3) Left(InvalidInput("input", "Too short"))
  else Right(input)
}

def saveToDatabase(data: String): Either[DatabaseError, Unit] = {
  // Simulated database operation
  Right(())
}

def processRequest(input: String): Either[AppError, String] = {
  for {
    valid <- validateInput(input)
    _ <- saveToDatabase(valid)
  } yield s"Processing complete: $valid"
}

// Centralized error handling
def handleError(error: AppError): String = error match {
  case InvalidInput(field, msg) => s"Field $field is invalid: $msg"
  case MissingField(field) => s"Missing field: $field"
  case RecordNotFound(id) => s"Record $id not found"
  case ConnectionFailed(msg) => s"Connection failed: $msg"
  case Timeout(sec) => s"Timed out ($sec seconds)"
  case ServerError(code) => s"Server error: $code"
}
```

---

## 9. Validation

### 9.1 Simple Validation

```scala
case class User(name: String, age: Int, email: String)

object UserValidator {
  type ValidationResult[T] = Either[List[String], T]
  
  private def validateName(name: String): ValidationResult[String] = {
    if (name.isEmpty) Left(List("Name cannot be empty"))
    else if (name.length < 2) Left(List("Name must be at least 2 characters"))
    else if (name.length > 50) Left(List("Name must be at most 50 characters"))
    else Right(name)
  }
  
  private def validateAge(age: Int): ValidationResult[Int] = {
    if (age < 0) Left(List("Age cannot be negative"))
    else if (age > 150) Left(List("Age cannot exceed 150"))
    else Right(age)
  }
  
  private def validateEmail(email: String): ValidationResult[String] = {
    val emailRegex = """^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""".r
    if (!emailRegex.matches(email)) Left(List("Invalid email format"))
    else Right(email)
  }
  
  def validate(name: String, age: Int, email: String): ValidationResult[User] = {
    val nameResult = validateName(name)
    val ageResult = validateAge(age)
    val emailResult = validateEmail(email)
    
    (nameResult, ageResult, emailResult) match {
      case (Right(n), Right(a), Right(e)) => Right(User(n, a, e))
      case _ =>
        val errors = List(nameResult, ageResult, emailResult).collect {
          case Left(errs) => errs
        }.flatten
        Left(errors)
    }
  }
}

// Testing
UserValidator.validate("Alice", 25, "alice@example.com") match {
  case Right(user) => println(s"Valid user: $user")
  case Left(errors) => println(s"Validation failed:\n${errors.mkString("\n")}")
}

UserValidator.validate("", -5, "invalid") match {
  case Right(user) => println(s"Valid user: $user")
  case Left(errors) => println(s"Validation failed:\n${errors.mkString("\n")}")
}
// Validation failed:
// Name cannot be empty
// Age cannot be negative
// Invalid email format
```

### 9.2 Composable Validators

```scala
// Validator type
type Validator[T] = T => Either[List[String], T]

// Basic validators
def nonEmpty(fieldName: String): Validator[String] = { value =>
  if (value.isEmpty) Left(List(s"$fieldName cannot be empty"))
  else Right(value)
}

def minLength(fieldName: String, min: Int): Validator[String] = { value =>
  if (value.length < min) Left(List(s"$fieldName must be at least $min characters"))
  else Right(value)
}

def maxLength(fieldName: String, max: Int): Validator[String] = { value =>
  if (value.length > max) Left(List(s"$fieldName must be at most $max characters"))
  else Right(value)
}

def range(fieldName: String, min: Int, max: Int): Validator[Int] = { value =>
  if (value < min || value > max) Left(List(s"$fieldName must be between $min and $max"))
  else Right(value)
}

// Combining validators
def combine[T](validators: Validator[T]*): Validator[T] = { value =>
  val results = validators.map(_(value))
  val errors = results.collect { case Left(errs) => errs }.flatten.toList
  
  if (errors.isEmpty) Right(value)
  else Left(errors)
}

// Usage
val nameValidator = combine(
  nonEmpty("Name"),
  minLength("Name", 2),
  maxLength("Name", 50)
)

val ageValidator = range("Age", 0, 150)

nameValidator("Alice")  // Right("Alice")
nameValidator("")       // Left(List("Name cannot be empty", "Name must be at least 2 characters"))
ageValidator(25)        // Right(25)
ageValidator(200)       // Left(List("Age must be between 0 and 150"))
```

---

## 10. Best Practices

### 10.1 When to Use Which Error Handling Approach

```scala
// ✅ Use Option: value may not exist, but it's not an error
def findFirst[T](list: List[T], predicate: T => Boolean): Option[T] = {
  list.find(predicate)
}

// ✅ Use Either: error information required
def divide(a: Int, b: Int): Either[String, Int] = {
  if (b == 0) Left("Divisor cannot be zero")
  else Right(a / b)
}

// ✅ Use Try: wrapping code that may throw exceptions
def parseJson(json: String): Try[Map[String, Any]] = Try {
  // Parsing logic that may throw exceptions
  Map("parsed" -> true)
}

// ❌ Avoid: returning null
def badFind(id: Int): String = {
  if (id > 0) s"User$id" else null
}

// ❌ Avoid: throwing business logic exceptions
def badValidate(age: Int): Int = {
  if (age < 0) throw new IllegalArgumentException("Invalid age")
  age
}
```

### 10.2 Error Handling Principles

```scala
// 1. Prefer Option/Either/Try
// 2. Don't swallow exceptions
// 3. Provide meaningful error messages
// 4. Handle errors at boundaries
// 5. Use types to express possible errors

// ❌ Bad
def process(data: String): String = {
  try {
    // Complex processing
    data.toUpperCase
  } catch {
    case _: Exception => ""  // Swallowing the exception!
  }
}

// ✅ Good
def processGood(data: String): Either[String, String] = {
  Try(data.toUpperCase).toEither.left.map(_.getMessage)
}

// ✅ Handle errors at boundaries
class UserService {
  // Use Either internally
  private def validateUser(name: String): Either[String, String] = {
    if (name.nonEmpty) Right(name) else Left("Invalid name")
  }
  
  // Public API can throw exceptions or return Try
  def createUser(name: String): Try[String] = {
    validateUser(name) match {
      case Right(validName) => Success(s"User: $validName")
      case Left(error) => Failure(new IllegalArgumentException(error))
    }
  }
}
```

### 10.3 Avoiding Excessive Nesting

```scala
// ❌ Bad: deeply nested
def badProcess(id: Int): Option[String] = {
  getUser(id) match {
    case Some(user) =>
      getEmail(user) match {
        case Some(email) =>
          sendEmail(email) match {
            case Some(result) => Some(result)
            case None => None
          }
        case None => None
      }
    case None => None
  }
}

// ✅ Good: using for comprehension
def goodProcess(id: Int): Option[String] = {
  for {
    user <- getUser(id)
    email <- getEmail(user)
    result <- sendEmail(email)
  } yield result
}

// ✅ Good: using flatMap chain
def goodProcess2(id: Int): Option[String] = {
  getUser(id)
    .flatMap(getEmail)
    .flatMap(sendEmail)
}

def getUser(id: Int): Option[String] = Some(s"User$id")
def getEmail(user: String): Option[String] = Some(s"$user@example.com")
def sendEmail(email: String): Option[String] = Some(s"Sent to $email")
```

---

## 11. Practice Exercises

### Exercise 1: Configuration File Parser

```scala
import scala.util.{Try, Success, Failure}
import scala.io.Source

case class Config(
  host: String,
  port: Int,
  timeout: Int,
  debug: Boolean
)

object ConfigParser {
  sealed trait ConfigError
  case class FileNotFound(filename: String) extends ConfigError
  case class ParseError(line: Int, message: String) extends ConfigError
  case class ValidationError(field: String, message: String) extends ConfigError
  case class MissingField(field: String) extends ConfigError
  
  type Result[T] = Either[ConfigError, T]
  
  def parseFile(filename: String): Result[Map[String, String]] = {
    Try {
      val source = Source.fromFile(filename)
      try {
        source.getLines().zipWithIndex.foldLeft(Map.empty[String, String]) {
          case (acc, (line, idx)) =>
            if (line.trim.isEmpty || line.trim.startsWith("#")) {
              acc
            } else {
              line.split("=", 2) match {
                case Array(key, value) => acc + (key.trim -> value.trim)
                case _ => throw new Exception(s"Invalid line format: $line")
              }
            }
        }
      } finally {
        source.close()
      }
    }.toEither.left.map {
      case _: java.io.FileNotFoundException => FileNotFound(filename)
      case ex => ParseError(0, ex.getMessage)
    }
  }
  
  def getString(map: Map[String, String], key: String): Result[String] = {
    map.get(key).toRight(MissingField(key))
  }
  
  def getInt(map: Map[String, String], key: String): Result[Int] = {
    for {
      value <- getString(map, key)
      int <- value.toIntOption.toRight(
        ValidationError(key, s"Cannot parse as integer: $value")
      )
    } yield int
  }
  
  def getBoolean(map: Map[String, String], key: String): Result[Boolean] = {
    for {
      value <- getString(map, key)
      bool <- value.toLowerCase match {
        case "true" | "yes" | "1" => Right(true)
        case "false" | "no" | "0" => Right(false)
        case _ => Left(ValidationError(key, s"Cannot parse as boolean: $value"))
      }
    } yield bool
  }
  
  def parseConfig(filename: String): Result[Config] = {
    for {
      map <- parseFile(filename)
      host <- getString(map, "host")
      port <- getInt(map, "port")
      timeout <- getInt(map, "timeout")
      debug <- getBoolean(map, "debug")
      
      // Validation
      _ <- if (port > 0 && port < 65536) Right(())
           else Left(ValidationError("port", "Port must be between 1 and 65535"))
      _ <- if (timeout > 0) Right(())
           else Left(ValidationError("timeout", "Timeout must be greater than 0"))
    } yield Config(host, port, timeout, debug)
  }
  
  def errorMessage(error: ConfigError): String = error match {
    case FileNotFound(filename) => s"File not found: $filename"
    case ParseError(line, msg) => s"Parse error (line $line): $msg"
    case ValidationError(field, msg) => s"Validation error ($field): $msg"
    case MissingField(field) => s"Missing required field: $field"
  }
  
  // Testing
  def main(args: Array[String]): Unit = {
    // Create a test configuration file
    val configContent = """# Server Configuration
      |host = localhost
      |port = 8080
      |timeout = 30
      |debug = true
      |""".stripMargin
    
    val file = new java.io.PrintWriter("test-config.txt")
    try file.write(configContent) finally file.close()
    
    parseConfig("test-config.txt") match {
      case Right(config) => 
        println(s"Configuration loaded successfully: $config")
      case Left(error) => 
        println(s"Configuration load failed: ${errorMessage(error)}")
    }
    
    // Cleanup
    new java.io.File("test-config.txt").delete()
  }
}
```

### Exercise 2: User Registration System

```scala
import java.util.UUID
import scala.util.matching.Regex

case class User(
  id: String,
  username: String,
  email: String,
  age: Int
)

object UserRegistration {
  // Error types
  sealed trait RegistrationError
  case class InvalidUsername(message: String) extends RegistrationError
  case class InvalidEmail(message: String) extends RegistrationError
  case class InvalidAge(message: String) extends RegistrationError
  case class DuplicateUsername(username: String) extends RegistrationError
  case class DuplicateEmail(email: String) extends RegistrationError
  case class DatabaseError(message: String) extends RegistrationError
  
  type Result[T] = Either[RegistrationError, T]
  
  // Simulated database
  private var users = List.empty[User]
  
  // Validators
  object Validators {
    private val emailRegex: Regex = 
      """^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""".r
    
    def validateUsername(username: String): Result[String] = {
      if (username.isEmpty) 
        Left(InvalidUsername("Username cannot be empty"))
      else if (username.length < 3)
        Left(InvalidUsername("Username must be at least 3 characters"))
      else if (username.length > 20)
        Left(InvalidUsername("Username must be at most 20 characters"))
      else if (!username.matches("[a-zA-Z0-9_]+"))
        Left(InvalidUsername("Username can only contain letters, numbers and underscores"))
      else
        Right(username)
    }
    
    def validateEmail(email: String): Result[String] = {
      if (email.isEmpty)
        Left(InvalidEmail("Email cannot be empty"))
      else if (!emailRegex.matches(email))
        Left(InvalidEmail("Invalid email format"))
      else
        Right(email)
    }
    
    def validateAge(age: Int): Result[Int] = {
      if (age < 13)
        Left(InvalidAge("Age must be at least 13"))
      else if (age > 120)
        Left(InvalidAge("Age cannot exceed 120"))
      else
        Right(age)
    }
  }
  
  // Uniqueness checks
  def checkUsernameUnique(username: String): Result[String] = {
    if (users.exists(_.username == username))
      Left(DuplicateUsername(username))
    else
      Right(username)
  }
  
  def checkEmailUnique(email: String): Result[String] = {
    if (users.exists(_.email == email))
      Left(DuplicateEmail(email))
    else
      Right(email)
  }
  
  // Registration
  def register(username: String, email: String, age: Int): Result[User] = {
    for {
      validUsername <- Validators.validateUsername(username)
      validEmail <- Validators.validateEmail(email)
      validAge <- Validators.validateAge(age)
      
      uniqueUsername <- checkUsernameUnique(validUsername)
      uniqueEmail <- checkEmailUnique(validEmail)
      
      user = User(UUID.randomUUID().toString, uniqueUsername, uniqueEmail, validAge)
      _ = users = users :+ user
    } yield user
  }
  
  // List all users
  def listUsers(): List[User] = users
  
  // Find a user
  def findByUsername(username: String): Option[User] = {
    users.find(_.username == username)
  }
  
  // Error messages
  def errorMessage(error: RegistrationError): String = error match {
    case InvalidUsername(msg) => s"Username error: $msg"
    case InvalidEmail(msg) => s"Email error: $msg"
    case InvalidAge(msg) => s"Age error: $msg"
    case DuplicateUsername(username) => s"Username '$username' is already taken"
    case DuplicateEmail(email) => s"Email '$email' is already in use"
    case DatabaseError(msg) => s"Database error: $msg"
  }
  
  // Testing
  def main(args: Array[String]): Unit = {
    println("=== User Registration System ===\n")
    
    // Successful registration
    register("alice", "alice@example.com", 25) match {
      case Right(user) => println(s"✓ Registration successful: ${user.username}")
      case Left(error) => println(s"✗ ${errorMessage(error)}")
    }
    
    // Duplicate username
    register("alice", "alice2@example.com", 30) match {
      case Right(user) => println(s"✓ Registration successful: ${user.username}")
      case Left(error) => println(s"✗ ${errorMessage(error)}")
    }
    
    // Invalid email
    register("bob", "invalid-email", 28) match {
      case Right(user) => println(s"✓ Registration successful: ${user.username}")
      case Left(error) => println(s"✗ ${errorMessage(error)}")
    }
    
    // Age too young
    register("charlie", "charlie@example.com", 10) match {
      case Right(user) => println(s"✓ Registration successful: ${user.username}")
      case Left(error) => println(s"✗ ${errorMessage(error)}")
    }
    
    // Successfully register multiple users
    register("bob", "bob@example.com", 28)
    register("charlie", "charlie@example.com", 22)
    
    println("\n=== All Users ===")
    listUsers().foreach(u => println(s"${u.username} (${u.email}, age ${u.age})"))
  }
}
```

### Exercise 3: Safe Calculator

```scala
import scala.util.{Try, Success, Failure}

object SafeCalculator {
  sealed trait CalcError
  case object DivisionByZero extends CalcError
  case object InvalidNumber extends CalcError
  case class InvalidOperation(op: String) extends CalcError
  case class ParseError(message: String) extends CalcError
  
  type Result[T] = Either[CalcError, T]
  
  // Basic operations
  def add(a: Double, b: Double): Result[Double] = Right(a + b)
  
  def subtract(a: Double, b: Double): Result[Double] = Right(a - b)
  
  def multiply(a: Double, b: Double): Result[Double] = Right(a * b)
  
  def divide(a: Double, b: Double): Result[Double] = {
    if (b == 0) Left(DivisionByZero)
    else Right(a / b)
  }
  
  def power(base: Double, exp: Double): Result[Double] = {
    Try(math.pow(base, exp)).toOption
      .toRight(InvalidOperation("power"))
  }
  
  def squareRoot(n: Double): Result[Double] = {
    if (n < 0) Left(InvalidOperation("sqrt of negative"))
    else Right(math.sqrt(n))
  }
  
  // Parse a number
  def parseNumber(s: String): Result[Double] = {
    s.toDoubleOption.toRight(InvalidNumber)
  }
  
  // Execute an operation
  def execute(op: String, a: Double, b: Double): Result[Double] = op match {
    case "+" => add(a, b)
    case "-" => subtract(a, b)
    case "*" => multiply(a, b)
    case "/" => divide(a, b)
    case "^" => power(a, b)
    case _ => Left(InvalidOperation(op))
  }
  
  // Parse and execute an expression (simple version: "number operator number")
  def evaluate(expr: String): Result[Double] = {
    val parts = expr.trim.split("\\s+")
    
    parts match {
      case Array(a, op, b) =>
        for {
          numA <- parseNumber(a)
          numB <- parseNumber(b)
          result <- execute(op, numA, numB)
        } yield result
      
      case Array("sqrt", n) =>
        for {
          num <- parseNumber(n)
          result <- squareRoot(num)
        } yield result
      
      case _ =>
        Left(ParseError(s"Cannot parse expression: $expr"))
    }
  }
  
  // Error messages
  def errorMessage(error: CalcError): String = error match {
    case DivisionByZero => "Error: Divisor cannot be zero"
    case InvalidNumber => "Error: Invalid number"
    case InvalidOperation(op) => s"Error: Invalid operation '$op'"
    case ParseError(msg) => s"Parse error: $msg"
  }
  
  // REPL
  def repl(): Unit = {
    println("Simple Calculator")
    println("Supported operations: + - * / ^ sqrt")
    println("Example: 10 + 5")
    println("         sqrt 16")
    println("Type 'quit' to exit\n")
    
    var running = true
    
    while (running) {
      print("> ")
      val input = scala.io.StdIn.readLine()
      
      if (input != null) {
        input.trim.toLowerCase match {
          case "quit" | "exit" => running = false
          case expr if expr.nonEmpty =>
            evaluate(expr) match {
              case Right(result) => println(s"= $result")
              case Left(error) => println(errorMessage(error))
            }
          case _ => // Empty line, ignore
        }
      }
    }
    
    println("Goodbye!")
  }
  
  // Testing
  def main(args: Array[String]): Unit = {
    val tests = List(
      "10 + 5",
      "20 - 8",
      "6 * 7",
      "15 / 3",
      "2 ^ 8",
      "sqrt 16",
      "10 / 0",
      "abc + 5",
      "10 % 3"
    )
    
    println("=== Calculator Tests ===\n")
    tests.foreach { expr =>
      evaluate(expr) match {
        case Right(result) => println(f"$expr%-15s = $result%.2f")
        case Left(error) => println(f"$expr%-15s -> ${errorMessage(error)}")
      }
    }
    
    println("\nStarting REPL mode...")
    repl()
  }
}
```

### Exercise 4: Data Validation Framework

```scala
object ValidationFramework {
  // Validation result
  sealed trait ValidationResult[+A]
  case class Valid[A](value: A) extends ValidationResult[A]
  case class Invalid(errors: List[String]) extends ValidationResult[Nothing]
  
  // Validator
  trait Validator[T] {
    def validate(value: T): ValidationResult[T]
    
    // Combining validators
    def and(other: Validator[T]): Validator[T] = { value =>
      (this.validate(value), other.validate(value)) match {
        case (Valid(v), Valid(_)) => Valid(v)
        case (Invalid(e1), Invalid(e2)) => Invalid(e1 ++ e2)
        case (Invalid(e), _) => Invalid(e)
        case (_, Invalid(e)) => Invalid(e)
      }
    }
    
    // Transformation
    def map[U](f: T => U): Validator[U] = { value =>
      this.validate(value) match {
        case Valid(v) => Valid(f(v))
        case Invalid(e) => Invalid(e)
      }
    }
  }
  
  // Basic validators
  object Validators {
    def nonEmpty(fieldName: String): Validator[String] = { value =>
      if (value.isEmpty) Invalid(List(s"$fieldName cannot be empty"))
      else Valid(value)
    }
    
    def minLength(fieldName: String, min: Int): Validator[String] = { value =>
      if (value.length < min) 
        Invalid(List(s"$fieldName requires at least $min characters"))
      else Valid(value)
    }
    
    def maxLength(fieldName: String, max: Int): Validator[String] = { value =>
      if (value.length > max) 
        Invalid(List(s"$fieldName must be at most $max characters"))
      else Valid(value)
    }
    
    def matches(fieldName: String, regex: String): Validator[String] = { value =>
      if (!value.matches(regex)) 
        Invalid(List(s"$fieldName format is incorrect"))
      else Valid(value)
    }
    
    def min(fieldName: String, minValue: Int): Validator[Int] = { value =>
      if (value < minValue) 
        Invalid(List(s"$fieldName must be at least $minValue"))
      else Valid(value)
    }
    
    def max(fieldName: String, maxValue: Int): Validator[Int] = { value =>
      if (value > maxValue) 
        Invalid(List(s"$fieldName must be at most $maxValue"))
      else Valid(value)
    }
    
    def range(fieldName: String, minValue: Int, maxValue: Int): Validator[Int] = {
      min(fieldName, minValue).and(max(fieldName, maxValue))
    }
    
    def email(fieldName: String): Validator[String] = {
      matches(fieldName, """^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""")
    }
  }
  
  // Example usage
  case class SignUpForm(
    username: String,
    email: String,
    password: String,
    age: Int
  )
  
  object SignUpFormValidator {
    import Validators.*
    
    val usernameValidator: Validator[String] = 
      nonEmpty("Username")
        .and(minLength("Username", 3))
        .and(maxLength("Username", 20))
        .and(matches("Username", "[a-zA-Z0-9_]+"))
    
    val emailValidator: Validator[String] = 
      nonEmpty("Email")
        .and(email("Email"))
    
    val passwordValidator: Validator[String] = 
      nonEmpty("Password")
        .and(minLength("Password", 8))
        .and(maxLength("Password", 100))
    
    val ageValidator: Validator[Int] = 
      range("Age", 13, 120)
    
    def validate(form: SignUpForm): ValidationResult[SignUpForm] = {
      val usernameResult = usernameValidator.validate(form.username)
      val emailResult = emailValidator.validate(form.email)
      val passwordResult = passwordValidator.validate(form.password)
      val ageResult = ageValidator.validate(form.age)
      
      (usernameResult, emailResult, passwordResult, ageResult) match {
        case (Valid(_), Valid(_), Valid(_), Valid(_)) => 
          Valid(form)
        case _ =>
          val errors = List(usernameResult, emailResult, passwordResult, ageResult)
            .collect { case Invalid(e) => e }
            .flatten
          Invalid(errors)
      }
    }
  }
  
  // Testing
  def main(args: Array[String]): Unit = {
    val validForm = SignUpForm("alice", "alice@example.com", "password123", 25)
    val invalidForm = SignUpForm("ab", "invalid", "123", 10)
    
    println("=== Valid Form ===")
    SignUpFormValidator.validate(validForm) match {
      case Valid(form) => println(s"✓ Validation passed: $form")
      case Invalid(errors) => 
        println("✗ Validation failed:")
        errors.foreach(e => println(s"  - $e"))
    }
    
    println("\n=== Invalid Form ===")
    SignUpFormValidator.validate(invalidForm) match {
      case Valid(form) => println(s"✓ Validation passed: $form")
      case Invalid(errors) => 
        println("✗ Validation failed:")
        errors.foreach(e => println(s"  - $e"))
    }
  }
}
```

### Exercise 5: API Client

```scala
import scala.util.{Try, Success, Failure}

object ApiClient {
  // Error types
  sealed trait ApiError
  case class NetworkError(message: String) extends ApiError
  case class ParseError(message: String) extends ApiError
  case class ServerError(code: Int, message: String) extends ApiError
  case class ClientError(code: Int, message: String) extends ApiError
  case class Timeout(seconds: Int) extends ApiError
  
  type ApiResult[T] = Either[ApiError, T]
  
  // HTTP response
  case class Response(code: Int, body: String)
  
  // Simulated HTTP request
  def httpGet(url: String): Try[Response] = Try {
    // Simulated network delay
    Thread.sleep(100)
    
    // Simulated different responses
    url match {
      case u if u.contains("users") => 
        Response(200, """{"users": [{"id": 1, "name": "Alice"}]}""")
      case u if u.contains("error") => 
        Response(500, """{"error": "Internal Server Error"}""")
      case u if u.contains("notfound") => 
        Response(404, """{"error": "Not Found"}""")
      case _ => 
        throw new java.net.ConnectException("Connection failed")
    }
  }
  
  // Handle response
  def handleResponse(response: Response): ApiResult[String] = {
    response.code match {
      case code if code >= 200 && code < 300 => 
        Right(response.body)
      case code if code >= 400 && code < 500 => 
        Left(ClientError(code, response.body))
      case code if code >= 500 => 
        Left(ServerError(code, response.body))
      case code => 
        Left(ClientError(code, "Unknown status code"))
    }
  }
  
  // API call
  def get(url: String): ApiResult[String] = {
    httpGet(url) match {
      case Success(response) => handleResponse(response)
      case Failure(ex: java.net.ConnectException) => 
        Left(NetworkError(s"Connection failed: ${ex.getMessage}"))
      case Failure(ex: java.net.SocketTimeoutException) => 
        Left(Timeout(30))
      case Failure(ex) => 
        Left(NetworkError(ex.getMessage))
    }
  }
  
  // Retry mechanism
  def getWithRetry(url: String, maxRetries: Int = 3): ApiResult[String] = {
    def attempt(remaining: Int): ApiResult[String] = {
      get(url) match {
        case Right(result) => Right(result)
        case Left(NetworkError(_)) if remaining > 1 =>
          println(s"Retrying... (${ remaining - 1 } attempts remaining)")
          Thread.sleep(1000)
          attempt(remaining - 1)
        case Left(error) => Left(error)
      }
    }
    attempt(maxRetries)
  }
  
  // Parse JSON (simplified)
  def parseJson(json: String): ApiResult[Map[String, Any]] = {
    Try {
      // A real JSON library should be used here
      // Simple demonstration
      if (json.contains("users")) {
        Map("users" -> List(Map("id" -> 1, "name" -> "Alice")))
      } else {
        Map.empty[String, Any]
      }
    }.toEither.left.map(ex => ParseError(ex.getMessage))
  }
  
  // Complete flow
  def fetchUsers(baseUrl: String): ApiResult[List[String]] = {
    for {
      body <- getWithRetry(s"$baseUrl/users")
      data <- parseJson(body)
      users <- data.get("users") match {
        case Some(list: List[?]) => Right(list.map(_.toString))
        case _ => Left(ParseError("Cannot parse user list"))
      }
    } yield users
  }
  
  // Error handling
  def handleError(error: ApiError): String = error match {
    case NetworkError(msg) => s"Network error: $msg"
    case ParseError(msg) => s"Parse error: $msg"
    case ServerError(code, msg) => s"Server error ($code): $msg"
    case ClientError(code, msg) => s"Client error ($code): $msg"
    case Timeout(sec) => s"Request timed out ($sec seconds)"
  }
  
  // Testing
  def main(args: Array[String]): Unit = {
    println("=== API Client Tests ===\n")
    
    val testUrls = List(
      ("Successful request", "http://api.example.com/users"),
      ("404 error", "http://api.example.com/notfound"),
      ("Server error", "http://api.example.com/error"),
      ("Network error", "http://api.example.com/timeout")
    )
    
    testUrls.foreach { case (name, url) =>
      println(s"$name: $url")
      get(url) match {
        case Right(body) => println(s"  ✓ Success: ${body.take(50)}...")
        case Left(error) => println(s"  ✗ ${handleError(error)}")
      }
      println()
    }
  }
}
```

---

## 12. Key Summary

### Error Handling Types
- **Option**: value may or may not exist
- **Either**: error information required (Left=error, Right=success)
- **Try**: wrapping code that may throw exceptions

### Key Operations
- **map**: transform the success value
- **flatMap**: chaining operations
- **fold**: handling both cases
- **getOrElse**: providing a default value
- **recover**: recovering from an error

### Best Practices
- Prefer Option/Either/Try
- Don't swallow exceptions
- Use for comprehensions to combine
- Handle errors at boundaries
- Use types to express errors

### Selection Guide
- Lookup operations → Option
- Validation logic → Either
- Wrapping exceptions → Try
- Avoid using null and throwing business exceptions

---

## Next Steps

After completing Part 7, you have mastered:
- ✅ Usage of Option, Either, and Try
- ✅ Best practices for error handling
- ✅ Combining and converting errors
- ✅ Custom error types
- ✅ Validation framework design

**The Scala core tutorial is complete!**

You have learned:
1. Basic syntax and types
2. Functional programming
3. Object-oriented programming
4. Collection operations
5. Pattern matching
6. Error handling

Suggested next steps:
- Deep dive into [Contextual Abstractions and Type Classes](scala_part8_advanced_topics.md)
- Implement a complete project
- Learn the Scala ecosystem (Akka, Play, Cats)

Congratulations on completing the Scala core tutorial!

---

> [📚 Table of Contents](../../README.md) | [« Prev: Pattern Matching](scala_part6_pattern_matching.md) | [Next: Advanced Topics »](scala_part8_advanced_topics.md)
