# Scala 教學 - 第七部分:錯誤處理

> [« 上一篇：模式比對](scala_part6_pattern_matching.md) | [📚 目錄](../README.md) | [下一篇：進階主題 »](scala_part8_advanced_topics.md)

---

## 目錄
1. [錯誤處理概覽](#1-錯誤處理概覽)
2. [Option 型別](#2-option-型別)
3. [Either 型別](#3-either-型別)
4. [Try 型別](#4-try-型別)
5. [異常處理](#5-異常處理)
6. [錯誤處理模式](#6-錯誤處理模式)
7. [組合錯誤處理](#7-組合錯誤處理)
8. [自訂錯誤型別](#8-自訂錯誤型別)
9. [驗證](#9-驗證)
10. [最佳實踐](#10-最佳實踐)
11. [實作練習](#11-實作練習)

---

## 1. 錯誤處理概覽

### 1.1 錯誤處理方式比較

```scala
// ❌ 使用 null (不推薦)
def findUserBad(id: Int): String = {
  if (id > 0) "User" + id
  else null  // 危險!
}

val user = findUserBad(-1)
// user.length  // NullPointerException!

// ❌ 拋出異常 (不函數式)
def findUserException(id: Int): String = {
  if (id > 0) "User" + id
  else throw new IllegalArgumentException("Invalid ID")
}

// ✅ 使用 Option (推薦)
def findUserOption(id: Int): Option[String] = {
  if (id > 0) Some("User" + id)
  else None
}

// ✅ 使用 Either (更多資訊)
def findUserEither(id: Int): Either[String, String] = {
  if (id > 0) Right("User" + id)
  else Left("Invalid ID: must be positive")
}

// ✅ 使用 Try (處理異常)
import scala.util.{Try, Success, Failure}

def findUserTry(id: Int): Try[String] = Try {
  if (id > 0) "User" + id
  else throw new IllegalArgumentException("Invalid ID")
}
```

### 1.2 選擇指南

```scala
// Option: 值可能存在或不存在
// - 查找操作
// - 可選參數
// - 不需要錯誤詳情

// Either: 需要錯誤資訊
// - 驗證
// - 業務邏輯錯誤
// - 需要錯誤訊息

// Try: 包裝可能拋出異常的程式碼
// - 呼叫 Java 程式碼
// - I/O 操作
// - 解析操作
```

---

## 2. Option 型別

### 2.1 基本用法

```scala
// 建立 Option
val some: Option[Int] = Some(42)
val none: Option[Int] = None

// 從可能為 null 的值建立
val maybeNull: String = null
val opt: Option[String] = Option(maybeNull)  // None

val notNull: String = "hello"
val opt2: Option[String] = Option(notNull)   // Some("hello")

// 取值
some.get           // 42 - 危險!可能拋出異常
none.get           // NoSuchElementException!

// 安全取值
some.getOrElse(0)  // 42
none.getOrElse(0)  // 0

// 檢查
some.isDefined     // true
some.isEmpty       // false
none.isDefined     // false
none.isEmpty       // true
```

### 2.2 Option 操作

```scala
val maybeNumber: Option[Int] = Some(42)

// map - 轉換值
maybeNumber.map(_ * 2)           // Some(84)
None.map((x: Int) => x * 2)      // None

// flatMap - 鏈式操作
def divide(a: Int, b: Int): Option[Int] = {
  if (b != 0) Some(a / b) else None
}

val result = maybeNumber.flatMap(n => divide(n, 2))  // Some(21)
val result2 = maybeNumber.flatMap(n => divide(n, 0)) // None

// filter - 過濾
maybeNumber.filter(_ > 40)       // Some(42)
maybeNumber.filter(_ > 50)       // None

// foreach - 有值時執行
maybeNumber.foreach(n => println(s"Value: $n"))
None.foreach((n: Int) => println(s"Value: $n"))  // 不執行

// fold - 提供預設行為
maybeNumber.fold(0)(_ * 2)       // 84
None.fold(0)((x: Int) => x * 2)  // 0

// orElse - 提供替代 Option
maybeNumber.orElse(Some(0))      // Some(42)
None.orElse(Some(0))             // Some(0)
```

### 2.3 模式比對

```scala
def describe(opt: Option[Int]): String = opt match {
  case Some(value) => s"有值: $value"
  case None => "無值"
}

describe(Some(42))  // "有值: 42"
describe(None)      // "無值"

// for 推導式
val opt1 = Some(10)
val opt2 = Some(20)

val result = for {
  a <- opt1
  b <- opt2
} yield a + b

println(result)  // Some(30)

// 短路行為
val result2 = for {
  a <- opt1
  b <- None
  c <- opt2
} yield a + b + c

println(result2)  // None
```

### 2.4 實用範例

```scala
// 集合操作
val numbers = List(1, 2, 3, 4, 5)

// find 返回 Option
val firstEven = numbers.find(_ % 2 == 0)  // Some(2)
val firstNegative = numbers.find(_ < 0)   // None

// headOption, lastOption
List(1, 2, 3).headOption     // Some(1)
List.empty[Int].headOption   // None

// Map 查找
val scores = Map("Alice" -> 95, "Bob" -> 87)

scores.get("Alice")          // Some(95)
scores.get("Charlie")        // None

// 安全的字串轉換
def toInt(s: String): Option[Int] = {
  try {
    Some(s.toInt)
  } catch {
    case _: NumberFormatException => None
  }
}

toInt("42")   // Some(42)
toInt("abc")  // None

// 或使用 Scala 提供的方法
"42".toIntOption    // Some(42)
"abc".toIntOption   // None

// 鏈式查找
case class Address(city: String)
case class Person(name: String, address: Option[Address])
case class Company(name: String, ceo: Option[Person])

def getCeoCity(company: Company): Option[String] = {
  company.ceo.flatMap(_.address).map(_.city)
}

val company1 = Company("TechCorp", Some(Person("Alice", Some(Address("Taipei")))))
val company2 = Company("StartUp", Some(Person("Bob", None)))
val company3 = Company("SmallCo", None)

getCeoCity(company1)  // Some("Taipei")
getCeoCity(company2)  // None
getCeoCity(company3)  // None
```

---

## 3. Either 型別

### 3.1 基本用法

```scala
// Either[L, R] - Left 表示錯誤, Right 表示成功
// 慣例: Left 是錯誤, Right 是正確值

def divide(a: Int, b: Int): Either[String, Int] = {
  if (b == 0) Left("除數不能為零")
  else Right(a / b)
}

val result1 = divide(10, 2)   // Right(5)
val result2 = divide(10, 0)   // Left("除數不能為零")

// 檢查
result1.isRight   // true
result1.isLeft    // false
result2.isRight   // false
result2.isLeft    // true

// 提供明確的失敗預設值
result1.getOrElse(0) // 5

// 模式比對
result1 match {
  case Right(value) => println(s"成功: $value")
  case Left(error) => println(s"錯誤: $error")
}
```

### 3.2 Either 操作

```scala
val right: Either[String, Int] = Right(42)
val left: Either[String, Int] = Left("錯誤")

// map - 只作用於 Right
right.map(_ * 2)     // Right(84)
left.map(_ * 2)      // Left("錯誤")

// flatMap - 鏈式操作
def increment(n: Int): Either[String, Int] = {
  if (n < 100) Right(n + 1)
  else Left("數字太大")
}

right.flatMap(increment)  // Right(43)
left.flatMap(increment)   // Left("錯誤")

// fold - 處理兩種情況
val result = right.fold(
  error => s"失敗: $error",
  value => s"成功: $value"
)
// "成功: 42"

// getOrElse
right.getOrElse(0)   // 42
left.getOrElse(0)    // 0

// orElse
right.orElse(Right(0))   // Right(42)
left.orElse(Right(0))    // Right(0)

// swap - 交換 Left 和 Right
right.swap   // Left(42)
left.swap    // Right("錯誤")
```

### 3.3 for 推導式

```scala
def validateAge(age: Int): Either[String, Int] = {
  if (age < 0) Left("年齡不能為負數")
  else if (age > 150) Left("年齡不合理")
  else Right(age)
}

def validateName(name: String): Either[String, String] = {
  if (name.isEmpty) Left("名稱不能為空")
  else if (name.length < 2) Left("名稱太短")
  else Right(name)
}

// 使用 for 推導式組合
def createPerson(name: String, age: Int): Either[String, (String, Int)] = {
  for {
    validName <- validateName(name)
    validAge <- validateAge(age)
  } yield (validName, validAge)
}

createPerson("Alice", 25)   // Right((Alice,25))
createPerson("", 25)        // Left("名稱不能為空")
createPerson("Alice", -5)   // Left("年齡不能為負數")
createPerson("A", 200)      // Left("名稱太短") - 短路
```

### 3.4 Either vs Option

```scala
// Option: 只知道有或無
def findUser(id: Int): Option[String] = {
  if (id > 0) Some(s"User$id")
  else None  // 不知道為什麼失敗
}

// Either: 知道失敗原因
def findUserWithReason(id: Int): Either[String, String] = {
  if (id <= 0) Left("ID 必須是正數")
  else if (id > 1000) Left("ID 超出範圍")
  else Right(s"User$id")
}

// 轉換
val opt: Option[String] = Some("value")
opt.toRight("預設錯誤")      // Right("value")

val none: Option[String] = None
none.toRight("無值")         // Left("無值")

val either: Either[String, Int] = Right(42)
either.toOption              // Some(42)

val leftEither: Either[String, Int] = Left("error")
leftEither.toOption          // None
```

---

## 4. Try 型別

### 4.1 基本用法

```scala
import scala.util.{Try, Success, Failure}

// 包裝可能拋出異常的程式碼
val result1: Try[Int] = Try {
  "42".toInt
}
// Success(42)

val result2: Try[Int] = Try {
  "abc".toInt
}
// Failure(java.lang.NumberFormatException)

// 直接建立
val success: Try[Int] = Success(42)
val failure: Try[Int] = Failure(new Exception("錯誤"))

// 檢查
result1.isSuccess   // true
result1.isFailure   // false
result2.isSuccess   // false
result2.isFailure   // true

// 取值
result1.get         // 42 - 不安全!
result1.getOrElse(0)  // 42
result2.getOrElse(0)  // 0
```

### 4.2 Try 操作

```scala
val tryValue: Try[Int] = Try("42".toInt)

// map - 轉換成功值
tryValue.map(_ * 2)  // Success(84)

// flatMap - 鏈式操作
def divide(a: Int, b: Int): Try[Int] = Try {
  if (b == 0) throw new ArithmeticException("除以零")
  a / b
}

tryValue.flatMap(n => divide(n, 2))  // Success(21)
tryValue.flatMap(n => divide(n, 0))  // Failure(ArithmeticException)

// filter - 過濾
tryValue.filter(_ > 40)  // Success(42)
tryValue.filter(_ > 50)  // Failure(NoSuchElementException)

// recover - 從錯誤中恢復
val failed: Try[Int] = Failure(new Exception("錯誤"))
failed.recover {
  case _: Exception => 0
}
// Success(0)

// recoverWith - 返回另一個 Try
failed.recoverWith {
  case _: Exception => Try(42)
}
// Success(42)

// fold - 處理兩種情況
tryValue.fold(
  ex => s"失敗: ${ex.getMessage}",
  value => s"成功: $value"
)
// "成功: 42"

// toOption, toEither
tryValue.toOption  // Some(42)
tryValue.toEither  // Right(42)

val failed2: Try[Int] = Failure(new Exception("error"))
failed2.toOption   // None
failed2.toEither   // Left(java.lang.Exception: error)
```

### 4.3 實用範例

```scala
// 安全的檔案讀取
import scala.io.Source
import java.io.FileNotFoundException

def readFile(filename: String): Try[String] = Try {
  val source = Source.fromFile(filename)
  try source.mkString finally source.close()
}

readFile("data.txt") match {
  case Success(content) => println(s"檔案內容: $content")
  case Failure(ex: FileNotFoundException) => println("檔案不存在")
  case Failure(ex) => println(s"讀取錯誤: ${ex.getMessage}")
}

// 安全的 JSON 解析 (假設使用某個 JSON 函式庫)
def parseJson(json: String): Try[Map[String, Any]] = Try {
  // 假設的解析邏輯
  if (json.isEmpty) throw new IllegalArgumentException("空 JSON")
  Map("result" -> "parsed")
}

// 鏈式操作
def processData(filename: String): Try[String] = {
  for {
    content <- readFile(filename)
    data <- parseJson(content)
  } yield s"處理完成: $data"
}

// HTTP 請求
def httpGet(url: String): Try[String] = Try {
  scala.io.Source.fromURL(url).mkString
}

// 重試邏輯
def retry[T](n: Int)(fn: => T): Try[T] = {
  Try(fn) recoverWith {
    case _ if n > 1 => retry(n - 1)(fn)
  }
}

retry(3) {
  // 可能失敗的操作
  if (scala.util.Random.nextBoolean()) "成功"
  else throw new Exception("失敗")
}
```

---

## 5. 異常處理

### 5.1 try-catch-finally

```scala
// 基本語法
try {
  val result = 10 / 0
  println(result)
} catch {
  case e: ArithmeticException => println("除以零")
  case e: Exception => println(s"其他錯誤: ${e.getMessage}")
} finally {
  println("清理資源")
}

// 返回值
val result = try {
  "42".toInt
} catch {
  case _: NumberFormatException => 0
}
// result = 42

// 多個異常
def processFile(filename: String): String = {
  try {
    val source = Source.fromFile(filename)
    try {
      source.mkString
    } finally {
      source.close()
    }
  } catch {
    case _: FileNotFoundException => "檔案不存在"
    case _: IOException => "讀取錯誤"
    case e: Exception => s"未知錯誤: ${e.getMessage}"
  }
}
```

### 5.2 自訂異常

```scala
// 定義異常
class InvalidAgeException(message: String) extends Exception(message)
class InvalidNameException(message: String) extends Exception(message)

// 使用
def validatePerson(name: String, age: Int): Unit = {
  if (name.isEmpty) {
    throw new InvalidNameException("名稱不能為空")
  }
  if (age < 0 || age > 150) {
    throw new InvalidAgeException(s"年齡不合理: $age")
  }
}

try {
  validatePerson("", 25)
} catch {
  case e: InvalidNameException => println(e.getMessage)
  case e: InvalidAgeException => println(e.getMessage)
}
```

### 5.3 使用 Try 替代 try-catch

```scala
// ❌ 傳統方式
def parseIntOld(s: String): Int = {
  try {
    s.toInt
  } catch {
    case _: NumberFormatException => 0
  }
}

// ✅ 使用 Try (更函數式)
def parseIntNew(s: String): Try[Int] = Try(s.toInt)

// 使用
parseIntNew("42") match {
  case Success(n) => println(s"數字: $n")
  case Failure(ex) => println(s"錯誤: ${ex.getMessage}")
}

// 或使用 getOrElse
val number = parseIntNew("42").getOrElse(0)
```

---

## 6. 錯誤處理模式

### 6.1 鏈式處理

```scala
// Option 鏈
def getUser(id: Int): Option[String] = 
  if (id > 0) Some(s"User$id") else None

def getEmail(user: String): Option[String] = 
  if (user.nonEmpty) Some(s"$user@example.com") else None

def sendEmail(email: String): Option[String] = 
  if (email.contains("@")) Some(s"已發送到 $email") else None

// 組合
val result = for {
  user <- getUser(1)
  email <- getEmail(user)
  message <- sendEmail(email)
} yield message

println(result)  // Some("已發送到 User1@example.com")

// Either 鏈
def validateUserId(id: Int): Either[String, Int] = 
  if (id > 0) Right(id) else Left("ID 無效")

def validateUserExists(id: Int): Either[String, String] = 
  if (id <= 100) Right(s"User$id") else Left("用戶不存在")

def validateUserActive(user: String): Either[String, String] = 
  if (!user.endsWith("0")) Right(user) else Left("用戶未啟用")

// 組合
val result2 = for {
  id <- validateUserId(50)
  user <- validateUserExists(id)
  activeUser <- validateUserActive(user)
} yield activeUser

println(result2)  // Right("User50")
```

### 6.2 累積錯誤

```scala
// 使用 Either 只能得到第一個錯誤
// 要累積所有錯誤,可以使用自訂型別

type ValidationResult[T] = Either[List[String], T]

def validateName(name: String): ValidationResult[String] = {
  if (name.isEmpty) Left(List("名稱不能為空"))
  else if (name.length < 2) Left(List("名稱太短"))
  else Right(name)
}

def validateAge(age: Int): ValidationResult[Int] = {
  if (age < 0) Left(List("年齡不能為負數"))
  else if (age > 150) Left(List("年齡太大"))
  else Right(age)
}

def validateEmail(email: String): ValidationResult[String] = {
  if (!email.contains("@")) Left(List("Email 格式錯誤"))
  else Right(email)
}

// 手動累積錯誤
case class Person(name: String, age: Int, email: String)

def createPerson(name: String, age: Int, email: String): ValidationResult[Person] = {
  val nameResult = validateName(name)
  val ageResult = validateAge(age)
  val emailResult = validateEmail(email)
  
  (nameResult, ageResult, emailResult) match {
    case (Right(n), Right(a), Right(e)) => 
      Right(Person(n, a, e))
    case _ =>
      val errors = List(nameResult, ageResult, emailResult).collect {
        case Left(errs) => errs
      }.flatten
      Left(errors)
  }
}

createPerson("", -5, "invalid")
// Left(List("名稱不能為空", "年齡不能為負數", "Email 格式錯誤"))
```

### 6.3 優雅降級

```scala
// 提供多層回退方案
def getPriceFromPrimaryDB(id: Int): Option[Double] = {
  // 可能失敗
  None
}

def getPriceFromCache(id: Int): Option[Double] = {
  Some(99.99)
}

def getDefaultPrice(id: Int): Double = {
  0.0
}

def getPrice(id: Int): Double = {
  getPriceFromPrimaryDB(id)
    .orElse(getPriceFromCache(id))
    .getOrElse(getDefaultPrice(id))
}

println(getPrice(1))  // 99.99 (從快取取得)
```

---

## 7. 組合錯誤處理

### 7.1 Option, Either, Try 互轉

```scala
// Option → Either
val opt: Option[Int] = Some(42)
opt.toRight("無值")  // Right(42)

val none: Option[Int] = None
none.toRight("無值")  // Left("無值")

// Option → Try
opt.fold(
  Failure(new NoSuchElementException("無值")): Try[Int]
)(Success(_))

// Either → Option
val either: Either[String, Int] = Right(42)
either.toOption  // Some(42)

val left: Either[String, Int] = Left("錯誤")
left.toOption  // None

// Either → Try
either.fold(
  error => Failure(new Exception(error)),
  Success(_)
)

// Try → Option
val tryValue: Try[Int] = Success(42)
tryValue.toOption  // Some(42)

val failure: Try[Int] = Failure(new Exception("錯誤"))
failure.toOption  // None

// Try → Either
tryValue.toEither  // Right(42)
failure.toEither   // Left(Exception)
```

### 7.2 混合使用

```scala
import scala.util.{Try, Success, Failure}

// 實際場景:用戶註冊
case class User(id: Int, name: String, email: String)

// 不同的驗證返回不同型別
def findExistingUser(email: String): Option[User] = {
  // 查找數據庫
  None
}

def validateEmail(email: String): Either[String, String] = {
  if (email.contains("@")) Right(email)
  else Left("Email 格式錯誤")
}

def saveUser(user: User): Try[User] = Try {
  // 可能拋出異常的數據庫操作
  user
}

// 組合不同的錯誤處理型別
def registerUser(name: String, email: String): Either[String, User] = {
  for {
    validEmail <- validateEmail(email)
    _ <- findExistingUser(validEmail) match {
      case Some(_) => Left("Email 已被使用")
      case None => Right(())
    }
    user = User(1, name, validEmail)
    savedUser <- saveUser(user).toEither.left.map(_.getMessage)
  } yield savedUser
}

registerUser("Alice", "alice@example.com") match {
  case Right(user) => println(s"註冊成功: $user")
  case Left(error) => println(s"註冊失敗: $error")
}
```

---

## 8. 自訂錯誤型別

### 8.1 ADT 錯誤型別

```scala
// 使用 sealed trait 定義錯誤
sealed trait UserError
case class UserNotFound(id: Int) extends UserError
case class InvalidEmail(email: String) extends UserError
case class DuplicateUser(email: String) extends UserError
case class DatabaseError(message: String) extends UserError

// 使用
def findUser(id: Int): Either[UserError, String] = {
  if (id <= 0) Left(UserNotFound(id))
  else Right(s"User$id")
}

def validateUserEmail(email: String): Either[UserError, String] = {
  if (!email.contains("@")) Left(InvalidEmail(email))
  else Right(email)
}

// 處理
def processUser(id: Int): String = {
  findUser(id) match {
    case Right(user) => s"找到用戶: $user"
    case Left(UserNotFound(id)) => s"用戶 $id 不存在"
    case Left(InvalidEmail(email)) => s"Email $email 無效"
    case Left(DuplicateUser(email)) => s"Email $email 已被使用"
    case Left(DatabaseError(msg)) => s"數據庫錯誤: $msg"
  }
}
```

### 8.2 錯誤層次結構

```scala
// 分層錯誤
sealed trait AppError
sealed trait ValidationError extends AppError
sealed trait DatabaseError extends AppError
sealed trait NetworkError extends AppError

case class InvalidInput(field: String, message: String) extends ValidationError
case class MissingField(field: String) extends ValidationError

case class RecordNotFound(id: Int) extends DatabaseError
case class ConnectionFailed(message: String) extends DatabaseError

case class Timeout(seconds: Int) extends NetworkError
case class ServerError(code: Int) extends NetworkError

// 使用
def validateInput(input: String): Either[ValidationError, String] = {
  if (input.isEmpty) Left(MissingField("input"))
  else if (input.length < 3) Left(InvalidInput("input", "太短"))
  else Right(input)
}

def saveToDatabase(data: String): Either[DatabaseError, Unit] = {
  // 模擬數據庫操作
  Right(())
}

def processRequest(input: String): Either[AppError, String] = {
  for {
    valid <- validateInput(input)
    _ <- saveToDatabase(valid)
  } yield s"處理完成: $valid"
}

// 集中錯誤處理
def handleError(error: AppError): String = error match {
  case InvalidInput(field, msg) => s"欄位 $field 無效: $msg"
  case MissingField(field) => s"缺少欄位: $field"
  case RecordNotFound(id) => s"記錄 $id 不存在"
  case ConnectionFailed(msg) => s"連線失敗: $msg"
  case Timeout(sec) => s"逾時 ($sec 秒)"
  case ServerError(code) => s"伺服器錯誤: $code"
}
```

---

## 9. 驗證

### 9.1 簡單驗證

```scala
case class User(name: String, age: Int, email: String)

object UserValidator {
  type ValidationResult[T] = Either[List[String], T]
  
  private def validateName(name: String): ValidationResult[String] = {
    if (name.isEmpty) Left(List("名稱不能為空"))
    else if (name.length < 2) Left(List("名稱至少 2 個字元"))
    else if (name.length > 50) Left(List("名稱最多 50 個字元"))
    else Right(name)
  }
  
  private def validateAge(age: Int): ValidationResult[Int] = {
    if (age < 0) Left(List("年齡不能為負數"))
    else if (age > 150) Left(List("年齡不能超過 150"))
    else Right(age)
  }
  
  private def validateEmail(email: String): ValidationResult[String] = {
    val emailRegex = """^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""".r
    if (!emailRegex.matches(email)) Left(List("Email 格式錯誤"))
    else Right(email)
  }
  
  def validate(name: String, age: Int, email: String): ValidationResult[User] = {
    val nameResult = validateName(name)
    val ageResult = validateAge(age)
    val emailResult = validateEmail(email)
    
    (nameResult, ageResult, emailResult) match {
      case (Right(n), Right(a), Right(e)) => Right(User(n, a, e))
      case _ =>
        val errors = List(nameResult, ageResult, emailResult).collect {
          case Left(errs) => errs
        }.flatten
        Left(errors)
    }
  }
}

// 測試
UserValidator.validate("Alice", 25, "alice@example.com") match {
  case Right(user) => println(s"有效用戶: $user")
  case Left(errors) => println(s"驗證失敗:\n${errors.mkString("\n")}")
}

UserValidator.validate("", -5, "invalid") match {
  case Right(user) => println(s"有效用戶: $user")
  case Left(errors) => println(s"驗證失敗:\n${errors.mkString("\n")}")
}
// 驗證失敗:
// 名稱不能為空
// 年齡不能為負數
// Email 格式錯誤
```

### 9.2 組合驗證器

```scala
// 驗證器型別
type Validator[T] = T => Either[List[String], T]

// 基本驗證器
def nonEmpty(fieldName: String): Validator[String] = { value =>
  if (value.isEmpty) Left(List(s"$fieldName 不能為空"))
  else Right(value)
}

def minLength(fieldName: String, min: Int): Validator[String] = { value =>
  if (value.length < min) Left(List(s"$fieldName 至少 $min 個字元"))
  else Right(value)
}

def maxLength(fieldName: String, max: Int): Validator[String] = { value =>
  if (value.length > max) Left(List(s"$fieldName 最多 $max 個字元"))
  else Right(value)
}

def range(fieldName: String, min: Int, max: Int): Validator[Int] = { value =>
  if (value < min || value > max) Left(List(s"$fieldName 必須在 $min 到 $max 之間"))
  else Right(value)
}

// 組合驗證器
def combine[T](validators: Validator[T]*): Validator[T] = { value =>
  val results = validators.map(_(value))
  val errors = results.collect { case Left(errs) => errs }.flatten.toList
  
  if (errors.isEmpty) Right(value)
  else Left(errors)
}

// 使用
val nameValidator = combine(
  nonEmpty("名稱"),
  minLength("名稱", 2),
  maxLength("名稱", 50)
)

val ageValidator = range("年齡", 0, 150)

nameValidator("Alice")  // Right("Alice")
nameValidator("")       // Left(List("名稱不能為空", "名稱至少 2 個字元"))
ageValidator(25)        // Right(25)
ageValidator(200)       // Left(List("年齡必須在 0 到 150 之間"))
```

---

## 10. 最佳實踐

### 10.1 何時使用哪種錯誤處理

```scala
// ✅ 使用 Option: 值可能不存在,但不是錯誤
def findFirst[T](list: List[T], predicate: T => Boolean): Option[T] = {
  list.find(predicate)
}

// ✅ 使用 Either: 需要錯誤資訊
def divide(a: Int, b: Int): Either[String, Int] = {
  if (b == 0) Left("除數不能為零")
  else Right(a / b)
}

// ✅ 使用 Try: 包裝可能拋出異常的程式碼
def parseJson(json: String): Try[Map[String, Any]] = Try {
  // 解析邏輯,可能拋出異常
  Map("parsed" -> true)
}

// ❌ 避免:返回 null
def badFind(id: Int): String = {
  if (id > 0) s"User$id" else null
}

// ❌ 避免:拋出業務邏輯異常
def badValidate(age: Int): Int = {
  if (age < 0) throw new IllegalArgumentException("年齡無效")
  age
}
```

### 10.2 錯誤處理原則

```scala
// 1. 優先使用 Option/Either/Try
// 2. 不要吞掉異常
// 3. 提供有意義的錯誤訊息
// 4. 在邊界處理錯誤
// 5. 使用型別表達可能的錯誤

// ❌ 不好
def process(data: String): String = {
  try {
    // 複雜處理
    data.toUpperCase
  } catch {
    case _: Exception => ""  // 吞掉異常!
  }
}

// ✅ 好
def processGood(data: String): Either[String, String] = {
  Try(data.toUpperCase).toEither.left.map(_.getMessage)
}

// ✅ 在邊界處理
class UserService {
  // 內部使用 Either
  private def validateUser(name: String): Either[String, String] = {
    if (name.nonEmpty) Right(name) else Left("名稱無效")
  }
  
  // 公開 API 可以拋出異常或返回 Try
  def createUser(name: String): Try[String] = {
    validateUser(name) match {
      case Right(validName) => Success(s"User: $validName")
      case Left(error) => Failure(new IllegalArgumentException(error))
    }
  }
}
```

### 10.3 避免過度嵌套

```scala
// ❌ 不好:深層嵌套
def badProcess(id: Int): Option[String] = {
  getUser(id) match {
    case Some(user) =>
      getEmail(user) match {
        case Some(email) =>
          sendEmail(email) match {
            case Some(result) => Some(result)
            case None => None
          }
        case None => None
      }
    case None => None
  }
}

// ✅ 好:使用 for 推導式
def goodProcess(id: Int): Option[String] = {
  for {
    user <- getUser(id)
    email <- getEmail(user)
    result <- sendEmail(email)
  } yield result
}

// ✅ 好:使用 flatMap 鏈
def goodProcess2(id: Int): Option[String] = {
  getUser(id)
    .flatMap(getEmail)
    .flatMap(sendEmail)
}

def getUser(id: Int): Option[String] = Some(s"User$id")
def getEmail(user: String): Option[String] = Some(s"$user@example.com")
def sendEmail(email: String): Option[String] = Some(s"Sent to $email")
```

---

## 11. 實作練習

### 練習 1: 設定檔解析器

```scala
import scala.util.{Try, Success, Failure}
import scala.io.Source

case class Config(
  host: String,
  port: Int,
  timeout: Int,
  debug: Boolean
)

object ConfigParser {
  sealed trait ConfigError
  case class FileNotFound(filename: String) extends ConfigError
  case class ParseError(line: Int, message: String) extends ConfigError
  case class ValidationError(field: String, message: String) extends ConfigError
  case class MissingField(field: String) extends ConfigError
  
  type Result[T] = Either[ConfigError, T]
  
  def parseFile(filename: String): Result[Map[String, String]] = {
    Try {
      val source = Source.fromFile(filename)
      try {
        source.getLines().zipWithIndex.foldLeft(Map.empty[String, String]) {
          case (acc, (line, idx)) =>
            if (line.trim.isEmpty || line.trim.startsWith("#")) {
              acc
            } else {
              line.split("=", 2) match {
                case Array(key, value) => acc + (key.trim -> value.trim)
                case _ => throw new Exception(s"無效的行格式: $line")
              }
            }
        }
      } finally {
        source.close()
      }
    }.toEither.left.map {
      case _: java.io.FileNotFoundException => FileNotFound(filename)
      case ex => ParseError(0, ex.getMessage)
    }
  }
  
  def getString(map: Map[String, String], key: String): Result[String] = {
    map.get(key).toRight(MissingField(key))
  }
  
  def getInt(map: Map[String, String], key: String): Result[Int] = {
    for {
      value <- getString(map, key)
      int <- value.toIntOption.toRight(
        ValidationError(key, s"無法解析為整數: $value")
      )
    } yield int
  }
  
  def getBoolean(map: Map[String, String], key: String): Result[Boolean] = {
    for {
      value <- getString(map, key)
      bool <- value.toLowerCase match {
        case "true" | "yes" | "1" => Right(true)
        case "false" | "no" | "0" => Right(false)
        case _ => Left(ValidationError(key, s"無法解析為布林值: $value"))
      }
    } yield bool
  }
  
  def parseConfig(filename: String): Result[Config] = {
    for {
      map <- parseFile(filename)
      host <- getString(map, "host")
      port <- getInt(map, "port")
      timeout <- getInt(map, "timeout")
      debug <- getBoolean(map, "debug")
      
      // 驗證
      _ <- if (port > 0 && port < 65536) Right(())
           else Left(ValidationError("port", "端口必須在 1-65535 之間"))
      _ <- if (timeout > 0) Right(())
           else Left(ValidationError("timeout", "逾時必須大於 0"))
    } yield Config(host, port, timeout, debug)
  }
  
  def errorMessage(error: ConfigError): String = error match {
    case FileNotFound(filename) => s"檔案不存在: $filename"
    case ParseError(line, msg) => s"解析錯誤 (行 $line): $msg"
    case ValidationError(field, msg) => s"驗證錯誤 ($field): $msg"
    case MissingField(field) => s"缺少必要欄位: $field"
  }
  
  // 測試
  def main(args: Array[String]): Unit = {
    // 建立測試設定檔
    val configContent = """# Server Configuration
      |host = localhost
      |port = 8080
      |timeout = 30
      |debug = true
      |""".stripMargin
    
    val file = new java.io.PrintWriter("test-config.txt")
    try file.write(configContent) finally file.close()
    
    parseConfig("test-config.txt") match {
      case Right(config) => 
        println(s"設定載入成功: $config")
      case Left(error) => 
        println(s"設定載入失敗: ${errorMessage(error)}")
    }
    
    // 清理
    new java.io.File("test-config.txt").delete()
  }
}
```

### 練習 2: 用戶註冊系統

```scala
import java.util.UUID
import scala.util.matching.Regex

case class User(
  id: String,
  username: String,
  email: String,
  age: Int
)

object UserRegistration {
  // 錯誤型別
  sealed trait RegistrationError
  case class InvalidUsername(message: String) extends RegistrationError
  case class InvalidEmail(message: String) extends RegistrationError
  case class InvalidAge(message: String) extends RegistrationError
  case class DuplicateUsername(username: String) extends RegistrationError
  case class DuplicateEmail(email: String) extends RegistrationError
  case class DatabaseError(message: String) extends RegistrationError
  
  type Result[T] = Either[RegistrationError, T]
  
  // 模擬資料庫
  private var users = List.empty[User]
  
  // 驗證器
  object Validators {
    private val emailRegex: Regex = 
      """^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""".r
    
    def validateUsername(username: String): Result[String] = {
      if (username.isEmpty) 
        Left(InvalidUsername("用戶名不能為空"))
      else if (username.length < 3)
        Left(InvalidUsername("用戶名至少 3 個字元"))
      else if (username.length > 20)
        Left(InvalidUsername("用戶名最多 20 個字元"))
      else if (!username.matches("[a-zA-Z0-9_]+"))
        Left(InvalidUsername("用戶名只能包含字母、數字和底線"))
      else
        Right(username)
    }
    
    def validateEmail(email: String): Result[String] = {
      if (email.isEmpty)
        Left(InvalidEmail("Email 不能為空"))
      else if (!emailRegex.matches(email))
        Left(InvalidEmail("Email 格式錯誤"))
      else
        Right(email)
    }
    
    def validateAge(age: Int): Result[Int] = {
      if (age < 13)
        Left(InvalidAge("年齡必須至少 13 歲"))
      else if (age > 120)
        Left(InvalidAge("年齡不能超過 120 歲"))
      else
        Right(age)
    }
  }
  
  // 唯一性檢查
  def checkUsernameUnique(username: String): Result[String] = {
    if (users.exists(_.username == username))
      Left(DuplicateUsername(username))
    else
      Right(username)
  }
  
  def checkEmailUnique(email: String): Result[String] = {
    if (users.exists(_.email == email))
      Left(DuplicateEmail(email))
    else
      Right(email)
  }
  
  // 註冊
  def register(username: String, email: String, age: Int): Result[User] = {
    for {
      validUsername <- Validators.validateUsername(username)
      validEmail <- Validators.validateEmail(email)
      validAge <- Validators.validateAge(age)
      
      uniqueUsername <- checkUsernameUnique(validUsername)
      uniqueEmail <- checkEmailUnique(validEmail)
      
      user = User(UUID.randomUUID().toString, uniqueUsername, uniqueEmail, validAge)
      _ = users = users :+ user
    } yield user
  }
  
  // 列出所有用戶
  def listUsers(): List[User] = users
  
  // 查找用戶
  def findByUsername(username: String): Option[User] = {
    users.find(_.username == username)
  }
  
  // 錯誤訊息
  def errorMessage(error: RegistrationError): String = error match {
    case InvalidUsername(msg) => s"用戶名錯誤: $msg"
    case InvalidEmail(msg) => s"Email 錯誤: $msg"
    case InvalidAge(msg) => s"年齡錯誤: $msg"
    case DuplicateUsername(username) => s"用戶名 '$username' 已被使用"
    case DuplicateEmail(email) => s"Email '$email' 已被使用"
    case DatabaseError(msg) => s"資料庫錯誤: $msg"
  }
  
  // 測試
  def main(args: Array[String]): Unit = {
    println("=== 用戶註冊系統 ===\n")
    
    // 成功註冊
    register("alice", "alice@example.com", 25) match {
      case Right(user) => println(s"✓ 註冊成功: ${user.username}")
      case Left(error) => println(s"✗ ${errorMessage(error)}")
    }
    
    // 重複用戶名
    register("alice", "alice2@example.com", 30) match {
      case Right(user) => println(s"✓ 註冊成功: ${user.username}")
      case Left(error) => println(s"✗ ${errorMessage(error)}")
    }
    
    // 無效 Email
    register("bob", "invalid-email", 28) match {
      case Right(user) => println(s"✓ 註冊成功: ${user.username}")
      case Left(error) => println(s"✗ ${errorMessage(error)}")
    }
    
    // 年齡太小
    register("charlie", "charlie@example.com", 10) match {
      case Right(user) => println(s"✓ 註冊成功: ${user.username}")
      case Left(error) => println(s"✗ ${errorMessage(error)}")
    }
    
    // 成功註冊多個
    register("bob", "bob@example.com", 28)
    register("charlie", "charlie@example.com", 22)
    
    println("\n=== 所有用戶 ===")
    listUsers().foreach(u => println(s"${u.username} (${u.email}, ${u.age} 歲)"))
  }
}
```

### 練習 3: 安全的計算器

```scala
import scala.util.{Try, Success, Failure}

object SafeCalculator {
  sealed trait CalcError
  case object DivisionByZero extends CalcError
  case object InvalidNumber extends CalcError
  case class InvalidOperation(op: String) extends CalcError
  case class ParseError(message: String) extends CalcError
  
  type Result[T] = Either[CalcError, T]
  
  // 基本運算
  def add(a: Double, b: Double): Result[Double] = Right(a + b)
  
  def subtract(a: Double, b: Double): Result[Double] = Right(a - b)
  
  def multiply(a: Double, b: Double): Result[Double] = Right(a * b)
  
  def divide(a: Double, b: Double): Result[Double] = {
    if (b == 0) Left(DivisionByZero)
    else Right(a / b)
  }
  
  def power(base: Double, exp: Double): Result[Double] = {
    Try(math.pow(base, exp)).toOption
      .toRight(InvalidOperation("power"))
  }
  
  def squareRoot(n: Double): Result[Double] = {
    if (n < 0) Left(InvalidOperation("sqrt of negative"))
    else Right(math.sqrt(n))
  }
  
  // 解析數字
  def parseNumber(s: String): Result[Double] = {
    s.toDoubleOption.toRight(InvalidNumber)
  }
  
  // 執行操作
  def execute(op: String, a: Double, b: Double): Result[Double] = op match {
    case "+" => add(a, b)
    case "-" => subtract(a, b)
    case "*" => multiply(a, b)
    case "/" => divide(a, b)
    case "^" => power(a, b)
    case _ => Left(InvalidOperation(op))
  }
  
  // 解析並執行表達式 (簡單版本: "數字 運算符 數字")
  def evaluate(expr: String): Result[Double] = {
    val parts = expr.trim.split("\\s+")
    
    parts match {
      case Array(a, op, b) =>
        for {
          numA <- parseNumber(a)
          numB <- parseNumber(b)
          result <- execute(op, numA, numB)
        } yield result
      
      case Array("sqrt", n) =>
        for {
          num <- parseNumber(n)
          result <- squareRoot(num)
        } yield result
      
      case _ =>
        Left(ParseError(s"無法解析表達式: $expr"))
    }
  }
  
  // 錯誤訊息
  def errorMessage(error: CalcError): String = error match {
    case DivisionByZero => "錯誤: 除數不能為零"
    case InvalidNumber => "錯誤: 無效的數字"
    case InvalidOperation(op) => s"錯誤: 無效的運算 '$op'"
    case ParseError(msg) => s"解析錯誤: $msg"
  }
  
  // REPL
  def repl(): Unit = {
    println("簡易計算器")
    println("支援運算: + - * / ^ sqrt")
    println("範例: 10 + 5")
    println("      sqrt 16")
    println("輸入 'quit' 離開\n")
    
    var running = true
    
    while (running) {
      print("> ")
      val input = scala.io.StdIn.readLine()
      
      if (input != null) {
        input.trim.toLowerCase match {
          case "quit" | "exit" => running = false
          case expr if expr.nonEmpty =>
            evaluate(expr) match {
              case Right(result) => println(s"= $result")
              case Left(error) => println(errorMessage(error))
            }
          case _ => // 空行,忽略
        }
      }
    }
    
    println("再見!")
  }
  
  // 測試
  def main(args: Array[String]): Unit = {
    val tests = List(
      "10 + 5",
      "20 - 8",
      "6 * 7",
      "15 / 3",
      "2 ^ 8",
      "sqrt 16",
      "10 / 0",
      "abc + 5",
      "10 % 3"
    )
    
    println("=== 計算器測試 ===\n")
    tests.foreach { expr =>
      evaluate(expr) match {
        case Right(result) => println(f"$expr%-15s = $result%.2f")
        case Left(error) => println(f"$expr%-15s → ${errorMessage(error)}")
      }
    }
    
    println("\n啟動 REPL 模式...")
    repl()
  }
}
```

### 練習 4: 資料驗證框架

```scala
object ValidationFramework {
  // 驗證結果
  sealed trait ValidationResult[+A]
  case class Valid[A](value: A) extends ValidationResult[A]
  case class Invalid(errors: List[String]) extends ValidationResult[Nothing]
  
  // 驗證器
  trait Validator[T] {
    def validate(value: T): ValidationResult[T]
    
    // 組合驗證器
    def and(other: Validator[T]): Validator[T] = { value =>
      (this.validate(value), other.validate(value)) match {
        case (Valid(v), Valid(_)) => Valid(v)
        case (Invalid(e1), Invalid(e2)) => Invalid(e1 ++ e2)
        case (Invalid(e), _) => Invalid(e)
        case (_, Invalid(e)) => Invalid(e)
      }
    }
    
    // 轉換
    def map[U](f: T => U): Validator[U] = { value =>
      this.validate(value) match {
        case Valid(v) => Valid(f(v))
        case Invalid(e) => Invalid(e)
      }
    }
  }
  
  // 基本驗證器
  object Validators {
    def nonEmpty(fieldName: String): Validator[String] = { value =>
      if (value.isEmpty) Invalid(List(s"$fieldName 不能為空"))
      else Valid(value)
    }
    
    def minLength(fieldName: String, min: Int): Validator[String] = { value =>
      if (value.length < min) 
        Invalid(List(s"$fieldName 至少需要 $min 個字元"))
      else Valid(value)
    }
    
    def maxLength(fieldName: String, max: Int): Validator[String] = { value =>
      if (value.length > max) 
        Invalid(List(s"$fieldName 最多 $max 個字元"))
      else Valid(value)
    }
    
    def matches(fieldName: String, regex: String): Validator[String] = { value =>
      if (!value.matches(regex)) 
        Invalid(List(s"$fieldName 格式不正確"))
      else Valid(value)
    }
    
    def min(fieldName: String, minValue: Int): Validator[Int] = { value =>
      if (value < minValue) 
        Invalid(List(s"$fieldName 必須至少為 $minValue"))
      else Valid(value)
    }
    
    def max(fieldName: String, maxValue: Int): Validator[Int] = { value =>
      if (value > maxValue) 
        Invalid(List(s"$fieldName 最多為 $maxValue"))
      else Valid(value)
    }
    
    def range(fieldName: String, minValue: Int, maxValue: Int): Validator[Int] = {
      min(fieldName, minValue).and(max(fieldName, maxValue))
    }
    
    def email(fieldName: String): Validator[String] = {
      matches(fieldName, """^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""")
    }
  }
  
  // 使用範例
  case class SignUpForm(
    username: String,
    email: String,
    password: String,
    age: Int
  )
  
  object SignUpFormValidator {
    import Validators.*
    
    val usernameValidator: Validator[String] = 
      nonEmpty("用戶名")
        .and(minLength("用戶名", 3))
        .and(maxLength("用戶名", 20))
        .and(matches("用戶名", "[a-zA-Z0-9_]+"))
    
    val emailValidator: Validator[String] = 
      nonEmpty("Email")
        .and(email("Email"))
    
    val passwordValidator: Validator[String] = 
      nonEmpty("密碼")
        .and(minLength("密碼", 8))
        .and(maxLength("密碼", 100))
    
    val ageValidator: Validator[Int] = 
      range("年齡", 13, 120)
    
    def validate(form: SignUpForm): ValidationResult[SignUpForm] = {
      val usernameResult = usernameValidator.validate(form.username)
      val emailResult = emailValidator.validate(form.email)
      val passwordResult = passwordValidator.validate(form.password)
      val ageResult = ageValidator.validate(form.age)
      
      (usernameResult, emailResult, passwordResult, ageResult) match {
        case (Valid(_), Valid(_), Valid(_), Valid(_)) => 
          Valid(form)
        case _ =>
          val errors = List(usernameResult, emailResult, passwordResult, ageResult)
            .collect { case Invalid(e) => e }
            .flatten
          Invalid(errors)
      }
    }
  }
  
  // 測試
  def main(args: Array[String]): Unit = {
    val validForm = SignUpForm("alice", "alice@example.com", "password123", 25)
    val invalidForm = SignUpForm("ab", "invalid", "123", 10)
    
    println("=== 有效表單 ===")
    SignUpFormValidator.validate(validForm) match {
      case Valid(form) => println(s"✓ 驗證通過: $form")
      case Invalid(errors) => 
        println("✗ 驗證失敗:")
        errors.foreach(e => println(s"  - $e"))
    }
    
    println("\n=== 無效表單 ===")
    SignUpFormValidator.validate(invalidForm) match {
      case Valid(form) => println(s"✓ 驗證通過: $form")
      case Invalid(errors) => 
        println("✗ 驗證失敗:")
        errors.foreach(e => println(s"  - $e"))
    }
  }
}
```

### 練習 5: API 客戶端

```scala
import scala.util.{Try, Success, Failure}

object ApiClient {
  // 錯誤型別
  sealed trait ApiError
  case class NetworkError(message: String) extends ApiError
  case class ParseError(message: String) extends ApiError
  case class ServerError(code: Int, message: String) extends ApiError
  case class ClientError(code: Int, message: String) extends ApiError
  case class Timeout(seconds: Int) extends ApiError
  
  type ApiResult[T] = Either[ApiError, T]
  
  // HTTP 回應
  case class Response(code: Int, body: String)
  
  // 模擬 HTTP 請求
  def httpGet(url: String): Try[Response] = Try {
    // 模擬網路延遲
    Thread.sleep(100)
    
    // 模擬不同的回應
    url match {
      case u if u.contains("users") => 
        Response(200, """{"users": [{"id": 1, "name": "Alice"}]}""")
      case u if u.contains("error") => 
        Response(500, """{"error": "Internal Server Error"}""")
      case u if u.contains("notfound") => 
        Response(404, """{"error": "Not Found"}""")
      case _ => 
        throw new java.net.ConnectException("Connection failed")
    }
  }
  
  // 處理回應
  def handleResponse(response: Response): ApiResult[String] = {
    response.code match {
      case code if code >= 200 && code < 300 => 
        Right(response.body)
      case code if code >= 400 && code < 500 => 
        Left(ClientError(code, response.body))
      case code if code >= 500 => 
        Left(ServerError(code, response.body))
      case code => 
        Left(ClientError(code, "Unknown status code"))
    }
  }
  
  // API 呼叫
  def get(url: String): ApiResult[String] = {
    httpGet(url) match {
      case Success(response) => handleResponse(response)
      case Failure(ex: java.net.ConnectException) => 
        Left(NetworkError(s"連線失敗: ${ex.getMessage}"))
      case Failure(ex: java.net.SocketTimeoutException) => 
        Left(Timeout(30))
      case Failure(ex) => 
        Left(NetworkError(ex.getMessage))
    }
  }
  
  // 重試機制
  def getWithRetry(url: String, maxRetries: Int = 3): ApiResult[String] = {
    def attempt(remaining: Int): ApiResult[String] = {
      get(url) match {
        case Right(result) => Right(result)
        case Left(NetworkError(_)) if remaining > 1 =>
          println(s"重試中... (剩餘 ${remaining - 1} 次)")
          Thread.sleep(1000)
          attempt(remaining - 1)
        case Left(error) => Left(error)
      }
    }
    attempt(maxRetries)
  }
  
  // 解析 JSON (簡化版)
  def parseJson(json: String): ApiResult[Map[String, Any]] = {
    Try {
      // 這裡應該使用真正的 JSON 函式庫
      // 簡單示範
      if (json.contains("users")) {
        Map("users" -> List(Map("id" -> 1, "name" -> "Alice")))
      } else {
        Map.empty[String, Any]
      }
    }.toEither.left.map(ex => ParseError(ex.getMessage))
  }
  
  // 完整流程
  def fetchUsers(baseUrl: String): ApiResult[List[String]] = {
    for {
      body <- getWithRetry(s"$baseUrl/users")
      data <- parseJson(body)
      users <- data.get("users") match {
        case Some(list: List[?]) => Right(list.map(_.toString))
        case _ => Left(ParseError("無法解析用戶列表"))
      }
    } yield users
  }
  
  // 錯誤處理
  def handleError(error: ApiError): String = error match {
    case NetworkError(msg) => s"網路錯誤: $msg"
    case ParseError(msg) => s"解析錯誤: $msg"
    case ServerError(code, msg) => s"伺服器錯誤 ($code): $msg"
    case ClientError(code, msg) => s"客戶端錯誤 ($code): $msg"
    case Timeout(sec) => s"請求逾時 ($sec 秒)"
  }
  
  // 測試
  def main(args: Array[String]): Unit = {
    println("=== API 客戶端測試 ===\n")
    
    val testUrls = List(
      ("成功請求", "http://api.example.com/users"),
      ("404 錯誤", "http://api.example.com/notfound"),
      ("伺服器錯誤", "http://api.example.com/error"),
      ("網路錯誤", "http://api.example.com/timeout")
    )
    
    testUrls.foreach { case (name, url) =>
      println(s"$name: $url")
      get(url) match {
        case Right(body) => println(s"  ✓ 成功: ${body.take(50)}...")
        case Left(error) => println(s"  ✗ ${handleError(error)}")
      }
      println()
    }
  }
}
```

---

## 12. 重點總結

### 錯誤處理型別
- **Option**: 值可能存在或不存在
- **Either**: 需要錯誤資訊 (Left=錯誤, Right=成功)
- **Try**: 包裝可能拋出異常的程式碼

### 關鍵操作
- **map**: 轉換成功值
- **flatMap**: 鏈式操作
- **fold**: 處理兩種情況
- **getOrElse**: 提供預設值
- **recover**: 從錯誤恢復

### 最佳實踐
- 優先使用 Option/Either/Try
- 不要吞掉異常
- 使用 for 推導式組合
- 在邊界處理錯誤
- 使用型別表達錯誤

### 選擇指南
- 查找操作 → Option
- 驗證邏輯 → Either
- 包裝異常 → Try
- 避免使用 null 和拋出業務異常

---

## 下一步

完成第七部分後,您已經掌握:
- ✅ Option、Either、Try 的使用
- ✅ 錯誤處理的最佳實踐
- ✅ 組合與轉換錯誤
- ✅ 自訂錯誤型別
- ✅ 驗證框架設計

**Scala 核心教學已完成!**

您已經學習了:
1. 基礎語法與型別
2. 函數式程式設計
3. 物件導向程式設計
4. 集合操作
5. 模式比對
6. 錯誤處理

建議下一步:
- 深入學習[進階主題](scala_part8_advanced_topics.md) (隱式轉換、型別系統)
- 實作完整的專案
- 學習 Scala 生態系統 (Akka、Play、Cats)

恭喜您完成 Scala 核心教學!

---

> [« 上一篇：模式比對](scala_part6_pattern_matching.md) | [📚 目錄](../README.md) | [下一篇：進階主題 »](scala_part8_advanced_topics.md)
