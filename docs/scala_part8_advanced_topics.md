# Scala 教學 - 第八部分：上下文抽象與型別類別

> [« 上一篇：錯誤處理](scala_part7_error_handling.md) | [📚 目錄](../README.md) | [下一篇：巨集 »](scala_part9_macros.md)

---

## 目錄

1. [上下文參數](#1-上下文參數)
2. [Given 實例](#2-given-實例)
3. [上下文轉換](#3-上下文轉換)
4. [擴充方法](#4-擴充方法)
5. [型別類別](#5-型別類別)
6. [Context Bound](#6-context-bound)
7. [實例搜尋與作用域](#7-實例搜尋與作用域)
8. [組合型別類別](#8-組合型別類別)
9. [最佳實踐](#9-最佳實踐)
10. [練習](#10-練習)

---

## 1. 上下文參數

Scala 3 使用 `using` 宣告由呼叫端上下文提供的參數。這適合傳遞設定、排序規則、
執行環境或型別類別實例。

```scala
def greet(name: String)(using greeting: String): String =
  s"$greeting, $name!"

given defaultGreeting: String = "Hello"

greet("Alice")             // "Hello, Alice!"
greet("Bob")(using "Hi") // "Hi, Bob!"
```

多個上下文參數可以放在同一個 `using` 子句：

```scala
def format(value: Double)(using precision: Int, symbol: String): String =
  s"$symbol${value.formatted(s"%.${precision}f")}"

given defaultPrecision: Int = 2
given defaultSymbol: String = "$"

format(123.456) // "$123.46"
```

上下文參數也能傳遞標準函式庫提供的能力：

```scala
def sortValues[A](values: List[A])(using ordering: Ordering[A]): List[A] =
  values.sorted

case class Person(name: String, age: Int)

given Ordering[Person] = Ordering.by(_.age)

sortValues(List(Person("Alice", 25), Person("Bob", 20)))
```

---

## 2. Given 實例

`given` 建立編譯器可依型別尋找的上下文值。實例可以具名，也可以只由型別識別。

```scala
case class DatabaseConfig(host: String, port: Int)

given productionConfig: DatabaseConfig =
  DatabaseConfig("db.example.com", 5432)

def connectionLabel(using config: DatabaseConfig): String =
  s"${config.host}:${config.port}"
```

需要實作介面時，可以使用 `with`：

```scala
trait Encoder[A]:
  def encode(value: A): String

given Encoder[Int] with
  def encode(value: Int): String = value.toString
```

參數化實例能由其他實例組合而成：

```scala
given [A](using encoder: Encoder[A]): Encoder[List[A]] with
  def encode(values: List[A]): String =
    values.map(encoder.encode).mkString("[", ",", "]")
```

---

## 3. 上下文轉換

`Conversion[A, B]` 表示編譯器可在需要 `B` 時將 `A` 轉換為 `B`。自動轉換可能隱藏
成本或錯誤，因此應保持範圍精確；一般資料轉換優先使用具名方法。

```scala
import scala.Conversion
import scala.language.implicitConversions

final case class UserId(value: String)

given Conversion[String, UserId] = UserId(_)

def loadUser(id: UserId): String = id.value

loadUser("user-123")
```

如果轉換可能失敗，使用 `Option` 或 `Either` 明確表達結果：

```scala
final case class Port private (value: Int)

object Port:
  def from(value: Int): Either[String, Port] =
    Either.cond(value >= 1 && value <= 65535, Port(value), "invalid port")
```

---

## 4. 擴充方法

`extension` 可以替既有型別加入方法，而不需要修改原始型別。

```scala
extension (value: Int)
  def squared: Int = value * value
  def isEven: Boolean = value % 2 == 0

3.squared // 9
4.isEven  // true
```

泛型擴充方法可以保留元素型別：

```scala
extension [A](values: List[A])
  def secondOption: Option[A] = values.drop(1).headOption
  def toEither(error: => String): Either[String, List[A]] =
    Either.cond(values.nonEmpty, values, error)

List(1, 2, 3).secondOption       // Some(2)
List.empty[Int].toEither("empty") // Left("empty")
```

---

## 5. 型別類別

型別類別以泛型介面描述能力，再為個別型別提供實例。演算法只依賴能力，不需要修改
資料型別或建立繼承關係。

```scala
trait Show[A]:
  def show(value: A): String

object Show:
  def apply[A](using instance: Show[A]): Show[A] = instance

  def instance[A](render: A => String): Show[A] =
    new Show[A]:
      def show(value: A): String = render(value)

  given Show[Int] = instance(_.toString)
  given Show[String] = instance(value => s"\"$value\"")

def render[A](value: A)(using show: Show[A]): String =
  show.show(value)

render(42)      // "42"
render("Scala") // "\"Scala\""
```

把實例放在型別類別或資料型別的伴生物件中，可讓編譯器在不額外匯入的情況下找到它。

```scala
case class Person(name: String, age: Int)

object Person:
  given Show[Person] =
    Show.instance(person => s"${person.name} (${person.age})")
```

語法操作可以用擴充方法呈現：

```scala
extension [A](value: A)
  def show(using instance: Show[A]): String = instance.show(value)

Person("Alice", 25).show
```

---

## 6. Context Bound

Context bound（上下文界定）是只需要某個型別類別實例時的簡寫。`[A: Show]` 表示作用域
中必須存在 `Show[A]`。

```scala
def renderAll[A: Show](values: List[A]): List[String] =
  values.map(value => summon[Show[A]].show(value))
```

`summon[A]` 取得目前作用域中的 `A` 實例。若需要多個能力，可以串接多個界定：

```scala
def sortedLabels[A: Show: Ordering](values: List[A]): List[String] =
  val show = summon[Show[A]]
  values.sorted.map(show.show)
```

若方法內多次使用實例，具名的 `using` 參數通常更容易閱讀。

---

## 7. 實例搜尋與作用域

編譯器會從目前作用域、明確匯入及相關型別的伴生物件尋找 `given`。可以只匯入某個
物件提供的 given 實例：

```scala
object AgeOrdering:
  given Ordering[Person] = Ordering.by(_.age)

import AgeOrdering.given

List(Person("Alice", 25), Person("Bob", 20)).sorted
```

區域實例適合暫時改變行為：

```scala
def descending(values: List[Int]): List[Int] =
  given Ordering[Int] = Ordering.Int.reverse
  values.sorted
```

同一作用域若有多個同型別候選實例，編譯器可能無法決定要使用哪一個。應縮小匯入
範圍，或在呼叫時明確傳入：

```scala
val byName: Ordering[Person] = Ordering.by(_.name)

sortValues(List(Person("Bob", 20), Person("Alice", 25)))(using byName)
```

---

## 8. 組合型別類別

型別類別實例可以遞迴組合。例如，只要元素具備 `Show`，清單也能具備 `Show`：

```scala
object ShowInstances:
  given [A](using itemShow: Show[A]): Show[List[A]] =
    Show.instance { values =>
      values.map(itemShow.show).mkString("[", ", ", "]")
    }

import ShowInstances.given

render(List(1, 2, 3)) // "[1, 2, 3]"
```

代數結構也能使用相同方式建模：

```scala
trait Monoid[A]:
  def empty: A
  def combine(left: A, right: A): A

object Monoid:
  def apply[A](using instance: Monoid[A]): Monoid[A] = instance

  given Monoid[Int] with
    def empty: Int = 0
    def combine(left: Int, right: Int): Int = left + right

  given Monoid[String] with
    def empty: String = ""
    def combine(left: String, right: String): String = left + right

def combineAll[A: Monoid](values: List[A]): A =
  val monoid = summon[Monoid[A]]
  values.foldLeft(monoid.empty)(monoid.combine)
```

若一個型別需要多種不同的組合規則，應將實例放入具名物件，由使用端明確匯入，避免
同型別實例衝突。

---

## 9. 最佳實踐

- 使用 `using` 表達呼叫鏈中需要傳遞的上下文能力。
- 將預設 `given` 放在型別類別或資料型別的伴生物件中。
- 使用 `extension` 提供語法操作，讓核心型別類別維持精簡。
- 自動 `Conversion` 只用於安全、無損且不令人意外的轉換。
- 可能失敗的轉換使用 `Option` 或 `Either`。
- 避免在廣泛作用域中提供多個相同型別的 `given`。
- 優先依賴小型、可組合的型別類別。
- 測試泛型函式的可觀察結果，而不是實例搜尋的內部步驟。

---

## 10. 練習

1. 定義 `Eq[A]` 型別類別，並為 `Int`、`String` 和 `List[A]` 提供 `given`。
2. 為 `Eq[A]` 建立 `===` 與 `=/=` 擴充方法。
3. 定義 `JsonEncoder[A]`，並組合出 `JsonEncoder[Option[A]]`。
4. 使用 `using Ordering[A]` 實作泛型最大值函式，處理空清單時回傳 `Option[A]`。
5. 為兩種 `Ordering[Person]` 建立具名物件，分別按姓名與年齡排序。

---

## 總結

本章使用 Scala 3.3.8 的 `given`、`using`、`summon`、context bound、`extension` 與
`Conversion` 建立上下文抽象。這些工具能清楚區分實例定義、上下文需求、語法擴充與
型別轉換，並讓型別類別程式保持可組合及可測試。

---

> [« 上一篇：錯誤處理](scala_part7_error_handling.md) | [📚 目錄](../README.md) | [下一篇：巨集 »](scala_part9_macros.md)
