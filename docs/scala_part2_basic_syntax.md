# Scala 教學 - 第二部分:基本語法

> [« 上一篇：Scala 簡介與環境設置](scala_part1_introduction.md) | [📚 目錄](../README.md) | [下一篇：函數與方法 »](scala_part3_functions.md)

---

## 目錄
1. [變數宣告](#1-變數宣告)
2. [基本資料型別](#2-基本資料型別)
3. [字串操作](#3-字串操作)
4. [運算子](#4-運算子)
5. [條件式](#5-條件式)
6. [迴圈](#6-迴圈)
7. [表達式 vs 陳述式](#7-表達式-vs-陳述式)
8. [程式碼區塊](#8-程式碼區塊)
9. [實作練習](#9-實作練習)

---

## 1. 變數宣告

### 1.1 val - 不可變變數 (推薦)

`val` 定義的變數在初始化後不能重新賦值,類似於 Java 的 `final` 或 JavaScript 的 `const`。

```scala
val name: String = "Alice"
val age: Int = 25

// 錯誤!不能重新賦值
// name = "Bob"  // 編譯錯誤: reassignment to val

// 型別推導 - 編譯器自動推斷型別
val city = "Taipei"        // 推導為 String
val population = 2600000   // 推導為 Int
val temperature = 25.5     // 推導為 Double
val isCapital = true       // 推導為 Boolean
```

**為什麼優先使用 val?**
- 程式更容易理解和維護
- 避免意外修改
- 支援函數式程式設計
- 多執行緒環境更安全

### 1.2 var - 可變變數

`var` 定義的變數可以重新賦值。

```scala
var counter: Int = 0
counter = 1     // OK
counter = 2     // OK

var message = "Hello"
message = "Hi"  // OK

// 但不能改變型別
// message = 123  // 錯誤!型別不符
```

**何時使用 var?**
```scala
// 計數器
var count = 0
for (i <- 1 to 10) {
  count += i
}

// 累加器
var sum = 0.0
val numbers = List(1.5, 2.5, 3.5)
numbers.foreach(n => sum += n)

// 狀態管理
var isRunning = true
while (isRunning) {
  // 某些條件下
  isRunning = false
}
```

**最佳實踐:**
```scala
// ❌ 不好的做法
var result = 0
for (i <- 1 to 10) {
  result += i
}

// ✅ 更好的做法 (函數式風格)
val result = (1 to 10).sum

// ❌ 不好的做法
var list = List(1, 2, 3)
list = list :+ 4

// ✅ 更好的做法
val list1 = List(1, 2, 3)
val list2 = list1 :+ 4  // 建立新的 list
```

### 1.3 lazy val - 延遲初始化

`lazy val` 只在第一次存取時才會初始化,適合用於耗時的計算。

```scala
lazy val expensiveComputation: Int = {
  println("正在執行耗時計算...")
  Thread.sleep(2000)  // 模擬耗時操作
  42
}

println("定義完成")
// 輸出: 定義完成

println(expensiveComputation)
// 輸出: 正在執行耗時計算...
//      42

println(expensiveComputation)
// 輸出: 42 (不會再次計算)
```

**實際應用範例:**
```scala
// 資料庫連線 (只在需要時才建立)
lazy val dbConnection = {
  println("建立資料庫連線...")
  // 建立並返回連線
  createConnection()
}

// 設定檔讀取
lazy val config = {
  println("讀取設定檔...")
  readConfigFile("config.json")
}

// 大型資料載入
lazy val largeDataset = {
  println("載入大型資料集...")
  loadDataFromFile("large_file.csv")
}
```

### 1.4 常數定義

Scala 沒有專門的 `const` 關鍵字,但可以使用 `val` 配合命名慣例:

```scala
// 常數通常使用大寫字母
object Constants {
  val PI: Double = 3.14159265359
  val E: Double = 2.71828182846
  val SPEED_OF_LIGHT: Int = 299792458  // m/s
  
  val MAX_RETRY: Int = 3
  val TIMEOUT_MS: Long = 5000
}

// 使用
println(Constants.PI)
```

---

## 2. 基本資料型別

Scala 中所有的型別都是物件,沒有原始型別 (primitive types)。

### 2.1 數值型別

```scala
// 整數型別
val byteVal: Byte = 127           // 8-bit, -128 到 127
val shortVal: Short = 32767       // 16-bit, -32768 到 32767
val intVal: Int = 2147483647      // 32-bit
val longVal: Long = 9223372036854775807L  // 64-bit, 需要 L 後綴

// 浮點數型別
val floatVal: Float = 3.14f       // 32-bit, 需要 f 後綴
val doubleVal: Double = 3.14159   // 64-bit

// 數值字面值的不同寫法
val decimal = 42
val hex = 0x2A          // 16進位
val binary = 0b101010   // Scala 3.3.8 支援的二進位字面值
val octal = 42          // Scala 3 不支援 052 這類舊式八進位字面值

// 使用底線增加可讀性
val million = 1_000_000
val billion = 1_000_000_000L
```

**數值運算範例:**
```scala
val a = 10
val b = 3

// 基本運算
val sum = a + b         // 13
val diff = a - b        // 7
val product = a * b     // 30
val quotient = a / b    // 3 (整數除法)
val remainder = a % b   // 1

// 型別提升
val intVal: Int = 10
val doubleVal: Double = 3.5
val result = intVal + doubleVal  // 13.5 (Int 自動提升為 Double)

// 型別轉換
val x: Int = 10
val y: Double = x.toDouble    // 10.0
val z: String = x.toString    // "10"
val w: Long = x.toLong        // 10L
```

### 2.2 布林型別

```scala
val isTrue: Boolean = true
val isFalse: Boolean = false

// 布林運算
val and = true && false   // false
val or = true || false    // true
val not = !true          // false

// 短路求值
def expensiveCheck(): Boolean = {
  println("執行昂貴的檢查")
  true
}

val result1 = false && expensiveCheck()  // 不會執行 expensiveCheck()
val result2 = true || expensiveCheck()   // 不會執行 expensiveCheck()
```

### 2.3 字元型別

```scala
val char: Char = 'A'
val digit: Char = '5'
val unicode: Char = '\u0041'  // 'A' 的 Unicode

// 特殊字元
val newline: Char = '\n'
val tab: Char = '\t'
val backslash: Char = '\\'
val quote: Char = '\''

// 字元運算
val a: Char = 'A'
val next: Char = (a + 1).toChar  // 'B'

println(a.isUpper)      // true
println(a.isLower)      // false
println(a.isDigit)      // false
println(a.toLower)      // 'a'
```

### 2.4 字串型別

```scala
val str: String = "Hello, Scala"

// 字串是不可變的
val s1 = "Hello"
val s2 = s1 + ", World"  // 建立新字串
// s1 仍然是 "Hello"

// 多行字串
val multiLine = """
  這是一個
  多行
  字串
"""

val formatted = """
  |第一行
  |第二行
  |第三行
  """.stripMargin  // 移除 | 前的空白
```

### 2.5 Unit 型別

`Unit` 類似於 Java 的 `void`,表示沒有有意義的返回值。

```scala
def printMessage(msg: String): Unit = {
  println(msg)
}

val result: Unit = printMessage("Hello")
println(result)  // ()

// Unit 的唯一值
val unit: Unit = ()
```

### 2.6 Nothing 和 Null

**Nothing:**
```scala
// Nothing 是所有型別的子型別
// 用於表示永不正常返回的函數

def error(message: String): Nothing = {
  throw new RuntimeException(message)
}

def infiniteLoop(): Nothing = {
  while (true) {}
}

// 在型別系統中很有用
val list: List[Nothing] = List()  // 空列表
```

**Null:**
```scala
// Null 是所有參考型別的子型別
// 最好避免使用,用 Option 替代

val nullString: String = null  // 可以,但不推薦
// val nullInt: Int = null     // 錯誤!值型別不能為 null

// ❌ 不推薦
def findUser(id: Int): String = {
  if (id > 0) "User" else null
}

// ✅ 推薦
def findUserSafe(id: Int): Option[String] = {
  if (id > 0) Some("User") else None
}
```

### 2.7 Any, AnyVal, AnyRef

```scala
// Any 是所有型別的根型別
val any1: Any = 42
val any2: Any = "Hello"
val any3: Any = true

// AnyVal 是所有值型別的父型別
val anyVal: AnyVal = 42
// val anyVal2: AnyVal = "Hello"  // 錯誤!String 不是 AnyVal

// AnyRef 是所有參考型別的父型別 (相當於 Java 的 Object)
val anyRef: AnyRef = "Hello"
val anyRef2: AnyRef = List(1, 2, 3)
```

**型別層次結構:**
```
        Any
       /   \
   AnyVal  AnyRef
   /  |  \    |  \
Int Double Char String List ...
      |
   Boolean
      |
   Unit
      |
   Nothing
```

---

## 3. 字串操作

### 3.1 字串插值

Scala 提供三種字串插值器:

**s 插值器 (最常用):**
```scala
val name = "Alice"
val age = 25

// 簡單插值
val greeting = s"Hello, $name!"
println(greeting)  // Hello, Alice!

// 表達式插值
val message = s"$name is $age years old"
val nextYear = s"Next year, $name will be ${age + 1}"

// 可以包含任何表達式
val calculation = s"10 + 20 = ${10 + 20}"

// 呼叫方法
val upper = s"${name.toUpperCase} is shouting!"
```

**f 插值器 (格式化):**
```scala
val pi = 3.14159265359

// 浮點數格式化
val formatted1 = f"Pi is approximately $pi%.2f"  // Pi is approximately 3.14
val formatted2 = f"Pi is $pi%.4f"                 // Pi is 3.1416

// 其他格式
val num = 42
val hex = f"$num%x"        // 2a (16進位)
val padded = f"$num%05d"   // 00042 (補零到5位)

// 多個變數
val name = "Alice"
val score = 95.5
val report = f"$name%s scored $score%.1f points"
```

**raw 插值器 (不處理轉義):**
```scala
// 正常字串會處理轉義字元
val normal = s"Line1\nLine2"
println(normal)
// Line1
// Line2

// raw 不處理轉義字元
val raw = raw"Line1\nLine2"
println(raw)  // Line1\nLine2

// 常用於正則表達式和檔案路徑
val path = raw"C:\Users\Documents\file.txt"
val regex = raw"\d{3}-\d{4}"
```

**自訂插值器:**
```scala
// 進階主題 - 可以建立自己的插值器
extension (sc: StringContext) {
  def json(args: Any*): String = {
    // 自訂邏輯
    sc.parts.zip(args).map { case (p, a) => p + a }.mkString
  }
}

val key = "name"
val value = "Alice"
val jsonStr = json"""{"$key": "$value"}"""
```

### 3.2 字串方法

```scala
val str = "Hello, Scala Programming"

// 長度
str.length              // 24

// 大小寫轉換
str.toLowerCase         // "hello, scala programming"
str.toUpperCase         // "HELLO, SCALA PROGRAMMING"
str.capitalize          // "Hello, scala programming"

// 檢查
str.isEmpty             // false
str.nonEmpty            // true
str.startsWith("Hello") // true
str.endsWith("ing")     // true
str.contains("Scala")   // true

// 搜尋
str.indexOf("Scala")    // 7
str.indexOf("Java")     // -1 (未找到)
str.lastIndexOf("a")    // 20

// 子字串
str.substring(7)        // "Scala Programming"
str.substring(7, 12)    // "Scala"
str.take(5)             // "Hello"
str.drop(7)             // "Scala Programming"
str.takeRight(11)       // "Programming"
str.dropRight(11)       // "Hello, Scala"

// 分割
str.split(" ")          // Array("Hello,", "Scala", "Programming")
str.split(",").map(_.trim)  // Array("Hello", "Scala Programming")

// 替換
str.replace("Scala", "Java")     // "Hello, Java Programming"
str.replaceAll("[aeiou]", "*")   // "H*ll*, Sc*l* Pr*gr*mm*ng"
str.replaceFirst("a", "A")       // "Hello, ScAla Programming"

// 去除空白
val padded = "  Hello  "
padded.trim             // "Hello"
padded.stripPrefix("  ")  // "Hello  "
padded.stripSuffix("  ")  // "  Hello"

// 重複
"Ha" * 3                // "HaHaHa"

// 反轉
str.reverse             // "gnimmargorP alacS ,olleH"

// 比較
"abc".compareTo("abd")  // -1 (負數表示小於)
"abc" == "abc"          // true
"abc".equals("abc")     // true
"abc".equalsIgnoreCase("ABC")  // true
```

### 3.3 多行字串處理

```scala
// 三引號字串
val poem = """
  |Roses are red,
  |Violets are blue,
  |Scala is awesome,
  |And so are you!
  """.stripMargin

println(poem)

// 自訂邊界符號
val code = """
  #def hello(): Unit = {
  #  println("Hello")
  #}
  """.stripMargin('#')

// 保留縮排
val indented = """
    First line
    Second line
      Indented line
  """.trim

// 移除每行前後空白
val lines = """
  Line 1
  Line 2
  Line 3
""".split("\n").map(_.trim).mkString("\n")
```

### 3.4 字串與其他型別轉換

```scala
// 字串轉數字
val numStr = "42"
val num = numStr.toInt           // 42
val double = "3.14".toDouble     // 3.14
val long = "1000000".toLong      // 1000000

// 安全轉換 (處理錯誤)
def safeToInt(str: String): Option[Int] = {
  try {
    Some(str.toInt)
  } catch {
    case _: NumberFormatException => None
  }
}

safeToInt("42")      // Some(42)
safeToInt("abc")     // None

// 使用 Try
import scala.util.{Try, Success, Failure}

Try("42".toInt)      // Success(42)
Try("abc".toInt)     // Failure(NumberFormatException)

// 數字轉字串
val n = 42
n.toString           // "42"
s"$n"               // "42"

// 其他轉換
val bool = "true".toBoolean     // true
val list = "1,2,3".split(",").map(_.toInt).toList  // List(1, 2, 3)
```

### 3.5 StringBuilder

對於大量字串操作,使用 `StringBuilder` 更有效率:

```scala
val sb = new StringBuilder

sb.append("Hello")
sb.append(" ")
sb.append("World")

val result = sb.toString  // "Hello World"

// 鏈式呼叫
val result2 = new StringBuilder()
  .append("Line 1")
  .append("\n")
  .append("Line 2")
  .toString

// 建構大字串
def buildLargeString(n: Int): String = {
  val sb = new StringBuilder
  for (i <- 1 to n) {
    sb.append(s"Item $i\n")
  }
  sb.toString
}
```

---

## 4. 運算子

### 4.1 算術運算子

```scala
val a = 10
val b = 3

// 基本運算
a + b    // 13 (加)
a - b    // 7  (減)
a * b    // 30 (乘)
a / b    // 3  (除,整數除法)
a % b    // 1  (餘數)

// 浮點數除法
val x = 10.0
val y = 3.0
x / y    // 3.3333...

// 複合賦值運算子 (只能用於 var)
var count = 10
count += 5   // count = 15
count -= 3   // count = 12
count *= 2   // count = 24
count /= 4   // count = 6
count %= 4   // count = 2

// 一元運算子
+a       // 10 (正號)
-a       // -10 (負號)
```

### 4.2 關係運算子

```scala
val a = 10
val b = 20

a == b   // false (等於)
a != b   // true  (不等於)
a < b    // true  (小於)
a <= b   // true  (小於等於)
a > b    // false (大於)
a >= b   // false (大於等於)

// 字串比較
"abc" == "abc"     // true
"abc" < "abd"      // true (字典序)

// 參考比較 (很少使用)
val s1 = new String("hello")
val s2 = new String("hello")
s1 == s2           // true (值相等)
s1 eq s2           // false (參考不同)
```

### 4.3 邏輯運算子

```scala
val t = true
val f = false

// AND
t && t   // true
t && f   // false
f && t   // false
f && f   // false

// OR
t || t   // true
t || f   // true
f || t   // true
f || f   // false

// NOT
!t       // false
!f       // true

// 短路求值
def expensive(): Boolean = {
  println("執行昂貴操作")
  true
}

false && expensive()  // 不會執行 expensive()
true || expensive()   // 不會執行 expensive()

// 位元運算子
val x = 5   // 0101
val y = 3   // 0011

x & y    // 1  (0001, AND)
x | y    // 7  (0111, OR)
x ^ y    // 6  (0110, XOR)
~x       // -6 (1010, NOT)
x << 1   // 10 (1010, 左移)
x >> 1   // 2  (0010, 右移)
x >>> 1  // 2  (0010, 無符號右移)
```

### 4.4 運算子優先順序

```scala
// 從高到低
// 1. 所有其他特殊字元
// 2. * / %
// 3. + -
// 4. :
// 5. = !
// 6. < >
// 7. &
// 8. ^
// 9. |
// 10. 所有字母
// 11. 所有賦值運算子

// 範例
val result1 = 2 + 3 * 4      // 14 (不是 20)
val result2 = (2 + 3) * 4    // 20
val result3 = 10 - 5 - 2     // 3 (左結合)
```

### 4.5 運算子即方法

Scala 中的運算子其實是方法呼叫:

```scala
val a = 1
val b = 2

// 這兩種寫法相同
a + b
a.+(b)

// 這兩種寫法相同
a < b
a.<(b)

// 字串連接
"Hello" + " World"
"Hello".+(" World")

// 自訂運算子
class Vector2D(val x: Double, val y: Double) {
  def +(other: Vector2D): Vector2D = {
    new Vector2D(x + other.x, y + other.y)
  }
  
  def *(scalar: Double): Vector2D = {
    new Vector2D(x * scalar, y * scalar)
  }
  
  override def toString = s"Vector2D($x, $y)"
}

val v1 = new Vector2D(1, 2)
val v2 = new Vector2D(3, 4)
val v3 = v1 + v2        // Vector2D(4.0, 6.0)
val v4 = v1 * 2.0       // Vector2D(2.0, 4.0)
```

---

## 5. 條件式

### 5.1 if-else 表達式

Scala 的 if-else 是表達式,會返回值:

```scala
val age = 18

// 基本 if-else
val status = if (age >= 18) "成人" else "未成年"
println(status)  // 成人

// if-else 可以有不同型別
val result = if (age >= 18) "Adult" else 0
// result 的型別是 Any (String 和 Int 的共同父型別)

// 單行 if (不推薦省略 else)
if (age >= 18) println("成人")

// 多行 if-else
val grade = 85
val level = if (grade >= 90) {
  println("優秀!")
  "A"
} else if (grade >= 80) {
  println("良好!")
  "B"
} else if (grade >= 70) {
  println("及格")
  "C"
} else {
  println("不及格")
  "F"
}
```

### 5.2 巢狀條件

```scala
val score = 85
val attendance = 90

val finalGrade = if (score >= 60) {
  if (attendance >= 80) {
    "通過 (出席率良好)"
  } else {
    "通過 (出席率需改善)"
  }
} else {
  if (attendance >= 80) {
    "不通過 (建議補考)"
  } else {
    "不通過 (出席率不足)"
  }
}
```

### 5.3 條件式最佳實踐

```scala
// ❌ 避免:使用 if 但不使用返回值
var result = ""
if (condition) {
  result = "yes"
} else {
  result = "no"
}

// ✅ 推薦:使用 if 表達式
val result = if (condition) "yes" else "no"

// ❌ 避免:複雜的巢狀條件
if (a) {
  if (b) {
    if (c) {
      // ...
    }
  }
}

// ✅ 推薦:使用 match 或提前返回
def process(a: Boolean, b: Boolean, c: Boolean): String = {
  if (!a) return "a is false"
  if (!b) return "b is false"
  if (!c) return "c is false"
  "all true"
}

// ✅ 或使用模式比對
(a, b, c) match {
  case (true, true, true) => "all true"
  case (false, _, _) => "a is false"
  case (_, false, _) => "b is false"
  case (_, _, false) => "c is false"
}
```

---

## 6. 迴圈

### 6.1 for 迴圈

**基本語法:**
```scala
// 遍歷範圍
for (i <- 1 to 5) {
  println(i)
}
// 輸出: 1 2 3 4 5

// until (不包含結束值)
for (i <- 1 until 5) {
  println(i)
}
// 輸出: 1 2 3 4

// 指定步進
for (i <- 1 to 10 by 2) {
  println(i)
}
// 輸出: 1 3 5 7 9

// 倒序
for (i <- 10 to 1 by -1) {
  println(i)
}
// 輸出: 10 9 8 ... 1
```

**遍歷集合:**
```scala
val fruits = List("apple", "banana", "cherry")

for (fruit <- fruits) {
  println(fruit)
}

// 帶索引
for (i <- fruits.indices) {
  println(s"$i: ${fruits(i)}")
}

// 使用 zipWithIndex
for ((fruit, index) <- fruits.zipWithIndex) {
  println(s"$index: $fruit")
}
```

**多重生成器:**
```scala
// 巢狀迴圈
for (i <- 1 to 3; j <- 1 to 2) {
  println(s"i=$i, j=$j")
}
// 輸出:
// i=1, j=1
// i=1, j=2
// i=2, j=1
// i=2, j=2
// i=3, j=1
// i=3, j=2

// 九九乘法表
for (i <- 1 to 9; j <- 1 to 9) {
  print(f"${i * j}%4d")
  if (j == 9) println()
}
```

**帶條件的 for (Guard):**
```scala
// 只處理偶數
for (i <- 1 to 10 if i % 2 == 0) {
  println(i)
}
// 輸出: 2 4 6 8 10

// 多個條件
for {
  i <- 1 to 100
  if i % 3 == 0
  if i % 5 == 0
} {
  println(i)
}
// 輸出: 15 30 45 60 75 90

// 複雜過濾
val numbers = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
for {
  n <- numbers
  if n % 2 == 0
  if n > 5
} {
  println(s"偶數且大於5: $n")
}
```

### 6.2 for 推導式 (for comprehension)

for 推導式會產生新的集合:

```scala
// 基本用法
val doubled = for (i <- 1 to 5) yield i * 2
// doubled: IndexedSeq[Int] = Vector(2, 4, 6, 8, 10)

// 轉換集合
val fruits = List("apple", "banana", "cherry")
val upperFruits = for (fruit <- fruits) yield fruit.toUpperCase
// upperFruits: List[String] = List(APPLE, BANANA, CHERRY)

// 帶條件
val evenDoubled = for (i <- 1 to 10 if i % 2 == 0) yield i * 2
// evenDoubled: IndexedSeq[Int] = Vector(4, 8, 12, 16, 20)

// 多重生成器
val pairs = for {
  x <- 1 to 3
  y <- 1 to 2
} yield (x, y)
// pairs: IndexedSeq[(Int, Int)] = Vector((1,1), (1,2), (2,1), (2,2), (3,1), (3,2))

// 複雜轉換
case class Person(name: String, age: Int)
val people = List(
  Person("Alice", 25),
  Person("Bob", 30),
  Person("Charlie", 20)
)

val names = for {
  person <- people
  if person.age >= 25
} yield person.name
// names: List[String] = List(Alice, Bob)
```

**for 推導式 vs map/filter:**
```scala
val numbers = List(1, 2, 3, 4, 5)

// 使用 for 推導式
val result1 = for {
  n <- numbers
  if n % 2 == 0
} yield n * 2

// 使用 filter 和 map
val result2 = numbers.filter(_ % 2 == 0).map(_ * 2)

// 兩者結果相同: List(4, 8)
```

### 6.3 while 迴圈

```scala
var i = 0
while (i < 5) {
  println(i)
  i += 1
}

// 實際範例:讀取輸入直到特定條件
import scala.io.StdIn

var input = ""
while (input != "quit") {
  print("輸入指令 (quit 離開): ")
  input = StdIn.readLine()
  println(s"你輸入: $input")
}
```

### 6.4 先執行本體再測試條件

```scala
// Scala 3 已移除 do-while 語法。若本體必須先執行，再把它放入 while 條件區塊。
var count = 0
while {
  println(s"Count: $count")
  count += 1
  count < 5
} do ()

// 至少執行一次
var x = 10
while {
  println("執行一次")
  x < 5
} do ()  // 條件為 false，但條件區塊仍先執行一次
```

### 6.5 迴圈控制

Scala 沒有 `break` 和 `continue`,但有替代方案:

**使用 return (在方法中):**
```scala
def findFirst(numbers: List[Int], target: Int): Option[Int] = {
  for (i <- numbers.indices) {
    if (numbers(i) == target) {
      return Some(i)  // 提前返回
    }
  }
  None
}
```

**使用 scala.util.control.Breaks:**
```scala
import scala.util.control.Breaks.*

// break 範例
breakable {
  for (i <- 1 to 10) {
    println(i)
    if (i == 5) break  // 跳出迴圈
  }
}

// continue 範例 (使用巢狀 breakable)
for (i <- 1 to 5) {
  breakable {
    if (i == 3) break  // 相當於 continue
    println(i)
  }
}
// 輸出: 1 2 4 5
```

**函數式替代方案 (推薦):**
```scala
// 使用 takeWhile 代替 break
val numbers = (1 to 100).takeWhile(_ < 50)

// 使用 filter 代替 continue
(1 to 10).filter(_ != 5).foreach(println)

// 使用 find 代替提前返回
val firstEven = (1 to 10).find(_ % 2 == 0)  // Some(2)
```

---

## 7. 表達式 vs 陳述式

### 7.1 理解差異

**表達式 (Expression):** 會求值並返回結果
**陳述式 (Statement):** 執行動作但不返回有意義的值

```scala
// 表達式範例
val x = 10          // 10 是表達式
val y = x + 5       // x + 5 是表達式
val z = if (x > 5) "big" else "small"  // if-else 是表達式

// 在 Scala 中,幾乎所有東西都是表達式
val result = {
  val a = 10
  val b = 20
  a + b  // 最後一個表達式的值就是區塊的值
}
// result = 30
```

### 7.2 程式碼區塊

```scala
// 區塊是表達式
val area = {
  val width = 10
  val height = 20
  width * height  // 返回 200
}

// 區塊中的變數是局部的
{
  val temp = 100
  println(temp)
}
// println(temp)  // 錯誤!temp 不存在

// 複雜計算
val discount = {
  val price = 1000
  val customerType = "VIP"
  
  if (customerType == "VIP") {
    price * 0.8
  } else if (customerType == "Member") {
    price * 0.9
  } else {
    price
  }
}
```

### 7.3 Unit 型別的陳述式

```scala
// println 返回 Unit
val result: Unit = println("Hello")

// 賦值返回 Unit
var x = 10
val assignment: Unit = (x = 20)

// while 迴圈返回 Unit
val loop: Unit = while (x < 30) {
  x += 1
}
```

---

## 8. 程式碼區塊

### 8.1 區塊語法

```scala
// 基本區塊
{
  val x = 10
  val y = 20
  x + y
}

// 區塊作為參數
List(1, 2, 3).map { x =>
  val doubled = x * 2
  val squared = doubled * doubled
  squared
}

// 函數定義使用區塊
def calculate(a: Int, b: Int): Int = {
  val sum = a + b
  val product = a * b
  sum + product
}
```

### 8.2 區塊的作用域

```scala
val outer = "outer"

{
  val inner = "inner"
  println(outer)  // OK
  println(inner)  // OK
}

// println(inner)  // 錯誤!

// 變數遮蔽
val x = 10
{
  val x = 20  // 新的 x,遮蔽外層的 x
  println(x)  // 20
}
println(x)    // 10
```

### 8.3 區塊的實際應用

```scala
// 初始化複雜物件
val config = {
  val env = sys.env.getOrElse("ENV", "dev")
  val port = if (env == "prod") 8080 else 3000
  val host = if (env == "prod") "0.0.0.0" else "localhost"
  
  Map(
    "env" -> env,
    "port" -> port,
    "host" -> host
  )
}

// 條件式初始化
val logger = {
  val debug = true
  if (debug) {
    println("Debug mode enabled")
    new DebugLogger()
  } else {
    new ProductionLogger()
  }
}

// 資源管理
val data = {
  val file = scala.io.Source.fromFile("data.txt")
  try {
    file.getLines().toList
  } finally {
    file.close()
  }
}
```

---

## 9. 實作練習

### 練習 1: 溫度轉換器

```scala
@main def temperatureConverter(): Unit = {
  def celsiusToFahrenheit(c: Double): Double = {
    c * 9 / 5 + 32
  }
  
  def fahrenheitToCelsius(f: Double): Double = {
    (f - 32) * 5 / 9
  }
  
  // 測試
  val tempC = 25.0
  val tempF = celsiusToFahrenheit(tempC)
  println(f"$tempC%.1f°C = $tempF%.1f°F")
  
  val tempF2 = 77.0
  val tempC2 = fahrenheitToCelsius(tempF2)
  println(f"$tempF2%.1f°F = $tempC2%.1f°C")
}
```

**預期輸出:**
```
25.0°C = 77.0°F
77.0°F = 25.0°C
```

### 練習 2: 成績分級系統

```scala
@main def gradeSystem(): Unit = {
  def getGrade(score: Int): String = {
    if (score < 0 || score > 100) {
      "無效分數"
    } else if (score >= 90) {
      "A"
    } else if (score >= 80) {
      "B"
    } else if (score >= 70) {
      "C"
    } else if (score >= 60) {
      "D"
    } else {
      "F"
    }
  }
  
  def getComment(grade: String): String = grade match {
    case "A" => "優秀!"
    case "B" => "良好!"
    case "C" => "及格"
    case "D" => "需要加強"
    case "F" => "不及格"
    case _ => ""
  }
  
  // 測試多個分數
  val scores = List(95, 85, 75, 65, 55, 105)
  
  for (score <- scores) {
    val grade = getGrade(score)
    val comment = getComment(grade)
    println(f"分數: $score%3d => 等級: $grade ($comment)")
  }
}
```

### 練習 3: 簡易計算機

```scala
@main def simpleCalculator(): Unit = {
  def calculate(a: Double, b: Double, operator: String): Option[Double] = {
    operator match {
      case "+" => Some(a + b)
      case "-" => Some(a - b)
      case "*" => Some(a * b)
      case "/" if b != 0 => Some(a / b)
      case "/" => None  // 除以零
      case _ => None    // 不支援的運算子
    }
  }
  
  // 測試
  val tests = List(
    (10.0, 5.0, "+"),
    (10.0, 5.0, "-"),
    (10.0, 5.0, "*"),
    (10.0, 5.0, "/"),
    (10.0, 0.0, "/"),
    (10.0, 5.0, "%")
  )
  
  for ((a, b, op) <- tests) {
    calculate(a, b, op) match {
      case Some(result) => println(f"$a%.1f $op $b%.1f = $result%.2f")
      case None => println(f"$a%.1f $op $b%.1f = 錯誤!")
    }
  }
}
```

### 練習 4: FizzBuzz

經典的 FizzBuzz 問題:

```scala
@main def runFizzBuzz(): Unit = {
  def fizzBuzz(n: Int): String = {
    if (n % 15 == 0) "FizzBuzz"
    else if (n % 3 == 0) "Fizz"
    else if (n % 5 == 0) "Buzz"
    else n.toString
  }
  
  // 方法一:使用 for 迴圈
  println("=== 方法一 ===")
  for (i <- 1 to 30) {
    println(s"$i: ${fizzBuzz(i)}")
  }
  
  // 方法二:使用 for 推導式
  println("\n=== 方法二 ===")
  val results = for (i <- 1 to 30) yield fizzBuzz(i)
  results.zipWithIndex.foreach { case (result, index) =>
    println(s"${index + 1}: $result")
  }
}
```

### 練習 5: 質數判斷

```scala
@main def primeChecker(): Unit = {
  def isPrime(n: Int): Boolean = {
    if (n <= 1) {
      false
    } else if (n == 2) {
      true
    } else if (n % 2 == 0) {
      false
    } else {
      // 只需檢查到 sqrt(n)
      val sqrt = math.sqrt(n).toInt
      !(3 to sqrt by 2).exists(i => n % i == 0)
    }
  }
  
  // 找出 1 到 100 的所有質數
  val primes = for {
    n <- 1 to 100
    if isPrime(n)
  } yield n
  
  println(s"1 到 100 的質數: ${primes.mkString(", ")}")
  println(s"總共有 ${primes.length} 個質數")
}
```

### 練習 6: 字串處理

```scala
@main def stringProcessor(): Unit = {
  // 統計字串中的字元
  def charCount(str: String): Map[Char, Int] = {
    str.groupBy(identity).view.mapValues(_.length).toMap
  }
  
  // 判斷是否為迴文
  def isPalindrome(str: String): Boolean = {
    val cleaned = str.toLowerCase.replaceAll("[^a-z0-9]", "")
    cleaned == cleaned.reverse
  }
  
  // 單字計數
  def wordCount(str: String): Int = {
    str.split("\\s+").filter(_.nonEmpty).length
  }
  
  // 測試
  val text = "Hello, World! This is a test."
  
  println(s"原始文字: $text")
  println(s"字元統計: ${charCount(text.toLowerCase.replaceAll("[^a-z]", ""))}")
  println(s"單字數量: ${wordCount(text)}")
  
  val palindromes = List("A man a plan a canal Panama", "Hello", "racecar")
  palindromes.foreach { str =>
    println(s"'$str' 是迴文? ${isPalindrome(str)}")
  }
}
```

### 練習 7: 數列生成

```scala
@main def sequenceGenerator(): Unit = {
  // 費氏數列
  def fibonacci(n: Int): List[Int] = {
    def fib(count: Int, a: Int, b: Int, acc: List[Int]): List[Int] = {
      if (count == 0) acc.reverse
      else fib(count - 1, b, a + b, a :: acc)
    }
    fib(n, 0, 1, List())
  }
  
  // 階乘
  def factorial(n: Int): BigInt = {
    if (n <= 1) 1
    else (1 to n).map(BigInt(_)).product
  }
  
  // 完全平方數
  def perfectSquares(limit: Int): List[Int] = {
    (1 to limit).map(x => x * x).toList
  }
  
  // 測試
  println(s"前 10 個費氏數列: ${fibonacci(10).mkString(", ")}")
  println(s"10 的階乘: ${factorial(10)}")
  println(s"前 10 個完全平方數: ${perfectSquares(10).mkString(", ")}")
}
```

---

## 10. 重點總結

### 變數宣告
- 優先使用 `val` (不可變)
- 只在必要時使用 `var` (可變)
- `lazy val` 用於延遲初始化

### 資料型別
- 所有型別都是物件
- 型別推導讓程式碼更簡潔
- 避免使用 `null`,使用 `Option`

### 字串
- 使用 s 插值器進行字串插值
- f 插值器用於格式化
- raw 插值器用於不處理轉義字元

### 條件與迴圈
- if-else 是表達式,會返回值
- for 推導式產生新集合
- 優先使用函數式方法而非迴圈

### 表達式優於陳述式
- Scala 鼓勵使用表達式
- 程式碼區塊會返回最後一個表達式的值
- 這使得程式碼更簡潔、更易組合

---

## 下一步

完成第二部分後,您已經掌握:
- ✅ Scala 的基本語法
- ✅ 變數與資料型別
- ✅ 字串操作
- ✅ 控制流程

**接下來學習:**
- [第三部分: 函數與方法](scala_part3_functions.md) - 深入函數式程式設計
- [第四部分: 物件導向程式設計](scala_part4_oop.md)

準備好繼續了嗎?

---

> [« 上一篇：Scala 簡介與環境設置](scala_part1_introduction.md) | [📚 目錄](../README.md) | [下一篇：函數與方法 »](scala_part3_functions.md)
