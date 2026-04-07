# Scala Tutorial - Part 1: Introduction to Scala and Environment Setup

> [📚 Table of Contents](../../README.md) | [Next: Basic Syntax »](scala_part2_basic_syntax.md)

---

## Table of Contents
1. [What is Scala?](#1-what-is-scala)
2. [Scala Features and Advantages](#2-scala-features-and-advantages)
3. [Environment Installation](#3-environment-installation)
4. [Development Tool Choices](#4-development-tool-choices)
5. [Your First Scala Program](#5-your-first-scala-program)
6. [Project Structure and Build Tools](#6-project-structure-and-build-tools)
7. [REPL Interactive Environment](#7-repl-interactive-environment)

---

## 1. What is Scala?

Scala (Scalable Language) is a modern multi-paradigm programming language, designed in 2004 by Professor Martin Odersky at the École Polytechnique Fédérale de Lausanne (EPFL) in Switzerland.

### 1.1 Core Philosophy

Scala combines two major programming paradigms:

**Object-Oriented Programming (OOP)**
- Everything is an object
- Supports classes, inheritance, and polymorphism
- Powerful type system

**Functional Programming (FP)**
- Functions are first-class citizens
- Immutability
- Higher-order functions
- Pattern matching

### 1.2 Why "Scalable"?

The name Scala comes from "Scalable Language", meaning it is able to:
- Scale from small scripts to large systems
- Go from simple prototypes to enterprise-level applications
- Adapt to development needs of different scales

### 1.3 Application Domains of Scala

**Big Data Processing**
- Apache Spark (the most well-known Scala application)
- Apache Kafka
- Apache Flink

**Backend Development**
- Play Framework (web framework)
- Akka (concurrent and distributed systems)
- Backend services at companies like Twitter and LinkedIn

**Financial Technology**
- High-frequency trading systems
- Risk management systems

---

## 2. Scala Features and Advantages

### 2.1 Key Features

#### Full Interoperability with Java
```scala
// Java class libraries can be used directly
import java.util.Date
import java.io.File

val now = new Date()
val file = new File("/path/to/file")
```

#### Static Type System + Type Inference
```scala
// Type inference means you don't need to specify types explicitly
val name = "Alice"  // automatically inferred as String
val age = 25        // automatically inferred as Int

// But you can still specify types explicitly
val greeting: String = "Hello"
val count: Int = 10
```

#### Concise Syntax
```scala
// Java style
public class Person {
    private final String name;
    private int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() {
        return name;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
}

// Scala style (one line!)
case class Person(name: String, var age: Int)
```

#### Functional Programming Support
```scala
// Higher-order functions
val numbers = List(1, 2, 3, 4, 5)
val doubled = numbers.map(_ * 2)

// Immutability
val immutableList = List(1, 2, 3)
// immutableList will never change

// Pattern matching
def describe(x: Any) = x match {
  case i: Int => s"Integer: $i"
  case s: String => s"String: $s"
  case _ => "Other"
}
```

### 2.2 Advantage Analysis

**Pros**

✅ **Expressive**: Code for the same functionality is typically 1/3 to 1/2 the size of Java

✅ **Type-safe**: Many errors can be caught at compile time

✅ **Concurrency**: Immutability makes writing concurrent programs easier

✅ **Excellent performance**: Compiles to JVM bytecode, with performance close to Java

✅ **Ecosystem**: All Java class libraries are available

**Cons**

❌ **Learning curve**: Steeper than Java, especially functional programming concepts

❌ **Compilation speed**: Slower than Java

❌ **Binary compatibility**: There may be compatibility issues between different versions

---

## 3. Environment Installation

### 3.1 Prerequisites

Scala runs on the JVM, so Java must be installed first.

**Install JDK**

Check if already installed:
```bash
java -version
```

If not installed, it is recommended to use OpenJDK 11 or a higher version:

**macOS (using Homebrew)**
```bash
brew install openjdk@11
```

**Ubuntu/Debian**
```bash
sudo apt update
sudo apt install openjdk-11-jdk
```

**Windows**
Download and install from [AdoptOpenJDK](https://adoptopenjdk.net/)

### 3.2 Installing Scala

#### Method 1: Using SDKMAN (Recommended)

SDKMAN is a version management tool for the JVM ecosystem, supporting macOS, Linux, and Windows (WSL).

**1. Install SDKMAN**
```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
```

**2. Verify installation**
```bash
sdk version
```

**3. Install Scala**
```bash
# Install the latest version
sdk install scala

# Or specify a version
sdk install scala 3.3.1
```

**4. Install sbt (Scala Build Tool)**
```bash
sdk install sbt
```

**5. Verify installation**
```bash
scala -version
sbt --version
```

#### Method 2: Using Coursier (New Recommended Method)

Coursier is the officially recommended Scala installation tool.

**macOS/Linux**
```bash
curl -fL https://github.com/coursier/launchers/raw/master/cs-x86_64-pc-linux.gz | gzip -d > cs
chmod +x cs
./cs setup
```

**Windows**
Download the installer from the [Coursier website](https://get-coursier.io/docs/cli-installation)

Running it will automatically install:
- JDK
- Scala
- sbt
- Other commonly used tools

#### Method 3: Manual Installation

**macOS (using Homebrew)**
```bash
brew install scala
brew install sbt
```

**Ubuntu/Debian**
```bash
sudo apt install scala
echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list
curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo apt-key add
sudo apt update
sudo apt install sbt
```

**Windows**
Download the installer from the [Scala website](https://www.scala-lang.org/download/)

### 3.3 Verifying the Installation

```bash
# Check Java
java -version
# Should see something like: openjdk version "11.0.x"

# Check Scala
scala -version
# Should see something like: Scala code runner version 3.3.1

# Check sbt
sbt --version
# Should see something like: sbt version in this project: 1.9.7
```

---

## 4. Development Tool Choices

### 4.1 IntelliJ IDEA (Most Recommended)

**Pros:**
- Professional Scala support
- Powerful debugging features
- Auto-completion and refactoring tools
- Integrated sbt

**Installation Steps:**

1. Download [IntelliJ IDEA](https://www.jetbrains.com/idea/download/)
   - Community Edition (free)
   - Ultimate Edition (paid, but more complete features)

2. Install the Scala plugin
   - Open IntelliJ IDEA
   - File → Settings (or Preferences)
   - Plugins → Marketplace
   - Search for "Scala"
   - Install the "Scala" plugin
   - Restart the IDE

3. Create a new project
   - File → New → Project
   - Select "Scala" and "sbt"
   - Set the project name and location
   - Select JDK and Scala version

### 4.2 Visual Studio Code

**Pros:**
- Lightweight
- Free and open source
- Cross-platform

**Installation Steps:**

1. Download [VS Code](https://code.visualstudio.com/)

2. Install the Metals extension
   - Open VS Code
   - Press Ctrl+Shift+X (or Cmd+Shift+X)
   - Search for "Scala (Metals)"
   - Install

3. Optional: Install other useful extensions
   - "Scala Syntax (official)"
   - "GitLens"

### 4.3 Other Options

**Sublime Text + LSP**
- Lightweight but fewer features

**Vim/Neovim + CoC**
- Suitable for developers who prefer a terminal environment

**Eclipse + Scala IDE**
- An older choice, not particularly recommended

---

## 5. Your First Scala Program

### 5.1 Using the REPL

The Scala REPL (Read-Eval-Print Loop) is an interactive environment suitable for quickly testing code.

**Start the REPL:**
```bash
scala
```

**Basic operations:**
```scala
// Type in the REPL
scala> println("Hello, Scala!")
Hello, Scala!

scala> val x = 42
val x: Int = 42

scala> x * 2
val res0: Int = 84

scala> def greet(name: String) = s"Hello, $name!"
def greet(name: String): String

scala> greet("World")
val res1: String = Hello, World!

// Exit the REPL
scala> :quit
```

### 5.2 Creating a Script File

**Create the file hello.scala:**
```scala
// hello.scala
object Hello {
  def main(args: Array[String]): Unit = {
    println("Hello, Scala!")
    println(s"You passed in ${args.length} argument(s)")
    args.foreach(arg => println(s"  - $arg"))
  }
}
```

**Run the script:**
```bash
scala hello.scala
# Output: Hello, Scala!
#         You passed in 0 argument(s)

scala hello.scala Alice Bob
# Output: Hello, Scala!
#         You passed in 2 argument(s)
#           - Alice
#           - Bob
```

### 5.3 Using the App Trait (Simplified Version)

```scala
// hello_app.scala
object HelloApp extends App {
  println("Hello from App!")
  println(s"Arguments: ${args.mkString(", ")}")
}
```

**Run:**
```bash
scala hello_app.scala arg1 arg2
```

### 5.4 Compile and Run

**Compile:**
```bash
scalac hello.scala
# Generates Hello.class and Hello$.class
```

**Run the compiled program:**
```bash
scala Hello
# Or using Java
java Hello
```

---

## 6. Project Structure and Build Tools

### 6.1 Creating a Project with sbt

sbt (Scala Build Tool) is the standard build tool for Scala.

**Quickly create a project:**
```bash
# Use the official template
sbt new scala/scala-seed.g8
# It will ask for a project name, e.g. enter: my-first-project
```

**Generated project structure:**
```
my-first-project/
├── build.sbt              # Build configuration file
├── project/
│   └── build.properties   # sbt version configuration
└── src/
    ├── main/
    │   └── scala/
    │       └── example/
    │           └── Hello.scala
    └── test/
        └── scala/
            └── example/
                └── HelloSpec.scala
```

### 6.2 The build.sbt Configuration File

**Basic build.sbt:**
```scala
// build.sbt
name := "my-first-project"

version := "0.1.0"

scalaVersion := "3.3.1"

// Dependencies
libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.2.17",
  "org.scalatest" %% "scalatest" % "3.2.17" % "test"
)
```

**Configuration key explanations:**
- `name`: Project name
- `version`: Version number
- `scalaVersion`: Scala version
- `libraryDependencies`: External library dependencies

### 6.3 Common sbt Commands

**Enter the sbt shell:**
```bash
sbt
```

**Inside the sbt shell:**
```sbt
# Compile the project
compile

# Run the program
run

# Run tests
test

# Continuous compilation (automatically recompiles when files change)
~compile

# Clean build output
clean

# Reload build.sbt
reload

# Show project dependencies
libraryDependencies

# Exit sbt
exit
```

**Run directly (without entering the shell):**
```bash
sbt compile
sbt run
sbt test
```

### 6.4 Project File Descriptions

**src/main/scala/example/Hello.scala:**
```scala
package example

object Hello extends App {
  println("Hello, Scala!")
  
  def greet(name: String): String = {
    s"Hello, $name!"
  }
}
```

**Project structure best practices:**
```
my-project/
├── build.sbt
├── project/
│   ├── build.properties
│   └── plugins.sbt         # sbt plugin configuration
├── src/
│   ├── main/
│   │   ├── scala/
│   │   │   └── com/
│   │   │       └── mycompany/
│   │   │           ├── Main.scala
│   │   │           ├── models/
│   │   │           ├── services/
│   │   │           └── utils/
│   │   └── resources/      # Configuration files, resource files
│   └── test/
│       ├── scala/
│       │   └── com/
│       │       └── mycompany/
│       │           └── MainSpec.scala
│       └── resources/
├── target/                  # Compiled output (auto-generated)
└── README.md
```

### 6.5 Adding External Dependencies

**Add to build.sbt:**
```scala
libraryDependencies ++= Seq(
  // JSON processing
  "io.circe" %% "circe-core" % "0.14.6",
  "io.circe" %% "circe-generic" % "0.14.6",
  "io.circe" %% "circe-parser" % "0.14.6",
  
  // HTTP client
  "com.softwaremill.sttp.client3" %% "core" % "3.9.1",
  
  // Testing framework
  "org.scalatest" %% "scalatest" % "3.2.17" % Test
)
```

**Dependency format explanation:**
```scala
"group-id" %% "artifact-id" % "version" % "configuration"

// %% automatically appends the Scala version number
// For example, under Scala 3.3.1:
"org.scalatest" %% "scalatest" % "3.2.17"
// Will actually download: org.scalatest:scalatest_3:3.2.17

// % does not append the Scala version number (used for Java libraries)
"mysql" % "mysql-connector-java" % "8.0.33"
```

---

## 7. REPL Interactive Environment

### 7.1 Basic REPL Usage

**Start the REPL:**
```bash
scala
```

**Basic operation examples:**
```scala
scala> 1 + 1
val res0: Int = 2

scala> res0 * 5
val res1: Int = 10

scala> val name = "Alice"
val name: String = Alice

scala> s"Hello, $name!"
val res2: String = Hello, Alice!
```

### 7.2 REPL Special Commands

```scala
// Check the type of a variable
scala> :type name
String

// Check the type of the last result
scala> :type res2
String

// Load an external file
scala> :load myfile.scala

// Show command history
scala> :history

// Paste multi-line code mode
scala> :paste
// Entering paste mode (ctrl-D to finish)

def factorial(n: Int): Int = {
  if (n <= 1) 1
  else n * factorial(n - 1)
}

// Press Ctrl+D to finish

scala> factorial(5)
val res3: Int = 120

// Reset the REPL (clears all definitions)
scala> :reset

// Exit the REPL
scala> :quit
```

### 7.3 Using Libraries in the REPL

**Method 1: Using Ammonite (Enhanced REPL)**

Install Ammonite:
```bash
sudo sh -c '(echo "#!/usr/bin/env sh" && curl -L https://github.com/com-lihaoyi/Ammonite/releases/download/2.5.11/2.13-2.5.11) > /usr/local/bin/amm && chmod +x /usr/local/bin/amm'
```

Using Ammonite:
```scala
// Start
amm

// Load a library
import $ivy.`com.lihaoyi::requests:0.8.0`

// Use it
val response = requests.get("https://api.github.com")
println(response.text())
```

**Method 2: Using scala-cli**

Install scala-cli:
```bash
curl -sSLf https://scala-cli.virtuslab.org/get | sh
```

Usage:
```scala
// Create the file script.sc
//> using lib "com.lihaoyi::requests:0.8.0"

import requests._

val response = get("https://api.github.com")
println(response.text())
```

Run:
```bash
scala-cli script.sc
```

### 7.4 Useful REPL Tips

**Auto-completion:**
```scala
scala> val list = List(1, 2, 3)
scala> list.ma<TAB>  // Press Tab to show all methods starting with "ma"
```

**View method documentation:**
```scala
scala> :help map
// Or in some REPLs
scala> list.map _
```

**Configure the REPL environment:**

Create `~/.scala/repl-config.scala`:
```scala
// Automatically import commonly used packages
import scala.collection.mutable
import scala.util.{Try, Success, Failure}

// Set a custom prompt
scala.tools.nsc.interpreter.replProps.prompt.set("scala>>> ")

// Custom functions
def time[T](block: => T): T = {
  val start = System.nanoTime()
  val result = block
  val end = System.nanoTime()
  println(s"Execution time: ${(end - start) / 1000000.0} ms")
  result
}
```

---

## 8. Practice Exercises

### Exercise 1: Hello World Variations

Create a program that greets the user based on the current time:

```scala
// TimeGreeting.scala
import java.time.LocalTime

object TimeGreeting extends App {
  val hour = LocalTime.now().getHour
  
  val greeting = hour match {
    case h if h >= 5 && h < 12 => "Good morning"
    case h if h >= 12 && h < 18 => "Good afternoon"
    case h if h >= 18 && h < 22 => "Good evening"
    case _ => "It's late"
  }
  
  println(s"$greeting, welcome to Scala!")
}
```

### Exercise 2: Simple Calculator

```scala
// Calculator.scala
object Calculator extends App {
  def calculate(a: Double, b: Double, op: String): Double = {
    op match {
      case "+" => a + b
      case "-" => a - b
      case "*" => a * b
      case "/" if b != 0 => a / b
      case "/" => throw new ArithmeticException("Divisor cannot be zero")
      case _ => throw new IllegalArgumentException("Unsupported operator")
    }
  }
  
  // Tests
  println(calculate(10, 5, "+"))  // 15.0
  println(calculate(10, 5, "-"))  // 5.0
  println(calculate(10, 5, "*"))  // 50.0
  println(calculate(10, 5, "/"))  // 2.0
}
```

### Exercise 3: Creating a Project with sbt

1. Create a new project
2. Add the ScalaTest dependency
3. Write simple tests

```bash
# Create the project
sbt new scala/scala-seed.g8
# Project name: calculator-project
```

**Modify build.sbt:**
```scala
name := "calculator-project"
version := "0.1.0"
scalaVersion := "3.3.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17" % Test
```

**src/main/scala/Calculator.scala:**
```scala
object Calculator {
  def add(a: Int, b: Int): Int = a + b
  def subtract(a: Int, b: Int): Int = a - b
  def multiply(a: Int, b: Int): Int = a * b
  def divide(a: Int, b: Int): Option[Int] = {
    if (b != 0) Some(a / b) else None
  }
}
```

**src/test/scala/CalculatorSpec.scala:**
```scala
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CalculatorSpec extends AnyFlatSpec with Matchers {
  "Calculator" should "add two numbers correctly" in {
    Calculator.add(2, 3) shouldBe 5
  }
  
  it should "subtract two numbers correctly" in {
    Calculator.subtract(5, 3) shouldBe 2
  }
  
  it should "multiply two numbers correctly" in {
    Calculator.multiply(3, 4) shouldBe 12
  }
  
  it should "divide two numbers correctly" in {
    Calculator.divide(10, 2) shouldBe Some(5)
  }
  
  it should "handle division by zero" in {
    Calculator.divide(10, 0) shouldBe None
  }
}
```

**Run the tests:**
```bash
sbt test
```

---

## 9. Frequently Asked Questions

### Q1: Scala 2 vs Scala 3 — which one should I learn?

**Recommendation: Learn Scala 3**

- Scala 3 is the future direction
- Cleaner syntax
- More powerful type system
- However, there is still a lot of Scala 2 code out there, and the two have good interoperability

### Q2: What should I do if sbt compilation is too slow?

**Solutions:**

1. Use the sbt shell instead of restarting sbt each time
```bash
# Enter the shell, then use ~compile
sbt
> ~compile
```

2. Increase JVM memory
```bash
# Set environment variable
export SBT_OPTS="-Xmx2G -XX:+UseConcMarkSweepGC"
```

3. Use incremental compilation in Metals (IDE)

### Q3: How do I use Java libraries in Scala?

Simply add the dependency:

```scala
// build.sbt
libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.33"

// Use it in code
import java.sql.DriverManager

val conn = DriverManager.getConnection(url, user, password)
```

### Q4: Can definitions in the REPL be saved?

You can use the `:save` command:

```scala
scala> :save mycode.scala
```

Or use Ammonite's `repl.sess.save("session.sc")`

---

## 10. Next Steps

Congratulations on completing Part 1! You now:
- ✅ Understand Scala's features and advantages
- ✅ Have successfully set up the development environment
- ✅ Know how to use the REPL and sbt
- ✅ Have created and run your first Scala program

**What to learn next:**
1. **[Part 2: Basic Syntax](scala_part2_basic_syntax.md)** - Variables, data types, control flow
2. **[Part 3: Functions and Methods](scala_part3_functions.md)** - Functional programming fundamentals
3. **[Part 4: Object-Oriented Programming](scala_part4_oop.md)** - Classes, objects, inheritance

**Recommended Resources:**
- Official documentation: [docs.scala-lang.org](https://docs.scala-lang.org)
- Scala exercises: [scala-exercises.org](https://www.scala-exercises.org)
- Online REPL: [scastie.scala-lang.org](https://scastie.scala-lang.org)

Ready? Let's continue to Part 2!

---

> [📚 Table of Contents](../../README.md) | [Next: Basic Syntax »](scala_part2_basic_syntax.md)
