# Scala 教學 - 第十部分: Modern Scala 3

> [« 上一篇：宏](scala_part9_macros.md) | [📚 目錄](../README.md) | [下一篇：Mill 與可執行範例 »](scala_part11_mill_examples.md)

---

## 目錄
1. [為什麼需要 Modern Scala 3](#1-為什麼需要-modern-scala-3)
2. [given 與 using 的上下文抽象](#2-given-與-using-的上下文抽象)
3. [Extension Methods](#3-extension-methods)
4. [Enums](#4-enums)
5. [Opaque Types](#5-opaque-types)
6. [Exports](#6-exports)
7. [Derives](#7-derives)
8. [遷移注意事項](#8-遷移注意事項)
9. [實作練習](#9-實作練習)

---

## 1. 為什麼需要 Modern Scala 3

Scala 3 保留 Scala 的核心模型：簡潔語法、強型別、物件導向與函數式程式設計。實務上最大的差異，是許多 Scala 2 的隱式寫法現在有更清楚、更安全的語法。

建議先讀完第八部分。第八部分說明 implicits 與 type classes 的概念，本章則示範新專案應優先使用的 Scala 3 寫法。

主要語法更新：
- `given` 定義上下文值。
- `using` 要求上下文值。
- `extension` 在不包裝型別的情況下新增方法。
- `enum` 直接定義代數資料型別。
- `opaque type` 建立零額外成本的領域型別。
- `export` 轉發內部物件的成員。
- `derives` 要求編譯器或函式庫產生 type class instances。

---

## 2. given 與 using 的上下文抽象

Scala 2 常使用 `implicit val`、`implicit def` 與 implicit parameter lists。Scala 3 保留相同概念，但語意更明確。

```scala
trait Show[A]:
  def show(value: A): String

given Show[Int] with
  def show(value: Int): String = value.toString

given Show[String] with
  def show(value: String): String = value

def render[A](value: A)(using show: Show[A]): String =
  show.show(value)

val renderedNumber = render(42)
val renderedText = render("Scala")
```

需要直接取得上下文值時，使用 `summon`：

```scala
def renderTwice[A](value: A)(using Show[A]): String =
  val show = summon[Show[A]]
  s"${show.show(value)}, ${show.show(value)}"
```

Context bounds 仍然可用：

```scala
def renderAll[A: Show](values: List[A]): List[String] =
  values.map(value => summon[Show[A]].show(value))
```

建議：
- 新的 Scala 3 程式碼優先使用 `given` 與 `using`。
- given instances 盡量放在 type 或 type class 的 companion object 附近。
- 除非 scope 很小且意圖明確，避免大量 wildcard import givens。
- 當名稱能改善錯誤訊息或 API 可讀性時，替 given 命名。

---

## 3. Extension Methods

Extension methods 取代許多 Scala 2 implicit class 的使用場景。

```scala
extension (text: String)
  def words: List[String] =
    text.trim.split("\\s+").toList.filter(_.nonEmpty)

  def titleCase: String =
    words.map(word => word.head.toUpper + word.tail.toLowerCase).mkString(" ")

val title = "modern scala 3".titleCase
```

泛型 extension methods 適合小而清楚的 API：

```scala
extension [A](values: List[A])
  def secondOption: Option[A] =
    values.drop(1).headOption
```

建議：
- 用 extension methods 提升領域語意的可讀性。
- 不要把所有 helper method 都變成 extension method。
- 若需要 import，將 extension methods 放在命名清楚的 object 裡。

---

## 4. Enums

Scala 3 enums 可以直接描述一組封閉的狀態。

```scala
enum OrderStatus:
  case Draft, Submitted, Paid, Cancelled

def canPay(status: OrderStatus): Boolean =
  status match
    case OrderStatus.Submitted => true
    case _ => false
```

Enums 也可以攜帶資料：

```scala
enum PaymentResult:
  case Approved(transactionId: String)
  case Declined(reason: String)
  case RequiresReview(score: Int)

def message(result: PaymentResult): String =
  result match
    case PaymentResult.Approved(id) => s"Approved: $id"
    case PaymentResult.Declined(reason) => s"Declined: $reason"
    case PaymentResult.RequiresReview(score) => s"Review score: $score"
```

適合使用 enums 的情境：
- 所有 cases 在編譯時已知。
- exhaustive pattern matching 有價值。
- 每個 case 都代表明確的業務狀態。

---

## 5. Opaque Types

Opaque types 可以建立更強的領域型別，而且不增加執行時包裝成本。

```scala
object Domain:
  opaque type UserId = String

  object UserId:
    def from(value: String): Option[UserId] =
      Option.when(value.nonEmpty)(value)

  extension (id: UserId)
    def value: String = id

import Domain.*

val id = UserId.from("u-123")
```

在定義範圍之外，`UserId` 不等於 `String`。在定義範圍內，編譯器仍以 `String` 表示它。

適合使用 opaque types 的情境：
- identifiers。
- validated values。
- units of measure。
- 小型領域型別，且 case class wrapper 顯得太重。

---

## 6. Exports

`export` 可以轉發內部物件的指定成員。

```scala
class UserService(repository: UserRepository):
  export repository.findById
  export repository.save
```

它適合用於 module composition，但不應該隱藏重要邊界。若方法需要額外行為或驗證，明確寫出 method 通常更好。

---

## 7. Derives

`derives` 讓編譯器或函式庫產生 type class instances。

```scala
trait JsonEncoder[A]

case class User(id: String, name: String) derives JsonEncoder
```

實際 derivation 機制取決於 type class。Circe、Cats、Tapir 等函式庫都使用這類模式來減少重複樣板碼。

適合使用 derivation 的情境：
- 產生的 instance 行為明確且可預期。
- 產生行為符合領域規則。
- 特殊邊界條件仍有測試覆蓋。

---

## 8. 遷移注意事項

常見 Scala 2 到 Scala 3 對應：

| Scala 2 idiom | Scala 3 建議 |
|---|---|
| `implicit val` instance | `given` instance |
| implicit parameter list | `using` parameter list |
| `implicitly[A]` | `summon[A]` |
| implicit class syntax extension | `extension` method |
| sealed trait plus case objects | `enum` |
| value class wrapper | 只需型別區分時可考慮 `opaque type` |

遷移策略：
1. 先保持行為不變。
2. 將 implicit parameters 改成 `using`。
3. 將 type class instances 改成 `given`。
4. 將簡單 implicit classes 改成 extension methods。
5. 只有在 API 影響可接受時，才把 sealed hierarchies 改成 enums。

---

## 9. 實作練習

1. 定義 `Show[A]` type class，並為 `Int`、`String`、`User` 提供 given instances。
2. 為 `List[A]` 新增 extension methods：`secondOption`、`nonEmptyCount`、`mapToSet`。
3. 建立至少四個狀態的 `CheckoutState` enum，並撰寫 exhaustive matcher。
4. 建立 opaque `Email` type，驗證字串必須包含 `@`。
5. 將第八部分的一個 Scala 2 implicit-class 範例改寫成 Scala 3 extension syntax。

---

## 下一步

讀完本章後，建議在真實專案裡練習這些語法：
- [第十一部分：Mill 與可執行範例](scala_part11_mill_examples.md)
- [第十二部分：測試](scala_part12_testing.md)

---

> [« 上一篇：宏](scala_part9_macros.md) | [📚 目錄](../README.md) | [下一篇：Mill 與可執行範例 »](scala_part11_mill_examples.md)
