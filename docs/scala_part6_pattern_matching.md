# Scala 教學 - 第六部分：Pattern Matching（模式比對）

> [Runnable example and tests](../examples/src/examples/patterns) | [« 上一篇：集合操作](scala_part5_collections.md) | [📚 目錄](../README.md) | [下一篇：錯誤處理 »](scala_part7_error_handling.md)

---

## 目錄
1. [Pattern Matching 基礎](#1-pattern-matching模式比對基礎)
2. [Constant Pattern](#2-constant-pattern常量模式)
3. [Variable Pattern](#3-variable-pattern變數模式)
4. [Constructor Pattern](#4-constructor-pattern建構子模式)
5. [Sequence Pattern](#5-sequence-pattern序列模式)
6. [Tuple Pattern](#6-tuple-pattern元組模式)
7. [Type Pattern](#7-type-pattern型別模式)
8. [Pattern Guard](#8-pattern-guard模式守衛條件)
9. [Pattern Binding](#9-pattern-binding模式綁定)
10. [Regular Expression Pattern](#10-regular-expression-pattern正規表示式模式)
11. [PartialFunction](#11-partialfunction部分函數)
12. [Option 模式比對](#12-option-模式比對)
13. [實作練習](#13-實作練習)

---

## 1. Pattern Matching（模式比對）基礎

### 1.1 match 表達式

```scala
// 基本語法
val x = 5

x match {
  case 1 => "one"
  case 2 => "two"
  case 3 => "three"
  case _ => "other"  // _ 是萬用字元
}

// match 是表達式,會返回值
val result = x match {
  case 1 => "one"
  case 2 => "two"
  case _ => "many"
}
println(result)  // "many"
```

### 1.2 與 switch 的差異

```scala
// Java switch (只能用於基本型別和字串)
// switch(x) {
//   case 1: return "one";
//   case 2: return "two";
//   default: return "other";
// }

// Scala match (更強大)
// - 可以匹配任何型別
// - 可以匹配複雜結構
// - 是表達式,返回值
// - 不需要 break
// - 會檢查完整性 (exhaustiveness)

def describe(x: Any): String = x match {
  case 1 => "integer one"
  case "hello" => "string hello"
  case true => "boolean true"
  case List(1, 2, 3) => "list of 1, 2, 3"
  case _ => "something else"
}
```

### 1.3 完整性檢查

```scala
sealed trait Color
case object Red extends Color
case object Green extends Color
case object Blue extends Color

// 編譯器會警告:缺少 Blue 的情況
def colorName(color: Color): String = color match {
  case Red => "red"
  case Green => "green"
  // 缺少 Blue - 編譯警告!
}

// 正確版本
def colorNameComplete(color: Color): String = color match {
  case Red => "red"
  case Green => "green"
  case Blue => "blue"
}
```

---

## 2. Constant Pattern（常量模式）

### 2.1 Literal Matching（字面值比對）

```scala
// 數字匹配
def matchNumber(n: Int): String = n match {
  case 0 => "zero"
  case 1 => "one"
  case 2 => "two"
  case _ => "many"
}

// 字串匹配
def matchString(s: String): String = s match {
  case "hello" => "greeting"
  case "bye" => "farewell"
  case "" => "empty"
  case _ => "other"
}

// 布林值匹配
def matchBoolean(b: Boolean): String = b match {
  case true => "yes"
  case false => "no"
}

// 字元匹配
def matchChar(c: Char): String = c match {
  case 'a' | 'e' | 'i' | 'o' | 'u' => "vowel"
  case _ => "consonant"
}
```

### 2.2 常量值匹配

```scala
val MaxSize = 100
val MinSize = 0

def validateSize(size: Int): String = size match {
  case MinSize => "minimum"
  case MaxSize => "maximum"
  case s if s < MinSize => "too small"
  case s if s > MaxSize => "too large"
  case _ => "valid"
}

// 使用物件中的常量
object Constants {
  val SUCCESS = 200
  val NOT_FOUND = 404
  val SERVER_ERROR = 500
}

def handleStatus(code: Int): String = code match {
  case Constants.SUCCESS => "OK"
  case Constants.NOT_FOUND => "Not Found"
  case Constants.SERVER_ERROR => "Server Error"
  case _ => "Unknown"
}
```

---

## 3. Variable Pattern（變數模式）

### 3.1 變數綁定

```scala
// 變數模式會匹配任何值並綁定
def describe(x: Any): String = x match {
  case 0 => "zero"
  case n: Int => s"integer: $n"  // n 綁定匹配的值
  case s: String => s"string: $s"
  case other => s"something: $other"
}

describe(42)      // "integer: 42"
describe("hi")    // "string: hi"
describe(true)    // "something: true"
```

### 3.2 變數 vs 常量

```scala
val x = 10

// 這會匹配任何值,因為 x 是變數模式
5 match {
  case x => s"matched: $x"  // 總是匹配,x = 5
}

// 要匹配常量 x,使用反引號
5 match {
  case `x` => "equals to x"  // 只在值為 10 時匹配
  case _ => "not equals to x"
}

// 實用範例
object Color {
  val Red = "#FF0000"
  val Green = "#00FF00"
  val Blue = "#0000FF"
}

def identifyColor(hex: String): String = hex match {
  case Color.Red => "red"      // 匹配常量
  case Color.Green => "green"
  case Color.Blue => "blue"
  case color => s"unknown: $color"  // 變數模式
}
```

---

## 4. Constructor Pattern（建構子模式）

### 4.1 Case Class 解構

```scala
case class Person(name: String, age: Int)

def greet(person: Person): String = person match {
  case Person("Alice", _) => "Hi Alice!"
  case Person(name, age) if age < 18 => s"Hi young $name!"
  case Person(name, age) => s"Hello $name, you are $age"
}

val alice = Person("Alice", 25)
val bob = Person("Bob", 15)

greet(alice)  // "Hi Alice!"
greet(bob)    // "Hi young Bob!"
```

### 4.2 嵌套解構

```scala
case class Address(city: String, country: String)
case class Person(name: String, age: Int, address: Address)

def describe(person: Person): String = person match {
  case Person(name, _, Address("Taipei", "Taiwan")) =>
    s"$name lives in Taipei, Taiwan"
  
  case Person(name, age, Address(city, "Taiwan")) =>
    s"$name, $age years old, lives in $city, Taiwan"
  
  case Person(name, _, Address(city, country)) =>
    s"$name lives in $city, $country"
}

val p1 = Person("Alice", 25, Address("Taipei", "Taiwan"))
val p2 = Person("Bob", 30, Address("Tokyo", "Japan"))

describe(p1)  // "Alice lives in Taipei, Taiwan"
describe(p2)  // "Bob lives in Tokyo, Japan"
```

### 4.3 複雜結構解構

```scala
case class Company(name: String, employees: List[Person])

def analyzeCompany(company: Company): String = company match {
  case Company(name, Nil) =>
    s"$name has no employees"
  
  case Company(name, List(person)) =>
    s"$name has one employee: ${person.name}"
  
  case Company(name, List(p1, p2)) =>
    s"$name has two employees: ${p1.name} and ${p2.name}"
  
  case Company(name, employees) =>
    s"$name has ${employees.length} employees"
}

// 深度嵌套
case class Department(name: String, manager: Person, team: List[Person])
case class Organization(name: String, departments: List[Department])

def findManager(org: Organization, deptName: String): Option[String] = org match {
  case Organization(_, departments) =>
    departments.collectFirst {
      case Department(`deptName`, Person(managerName, _, _), _) => managerName
    }
}
```

---

## 5. Sequence Pattern（序列模式）

### 5.1 List 模式

```scala
// 空列表
def isEmpty(list: List[Int]): String = list match {
  case Nil => "empty"
  case _ => "not empty"
}

// 固定元素
def matchList(list: List[Int]): String = list match {
  case List(1, 2, 3) => "exactly 1, 2, 3"
  case List(1, _, 3) => "1, something, 3"
  case List(1, _*) => "starts with 1"
  case _ => "other"
}

// head :: tail 模式
def processHead(list: List[Int]): String = list match {
  case Nil => "empty"
  case head :: Nil => s"only $head"
  case head :: tail => s"head: $head, tail: $tail"
}

// 更複雜的模式
def analyze(list: List[Int]): String = list match {
  case Nil => "empty"
  case x :: Nil => s"single element: $x"
  case x :: y :: Nil => s"two elements: $x, $y"
  case x :: y :: rest => s"starts with $x, $y, has ${rest.length} more"
}

analyze(List())           // "empty"
analyze(List(1))          // "single element: 1"
analyze(List(1, 2))       // "two elements: 1, 2"
analyze(List(1, 2, 3, 4)) // "starts with 1, 2, has 2 more"
```

### 5.2 可變長度序列

```scala
// 使用 _* 匹配任意數量元素
def matchAny(list: List[Int]): String = list match {
  case List(1, 2, _*) => "starts with 1, 2"
  case List(_, _, 3) => "ends with 3, has 3 elements"
  case List(first, _*) => s"first is $first"
  case _ => "other"
}

// 提取前幾個和剩餘
def splitList(list: List[Int]): String = list match {
  case first :: second :: rest => 
    s"first: $first, second: $second, rest: $rest"
  case _ => "less than 2 elements"
}

// 實用範例:命令解析
def parseCommand(args: List[String]): String = args match {
  case "help" :: Nil => "Showing help"
  case "create" :: name :: Nil => s"Creating: $name"
  case "delete" :: name :: Nil => s"Deleting: $name"
  case "list" :: Nil => "Listing all"
  case "search" :: query :: Nil => s"Searching for: $query"
  case cmd :: _ => s"Unknown command: $cmd"
  case Nil => "No command"
}
```

### 5.3 Array 和 Vector 模式

```scala
// Array 模式 (需要使用 Array.apply)
def matchArray(arr: Array[Int]): String = arr match {
  case Array() => "empty"
  case Array(x) => s"one element: $x"
  case Array(x, y) => s"two elements: $x, $y"
  case _ => "more elements"
}

// Vector 類似
def matchVector(vec: Vector[Int]): String = vec match {
  case Vector() => "empty"
  case Vector(x) => s"one element: $x"
  case x +: xs => s"head: $x, tail: $xs"
  case _ => "other"
}
```

---

## 6. Tuple Pattern（元組模式）

### 6.1 元組解構

```scala
// 配對 (Pair)
def describePair(pair: (Int, String)): String = pair match {
  case (1, "one") => "perfect match"
  case (n, "one") => s"$n and one"
  case (1, s) => s"one and $s"
  case (n, s) => s"$n and $s"
}

// 三元組
def describe3(triple: (Int, String, Boolean)): String = triple match {
  case (1, "one", true) => "all match"
  case (n, s, true) => s"$n, $s, and true"
  case (n, s, false) => s"$n, $s, and false"
}

// 嵌套元組
def nested(t: ((Int, Int), String)): String = t match {
  case ((x, y), s) => s"point ($x, $y) with label $s"
}
```

### 6.2 實用範例

```scala
// 座標處理
type Point = (Double, Double)

def classifyPoint(p: Point): String = p match {
  case (0, 0) => "origin"
  case (0, y) => s"on y-axis at $y"
  case (x, 0) => s"on x-axis at $x"
  case (x, y) if x == y => s"on diagonal at ($x, $y)"
  case (x, y) if x > 0 && y > 0 => "quadrant I"
  case (x, y) if x < 0 && y > 0 => "quadrant II"
  case (x, y) if x < 0 && y < 0 => "quadrant III"
  case (x, y) => "quadrant IV"
}

// Map 迭代
val scores = Map("Alice" -> 95, "Bob" -> 87, "Charlie" -> 92)

scores.foreach {
  case (name, score) if score >= 90 => println(s"$name: Excellent")
  case (name, score) => println(s"$name: $score")
}

// 交換元素
def swap[A, B](pair: (A, B)): (B, A) = pair match {
  case (a, b) => (b, a)
}
```

---

## 7. Type Pattern（型別模式）

### 7.1 基本型別匹配

```scala
def typeMatch(x: Any): String = x match {
  case i: Int => s"integer: $i"
  case s: String => s"string: $s"
  case d: Double => s"double: $d"
  case b: Boolean => s"boolean: $b"
  case _ => "unknown type"
}

typeMatch(42)       // "integer: 42"
typeMatch("hello")  // "string: hello"
typeMatch(3.14)     // "double: 3.14"
typeMatch(true)     // "boolean: true"
```

### 7.2 集合型別匹配

```scala
def collectionMatch(x: Any): String = x match {
  case list: List[?] => s"list of ${list.length} elements"
  case set: Set[?] => s"set of ${set.size} elements"
  case map: Map[?, ?] => s"map of ${map.size} entries"
  case arr: Array[?] => s"array of ${arr.length} elements"
  case _ => "other type"
}

collectionMatch(List(1, 2, 3))        // "list of 3 elements"
collectionMatch(Set("a", "b"))        // "set of 2 elements"
collectionMatch(Map("x" -> 1))        // "map of 1 entries"
```

### 7.3 Type Erasure（型別擦除）注意事項

```scala
// ⚠️ 警告:型別擦除
// JVM 會擦除泛型型別資訊

def buggyMatch(x: Any): String = x match {
  case list: List[Int] => "list of ints"      // 警告!
  case list: List[String] => "list of strings" // 實際上永遠不會匹配
  case _ => "other"
}

buggyMatch(List(1, 2, 3))      // "list of ints"
buggyMatch(List("a", "b"))     // "list of ints" - 錯誤!

// 正確做法:檢查元素型別
def correctMatch(x: Any): String = x match {
  case list: List[?] if list.nonEmpty && list.head.isInstanceOf[Int] =>
    "list of ints"
  case list: List[?] if list.nonEmpty && list.head.isInstanceOf[String] =>
    "list of strings"
  case list: List[?] => "empty list"
  case _ => "other"
}
```

### 7.4 階層型別匹配

```scala
abstract class Animal
case class Dog(name: String) extends Animal
case class Cat(name: String) extends Animal
case class Bird(name: String) extends Animal

def sound(animal: Animal): String = animal match {
  case Dog(name) => s"$name says Woof!"
  case Cat(name) => s"$name says Meow!"
  case Bird(name) => s"$name says Tweet!"
  case _ => "Unknown animal"
}

sound(Dog("Buddy"))   // "Buddy says Woof!"
sound(Cat("Whiskers")) // "Whiskers says Meow!"
```

---

## 8. Pattern Guard（模式守衛條件）

### 8.1 基本 Guard

```scala
// if 條件限制匹配
def classify(n: Int): String = n match {
  case x if x < 0 => "negative"
  case 0 => "zero"
  case x if x > 0 && x < 10 => "small positive"
  case x if x >= 10 => "large positive"
}

classify(-5)  // "negative"
classify(5)   // "small positive"
classify(15)  // "large positive"
```

### 8.2 複雜 Guard

```scala
case class Person(name: String, age: Int, city: String)

def categorize(person: Person): String = person match {
  case Person(name, age, "Taipei") if age < 18 =>
    s"Young $name from Taipei"
  
  case Person(name, age, "Taipei") if age >= 18 && age < 65 =>
    s"Adult $name from Taipei"
  
  case Person(name, age, "Taipei") if age >= 65 =>
    s"Senior $name from Taipei"
  
  case Person(name, age, city) if age < 18 =>
    s"Young $name from $city"
  
  case Person(name, _, city) =>
    s"$name from $city"
}
```

### 8.3 Guard 中的函數呼叫

```scala
def isPrime(n: Int): Boolean = {
  if (n <= 1) false
  else if (n == 2) true
  else !(2 to math.sqrt(n).toInt).exists(i => n % i == 0)
}

def classifyNumber(n: Int): String = n match {
  case x if x < 0 => "negative"
  case 0 => "zero"
  case 1 => "one"
  case x if isPrime(x) => s"$x is prime"
  case x if x % 2 == 0 => s"$x is even"
  case x => s"$x is odd composite"
}

classifyNumber(7)   // "7 is prime"
classifyNumber(8)   // "8 is even"
classifyNumber(9)   // "9 is odd composite"
```

### 8.4 多重條件

```scala
case class Student(name: String, score: Int, attendance: Int)

def evaluateStudent(student: Student): String = student match {
  case Student(name, score, attendance) 
    if score >= 90 && attendance >= 80 =>
    s"$name: Excellent (A)"
  
  case Student(name, score, attendance) 
    if score >= 80 && attendance >= 70 =>
    s"$name: Good (B)"
  
  case Student(name, score, attendance) 
    if score >= 60 && attendance >= 60 =>
    s"$name: Pass (C)"
  
  case Student(name, _, _) =>
    s"$name: Fail (F)"
}
```

---

## 9. Pattern Binding（模式綁定）

### 9.1 @ 符號綁定

```scala
// 使用 @ 綁定整個匹配值
case class Address(city: String, country: String)
case class Person(name: String, address: Address)

def describe(person: Person): String = person match {
  case Person(name, addr @ Address("Taipei", _)) =>
    s"$name lives in Taipei: $addr"
  
  case Person(name, addr @ Address(_, "Taiwan")) =>
    s"$name lives in Taiwan: $addr"
  
  case Person(name, address) =>
    s"$name lives at $address"
}

val p = Person("Alice", Address("Taipei", "Taiwan"))
describe(p)  // "Alice lives in Taipei: Address(Taipei,Taiwan)"
```

### 9.2 嵌套綁定

```scala
sealed trait Tree
case class Leaf(value: Int) extends Tree
case class Branch(left: Tree, right: Tree) extends Tree

def analyze(tree: Tree): String = tree match {
  case leaf @ Leaf(value) =>
    s"Leaf with value $value: $leaf"
  
  case branch @ Branch(left @ Leaf(_), right @ Leaf(_)) =>
    s"Branch with two leaves: $branch"
  
  case Branch(left, right) =>
    s"Branch with subtrees"
}
```

### 9.3 實用範例

```scala
// HTTP 回應處理
sealed trait Response
case class Success(code: Int, body: String) extends Response
case class Error(code: Int, message: String) extends Response

def handleResponse(response: Response): String = response match {
  case success @ Success(200, _) =>
    s"OK: $success"
  
  case success @ Success(201, _) =>
    s"Created: $success"
  
  case error @ Error(404, _) =>
    s"Not Found: $error"
  
  case error @ Error(code, _) if code >= 500 =>
    s"Server Error: $error"
  
  case other =>
    s"Other response: $other"
}
```

---

## 10. Regular Expression Pattern（正規表示式模式）

### 10.1 基本正則匹配

```scala
val EmailPattern = "(.+)@(.+)\\.(.+)".r

def validateEmail(email: String): String = email match {
  case EmailPattern(user, domain, tld) =>
    s"User: $user, Domain: $domain, TLD: $tld"
  case _ =>
    "Invalid email"
}

validateEmail("alice@example.com")
// "User: alice, Domain: example, TLD: com"

validateEmail("invalid")
// "Invalid email"
```

### 10.2 複雜模式

```scala
// 電話號碼
val PhonePattern = """(\d{3})-(\d{4})""".r

def parsePhone(phone: String): Option[(String, String)] = phone match {
  case PhonePattern(prefix, number) => Some((prefix, number))
  case _ => None
}

parsePhone("123-4567")  // Some((123,4567))
parsePhone("invalid")   // None

// 日期
val DatePattern = """(\d{4})-(\d{2})-(\d{2})""".r

def parseDate(date: String): String = date match {
  case DatePattern(year, month, day) =>
    s"Year: $year, Month: $month, Day: $day"
  case _ =>
    "Invalid date format"
}

parseDate("2024-01-15")
// "Year: 2024, Month: 01, Day: 15"
```

### 10.3 URL 解析

```scala
val UrlPattern = """(https?)://([^/]+)(/.*)?""".r

def parseUrl(url: String): String = url match {
  case UrlPattern(protocol, domain, path) =>
    s"Protocol: $protocol, Domain: $domain, Path: ${Option(path).getOrElse("/")}"
  case _ =>
    "Invalid URL"
}

parseUrl("https://example.com/path/to/page")
// "Protocol: https, Domain: example.com, Path: /path/to/page"

parseUrl("http://example.com")
// "Protocol: http, Domain: example.com, Path: /"
```

### 10.4 日誌解析

```scala
val LogPattern = """(\d{4}-\d{2}-\d{2}) (\d{2}:\d{2}:\d{2}) \[(\w+)\] (.+)""".r

case class LogEntry(date: String, time: String, level: String, message: String)

def parseLog(line: String): Option[LogEntry] = line match {
  case LogPattern(date, time, level, message) =>
    Some(LogEntry(date, time, level, message))
  case _ =>
    None
}

val log = "2024-01-15 14:30:45 [ERROR] Connection failed"
parseLog(log)
// Some(LogEntry(2024-01-15,14:30:45,ERROR,Connection failed))
```

---

## 11. `PartialFunction`（部分函數）

### 11.1 PartialFunction 基礎

```scala
// 部分函數:只對輸入的子集有定義
val divide: PartialFunction[(Int, Int), Int] = {
  case (x, y) if y != 0 => x / y
}

// 檢查是否有定義
divide.isDefinedAt((10, 2))  // true
divide.isDefinedAt((10, 0))  // false

// 使用
divide((10, 2))  // 5
// divide((10, 0))  // MatchError!

// 安全使用
divide.lift((10, 0))  // None
divide.lift((10, 2))  // Some(5)
```

### 11.2 collect 方法

```scala
val numbers = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// collect 使用部分函數
val evens = numbers.collect {
  case x if x % 2 == 0 => x * 2
}
// List(4, 8, 12, 16, 20)

// 等價於 filter + map
val evens2 = numbers.filter(_ % 2 == 0).map(_ * 2)
```

### 11.3 實用範例

```scala
// 處理混合型別列表
val mixed: List[Any] = List(1, "hello", 2.5, true, 42, "world")

val strings = mixed.collect {
  case s: String => s.toUpperCase
}
// List("HELLO", "WORLD")

val numbers = mixed.collect {
  case i: Int => i * 2
  case d: Double => d * 2
}
// List(2, 5.0, 84)

// Option 處理
val maybeNumbers = List(Some(1), None, Some(2), None, Some(3))

val values = maybeNumbers.collect {
  case Some(x) => x * 2
}
// List(2, 4, 6)
```

### 11.4 組合部分函數

```scala
val handleInt: PartialFunction[Any, String] = {
  case i: Int => s"integer: $i"
}

val handleString: PartialFunction[Any, String] = {
  case s: String => s"string: $s"
}

// 組合部分函數
val handleBoth = handleInt orElse handleString

handleBoth(42)      // "integer: 42"
handleBoth("hi")    // "string: hi"
// handleBoth(true)  // MatchError

// 帶預設值
val handleAll = handleBoth orElse {
  case x => s"other: $x"
}

handleAll(true)  // "other: true"
```

---

## 12. Option 模式比對

### 12.1 基本 Option 匹配

```scala
def describe(opt: Option[Int]): String = opt match {
  case Some(value) => s"has value: $value"
  case None => "no value"
}

describe(Some(42))  // "has value: 42"
describe(None)      // "no value"
```

### 12.2 嵌套 Option

```scala
def process(opt: Option[Option[Int]]): String = opt match {
  case Some(Some(value)) => s"nested value: $value"
  case Some(None) => "outer Some, inner None"
  case None => "outer None"
}

process(Some(Some(42)))  // "nested value: 42"
process(Some(None))      // "outer Some, inner None"
process(None)            // "outer None"
```

### 12.3 Option 與 for 推導式

```scala
def divide(a: Int, b: Int): Option[Int] = {
  if (b != 0) Some(a / b) else None
}

// 鏈式 Option 操作
val result = for {
  a <- divide(10, 2)   // Some(5)
  b <- divide(a, 2)    // Some(2)
  c <- divide(b, 1)    // Some(2)
} yield c

println(result)  // Some(2)

// 短路行為
val result2 = for {
  a <- divide(10, 2)
  b <- divide(a, 0)    // None - 短路
  c <- divide(b, 1)
} yield c

println(result2)  // None
```

### 12.4 實用範例

```scala
case class User(id: Int, name: String, email: Option[String])

val users = List(
  User(1, "Alice", Some("alice@example.com")),
  User(2, "Bob", None),
  User(3, "Charlie", Some("charlie@example.com"))
)

// 提取有 email 的用戶
val withEmail = users.collect {
  case User(_, name, Some(email)) => (name, email)
}
// List((Alice,alice@example.com), (Charlie,charlie@example.com))

// 查找用戶
def findUser(id: Int): Option[User] = {
  users.find(_.id == id)
}

findUser(1) match {
  case Some(User(_, name, Some(email))) =>
    println(s"Found $name with email $email")
  case Some(User(_, name, None)) =>
    println(s"Found $name without email")
  case None =>
    println("User not found")
}
```

---

## 13. 實作練習

### 練習 1: 計算器

```scala
// 表達式樹
sealed trait Expr
case class Number(value: Double) extends Expr
case class Add(left: Expr, right: Expr) extends Expr
case class Subtract(left: Expr, right: Expr) extends Expr
case class Multiply(left: Expr, right: Expr) extends Expr
case class Divide(left: Expr, right: Expr) extends Expr

object Calculator {
  def eval(expr: Expr): Double = expr match {
    case Number(value) => value
    
    case Add(left, right) =>
      eval(left) + eval(right)
    
    case Subtract(left, right) =>
      eval(left) - eval(right)
    
    case Multiply(left, right) =>
      eval(left) * eval(right)
    
    case Divide(left, right) =>
      val divisor = eval(right)
      if (divisor != 0) eval(left) / divisor
      else throw new ArithmeticException("Division by zero")
  }
  
  // 簡化表達式
  def simplify(expr: Expr): Expr = expr match {
    // 加法簡化
    case Add(Number(0), x) => simplify(x)
    case Add(x, Number(0)) => simplify(x)
    case Add(Number(a), Number(b)) => Number(a + b)
    
    // 乘法簡化
    case Multiply(Number(0), _) => Number(0)
    case Multiply(_, Number(0)) => Number(0)
    case Multiply(Number(1), x) => simplify(x)
    case Multiply(x, Number(1)) => simplify(x)
    case Multiply(Number(a), Number(b)) => Number(a * b)
    
    // 遞迴簡化
    case Add(left, right) => 
      Add(simplify(left), simplify(right))
    case Multiply(left, right) => 
      Multiply(simplify(left), simplify(right))
    case Subtract(left, right) => 
      Subtract(simplify(left), simplify(right))
    case Divide(left, right) => 
      Divide(simplify(left), simplify(right))
    
    case other => other
  }
  
  // 測試
  def main(args: Array[String]): Unit = {
    // (3 + 5) * 2
    val expr1 = Multiply(Add(Number(3), Number(5)), Number(2))
    println(s"Result: ${eval(expr1)}")  // 16.0
    
    // 0 * (3 + 5)
    val expr2 = Multiply(Number(0), Add(Number(3), Number(5)))
    println(s"Original: $expr2")
    println(s"Simplified: ${simplify(expr2)}")  // Number(0)
    
    // 1 * x + 0
    val expr3 = Add(Multiply(Number(1), Number(42)), Number(0))
    println(s"Simplified: ${simplify(expr3)}")  // Number(42)
  }
}
```

### 練習 2: JSON 解析器

```scala
// 簡單的 JSON AST
sealed trait Json
case object JsonNull extends Json
case class JsonBool(value: Boolean) extends Json
case class JsonNumber(value: Double) extends Json
case class JsonString(value: String) extends Json
case class JsonArray(values: List[Json]) extends Json
case class JsonObject(fields: Map[String, Json]) extends Json

object JsonParser {
  // 美化輸出
  def pretty(json: Json, indent: Int = 0): String = {
    val spaces = "  " * indent
    json match {
      case JsonNull => "null"
      case JsonBool(value) => value.toString
      case JsonNumber(value) => value.toString
      case JsonString(value) => s""""$value""""
      
      case JsonArray(Nil) => "[]"
      case JsonArray(values) =>
        val elements = values.map(v => pretty(v, indent + 1))
        s"[\n${elements.map(spaces + "  " + _).mkString(",\n")}\n$spaces]"
      
      case JsonObject(fields) if fields.isEmpty => "{}"
      case JsonObject(fields) =>
        val pairs = fields.map { case (key, value) =>
          s""""$key": ${pretty(value, indent + 1)}"""
        }
        s"{\n${pairs.map(spaces + "  " + _).mkString(",\n")}\n$spaces}"
    }
  }
  
  // 查找路徑
  def find(json: Json, path: List[String]): Option[Json] = (json, path) match {
    case (value, Nil) => Some(value)
    
    case (JsonObject(fields), key :: rest) =>
      fields.get(key).flatMap(find(_, rest))
    
    case (JsonArray(values), indexStr :: rest) =>
      indexStr.toIntOption
        .flatMap(i => values.lift(i))
        .flatMap(find(_, rest))
    
    case _ => None
  }
  
  // 更新值
  def update(json: Json, path: List[String], newValue: Json): Json = {
    (json, path) match {
      case (_, Nil) => newValue
      
      case (JsonObject(fields), key :: rest) =>
        val updatedValue = fields.get(key) match {
          case Some(value) => update(value, rest, newValue)
          case None => newValue
        }
        JsonObject(fields + (key -> updatedValue))
      
      case (JsonArray(values), indexStr :: rest) =>
        indexStr.toIntOption match {
          case Some(i) if i >= 0 && i < values.length =>
            val updated = update(values(i), rest, newValue)
            JsonArray(values.updated(i, updated))
          case _ => json
        }
      
      case _ => json
    }
  }
  
  // 測試
  def main(args: Array[String]): Unit = {
    val json = JsonObject(Map(
      "name" -> JsonString("Alice"),
      "age" -> JsonNumber(25),
      "active" -> JsonBool(true),
      "address" -> JsonObject(Map(
        "city" -> JsonString("Taipei"),
        "country" -> JsonString("Taiwan")
      )),
      "hobbies" -> JsonArray(List(
        JsonString("reading"),
        JsonString("coding")
      ))
    ))
    
    println("=== Original JSON ===")
    println(pretty(json))
    
    println("\n=== Find city ===")
    println(find(json, List("address", "city")))
    
    println("\n=== Update age ===")
    val updated = update(json, List("age"), JsonNumber(26))
    println(pretty(updated))
  }
}
```

### 練習 3：State Machine（狀態機）

```scala
// 自動販賣機
sealed trait VendingMachineState
case object Idle extends VendingMachineState
case class AcceptingMoney(amount: Int) extends VendingMachineState
case class Dispensing(item: String) extends VendingMachineState

sealed trait Input
case class InsertCoin(amount: Int) extends Input
case class SelectItem(item: String, price: Int) extends Input
case object Cancel extends Input
case object Collect extends Input

case class VendingMachine(
  state: VendingMachineState,
  inventory: Map[String, Int],
  revenue: Int
) {
  def process(input: Input): (VendingMachine, String) = (state, input) match {
    // 閒置狀態
    case (Idle, InsertCoin(amount)) =>
      (copy(state = AcceptingMoney(amount)), s"已投入 $$$amount")
    
    case (Idle, _) =>
      (this, "請先投幣")
    
    // 接受金額狀態
    case (AcceptingMoney(current), InsertCoin(amount)) =>
      val total = current + amount
      (copy(state = AcceptingMoney(total)), s"總計 $$$total")
    
    case (AcceptingMoney(amount), SelectItem(item, price)) 
      if amount >= price && inventory.getOrElse(item, 0) > 0 =>
      val change = amount - price
      val newInventory = inventory.updated(item, inventory(item) - 1)
      val message = if (change > 0) 
        s"已選擇 $item,找零 $$$change" 
      else 
        s"已選擇 $item"
      (copy(
        state = Dispensing(item),
        inventory = newInventory,
        revenue = revenue + price
      ), message)
    
    case (AcceptingMoney(amount), SelectItem(item, price)) 
      if amount < price =>
      (this, s"金額不足,還需 $$${ price - amount}")
    
    case (AcceptingMoney(amount), SelectItem(item, _)) =>
      (this, s"$item 已售完")
    
    case (AcceptingMoney(amount), Cancel) =>
      (copy(state = Idle), s"已取消,退回 $$$amount")
    
    // 出貨狀態
    case (Dispensing(item), Collect) =>
      (copy(state = Idle), s"已取得 $item,交易完成")
    
    case (Dispensing(_), _) =>
      (this, "請先取走商品")
    
    case _ =>
      (this, "無效操作")
  }
}

object VendingMachineDemo {
  def main(args: Array[String]): Unit = {
    var machine = VendingMachine(
      state = Idle,
      inventory = Map("可樂" -> 5, "咖啡" -> 3, "茶" -> 10),
      revenue = 0
    )
    
    def execute(input: Input): Unit = {
      val (newMachine, message) = machine.process(input)
      machine = newMachine
      println(s">>> $message")
      println(s"狀態: ${machine.state}\n")
    }
    
    println("=== 販賣機測試 ===\n")
    
    execute(InsertCoin(10))
    execute(InsertCoin(10))
    execute(SelectItem("可樂", 15))
    execute(Collect)
    
    println("\n=== 第二次購買 ===\n")
    execute(InsertCoin(20))
    execute(SelectItem("咖啡", 25))
    execute(InsertCoin(10))
    execute(SelectItem("咖啡", 25))
    execute(Collect)
    
    println(s"\n總收入: $${machine.revenue}")
    println(s"庫存: ${machine.inventory}")
  }
}
```

### 練習 4: 命令解析器

```scala
// 命令 AST
sealed trait Command
case object Help extends Command
case object Exit extends Command
case class Echo(message: String) extends Command
case class Set(key: String, value: String) extends Command
case class Get(key: String) extends Command
case class Delete(key: String) extends Command
case class List(prefix: Option[String]) extends Command
case class Unknown(input: String) extends Command

object CommandParser {
  def parse(input: String): Command = {
    val parts = input.trim.split("\\s+").toList
    
    parts match {
      case "help" :: Nil => Help
      case "exit" :: Nil | "quit" :: Nil => Exit
      
      case "echo" :: message => 
        Echo(message.mkString(" "))
      
      case "set" :: key :: value :: Nil => 
        Set(key, value)
      
      case "get" :: key :: Nil => 
        Get(key)
      
      case "delete" :: key :: Nil | "del" :: key :: Nil => 
        Delete(key)
      
      case "list" :: Nil => 
        List(None)
      
      case "list" :: prefix :: Nil => 
        List(Some(prefix))
      
      case _ => 
        Unknown(input)
    }
  }
  
  def execute(
    command: Command,
    storage: Map[String, String]
  ): (Map[String, String], String) = command match {
    
    case Help =>
      (storage, """可用命令:
        |  help              - 顯示此幫助
        |  exit/quit         - 離開
        |  echo <message>    - 顯示訊息
        |  set <key> <value> - 設定值
        |  get <key>         - 取得值
        |  delete <key>      - 刪除值
        |  list [prefix]     - 列出所有鍵(可選前綴過濾)
        |""".stripMargin)
    
    case Exit =>
      (storage, "再見!")
    
    case Echo(message) =>
      (storage, message)
    
    case Set(key, value) =>
      (storage + (key -> value), s"已設定 $key = $value")
    
    case Get(key) =>
      storage.get(key) match {
        case Some(value) => (storage, s"$key = $value")
        case None => (storage, s"鍵 $key 不存在")
      }
    
    case Delete(key) =>
      if (storage.contains(key))
        (storage - key, s"已刪除 $key")
      else
        (storage, s"鍵 $key 不存在")
    
    case List(None) =>
      val keys = storage.keys.toList.sorted
      (storage, if (keys.isEmpty) "無資料" else keys.mkString(", "))
    
    case List(Some(prefix)) =>
      val keys = storage.keys.filter(_.startsWith(prefix)).toList.sorted
      (storage, if (keys.isEmpty) s"無前綴為 $prefix 的鍵" else keys.mkString(", "))
    
    case Unknown(input) =>
      (storage, s"未知命令: $input (輸入 'help' 查看可用命令)")
  }
  
  // REPL
  def repl(): Unit = {
    var storage = Map.empty[String, String]
    var running = true
    
    println("命令列介面 (輸入 'help' 查看命令)")
    
    while (running) {
      print("\n> ")
      val input = scala.io.StdIn.readLine()
      
      if (input != null && input.nonEmpty) {
        val command = parse(input)
        val (newStorage, message) = execute(command, storage)
        storage = newStorage
        println(message)
        
        if (command == Exit) {
          running = false
        }
      }
    }
  }
  
  def main(args: Array[String]): Unit = {
    repl()
  }
}
```

### 練習 5: 二元樹操作

```scala
sealed trait Tree[+A]
case object Empty extends Tree[Nothing]
case class Node[A](value: A, left: Tree[A], right: Tree[A]) extends Tree[A]

object TreeOps {
  // 大小
  def size[A](tree: Tree[A]): Int = tree match {
    case Empty => 0
    case Node(_, left, right) => 1 + size(left) + size(right)
  }
  
  // 深度
  def depth[A](tree: Tree[A]): Int = tree match {
    case Empty => 0
    case Node(_, left, right) => 1 + math.max(depth(left), depth(right))
  }
  
  // 映射
  def map[A, B](tree: Tree[A])(f: A => B): Tree[B] = tree match {
    case Empty => Empty
    case Node(value, left, right) =>
      Node(f(value), map(left)(f), map(right)(f))
  }
  
  // 折疊
  def fold[A, B](tree: Tree[A])(z: B)(f: (B, A, B) => B): B = tree match {
    case Empty => z
    case Node(value, left, right) =>
      f(fold(left)(z)(f), value, fold(right)(z)(f))
  }
  
  // 遍歷
  def inOrder[A](tree: Tree[A]): List[A] = tree match {
    case Empty => Nil
    case Node(value, left, right) =>
      inOrder(left) ++ List(value) ++ inOrder(right)
  }
  
  def preOrder[A](tree: Tree[A]): List[A] = tree match {
    case Empty => Nil
    case Node(value, left, right) =>
      value :: preOrder(left) ++ preOrder(right)
  }
  
  def postOrder[A](tree: Tree[A]): List[A] = tree match {
    case Empty => Nil
    case Node(value, left, right) =>
      postOrder(left) ++ postOrder(right) ++ List(value)
  }
  
  // 查找
  def contains[A](tree: Tree[A], target: A)(using ord: Ordering[A]): Boolean = {
    tree match {
      case Empty => false
      case Node(value, left, right) =>
        if (ord.equiv(value, target)) true
        else if (ord.lt(target, value)) contains(left, target)
        else contains(right, target)
    }
  }
  
  // 插入 (BST)
  def insert[A](tree: Tree[A], elem: A)(using ord: Ordering[A]): Tree[A] = {
    tree match {
      case Empty => Node(elem, Empty, Empty)
      case Node(value, left, right) =>
        if (ord.lteq(elem, value))
          Node(value, insert(left, elem), right)
        else
          Node(value, left, insert(right, elem))
    }
  }
  
  // 測試
  def main(args: Array[String]): Unit = {
    val tree = Node(5,
      Node(3,
        Node(1, Empty, Empty),
        Node(4, Empty, Empty)
      ),
      Node(8,
        Node(7, Empty, Empty),
        Node(9, Empty, Empty)
      )
    )
    
    println(s"Size: ${size(tree)}")
    println(s"Depth: ${depth(tree)}")
    println(s"In-order: ${inOrder(tree)}")
    println(s"Pre-order: ${preOrder(tree)}")
    println(s"Post-order: ${postOrder(tree)}")
    println(s"Contains 7: ${contains(tree, 7)}")
    println(s"Contains 10: ${contains(tree, 10)}")
    
    val doubled = map(tree)(_ * 2)
    println(s"Doubled: ${inOrder(doubled)}")
    
    val sum = fold(tree)(0)((l, v, r) => l + v + r)
    println(s"Sum: $sum")
  }
}
```

---

## 14. 重點總結

### 模式比對特性
- **強大且靈活**:可匹配值、型別、結構
- **完整性檢查**:編譯器會警告遺漏的情況
- **表達式**:返回值,可組合

### 常用模式
- **常量模式**:字面值匹配
- **變數模式**:綁定匹配值
- **建構子模式**:解構 case class
- **序列模式**:List、Array 解構
- **型別模式**:型別檢查

### 進階技巧
- **Guard 條件**:if 限制匹配
- **模式綁定**:@ 符號
- **正則表達式**:字串解析
- **部分函數**:collect 方法

### 最佳實踐
- 使用 sealed trait 確保完整性
- 優先匹配具體情況,最後使用 _
- 善用 Guard 簡化邏輯
- 結合 Option 處理可能缺失的值

---

## 下一步

完成第六部分後,您已經掌握:
- ✅ 模式比對的基本與進階用法
- ✅ 各種模式的應用場景
- ✅ Guard 條件與模式綁定
- ✅ 部分函數的使用

**接下來學習:**
- [第七部分:錯誤處理](scala_part7_error_handling.md) - Option、Either、Try 的深入應用

準備好繼續了嗎?

---

> [« 上一篇：集合操作](scala_part5_collections.md) | [📚 目錄](../README.md) | [下一篇：錯誤處理 »](scala_part7_error_handling.md)
