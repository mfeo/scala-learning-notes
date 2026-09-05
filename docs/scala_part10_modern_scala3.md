# Scala 教學 - 第十部分：Modern Scala 3.3.8 LTS

> [Runnable example and tests](../examples/src/examples/modern) | [« 上一篇：巨集](scala_part9_macros.md) | [📚 目錄](../README.md) | [下一篇：Mill 與可執行範例 »](scala_part11_mill_examples.md)

---

## 目錄
1. [為什麼需要 Modern Scala 3](#1-為什麼需要-modern-scala-3)
2. [Contextual Abstractions](#2-contextual-abstractions上下文抽象)
3. [Extension Methods](#3-extension-methods擴充方法)
4. [Enums](#4-enum列舉)
5. [Opaque Types](#5-opaque-type不透明型別)
6. [Export Clauses](#6-export-clause匯出子句)
7. [Type Class Derivation](#7-type-class-derivation型別類別衍生)
8. [Scala 3.3.8 慣例](#8-scala-338-慣例)
9. [實作練習](#9-實作練習)

---

## 1. 為什麼需要 Modern Scala 3

本指南以 Scala 3.3.8 LTS 為目標。LTS 是 Long-Term Support（長期支援），表示
3.3 系列會比一般版本獲得更長期、以相容性為主的維護。Scala 3 保留 Static Typing
（靜態型別）、Object-Oriented Programming（物件導向程式設計）與 Functional
Programming（函數式程式設計）等核心模型，同時加入更清楚的語法與更有表達力的
Type System（型別系統）。

主要改進包括 `given`／`using` 上下文抽象、extension method、enum、opaque type、
export、型別類別衍生、新型別、可省略大括號、頂層定義及編譯期後設程式設計。

### 1.1 語法與定義

Scala 3.3.8 支援以縮排省略大括號、新控制結構語法、頂層定義、長定義使用的 `end`
標記，以及 `@main` 程式進入點：

```scala
def classify(value: Int): String =
  if value < 0 then "negative"
  else if value == 0 then "zero"
  else "positive"

@main def hello(name: String): Unit =
  println(s"Hello, $name")
```

類別通常可省略 `new` 建構，trait 可接受參數；若具體 class 預期從另一個來源檔案被
繼承，就必須宣告成 `open`：

```scala
class User(val name: String)
val user = User("Ada")

trait Named(prefix: String):
  def name: String
  def displayName: String = s"$prefix$name"

open class PublicBase
```

Scala 3 的 import、型別萬用字元及可變參數展開語法也更明確：

```scala
import java.time.{LocalDate as Date}
import scala.collection.mutable.*

val unknownNumbers: List[? <: Number] = List(Integer.valueOf(1))
val values = List(1, 2, 3)
val copied = List(values*)
```

英數字方法若預期用中綴形式呼叫，請加上 `infix`；符號方法可用 `@targetName` 提供
穩定且方便 Java 呼叫的名稱：

```scala
import scala.annotation.targetName

case class Count(value: Int)

extension (left: Count)
  infix def plus(right: Count): Count = Count(left.value + right.value)

  @targetName("timesCount")
  def *(factor: Int): Count = Count(left.value * factor)
```

### 1.2 Modern Types（現代型別）

Scala 3.3.8 包含 Intersection Type（交集型別）、Union Type（聯集型別）、
Type Lambda（型別 Lambda）、Match Type（比對型別）、Dependent Function Type
（相依函式型別）與 Polymorphic Function Type（多型函式型別）：

```scala
trait Resettable:
  def reset(): Unit

trait Closeable:
  def close(): Unit

def resetAndClose(value: Resettable & Closeable): Unit =
  value.reset()
  value.close()

type StringOrInt = String | Int
type MapValues[K] = [V] =>> Map[K, V]

type Element[X] = X match
  case String => Char
  case Array[t] => t
  case Iterable[t] => t

trait Entry:
  type Key
  def key: Key

val keyOf: (entry: Entry) => entry.Key =
  (entry: Entry) => entry.key

val identity: [A] => A => A =
  [A] => (value: A) => value
```

標準 Tuple Operations（元組操作）、Match Type（比對型別），以及可適用於不同型別
種類的 `Tuple` 與 `Function` 抽象，支援任意 arity（參數或元素數量）的泛型程式設計。

---

## 2. Contextual Abstractions（上下文抽象）

Scala 3.3.8 使用 `given` 定義上下文實例、`using` 宣告上下文需求，並以 `summon`
取得目前作用域中的實例。

```scala
trait Show[A]:
  def show(value: A): String

given Show[Int] with
  def show(value: Int): String = value.toString

given Show[String] with
  def show(value: String): String = value

def render[A](value: A)(using show: Show[A]): String =
  show.show(value)

def renderAll[A: Show](values: List[A]): List[String] =
  values.map(value => summon[Show[A]].show(value))
```

Given 匯入與一般萬用字元匯入分開：

```scala
object Formats:
  given Show[Double] with
    def show(value: Double): String = f"$value%.2f"

import Formats.given
```

Context function 把可用的上下文納入函式型別；by-name context parameter 會延後求值，
適合遞迴上下文定義：

```scala
trait Logger:
  def log(message: String): Unit

type Logged[A] = Logger ?=> A

def announce(message: String): Logged[Unit] =
  summon[Logger].log(message)

def delayed(using logger: => Logger): Logger = logger
```

需要有意識地執行上下文轉換時，使用標準 `Conversion` 型別類別：

```scala
import scala.Conversion
import scala.language.implicitConversions
given Conversion[Int, String] = _.toString
```

啟用 `-language:strictEquality` 後，等值比較需要 `CanEqual` 證據；若嚴格等值比較符合
領域設計，case class 與 enum 可衍生它：

```scala
case class UserId(value: String) derives CanEqual
```

Given 應放在所提供型別或型別類別的 companion object 附近、明確匯入，並避免範圍過廣
的轉換。

---

## 3. Extension Methods（擴充方法）

Extension method 讓既有型別取得新操作，而不需要修改原始型別：

```scala
extension (text: String)
  def words: List[String] =
    text.trim.split("\\s+").toList.filter(_.nonEmpty)

  def titleCase: String =
    words.map(word => s"${word.head.toUpper}${word.tail.toLowerCase}").mkString(" ")

extension [A](values: List[A])
  def secondOption: Option[A] = values.drop(1).headOption
```

Scala 3.3.8 也支援多型別參數的群組擴充、上下文參數、運算子擴充，以及名稱以 `:`
結尾的右結合擴充。需匯入的擴充應放在名稱清楚的 object 中；若呼叫端沒有更易讀，
則優先使用一般方法。

---

## 4. Enum（列舉）

Enum 可表示簡單列舉及代數資料型別：

```scala
enum OrderStatus derives CanEqual:
  case Draft, Submitted, Paid, Cancelled

enum PaymentResult[+A]:
  case Approved(value: A)
  case Declined(reason: String)

def message(result: PaymentResult[String]): String =
  result match
    case PaymentResult.Approved(id) => s"Approved: $id"
    case PaymentResult.Declined(reason) => s"Declined: $reason"
```

Enum 可有參數、成員、泛型 case 及與 Java 相容的 case。所有替代情形可在編譯期得知，
而且完整模式比對有價值時，適合使用 enum。

---

## 5. Opaque Type（不透明型別）

Opaque type alias 在不配置包裝物件的情況下建立抽象邊界：

```scala
object Domain:
  opaque type UserId = String

  object UserId:
    def from(value: String): Option[UserId] =
      Option.when(value.trim.nonEmpty)(value.trim)

  extension (id: UserId)
    def value: String = id

import Domain.*
```

在定義範圍外，`UserId` 與 `String` 不同；範圍內則可見其表示。Opaque alias 可為
泛型、有界限、位於頂層或作為 class 成員。成員 opaque type 是 path-dependent type，
因此兩個實例能以相同表示定義出不同抽象型別。

---

## 6. Export Clause（匯出子句）

`export` 會建立轉發成員，支援選取、萬用字元、重新命名及 given 匯出：

```scala
class UserService(repository: UserRepository):
  export repository.{findById, save}

class PublicService(repository: UserRepository):
  export repository.findById as findUser
```

Export 適合組合元件及建立 façade API。若轉發時必須加入驗證、授權或其他行為，請改用
明確方法。

---

## 7. Type Class Derivation（型別類別衍生）

`derives` 會向型別類別的 companion object 要求 `derived` 實作；編譯器提供描述欄位或
替代情形的 `Mirror`：

```scala
import scala.deriving.Mirror

trait JsonEncoder[A]:
  def encode(value: A): String

object JsonEncoder:
  def derived[A](using Mirror.Of[A]): JsonEncoder[A] =
    new JsonEncoder[A]:
      def encode(value: A): String = value.toString

case class User(id: String, name: String) derives JsonEncoder
```

這是刻意簡化的範例；正式 encoder 會檢查 `MirroredElemTypes` 並遞迴取得 encoder。
只有型別類別提供必要的 `derived` 方法，或編譯器像對 `CanEqual` 一樣提供特殊支援時，
衍生才會成立。

---

## 8. Scala 3.3.8 慣例

### 8.1 語法檢查表

- 使用 `given` 定義上下文實例，使用 `using` 宣告上下文參數。
- 使用 `summon[A]` 取得作用域中的 `A` 實例。
- 使用 `extension` 為既有型別提供新操作。
- 萬用字元型別寫成 `List[?]`，可變參數展開寫成 `values*`。
- 匯入萬用成員時使用 `import a.*`，重新命名時使用 `import a.{x as y}`。
- 方法可直接當成函式值傳遞，不需要額外的 eta expansion 標記。
- 使用 `@main` 定義小型程式進入點。
- 方法本體使用 `=`，並在公開方法上標示回傳型別。
- 需要先執行本體再判斷時，使用條件為程式區塊的 `while`。
- 共用定義可以直接放在頂層；XML literal 需要獨立的 Scala XML 函式庫。

### 8.2 其他 Scala 3.3.8 功能

下列功能較專門，但都屬於 3.3 語言面，閱讀程式碼時應能辨識：

- trait parameter、transparent trait 與 class、`open` class、universal apply method、
  頂層定義、parameter untupling 及 kind polymorphism；
- 透過 `Selectable` 實作的程式化 structural type、`Matchable` 標記，以及透過
  `TypeTest` 執行的安全擦除型別測試；
- 改進的 overload resolution、implicit resolution、型別推斷、pattern binding、
  match 完整性、lazy val 初始化及字串插值跳脫檢查；
- `@threadUnsafe` 可讓 lazy val 不使用執行緒安全初始化，以及 `0b1010` 等二進位字面值；
- `-Yexplicit-nulls` 顯式空值與 `-Wsafe-init` 安全初始化警告；兩者在 Scala 3.3.8
  都是選用檢查；
- `inline`、`transparent inline`、編譯期操作、quote、splice 與 reflection 等
  後設程式設計功能，詳見第九部分；TASTy inspection 可讓工具檢查已編譯程式碼中的
  typed abstract syntax tree（帶型別抽象語法樹）。

Scala 3.3.8 版本本身也加入 JDK 26 支援、`@uncheckedOverride`、
`-Yfuture-lazy-vals` 相容性選項、局部程式碼覆蓋開關標記及 REPL 中斷處理改善。
JDK 是 Java Development Kit（Java 開發工具套件）；REPL 是 Read-Eval-Print
Loop（讀取、求值、輸出循環的互動式提示環境）。這些是版本能力，不是新的核心語法。

---

## 9. 實作練習

1. 定義 `Show[A]` 型別類別、given instance 及明確 given 匯入。
2. 為 `List[A]` 新增 extension method，其中一個要求 `Ordering[A]` 上下文。
3. 建立泛型 `CheckoutState[+A]` enum 並撰寫完整模式比對。
4. 建立驗證 `@` 的 opaque `Email` 型別。
5. 定義並使用聯集型別、交集型別及 match type。
6. 透過 `Mirror` 衍生小型型別類別，並測試 product 與 sum type。
7. 使用 `given`、`using` 與 `extension` 建立一組可組合的格式化工具。

---

## 下一步

請在儲存庫的 Mill 專案中練習，並在閱讀本指南時維持編譯器版本為 `3.3.8`：

- [第十一部分：Mill 與可執行範例](scala_part11_mill_examples.md)
- [第十二部分：測試](scala_part12_testing.md)
- [Scala 3 官方參考](https://docs.scala-lang.org/scala3/reference/)

---

> [« 上一篇：巨集](scala_part9_macros.md) | [📚 目錄](../README.md) | [下一篇：Mill 與可執行範例 »](scala_part11_mill_examples.md)
