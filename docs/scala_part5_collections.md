# Scala 教學 - 第五部分:集合操作

> [« 上一篇：物件導向程式設計](scala_part4_oop.md) | [📚 目錄](../README.md) | [下一篇：模式比對 »](scala_part6_pattern_matching.md)

---

## 目錄
1. [集合概覽](#1-集合概覽)
2. [List 列表](#2-list-列表)
3. [Set 集合](#3-set-集合)
4. [Map 映射](#4-map-映射)
5. [Vector 向量](#5-vector-向量)
6. [Array 陣列](#6-array-陣列)
7. [轉換操作](#7-轉換操作)
8. [過濾與分組](#8-過濾與分組)
9. [聚合操作](#9-聚合操作)
10. [高階集合操作](#10-高階集合操作)
11. [for 推導式](#11-for-推導式)
12. [效能考量](#12-效能考量)
13. [實作練習](#13-實作練習)

---

## 1. 集合概覽

### 1.1 集合層次結構

```
Iterable
    |
    +--- Seq (有序)
    |     |
    |     +--- IndexedSeq (快速隨機存取)
    |     |     |--- Vector
    |     |     |--- Array
    |     |     +--- Range
    |     |
    |     +--- LinearSeq (快速頭尾操作)
    |           |--- List
    |           +--- LazyList
    |
    +--- Set (無序、不重複)
    |     |--- HashSet
    |     |--- TreeSet
    |     +--- BitSet
    |
    +--- Map (鍵值對)
          |--- HashMap
          +--- TreeMap
```

### 1.2 可變 vs 不可變

```scala
// 不可變集合 (預設、推薦)
import scala.collection.immutable.*

val list = List(1, 2, 3)
val set = Set(1, 2, 3)
val map = Map("a" -> 1, "b" -> 2)

// list.append(4)  // 錯誤!沒有此方法
val newList = list :+ 4  // 建立新集合

// 可變集合
import scala.collection.mutable

val mutableList = mutable.ListBuffer(1, 2, 3)
val mutableSet = mutable.Set(1, 2, 3)
val mutableMap = mutable.Map("a" -> 1, "b" -> 2)

mutableList += 4        // 直接修改
mutableSet += 5
mutableMap("c") = 3
```

### 1.3 常用操作概覽

```scala
val numbers = List(1, 2, 3, 4, 5)

// 轉換
numbers.map(_ * 2)           // List(2, 4, 6, 8, 10)
numbers.flatMap(n => List(n, n * 2))  // List(1, 2, 2, 4, 3, 6, ...)

// 過濾
numbers.filter(_ % 2 == 0)   // List(2, 4)
numbers.filterNot(_ % 2 == 0)  // List(1, 3, 5)

// 聚合
numbers.sum                  // 15
numbers.product              // 120
numbers.reduce(_ + _)        // 15
numbers.fold(0)(_ + _)       // 15

// 查詢
numbers.find(_ > 3)          // Some(4)
numbers.exists(_ > 3)        // true
numbers.forall(_ > 0)        // true

// 排序
numbers.sorted               // List(1, 2, 3, 4, 5)
numbers.sortBy(-_)           // List(5, 4, 3, 2, 1)
```

---

## 2. List 列表

### 2.1 建立 List

```scala
// 空列表
val empty = List()
val empty2 = Nil

// 有元素的列表
val numbers = List(1, 2, 3, 4, 5)
val strings = List("apple", "banana", "cherry")

// 使用 :: (cons) 運算子
val list1 = 1 :: 2 :: 3 :: Nil  // List(1, 2, 3)

// 使用 ::: 連接列表
val combined = List(1, 2) ::: List(3, 4)  // List(1, 2, 3, 4)

// 範圍建立
val range1 = List.range(1, 6)     // List(1, 2, 3, 4, 5)
val range2 = List.range(0, 10, 2) // List(0, 2, 4, 6, 8)

// 填充
val zeros = List.fill(5)(0)       // List(0, 0, 0, 0, 0)
val repeated = List.fill(3)("Hi") // List("Hi", "Hi", "Hi")

// 表格式建立
val table = List.tabulate(5)(n => n * n)  // List(0, 1, 4, 9, 16)
```

### 2.2 基本操作

```scala
val list = List(1, 2, 3, 4, 5)

// 存取元素
list.head           // 1 (第一個元素)
list.tail           // List(2, 3, 4, 5) (除了第一個)
list.last           // 5 (最後一個元素)
list.init           // List(1, 2, 3, 4) (除了最後一個)
list(2)             // 3 (索引存取,從 0 開始)

// 長度相關
list.length         // 5
list.isEmpty        // false
list.nonEmpty       // true
list.size           // 5

// 檢查元素
list.contains(3)    // true
list.indexOf(3)     // 2
list.lastIndexOf(3) // 2

// 子列表
list.take(3)        // List(1, 2, 3) (前 3 個)
list.drop(2)        // List(3, 4, 5) (去掉前 2 個)
list.slice(1, 4)    // List(2, 3, 4) (從索引 1 到 3)

// 條件取捨
list.takeWhile(_ < 4)  // List(1, 2, 3)
list.dropWhile(_ < 3)  // List(3, 4, 5)
list.span(_ < 3)       // (List(1, 2), List(3, 4, 5))
```

### 2.3 增加與刪除元素

```scala
val list = List(2, 3, 4)

// 前面增加
0 :: list           // List(0, 2, 3, 4)
1 :: 0 :: list      // List(1, 0, 2, 3, 4)

// 後面增加
list :+ 5           // List(2, 3, 4, 5)
list :+ 5 :+ 6      // List(2, 3, 4, 5, 6)

// 連接列表
List(1, 2) ++ List(3, 4)    // List(1, 2, 3, 4)
List(1, 2) ::: List(3, 4)   // List(1, 2, 3, 4)

// 插入
val inserted = list.patch(1, List(10, 20), 0)  // List(2, 10, 20, 3, 4)

// 移除
list.filter(_ != 3)          // List(2, 4)
list.filterNot(_ == 3)       // List(2, 4)
list.diff(List(2, 4))        // List(3)

// 去重
List(1, 2, 2, 3, 3, 3).distinct  // List(1, 2, 3)
```

### 2.4 List 模式比對

```scala
def processList(list: List[Int]): String = list match {
  case Nil => "空列表"
  case head :: Nil => s"只有一個元素: $head"
  case head :: tail => s"第一個: $head, 其餘: $tail"
}

// 更複雜的模式
def sumPairs(list: List[Int]): List[Int] = list match {
  case Nil => Nil
  case x :: Nil => List(x)
  case x :: y :: tail => (x + y) :: sumPairs(tail)
}

sumPairs(List(1, 2, 3, 4, 5))  // List(3, 7, 5)

// 匹配特定模式
val list = List(1, 2, 3, 4, 5)

list match {
  case List(1, 2, _*) => println("以 1, 2 開頭")
  case _ => println("其他")
}
```

### 2.5 List 進階操作

```scala
val list1 = List(1, 2, 3)
val list2 = List("a", "b", "c")

// 配對
list1.zip(list2)  // List((1,"a"), (2,"b"), (3,"c"))

// 帶索引
list1.zipWithIndex  // List((1,0), (2,1), (3,2))

// 解配對
val pairs = List((1, "a"), (2, "b"))
pairs.unzip  // (List(1, 2), List("a", "b"))

// 分組
List(1, 2, 3, 4, 5, 6).grouped(2).toList
// List(List(1, 2), List(3, 4), List(5, 6))

// 滑動窗口
List(1, 2, 3, 4, 5).sliding(3).toList
// List(List(1, 2, 3), List(2, 3, 4), List(3, 4, 5))

// 分割
val (evens, odds) = List(1, 2, 3, 4, 5, 6).partition(_ % 2 == 0)
// evens: List(2, 4, 6), odds: List(1, 3, 5)

// 扁平化
List(List(1, 2), List(3, 4), List(5, 6)).flatten
// List(1, 2, 3, 4, 5, 6)

// 轉置
List(List(1, 2, 3), List(4, 5, 6)).transpose
// List(List(1, 4), List(2, 5), List(3, 6))
```

---

## 3. Set 集合

### 3.1 建立 Set

```scala
// 基本建立
val set1 = Set(1, 2, 3, 4, 5)
val set2 = Set(1, 2, 2, 3, 3, 3)  // Set(1, 2, 3) - 自動去重

// 空集合
val empty = Set()
val empty2 = Set.empty[Int]

// 從其他集合建立
val fromList = List(1, 2, 2, 3).toSet  // Set(1, 2, 3)

// 有序 Set (TreeSet)
import scala.collection.immutable.TreeSet
val ordered = TreeSet(3, 1, 2, 5, 4)  // TreeSet(1, 2, 3, 4, 5)

// 可變 Set
import scala.collection.mutable
val mutableSet = mutable.Set(1, 2, 3)
```

### 3.2 基本操作

```scala
val set = Set(1, 2, 3, 4, 5)

// 檢查元素
set.contains(3)     // true
set(3)              // true (與 contains 相同)
set.exists(_ > 3)   // true

// 大小
set.size            // 5
set.isEmpty         // false
set.nonEmpty        // true

// 最小/最大值
set.min             // 1
set.max             // 5

// 子集合檢查
Set(1, 2).subsetOf(Set(1, 2, 3))  // true
Set(1, 2, 3).subsetOf(Set(1, 2))  // false
```

### 3.3 增加與移除

```scala
val set = Set(1, 2, 3)

// 增加元素
set + 4             // Set(1, 2, 3, 4)
set + 2             // Set(1, 2, 3) - 2 已存在
set + (4, 5)        // Set(1, 2, 3, 4, 5)
set ++ Set(4, 5)    // Set(1, 2, 3, 4, 5)

// 移除元素
set - 2             // Set(1, 3)
set - (1, 2)        // Set(3)
set -- Set(1, 2)    // Set(3)

// 可變 Set
import scala.collection.mutable
val mSet = mutable.Set(1, 2, 3)
mSet += 4           // Set(1, 2, 3, 4)
mSet -= 2           // Set(1, 3, 4)
mSet ++= Set(5, 6)  // Set(1, 3, 4, 5, 6)
```

### 3.4 集合運算

```scala
val set1 = Set(1, 2, 3, 4)
val set2 = Set(3, 4, 5, 6)

// 聯集 (Union)
set1 union set2     // Set(1, 2, 3, 4, 5, 6)
set1 | set2         // 同上

// 交集 (Intersection)
set1 intersect set2 // Set(3, 4)
set1 & set2         // 同上

// 差集 (Difference)
set1 diff set2      // Set(1, 2)
set1 &~ set2        // 同上
set2 diff set1      // Set(5, 6)

// 對稱差 (沒有直接方法,需要組合)
(set1 diff set2) union (set2 diff set1)  // Set(1, 2, 5, 6)
```

### 3.5 Set 應用範例

```scala
// 去除重複元素
val duplicates = List(1, 2, 2, 3, 3, 3, 4, 5, 5)
val unique = duplicates.toSet.toList  // List(1, 2, 3, 4, 5) - 順序可能不同

// 檢查是否有重複
def hasDuplicates[T](list: List[T]): Boolean = {
  list.size != list.toSet.size
}

hasDuplicates(List(1, 2, 3))      // false
hasDuplicates(List(1, 2, 2, 3))   // true

// 找出重複的元素
def findDuplicates[T](list: List[T]): Set[T] = {
  list.groupBy(identity).filter(_._2.length > 1).keySet
}

findDuplicates(List(1, 2, 2, 3, 3, 3, 4))  // Set(2, 3)

// 成員資格測試
val validIds = Set(1, 2, 3, 4, 5)
val inputId = 3

if (validIds.contains(inputId)) {
  println("有效的 ID")
}

// 黑名單/白名單
val blacklist = Set("user1", "user2", "user3")
val username = "user4"

if (!blacklist.contains(username)) {
  println("允許存取")
}
```

---

## 4. Map 映射

### 4.1 建立 Map

```scala
// 基本建立
val map1 = Map("a" -> 1, "b" -> 2, "c" -> 3)
val map2 = Map(("a", 1), ("b", 2), ("c", 3))  // 等價

// 空 Map
val empty = Map()
val empty2 = Map.empty[String, Int]

// 有序 Map (TreeMap)
import scala.collection.immutable.TreeMap
val ordered = TreeMap("c" -> 3, "a" -> 1, "b" -> 2)
// TreeMap(a -> 1, b -> 2, c -> 3)

// 可變 Map
import scala.collection.mutable
val mutableMap = mutable.Map("a" -> 1, "b" -> 2)
```

### 4.2 存取元素

```scala
val ages = Map("Alice" -> 25, "Bob" -> 30, "Charlie" -> 35)

// 直接存取 (可能拋出異常)
ages("Alice")           // 25
// ages("David")        // NoSuchElementException

// 安全存取
ages.get("Alice")       // Some(25)
ages.get("David")       // None

ages.getOrElse("Alice", 0)   // 25
ages.getOrElse("David", 0)   // 0

// 使用 Option
ages.get("Alice") match {
  case Some(age) => println(s"Alice is $age years old")
  case None => println("Alice not found")
}

// 鍵和值
ages.keys              // Iterable(Alice, Bob, Charlie)
ages.values            // Iterable(25, 30, 35)
ages.keySet            // Set(Alice, Bob, Charlie)
```

### 4.3 增加、更新與刪除

```scala
val map = Map("a" -> 1, "b" -> 2)

// 增加/更新
map + ("c" -> 3)        // Map(a -> 1, b -> 2, c -> 3)
map + ("a" -> 10)       // Map(a -> 10, b -> 2) - 更新
map + ("c" -> 3, "d" -> 4)  // 增加多個

// 合併
map ++ Map("c" -> 3, "d" -> 4)

// 刪除
map - "a"               // Map(b -> 2)
map - ("a", "b")        // Map()
map -- List("a", "b")   // Map()

// 可變 Map 的操作
import scala.collection.mutable
val mMap = mutable.Map("a" -> 1, "b" -> 2)

mMap("c") = 3           // 增加
mMap("a") = 10          // 更新
mMap += ("d" -> 4)      // 增加
mMap -= "b"             // 刪除
mMap ++= Map("e" -> 5, "f" -> 6)  // 合併

// 更新時的便利方法
val updated = map.updated("a", 10)  // Map(a -> 10, b -> 2)
```

### 4.4 遍歷 Map

```scala
val ages = Map("Alice" -> 25, "Bob" -> 30, "Charlie" -> 35)

// foreach
ages.foreach { case (name, age) =>
  println(s"$name is $age years old")
}

// map 轉換
val incremented = ages.map { case (name, age) => name -> (age + 1) }
// Map(Alice -> 26, Bob -> 31, Charlie -> 36)

// 只轉換值
val doubled = ages.view.mapValues(_ * 2).toMap
// Map(Alice -> 50, Bob -> 60, Charlie -> 70)

// filter
val adults = ages.filter { case (_, age) => age >= 30 }
// Map(Bob -> 30, Charlie -> 35)

// for 推導式
val ageStrings = for ((name, age) <- ages) yield s"$name: $age"
// Iterable(Alice: 25, Bob: 30, Charlie: 35)
```

### 4.5 Map 進階操作

```scala
val map1 = Map("a" -> 1, "b" -> 2)
val map2 = Map("b" -> 3, "c" -> 4)

// 合併 (右側覆蓋左側)
map1 ++ map2  // Map(a -> 1, b -> 3, c -> 4)

// 分組
val words = List("apple", "banana", "apricot", "cherry", "avocado")
val grouped = words.groupBy(_.head)
// Map(a -> List(apple, apricot, avocado), b -> List(banana), c -> List(cherry))

// 計數
val letters = List('a', 'b', 'a', 'c', 'b', 'a')
val counts = letters.groupBy(identity).view.mapValues(_.length).toMap
// Map(a -> 3, b -> 2, c -> 1)

// 反轉 Map
val original = Map("a" -> 1, "b" -> 2, "c" -> 3)
val inverted = original.map(_.swap)
// Map(1 -> a, 2 -> b, 3 -> c)

// withDefaultValue
val mapWithDefault = Map("a" -> 1, "b" -> 2).withDefaultValue(0)
mapWithDefault("c")  // 0 (不拋出異常)

// 多值 Map
val multiMap = Map("a" -> List(1, 2), "b" -> List(3, 4))
val updated = multiMap.updatedWith("a")(_.map(list => 0 :: list))
// Map(a -> List(0, 1, 2), b -> List(3, 4))
```

### 4.6 Map 實用範例

```scala
// 字數統計
def wordCount(text: String): Map[String, Int] = {
  text.toLowerCase
    .split("\\W+")
    .filter(_.nonEmpty)
    .groupBy(identity)
    .view
    .mapValues(_.length)
    .toMap
}

val text = "Hello world hello Scala world"
wordCount(text)  // Map(hello -> 2, world -> 2, scala -> 1)

// 電話簿
val phoneBook = Map(
  "Alice" -> "123-4567",
  "Bob" -> "234-5678",
  "Charlie" -> "345-6789"
)

def lookupPhone(name: String): String = {
  phoneBook.getOrElse(name, "號碼未找到")
}

// 快取實現
class Cache[K, V] {
  private var cache = Map.empty[K, V]
  
  def get(key: K): Option[V] = cache.get(key)
  
  def put(key: K, value: V): Unit = {
    cache = cache + (key -> value)
  }
  
  def getOrCompute(key: K)(compute: => V): V = {
    cache.get(key) match {
      case Some(value) => value
      case None =>
        val value = compute
        cache = cache + (key -> value)
        value
    }
  }
}

// 使用快取
val cache = new Cache[String, Int]
cache.getOrCompute("expensive") {
  println("計算中...")
  Thread.sleep(1000)
  42
}
```

---

## 5. Vector 向量

### 5.1 Vector 基礎

```scala
// 建立 Vector
val vec1 = Vector(1, 2, 3, 4, 5)
val vec2 = Vector.empty[Int]
val vec3 = Vector.fill(5)(0)  // Vector(0, 0, 0, 0, 0)

// 從其他集合建立
val fromList = List(1, 2, 3).toVector

// Vector 特性
// - 不可變
// - 快速隨機存取 O(log32 N) ≈ O(1)
// - 快速增加/更新 O(log32 N)
// - 適合大型集合
```

### 5.2 Vector 操作

```scala
val vec = Vector(1, 2, 3, 4, 5)

// 存取
vec(0)              // 1
vec.head            // 1
vec.last            // 5

// 增加元素
vec :+ 6            // Vector(1, 2, 3, 4, 5, 6)
0 +: vec            // Vector(0, 1, 2, 3, 4, 5)

// 更新元素 (返回新 Vector)
vec.updated(2, 10)  // Vector(1, 2, 10, 4, 5)

// 連接
vec ++ Vector(6, 7, 8)

// 其他操作與 List 類似
vec.map(_ * 2)
vec.filter(_ % 2 == 0)
vec.take(3)
```

### 5.3 Vector vs List

```scala
// List: 快速前置操作
val list = List(1, 2, 3)
val newList = 0 :: list  // O(1)

// Vector: 快速隨機存取和更新
val vector = Vector(1, 2, 3)
val element = vector(100)  // O(log32 N) ≈ O(1)
val updated = vector.updated(100, 999)  // O(log32 N)

// 性能比較
// List:
// - 前置操作 (::): O(1)
// - 隨機存取: O(N)
// - 更新: O(N)
// 
// Vector:
// - 前置/後置操作: O(log32 N)
// - 隨機存取: O(log32 N) ≈ O(1)
// - 更新: O(log32 N)

// 選擇建議:
// - 大部分操作在頭部 → List
// - 需要隨機存取 → Vector
// - 大型集合且操作多樣 → Vector
```

---

## 6. Array 陣列

### 6.1 Array 基礎

```scala
// 建立 Array
val arr1 = Array(1, 2, 3, 4, 5)
val arr2 = new Array[Int](10)  // 長度 10,初始值為 0
val arr3 = Array.fill(5)("Hi")
val arr4 = Array.tabulate(5)(i => i * i)

// Array 是可變的!
arr1(0) = 10
println(arr1(0))  // 10

// 多維陣列
val matrix = Array.ofDim[Int](3, 3)
matrix(0)(0) = 1
matrix(1)(1) = 2
matrix(2)(2) = 3
```

### 6.2 Array 操作

```scala
val arr = Array(1, 2, 3, 4, 5)

// 存取
arr(0)              // 1
arr.head            // 1
arr.last            // 5

// 修改 (in-place)
arr(0) = 10
arr.update(1, 20)

// 長度
arr.length          // 5
arr.size            // 5

// 轉換 (返回新 Array)
arr.map(_ * 2)
arr.filter(_ % 2 == 0)

// 排序 (in-place)
val unsorted = Array(3, 1, 4, 1, 5)
scala.util.Sorting.quickSort(unsorted)
println(unsorted.mkString(", "))  // 1, 1, 3, 4, 5
```

### 6.3 Array 與集合轉換

```scala
// Array → 其他集合
val arr = Array(1, 2, 3)
arr.toList          // List(1, 2, 3)
arr.toVector        // Vector(1, 2, 3)
arr.toSet           // Set(1, 2, 3)

// 其他集合 → Array
List(1, 2, 3).toArray
Vector(1, 2, 3).toArray
Set(1, 2, 3).toArray

// ArrayBuffer (可變、可調整大小)
import scala.collection.mutable.ArrayBuffer
val buffer = ArrayBuffer(1, 2, 3)
buffer += 4
buffer += (5, 6)
buffer.toArray      // 轉為固定大小的 Array
```

---

## 7. 轉換操作

### 7.1 map 系列

```scala
val numbers = List(1, 2, 3, 4, 5)

// map - 一對一轉換
numbers.map(x => x * 2)      // List(2, 4, 6, 8, 10)
numbers.map(_ * 2)           // 簡寫

// flatMap - 一對多轉換後扁平化
numbers.flatMap(x => List(x, x * 2))
// List(1, 2, 2, 4, 3, 6, 4, 8, 5, 10)

// flatten - 扁平化
List(List(1, 2), List(3, 4), List(5, 6)).flatten
// List(1, 2, 3, 4, 5, 6)

// collect - 部分函數應用
numbers.collect {
  case x if x % 2 == 0 => x * 2
}
// List(4, 8)

// mapValues (Map 專用)
Map("a" -> 1, "b" -> 2).view.mapValues(_ * 2).toMap
// Map(a -> 2, b -> 4)
```

### 7.2 實用的 map 範例

```scala
// 字串轉換
val words = List("hello", "world", "scala")
words.map(_.toUpperCase)           // List(HELLO, WORLD, SCALA)
words.map(_.length)                // List(5, 5, 5)
words.map(w => w.head -> w.length) // List((h,5), (w,5), (s,5))

// 物件轉換
case class Person(name: String, age: Int)
val people = List(
  Person("Alice", 25),
  Person("Bob", 30),
  Person("Charlie", 35)
)

people.map(_.name)                 // List(Alice, Bob, Charlie)
people.map(p => p.name -> p.age)   // List((Alice,25), (Bob,30), (Charlie,35))

// 嵌套結構
val matrix = List(
  List(1, 2, 3),
  List(4, 5, 6),
  List(7, 8, 9)
)

matrix.map(row => row.map(_ * 2))
// List(List(2, 4, 6), List(8, 10, 12), List(14, 16, 18))

matrix.flatMap(row => row)  // 或 matrix.flatten
// List(1, 2, 3, 4, 5, 6, 7, 8, 9)
```

---

## 8. 過濾與分組

### 8.1 過濾操作

```scala
val numbers = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// filter - 保留符合條件的
numbers.filter(_ % 2 == 0)         // List(2, 4, 6, 8, 10)
numbers.filter(_ > 5)              // List(6, 7, 8, 9, 10)

// filterNot - 排除符合條件的
numbers.filterNot(_ % 2 == 0)      // List(1, 3, 5, 7, 9)

// partition - 分割成兩組
val (evens, odds) = numbers.partition(_ % 2 == 0)
// evens: List(2, 4, 6, 8, 10), odds: List(1, 3, 5, 7, 9)

// span - 根據條件分割一次
val (less5, more5) = numbers.span(_ < 5)
// less5: List(1, 2, 3, 4), more5: List(5, 6, 7, 8, 9, 10)

// takeWhile / dropWhile
numbers.takeWhile(_ < 5)           // List(1, 2, 3, 4)
numbers.dropWhile(_ < 5)           // List(5, 6, 7, 8, 9, 10)
```

### 8.2 分組操作

```scala
val numbers = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// groupBy - 按條件分組
val grouped = numbers.groupBy(_ % 3)
// Map(1 -> List(1, 4, 7, 10), 2 -> List(2, 5, 8), 0 -> List(3, 6, 9))

// grouped - 固定大小分組
numbers.grouped(3).toList
// List(List(1, 2, 3), List(4, 5, 6), List(7, 8, 9), List(10))

// sliding - 滑動窗口
numbers.sliding(3).toList
// List(List(1, 2, 3), List(2, 3, 4), List(3, 4, 5), ...)

// 自訂滑動步長
numbers.sliding(3, 2).toList
// List(List(1, 2, 3), List(3, 4, 5), List(5, 6, 7), ...)
```

### 8.3 實用範例

```scala
// 按類別分組
case class Product(name: String, category: String, price: Double)

val products = List(
  Product("iPhone", "Electronics", 999.0),
  Product("iPad", "Electronics", 799.0),
  Product("Book", "Books", 29.0),
  Product("Pen", "Stationery", 2.0),
  Product("Notebook", "Stationery", 5.0)
)

val byCategory = products.groupBy(_.category)
// Map(
//   Electronics -> List(iPhone, iPad),
//   Books -> List(Book),
//   Stationery -> List(Pen, Notebook)
// )

// 按價格範圍分組
val byPriceRange = products.groupBy { p =>
  if (p.price < 10) "便宜"
  else if (p.price < 100) "中等"
  else "昂貴"
}

// 找出重複元素
def findDuplicates[T](list: List[T]): List[T] = {
  list.groupBy(identity)
    .filter(_._2.length > 1)
    .keys
    .toList
}

findDuplicates(List(1, 2, 2, 3, 3, 3, 4))  // List(2, 3)

// 統計出現次數
def countOccurrences[T](list: List[T]): Map[T, Int] = {
  list.groupBy(identity).view.mapValues(_.length).toMap
}

countOccurrences(List("a", "b", "a", "c", "b", "a"))
// Map(a -> 3, b -> 2, c -> 1)
```

---

## 9. 聚合操作

### 9.1 基本聚合

```scala
val numbers = List(1, 2, 3, 4, 5)

// sum, product, min, max
numbers.sum            // 15
numbers.product        // 120
numbers.min            // 1
numbers.max            // 5

// 平均值 (沒有直接方法)
numbers.sum.toDouble / numbers.length  // 3.0

// size, length
numbers.size           // 5
numbers.length         // 5

// count
numbers.count(_ % 2 == 0)  // 2 (偶數個數)
```

### 9.2 reduce 和 fold

```scala
val numbers = List(1, 2, 3, 4, 5)

// reduce - 沒有初始值
numbers.reduce(_ + _)              // 15
numbers.reduce(_ * _)              // 120
numbers.reduce((a, b) => a + b)    // 15

// reduceLeft / reduceRight
numbers.reduceLeft(_ - _)          // ((((1-2)-3)-4)-5) = -13
numbers.reduceRight(_ - _)         // (1-(2-(3-(4-5)))) = 3

// fold - 有初始值
numbers.fold(0)(_ + _)             // 15
numbers.fold(1)(_ * _)             // 120
numbers.fold(100)(_ + _)           // 115

// foldLeft / foldRight
numbers.foldLeft(0)(_ + _)         // 15
numbers.foldLeft("")((s, n) => s + n)  // "12345"

numbers.foldRight(0)(_ + _)        // 15
numbers.foldRight("")((n, s) => s + n)  // "54321"

// scanLeft / scanRight - 保留中間結果
numbers.scanLeft(0)(_ + _)
// List(0, 1, 3, 6, 10, 15) - 累積和的過程

numbers.scanRight(0)(_ + _)
// List(15, 14, 12, 9, 5, 0)
```

### 9.3 聚合範例

```scala
// 字串連接
val words = List("Hello", "Scala", "World")
words.reduce(_ + " " + _)          // "Hello Scala World"
words.mkString(" ")                // 更簡單的方式

// 找出最長的字串
words.reduce((a, b) => if (a.length > b.length) a else b)
// "Hello"

// 計算階乘
def factorial(n: Int): Int = {
  (1 to n).reduce(_ * _)
}

factorial(5)  // 120

// 反轉列表 (使用 fold)
val list = List(1, 2, 3, 4, 5)
list.foldLeft(List.empty[Int])((acc, x) => x :: acc)
// List(5, 4, 3, 2, 1)

// 分組計數
case class Person(name: String, age: Int, city: String)
val people = List(
  Person("Alice", 25, "Taipei"),
  Person("Bob", 30, "Tokyo"),
  Person("Charlie", 25, "Taipei"),
  Person("David", 30, "Seoul")
)

// 按城市計算平均年齡
val avgAgeByCity = people
  .groupBy(_.city)
  .view
  .mapValues(ps => ps.map(_.age).sum.toDouble / ps.length)
  .toMap

// Map(Taipei -> 25.0, Tokyo -> 30.0, Seoul -> 30.0)
```

### 9.4 單次走訪聚合

```scala
val numbers = List(1, 2, 3, 4, 5)

val sum = numbers.foldLeft(0)(_ + _)
// 15

// 實用範例:計算平均值
case class Average(sum: Double, count: Int)

val avg = numbers.foldLeft(Average(0, 0)) { (acc, n) =>
  Average(acc.sum + n, acc.count + 1)
}

avg.sum / avg.count  // 3.0
```

Scala 3.3.8 使用 Scala 2.13 集合函式庫。平行集合由獨立的
`scala-parallel-collections` 模組提供，而不是標準函式庫的 `List.aggregate`。

---

## 10. 高階集合操作

### 10.1 zip 和 unzip

```scala
val list1 = List(1, 2, 3)
val list2 = List("a", "b", "c")

// zip - 配對
list1.zip(list2)  // List((1,a), (2,b), (3,c))

// zipWithIndex - 配對索引
list1.zipWithIndex  // List((1,0), (2,1), (3,2))

// zipAll - 處理不同長度
val short = List(1, 2)
val long = List("a", "b", "c", "d")
short.zipAll(long, 0, "?")
// List((1,a), (2,b), (0,c), (0,d))

// unzip - 解配對
val pairs = List((1, "a"), (2, "b"), (3, "c"))
pairs.unzip  // (List(1, 2, 3), List(a, b, c))

// unzip3 - 三元組
val triples = List((1, "a", true), (2, "b", false))
triples.unzip3
// (List(1, 2), List(a, b), List(true, false))
```

### 10.2 排序

```scala
val numbers = List(3, 1, 4, 1, 5, 9, 2, 6)

// sorted - 自然排序
numbers.sorted              // List(1, 1, 2, 3, 4, 5, 6, 9)
numbers.sorted.reverse      // List(9, 6, 5, 4, 3, 2, 1, 1)

// sortBy - 按指定欄位排序
val words = List("apple", "pie", "ad", "banana")
words.sortBy(_.length)      // List(ad, pie, apple, banana)
words.sortBy(-_.length)     // List(banana, apple, pie, ad)

// sortWith - 自訂比較函數
numbers.sortWith(_ > _)     // List(9, 6, 5, 4, 3, 2, 1, 1)
words.sortWith(_.length < _.length)  // 同 sortBy(_.length)

// 複雜排序
case class Person(name: String, age: Int)
val people = List(
  Person("Alice", 30),
  Person("Bob", 25),
  Person("Charlie", 30)
)

// 先按年齡,再按名字
people.sortBy(p => (p.age, p.name))
// List(Person(Bob,25), Person(Alice,30), Person(Charlie,30))

// 多條件排序
people.sortWith { (p1, p2) =>
  if (p1.age != p2.age) p1.age < p2.age
  else p1.name < p2.name
}
```

### 10.3 去重與查找

```scala
val numbers = List(1, 2, 2, 3, 3, 3, 4, 4, 4, 4)

// distinct - 去重
numbers.distinct            // List(1, 2, 3, 4)

// find - 找第一個符合的
numbers.find(_ > 2)         // Some(3)
numbers.find(_ > 10)        // None

// exists - 是否存在
numbers.exists(_ > 5)       // false
numbers.exists(_ == 3)      // true

// forall - 是否全部符合
numbers.forall(_ > 0)       // true
numbers.forall(_ % 2 == 0)  // false

// contains
numbers.contains(3)         // true

// indexOf / lastIndexOf
numbers.indexOf(3)          // 3
numbers.lastIndexOf(3)      // 5

// indexWhere
numbers.indexWhere(_ > 2)   // 3 (第一個大於 2 的索引)
```

### 10.4 集合操作

```scala
val list1 = List(1, 2, 3, 4)
val list2 = List(3, 4, 5, 6)

// 聯集 (保留重複,使用 Set 可去重)
list1 ++ list2              // List(1, 2, 3, 4, 3, 4, 5, 6)
(list1 ++ list2).distinct   // List(1, 2, 3, 4, 5, 6)

// 交集
list1.intersect(list2)      // List(3, 4)

// 差集
list1.diff(list2)           // List(1, 2)
list2.diff(list1)           // List(5, 6)

// 對稱差
(list1.diff(list2) ++ list2.diff(list1)).distinct
// List(1, 2, 5, 6)
```

---

## 11. for 推導式

### 11.1 基本 for 推導式

```scala
// 單個生成器
val result1 = for (i <- 1 to 5) yield i * 2
// Vector(2, 4, 6, 8, 10)

// 多個生成器
val result2 = for {
  i <- 1 to 3
  j <- 1 to 2
} yield (i, j)
// Vector((1,1), (1,2), (2,1), (2,2), (3,1), (3,2))

// 帶條件 (guard)
val result3 = for {
  i <- 1 to 10
  if i % 2 == 0
} yield i
// Vector(2, 4, 6, 8, 10)

// 多個條件
val result4 = for {
  i <- 1 to 10
  if i % 2 == 0
  if i > 5
} yield i
// Vector(6, 8, 10)
```

### 11.2 for 推導式 vs 集合操作

```scala
val numbers = List(1, 2, 3, 4, 5)

// for 推導式
val doubled1 = for (n <- numbers) yield n * 2

// 等價的 map
val doubled2 = numbers.map(_ * 2)

// 帶條件的 for
val evens1 = for (n <- numbers if n % 2 == 0) yield n

// 等價的 filter
val evens2 = numbers.filter(_ % 2 == 0)

// 嵌套 for
val pairs1 = for {
  i <- 1 to 3
  j <- 1 to 2
} yield (i, j)

// 等價的 flatMap + map
val pairs2 = (1 to 3).flatMap(i => (1 to 2).map(j => (i, j)))
```

### 11.3 變數綁定

```scala
// 在 for 中定義變數
val result = for {
  x <- 1 to 5
  square = x * x        // 變數綁定
  if square > 10
} yield (x, square)
// Vector((4,16), (5,25))

// 解構
val pairs = List((1, "a"), (2, "b"), (3, "c"))

val result2 = for {
  (num, letter) <- pairs
  if num > 1
} yield s"$num: $letter"
// List("2: b", "3: c")
```

### 11.4 實用範例

```scala
// 笛卡爾積
val colors = List("Red", "Green", "Blue")
val sizes = List("S", "M", "L")

val products = for {
  color <- colors
  size <- sizes
} yield s"$color-$size"
// List(Red-S, Red-M, Red-L, Green-S, ...)

// 扁平化嵌套結構
val matrix = List(
  List(1, 2, 3),
  List(4, 5, 6),
  List(7, 8, 9)
)

val flattened = for {
  row <- matrix
  elem <- row
} yield elem
// List(1, 2, 3, 4, 5, 6, 7, 8, 9)

// 組合條件查詢
case class Person(name: String, age: Int, city: String)

val people = List(
  Person("Alice", 25, "Taipei"),
  Person("Bob", 30, "Tokyo"),
  Person("Charlie", 25, "Taipei"),
  Person("David", 35, "Seoul")
)

val result = for {
  person <- people
  if person.age >= 25
  if person.city == "Taipei"
} yield person.name
// List(Alice, Charlie)

// Option 處理
def divide(a: Int, b: Int): Option[Int] = {
  if (b != 0) Some(a / b) else None
}

val result3 = for {
  a <- divide(10, 2)
  b <- divide(a, 2)
  c <- divide(b, 0)  // None
} yield c
// None (短路求值)
```

---

## 12. 效能考量

### 12.1 集合選擇指南

```scala
// List: 前置操作頻繁
val list = 1 :: 2 :: 3 :: Nil     // O(1)
list.head                          // O(1)
list.tail                          // O(1)
list(100)                          // O(N) - 慢!

// Vector: 需要隨機存取
val vector = Vector(1, 2, 3)
vector(100)                        // O(log32 N) ≈ O(1)
vector.updated(100, 999)           // O(log32 N)

// Array: 需要可變性和最佳效能
val array = Array(1, 2, 3)
array(0) = 10                      // O(1) - 真正的 O(1)

// Set: 需要唯一性和成員測試
val set = Set(1, 2, 3)
set.contains(2)                    // O(1) 平均

// Map: 鍵值查找
val map = Map("a" -> 1, "b" -> 2)
map("a")                           // O(1) 平均
```

### 12.2 效能陷阱

```scala
// ❌ 避免:重複連接字串
var result = ""
for (i <- 1 to 1000) {
  result += i.toString  // O(N^2) - 每次都建立新字串!
}

// ✅ 推薦:使用 StringBuilder
val sb = new StringBuilder
for (i <- 1 to 1000) {
  sb.append(i)
}
val result = sb.toString

// ❌ 避免:在 List 末端操作
var list = List[Int]()
for (i <- 1 to 1000) {
  list = list :+ i  // O(N) 每次!
}

// ✅ 推薦:使用前置操作後反轉,或使用 ListBuffer
var list2 = List[Int]()
for (i <- 1 to 1000) {
  list2 = i :: list2
}
list2 = list2.reverse

// 或使用 ListBuffer
import scala.collection.mutable.ListBuffer
val buffer = ListBuffer[Int]()
for (i <- 1 to 1000) {
  buffer += i  // O(1)
}
val finalList = buffer.toList

// ❌ 避免:多次遍歷
val numbers = (1 to 1000000).toList
val sum = numbers.sum
val max = numbers.max
val min = numbers.min

// ✅ 推薦:單次遍歷
val (sum2, max2, min2) = numbers.foldLeft((0, Int.MinValue, Int.MaxValue)) {
  case ((s, max, min), n) => (s + n, math.max(max, n), math.min(min, n))
}
```

### 12.3 View (懶求值)

```scala
// 沒有 view - 每個操作建立中間集合
val result1 = (1 to 1000000)
  .map(_ + 1)       // 建立集合 1
  .filter(_ % 2 == 0)  // 建立集合 2
  .map(_ * 2)       // 建立集合 3
  .take(10)

// 使用 view - 懶求值,只處理需要的元素
val result2 = (1 to 1000000).view
  .map(_ + 1)
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(10)
  .toList  // 強制求值

// 性能測試
def timeIt(block: => Unit): Long = {
  val start = System.nanoTime()
  block
  System.nanoTime() - start
}

println("Without view: " + timeIt(result1))
println("With view: " + timeIt(result2))
```

---

## 13. 實作練習

### 練習 1: 資料處理管道

```scala
// 學生成績系統
case class Student(id: Int, name: String, scores: List[Int])

object GradeSystem {
  val students = List(
    Student(1, "Alice", List(85, 90, 88)),
    Student(2, "Bob", List(75, 80, 78)),
    Student(3, "Charlie", List(95, 92, 98)),
    Student(4, "David", List(60, 65, 70)),
    Student(5, "Eve", List(88, 85, 90))
  )
  
  // 計算平均分
  def average(scores: List[Int]): Double = {
    scores.sum.toDouble / scores.length
  }
  
  // 找出平均分最高的學生
  def topStudent: Student = {
    students.maxBy(s => average(s.scores))
  }
  
  // 找出不及格的學生 (平均分 < 70)
  def failingStudents: List[Student] = {
    students.filter(s => average(s.scores) < 70)
  }
  
  // 統計各分數段人數
  def gradeDistribution: Map[String, Int] = {
    students.groupBy { s =>
      val avg = average(s.scores)
      if (avg >= 90) "A"
      else if (avg >= 80) "B"
      else if (avg >= 70) "C"
      else "F"
    }.view.mapValues(_.length).toMap
  }
  
  // 所有科目的平均分
  def averageBySubject: List[Double] = {
    val allScores = students.map(_.scores)
    allScores.transpose.map(subject => subject.sum.toDouble / subject.length)
  }
  
  // 測試
  def main(args: Array[String]): Unit = {
    println(s"Top student: ${topStudent.name}")
    println(s"Failing students: ${failingStudents.map(_.name)}")
    println(s"Grade distribution: $gradeDistribution")
    println(s"Average by subject: $averageBySubject")
  }
}
```

### 練習 2: 文字分析

```scala
object TextAnalyzer {
  def wordFrequency(text: String): Map[String, Int] = {
    text.toLowerCase
      .split("\\W+")
      .filter(_.nonEmpty)
      .groupBy(identity)
      .view
      .mapValues(_.length)
      .toMap
  }
  
  def mostCommonWords(text: String, n: Int): List[(String, Int)] = {
    wordFrequency(text)
      .toList
      .sortBy(-_._2)
      .take(n)
  }
  
  def wordLengthDistribution(text: String): Map[Int, Int] = {
    text.split("\\W+")
      .filter(_.nonEmpty)
      .map(_.length)
      .groupBy(identity)
      .view
      .mapValues(_.length)
      .toMap
  }
  
  def longestWords(text: String, n: Int): List[String] = {
    text.split("\\W+")
      .filter(_.nonEmpty)
      .distinct
      .sortBy(-_.length)
      .take(n)
      .toList
  }
  
  // 測試
  def main(args: Array[String]): Unit = {
    val text = """
      Scala is a general-purpose programming language providing support for 
      both object-oriented programming and functional programming. The language 
      has a strong static type system. Designed to be concise, many of Scala's 
      design decisions are aimed to address criticisms of Java.
    """
    
    println("=== Word Frequency (Top 10) ===")
    mostCommonWords(text, 10).foreach { case (word, count) =>
      println(f"$word%15s: $count")
    }
    
    println("\n=== Word Length Distribution ===")
    wordLengthDistribution(text).toList.sortBy(_._1).foreach {
      case (length, count) => println(s"$length letters: $count words")
    }
    
    println("\n=== Longest Words (Top 5) ===")
    longestWords(text, 5).foreach(println)
  }
}
```

### 練習 3: 電商訂單系統

```scala
case class Product(id: Int, name: String, price: Double, category: String)
case class OrderItem(product: Product, quantity: Int) {
  def total: Double = product.price * quantity
}
case class Order(id: Int, customerId: Int, items: List[OrderItem], date: String)

object ECommerceAnalytics {
  val products = List(
    Product(1, "Laptop", 999.0, "Electronics"),
    Product(2, "Mouse", 29.0, "Electronics"),
    Product(3, "Keyboard", 79.0, "Electronics"),
    Product(4, "Book", 19.0, "Books"),
    Product(5, "Pen", 2.0, "Stationery")
  )
  
  val orders = List(
    Order(1, 101, List(
      OrderItem(products(0), 1),
      OrderItem(products(1), 2)
    ), "2024-01-15"),
    Order(2, 102, List(
      OrderItem(products(3), 3),
      OrderItem(products(4), 10)
    ), "2024-01-16"),
    Order(3, 101, List(
      OrderItem(products(2), 1)
    ), "2024-01-17")
  )
  
  // 訂單總額
  def orderTotal(order: Order): Double = {
    order.items.map(_.total).sum
  }
  
  // 所有訂單總額
  def totalRevenue: Double = {
    orders.map(orderTotal).sum
  }
  
  // 最暢銷產品
  def topSellingProducts(n: Int): List[(Product, Int)] = {
    orders
      .flatMap(_.items)
      .groupBy(_.product)
      .view
      .mapValues(items => items.map(_.quantity).sum)
      .toList
      .sortBy(-_._2)
      .take(n)
  }
  
  // 按類別統計銷售額
  def revenueByCategory: Map[String, Double] = {
    orders
      .flatMap(_.items)
      .groupBy(_.product.category)
      .view
      .mapValues(items => items.map(_.total).sum)
      .toMap
  }
  
  // 客戶消費排名
  def topCustomers(n: Int): List[(Int, Double)] = {
    orders
      .groupBy(_.customerId)
      .view
      .mapValues(orders => orders.map(orderTotal).sum)
      .toList
      .sortBy(-_._2)
      .take(n)
  }
  
  // 平均訂單金額
  def averageOrderValue: Double = {
    totalRevenue / orders.length
  }
  
  // 測試
  def main(args: Array[String]): Unit = {
    println(f"Total Revenue: $$${totalRevenue}%.2f")
    println(f"Average Order Value: $$${averageOrderValue}%.2f")
    
    println("\n=== Top Selling Products ===")
    topSellingProducts(3).foreach { case (product, quantity) =>
      println(s"${product.name}: $quantity units")
    }
    
    println("\n=== Revenue by Category ===")
    revenueByCategory.foreach { case (category, revenue) =>
      println(f"$category: $$${revenue}%.2f")
    }
    
    println("\n=== Top Customers ===")
    topCustomers(5).foreach { case (customerId, total) =>
      println(f"Customer $customerId: $$${total}%.2f")
    }
  }
}
```

### 練習 4: 社交網路分析

```scala
case class User(id: Int, name: String, followers: Set[Int])
case class Post(id: Int, userId: Int, content: String, likes: Int, tags: Set[String])

object SocialNetworkAnalysis {
  val users = List(
    User(1, "Alice", Set(2, 3, 4)),
    User(2, "Bob", Set(1, 3)),
    User(3, "Charlie", Set(1, 2, 4, 5)),
    User(4, "David", Set(1, 3)),
    User(5, "Eve", Set(3))
  )
  
  val posts = List(
    Post(1, 1, "Hello World", 10, Set("intro", "hello")),
    Post(2, 1, "Learning Scala", 25, Set("scala", "programming")),
    Post(3, 2, "Great day!", 5, Set("life")),
    Post(4, 3, "Scala tips", 30, Set("scala", "tips")),
    Post(5, 3, "More Scala", 20, Set("scala", "advanced"))
  )
  
  // 找出最受歡迎的用戶 (followers 最多)
  def mostPopularUser: User = {
    users.maxBy(_.followers.size)
  }
  
  // 找出互相關注的用戶對
  def mutualFollows: List[(Int, Int)] = {
    for {
      user1 <- users
      user2 <- users
      if user1.id < user2.id
      if user1.followers.contains(user2.id) && user2.followers.contains(user1.id)
    } yield (user1.id, user2.id)
  }
  
  // 最多讚的文章
  def topPosts(n: Int): List[Post] = {
    posts.sortBy(-_.likes).take(n)
  }
  
  // 最熱門的標籤
  def trendingTags(n: Int): List[(String, Int)] = {
    posts
      .flatMap(_.tags)
      .groupBy(identity)
      .view
      .mapValues(_.length)
      .toList
      .sortBy(-_._2)
      .take(n)
  }
  
  // 用戶的總讚數
  def userTotalLikes(userId: Int): Int = {
    posts.filter(_.userId == userId).map(_.likes).sum
  }
  
  // 用戶活躍度排名
  def userActivity: List[(String, Int)] = {
    users.map { user =>
      val postCount = posts.count(_.userId == user.id)
      val totalLikes = userTotalLikes(user.id)
      (user.name, postCount + totalLikes)
    }.sortBy(-_._2)
  }
  
  // 推薦關注 (關注者的關注者)
  def recommendFollows(userId: Int): Set[Int] = {
    val user = users.find(_.id == userId).get
    val following = user.followers
    
    following
      .flatMap(id => users.find(_.id == id).get.followers)
      .filterNot(id => following.contains(id) || id == userId)
  }
  
  // 測試
  def main(args: Array[String]): Unit = {
    println(s"Most popular user: ${mostPopularUser.name}")
    
    println("\n=== Mutual Follows ===")
    mutualFollows.foreach { case (id1, id2) =>
      val name1 = users.find(_.id == id1).get.name
      val name2 = users.find(_.id == id2).get.name
      println(s"$name1 ↔ $name2")
    }
    
    println("\n=== Top Posts ===")
    topPosts(3).foreach { post =>
      println(s"'${post.content}' by User ${post.userId}: ${post.likes} likes")
    }
    
    println("\n=== Trending Tags ===")
    trendingTags(5).foreach { case (tag, count) =>
      println(s"#$tag: $count posts")
    }
    
    println("\n=== User Activity ===")
    userActivity.foreach { case (name, score) =>
      println(s"$name: $score")
    }
    
    println("\n=== Recommendations for Alice ===")
    val recs = recommendFollows(1)
    recs.foreach { id =>
      println(users.find(_.id == id).get.name)
    }
  }
}
```

### 練習 5: 資料轉換練習

```scala
object DataTransformations {
  // 1. 扁平化嵌套列表
  def flattenNested[T](list: List[List[T]]): List[T] = {
    list.flatten
    // 或 list.flatMap(identity)
  }
  
  // 2. 移除連續重複元素
  def removeDuplicates[T](list: List[T]): List[T] = {
    list.foldRight(List.empty[T]) { (elem, acc) =>
      if (acc.isEmpty || acc.head != elem) elem :: acc
      else acc
    }
  }
  
  // 3. 分組連續相同元素
  def groupConsecutive[T](list: List[T]): List[List[T]] = {
    list.foldRight(List.empty[List[T]]) { (elem, acc) =>
      acc match {
        case Nil => List(List(elem))
        case head :: tail =>
          if (head.head == elem) (elem :: head) :: tail
          else List(elem) :: acc
      }
    }
  }
  
  // 4. 旋轉列表
  def rotate[T](list: List[T], n: Int): List[T] = {
    val size = list.length
    if (size == 0) list
    else {
      val shift = ((n % size) + size) % size
      list.drop(shift) ++ list.take(shift)
    }
  }
  
  // 5. 壓縮列表 (Run-Length Encoding)
  def encode[T](list: List[T]): List[(Int, T)] = {
    groupConsecutive(list).map(group => (group.length, group.head))
  }
  
  // 6. 解壓縮列表
  def decode[T](encoded: List[(Int, T)]): List[T] = {
    encoded.flatMap { case (count, elem) => List.fill(count)(elem) }
  }
  
  // 測試
  def main(args: Array[String]): Unit = {
    println("=== Flatten ===")
    println(flattenNested(List(List(1, 2), List(3, 4), List(5, 6))))
    
    println("\n=== Remove Duplicates ===")
    println(removeDuplicates(List(1, 1, 2, 2, 2, 3, 3, 4, 1, 1)))
    
    println("\n=== Group Consecutive ===")
    println(groupConsecutive(List(1, 1, 2, 2, 2, 3, 3, 4, 1, 1)))
    
    println("\n=== Rotate ===")
    println(rotate(List(1, 2, 3, 4, 5), 2))
    println(rotate(List(1, 2, 3, 4, 5), -2))
    
    println("\n=== Encode ===")
    val list = List(1, 1, 2, 2, 2, 3, 3, 4, 1, 1)
    val encoded = encode(list)
    println(s"Original: $list")
    println(s"Encoded: $encoded")
    println(s"Decoded: ${decode(encoded)}")
  }
}
```

---

## 14. 重點總結

### 集合選擇
- **List**: 前置操作、遞迴處理
- **Vector**: 隨機存取、通用序列
- **Set**: 唯一性、成員測試
- **Map**: 鍵值查找
- **Array**: 可變、最佳效能

### 核心操作
- **轉換**: map, flatMap, collect
- **過濾**: filter, filterNot, partition
- **聚合**: sum, reduce, fold, foldLeft
- **查找**: find, exists, forall
- **排序**: sorted, sortBy, sortWith

### 最佳實踐
- 優先使用不可變集合
- 使用 for 推導式提高可讀性
- 注意效能陷阱 (避免重複遍歷)
- 適時使用 view 進行懶求值
- 鏈式操作保持程式碼簡潔

### for 推導式
- 等價於 map/flatMap/filter 組合
- 提供更清晰的語法
- 支援模式比對和變數綁定
- 適合處理 Option、List 等

---

## 下一步

完成第五部分後,您已經掌握:
- ✅ Scala 集合體系的全貌
- ✅ List、Set、Map 等核心集合
- ✅ 豐富的集合操作方法
- ✅ for 推導式的應用
- ✅ 效能優化技巧

**接下來學習:**
- [第六部分:模式比對](scala_part6_pattern_matching.md) - Scala 最強大的特性之一
- [第七部分:錯誤處理](scala_part7_error_handling.md) - Option、Either、Try

準備好繼續了嗎?

---

> [« 上一篇：物件導向程式設計](scala_part4_oop.md) | [📚 目錄](../README.md) | [下一篇：模式比對 »](scala_part6_pattern_matching.md)
