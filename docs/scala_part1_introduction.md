# Scala 教學 - 第一部分:Scala 簡介與環境設置

> [📚 目錄](../README.md) | [下一篇：基本語法 »](scala_part2_basic_syntax.md)

---

## 目錄
1. [什麼是 Scala?](#1-什麼是-scala)
2. [Scala 的特點與優勢](#2-scala-的特點與優勢)
3. [環境安裝](#3-環境安裝)
4. [開發工具選擇](#4-開發工具選擇)
5. [第一個 Scala 程式](#5-第一個-scala-程式)
6. [專案結構與建構工具](#6-專案結構與建構工具)
7. [REPL 互動式環境](#7-repl-互動式環境)

---

## 1. 什麼是 Scala?

Scala (Scalable Language) 是一個現代化的多範型程式語言,由瑞士洛桑聯邦理工學院的 Martin Odersky 教授於 2004 年設計。

### 1.1 核心理念

Scala 結合了兩種主要的程式設計典範:

**物件導向程式設計 (OOP)**
- 一切皆為物件
- 支援類別、繼承、多型
- 強大的型別系統

**函數式程式設計 (FP)**
- 函數是一等公民
- 不可變性
- 高階函數
- 模式比對

### 1.2 為什麼叫 "Scalable"?

Scala 的名稱來自 "Scalable Language",意味著它能夠:
- 從小型腳本擴展到大型系統
- 從簡單的原型到企業級應用
- 適應不同規模的開發需求

### 1.3 Scala 的應用領域

**大數據處理**
- Apache Spark (最知名的 Scala 應用)
- Apache Kafka
- Apache Flink

**後端開發**
- Play Framework (Web 框架)
- Akka (並行與分散式系統)
- Twitter、LinkedIn 等公司的後端服務

**金融科技**
- 高頻交易系統
- 風險管理系統

---

## 2. Scala 的特點與優勢

### 2.1 主要特點

#### 與 Java 完全互通
```scala
// 可以直接使用 Java 類別庫
import java.util.Date
import java.io.File

val now = new Date()
val file = new File("/path/to/file")
```

#### 靜態型別系統 + 型別推導
```scala
// 有型別推導,不需要明確指定型別
val name = "Alice"  // 自動推導為 String
val age = 25        // 自動推導為 Int

// 但仍可明確指定型別
val greeting: String = "Hello"
val count: Int = 10
```

#### 簡潔的語法
```scala
// Java 寫法
public class Person {
    private final String name;
    private int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() {
        return name;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
}

// Scala 寫法 (一行!)
case class Person(name: String, var age: Int)
```

#### 函數式程式設計支援
```scala
// 高階函數
val numbers = List(1, 2, 3, 4, 5)
val doubled = numbers.map(_ * 2)

// 不可變性
val immutableList = List(1, 2, 3)
// immutableList 永遠不會改變

// 模式比對
def describe(x: Any) = x match {
  case i: Int => s"整數: $i"
  case s: String => s"字串: $s"
  case _ => "其他"
}
```

### 2.2 優勢分析

**優點**

✅ **表達力強**: 相同功能的程式碼量通常是 Java 的 1/3 到 1/2

✅ **型別安全**: 編譯時期就能捕捉許多錯誤

✅ **並行處理**: 不可變性使得並行程式更容易撰寫

✅ **效能優秀**: 編譯為 JVM 字節碼,效能接近 Java

✅ **生態系統**: 可使用所有 Java 類別庫

**缺點**

❌ **學習曲線**: 比 Java 陡峭,特別是函數式程式設計概念

❌ **編譯速度**: 比 Java 慢

❌ **二進位相容性**: 不同版本之間可能有相容性問題

---

## 3. 環境安裝

### 3.1 前置需求

Scala 運行在 JVM 上,因此需要先安裝 Java。

**安裝 JDK**

檢查是否已安裝:
```bash
java -version
```

如果沒有安裝,建議使用 OpenJDK 11 或更高版本:

**macOS (使用 Homebrew)**
```bash
brew install openjdk@11
```

**Ubuntu/Debian**
```bash
sudo apt update
sudo apt install openjdk-11-jdk
```

**Windows**
從 [AdoptOpenJDK](https://adoptopenjdk.net/) 下載並安裝

### 3.2 安裝 Scala

#### 方式一: 使用 SDKMAN (替代方案)

SDKMAN 是一個 JVM 生態系統的版本管理工具,支援 macOS、Linux 和 Windows (WSL)。

**1. 安裝 SDKMAN**
```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
```

**2. 驗證安裝**
```bash
sdk version
```

**3. 安裝 Scala**
```bash
# 安裝本指南使用的版本
sdk install scala 3.3.8
```

**4. 安裝 sbt (Scala Build Tool)**
```bash
sdk install sbt
```

**5. 驗證安裝**
```bash
scala -version
sbt --version
```

#### 方式二: 使用 Coursier (推薦)

Coursier 是 Scala 官方推薦的安裝工具。

**macOS/Linux**
```bash
curl -fL https://github.com/coursier/launchers/raw/master/cs-x86_64-pc-linux.gz | gzip -d > cs
chmod +x cs
./cs setup
cs install scala:3.3.8
cs install scalac:3.3.8
```

**Windows**
從 [Coursier 官網](https://get-coursier.io/docs/cli-installation) 下載安裝程式

完成 setup 後，執行 `cs install scala:3.3.8` 與 `cs install scalac:3.3.8`，
確保執行器及編譯器與本指南一致。

#### 方式三: 手動安裝

從 [Scala 3.3.8 官方版本頁](https://www.scala-lang.org/download/3.3.8.html) 下載
Scala 3.3.8 binary。未指定版本的 Homebrew 或 Linux distribution package 可能安裝
不同 Scala 版本，因此重現本指南時不要使用它們。

### 3.3 驗證安裝

```bash
# 檢查 Java
java -version
# 應該看到類似: openjdk version "11.0.x"

# 檢查 Scala
scala -version
# 應該看到類似: Scala code runner version 3.3.8

# 檢查 sbt
sbt --version
# 應該看到類似: sbt version in this project: 1.9.7
```

---

## 4. 開發工具選擇

### 4.1 IntelliJ IDEA (最推薦)

**優點:**
- 專業的 Scala 支援
- 強大的除錯功能
- 自動完成和重構工具
- 整合 sbt

**安裝步驟:**

1. 下載 [IntelliJ IDEA](https://www.jetbrains.com/idea/download/)
   - Community Edition (免費)
   - Ultimate Edition (付費,但功能更完整)

2. 安裝 Scala 外掛
   - 開啟 IntelliJ IDEA
   - File → Settings (或 Preferences)
   - Plugins → Marketplace
   - 搜尋 "Scala"
   - 安裝 "Scala" 外掛
   - 重啟 IDE

3. 建立新專案
   - File → New → Project
   - 選擇 "Scala" 和 "sbt"
   - 設定專案名稱和位置
   - 選擇 JDK 和 Scala 版本

### 4.2 Visual Studio Code

**優點:**
- 輕量級
- 免費開源
- 跨平台

**安裝步驟:**

1. 下載 [VS Code](https://code.visualstudio.com/)

2. 安裝 Metals 擴充套件
   - 開啟 VS Code
   - 按 Ctrl+Shift+X (或 Cmd+Shift+X)
   - 搜尋 "Scala (Metals)"
   - 安裝

3. 可選:安裝其他實用擴充套件
   - "Scala Syntax (official)"
   - "GitLens"

### 4.3 其他選項

**Sublime Text + LSP**
- 輕量但功能較少

**Vim/Neovim + CoC**
- 適合喜歡終端環境的開發者

**Eclipse + Scala IDE**
- 較舊的選擇,不太推薦

---

## 5. 第一個 Scala 程式

### 5.1 使用 REPL

Scala REPL (Read-Eval-Print Loop) 是一個互動式環境,適合快速測試程式碼。

**啟動 REPL:**
```bash
scala
```

**基本操作:**
```scala
// 在 REPL 中輸入
scala> println("Hello, Scala!")
Hello, Scala!

scala> val x = 42
val x: Int = 42

scala> x * 2
val res0: Int = 84

scala> def greet(name: String) = s"Hello, $name!"
def greet(name: String): String

scala> greet("World")
val res1: String = Hello, World!

// 離開 REPL
scala> :quit
```

### 5.2 建立腳本檔案

**建立檔案 hello.scala:**
```scala
// hello.scala
object Hello {
  def main(args: Array[String]): Unit = {
    println("Hello, Scala!")
    println(s"你傳入了 ${args.length} 個參數")
    args.foreach(arg => println(s"  - $arg"))
  }
}
```

**執行腳本:**
```bash
scala hello.scala
# 輸出: Hello, Scala!
#      你傳入了 0 個參數

scala hello.scala Alice Bob
# 輸出: Hello, Scala!
#      你傳入了 2 個參數
#        - Alice
#        - Bob
```

### 5.3 使用 main 方法

```scala
// hello_app.scala
@main def helloApp(args: String*): Unit = {
  println("Hello from Scala 3!")
  println(s"參數: ${args.mkString(", ")}")
}
```

**執行:**
```bash
scala hello_app.scala arg1 arg2
```

### 5.4 編譯並執行

**編譯:**
```bash
scalac hello.scala
# 產生 Hello.class 和 Hello$.class
```

**執行編譯後的程式:**
```bash
scala Hello
# 或使用 Java
java Hello
```

---

## 6. 專案結構與建構工具

### 6.1 使用 sbt 建立專案

sbt (Scala Build Tool) 是 Scala 的標準建構工具。

**快速建立專案:**
```bash
# 使用官方模板
sbt new scala/scala-seed.g8
# 會詢問專案名稱,例如輸入: my-first-project
```

**產生的專案結構:**
```
my-first-project/
├── build.sbt              # 建構設定檔
├── project/
│   └── build.properties   # sbt 版本設定
└── src/
    ├── main/
    │   └── scala/
    │       └── example/
    │           └── Hello.scala
    └── test/
        └── scala/
            └── example/
                └── HelloSpec.scala
```

### 6.2 build.sbt 設定檔

**基本的 build.sbt:**
```scala
// build.sbt
name := "my-first-project"

version := "0.1.0"

scalaVersion := "3.3.8"

// 依賴項
libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.2.17",
  "org.scalatest" %% "scalatest" % "3.2.17" % "test"
)
```

**設定項說明:**
- `name`: 專案名稱
- `version`: 版本號
- `scalaVersion`: Scala 版本
- `libraryDependencies`: 外部依賴庫

### 6.3 常用 sbt 指令

**進入 sbt shell:**
```bash
sbt
```

**在 sbt shell 中:**
```sbt
# 編譯專案
compile

# 執行程式
run

# 執行測試
test

# 持續編譯 (檔案變更時自動重新編譯)
~compile

# 清除編譯結果
clean

# 重新載入 build.sbt
reload

# 顯示專案依賴
libraryDependencies

# 離開 sbt
exit
```

**直接執行 (不進入 shell):**
```bash
sbt compile
sbt run
sbt test
```

### 6.4 專案檔案說明

**src/main/scala/example/Hello.scala:**
```scala
package example

@main def hello(): Unit = {
  println("Hello, Scala!")
  
  def greet(name: String): String = {
    s"Hello, $name!"
  }
}
```

**專案結構最佳實踐:**
```
my-project/
├── build.sbt
├── project/
│   ├── build.properties
│   └── plugins.sbt         # sbt 外掛設定
├── src/
│   ├── main/
│   │   ├── scala/
│   │   │   └── com/
│   │   │       └── mycompany/
│   │   │           ├── Main.scala
│   │   │           ├── models/
│   │   │           ├── services/
│   │   │           └── utils/
│   │   └── resources/      # 設定檔、資源檔
│   └── test/
│       ├── scala/
│       │   └── com/
│       │       └── mycompany/
│       │           └── MainSpec.scala
│       └── resources/
├── target/                  # 編譯輸出 (自動產生)
└── README.md
```

### 6.5 加入外部依賴

**在 build.sbt 中加入:**
```scala
libraryDependencies ++= Seq(
  // JSON 處理
  "io.circe" %% "circe-core" % "0.14.6",
  "io.circe" %% "circe-generic" % "0.14.6",
  "io.circe" %% "circe-parser" % "0.14.6",
  
  // HTTP 客戶端
  "com.softwaremill.sttp.client3" %% "core" % "3.9.1",
  
  // 測試框架
  "org.scalatest" %% "scalatest" % "3.2.17" % Test
)
```

**依賴格式說明:**
```scala
"group-id" %% "artifact-id" % "version" % "configuration"

// %% 會自動加上 Scala 版本號
// 例如在 Scala 3.3.8 下:
"org.scalatest" %% "scalatest" % "3.2.17"
// 實際會下載: org.scalatest:scalatest_3:3.2.17

// % 則不會加上 Scala 版本號 (用於 Java 函式庫)
"mysql" % "mysql-connector-java" % "8.0.33"
```

---

## 7. REPL 互動式環境

### 7.1 REPL 基本使用

**啟動 REPL:**
```bash
scala
```

**基本操作範例:**
```scala
scala> 1 + 1
val res0: Int = 2

scala> res0 * 5
val res1: Int = 10

scala> val name = "Alice"
val name: String = Alice

scala> s"Hello, $name!"
val res2: String = Hello, Alice!
```

### 7.2 REPL 特殊指令

```scala
// 查看變數型別
scala> :type name
String

// 查看上一個結果的型別
scala> :type res2
String

// 載入外部檔案
scala> :load myfile.scala

// 顯示歷史指令
scala> :history

// 貼上多行程式碼模式
scala> :paste
// Entering paste mode (ctrl-D to finish)

def factorial(n: Int): Int = {
  if (n <= 1) 1
  else n * factorial(n - 1)
}

// 按 Ctrl+D 結束

scala> factorial(5)
val res3: Int = 120

// 重設 REPL (清除所有定義)
scala> :reset

// 離開 REPL
scala> :quit
```

### 7.3 使用 Scala CLI 載入函式庫

安裝 scala-cli:
```bash
curl -sSLf https://scala-cli.virtuslab.org/get | sh
```

使用:
```scala
// 建立檔案 script.sc
//> using scala "3.3.8"
//> using lib "com.lihaoyi::requests:0.8.0"

import requests.*

val response = get("https://api.github.com")
println(response.text())
```

執行:
```bash
scala-cli script.sc
```

### 7.4 REPL 實用技巧

**自動完成:**
```scala
scala> val list = List(1, 2, 3)
scala> list.ma<TAB>  // 按 Tab 鍵會顯示所有 ma 開頭的方法
```

**查看方法文件:**
```scala
scala> :help map
// 或在某些 REPL 中
scala> list.map _
```

**設定 REPL 環境:**

建立 `~/.scala/repl-config.scala`:
```scala
// 自動匯入常用套件
import scala.collection.mutable
import scala.util.{Try, Success, Failure}

// 設定自訂提示符
scala.tools.nsc.interpreter.replProps.prompt.set("scala>>> ")

// 自訂函數
def time[T](block: => T): T = {
  val start = System.nanoTime()
  val result = block
  val end = System.nanoTime()
  println(s"執行時間: ${(end - start) / 1000000.0} ms")
  result
}
```

---

## 8. 實作練習

### 練習 1: Hello World 變化

建立一個程式,根據當前時間問候使用者:

```scala
// TimeGreeting.scala
import java.time.LocalTime

@main def timeGreeting(): Unit = {
  val hour = LocalTime.now().getHour
  
  val greeting = hour match {
    case h if h >= 5 && h < 12 => "早安"
    case h if h >= 12 && h < 18 => "午安"
    case h if h >= 18 && h < 22 => "晚安"
    case _ => "夜深了"
  }
  
  println(s"$greeting,歡迎使用 Scala!")
}
```

### 練習 2: 簡單計算器

```scala
// Calculator.scala
@main def calculator(): Unit = {
  def calculate(a: Double, b: Double, op: String): Double = {
    op match {
      case "+" => a + b
      case "-" => a - b
      case "*" => a * b
      case "/" if b != 0 => a / b
      case "/" => throw new ArithmeticException("除數不能為零")
      case _ => throw new IllegalArgumentException("不支援的運算子")
    }
  }
  
  // 測試
  println(calculate(10, 5, "+"))  // 15.0
  println(calculate(10, 5, "-"))  // 5.0
  println(calculate(10, 5, "*"))  // 50.0
  println(calculate(10, 5, "/"))  // 2.0
}
```

### 練習 3: 使用 sbt 建立專案

1. 建立新專案
2. 加入 ScalaTest 依賴
3. 撰寫簡單的測試

```bash
# 建立專案
sbt new scala/scala-seed.g8
# 專案名稱: calculator-project
```

**修改 build.sbt:**
```scala
name := "calculator-project"
version := "0.1.0"
scalaVersion := "3.3.8"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17" % Test
```

**src/main/scala/Calculator.scala:**
```scala
object Calculator {
  def add(a: Int, b: Int): Int = a + b
  def subtract(a: Int, b: Int): Int = a - b
  def multiply(a: Int, b: Int): Int = a * b
  def divide(a: Int, b: Int): Option[Int] = {
    if (b != 0) Some(a / b) else None
  }
}
```

**src/test/scala/CalculatorSpec.scala:**
```scala
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CalculatorSpec extends AnyFlatSpec with Matchers {
  "Calculator" should "add two numbers correctly" in {
    Calculator.add(2, 3) shouldBe 5
  }
  
  it should "subtract two numbers correctly" in {
    Calculator.subtract(5, 3) shouldBe 2
  }
  
  it should "multiply two numbers correctly" in {
    Calculator.multiply(3, 4) shouldBe 12
  }
  
  it should "divide two numbers correctly" in {
    Calculator.divide(10, 2) shouldBe Some(5)
  }
  
  it should "handle division by zero" in {
    Calculator.divide(10, 0) shouldBe None
  }
}
```

**執行測試:**
```bash
sbt test
```

---

## 9. 常見問題解答

### Q1: Scala 2 vs Scala 3,應該學哪一個?

**建議: 學 Scala 3**

- Scala 3 是未來方向
- 語法更簡潔
- 型別系統更強大
- 但 Scala 2 的程式碼仍然很多,兩者互通性良好

### Q2: sbt 編譯很慢怎麼辦?

**解決方法:**

1. 使用 sbt shell 而非每次都啟動 sbt
```bash
# 進入 shell 後使用 ~compile
sbt
> ~compile
```

2. 增加 JVM 記憶體
```bash
# 設定環境變數
export SBT_OPTS="-Xmx2G -XX:+UseConcMarkSweepGC"
```

3. 使用 Metals (IDE) 的增量編譯

### Q3: 如何在 Scala 中使用 Java 函式庫?

直接加入依賴即可:

```scala
// build.sbt
libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.33"

// 在程式中使用
import java.sql.DriverManager

val conn = DriverManager.getConnection(url, user, password)
```

### Q4: REPL 中的定義可以儲存嗎?

可以使用 `:save` 指令:

```scala
scala> :save mycode.scala
```

或使用 Ammonite 的 `repl.sess.save("session.sc")`

---

## 10. 下一步學習

恭喜完成第一部分!您現在已經:
- ✅ 了解 Scala 的特點與優勢
- ✅ 成功安裝開發環境
- ✅ 會使用 REPL 和 sbt
- ✅ 建立並執行第一個 Scala 程式

**接下來應該學習:**
1. **[第二部分: 基本語法](scala_part2_basic_syntax.md)** - 變數、資料型別、控制流程
2. **[第三部分: 函數與方法](scala_part3_functions.md)** - 函數式程式設計基礎
3. **[第四部分: 物件導向](scala_part4_oop.md)** - 類別、物件、繼承

**推薦資源:**
- 官方文檔: [docs.scala-lang.org](https://docs.scala-lang.org)
- Scala 演練: [scala-exercises.org](https://www.scala-exercises.org)
- 線上 REPL: [scastie.scala-lang.org](https://scastie.scala-lang.org)

準備好了嗎?讓我們繼續學習第二部分!

---

> [📚 目錄](../README.md) | [下一篇：基本語法 »](scala_part2_basic_syntax.md)
