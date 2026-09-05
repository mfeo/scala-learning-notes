# Scala 教學 - 第三部分：Functions and Methods（函數與方法）

> [« 上一篇：基本語法](scala_part2_basic_syntax.md) | [📚 目錄](../README.md) | [下一篇：物件導向程式設計 »](scala_part4_oop.md)

---

## 目錄
1. [Function 基礎](#1-function函數基礎)
2. [Method 與 Function](#2-method方法與-function函數)
3. [參數](#3-參數)
4. [Higher-Order Function](#4-higher-order-function高階函數)
5. [Lambda Expression](#5-lambda-expression匿名函數)
6. [Closure](#6-closure閉包)
7. [Currying](#7-currying柯里化)
8. [Partial Application](#8-partial-application部分應用)
9. [Recursive Function](#9-recursive-function遞迴函數)
10. [Function Composition](#10-function-composition函數組合)
11. [實作練習](#11-實作練習)

---

## 1. Function（函數）基礎

### 1.1 定義函數

在 Scala 中,函數使用 `def` 關鍵字定義:

```scala
// 基本語法
def functionName(param1: Type1, param2: Type2): ReturnType = {
  // 函數體
  // 最後一個表達式的值就是返回值
}

// 簡單範例
def add(x: Int, y: Int): Int = {
  x + y
}

val result = add(3, 4)  // 7

// 單行函數可以省略大括號
def multiply(x: Int, y: Int): Int = x * y

// 沒有參數的函數
def getCurrentTime(): Long = {
  System.currentTimeMillis()
}

// 可以省略空括號
def getCurrentTime: Long = System.currentTimeMillis()
```

### 1.2 返回型別推導

Scala 可以自動推導返回型別,但建議對公開的 API 明確指定:

```scala
// 返回型別推導
def square(x: Int) = x * x  // 推導為 Int

// 明確指定返回型別 (推薦用於公開函數)
def square(x: Int): Int = x * x

// 複雜函數建議明確指定
def processData(data: List[Int]): List[String] = {
  data.filter(_ > 0).map(_.toString)
}
```

### 1.3 無返回值的函數

返回 `Unit` 的函數相當於 Java 的 `void`:

```scala
def printMessage(msg: String): Unit = {
  println(msg)
}

// Scala 3 要求在方法本體前寫出 =
def printMessage(msg: String): Unit = {
  println(msg)
}
```

### 1.4 Unit-Returning Method（回傳 `Unit` 的方法）

```scala
def greet(name: String): Unit = {
  println(s"Hello, $name")
}
```

---

## 2. Method（方法）與 Function（函數）

### 2.1 方法 (Method)

方法是定義在類別或物件中的成員:

```scala
class Calculator {
  // 這是方法
  def add(x: Int, y: Int): Int = x + y
  
  def multiply(x: Int, y: Int): Int = x * y
}

val calc = new Calculator
calc.add(3, 4)  // 呼叫方法
```

### 2.2 函數 (Function)

函數是物件,可以賦值給變數、作為參數傳遞:

```scala
// 函數值 (Function Value)
val add: (Int, Int) => Int = (x, y) => x + y

// 使用函數
val result = add(3, 4)  // 7

// 函數可以賦值給變數
val myFunc = add
myFunc(5, 6)  // 11
```

### 2.3 Eta Expansion（方法轉函數）

```scala
class Calculator {
  def add(x: Int, y: Int): Int = x + y
}

val calc = new Calculator

// Scala 3 會自動執行 eta expansion
val addFunc: (Int, Int) => Int = calc.add
val addFunc2 = calc.add

// 現在可以像函數一樣使用
List(1, 2, 3).map(addFunc(_, 10))  // List(11, 12, 13)
```

### 2.4 關鍵差異

```scala
// 方法可以有型別參數
def identity[T](x: T): T = x

// 方法可以有多個參數列表
def add(x: Int)(y: Int): Int = x + y

// 方法可以有預設參數和具名參數
def greet(name: String, greeting: String = "Hello"): String = {
  s"$greeting, $name!"
}

// 函數值沒有這些特性,但可以被當作物件傳遞
val func: Int => Int = x => x + 1
```

---

## 3. 參數

### 3.1 預設參數

```scala
def greet(name: String, greeting: String = "Hello", punctuation: String = "!"): String = {
  s"$greeting, $name$punctuation"
}

greet("Alice")                    // "Hello, Alice!"
greet("Bob", "Hi")                // "Hi, Bob!"
greet("Charlie", "Hey", "?")      // "Hey, Charlie?"
```

### 3.2 具名參數

```scala
def createUser(name: String, age: Int, email: String, active: Boolean = true): String = {
  s"User($name, $age, $email, $active)"
}

// 使用具名參數可以任意順序
val user1 = createUser(
  email = "alice@example.com",
  name = "Alice",
  age = 25
)

// 混合使用位置參數和具名參數
val user2 = createUser("Bob", 30, email = "bob@example.com")

// 跳過有預設值的參數
val user3 = createUser(
  name = "Charlie",
  age = 35,
  email = "charlie@example.com"
)
```

### 3.3 Varargs（可變參數）

```scala
def sum(numbers: Int*): Int = {
  numbers.sum
}

sum(1, 2, 3)        // 6
sum(1, 2, 3, 4, 5)  // 15
sum()               // 0

// 可變參數必須是最後一個參數
def printWithPrefix(prefix: String, items: String*): Unit = {
  items.foreach(item => println(s"$prefix: $item"))
}

printWithPrefix("Item", "apple", "banana", "cherry")

// 傳遞序列給可變參數函數
val numbers = List(1, 2, 3, 4, 5)
sum(numbers*)  // 使用後置 * 展開序列
```

### 3.4 By-Name Parameters（傳名參數）

傳名參數在每次使用時才求值:

```scala
// 普通參數 (傳值)
def byValue(x: Int): Unit = {
  println(s"第一次使用: $x")
  println(s"第二次使用: $x")
}

// 傳名參數 (注意 => 符號)
def byName(x: => Int): Unit = {
  println(s"第一次使用: $x")
  println(s"第二次使用: $x")
}

def expensive(): Int = {
  println("執行昂貴計算")
  42
}

println("=== 傳值 ===")
byValue(expensive())
// 輸出:
// 執行昂貴計算
// 第一次使用: 42
// 第二次使用: 42

println("\n=== 傳名 ===")
byName(expensive())
// 輸出:
// 執行昂貴計算
// 第一次使用: 42
// 執行昂貴計算
// 第二次使用: 42
```

**實用範例:自訂控制結構**

```scala
// 實作 while 迴圈
def myWhile(condition: => Boolean)(body: => Unit): Unit = {
  if (condition) {
    body
    myWhile(condition)(body)
  }
}

var count = 0
myWhile(count < 5) {
  println(count)
  count += 1
}

// 實作 unless (相反的 if)
def unless(condition: Boolean)(body: => Unit): Unit = {
  if (!condition) body
}

val x = 10
unless(x < 5) {
  println("x 不小於 5")
}

// 計時函數
def time[T](block: => T): T = {
  val start = System.nanoTime()
  val result = block
  val end = System.nanoTime()
  println(f"執行時間: ${(end - start) / 1000000.0}%.2f ms")
  result
}

val result = time {
  (1 to 1000000).sum
}
```

---

## 4. Higher-Order Function（高階函數）

高階函數是接受函數作為參數或返回函數的函數。

### 4.1 函數作為參數

```scala
// 接受函數作為參數
def applyOperation(a: Int, b: Int, op: (Int, Int) => Int): Int = {
  op(a, b)
}

// 定義一些操作
def add(x: Int, y: Int): Int = x + y
def multiply(x: Int, y: Int): Int = x * y
def subtract(x: Int, y: Int): Int = x - y

// 使用
applyOperation(10, 5, add)       // 15
applyOperation(10, 5, multiply)  // 50
applyOperation(10, 5, subtract)  // 5

// 使用匿名函數
applyOperation(10, 5, (x, y) => x / y)  // 2
```

**實用範例:自訂 filter**

```scala
def customFilter[T](list: List[T], predicate: T => Boolean): List[T] = {
  list match {
    case Nil => Nil
    case head :: tail =>
      if (predicate(head)) head :: customFilter(tail, predicate)
      else customFilter(tail, predicate)
  }
}

val numbers = List(1, 2, 3, 4, 5, 6)
customFilter(numbers, (x: Int) => x % 2 == 0)  // List(2, 4, 6)
customFilter(numbers, (x: Int) => x > 3)       // List(4, 5, 6)
```

### 4.2 返回函數

```scala
// 返回函數
def multiplier(factor: Int): Int => Int = {
  (x: Int) => x * factor
}

val double = multiplier(2)
val triple = multiplier(3)

double(5)  // 10
triple(5)  // 15

// 更複雜的範例
def createGreeter(greeting: String): String => String = {
  (name: String) => s"$greeting, $name!"
}

val sayHello = createGreeter("Hello")
val sayHi = createGreeter("Hi")

sayHello("Alice")  // "Hello, Alice!"
sayHi("Bob")       // "Hi, Bob!"
```

**實用範例:建立驗證器**

```scala
def createValidator(min: Int, max: Int): Int => Boolean = {
  (value: Int) => value >= min && value <= max
}

val ageValidator = createValidator(0, 120)
val percentageValidator = createValidator(0, 100)

ageValidator(25)      // true
ageValidator(150)     // false
percentageValidator(50)   // true
percentageValidator(101)  // false
```

### 4.3 組合使用

```scala
// 接受函數並返回函數
def compose[A, B, C](f: B => C, g: A => B): A => C = {
  (x: A) => f(g(x))
}

val addOne = (x: Int) => x + 1
val double = (x: Int) => x * 2

val addOneThenDouble = compose(double, addOne)
addOneThenDouble(5)  // 12 (先 +1 變成 6,再 *2)

// 使用多個高階函數
def transform[T](
  list: List[T],
  filter: T => Boolean,
  map: T => T
): List[T] = {
  list.filter(filter).map(map)
}

val numbers = List(1, 2, 3, 4, 5, 6)
transform(
  numbers,
  (x: Int) => x % 2 == 0,  // 只保留偶數
  (x: Int) => x * 10        // 乘以 10
)
// Result: List(20, 40, 60)
```

---

## 5. Lambda Expression（匿名函數）

### 5.1 基本語法

```scala
// 完整語法
val add = (x: Int, y: Int) => x + y

// 使用
add(3, 4)  // 7

// 單參數
val square = (x: Int) => x * x
square(5)  // 25

// 無參數
val random = () => math.random()
random()

// 多行匿名函數
val complexFunc = (x: Int, y: Int) => {
  val sum = x + y
  val product = x * y
  sum + product
}
```

### 5.2 型別推導

```scala
val numbers = List(1, 2, 3, 4, 5)

// 完整型別
numbers.map((x: Int) => x * 2)

// 型別推導
numbers.map(x => x * 2)

// 使用底線簡寫 (參數只使用一次)
numbers.map(_ * 2)

// 多個參數的底線
List(1, 2, 3).reduce((x, y) => x + y)
List(1, 2, 3).reduce(_ + _)
```

### 5.3 底線語法詳解

```scala
val numbers = List(1, 2, 3, 4, 5)

// 單個底線
numbers.map(_ * 2)           // 每個元素 * 2
numbers.filter(_ > 2)        // 過濾大於 2 的
numbers.map(_.toString)      // 轉為字串

// 多個底線 (按順序對應參數)
numbers.reduce(_ + _)        // 第一個 _ 是 x,第二個 _ 是 y
numbers.reduce(_ - _)        // x - y
numbers.reduce((x, y) => x - y)  // 等價寫法

// 不能用底線的情況
numbers.map(_ + _)           // 錯誤!無法判斷參數數量
numbers.map(x => x + x)      // 正確:參數使用兩次

// 底線用於方法呼叫
val strings = List("hello", "world")
strings.map(_.toUpperCase)   // 等於 s => s.toUpperCase
strings.map(_.length)        // 等於 s => s.length
```

### 5.4 匿名函數的實際應用

```scala
// 列表處理
val words = List("apple", "banana", "cherry", "date")

// 過濾
words.filter(_.length > 5)  // List("banana", "cherry")

// 轉換
words.map(_.toUpperCase)

// 排序
words.sortBy(_.length)  // 按長度排序
words.sortBy(-_.length) // 按長度反向排序

// 分組
words.groupBy(_.head)  // 按首字母分組
// Map(a -> List(apple), b -> List(banana), c -> List(cherry), d -> List(date))

// 多重操作
words
  .filter(_.length >= 5)
  .map(_.toUpperCase)
  .sortBy(_.length)
```

**複雜範例:資料處理**

```scala
case class Person(name: String, age: Int, city: String)

val people = List(
  Person("Alice", 25, "Taipei"),
  Person("Bob", 30, "Tokyo"),
  Person("Charlie", 25, "Taipei"),
  Person("David", 35, "Seoul")
)

// 找出所有 25 歲的人的名字
people
  .filter(_.age == 25)
  .map(_.name)
// List("Alice", "Charlie")

// 按城市分組,計算平均年齡
people
  .groupBy(_.city)
  .view
  .mapValues(ps => ps.map(_.age).sum.toDouble / ps.length)
  .toMap
// Map("Taipei" -> 25.0, "Tokyo" -> 30.0, "Seoul" -> 35.0)
```

---

## 6. Closure（閉包）

閉包是引用了自由變數的函數,這些變數在函數定義時的環境中。

### 6.1 基本概念

```scala
def makeAdder(x: Int): Int => Int = {
  (y: Int) => x + y  // x 是自由變數,來自外部環境
}

val add5 = makeAdder(5)
val add10 = makeAdder(10)

add5(3)   // 8
add10(3)  // 13

// x 被"關閉"在返回的函數中
```

### 6.2 閉包捕獲變數

```scala
var factor = 2

val multiplier = (x: Int) => x * factor

multiplier(5)  // 10

factor = 3
multiplier(5)  // 15 (使用更新後的 factor)

// 閉包捕獲的是變數的引用,不是值
```

### 6.3 實用範例

**計數器:**

```scala
def makeCounter(): () => Int = {
  var count = 0
  () => {
    count += 1
    count
  }
}

val counter1 = makeCounter()
val counter2 = makeCounter()

counter1()  // 1
counter1()  // 2
counter1()  // 3

counter2()  // 1 (獨立的計數器)
counter2()  // 2
```

**銀行帳戶:**

```scala
def createAccount(initialBalance: Double): (String, Double) => Double = {
  var balance = initialBalance
  
  (operation: String, amount: Double) => {
    operation match {
      case "deposit" =>
        balance += amount
        balance
      case "withdraw" =>
        if (amount <= balance) {
          balance -= amount
          balance
        } else {
          println("餘額不足")
          balance
        }
      case "balance" =>
        balance
    }
  }
}

val account = createAccount(1000.0)

account("deposit", 500.0)   // 1500.0
account("withdraw", 200.0)  // 1300.0
account("balance", 0)       // 1300.0
```

**記憶化 (Memoization):**

```scala
def memoize[A, B](f: A => B): A => B = {
  val cache = scala.collection.mutable.Map[A, B]()
  (x: A) => cache.getOrElseUpdate(x, f(x))
}

// 費氏數列 (未優化版本很慢)
def slowFib(n: Int): Int = {
  if (n <= 1) n
  else slowFib(n - 1) + slowFib(n - 2)
}

// 記憶化版本
val fastFib = memoize(slowFib)

// 第一次計算會慢
time { fastFib(40) }  // 約需 1-2 秒

// 第二次從快取取得,非常快
time { fastFib(40) }  // < 1 毫秒
```

---

## 7. Currying（柯里化）

柯里化是將多參數函數轉換為一系列單參數函數的技術。

### 7.1 基本語法

```scala
// 普通函數
def add(x: Int, y: Int): Int = x + y

// 柯里化函數
def addCurried(x: Int)(y: Int): Int = x + y

// 使用
add(3, 4)         // 7
addCurried(3)(4)  // 7

// 部分應用
val add3 = addCurried(3) _
add3(4)  // 7
add3(10) // 13
```

### 7.2 手動柯里化

```scala
// 將普通函數轉為柯里化函數
def curry[A, B, C](f: (A, B) => C): A => B => C = {
  (a: A) => (b: B) => f(a, b)
}

val add = (x: Int, y: Int) => x + y
val curriedAdd = curry(add)

val add5 = curriedAdd(5)
add5(3)  // 8

// 反柯里化
def uncurry[A, B, C](f: A => B => C): (A, B) => C = {
  (a: A, b: B) => f(a)(b)
}

val normalAdd = uncurry(curriedAdd)
normalAdd(3, 4)  // 7
```

### 7.3 實用範例

**自訂過濾器:**

```scala
def customFilter[T](predicate: T => Boolean)(list: List[T]): List[T] = {
  list.filter(predicate)
}

// 建立專門的過濾器
val filterEven = customFilter[Int](_ % 2 == 0) _
val filterPositive = customFilter[Int](_ > 0) _

val numbers = List(-2, -1, 0, 1, 2, 3, 4, 5)

filterEven(numbers)      // List(-2, 0, 2, 4)
filterPositive(numbers)  // List(1, 2, 3, 4, 5)
```

**HTML 產生器:**

```scala
def htmlTag(tagName: String)(attributes: String)(content: String): String = {
  s"<$tagName $attributes>$content</$tagName>"
}

// 建立特定標籤的產生器
val div = htmlTag("div") _
val span = htmlTag("span") _

div("class='container'")(

"Hello, World")
// <div class='container'>Hello, World</div>

span("id='message'")("Important!")
// <span id='message'>Important!</span>

// 進一步特化
val containerDiv = div("class='container'") _
containerDiv("Content 1")
containerDiv("Content 2")
```

**資料庫查詢:**

```scala
// 模擬資料庫查詢
case class User(id: Int, name: String, age: Int, city: String)

def query(table: String)(condition: User => Boolean)(users: List[User]): List[User] = {
  println(s"查詢表格: $table")
  users.filter(condition)
}

val users = List(
  User(1, "Alice", 25, "Taipei"),
  User(2, "Bob", 30, "Tokyo"),
  User(3, "Charlie", 25, "Taipei")
)

// 建立專門的查詢
val userQuery = query("users") _

val findByAge = userQuery((u: User) => u.age == 25) _
val findByCity = userQuery((u: User) => u.city == "Taipei") _

findByAge(users)   // List(User(1, "Alice", 25, "Taipei"), User(3, "Charlie", 25, "Taipei"))
findByCity(users)  // List(User(1, "Alice", 25, "Taipei"), User(3, "Charlie", 25, "Taipei"))
```

---

## 8. Partial Application（部分應用）

部分應用函數是固定某些參數,產生新函數。

### 8.1 基本概念

```scala
def sum(a: Int, b: Int, c: Int): Int = a + b + c

// 部分應用:固定第一個參數
val add10 = sum(10, _: Int, _: Int)
add10(5, 3)  // 18

// 固定前兩個參數
val add15 = sum(10, 5, _: Int)
add15(3)  // 18

// 柯里化更容易做部分應用
def sumCurried(a: Int)(b: Int)(c: Int): Int = a + b + c

val add10Curried = sumCurried(10) _
val add15Curried = sumCurried(10)(5) _

add10Curried(5)(3)  // 18
add15Curried(3)     // 18
```

### 8.2 實用範例

**日誌記錄器:**

```scala
def log(level: String)(timestamp: Long)(message: String): Unit = {
  println(s"[$level] [$timestamp] $message")
}

// 建立特定級別的日誌記錄器
val info = log("INFO") _
val error = log("ERROR") _
val debug = log("DEBUG") _

val now = System.currentTimeMillis()

info(now)("應用程式啟動")
error(now)("發生錯誤")
debug(now)("除錯訊息")

// 進一步固定時間戳
val infoNow = info(now) _
infoNow("訊息 1")
infoNow("訊息 2")
```

**數學運算:**

```scala
def calculate(operation: String)(x: Double)(y: Double): Double = {
  operation match {
    case "add" => x + y
    case "subtract" => x - y
    case "multiply" => x * y
    case "divide" if y != 0 => x / y
    case _ => 0.0
  }
}

val add = calculate("add") _
val multiply = calculate("multiply") _

val add10 = add(10) _
val double = multiply(2) _

add10(5)    // 15.0
double(7)   // 14.0
```

**格式化器:**

```scala
def format(prefix: String)(suffix: String)(content: String): String = {
  s"$prefix$content$suffix"
}

val htmlBold = format("<b>")(</b>") _
val htmlItalic = format("<i>")(</i>") _
val quote = format("\"")(\"") _

htmlBold("重要")     // "<b>重要</b>"
htmlItalic("強調")   // "<i>強調</i>"
quote("引用文字")    // "\"引用文字\""
```

---

## 9. Recursive Function（遞迴函數）

### 9.1 基本遞迴

```scala
// 階乘
def factorial(n: Int): Int = {
  if (n <= 1) 1
  else n * factorial(n - 1)
}

factorial(5)  // 120

// 費氏數列
def fibonacci(n: Int): Int = {
  if (n <= 1) n
  else fibonacci(n - 1) + fibonacci(n - 2)
}

fibonacci(10)  // 55

// 最大公約數 (GCD)
def gcd(a: Int, b: Int): Int = {
  if (b == 0) a
  else gcd(b, a % b)
}

gcd(48, 18)  // 6
```

### 9.2 Tail Recursion（尾遞迴）優化

尾遞迴是指遞迴呼叫是函數的最後一個操作,可以被編譯器優化為迴圈。

```scala
import scala.annotation.tailrec

// 非尾遞迴 (可能堆疊溢位)
def factorial(n: Int): Int = {
  if (n <= 1) 1
  else n * factorial(n - 1)  // 不是尾遞迴,因為還要乘以 n
}

// 尾遞迴版本
@tailrec
def factorialTail(n: Int, accumulator: Int = 1): Int = {
  if (n <= 1) accumulator
  else factorialTail(n - 1, n * accumulator)  // 尾遞迴!
}

factorialTail(5)      // 120
factorialTail(10000)  // 不會堆疊溢位

// 費氏數列尾遞迴版本
@tailrec
def fibonacciTail(n: Int, a: Int = 0, b: Int = 1): Int = {
  if (n == 0) a
  else fibonacciTail(n - 1, b, a + b)
}

fibonacciTail(10)  // 55
```

**@tailrec 註解:**

```scala
// @tailrec 會檢查函數是否真的是尾遞迴
@tailrec
def sum(n: Int): Int = {
  if (n <= 0) 0
  else n + sum(n - 1)  // 編譯錯誤!不是尾遞迴
}

// 正確的尾遞迴版本
@tailrec
def sumTail(n: Int, acc: Int = 0): Int = {
  if (n <= 0) acc
  else sumTail(n - 1, acc + n)
}
```

### 9.3 列表處理遞迴

```scala
// 計算列表長度
@tailrec
def length[T](list: List[T], acc: Int = 0): Int = list match {
  case Nil => acc
  case _ :: tail => length(tail, acc + 1)
}

length(List(1, 2, 3, 4, 5))  // 5

// 反轉列表
@tailrec
def reverse[T](list: List[T], acc: List[T] = Nil): List[T] = list match {
  case Nil => acc
  case head :: tail => reverse(tail, head :: acc)
}

reverse(List(1, 2, 3, 4, 5))  // List(5, 4, 3, 2, 1)

// 列表求和
@tailrec
def sumList(list: List[Int], acc: Int = 0): Int = list match {
  case Nil => acc
  case head :: tail => sumList(tail, acc + head)
}

sumList(List(1, 2, 3, 4, 5))  // 15

// 過濾列表
@tailrec
def filter[T](list: List[T], predicate: T => Boolean, acc: List[T] = Nil): List[T] = list match {
  case Nil => acc.reverse
  case head :: tail =>
    if (predicate(head)) filter(tail, predicate, head :: acc)
    else filter(tail, predicate, acc)
}

filter(List(1, 2, 3, 4, 5, 6), (x: Int) => x % 2 == 0)  // List(2, 4, 6)
```

### 9.4 樹結構遞迴

```scala
// 二元樹定義
sealed trait Tree[+T]
case class Leaf[T](value: T) extends Tree[T]
case class Branch[T](left: Tree[T], right: Tree[T]) extends Tree[T]

// 計算樹的大小
def size[T](tree: Tree[T]): Int = tree match {
  case Leaf(_) => 1
  case Branch(left, right) => size(left) + size(right)
}

// 計算樹的最大深度
def maxDepth[T](tree: Tree[T]): Int = tree match {
  case Leaf(_) => 1
  case Branch(left, right) => 1 + math.max(maxDepth(left), maxDepth(right))
}

// 測試
val tree = Branch(
  Branch(Leaf(1), Leaf(2)),
  Branch(Leaf(3), Branch(Leaf(4), Leaf(5)))
)

size(tree)      // 5
maxDepth(tree)  // 4
```

---

## 10. Function Composition（函數組合）

### 10.1 andThen 和 compose

```scala
val addOne = (x: Int) => x + 1
val double = (x: Int) => x * 2
val square = (x: Int) => x * x

// andThen: f andThen g = g(f(x))
val addThenDouble = addOne andThen double
addThenDouble(5)  // 12 (先 +1 得 6,再 *2)

val doubleThenSquare = double andThen square
doubleThenSquare(3)  // 36 (先 *2 得 6,再平方)

// compose: f compose g = f(g(x))
val doubleAfterAdd = double compose addOne
doubleAfterAdd(5)  // 12 (先 +1 得 6,再 *2)

val squareAfterDouble = square compose double
squareAfterDouble(3)  // 36 (先 *2 得 6,再平方)

// 鏈式組合
val pipeline = addOne andThen double andThen square
pipeline(2)  // 36 ((2+1)*2)^2 = 6^2 = 36
```

### 10.2 自訂組合函數

```scala
def compose[A, B, C](f: B => C, g: A => B): A => C = {
  (x: A) => f(g(x))
}

def andThen[A, B, C](f: A => B, g: B => C): A => C = {
  (x: A) => g(f(x))
}

// 使用
val addOne = (x: Int) => x + 1
val double = (x: Int) => x * 2

val composed = compose(double, addOne)
composed(5)  // 12

val chained = andThen(addOne, double)
chained(5)  // 12
```

### 10.3 實用範例

**資料處理管道:**

```scala
// 字串處理管道
val trim = (s: String) => s.trim
val lowercase = (s: String) => s.toLowerCase
val removeSpaces = (s: String) => s.replaceAll("\\s+", "")

val normalize = trim andThen lowercase andThen removeSpaces

normalize("  Hello World  ")  // "helloworld"

// 數值處理管道
val input = List("  123  ", " 456 ", "789")

val processNumber = trim andThen (_.toInt) andThen (_ * 2)

input.map(processNumber)  // List(246, 912, 1578)
```

**驗證管道:**

```scala
type Validator[T] = T => Either[String, T]

def minLength(n: Int): Validator[String] = { s =>
  if (s.length >= n) Right(s)
  else Left(s"長度必須至少 $n 個字元")
}

def maxLength(n: Int): Validator[String] = { s =>
  if (s.length <= n) Right(s)
  else Left(s"長度不能超過 $n 個字元")
}

def nonEmpty: Validator[String] = { s =>
  if (s.nonEmpty) Right(s)
  else Left("不能為空")
}

// 組合驗證器
def validate[T](value: T, validators: Validator[T]*): Either[String, T] = {
  validators.foldLeft(Right(value): Either[String, T]) { (acc, validator) =>
    acc.flatMap(validator)
  }
}

validate("hello", nonEmpty, minLength(3), maxLength(10))  // Right("hello")
validate("hi", nonEmpty, minLength(3), maxLength(10))     // Left("長度必須至少 3 個字元")
```

**函數管道操作符:**

```scala
extension [A](value: A) {
  def |>[B](f: A => B): B = f(value)
}

// 使用
val result = 5 |> (_ + 1) |> (_ * 2) |> (_ - 3)
// 等同於: ((5 + 1) * 2) - 3 = 9

// 實際範例
val data = "  Hello, World!  "
val processed = data
  |> (_.trim)
  |> (_.toLowerCase)
  |> (_.split(","))
  |> (_.head)

println(processed)  // "hello"
```

---

## 11. 實作練習

### 練習 1: 數學函數庫

```scala
object MathLibrary {
  // 實作冪次方 (不使用 Math.pow)
  def power(base: Double, exp: Int): Double = {
    @tailrec
    def powerTail(base: Double, exp: Int, acc: Double): Double = {
      if (exp == 0) acc
      else if (exp > 0) powerTail(base, exp - 1, acc * base)
      else powerTail(base, exp + 1, acc / base)
    }
    powerTail(base, exp, 1.0)
  }
  
  // 平方根 (牛頓法)
  def sqrt(n: Double, epsilon: Double = 0.0001): Double = {
    @tailrec
    def improve(guess: Double): Double = {
      if (math.abs(guess * guess - n) < epsilon) guess
      else improve((guess + n / guess) / 2)
    }
    improve(1.0)
  }
  
  // 階乘
  def factorial(n: Int): BigInt = {
    @tailrec
    def factTail(n: Int, acc: BigInt): BigInt = {
      if (n <= 1) acc
      else factTail(n - 1, n * acc)
    }
    factTail(n, 1)
  }
  
  // 組合數 C(n, k)
  def combination(n: Int, k: Int): BigInt = {
    factorial(n) / (factorial(k) * factorial(n - k))
  }
  
  // 測試
  def main(args: Array[String]): Unit = {
    println(s"2^10 = ${power(2, 10)}")
    println(s"sqrt(16) = ${sqrt(16)}")
    println(s"10! = ${factorial(10)}")
    println(s"C(10, 3) = ${combination(10, 3)}")
  }
}
```

### 練習 2: 列表處理函數

```scala
object ListOperations {
  // 實作 map
  def map[A, B](list: List[A], f: A => B): List[B] = {
    @tailrec
    def mapTail(remaining: List[A], acc: List[B]): List[B] = remaining match {
      case Nil => acc.reverse
      case head :: tail => mapTail(tail, f(head) :: acc)
    }
    mapTail(list, Nil)
  }
  
  // 實作 filter
  def filter[A](list: List[A], predicate: A => Boolean): List[A] = {
    @tailrec
    def filterTail(remaining: List[A], acc: List[A]): List[A] = remaining match {
      case Nil => acc.reverse
      case head :: tail =>
        if (predicate(head)) filterTail(tail, head :: acc)
        else filterTail(tail, acc)
    }
    filterTail(list, Nil)
  }
  
  // 實作 foldLeft
  @tailrec
  def foldLeft[A, B](list: List[A], initial: B)(f: (B, A) => B): B = list match {
    case Nil => initial
    case head :: tail => foldLeft(tail, f(initial, head))(f)
  }
  
  // 實作 foldRight (非尾遞迴)
  def foldRight[A, B](list: List[A], initial: B)(f: (A, B) => B): B = list match {
    case Nil => initial
    case head :: tail => f(head, foldRight(tail, initial)(f))
  }
  
  // 實作 flatMap
  def flatMap[A, B](list: List[A], f: A => List[B]): List[B] = {
    foldLeft(list, List[B]())((acc, elem) => acc ++ f(elem))
  }
  
  // 測試
  def main(args: Array[String]): Unit = {
    val numbers = List(1, 2, 3, 4, 5)
    
    println(map(numbers, (x: Int) => x * 2))
    println(filter(numbers, (x: Int) => x % 2 == 0))
    println(foldLeft(numbers, 0)(_ + _))
    println(flatMap(numbers, (x: Int) => List(x, x * 2)))
  }
}
```

### 練習 3: 函數式快取

```scala
object FunctionalCache {
  // 通用快取函數
  def cached[A, B](f: A => B): A => B = {
    val cache = scala.collection.mutable.Map[A, B]()
    (x: A) => cache.getOrElseUpdate(x, f(x))
  }
  
  // 帶過期時間的快取
  def cachedWithExpiry[A, B](f: A => B, expiryMs: Long): A => B = {
    val cache = scala.collection.mutable.Map[A, (B, Long)]()
    (x: A) => {
      val now = System.currentTimeMillis()
      cache.get(x) match {
        case Some((value, timestamp)) if now - timestamp < expiryMs =>
          value
        case _ =>
          val value = f(x)
          cache(x) = (value, now)
          value
      }
    }
  }
  
  // 測試
  def expensiveOperation(n: Int): Int = {
    println(s"計算 $n...")
    Thread.sleep(1000)  // 模擬耗時操作
    n * n
  }
  
  def main(args: Array[String]): Unit = {
    val cachedOp = cached(expensiveOperation)
    
    println(cachedOp(5))  // 計算並快取
    println(cachedOp(5))  // 從快取取得
    println(cachedOp(10)) // 計算並快取
    println(cachedOp(5))  // 從快取取得
  }
}
```

### 練習 4: 函數管道

```scala
object FunctionPipeline {
  // 定義管道操作符
  extension [A](value: A) {
    def |>[B](f: A => B): B = f(value)
  }
  
  // 定義一些轉換函數
  val trim = (s: String) => s.trim
  val lowercase = (s: String) => s.toLowerCase
  val words = (s: String) => s.split("\\s+").toList
  val nonEmpty = (list: List[String]) => list.filter(_.nonEmpty)
  val sort = (list: List[String]) => list.sorted
  val unique = (list: List[String]) => list.distinct
  
  // 組合範例
  def processText(text: String): List[String] = {
    text
      |> trim
      |> lowercase
      |> words
      |> nonEmpty
      |> unique
      |> sort
  }
  
  // 測試
  def main(args: Array[String]): Unit = {
    val text = "  Hello World  hello Scala  Scala Programming  "
    val result = processText(text)
    println(result)  // List(hello, programming, scala, world)
  }
}
```

### 練習 5: 部分應用與柯里化應用

```scala
object ConfigurableLogger {
  // 日誌等級
  sealed trait LogLevel
  case object DEBUG extends LogLevel
  case object INFO extends LogLevel
  case object WARN extends LogLevel
  case object ERROR extends LogLevel
  
  // 柯里化的日誌函數
  def log(minLevel: LogLevel)(level: LogLevel)(timestamp: Long)(message: String): Unit = {
    val levels = List(DEBUG, INFO, WARN, ERROR)
    if (levels.indexOf(level) >= levels.indexOf(minLevel)) {
      val levelStr = level match {
        case DEBUG => "DEBUG"
        case INFO => "INFO"
        case WARN => "WARN"
        case ERROR => "ERROR"
      }
      println(s"[$levelStr] [$timestamp] $message")
    }
  }
  
  // 建立不同設定的日誌記錄器
  val productionLogger = log(INFO) _
  val developmentLogger = log(DEBUG) _
  
  val prodInfo = productionLogger(INFO) _
  val prodError = productionLogger(ERROR) _
  val devDebug = developmentLogger(DEBUG) _
  
  def main(args: Array[String]): Unit = {
    val now = System.currentTimeMillis()
    
    // 生產環境不會顯示 DEBUG
    productionLogger(DEBUG)(now)("這是除錯訊息")
    prodInfo(now)("應用程式啟動")
    prodError(now)("發生錯誤")
    
    println()
    
    // 開發環境會顯示所有等級
    devDebug(now)("這是除錯訊息")
    developmentLogger(INFO)(now)("應用程式啟動")
    developmentLogger(ERROR)(now)("發生錯誤")
  }
}
```

### 練習 6: 遞迴樹操作

```scala
object TreeOperations {
  // 二元搜尋樹定義
  sealed trait BST[+A]
  case object Empty extends BST[Nothing]
  case class Node[A](value: A, left: BST[A], right: BST[A]) extends BST[A]
  
  // 插入元素
  def insert[A](tree: BST[A], elem: A)(using ord: Ordering[A]): BST[A] = tree match {
    case Empty => Node(elem, Empty, Empty)
    case Node(value, left, right) =>
      if (ord.lt(elem, value)) Node(value, insert(left, elem), right)
      else if (ord.gt(elem, value)) Node(value, left, insert(right, elem))
      else tree
  }
  
  // 搜尋元素
  def contains[A](tree: BST[A], elem: A)(using ord: Ordering[A]): Boolean = tree match {
    case Empty => false
    case Node(value, left, right) =>
      if (ord.lt(elem, value)) contains(left, elem)
      else if (ord.gt(elem, value)) contains(right, elem)
      else true
  }
  
  // 中序遍歷
  def inOrder[A](tree: BST[A]): List[A] = tree match {
    case Empty => Nil
    case Node(value, left, right) => inOrder(left) ++ List(value) ++ inOrder(right)
  }
  
  // 樹的大小
  def size[A](tree: BST[A]): Int = tree match {
    case Empty => 0
    case Node(_, left, right) => 1 + size(left) + size(right)
  }
  
  // 樹的高度
  def height[A](tree: BST[A]): Int = tree match {
    case Empty => 0
    case Node(_, left, right) => 1 + math.max(height(left), height(right))
  }
  
  // 測試
  def main(args: Array[String]): Unit = {
    val tree = List(5, 3, 7, 1, 9, 4, 6)
      .foldLeft(Empty: BST[Int])(insert)
    
    println(s"中序遍歷: ${inOrder(tree)}")
    println(s"大小: ${size(tree)}")
    println(s"高度: ${height(tree)}")
    println(s"包含 4? ${contains(tree, 4)}")
    println(s"包含 8? ${contains(tree, 8)}")
  }
}
```

### 練習 7: 高階函數應用

```scala
object HigherOrderFunctions {
  // 重試機制
  def retry[T](maxAttempts: Int)(f: => T): Option[T] = {
    @tailrec
    def attempt(remaining: Int): Option[T] = {
      try {
        Some(f)
      } catch {
        case _: Exception if remaining > 1 =>
          println(s"失敗,剩餘嘗試次數: ${remaining - 1}")
          attempt(remaining - 1)
        case _: Exception =>
          None
      }
    }
    attempt(maxAttempts)
  }
  
  // 計時器
  def timed[T](name: String)(block: => T): T = {
    val start = System.nanoTime()
    val result = block
    val end = System.nanoTime()
    println(f"$name 執行時間: ${(end - start) / 1000000.0}%.2f ms")
    result
  }
  
  // 條件執行
  def when[T](condition: Boolean)(block: => T): Option[T] = {
    if (condition) Some(block) else None
  }
  
  // 重複執行
  def times(n: Int)(block: => Unit): Unit = {
    (1 to n).foreach(_ => block)
  }
  
  // 測試
  def main(args: Array[String]): Unit = {
    // 重試範例
    var attempts = 0
    val result = retry(3) {
      attempts += 1
      if (attempts < 3) throw new Exception("失敗")
      else "成功"
    }
    println(s"結果: $result")
    
    // 計時範例
    timed("排序") {
      (1 to 1000000).sorted
    }
    
    // 條件執行
    when(5 > 3) {
      println("條件為真")
    }
    
    // 重複執行
    times(3) {
      println("重複執行")
    }
  }
}
```

---

## 12. 重點總結

### 函數定義
- 使用 `def` 定義方法
- 支援返回型別推導
- 單行函數可省略大括號
- 返回值是最後一個表達式

### 方法 vs 函數
- 方法是類別的成員
- 函數是物件,可以賦值和傳遞
- 方法可以轉換為函數 (Eta Expansion)

### 參數類型
- 預設參數和具名參數
- 可變參數 (`*`)
- 傳名參數 (`=>`)

### 高階函數
- 函數可作為參數
- 函數可作為返回值
- 支援函數組合

### 匿名函數
- Lambda 表達式
- 底線簡寫
- 型別推導

### 進階概念
- 閉包:捕獲外部變數
- 柯里化:多參數列表
- 部分應用:固定部分參數
- 尾遞迴:優化遞迴
- 函數組合:andThen 和 compose

---

## 下一步

完成第三部分後,您已經掌握:
- ✅ 函數與方法的定義與使用
- ✅ 高階函數的概念與應用
- ✅ 函數式程式設計的核心技巧
- ✅ 遞迴與優化

**接下來學習:**
- [第四部分:物件導向程式設計](scala_part4_oop.md) - 類別、物件、繼承、Trait

準備好繼續了嗎?

---

> [« 上一篇：基本語法](scala_part2_basic_syntax.md) | [📚 目錄](../README.md) | [下一篇：物件導向程式設計 »](scala_part4_oop.md)
