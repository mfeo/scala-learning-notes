# Scala 教學 - 第四部分：Object-Oriented Programming（物件導向程式設計）

> [« 上一篇：函數與方法](scala_part3_functions.md) | [📚 目錄](../README.md) | [下一篇：集合操作 »](scala_part5_collections.md)

---

## 目錄
1. [Class 基礎](#1-class類別基礎)
2. [Constructor](#2-constructor建構子)
3. [Object](#3-object物件)
4. [Companion Object](#4-companion-object伴生物件)
5. [Case Class](#5-case-class案例類別)
6. [Trait](#6-trait特徵)
7. [Inheritance](#7-inheritance繼承)
8. [Abstract Class](#8-abstract-class抽象類別)
9. [Polymorphism](#9-polymorphism多型)
10. [Access Modifier](#10-access-modifier存取修飾符)
11. [實作練習](#11-實作練習)

---

## 1. Class（類別）基礎

### 1.1 定義類別

```scala
// 最簡單的類別
class Person {
  // 空類別
}

val person = new Person

// 帶欄位的類別
class Person {
  var name: String = ""
  var age: Int = 0
}

val alice = new Person
alice.name = "Alice"
alice.age = 25
```

### 1.2 主建構子

Scala 的主建構子直接定義在類別名稱後面:

```scala
class Person(var name: String, var age: Int) {
  println(s"建立 Person: $name, $age")
}

val alice = new Person("Alice", 25)
// 輸出: 建立 Person: Alice, 25

println(alice.name)  // Alice
alice.age = 26       // 可以修改,因為是 var
```

**參數修飾符:**

```scala
class Person(
  val name: String,      // public, immutable (推薦)
  var age: Int,          // public, mutable
  private val id: String,  // private, immutable
  address: String        // 只在建構子中可用,不是欄位
) {
  // address 參數只能在建構子和初始化區塊中使用
  println(s"住址: $address")
  
  // 無法在方法中使用 address
  // def printAddress() = println(address)  // 錯誤!
}

val person = new Person("Alice", 25, "001", "台北")
person.name          // OK
person.age           // OK
// person.id         // 錯誤! private
// person.address    // 錯誤! 不是欄位
```

### 1.3 類別主體

類別主體就是主建構子的執行內容:

```scala
class Person(val name: String, var age: Int) {
  // 初始化程式碼 - 在建構時執行
  println(s"初始化 Person: $name")
  
  // 欄位
  val createdAt: Long = System.currentTimeMillis()
  var email: String = s"${name.toLowerCase}@example.com"
  
  // 計算欄位
  def isAdult: Boolean = age >= 18
  
  // 方法
  def greet(): String = s"Hello, I'm $name, $age years old"
  
  def haveBirthday(): Unit = {
    age += 1
    println(s"Happy birthday! Now $age years old")
  }
  
  // 覆寫 toString
  override def toString: String = s"Person($name, $age)"
}

val alice = new Person("Alice", 25)
// 輸出: 初始化 Person: Alice

println(alice.greet())
alice.haveBirthday()
```

### 1.4 Getter 和 Setter

Scala 自動為欄位生成 getter 和 setter:

```scala
class Person(private var _name: String) {
  // 自訂 getter
  def name: String = _name
  
  // 自訂 setter (注意 _= 語法)
  def name_=(newName: String): Unit = {
    if (newName.nonEmpty) {
      _name = newName
    } else {
      throw new IllegalArgumentException("名稱不能為空")
    }
  }
}

val person = new Person("Alice")
println(person.name)     // 呼叫 getter
person.name = "Bob"      // 呼叫 setter
// person.name = ""      // 拋出異常

// 更實用的範例:驗證年齡
class Person(private var _age: Int) {
  def age: Int = _age
  
  def age_=(newAge: Int): Unit = {
    if (newAge >= 0 && newAge <= 150) {
      _age = newAge
    } else {
      throw new IllegalArgumentException("年齡必須在 0-150 之間")
    }
  }
}
```

---

## 2. Constructor（建構子）

### 2.1 主建構子

每個類別都有一個主建構子,定義在類別名稱後:

```scala
class Person(val name: String, var age: Int) {
  // 這整個類別主體就是主建構子
  println(s"建立 Person: $name, $age")
  
  require(age >= 0, "年齡不能為負數")
  require(name.nonEmpty, "名稱不能為空")
}
```

**主建構子的私有化:**

```scala
// 防止直接實例化,強制使用伴生物件的工廠方法
class Person private(val name: String, val age: Int)

// val p = new Person("Alice", 25)  // 錯誤! 建構子是私有的

object Person {
  def apply(name: String, age: Int): Person = {
    if (age >= 0) new Person(name, age)
    else throw new IllegalArgumentException("年齡不能為負數")
  }
}

val person = Person("Alice", 25)  // 使用 apply 方法
```

### 2.2 輔助建構子

使用 `this` 關鍵字定義輔助建構子:

```scala
class Person(val name: String, var age: Int) {
  // 輔助建構子必須呼叫主建構子或另一個輔助建構子
  def this(name: String) = {
    this(name, 0)  // 呼叫主建構子
  }
  
  def this() = {
    this("Unknown")  // 呼叫上面的輔助建構子
  }
  
  override def toString = s"Person($name, $age)"
}

// 使用不同的建構子
val p1 = new Person("Alice", 25)
val p2 = new Person("Bob")
val p3 = new Person()

println(p1)  // Person(Alice, 25)
println(p2)  // Person(Bob, 0)
println(p3)  // Person(Unknown, 0)
```

**更複雜的範例:**

```scala
class Rectangle(val width: Double, val height: Double) {
  // 正方形建構子
  def this(side: Double) = {
    this(side, side)
  }
  
  // 預設建構子 (單位正方形)
  def this() = {
    this(1.0)
  }
  
  def area: Double = width * height
  def perimeter: Double = 2 * (width + height)
  
  override def toString = s"Rectangle($width x $height)"
}

val rect1 = new Rectangle(5.0, 3.0)    // 長方形
val rect2 = new Rectangle(4.0)          // 正方形
val rect3 = new Rectangle()             // 單位正方形

println(s"$rect1, 面積: ${rect1.area}")
println(s"$rect2, 面積: ${rect2.area}")
println(s"$rect3, 面積: ${rect3.area}")
```

---

## 3. Object（物件）

`object` 是 Scala 實現單例模式的方式。

### 3.1 Singleton Object（單例物件）

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

// 使用 (不需要 new)
DatabaseConnection.connect()
println(DatabaseConnection.status)
DatabaseConnection.disconnect()

// 只有一個實例
val db1 = DatabaseConnection
val db2 = DatabaseConnection
println(db1 eq db2)  // true (相同實例)
```

### 3.2 工具物件

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

// 使用
println(MathUtils.square(5))
println(MathUtils.isPrime(17))
println(MathUtils.gcd(48, 18))
```

### 3.3 應用程式物件

```scala
@main def myApp(args: String*): Unit = {
  println("Hello, Scala!")
  println(s"參數: ${args.mkString(", ")}")
}

// 也支援與 Java 相容的 main 方法
object MyApp {
  def main(args: Array[String]): Unit = {
    println("Hello, Scala!")
  }
}
```

---

## 4. Companion Object（伴生物件）

伴生物件 (Companion Object) 與類別同名,且在同一個檔案中。

### 4.1 基本概念

```scala
class BankAccount(val accountNumber: String, private var balance: Double) {
  def deposit(amount: Double): Unit = {
    require(amount > 0, "金額必須大於 0")
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
  // 伴生物件可以存取類別的私有成員
  private var accountCounter = 0
  
  // 工廠方法
  def apply(initialBalance: Double): BankAccount = {
    accountCounter += 1
    new BankAccount(s"ACC${accountCounter.toString.padTo(6, '0')}", initialBalance)
  }
  
  // 建立特殊類型的帳戶
  def createSavingsAccount(initialBalance: Double): BankAccount = {
    val account = apply(initialBalance)
    println(s"建立儲蓄帳戶: ${account.accountNumber}")
    account
  }
  
  // 工具方法:計算總餘額
  def totalBalance(accounts: List[BankAccount]): Double = {
    accounts.map(_.balance).sum
  }
}

// 使用
val account1 = BankAccount(1000.0)  // 使用 apply
val account2 = BankAccount.createSavingsAccount(5000.0)

account1.deposit(500.0)
account1.withdraw(200.0)

println(account1)
println(account2)
```

### 4.2 apply 方法

`apply` 方法允許像函數一樣呼叫物件:

```scala
class Person(val name: String, val age: Int)

object Person {
  // apply 方法讓我們可以省略 new
  def apply(name: String, age: Int): Person = {
    new Person(name, age)
  }
  
  // 可以有多個 apply 方法 (重載)
  def apply(name: String): Person = {
    new Person(name, 0)
  }
}

val alice = Person("Alice", 25)  // 等同於 Person.apply("Alice", 25)
val bob = Person("Bob")          // 等同於 Person.apply("Bob")
```

### 4.3 `unapply` Method（方法）與 Extractor（提取器）

`unapply` 用於模式比對:

```scala
class Email(val user: String, val domain: String)

object Email {
  def apply(user: String, domain: String): Email = {
    new Email(user, domain)
  }
  
  // unapply 用於解構
  def unapply(email: Email): Option[(String, String)] = {
    Some((email.user, email.domain))
  }
  
  // 也可以從字串建立
  def fromString(emailStr: String): Option[Email] = {
    emailStr.split("@") match {
      case Array(user, domain) => Some(Email(user, domain))
      case _ => None
    }
  }
}

// 使用
val email = Email("alice", "example.com")

// 模式比對 (使用 unapply)
email match {
  case Email(user, domain) => println(s"User: $user, Domain: $domain")
}

// 解構賦值
val Email(u, d) = email
println(s"$u @ $d")
```

---

## 5. Case Class（案例類別）

Case Class 是 Scala 的特殊類別,自動提供許多有用功能。

### 5.1 基本用法

```scala
case class Person(name: String, age: Int)

// 自動功能:
// 1. 不需要 new 關鍵字
val alice = Person("Alice", 25)

// 2. 自動生成 toString
println(alice)  // Person(Alice,25)

// 3. 自動生成 equals 和 hashCode
val alice2 = Person("Alice", 25)
println(alice == alice2)  // true (比較值,不是參考)

// 4. 自動生成 copy 方法
val olderAlice = alice.copy(age = 26)
println(olderAlice)  // Person(Alice,26)

// 5. 支援模式比對
alice match {
  case Person(name, age) => println(s"$name is $age years old")
}

// 6. 自動生成伴生物件的 apply 和 unapply
```

### 5.2 Case Class 的特性

```scala
case class Point(x: Double, y: Double) {
  // 可以定義方法
  def distance(other: Point): Double = {
    math.sqrt(math.pow(x - other.x, 2) + math.pow(y - other.y, 2))
  }
  
  // 運算子方法
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

### 5.3 複製與修改

```scala
case class User(
  id: Int,
  username: String,
  email: String,
  active: Boolean = true
)

val user = User(1, "alice", "alice@example.com")

// 修改單個欄位
val updatedUser = user.copy(email = "newalice@example.com")

// 修改多個欄位
val deactivatedUser = user.copy(
  email = "old@example.com",
  active = false
)

println(user)            // 原始物件不變
println(updatedUser)
println(deactivatedUser)
```

### 5.4 嵌套 Case Class

```scala
case class Address(street: String, city: String, country: String)
case class Person(name: String, age: Int, address: Address)

val alice = Person(
  "Alice",
  25,
  Address("123 Main St", "Taipei", "Taiwan")
)

// 修改嵌套物件
val movedAlice = alice.copy(
  address = alice.address.copy(city = "Tokyo")
)

// 模式比對嵌套結構
alice match {
  case Person(name, _, Address(_, city, "Taiwan")) =>
    println(s"$name lives in $city, Taiwan")
}
```

### 5.5 Case Class vs 普通 Class

```scala
// Case Class
case class CasePerson(name: String, age: Int)

// 普通 Class
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

// Case Class 自動提供以上所有功能!
```

**何時使用 Case Class:**
- ✅ 不可變資料模型
- ✅ 需要模式比對
- ✅ 需要 copy 方法
- ✅ DTO (Data Transfer Object)
- ✅ 值物件

**何時使用普通 Class:**
- ✅ 需要可變狀態
- ✅ 需要自訂 equals/hashCode
- ✅ 需要隱藏實作細節

---

## 6. Trait（特徵）

Trait 類似於 Java 的介面,但可以包含實作。

### 6.1 基本 Trait

```scala
trait Greeter {
  def greet(name: String): String = s"Hello, $name!"
}

class Person(val name: String) extends Greeter

val person = new Person("Alice")
println(person.greet("Bob"))  // "Hello, Bob!"
```

### 6.2 抽象成員

```scala
trait Animal {
  // 抽象屬性
  def name: String
  
  // 抽象方法
  def makeSound(): String
  
  // 具體方法
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

### 6.3 混入多個 Trait

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

// 混入多個 trait
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

### 6.4 Trait 的執行順序

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
// 輸出順序:
// A initialized
// B initialized
// C initialized
// D initialized

println(d.message)
// 輸出: D -> C -> B -> A
// 線性化順序: D -> C -> B -> A
```

### 6.5 Self Type（自型別）

```scala
trait User {
  def username: String
}

trait Tweeter {
  // 自型別:要求混入此 trait 的類別也必須混入 User
  self: User =>
  
  def tweet(message: String): String = {
    s"$username: $message"
  }
}

// 必須同時繼承 User 和 Tweeter
class TwitterUser(val username: String) extends User with Tweeter

val user = new TwitterUser("alice")
println(user.tweet("Hello, World!"))  // "alice: Hello, World!"

// 錯誤範例
// class InvalidTweeter extends Tweeter  // 編譯錯誤!需要 User
```

### 6.6 實用的 Trait 範例

**日誌 Trait:**

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
    log("應用程式啟動")
  }
  
  def processData(): Unit = {
    try {
      // 處理資料
      log("資料處理中")
    } catch {
      case e: Exception => logError(e.getMessage)
    }
  }
}
```

**時間戳 Trait:**

```scala
trait Timestamped {
  val createdAt: Long = System.currentTimeMillis()
  
  def age: Long = System.currentTimeMillis() - createdAt
}

case class Document(title: String, content: String) extends Timestamped

val doc = Document("Report", "...")
Thread.sleep(1000)
println(s"文件年齡: ${doc.age} ms")
```

---

## 7. Inheritance（繼承）

### 7.1 基本繼承

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
println(dog.move())       // "Moving" (繼承自 Animal)
println(dog.fetch())      // "Buddy is fetching"
```

### 7.2 Method Override（方法覆寫）

```scala
class Shape {
  def area: Double = 0.0
  def perimeter: Double = 0.0
  
  // final 方法不能被覆寫
  final def description: String = "This is a shape"
}

class Circle(val radius: Double) extends Shape {
  override def area: Double = math.Pi * radius * radius
  override def perimeter: Double = 2 * math.Pi * radius
  
  // 錯誤!不能覆寫 final 方法
  // override def description: String = "Circle"
}

class Rectangle(val width: Double, val height: Double) extends Shape {
  override def area: Double = width * height
  override def perimeter: Double = 2 * (width + height)
}
```

### 7.3 super 關鍵字

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

### 7.4 型別測試與轉換

```scala
class Animal
class Dog extends Animal
class Cat extends Animal

val animal: Animal = new Dog

// isInstanceOf - 型別測試
if (animal.isInstanceOf[Dog]) {
  println("這是一隻狗")
}

// asInstanceOf - 型別轉換 (不安全!)
val dog = animal.asInstanceOf[Dog]

// 更安全的方式:模式比對
animal match {
  case d: Dog => println("這是一隻狗")
  case c: Cat => println("這是一隻貓")
  case _ => println("未知動物")
}
```

---

## 8. Abstract Class（抽象類別）

### 8.1 定義抽象類別

```scala
abstract class Animal {
  // 抽象屬性
  def name: String
  
  // 抽象方法
  def makeSound(): String
  
  // 具體方法
  def describe(): String = s"$name says ${makeSound()}"
  
  // 具體屬性
  val kingdom: String = "Animalia"
}

class Dog(val name: String) extends Animal {
  def makeSound(): String = "Woof!"
  
  def fetch(): String = s"$name is fetching"
}

// val animal = new Animal  // 錯誤!不能實例化抽象類別
val dog = new Dog("Buddy")
println(dog.describe())
```

### 8.2 抽象類別 vs Trait

**抽象類別:**
```scala
abstract class Vehicle(val maxSpeed: Int) {  // 可以有建構子參數
  def describe: String
}

class Car(maxSpeed: Int, val brand: String) extends Vehicle(maxSpeed) {
  def describe: String = s"$brand car, max speed: $maxSpeed km/h"
}
```

**Trait:**
```scala
trait Drivable {  // 不能有建構子參數
  def drive(): String
}

trait Flyable {
  def fly(): String
}

// 可以混入多個 trait
class FlyingCar extends Vehicle(300) with Drivable with Flyable {
  def describe: String = "Flying car"
  def drive(): String = "Driving on road"
  def fly(): String = "Flying in air"
}
```

**選擇指南:**
- 需要建構子參數 → 使用抽象類別
- 需要多重繼承 → 使用 Trait
- 表示 "是什麼" → 使用抽象類別
- 表示 "能做什麼" → 使用 Trait

### 8.3 複雜範例

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
    // 簡化的日期檢查
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

## 9. Polymorphism（多型）

### 9.1 子型別多型

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

// 多型:使用父類別型別
def printShapeInfo(shape: Shape): Unit = {
  println(f"面積: ${shape.area}%.2f")
  println(f"周長: ${shape.perimeter}%.2f")
}

val shapes: List[Shape] = List(
  new Circle(5),
  new Rectangle(4, 6),
  new Triangle(3, 4, 5)
)

shapes.foreach(printShapeInfo)

// 計算總面積
val totalArea = shapes.map(_.area).sum
println(f"總面積: $totalArea%.2f")
```

### 9.2 參數多型 (泛型)

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

**型別邊界:**

```scala
// 上界 (Upper Bound): T 必須是 Animal 或其子類別
class Zoo[T <: Animal](val animal: T) {
  def sound: String = animal.makeSound()
}

// 下界 (Lower Bound): T 必須是 Dog 或其父類別
class DogList[T >: Dog](val dogs: List[T])

// Scala 3 以 using 參數明確要求 Conversion[T, U]
```

### 9.3 Variance（型別變異）

**協變 (Covariance) - `+T`:**

```scala
class Animal
class Dog extends Animal
class Cat extends Animal

// Box 是協變的
class Box[+T](val content: T)

val dogBox: Box[Dog] = new Box(new Dog)
val animalBox: Box[Animal] = dogBox  // OK!協變允許這樣做

// List 是協變的
val dogs: List[Dog] = List(new Dog, new Dog)
val animals: List[Animal] = dogs  // OK!
```

**逆變 (Contravariance) - `-T`:**

```scala
trait Printer[-T] {
  def print(value: T): Unit
}

val animalPrinter: Printer[Animal] = new Printer[Animal] {
  def print(animal: Animal): Unit = println("Animal")
}

// 逆變:可以用 Animal 的 Printer 來處理 Dog
val dogPrinter: Printer[Dog] = animalPrinter  // OK!
```

**不變 (Invariance):**

```scala
// 預設是不變的
class Container[T](var content: T) {
  def get: T = content
  def set(newContent: T): Unit = content = newContent
}

val dogContainer: Container[Dog] = new Container(new Dog)
// val animalContainer: Container[Animal] = dogContainer  // 錯誤!不變
```

---

## 10. Access Modifier（存取修飾符）

### 10.1 public, private, protected

```scala
class Example {
  // public (預設)
  val publicField = "public"
  def publicMethod() = "public"
  
  // private (只在類別內部可見)
  private val privateField = "private"
  private def privateMethod() = "private"
  
  // protected (類別及其子類別可見)
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
    // println(privateField)  // 錯誤!
    println(protectedField)   // OK
  }
}

val example = new Example
println(example.publicField)     // OK
// println(example.privateField) // 錯誤!
// println(example.protectedField)  // 錯誤!
```

### 10.2 限定作用域

```scala
package com.example.app

class User {
  // 在 app 套件內可見
  private[app] val internalId = "123"
  
  // 在 example 套件內可見
  private[example] val companyId = "ABC"
  
  // 只在 User 類別內可見
  private val secretKey = "XYZ"
}

class Admin extends User {
  def canAccess(): Unit = {
    println(internalId)  // OK
    println(companyId)   // OK
    // println(secretKey)  // 錯誤：User 的 private 成員
  }
}
```

### 10.3 Scala 3 的物件私有推斷

Scala 3.3.8 已棄用 `private[this]`。請使用 `private`；編譯器會推斷成員是否只透過
`this` 存取，並套用相同最佳化。

```scala
class Counter {
  private var count = 0
  
  def increment(): Unit = count += 1
  
  def isGreaterThan(other: Counter): Boolean = {
    count > other.count  // 允許類別私有存取
  }
}
```

---

## 11. 實作練習

### 練習 1: 圖書館系統

```scala
// 基礎類別和 trait
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

// 具體類別
class Book(
  id: String,
  title: String,
  val author: String,
  val isbn: String
) extends LibraryItem(id, title) with Borrowable {
  def itemType: String = "Book"
  
  override def toString: String = {
    val base = super.toString
    val status = if (isAvailable) "可借閱" else s"已借出給 ${borrower.get}"
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
  override def canBorrow: Boolean = false  // 參考書不能外借
}

// 圖書館類別
class Library {
  private var items = List.empty[LibraryItem]
  
  def addItem(item: LibraryItem): Unit = {
    items = items :+ item
    println(s"已新增: $item")
  }
  
  def findById(id: String): Option[LibraryItem] = {
    items.find(_.id == id)
  }
  
  def listAvailable(): Unit = {
    println("\n=== 可借閱項目 ===")
    items.collect {
      case item: Borrowable if item.isAvailable => item
    }.foreach(println)
  }
  
  def listBorrowed(): Unit = {
    println("\n=== 已借出項目 ===")
    items.collect {
      case item: Borrowable if !item.isAvailable => item
    }.foreach(println)
  }
}

// 測試
@main def librarySystem(): Unit = {
  val library = new Library
  
  // 新增項目
  library.addItem(new Book("B001", "Scala 程式設計", "Martin Odersky", "123-456"))
  library.addItem(new Book("B002", "函數式程式設計", "Paul Chiusano", "789-012"))
  library.addItem(new Magazine("M001", "Programmer Monthly", "2024-01"))
  library.addItem(new ReferenceBook("R001", "Scala API 文檔", "Programming"))
  
  // 借閱
  library.findById("B001") match {
    case Some(item: Borrowable) =>
      if (item.borrow("Alice", "2024-02-01")) {
        println(s"\n${item.asInstanceOf[LibraryItem].title} 已借給 Alice")
      }
    case _ => println("無法借閱")
  }
  
  // 列出狀態
  library.listAvailable()
  library.listBorrowed()
}
```

### 練習 2: 銀行系統

```scala
// 帳戶基礎類別
abstract class Account(val accountNumber: String, protected var balance: Double) {
  def deposit(amount: Double): Unit = {
    require(amount > 0, "存款金額必須大於 0")
    balance += amount
    println(f"存款 $$${amount}%.2f,餘額: $$${balance}%.2f")
  }
  
  def withdraw(amount: Double): Boolean
  
  def getBalance: Double = balance
  
  override def toString: String = 
    s"${this.getClass.getSimpleName}($accountNumber, $$${balance}%.2f)"
}

// 支票帳戶 (可以透支)
class CheckingAccount(
  accountNumber: String,
  balance: Double,
  val overdraftLimit: Double
) extends Account(accountNumber, balance) {
  
  def withdraw(amount: Double): Boolean = {
    require(amount > 0, "提款金額必須大於 0")
    if (balance - amount >= -overdraftLimit) {
      balance -= amount
      println(f"提款 $$${amount}%.2f,餘額: $$${balance}%.2f")
      true
    } else {
      println("餘額不足(超過透支額度)")
      false
    }
  }
}

// 儲蓄帳戶 (有利息)
class SavingsAccount(
  accountNumber: String,
  balance: Double,
  val interestRate: Double
) extends Account(accountNumber, balance) {
  
  def withdraw(amount: Double): Boolean = {
    require(amount > 0, "提款金額必須大於 0")
    if (amount <= balance) {
      balance -= amount
      println(f"提款 $$${amount}%.2f,餘額: $$${balance}%.2f")
      true
    } else {
      println("餘額不足")
      false
    }
  }
  
  def addInterest(): Unit = {
    val interest = balance * interestRate
    balance += interest
    println(f"利息 $$${interest}%.2f 已加入,新餘額: $$${balance}%.2f")
  }
}

// 銀行客戶
case class Customer(id: String, name: String) {
  private var accounts = List.empty[Account]
  
  def addAccount(account: Account): Unit = {
    accounts = accounts :+ account
  }
  
  def getAccounts: List[Account] = accounts
  
  def totalBalance: Double = accounts.map(_.getBalance).sum
}

// 銀行
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
        1000.0  // 透支額度 $1000
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
        0.02  // 2% 利率
      )
      customer.addAccount(account)
      account
    }
  }
  
  def transfer(from: Account, to: Account, amount: Double): Boolean = {
    if (from.withdraw(amount)) {
      to.deposit(amount)
      println(s"轉帳 $$${amount}%.2f 從 ${from.accountNumber} 到 ${to.accountNumber}")
      true
    } else {
      false
    }
  }
}

// 測試
@main def bankingSystem(): Unit = {
  val bank = new Bank("MyBank")
  
  // 建立客戶
  val alice = bank.createCustomer("C001", "Alice")
  val bob = bank.createCustomer("C002", "Bob")
  
  // 建立帳戶
  val aliceChecking = bank.createCheckingAccount("C001", 1000.0).get
  val aliceSavings = bank.createSavingsAccount("C001", 5000.0).get
  val bobChecking = bank.createCheckingAccount("C002", 2000.0).get
  
  println("\n=== 初始狀態 ===")
  println(aliceChecking)
  println(aliceSavings)
  println(bobChecking)
  
  println("\n=== 交易 ===")
  aliceChecking.deposit(500.0)
  aliceChecking.withdraw(200.0)
  aliceSavings.addInterest()
  
  println("\n=== 轉帳 ===")
  bank.transfer(aliceChecking, bobChecking, 300.0)
  
  println("\n=== 最終狀態 ===")
  println(aliceChecking)
  println(aliceSavings)
  println(bobChecking)
  println(f"Alice 總餘額: $$${alice.totalBalance}%.2f")
  println(f"Bob 總餘額: $$${bob.totalBalance}%.2f")
}
```

### 練習 3: 遊戲角色系統

```scala
// 基礎 trait
trait Character {
  def name: String
  var health: Int
  var maxHealth: Int
  
  def isAlive: Boolean = health > 0
  
  def takeDamage(damage: Int): Unit = {
    health = math.max(0, health - damage)
    println(s"$name 受到 $damage 點傷害,剩餘生命: $health/$maxHealth")
    if (!isAlive) println(s"$name 已死亡!")
  }
  
  def heal(amount: Int): Unit = {
    if (isAlive) {
      health = math.min(maxHealth, health + amount)
      println(s"$name 恢復 $amount 點生命,當前生命: $health/$maxHealth")
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
      println(s"施放法術!消耗 $manaCost 魔力,剩餘: $mana/$maxMana")
      true
    } else {
      println("魔力不足!")
      false
    }
  }
}

trait Defensive {
  var armor: Int
  
  def calculateDamage(damage: Int): Int = {
    val reduced = math.max(1, damage - armor)
    println(s"護甲減免 ${damage - reduced} 點傷害")
    reduced
  }
}

// 具體職業
class Warrior(
  val name: String,
  var health: Int,
  var maxHealth: Int,
  var armor: Int
) extends Character with Attacker with Defensive {
  
  private val attackPower = 15
  
  def attack(target: Character): Unit = {
    println(s"$name 攻擊 ${target.name}!")
    target.takeDamage(attackPower)
  }
  
  override def takeDamage(damage: Int): Unit = {
    super.takeDamage(calculateDamage(damage))
  }
  
  def rage(): Unit = {
    println(s"$name 進入狂暴狀態!")
    // 提升攻擊力邏輯
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
    println(s"$name 用法杖攻擊 ${target.name}!")
    target.takeDamage(staffDamage)
  }
  
  def fireball(target: Character): Boolean = {
    println(s"$name 施放火球術!")
    castSpell(target, 20, 30)
  }
  
  def regenerateMana(amount: Int): Unit = {
    mana = math.min(maxMana, mana + amount)
    println(s"$name 恢復 $amount 點魔力,當前: $mana/$maxMana")
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
      println(s"$name 施放治療術!消耗 15 魔力")
      true
    } else {
      println("魔力不足!")
      false
    }
  }
  
  def massHeal(targets: List[Character]): Unit = {
    if (mana >= 30) {
      mana -= 30
      targets.foreach(_.heal(15))
      println(s"$name 施放群體治療!")
    } else {
      println("魔力不足!")
    }
  }
}

// 遊戲管理
class Battle {
  def simulate(attacker: Character with Attacker, defender: Character): Unit = {
    println(s"\n=== ${attacker.name} vs ${defender.name} ===")
    var round = 1
    
    while (attacker.isAlive && defender.isAlive && round <= 5) {
      println(s"\n--- 回合 $round ---")
      attacker.attack(defender)
      
      if (defender.isAlive) {
        defender match {
          case d: Attacker => d.attack(attacker)
          case _ =>
        }
      }
      
      round += 1
    }
    
    println("\n=== 戰鬥結束 ===")
    if (attacker.isAlive) println(s"${attacker.name} 獲勝!")
    else if (defender.isAlive) println(s"${defender.name} 獲勝!")
    else println("平手!")
  }
}

// 測試
@main def gameSystem(): Unit = {
  val warrior = new Warrior("戰士", 100, 100, 10)
  val mage = new Mage("法師", 70, 70, 100, 100)
  val healer = new Healer("牧師", 80, 80, 120, 120)
  
  println("=== 角色狀態 ===")
  println(s"${warrior.name}: HP ${warrior.health}/${warrior.maxHealth}, 護甲 ${warrior.armor}")
  println(s"${mage.name}: HP ${mage.health}/${mage.maxHealth}, MP ${mage.mana}/${mage.maxMana}")
  println(s"${healer.name}: HP ${healer.health}/${healer.maxHealth}, MP ${healer.mana}/${healer.maxMana}")
  
  println("\n=== 戰鬥測試 ===")
  warrior.attack(mage)
  mage.fireball(warrior)
  healer.healSpell(warrior)
  
  println("\n=== 模擬對戰 ===")
  val battle = new Battle
  
  // 重置生命值
  warrior.health = warrior.maxHealth
  mage.health = mage.maxHealth
  mage.mana = mage.maxMana
  
  battle.simulate(warrior, mage)
}
```

### 練習 4: 形狀層次結構

```scala
// 抽象基礎類別
abstract class Shape {
  def area: Double
  def perimeter: Double
  
  def describe: String = {
    f"${this.getClass.getSimpleName}: 面積 = ${area}%.2f, 周長 = ${perimeter}%.2f"
  }
}

// 2D 形狀
trait Shape2D {
  def vertices: Int
}

trait Drawable {
  def draw(): String = {
    s"繪製 ${this.getClass.getSimpleName}"
  }
}

trait Scalable {
  def scale(factor: Double): Shape
}

// 具體形狀
class Circle(val radius: Double) extends Shape with Shape2D with Drawable with Scalable {
  require(radius > 0, "半徑必須大於 0")
  
  def area: Double = math.Pi * radius * radius
  def perimeter: Double = 2 * math.Pi * radius
  def vertices: Int = 0
  
  def scale(factor: Double): Circle = new Circle(radius * factor)
  
  def diameter: Double = 2 * radius
}

class Rectangle(val width: Double, val height: Double) 
    extends Shape with Shape2D with Drawable with Scalable {
  
  require(width > 0 && height > 0, "寬度和高度必須大於 0")
  
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
  
  require(a + b > c && b + c > a && a + c > b, "不是有效的三角形")
  
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

// 複合形狀
class CompositeShape(val shapes: List[Shape]) extends Shape {
  def area: Double = shapes.map(_.area).sum
  def perimeter: Double = shapes.map(_.perimeter).sum
  
  override def describe: String = {
    s"""複合形狀包含 ${shapes.length} 個形狀:
       |${shapes.map(_.describe).mkString("\n")}
       |總面積: ${area}%.2f
       |總周長: ${perimeter}%.2f""".stripMargin
  }
}

// 形狀工廠
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

// 測試
@main def shapeSystem(): Unit = {
  println("=== 建立形狀 ===")
  val circle = ShapeFactory.createCircle(5.0)
  val square = ShapeFactory.createSquare(4.0)
  val rectangle = ShapeFactory.createRectangle(6.0, 3.0)
  val triangle = ShapeFactory.createRightTriangle(3.0, 4.0)
  
  val shapes = List(circle, square, rectangle, triangle)
  
  println("\n=== 形狀資訊 ===")
  shapes.foreach(s => println(s.describe))
  
  println("\n=== 可繪製形狀 ===")
  shapes.collect { case d: Drawable => d }.foreach(d => println(d.draw()))
  
  println("\n=== 縮放測試 ===")
  val scaledCircle = circle.scale(2.0)
  println(s"原始圓: ${circle.describe}")
  println(s"放大2倍: ${scaledCircle.describe}")
  
  println("\n=== 複合形狀 ===")
  val composite = new CompositeShape(shapes)
  println(composite.describe)
  
  println("\n=== 三角形類型檢查 ===")
  println(s"等邊三角形? ${triangle.isEquilateral}")
  println(s"等腰三角形? ${triangle.isIsosceles}")
  println(s"直角三角形? ${triangle.isRight}")
}
```

---

## 12. 重點總結

### 類別與物件
- **類別**:使用 `class` 定義,支援主建構子和輔助建構子
- **物件**:使用 `object` 定義單例
- **伴生物件**:提供工廠方法和工具函數
- **Case Class**:自動生成 equals、hashCode、toString、copy

### Trait
- 可包含抽象和具體成員
- 支援多重混入
- 用於定義可重用的行為

### 繼承
- 使用 `extends` 繼承類別或混入 trait
- 使用 `override` 覆寫方法
- 使用 `super` 呼叫父類別方法
- 支援抽象類別

### 多型
- 子型別多型 (繼承)
- 參數多型 (泛型)
- 型別變異 (協變、逆變、不變)

### 存取控制
- public (預設)
- private、protected
- 限定作用域 (private[package])

---

## 下一步

完成第四部分後,您已經掌握:
- ✅ 類別、物件、伴生物件的使用
- ✅ Case Class 的特性與應用
- ✅ Trait 的定義與混入
- ✅ 繼承與多型
- ✅ 物件導向設計原則

**接下來學習:**
- [第五部分:集合操作](scala_part5_collections.md) - List、Set、Map 等深入應用
- [第六部分:模式比對](scala_part6_pattern_matching.md) - 強大的控制流程工具

準備好繼續了嗎?

---

> [« 上一篇：函數與方法](scala_part3_functions.md) | [📚 目錄](../README.md) | [下一篇：集合操作 »](scala_part5_collections.md)
