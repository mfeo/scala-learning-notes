# Scala 教學 - 宏 (Macros)

> [« 上一篇：進階主題](scala_part8_advanced_topics.md) | [📚 目錄](../README.md)

---

## 目錄
1. [宏概覽](#1-宏概覽)
2. [Scala 2 vs Scala 3 宏](#2-scala-2-vs-scala-3-宏)
3. [編譯時計算](#3-編譯時計算)
4. [Scala 3 Inline](#4-scala-3-inline)
5. [引號與拼接](#5-引號與拼接)
6. [宏實作範例](#6-宏實作範例)
7. [反射與型別操作](#7-反射與型別操作)
8. [實用宏範例](#8-實用宏範例)
9. [除錯與測試](#9-除錯與測試)
10. [最佳實踐](#10-最佳實踐)

---

## 1. 宏概覽

### 1.1 什麼是宏?

```scala
// 宏 (Macro) 是在編譯時執行的程式碼
// 可以檢查、生成、轉換程式碼

// 普通函數:運行時執行
def square(x: Int): Int = x * x

// 宏:編譯時執行,生成程式碼
// 編譯器會將宏呼叫替換為生成的程式碼

// 範例:除錯宏
debug(x + y)  
// 編譯時展開為:
// println(s"x + y = ${x + y}")
```

### 1.2 為什麼使用宏?

```scala
// 1. 編譯時驗證
// 檢查 SQL 字串在編譯時是否有效
sql"SELECT * FROM users WHERE id = $id"

// 2. 效能優化
// 消除運行時反射開銷
case class Person(name: String, age: Int)
// 自動生成高效的序列化程式碼

// 3. 程式碼生成
// 自動生成樣板程式碼
@JsonCodec
case class User(id: Int, name: String)
// 自動生成 JSON 編碼/解碼器

// 4. DSL 實作
// 創建領域特定語言
html {
  head {
    title("My Page")
  }
  body {
    h1("Hello")
  }
}

// 5. 型別級編程
// 在型別層面進行計算
```

### 1.3 宏的限制

```scala
// ⚠️ 宏的缺點:

// 1. 複雜性
// - 需要理解編譯器內部
// - 除錯困難
// - 學習曲線陡峭

// 2. 編譯時間
// - 增加編譯時間
// - 可能影響開發效率

// 3. 工具支援
// - IDE 支援有限
// - 錯誤訊息可能不清楚

// 4. 版本相容性
// - Scala 2 和 Scala 3 宏不相容
// - 需要分別維護

// 因此:只在必要時使用宏!
```

---

## 2. Scala 2 vs Scala 3 宏

### 2.1 Scala 2 宏系統

```scala
// Scala 2 宏使用反射 API (已不推薦)

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object Macros {
  // 宏介面
  def debug(x: Any): Unit = macro debugImpl
  
  // 宏實作
  def debugImpl(c: blackbox.Context)(x: c.Tree): c.Tree = {
    import c.universe._
    
    val valueTree = x
    val valueString = show(x)
    
    q"""
      {
        val value = $valueTree
        println(s"$valueString = " + value)
        value
      }
    """
  }
}

// 使用
val x = 10
val y = 20
Macros.debug(x + y)  // 輸出: "x + y = 30"

// 問題:
// - API 複雜
// - 型別安全性差
// - 需要分離的編譯單元
```

### 2.2 Scala 3 宏系統

```scala
// Scala 3 使用新的宏系統:更簡單、更安全

// 1. Inline (內聯)
inline def square(x: Int): Int = x * x

// 2. 編譯時操作
import scala.quoted.*

inline def debug[T](inline x: T): T = ${debugImpl('x)}

def debugImpl[T: Type](x: Expr[T])(using Quotes): Expr[T] = {
  import quotes.reflect.*
  
  val valueExpr = x
  val valueString = x.show
  
  '{
    val value = $valueExpr
    println(s"$valueString = " + value)
    value
  }
}

// 使用
val result = debug(10 + 20)  // 輸出: "10 + 20 = 30"

// 優點:
// - 型別安全
// - 更清晰的 API
// - 不需要分離編譯
```

### 2.3 遷移指南

```scala
// Scala 2 → Scala 3 遷移

// Scala 2 寫法
import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros

def myMacro(x: Int): Int = macro myMacroImpl

def myMacroImpl(c: Context)(x: c.Tree): c.Tree = {
  import c.universe._
  q"$x * 2"
}

// Scala 3 等價寫法
import scala.quoted.*

inline def myMacro(x: Int): Int = ${myMacroImpl('x)}

def myMacroImpl(x: Expr[Int])(using Quotes): Expr[Int] = {
  '{ $x * 2 }
}

// 建議:
// - 新專案使用 Scala 3 宏
// - 舊專案可以保持 Scala 2 宏
// - 使用跨版本函式庫時注意相容性
```

---

## 3. 編譯時計算

### 3.1 Inline 基礎

```scala
// inline 關鍵字告訴編譯器在呼叫處展開程式碼

// 普通函數
def add(x: Int, y: Int): Int = x + y

val result1 = add(1, 2)
// 生成:
// val result1 = add(1, 2)  // 運行時呼叫

// Inline 函數
inline def addInline(x: Int, y: Int): Int = x + y

val result2 = addInline(1, 2)
// 生成:
// val result2 = 1 + 2  // 編譯時展開
// 進一步優化:
// val result2 = 3  // 常量折疊

// Inline 的優勢:
// - 消除函數呼叫開銷
// - 啟用進一步優化
// - 可以用於宏
```

### 3.2 Inline Match

```scala
// inline match 在編譯時求值

inline def selectColor(inline choice: Int): String = inline choice match {
  case 1 => "red"
  case 2 => "green"
  case 3 => "blue"
  case _ => "unknown"
}

val color = selectColor(2)
// 編譯時展開為:
// val color = "green"

// 錯誤檢查
// val badColor = selectColor(x)  // 編譯錯誤:必須是常量!

// 實用範例:編譯時配置
inline def getConfig(inline key: String): String = inline key match {
  case "env" => "production"
  case "host" => "localhost"
  case "port" => "8080"
  case _ => compiletime.error("Unknown config key")
}

val env = getConfig("env")  // "production"
// val bad = getConfig("invalid")  // 編譯錯誤!
```

### 3.3 Compiletime 操作

```scala
import scala.compiletime.*

// 1. 編譯時錯誤
inline def requirePositive(inline x: Int): Int = {
  inline if (x <= 0) {
    error("Value must be positive")
  }
  x
}

val good = requirePositive(10)  // OK
// val bad = requirePositive(-5)  // 編譯錯誤!

// 2. 型別操作
inline def typeString[T]: String = {
  constValue[T] match {
    case _: Int => "Integer"
    case _: String => "Text"
    case _ => "Unknown"
  }
}

// 3. 元組操作
type MyTuple = (Int, String, Boolean)

inline def tupleSize[T <: Tuple]: Int = {
  constValue[Tuple.Size[T]]
}

val size = tupleSize[MyTuple]  // 3

// 4. 求和型別 (Sum Types)
sealed trait Color
case object Red extends Color
case object Green extends Color
case object Blue extends Color

inline def colorCount: Int = {
  constValue[Tuple.Size[Tuple.Union[Color]]]
}
```

---

## 4. Scala 3 Inline

### 4.1 基本 Inline

```scala
// inline 參數
inline def power(x: Double, inline n: Int): Double = {
  inline if (n == 0) 1.0
  else inline if (n == 1) x
  else x * power(x, n - 1)
}

val result = power(2.0, 3)
// 編譯時展開為:
// val result = 2.0 * 2.0 * 2.0

// inline 條件
inline def max(inline a: Int, inline b: Int): Int = {
  inline if (a > b) a else b
}

val m = max(10, 20)
// 展開為: val m = 20
```

### 4.2 Transparent Inline

```scala
// transparent inline 保留精確型別

// 普通 inline:返回型別固定
inline def identify[T](x: T): T = x

val x1 = identify(42)  // x1: Int
val x2 = identify("hi")  // x2: String

// transparent inline:返回最具體的型別
transparent inline def select(inline choice: Boolean): Any = {
  inline if (choice) 42 else "text"
}

val y1 = select(true)   // y1: Int (不是 Any!)
val y2 = select(false)  // y2: String

// 實用範例:型別級選擇
transparent inline def chooseType[A, B](inline useA: Boolean): Any = {
  inline if (useA) 
    summon[A]
  else 
    summon[B]
}
```

---

## 5. 引號與拼接

### 5.1 引號 (Quotes)

```scala
import scala.quoted.*

// 引號 '{...} 表示程式碼片段
// Expr[T] 表示型別為 T 的程式碼

def exampleQuote(using Quotes): Expr[Int] = {
  '{42}  // Expr[Int]
}

def exampleQuote2(using Quotes): Expr[String] = {
  '{"hello"}  // Expr[String]
}

// 組合表達式
def addExprs(a: Expr[Int], b: Expr[Int])(using Quotes): Expr[Int] = {
  '{$a + $b}  // 使用 $a, $b 拼接
}

// 範例:生成列表
def makeList(using Quotes): Expr[List[Int]] = {
  '{List(1, 2, 3, 4, 5)}
}
```

### 5.2 拼接 (Splicing)

```scala
import scala.quoted.*

// $expr 拼接表達式到引號中

def doubleExpr(x: Expr[Int])(using Quotes): Expr[Int] = {
  '{$x * 2}
}

// 在宏中使用
inline def double(x: Int): Int = ${doubleImpl('x)}

def doubleImpl(x: Expr[Int])(using Quotes): Expr[Int] = {
  '{$x * 2}
}

val result = double(21)  // 42

// 多個拼接
def sumExprs(exprs: Seq[Expr[Int]])(using Quotes): Expr[Int] = {
  exprs.reduce((a, b) => '{$a + $b})
}

// 條件拼接
def conditionalExpr(
  cond: Expr[Boolean], 
  thenBranch: Expr[Int], 
  elseBranch: Expr[Int]
)(using Quotes): Expr[Int] = {
  '{if ($cond) $thenBranch else $elseBranch}
}
```

### 5.3 模式匹配引號

```scala
import scala.quoted.*

// 匹配程式碼結構
def analyzeExpr(expr: Expr[Int])(using Quotes): String = {
  expr match {
    case '{42} => "literal 42"
    case '{($x: Int) + ($y: Int)} => s"addition: $x + $y"
    case '{($x: Int) * ($y: Int)} => s"multiplication: $x * $y"
    case _ => "other expression"
  }
}

// 提取子表達式
def extractAddition(expr: Expr[Int])(using Quotes): Option[(Expr[Int], Expr[Int])] = {
  expr match {
    case '{($a: Int) + ($b: Int)} => Some((a, b))
    case _ => None
  }
}

// 遞迴分析
def simplify(expr: Expr[Int])(using Quotes): Expr[Int] = {
  expr match {
    case '{0 + ($x: Int)} => x
    case '{($x: Int) + 0} => x
    case '{0 * ($x: Int)} => '{0}
    case '{($x: Int) * 0} => '{0}
    case '{1 * ($x: Int)} => x
    case '{($x: Int) * 1} => x
    case _ => expr
  }
}
```

---

## 6. 宏實作範例

### 6.1 Debug 宏

```scala
import scala.quoted.*

// 顯示表達式和其值
inline def debug[T](inline expr: T): T = ${debugImpl('expr)}

def debugImpl[T: Type](expr: Expr[T])(using Quotes): Expr[T] = {
  import quotes.reflect.*
  
  // 取得表達式的字串表示
  val exprStr = Expr(expr.show)
  
  // 生成程式碼
  '{
    val value = $expr
    println(s"${$exprStr} = " + value)
    value
  }
}

// 使用
val x = 10
val y = 20
val result = debug(x + y)
// 輸出: "x + y = 30"
// result = 30
```

### 6.2 Assert 宏

```scala
import scala.quoted.*

// 編譯時檢查的 assert
inline def staticAssert(inline condition: Boolean, inline msg: String): Unit = {
  inline if (!condition) {
    scala.compiletime.error(msg)
  }
}

// 運行時 assert 帶詳細資訊
inline def assert(inline condition: Boolean): Unit = {
  ${assertImpl('condition)}
}

def assertImpl(condition: Expr[Boolean])(using Quotes): Expr[Unit] = {
  import quotes.reflect.*
  
  val condStr = Expr(condition.show)
  
  '{
    if (!$condition) {
      throw new AssertionError(s"Assertion failed: ${$condStr}")
    }
  }
}

// 使用
val x = 5
assert(x > 0)  // OK
// assert(x > 10)  // 拋出: Assertion failed: x > 10

// 編譯時檢查
staticAssert(1 + 1 == 2, "Math broken!")  // OK
// staticAssert(1 + 1 == 3, "Math broken!")  // 編譯錯誤!
```

### 6.3 Enum Values 宏

```scala
import scala.quoted.*
import scala.compiletime.*

// 列舉所有值
enum Color:
  case Red, Green, Blue

// 取得所有列舉值
inline def enumValues[E](using m: deriving.Mirror.SumOf[E]): List[E] = {
  ${enumValuesImpl[E]}
}

def enumValuesImpl[E: Type](using Quotes): Expr[List[E]] = {
  import quotes.reflect.*
  
  // 使用反射取得所有 case
  val tpe = TypeRepr.of[E]
  val sym = tpe.typeSymbol
  
  if (!sym.flags.is(Flags.Enum)) {
    report.error(s"${sym.name} is not an enum")
    return '{Nil}
  }
  
  val children = sym.children.filter(_.flags.is(Flags.Case))
  
  val values = children.map { child =>
    Ref(child).asExprOf[E]
  }
  
  Expr.ofList(values)
}

// 使用
val colors = enumValues[Color]
// List(Red, Green, Blue)
```

### 6.4 Show Macro

```scala
import scala.quoted.*

// 自動生成 Show 實例
trait Show[T] {
  def show(value: T): String
}

object Show {
  inline def derived[T]: Show[T] = ${derivedImpl[T]}
  
  def derivedImpl[T: Type](using Quotes): Expr[Show[T]] = {
    import quotes.reflect.*
    
    val tpe = TypeRepr.of[T]
    val typeName = tpe.typeSymbol.name
    
    tpe.asType match {
      case '[t] =>
        '{
          new Show[t] {
            def show(value: t): String = {
              // 簡化版本:只顯示型別名稱
              s"$typeName(...)"
            }
          }
        }
    }
  }
}

// 使用
case class Person(name: String, age: Int) derives Show

val p = Person("Alice", 25)
val shower = summon[Show[Person]]
println(shower.show(p))  // "Person(...)"
```

---

## 7. 反射與型別操作

### 7.1 型別反射

```scala
import scala.quoted.*

def inspectType[T: Type](using Quotes): Unit = {
  import quotes.reflect.*
  
  val tpe = TypeRepr.of[T]
  
  println(s"Type: ${tpe.show}")
  println(s"Symbol: ${tpe.typeSymbol.name}")
  println(s"Is case class: ${tpe.typeSymbol.flags.is(Flags.Case)}")
  
  // 取得欄位
  if (tpe.typeSymbol.flags.is(Flags.Case)) {
    val fields = tpe.typeSymbol.caseFields
    println("Fields:")
    fields.foreach { field =>
      println(s"  - ${field.name}: ${field.tree}")
    }
  }
}

// 使用
case class Person(name: String, age: Int)

inline def inspect[T]: Unit = ${inspectTypeImpl[T]}

def inspectTypeImpl[T: Type](using Quotes): Expr[Unit] = {
  inspectType[T]
  '{()}
}

inspect[Person]
// 輸出:
// Type: Person
// Symbol: Person
// Is case class: true
// Fields:
//   - name: ...
//   - age: ...
```

### 7.2 Case Class 反射

```scala
import scala.quoted.*

// 取得 case class 欄位名稱
inline def fieldNames[T]: List[String] = ${fieldNamesImpl[T]}

def fieldNamesImpl[T: Type](using Quotes): Expr[List[String]] = {
  import quotes.reflect.*
  
  val tpe = TypeRepr.of[T]
  val fields = tpe.typeSymbol.caseFields
  
  val names = fields.map(f => Expr(f.name))
  
  Expr.ofList(names)
}

// 使用
case class User(id: Int, name: String, email: String)

val names = fieldNames[User]
// List("id", "name", "email")

// 取得欄位值
inline def fieldValues[T](value: T): List[Any] = ${fieldValuesImpl('value)}

def fieldValuesImpl[T: Type](value: Expr[T])(using Quotes): Expr[List[Any]] = {
  import quotes.reflect.*
  
  val tpe = TypeRepr.of[T]
  val fields = tpe.typeSymbol.caseFields
  
  val values = fields.map { field =>
    val select = Select(value.asTerm, field).asExpr
    '{$select: Any}
  }
  
  Expr.ofList(values)
}

val user = User(1, "Alice", "alice@example.com")
val values = fieldValues(user)
// List(1, "Alice", "alice@example.com")
```

### 7.3 泛型型別操作

```scala
import scala.quoted.*

// 檢查型別參數
def analyzeGeneric[F[_], A](using 
  Quotes, 
  Type[F], 
  Type[A]
): String = {
  import quotes.reflect.*
  
  val fTpe = TypeRepr.of[F]
  val aTpe = TypeRepr.of[A]
  
  s"Container: ${fTpe.show}, Element: ${aTpe.show}"
}

inline def analyze[F[_], A]: String = ${analyzeGenericImpl[F, A]}

def analyzeGenericImpl[F[_], A](using 
  Quotes, 
  Type[F], 
  Type[A]
): Expr[String] = {
  Expr(analyzeGeneric[F, A])
}

val result = analyze[List, Int]
// "Container: List, Element: Int"
```

---

## 8. 實用宏範例

### 8.1 JSON 宏

```scala
import scala.quoted.*

// 自動生成 JSON 編碼器
trait JsonEncoder[T] {
  def encode(value: T): String
}

object JsonEncoder {
  inline def derived[T]: JsonEncoder[T] = ${derivedImpl[T]}
  
  def derivedImpl[T: Type](using Quotes): Expr[JsonEncoder[T]] = {
    import quotes.reflect.*
    
    val tpe = TypeRepr.of[T]
    
    if (!tpe.typeSymbol.flags.is(Flags.Case)) {
      report.error("Only case classes supported")
      return '{new JsonEncoder[T] { def encode(value: T) = "{}" }}
    }
    
    val fields = tpe.typeSymbol.caseFields
    
    '{
      new JsonEncoder[T] {
        def encode(value: T): String = {
          val parts = List(
            ${Expr.ofList(fields.map { field =>
              val fieldName = field.name
              val getter = Select('{value}.asTerm, field).asExpr
              '{s""""$fieldName":${$getter.toString}"""}
            })}
          )
          parts.mkString("{", ",", "}")
        }
      }
    }
  }
}

// 使用
case class Person(name: String, age: Int) derives JsonEncoder

val encoder = summon[JsonEncoder[Person]]
val json = encoder.encode(Person("Alice", 25))
// {"name":"Alice","age":25}
```

### 8.2 SQL 宏

```scala
import scala.quoted.*

// 編譯時檢查 SQL
case class SQL(query: String)

object SQL {
  inline def apply(inline query: String): SQL = ${sqlImpl('query)}
  
  def sqlImpl(query: Expr[String])(using Quotes): Expr[SQL] = {
    import quotes.reflect.*
    
    // 取得字串常量
    query.value match {
      case Some(sql) =>
        // 簡單驗證
        if (!sql.toLowerCase.startsWith("select")) {
          report.error("SQL must start with SELECT")
        }
        if (sql.contains(";")) {
          report.error("Multiple statements not allowed")
        }
        
        '{new SQL($query)}
      
      case None =>
        report.error("SQL query must be a string literal")
        '{new SQL("")}
    }
  }
}

// 使用
val query1 = SQL("SELECT * FROM users")  // OK
// val query2 = SQL("DROP TABLE users")  // 編譯錯誤!
// val query3 = SQL("SELECT * FROM users; DELETE FROM users")  // 編譯錯誤!
```

### 8.3 測試宏

```scala
import scala.quoted.*

// 自動生成測試
inline def autoTest[T](inline value: T, inline expected: T): Unit = {
  ${autoTestImpl('value, 'expected)}
}

def autoTestImpl[T: Type](
  value: Expr[T], 
  expected: Expr[T]
)(using Quotes): Expr[Unit] = {
  import quotes.reflect.*
  
  val valueStr = value.show
  val expectedStr = expected.show
  
  '{
    val actual = $value
    val exp = $expected
    
    if (actual != exp) {
      throw new AssertionError(
        s"Test failed:\n" +
        s"  Expression: $valueStr\n" +
        s"  Expected: $exp\n" +
        s"  Actual: $actual"
      )
    } else {
      println(s"✓ $valueStr")
    }
  }
}

// 使用
autoTest(1 + 1, 2)  // ✓ 1 + 1
// autoTest(1 + 1, 3)  // 拋出詳細錯誤
```

### 8.4 效能計時宏

```scala
import scala.quoted.*

// 測量執行時間
inline def time[T](inline expr: T): T = ${timeImpl('expr)}

def timeImpl[T: Type](expr: Expr[T])(using Quotes): Expr[T] = {
  import quotes.reflect.*
  
  val exprStr = Expr(expr.show)
  
  '{
    val start = System.nanoTime()
    val result = $expr
    val end = System.nanoTime()
    val duration = (end - start) / 1_000_000.0
    
    println(f"${$exprStr} took $duration%.2f ms")
    result
  }
}

// 使用
val result = time {
  (1 to 1000000).sum
}
// 輸出: "(1 to 1000000).sum took 15.43 ms"
```

---

## 9. 除錯與測試

### 9.1 顯示生成的程式碼

```scala
import scala.quoted.*

// 使用 show 方法顯示生成的程式碼
def debugMacro[T: Type](expr: Expr[T])(using Quotes): Unit = {
  import quotes.reflect.*
  
  println("Expression:")
  println(expr.show)
  
  println("\nTree:")
  println(expr.asTerm.show(using Printer.TreeStructure))
}

// 在宏中使用
def myMacroImpl[T: Type](expr: Expr[T])(using Quotes): Expr[T] = {
  debugMacro(expr)  // 顯示除錯資訊
  expr
}
```

### 9.2 編譯器選項

```scala
// 在 build.sbt 中啟用宏除錯
scalacOptions ++= Seq(
  "-Xprint:typer",           // 顯示型別檢查後的程式碼
  "-Xprint-types",           // 顯示型別資訊
  "-Vprint:all",             // 顯示所有編譯階段
  "-Ycheck:all",             // 檢查所有階段
  "-Xlog-implicits"          // 顯示隱式解析
)

// 只針對特定檔案
scalacOptions ++= Seq(
  "-Vprint-args", "MyMacro.scala"
)
```

### 9.3 單元測試

```scala
import scala.quoted.*
import org.junit.Test
import org.junit.Assert.*

class MacroTest {
  // 測試宏展開
  @Test
  def testDebugMacro(): Unit = {
    val result = debug(1 + 2)
    assertEquals(3, result)
  }
  
  // 測試編譯時錯誤
  @Test
  def testCompileTimeError(): Unit = {
    // 使用 compile-time testing
    assertDoesNotCompile("""
      val x = "not a number"
      staticAssert(x.toInt > 0, "Must be positive")
    """)
  }
  
  // 測試生成的程式碼
  @Test
  def testCodeGeneration(): Unit = {
    case class Person(name: String) derives JsonEncoder
    
    val encoder = summon[JsonEncoder[Person]]
    val json = encoder.encode(Person("Alice"))
    
    assertTrue(json.contains("Alice"))
  }
}
```

---

## 10. 最佳實踐

### 10.1 何時使用宏

```scala
// ✅ 好的使用場景:

// 1. 消除樣板程式碼
case class User(id: Int, name: String) derives JsonCodec

// 2. 編譯時驗證
sql"SELECT * FROM users WHERE id = $id"

// 3. 效能關鍵路徑
// 消除運行時反射
inline def fastSerializer[T]: Serializer[T] = ...

// 4. DSL 實作
html {
  body {
    h1("Title")
  }
}

// ❌ 避免的場景:

// 1. 可以用普通函數實作
// 不需要: inline def add(x: Int, y: Int) = x + y
// 使用: def add(x: Int, y: Int) = x + y

// 2. 過度優化
// 除非確認是瓶頸,否則不要用宏優化

// 3. 複雜邏輯
// 宏應該簡單,複雜邏輯放在普通函數中
```

### 10.2 錯誤處理

```scala
import scala.quoted.*

def safeMacroImpl[T: Type](expr: Expr[T])(using Quotes): Expr[T] = {
  import quotes.reflect.*
  
  try {
    // 宏邏輯
    expr
  } catch {
    case e: Exception =>
      report.error(s"Macro failed: ${e.getMessage}")
      expr  // 返回原始表達式
  }
}

// 提供清晰的錯誤訊息
def validateImpl(expr: Expr[String])(using Quotes): Expr[String] = {
  import quotes.reflect.*
  
  expr.value match {
    case Some(s) if s.isEmpty =>
      report.error(
        "String cannot be empty",
        expr  // 指出錯誤位置
      )
      expr
    
    case Some(s) =>
      expr
    
    case None =>
      report.error("Expected string literal")
      expr
  }
}
```

### 10.3 效能考量

```scala
// 1. 避免過度內聯
// ❌ 不好:內聯大型函數
inline def processLargeData(data: List[Int]): List[Int] = {
  // 100 行程式碼...
  data.map(_ * 2).filter(_ > 0).sorted
}

// ✅ 好:只內聯小函數
inline def double(x: Int): Int = x * 2

def processLargeData(data: List[Int]): List[Int] = {
  data.map(double).filter(_ > 0).sorted
}

// 2. 編譯時間 vs 運行時間
// 權衡編譯時間增加和運行時效能提升

// 3. 快取宏結果
// 如果可能,快取昂貴的編譯時計算
```

### 10.4 文檔與測試

```scala
/**
 * Debug macro that prints expression and its value.
 * 
 * Example:
 * {{{
 * val x = 10
 * debug(x + 5)  // 輸出: "x + 5 = 15"
 * }}}
 * 
 * @param expr The expression to debug
 * @return The value of the expression
 */
inline def debug[T](inline expr: T): T = ${debugImpl('expr)}

// 提供測試
class DebugMacroTest {
  @Test
  def testSimpleExpression(): Unit = {
    val result = debug(1 + 2)
    assertEquals(3, result)
  }
  
  @Test
  def testComplexExpression(): Unit = {
    val x = 10
    val result = debug(x * 2 + 5)
    assertEquals(25, result)
  }
}
```

---

## 11. 實作練習

### 練習 1: Logging 宏

```scala
import scala.quoted.*

// 日誌等級
enum LogLevel:
  case Debug, Info, Warn, Error

// 條件編譯日誌
object Logger {
  // 編譯時日誌等級
  inline val compiledLevel: LogLevel = LogLevel.Info
  
  inline def debug(inline msg: String): Unit = {
    inline if (shouldLog(LogLevel.Debug)) {
      ${logImpl('msg, '{LogLevel.Debug})}
    }
  }
  
  inline def info(inline msg: String): Unit = {
    inline if (shouldLog(LogLevel.Info)) {
      ${logImpl('msg, '{LogLevel.Info})}
    }
  }
  
  inline def warn(inline msg: String): Unit = {
    inline if (shouldLog(LogLevel.Warn)) {
      ${logImpl('msg, '{LogLevel.Warn})}
    }
  }
  
  inline def error(inline msg: String): Unit = {
    inline if (shouldLog(LogLevel.Error)) {
      ${logImpl('msg, '{LogLevel.Error})}
    }
  }
  
  private inline def shouldLog(inline level: LogLevel): Boolean = {
    level.ordinal >= compiledLevel.ordinal
  }
  
  private def logImpl(
    msg: Expr[String], 
    level: Expr[LogLevel]
  )(using Quotes): Expr[Unit] = {
    import quotes.reflect.*
    
    val position = Position.ofMacroExpansion
    val file = Expr(position.sourceFile.name)
    val line = Expr(position.startLine + 1)
    
    '{
      val timestamp = java.time.LocalDateTime.now()
      println(s"[$timestamp] [${$level}] ${$file}:${$line} - ${$msg}")
    }
  }
}

// 使用
object App {
  def main(args: Array[String]): Unit = {
    Logger.debug("This won't show")  // 編譯時移除
    Logger.info("Application started")
    Logger.warn("Low memory")
    Logger.error("Connection failed")
  }
}
```

### 練習 2: Builder 宏

```scala
import scala.quoted.*

// 自動生成 builder
trait Builder[T] {
  def build(): T
}

object Builder {
  inline def derived[T]: BuilderFactory[T] = ${derivedImpl[T]}
  
  def derivedImpl[T: Type](using Quotes): Expr[BuilderFactory[T]] = {
    import quotes.reflect.*
    
    val tpe = TypeRepr.of[T]
    val fields = tpe.typeSymbol.caseFields
    
    val className = tpe.typeSymbol.name
    val builderName = s"${className}Builder"
    
    // 生成 builder 類別
    '{
      new BuilderFactory[T] {
        def create(): Builder[T] = new Builder[T] {
          private var values = Map.empty[String, Any]
          
          def set(field: String, value: Any): this.type = {
            values = values + (field -> value)
            this
          }
          
          def build(): T = {
            // 簡化版本:使用反射建立實例
            // 實際應該生成類型安全的程式碼
            ???
          }
        }
      }
    }
  }
}

trait BuilderFactory[T] {
  def create(): Builder[T]
}

// 使用
case class Person(name: String, age: Int, email: String) derives Builder

val factory = summon[BuilderFactory[Person]]
val person = factory.create()
  .set("name", "Alice")
  .set("age", 25)
  .set("email", "alice@example.com")
  .build()
```

### 練習 3: Enum 宏

```scala
import scala.quoted.*

// 列舉輔助函數
object EnumMacros {
  // 取得所有值
  inline def values[E]: Array[E] = ${valuesImpl[E]}
  
  def valuesImpl[E: Type](using Quotes): Expr[Array[E]] = {
    import quotes.reflect.*
    
    val tpe = TypeRepr.of[E]
    val sym = tpe.typeSymbol
    
    if (!sym.flags.is(Flags.Enum)) {
      report.error(s"${sym.name} is not an enum")
      return '{Array.empty[E]}
    }
    
    val cases = sym.children
      .filter(_.flags.is(Flags.Case))
      .map(c => Ref(c).asExprOf[E])
    
    Expr.ofArray(cases)
  }
  
  // 從字串解析
  inline def fromString[E](s: String): Option[E] = ${fromStringImpl[E]('s)}
  
  def fromStringImpl[E: Type](s: Expr[String])(using Quotes): Expr[Option[E]] = {
    import quotes.reflect.*
    
    val tpe = TypeRepr.of[E]
    val sym = tpe.typeSymbol
    val cases = sym.children.filter(_.flags.is(Flags.Case))
    
    val branches = cases.map { c =>
      val name = c.name
      CaseDef(
        Literal(StringConstant(name)),
        None,
        '{Some(${Ref(c).asExprOf[E]})}.asTerm
      )
    }
    
    val defaultCase = CaseDef(
      Wildcard(),
      None,
      '{None}.asTerm
    )
    
    Match(s.asTerm, branches :+ defaultCase).asExprOf[Option[E]]
  }
  
  // 取得名稱
  inline def nameOf[E](e: E): String = ${nameOfImpl('e)}
  
  def nameOfImpl[E: Type](e: Expr[E])(using Quotes): Expr[String] = {
    import quotes.reflect.*
    
    e.asTerm match {
      case Ident(name) => Expr(name)
      case Select(_, name) => Expr(name)
      case _ => 
        report.error("Cannot determine enum name")
        Expr("")
    }
  }
}

// 使用
enum Color:
  case Red, Green, Blue

val all = EnumMacros.values[Color]
// Array(Red, Green, Blue)

val red = EnumMacros.fromString[Color]("Red")
// Some(Red)

val name = EnumMacros.nameOf(Color.Blue)
// "Blue"
```

### 練習 4: 驗證宏

```scala
import scala.quoted.*
import scala.compiletime.*

// 編譯時字串驗證
object Validators {
  // Email 驗證
  inline def email(inline s: String): String = {
    ${emailImpl('s)}
  }
  
  def emailImpl(s: Expr[String])(using Quotes): Expr[String] = {
    import quotes.reflect.*
    
    s.value match {
      case Some(email) =>
        val emailRegex = """^[\w\.-]+@[\w\.-]+\.\w+$"""
        if (!email.matches(emailRegex)) {
          report.error(s"Invalid email: $email")
        }
        s
      
      case None =>
        // 運行時驗證
        '{
          val email = $s
          val emailRegex = """^[\w\.-]+@[\w\.-]+\.\w+$"""
          require(email.matches(emailRegex), s"Invalid email: $email")
          email
        }
    }
  }
  
  // URL 驗證
  inline def url(inline s: String): String = {
    ${urlImpl('s)}
  }
  
  def urlImpl(s: Expr[String])(using Quotes): Expr[String] = {
    import quotes.reflect.*
    
    s.value match {
      case Some(url) =>
        try {
          new java.net.URL(url)
          s
        } catch {
          case _: Exception =>
            report.error(s"Invalid URL: $url")
            s
        }
      
      case None =>
        '{
          val url = $s
          try {
            new java.net.URL(url)
            url
          } catch {
            case e: Exception =>
              throw new IllegalArgumentException(s"Invalid URL: $url", e)
          }
        }
    }
  }
  
  // 範圍驗證
  inline def inRange(inline value: Int, inline min: Int, inline max: Int): Int = {
    inline if (value < min || value > max) {
      error(s"Value $value not in range [$min, $max]")
    }
    value
  }
}

// 使用
val email = Validators.email("alice@example.com")  // OK
// val bad = Validators.email("invalid")  // 編譯錯誤!

val url = Validators.url("https://example.com")  // OK
// val badUrl = Validators.url("not a url")  // 編譯錯誤!

val age = Validators.inRange(25, 0, 150)  // OK
// val badAge = Validators.inRange(200, 0, 150)  // 編譯錯誤!
```

### 練習 5: 序列化宏

```scala
import scala.quoted.*

// 二進位序列化
trait BinaryCodec[T] {
  def encode(value: T): Array[Byte]
  def decode(bytes: Array[Byte]): T
}

object BinaryCodec {
  inline def derived[T]: BinaryCodec[T] = ${derivedImpl[T]}
  
  def derivedImpl[T: Type](using Quotes): Expr[BinaryCodec[T]] = {
    import quotes.reflect.*
    
    val tpe = TypeRepr.of[T]
    
    if (!tpe.typeSymbol.flags.is(Flags.Case)) {
      report.error("Only case classes supported")
      return '{
        new BinaryCodec[T] {
          def encode(value: T): Array[Byte] = Array.empty
          def decode(bytes: Array[Byte]): T = ???
        }
      }
    }
    
    val fields = tpe.typeSymbol.caseFields
    
    // 生成編碼邏輯
    val encodeExprs = fields.map { field =>
      val getter = Select('{???}.asTerm, field)
      val fieldType = field.tree match {
        case ValDef(_, tpt, _) => tpt.tpe
        case _ => TypeRepr.of[Any]
      }
      
      // 根據型別生成序列化程式碼
      fieldType.asType match {
        case '[Int] => 
          '{
            val value = ${getter.asExprOf[Int]}
            Array(
              (value >> 24).toByte,
              (value >> 16).toByte,
              (value >> 8).toByte,
              value.toByte
            )
          }
        
        case '[String] =>
          '{
            val value = ${getter.asExprOf[String]}
            value.getBytes("UTF-8")
          }
        
        case _ =>
          report.error(s"Unsupported field type: ${fieldType.show}")
          '{Array.empty[Byte]}
      }
    }
    
    '{
      new BinaryCodec[T] {
        def encode(value: T): Array[Byte] = {
          // 組合所有欄位的位元組
          ${Expr.ofList(encodeExprs)}.flatten.toArray
        }
        
        def decode(bytes: Array[Byte]): T = {
          // 簡化版本
          ???
        }
      }
    }
  }
}

// 使用
case class Point(x: Int, y: Int) derives BinaryCodec

val codec = summon[BinaryCodec[Point]]
val bytes = codec.encode(Point(10, 20))
val point = codec.decode(bytes)
```

---

## 12. 重點總結

### 宏的核心概念
- **編譯時執行**: 在編譯時生成或轉換程式碼
- **型別安全**: Scala 3 宏提供更好的型別安全
- **效能**: 消除運行時開銷

### Scala 3 宏系統
- **Inline**: 內聯展開
- **Quotes**: 引號表示程式碼 `'{...}`
- **Splicing**: 拼接插入程式碼 `${...}`
- **反射**: 檢查和操作型別

### 使用場景
- ✅ 消除樣板程式碼
- ✅ 編譯時驗證
- ✅ 效能優化
- ✅ DSL 實作

### 最佳實踐
- 只在必要時使用
- 保持簡單
- 提供清晰錯誤訊息
- 充分測試
- 完善文檔

---

## 參考資源

### 官方文檔
- Scala 3 Metaprogramming: https://docs.scala-lang.org/scala3/guides/macros/
- Scala 3 Quotes API: https://dotty.epfl.ch/docs/reference/metaprogramming/

### 學習資源
- "Programming in Scala" - Martin Odersky
- Scala 3 宏教學
- 開源專案範例

### 工具
- Scala 3 編譯器
- Metals (LSP server)
- IntelliJ IDEA

---

恭喜您完成 Scala 宏的學習!宏是強大但複雜的工具,建議:
1. 先精通 Scala 基礎和進階特性
2. 理解何時真正需要宏
3. 從簡單範例開始練習
4. 研究優秀開源專案的宏實作

記住:大多數情況下,普通的 Scala 特性就足夠了!

---

> [« 上一篇：進階主題](scala_part8_advanced_topics.md) | [📚 目錄](../README.md)
