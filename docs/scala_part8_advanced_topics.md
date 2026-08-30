# Scala 教學 - 第八部分:進階主題

> [« 上一篇：錯誤處理](scala_part7_error_handling.md) | [📚 目錄](../README.md) | [下一篇：巨集 »](scala_part9_macros.md)

---

## 目錄
1. [隱式系統概覽](#1-隱式系統概覽)
2. [隱式參數](#2-隱式參數)
3. [隱式轉換](#3-隱式轉換)
4. [隱式類別](#4-隱式類別)
5. [型別類別](#5-型別類別)
6. [Context Bounds](#6-context-bounds)
7. [隱式解析規則](#7-隱式解析規則)
8. [進階型別類別](#8-進階型別類別)
9. [最佳實踐](#9-最佳實踐)
10. [實作練習](#10-實作練習)

---

## 1. 隱式系統概覽

本章說明舊式 Scala 2 隱式語法，因為遷移既有程式碼時仍需理解它。Scala 3.3.8
為了相容性仍接受大部分語法，但新程式碼應使用第十部分介紹的 `given`、`using`、
`extension` 與 `Conversion`。

### 1.1 什麼是隱式?

```scala
// 隱式系統允許編譯器自動填入某些參數或進行轉換

// 問題:重複傳遞相同參數
def greet(name: String, greeting: String): String = {
  s"$greeting, $name!"
}

greet("Alice", "Hello")  // Hello, Alice!
greet("Bob", "Hello")    // Hello, Bob!
greet("Charlie", "Hello") // Hello, Charlie!

// 解決方案:使用隱式參數
def greetImplicit(name: String)(implicit greeting: String): String = {
  s"$greeting, $name!"
}

implicit val defaultGreeting: String = "Hello"

greetImplicit("Alice")   // Hello, Alice! - 自動使用 defaultGreeting
greetImplicit("Bob")     // Hello, Bob!
```

### 1.2 隱式系統的三種用途

```scala
// 1. 隱式參數 (Implicit Parameters)
//    - 自動傳遞上下文資訊
def process(data: String)(implicit config: Config): String = {
  // 使用 config
  data.toUpperCase
}

// 2. 上下文轉換 (Contextual Conversions)
//    - Scala 3 以明確形式表達自動型別轉換
import scala.Conversion
import scala.language.implicitConversions
given Conversion[Int, String] = _.toString
val s: String = 42  // 自動轉換為 "42"

// 3. 隱式類別 (Implicit Classes)
//    - 擴展現有型別的方法
implicit class RichInt(val x: Int) extends AnyVal {
  def times(f: => Unit): Unit = {
    (1 to x).foreach(_ => f)
  }
}

3.times {
  println("Hello")
}
// 輸出三次 "Hello"
```

---

## 2. 隱式參數

### 2.1 基本用法

```scala
// 定義隱式參數
def greet(name: String)(implicit greeting: String): String = {
  s"$greeting, $name!"
}

// 提供隱式值
implicit val defaultGreeting: String = "Hello"

// 自動使用隱式值
greet("Alice")  // "Hello, Alice!"

// 也可以明確傳遞
greet("Bob")("Hi")  // "Hi, Bob!"

// 多個隱式參數
def format(value: Double)(implicit precision: Int, symbol: String): String = {
  s"$symbol${value.formatted(s"%.${precision}f")}"
}

implicit val defaultPrecision: Int = 2
implicit val defaultSymbol: String = "$"

format(123.456)  // "$123.46"
```

### 2.2 隱式參數的常見用途

```scala
// 1. 配置傳遞
case class DatabaseConfig(host: String, port: Int)

def connect()(implicit config: DatabaseConfig): String = {
  s"Connecting to ${config.host}:${config.port}"
}

implicit val dbConfig: DatabaseConfig = DatabaseConfig("localhost", 5432)
connect()  // "Connecting to localhost:5432"

// 2. 執行上下文
import scala.concurrent.{Future, ExecutionContext}

def asyncTask(data: String)(implicit ec: ExecutionContext): Future[String] = {
  Future {
    data.toUpperCase
  }
}

implicit val ec: ExecutionContext = ExecutionContext.global
asyncTask("hello")  // 自動使用 global execution context

// 3. 排序
def sortList[T](list: List[T])(implicit ordering: Ordering[T]): List[T] = {
  list.sorted
}

sortList(List(3, 1, 4, 1, 5))  // List(1, 1, 3, 4, 5)
// Ordering[Int] 是預定義的隱式值

case class Person(name: String, age: Int)

implicit val personOrdering: Ordering[Person] = Ordering.by(_.age)
val people = List(Person("Alice", 25), Person("Bob", 20))
sortList(people)  // 按年齡排序
```

### 2.3 隱式參數與預設參數的差異

```scala
// 預設參數:在定義時指定
def greetDefault(name: String, greeting: String = "Hello"): String = {
  s"$greeting, $name!"
}

// 隱式參數:在呼叫範圍內指定
def greetImplicit(name: String)(implicit greeting: String): String = {
  s"$greeting, $name!"
}

// 預設參數:固定
greetDefault("Alice")  // 總是 "Hello, Alice!"

// 隱式參數:可以在不同範圍改變
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

## 3. 隱式轉換

### 3.1 基本隱式轉換

```scala
// 定義 Scala 3 上下文轉換
import scala.Conversion
import scala.language.implicitConversions
given Conversion[Int, String] = _.toString

// 使用隱式轉換
val s: String = 42  // 編譯器插入: intToString(42)

def printString(s: String): Unit = println(s)
printString(123)  // 自動轉換

// 更實用的範例:日期處理
import java.time.LocalDate

given Conversion[String, LocalDate] = LocalDate.parse(_)

def daysBetween(start: LocalDate, end: LocalDate): Long = {
  java.time.temporal.ChronoUnit.DAYS.between(start, end)
}

// 可以直接傳遞字串
daysBetween("2024-01-01", "2024-01-10")  // 9
```

### 3.2 隱式轉換的危險性

```scala
// ⚠️ 隱式轉換可能導致意外行為

// 定義過於寬泛的轉換
implicit def anyToString[T](x: T): String = x.toString

val x: String = List(1, 2, 3)  // "List(1, 2, 3)" - 可能不是你想要的

// 造成歧義
implicit def intToDouble(x: Int): Double = x.toDouble
implicit def intToFloat(x: Int): Float = x.toFloat

// val d: Number = 42  // 編譯錯誤:歧義!

// Scala 3.3.8 建議：
// - 轉換可能令人意外時，優先使用明確轉換方法
// - 否則定義範圍精確的 given Conversion[A, B]
// - 要新增操作時使用 extension method，而不是 implicit class
```

### 3.3 視圖界定 (已廢棄)

```scala
// Scala 2.x 的視圖界定 (View Bounds) - 已廢棄
// def process[T <% String](value: T): String = value

// Scala 3 做法：明確要求 Conversion
def process[T](value: T)(using conv: Conversion[T, String]): String = conv(value)

given Conversion[Int, String] = _.toString
process(42)  // "42"
```

---

## 4. 隱式類別

### 4.1 基本用法

以下是舊式相容語法。Scala 3.3.8 新程式碼應優先使用第十部分的 `extension` 語法。

```scala
// 隱式類別擴展現有型別
implicit class RichInt(val x: Int) extends AnyVal {
  def times(f: => Unit): Unit = {
    (1 to x).foreach(_ => f)
  }
  
  def squared: Int = x * x
  
  def isEven: Boolean = x % 2 == 0
}

// 使用擴展方法
5.times {
  println("Hello")
}

println(3.squared)  // 9
println(4.isEven)   // true

// 編譯器實際做的事:
// new RichInt(5).times { println("Hello") }
```

### 4.2 Value Classes

```scala
// 使用 AnyVal 避免運行時開銷
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

// 限制:
// 1. 只能有一個參數
// 2. 不能定義其他 val/var
// 3. 不能擴展其他類別 (除了 AnyVal)
```

### 4.3 實用的擴展

```scala
// 字串擴展
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

// 集合擴展
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

// Option 擴展
implicit class OptionOps[T](val opt: Option[T]) extends AnyVal {
  def orThrow(ex: => Exception): T = opt.getOrElse(throw ex)
  
  def toEither[L](left: => L): Either[L, T] = {
    opt.toRight(left)
  }
}

Some(42).orThrow(new Exception("Missing"))  // 42
// None.orThrow(new Exception("Missing"))   // 拋出異常

Some(42).toEither("error")  // Right(42)
None.toEither("error")      // Left("error")
```

---

## 5. 型別類別

### 5.1 什麼是型別類別?

```scala
// 型別類別 (Type Class) 是一種設計模式
// 用於為型別提供行為,而不修改型別本身

// 問題:如何讓不同型別支援 JSON 序列化?

// ❌ 傳統 OOP 方式:修改每個類別
trait JsonSerializable {
  def toJson: String
}

case class Person(name: String) extends JsonSerializable {
  def toJson: String = s"""{"name":"$name"}"""
}
// 但這需要修改源碼,且無法為 Int、String 等內建型別添加

// ✅ 型別類別方式:定義外部行為
trait JsonSerializer[T] {
  def toJson(value: T): String
}

// 為不同型別提供實例
implicit val intSerializer: JsonSerializer[Int] = new JsonSerializer[Int] {
  def toJson(value: Int): String = value.toString
}

implicit val stringSerializer: JsonSerializer[String] = new JsonSerializer[String] {
  def toJson(value: String): String = s""""$value""""
}

// 使用
def serialize[T](value: T)(implicit serializer: JsonSerializer[T]): String = {
  serializer.toJson(value)
}

serialize(42)      // "42"
serialize("hello") // "\"hello\""
```

### 5.2 定義型別類別

```scala
// 1. 定義型別類別 trait
trait Show[T] {
  def show(value: T): String
}

// 2. 提供實例
object Show {
  // 建立實例的輔助方法
  def apply[T](implicit instance: Show[T]): Show[T] = instance
  
  // 建立實例的工廠方法
  def instance[T](f: T => String): Show[T] = new Show[T] {
    def show(value: T): String = f(value)
  }
}

// 3. 為具體型別提供實例
implicit val intShow: Show[Int] = Show.instance(_.toString)

implicit val stringShow: Show[String] = Show.instance(s => s""""$s"""")

implicit val booleanShow: Show[Boolean] = Show.instance {
  case true => "yes"
  case false => "no"
}

// 4. 使用型別類別
def print[T](value: T)(implicit shower: Show[T]): Unit = {
  println(shower.show(value))
}

print(42)       // "42"
print("hello")  // "\"hello\""
print(true)     // "yes"
```

### 5.3 為自訂型別提供實例

```scala
case class Person(name: String, age: Int)

// 在伴生物件中定義隱式實例
object Person {
  implicit val personShow: Show[Person] = Show.instance { p =>
    s"Person(${p.name}, ${p.age})"
  }
}

print(Person("Alice", 25))  // "Person(Alice, 25)"

// 為集合提供實例
implicit def listShow[T](implicit itemShow: Show[T]): Show[List[T]] = {
  Show.instance { list =>
    list.map(itemShow.show).mkString("[", ", ", "]")
  }
}

print(List(1, 2, 3))  // "[1, 2, 3]"
print(List("a", "b"))  // "[\"a\", \"b\"]"
```

### 5.4 標準型別類別範例

```scala
// 1. Ordering - 排序
trait MyOrdering[T] {
  def compare(x: T, y: T): Int
}

implicit val intOrdering: MyOrdering[Int] = new MyOrdering[Int] {
  def compare(x: Int, y: Int): Int = x - y
}

def sort[T](list: List[T])(implicit ord: MyOrdering[T]): List[T] = {
  list.sortWith((a, b) => ord.compare(a, b) < 0)
}

// 2. Numeric - 數值運算
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

// 3. 相等性
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

### 6.1 基本語法

```scala
// 傳統寫法:隱式參數
def print[T](value: T)(implicit shower: Show[T]): Unit = {
  println(shower.show(value))
}

// Context Bound 語法糖
def printCB[T: Show](value: T): Unit = {
  val shower = implicitly[Show[T]]  // 取得隱式實例
  println(shower.show(value))
}

// 或使用 Show.apply
def printCB2[T: Show](value: T): Unit = {
  println(Show[T].show(value))
}

// 等價於:
// def printCB[T](value: T)(implicit evidence$1: Show[T]): Unit
```

### 6.2 多個 Context Bounds

```scala
// 可以有多個 context bounds
def process[T: Show: Ordering](value: T): String = {
  val shower = implicitly[Show[T]]
  val ord = implicitly[Ordering[T]]
  
  shower.show(value)
}

// 等價於:
// def process[T](value: T)(implicit shower: Show[T], ord: Ordering[T])
```

### 6.3 實用範例

```scala
// JSON 序列化
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

// 使用 context bound
def toJson[T: JsonWriter](value: T): String = {
  JsonWriter[T].write(value)
}

toJson(42)      // "42"
toJson("hello") // "\"hello\""

// 為 List 提供實例
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

## 7. 隱式解析規則

### 7.1 隱式搜尋範圍

```scala
// 編譯器搜尋隱式值的順序:

// 1. 當前作用域
def example1(): Unit = {
  implicit val x: Int = 42
  
  def needsImplicit(implicit i: Int): Int = i
  
  needsImplicit  // 找到 x
}

// 2. 明確引入的隱式
object Implicits {
  implicit val greeting: String = "Hello"
}

def example2(): Unit = {
  import Implicits.*
  
  def needsGreeting(implicit g: String): String = g
  
  needsGreeting  // 找到 greeting
}

// 3. 伴生物件
trait Show[T] {
  def show(value: T): String
}

object Show {
  // 在伴生物件中定義
  implicit val intShow: Show[Int] = new Show[Int] {
    def show(value: Int): String = value.toString
  }
}

def print[T](value: T)(implicit shower: Show[T]): Unit = {
  println(shower.show(value))
}

print(42)  // 自動找到 Show.intShow

// 4. 型別參數的伴生物件
case class Person(name: String)

object Person {
  implicit val personShow: Show[Person] = new Show[Person] {
    def show(value: Person): String = s"Person(${value.name})"
  }
}

print(Person("Alice"))  // 自動找到 Person.personShow
```

### 7.2 隱式優先級

```scala
// 當有多個候選時,編譯器選擇最具體的

trait Animal
class Dog extends Animal
class Puppy extends Dog

object Implicits {
  implicit val animalValue: Animal = new Animal {}
  implicit val dogValue: Dog = new Dog
}

import Implicits.*

def needsAnimal(implicit a: Animal): Animal = a

// 選擇 dogValue,因為 Dog 比 Animal 更具體
needsAnimal  // dogValue

// Scala 3.3.8 會選擇位於更深巢狀層級的候選值
def example(): Unit = {
  import Implicits.*
  
  implicit val localDog: Dog = new Dog
  
  needsAnimal  // 使用 localDog (本地定義優先)
}
```

### 7.3 避免歧義

```scala
// ❌ 歧義錯誤
object Bad {
  implicit val int1: Int = 1
  implicit val int2: Int = 2
  
  def needsInt(implicit i: Int): Int = i
  
  // needsInt  // 編譯錯誤:ambiguous implicit values
}

// ✅ 使用不同型別
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

## 8. 進階型別類別

### 8.1 Monoid

```scala
// Monoid: 具有結合律和單位元的型別類別
trait Monoid[T] {
  def empty: T                          // 單位元
  def combine(x: T, y: T): T           // 結合操作
}

object Monoid {
  def apply[T](implicit instance: Monoid[T]): Monoid[T] = instance
}

// Int 加法 monoid
implicit val intAddMonoid: Monoid[Int] = new Monoid[Int] {
  def empty: Int = 0
  def combine(x: Int, y: Int): Int = x + y
}

// Int 乘法 monoid
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

// 使用 monoid
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
// Functor: 可以 map 的型別
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

// 使用
def increment[F[_]: Functor](container: F[Int]): F[Int] = {
  Functor[F].map(container)(_ + 1)
}

increment(List(1, 2, 3))     // List(2, 3, 4)
increment(Some(42))          // Some(43)
increment(None)              // None
```

### 8.3 可序列化型別類別

```scala
// 完整的序列化框架
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

// 基本型別的 codec
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

// 組合 codec
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

// 使用
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

## 9. 最佳實踐

### 9.1 何時使用隱式

```scala
// ✅ 好的使用場景:

// 1. 型別類別
trait JsonWriter[T] {
  def write(value: T): String
}

// 2. 上下文資訊
def query(sql: String)(implicit connection: DatabaseConnection): Result = ???

// 3. 擴展方法 (使用隱式類別)
implicit class RichString(s: String) extends AnyVal {
  def isPalindrome: Boolean = s == s.reverse
}

// ❌ 避免的場景:

// 1. 隱式轉換 (容易造成混淆)
// implicit def intToString(i: Int): String = i.toString

// 2. 過多的隱式參數
// def bad(a: String)(implicit b: Int, c: String, d: Double, e: Boolean): Unit

// 3. 不明確的隱式
// implicit val x: Int = 42  // 太泛用
```

### 9.2 命名慣例

```scala
// ✅ 好的命名

// 型別類別實例:使用描述性名稱
implicit val personJsonWriter: JsonWriter[Person] = ???
implicit val intOrdering: Ordering[Int] = ???

// 隱式類別:Rich* 或 *Ops
implicit class RichInt(val x: Int) extends AnyVal
implicit class StringOps(val s: String) extends AnyVal

// ❌ 不好的命名
// implicit val x: JsonWriter[Person] = ???  // 不清楚
// implicit val impl: Ordering[Int] = ???    // 太泛用
```

### 9.3 組織隱式

```scala
// 方式 1: 在伴生物件中
case class Person(name: String, age: Int)

object Person {
  implicit val ordering: Ordering[Person] = Ordering.by(_.age)
  implicit val jsonWriter: JsonWriter[Person] = ???
}

// 方式 2: 在專門的 Implicits 物件中
object JsonWriters {
  implicit val intWriter: JsonWriter[Int] = ???
  implicit val stringWriter: JsonWriter[String] = ???
  implicit val personWriter: JsonWriter[Person] = ???
}

// 使用時明確引入
import JsonWriters.*

// 方式 3: 在 package object 中 (謹慎使用)
package object myapp {
  implicit val defaultTimeout: Timeout = Timeout(30.seconds)
}
```

### 9.4 調試隱式

```scala
// 查看編譯器選擇的隱式
import scala.language.implicitConversions

// 使用 implicitly 檢查
val writer = implicitly[JsonWriter[Int]]

// 在 sbt 中啟用編譯器選項
// scalacOptions += "-Xlog-implicits"

// 手動解析
def debug[T](value: T)(implicit writer: JsonWriter[T]): Unit = {
  println(s"Using writer: ${writer.getClass.getName}")
  println(s"Result: ${writer.write(value)}")
}
```

---

## 10. 實作練習

### 練習 1: 自訂 Show 型別類別

```scala
// 完整的 Show 型別類別實作
trait Show[T] {
  def show(value: T): String
}

object Show {
  // 召喚隱式實例
  def apply[T](implicit instance: Show[T]): Show[T] = instance
  
  // 建立實例
  def instance[T](f: T => String): Show[T] = new Show[T] {
    def show(value: T): String = f(value)
  }
  
  // 語法糖
  implicit class ShowOps[T](val value: T) extends AnyVal {
    def show(implicit s: Show[T]): String = s.show(value)
  }
  
  // 基本型別實例
  implicit val intShow: Show[Int] = instance(_.toString)
  implicit val stringShow: Show[String] = instance(s => s""""$s"""")
  implicit val booleanShow: Show[Boolean] = instance(_.toString)
  implicit val doubleShow: Show[Double] = instance(d => f"$d%.2f")
  
  // 容器型別實例
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
  
  // 元組實例
  implicit def tuple2Show[A: Show, B: Show]: Show[(A, B)] = instance {
    case (a, b) => s"(${a.show}, ${b.show})"
  }
}

// 測試
object ShowDemo {
  import Show.*
  
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

### 練習 2: Equal 型別類別

```scala
// Equal 型別類別用於型別安全的相等性比較
trait Equal[T] {
  def eqv(x: T, y: T): Boolean
  def neqv(x: T, y: T): Boolean = !eqv(x, y)
}

object Equal {
  def apply[T](implicit instance: Equal[T]): Equal[T] = instance
  
  def instance[T](f: (T, T) => Boolean): Equal[T] = new Equal[T] {
    def eqv(x: T, y: T): Boolean = f(x, y)
  }
  
  // 語法糖
  implicit class EqualOps[T](val x: T) extends AnyVal {
    def ===(y: T)(implicit eq: Equal[T]): Boolean = eq.eqv(x, y)
    def =/=(y: T)(implicit eq: Equal[T]): Boolean = eq.neqv(x, y)
  }
  
  // 基本型別實例
  implicit val intEqual: Equal[Int] = instance(_ == _)
  implicit val stringEqual: Equal[String] = instance(_ == _)
  implicit val booleanEqual: Equal[Boolean] = instance(_ == _)
  
  // 容器型別實例
  implicit def optionEqual[T: Equal]: Equal[Option[T]] = instance {
    case (Some(x), Some(y)) => x === y
    case (None, None) => true
    case _ => false
  }
  
  implicit def listEqual[T: Equal]: Equal[List[T]] = instance { (xs, ys) =>
    xs.length == ys.length && xs.zip(ys).forall { case (x, y) => x === y }
  }
}

// 測試
object EqualDemo {
  import Equal.*
  
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
    
    // 型別安全:不能比較不同型別
    // println(1 === "1")  // 編譯錯誤!
  }
}
```

### 練習 3: Monoid 實作

```scala
// 完整的 Monoid 型別類別
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
  
  // 語法糖
  implicit class MonoidOps[T](val x: T) extends AnyVal {
    def |+|(y: T)(implicit m: Monoid[T]): T = m.combine(x, y)
  }
  
  // 基本型別實例
  implicit val intAdditionMonoid: Monoid[Int] = instance(0)(_ + _)
  
  implicit val intMultiplicationMonoid: Monoid[Int] = instance(1)(_ * _)
  
  implicit val stringMonoid: Monoid[String] = instance("")(_ + _)
  
  implicit val booleanAndMonoid: Monoid[Boolean] = instance(true)(_ && _)
  
  implicit val booleanOrMonoid: Monoid[Boolean] = instance(false)(_ || _)
  
  // 容器型別實例
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
  
  // 實用函數
  def combineAll[T: Monoid](values: List[T]): T = {
    val m = Monoid[T]
    values.foldLeft(m.empty)(m.combine)
  }
  
  def combineN[T: Monoid](value: T, n: Int): T = {
    val m = Monoid[T]
    (1 to n).foldLeft(m.empty)((acc, _) => m.combine(acc, value))
  }
}

// 測試
object MonoidDemo {
  import Monoid.*
  
  def main(args: Array[String]): Unit = {
    // 數字
    println(combineAll(List(1, 2, 3, 4, 5)))  // 15
    println(1 |+| 2 |+| 3)                     // 6
    
    // 字串
    println(combineAll(List("Hello", " ", "World")))  // "Hello World"
    println("Hello" |+| " " |+| "Scala")              // "Hello Scala"
    
    // 列表
    println(combineAll(List(List(1, 2), List(3, 4), List(5))))
    // List(1, 2, 3, 4, 5)
    
    // Map (需要為值型別提供 Semigroup)
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

### 練習 4: Functor 和 Applicative

```scala
// Functor 型別類別
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
  
  // 實例
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

// Applicative 型別類別
trait Applicative[F[_]] extends Functor[F] {
  def pure[A](a: A): F[A]
  def ap[A, B](ff: F[A => B])(fa: F[A]): F[B]
  
  // 從 ap 實作 map
  def map[A, B](fa: F[A])(f: A => B): F[B] = {
    ap(pure(f))(fa)
  }
  
  def map2[A, B, C](fa: F[A], fb: F[B])(f: (A, B) => C): F[C] = {
    ap(map(fa)(a => (b: B) => f(a, b)))(fb)
  }
}

object Applicative {
  def apply[F[_]](implicit instance: Applicative[F]): Applicative[F] = instance
  
  // 實例
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

// 測試
object FunctorDemo {
  import Functor.*
  import Applicative.*
  
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

### 練習 5: 驗證型別類別

```scala
// 驗證結果
sealed trait Validated[+E, +A]
case class Valid[+A](value: A) extends Validated[Nothing, A]
case class Invalid[+E](errors: List[E]) extends Validated[E, Nothing]

object Validated {
  // Applicative 實例
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

// 驗證器
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
    if (value.isEmpty) Invalid(List(s"$field 不能為空"))
    else Valid(value)
  }
  
  def minLength(field: String, min: Int): Validator[String, String] = Validator { value =>
    if (value.length < min) Invalid(List(s"$field 至少需要 $min 個字元"))
    else Valid(value)
  }
  
  def maxLength(field: String, max: Int): Validator[String, String] = Validator { value =>
    if (value.length > max) Invalid(List(s"$field 最多 $max 個字元"))
    else Valid(value)
  }
  
  def matches(field: String, regex: String): Validator[String, String] = Validator { value =>
    if (!value.matches(regex)) Invalid(List(s"$field 格式不正確"))
    else Valid(value)
  }
  
  def range(field: String, min: Int, max: Int): Validator[String, Int] = Validator { value =>
    if (value < min || value > max) 
      Invalid(List(s"$field 必須在 $min 到 $max 之間"))
    else Valid(value)
  }
}

// 使用範例
object ValidationDemo {
  import Validators.*
  
  case class User(username: String, email: String, age: Int)
  
  val usernameValidator = 
    nonEmpty("用戶名")
      .and(minLength("用戶名", 3))
      .and(maxLength("用戶名", 20))
  
  val emailValidator = 
    nonEmpty("Email")
      .and(matches("Email", """^[\w\.-]+@[\w\.-]+\.\w+$"""))
  
  val ageValidator = range("年齡", 13, 120)
  
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
    // 有效輸入
    validateUser("alice", "alice@example.com", 25) match {
      case Valid(user) => println(s"✓ 有效: $user")
      case Invalid(errors) => 
        println("✗ 錯誤:")
        errors.foreach(e => println(s"  - $e"))
    }
    
    // 無效輸入 - 累積所有錯誤
    validateUser("ab", "invalid", 200) match {
      case Valid(user) => println(s"✓ 有效: $user")
      case Invalid(errors) => 
        println("✗ 錯誤:")
        errors.foreach(e => println(s"  - $e"))
    }
  }
}
```

---

## 11. 重點總結

### 隱式系統
- **隱式參數**: 自動傳遞上下文資訊
- **隱式轉換**: 自動型別轉換 (謹慎使用)
- **隱式類別**: 擴展現有型別

### 型別類別
- **定義行為**: 不修改原始型別
- **多態性**: 為不同型別提供統一介面
- **組合性**: 可以組合多個型別類別

### Context Bounds
- 語法糖: `[T: TypeClass]`
- 簡化隱式參數聲明
- 需要使用 `implicitly` 或 `TypeClass.apply`

### 最佳實踐
- 優先使用隱式類別而非隱式轉換
- 將隱式實例放在伴生物件中
- 使用描述性命名
- 謹慎使用,避免過度隱式

---

## 下一步

完成第八部分後,您已經掌握:
- ✅ 隱式參數和隱式類別
- ✅ 型別類別的設計模式
- ✅ Context Bounds 語法
- ✅ 標準型別類別 (Monoid, Functor 等)

**注意**：本章保留舊式語法，只用於閱讀及遷移 Scala 2 程式碼。新的 Scala 3.3.8
程式碼應使用第十部分的上下文抽象；新舊語法的匯入與解析規則並不完全相同。

建議繼續學習:
- Cats/Scalaz 等函數式程式設計函式庫
- 並發程式設計 (Future, Akka)
- 進階型別系統特性
- [第九部分:巨集 (Macros)](scala_part9_macros.md)

恭喜您完成進階主題!

---

> [« 上一篇：錯誤處理](scala_part7_error_handling.md) | [📚 目錄](../README.md) | [下一篇：巨集 »](scala_part9_macros.md)
