# Scala Tutorial - Part 5: Collections

> [📚 Table of Contents](../../README.md) | [« Prev: OOP](scala_part4_oop.md) | [Next: Pattern Matching »](scala_part6_pattern_matching.md)

---

## Table of Contents
1. [Collections Overview](#1-collections-overview)
2. [List](#2-list)
3. [Set](#3-set)
4. [Map](#4-map)
5. [Vector](#5-vector)
6. [Array](#6-array)
7. [Transformation Operations](#7-transformation-operations)
8. [Filtering and Grouping](#8-filtering-and-grouping)
9. [Aggregation Operations](#9-aggregation-operations)
10. [Advanced Collection Operations](#10-advanced-collection-operations)
11. [for Comprehensions](#11-for-comprehensions)
12. [Performance Considerations](#12-performance-considerations)
13. [Practice Exercises](#13-practice-exercises)

---

## 1. Collections Overview

### 1.1 Collection Hierarchy

```
Traversable
    |
 Iterable
    |
    +--- Seq (ordered)
    |     |
    |     +--- IndexedSeq (fast random access)
    |     |     |--- Vector
    |     |     |--- Array
    |     |     +--- Range
    |     |
    |     +--- LinearSeq (fast head/tail operations)
    |           |--- List
    |           +--- Stream/LazyList
    |
    +--- Set (unordered, no duplicates)
    |     |--- HashSet
    |     |--- TreeSet
    |     +--- BitSet
    |
    +--- Map (key-value pairs)
          |--- HashMap
          +--- TreeMap
```

### 1.2 Mutable vs Immutable

```scala
// Immutable collections (default, recommended)
import scala.collection.immutable._

val list = List(1, 2, 3)
val set = Set(1, 2, 3)
val map = Map("a" -> 1, "b" -> 2)

// list.append(4)  // Error! No such method
val newList = list :+ 4  // Creates a new collection

// Mutable collections
import scala.collection.mutable

val mutableList = mutable.ListBuffer(1, 2, 3)
val mutableSet = mutable.Set(1, 2, 3)
val mutableMap = mutable.Map("a" -> 1, "b" -> 2)

mutableList += 4        // Modify in place
mutableSet += 5
mutableMap("c") = 3
```

### 1.3 Common Operations Overview

```scala
val numbers = List(1, 2, 3, 4, 5)

// Transformation
numbers.map(_ * 2)           // List(2, 4, 6, 8, 10)
numbers.flatMap(n => List(n, n * 2))  // List(1, 2, 2, 4, 3, 6, ...)

// Filtering
numbers.filter(_ % 2 == 0)   // List(2, 4)
numbers.filterNot(_ % 2 == 0)  // List(1, 3, 5)

// Aggregation
numbers.sum                  // 15
numbers.product              // 120
numbers.reduce(_ + _)        // 15
numbers.fold(0)(_ + _)       // 15

// Querying
numbers.find(_ > 3)          // Some(4)
numbers.exists(_ > 3)        // true
numbers.forall(_ > 0)        // true

// Sorting
numbers.sorted               // List(1, 2, 3, 4, 5)
numbers.sortBy(-_)           // List(5, 4, 3, 2, 1)
```

---

## 2. List

### 2.1 Creating a List

```scala
// Empty list
val empty = List()
val empty2 = Nil

// List with elements
val numbers = List(1, 2, 3, 4, 5)
val strings = List("apple", "banana", "cherry")

// Using the :: (cons) operator
val list1 = 1 :: 2 :: 3 :: Nil  // List(1, 2, 3)

// Concatenating lists with :::
val combined = List(1, 2) ::: List(3, 4)  // List(1, 2, 3, 4)

// Range creation
val range1 = List.range(1, 6)     // List(1, 2, 3, 4, 5)
val range2 = List.range(0, 10, 2) // List(0, 2, 4, 6, 8)

// Fill
val zeros = List.fill(5)(0)       // List(0, 0, 0, 0, 0)
val repeated = List.fill(3)("Hi") // List("Hi", "Hi", "Hi")

// Tabulate creation
val table = List.tabulate(5)(n => n * n)  // List(0, 1, 4, 9, 16)
```

### 2.2 Basic Operations

```scala
val list = List(1, 2, 3, 4, 5)

// Accessing elements
list.head           // 1 (first element)
list.tail           // List(2, 3, 4, 5) (all except first)
list.last           // 5 (last element)
list.init           // List(1, 2, 3, 4) (all except last)
list(2)             // 3 (index access, 0-based)

// Length-related
list.length         // 5
list.isEmpty        // false
list.nonEmpty       // true
list.size           // 5

// Checking elements
list.contains(3)    // true
list.indexOf(3)     // 2
list.lastIndexOf(3) // 2

// Sub-lists
list.take(3)        // List(1, 2, 3) (first 3 elements)
list.drop(2)        // List(3, 4, 5) (drop first 2 elements)
list.slice(1, 4)    // List(2, 3, 4) (index 1 to 3)

// Conditional take/drop
list.takeWhile(_ < 4)  // List(1, 2, 3)
list.dropWhile(_ < 3)  // List(3, 4, 5)
list.span(_ < 3)       // (List(1, 2), List(3, 4, 5))
```

### 2.3 Adding and Removing Elements

```scala
val list = List(2, 3, 4)

// Prepend
0 :: list           // List(0, 2, 3, 4)
1 :: 0 :: list      // List(1, 0, 2, 3, 4)

// Append
list :+ 5           // List(2, 3, 4, 5)
list :+ 5 :+ 6      // List(2, 3, 4, 5, 6)

// Concatenate lists
List(1, 2) ++ List(3, 4)    // List(1, 2, 3, 4)
List(1, 2) ::: List(3, 4)   // List(1, 2, 3, 4)

// Insert
val inserted = list.patch(1, List(10, 20), 0)  // List(2, 10, 20, 3, 4)

// Remove
list.filter(_ != 3)          // List(2, 4)
list.filterNot(_ == 3)       // List(2, 4)
list.diff(List(2, 4))        // List(3)

// Deduplicate
List(1, 2, 2, 3, 3, 3).distinct  // List(1, 2, 3)
```

### 2.4 List Pattern Matching

```scala
def processList(list: List[Int]): String = list match {
  case Nil => "empty list"
  case head :: Nil => s"only one element: $head"
  case head :: tail => s"first: $head, rest: $tail"
}

// More complex patterns
def sumPairs(list: List[Int]): List[Int] = list match {
  case Nil => Nil
  case x :: Nil => List(x)
  case x :: y :: tail => (x + y) :: sumPairs(tail)
}

sumPairs(List(1, 2, 3, 4, 5))  // List(3, 7, 5)

// Matching specific patterns
val list = List(1, 2, 3, 4, 5)

list match {
  case List(1, 2, _*) => println("starts with 1, 2")
  case _ => println("other")
}
```

### 2.5 Advanced List Operations

```scala
val list1 = List(1, 2, 3)
val list2 = List("a", "b", "c")

// Zip
list1.zip(list2)  // List((1,"a"), (2,"b"), (3,"c"))

// Zip with index
list1.zipWithIndex  // List((1,0), (2,1), (3,2))

// Unzip
val pairs = List((1, "a"), (2, "b"))
pairs.unzip  // (List(1, 2), List("a", "b"))

// Group into fixed-size chunks
List(1, 2, 3, 4, 5, 6).grouped(2).toList
// List(List(1, 2), List(3, 4), List(5, 6))

// Sliding window
List(1, 2, 3, 4, 5).sliding(3).toList
// List(List(1, 2, 3), List(2, 3, 4), List(3, 4, 5))

// Partition
val (evens, odds) = List(1, 2, 3, 4, 5, 6).partition(_ % 2 == 0)
// evens: List(2, 4, 6), odds: List(1, 3, 5)

// Flatten
List(List(1, 2), List(3, 4), List(5, 6)).flatten
// List(1, 2, 3, 4, 5, 6)

// Transpose
List(List(1, 2, 3), List(4, 5, 6)).transpose
// List(List(1, 4), List(2, 5), List(3, 6))
```

---

## 3. Set

### 3.1 Creating a Set

```scala
// Basic creation
val set1 = Set(1, 2, 3, 4, 5)
val set2 = Set(1, 2, 2, 3, 3, 3)  // Set(1, 2, 3) - duplicates removed automatically

// Empty set
val empty = Set()
val empty2 = Set.empty[Int]

// Create from another collection
val fromList = List(1, 2, 2, 3).toSet  // Set(1, 2, 3)

// Ordered Set (TreeSet)
import scala.collection.immutable.TreeSet
val ordered = TreeSet(3, 1, 2, 5, 4)  // TreeSet(1, 2, 3, 4, 5)

// Mutable Set
import scala.collection.mutable
val mutableSet = mutable.Set(1, 2, 3)
```

### 3.2 Basic Operations

```scala
val set = Set(1, 2, 3, 4, 5)

// Check for element
set.contains(3)     // true
set(3)              // true (same as contains)
set.exists(_ > 3)   // true

// Size
set.size            // 5
set.isEmpty         // false
set.nonEmpty        // true

// Min/max value
set.min             // 1
set.max             // 5

// Subset check
Set(1, 2).subsetOf(Set(1, 2, 3))  // true
Set(1, 2, 3).subsetOf(Set(1, 2))  // false
```

### 3.3 Adding and Removing

```scala
val set = Set(1, 2, 3)

// Add element
set + 4             // Set(1, 2, 3, 4)
set + 2             // Set(1, 2, 3) - 2 already exists
set + (4, 5)        // Set(1, 2, 3, 4, 5)
set ++ Set(4, 5)    // Set(1, 2, 3, 4, 5)

// Remove element
set - 2             // Set(1, 3)
set - (1, 2)        // Set(3)
set -- Set(1, 2)    // Set(3)

// Mutable Set
import scala.collection.mutable
val mSet = mutable.Set(1, 2, 3)
mSet += 4           // Set(1, 2, 3, 4)
mSet -= 2           // Set(1, 3, 4)
mSet ++= Set(5, 6)  // Set(1, 3, 4, 5, 6)
```

### 3.4 Set Operations

```scala
val set1 = Set(1, 2, 3, 4)
val set2 = Set(3, 4, 5, 6)

// Union
set1 union set2     // Set(1, 2, 3, 4, 5, 6)
set1 | set2         // same as above

// Intersection
set1 intersect set2 // Set(3, 4)
set1 & set2         // same as above

// Difference
set1 diff set2      // Set(1, 2)
set1 &~ set2        // same as above
set2 diff set1      // Set(5, 6)

// Symmetric difference (no direct method, combine operations)
(set1 diff set2) union (set2 diff set1)  // Set(1, 2, 5, 6)
```

### 3.5 Set Application Examples

```scala
// Remove duplicate elements
val duplicates = List(1, 2, 2, 3, 3, 3, 4, 5, 5)
val unique = duplicates.toSet.toList  // List(1, 2, 3, 4, 5) - order may vary

// Check if duplicates exist
def hasDuplicates[T](list: List[T]): Boolean = {
  list.size != list.toSet.size
}

hasDuplicates(List(1, 2, 3))      // false
hasDuplicates(List(1, 2, 2, 3))   // true

// Find duplicate elements
def findDuplicates[T](list: List[T]): Set[T] = {
  list.groupBy(identity).filter(_._2.length > 1).keySet
}

findDuplicates(List(1, 2, 2, 3, 3, 3, 4))  // Set(2, 3)

// Membership testing
val validIds = Set(1, 2, 3, 4, 5)
val inputId = 3

if (validIds.contains(inputId)) {
  println("Valid ID")
}

// Blacklist/whitelist
val blacklist = Set("user1", "user2", "user3")
val username = "user4"

if (!blacklist.contains(username)) {
  println("Access allowed")
}
```

---

## 4. Map

### 4.1 Creating a Map

```scala
// Basic creation
val map1 = Map("a" -> 1, "b" -> 2, "c" -> 3)
val map2 = Map(("a", 1), ("b", 2), ("c", 3))  // equivalent

// Empty Map
val empty = Map()
val empty2 = Map.empty[String, Int]

// Ordered Map (TreeMap)
import scala.collection.immutable.TreeMap
val ordered = TreeMap("c" -> 3, "a" -> 1, "b" -> 2)
// TreeMap(a -> 1, b -> 2, c -> 3)

// Mutable Map
import scala.collection.mutable
val mutableMap = mutable.Map("a" -> 1, "b" -> 2)
```

### 4.2 Accessing Elements

```scala
val ages = Map("Alice" -> 25, "Bob" -> 30, "Charlie" -> 35)

// Direct access (may throw exception)
ages("Alice")           // 25
// ages("David")        // NoSuchElementException

// Safe access
ages.get("Alice")       // Some(25)
ages.get("David")       // None

ages.getOrElse("Alice", 0)   // 25
ages.getOrElse("David", 0)   // 0

// Using Option
ages.get("Alice") match {
  case Some(age) => println(s"Alice is $age years old")
  case None => println("Alice not found")
}

// Keys and values
ages.keys              // Iterable(Alice, Bob, Charlie)
ages.values            // Iterable(25, 30, 35)
ages.keySet            // Set(Alice, Bob, Charlie)
```

### 4.3 Adding, Updating and Removing

```scala
val map = Map("a" -> 1, "b" -> 2)

// Add/update
map + ("c" -> 3)        // Map(a -> 1, b -> 2, c -> 3)
map + ("a" -> 10)       // Map(a -> 10, b -> 2) - update
map + ("c" -> 3, "d" -> 4)  // add multiple

// Merge
map ++ Map("c" -> 3, "d" -> 4)

// Remove
map - "a"               // Map(b -> 2)
map - ("a", "b")        // Map()
map -- List("a", "b")   // Map()

// Mutable Map operations
import scala.collection.mutable
val mMap = mutable.Map("a" -> 1, "b" -> 2)

mMap("c") = 3           // add
mMap("a") = 10          // update
mMap += ("d" -> 4)      // add
mMap -= "b"             // remove
mMap ++= Map("e" -> 5, "f" -> 6)  // merge

// Convenience method for updating
val updated = map.updated("a", 10)  // Map(a -> 10, b -> 2)
```

### 4.4 Iterating over a Map

```scala
val ages = Map("Alice" -> 25, "Bob" -> 30, "Charlie" -> 35)

// foreach
ages.foreach { case (name, age) =>
  println(s"$name is $age years old")
}

// map transformation
val incremented = ages.map { case (name, age) => name -> (age + 1) }
// Map(Alice -> 26, Bob -> 31, Charlie -> 36)

// Transform values only
val doubled = ages.view.mapValues(_ * 2).toMap
// Map(Alice -> 50, Bob -> 60, Charlie -> 70)

// filter
val adults = ages.filter { case (_, age) => age >= 30 }
// Map(Bob -> 30, Charlie -> 35)

// for comprehension
val ageStrings = for ((name, age) <- ages) yield s"$name: $age"
// Iterable(Alice: 25, Bob: 30, Charlie: 35)
```

### 4.5 Advanced Map Operations

```scala
val map1 = Map("a" -> 1, "b" -> 2)
val map2 = Map("b" -> 3, "c" -> 4)

// Merge (right side overwrites left)
map1 ++ map2  // Map(a -> 1, b -> 3, c -> 4)

// Group by
val words = List("apple", "banana", "apricot", "cherry", "avocado")
val grouped = words.groupBy(_.head)
// Map(a -> List(apple, apricot, avocado), b -> List(banana), c -> List(cherry))

// Count occurrences
val letters = List('a', 'b', 'a', 'c', 'b', 'a')
val counts = letters.groupBy(identity).view.mapValues(_.length).toMap
// Map(a -> 3, b -> 2, c -> 1)

// Invert Map
val original = Map("a" -> 1, "b" -> 2, "c" -> 3)
val inverted = original.map(_.swap)
// Map(1 -> a, 2 -> b, 3 -> c)

// withDefaultValue
val mapWithDefault = Map("a" -> 1, "b" -> 2).withDefaultValue(0)
mapWithDefault("c")  // 0 (no exception thrown)

// Multi-value Map
val multiMap = Map("a" -> List(1, 2), "b" -> List(3, 4))
val updated = multiMap.updatedWith("a")(_.map(list => 0 :: list))
// Map(a -> List(0, 1, 2), b -> List(3, 4))
```

### 4.6 Practical Map Examples

```scala
// Word count
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

// Phone book
val phoneBook = Map(
  "Alice" -> "123-4567",
  "Bob" -> "234-5678",
  "Charlie" -> "345-6789"
)

def lookupPhone(name: String): String = {
  phoneBook.getOrElse(name, "Number not found")
}

// Cache implementation
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

// Using the cache
val cache = new Cache[String, Int]
cache.getOrCompute("expensive") {
  println("Computing...")
  Thread.sleep(1000)
  42
}
```

---

## 5. Vector

### 5.1 Vector Basics

```scala
// Create a Vector
val vec1 = Vector(1, 2, 3, 4, 5)
val vec2 = Vector.empty[Int]
val vec3 = Vector.fill(5)(0)  // Vector(0, 0, 0, 0, 0)

// Create from another collection
val fromList = List(1, 2, 3).toVector

// Vector characteristics
// - Immutable
// - Fast random access O(log32 N) ≈ O(1)
// - Fast append/update O(log32 N)
// - Suitable for large collections
```

### 5.2 Vector Operations

```scala
val vec = Vector(1, 2, 3, 4, 5)

// Access
vec(0)              // 1
vec.head            // 1
vec.last            // 5

// Add elements
vec :+ 6            // Vector(1, 2, 3, 4, 5, 6)
0 +: vec            // Vector(0, 1, 2, 3, 4, 5)

// Update element (returns new Vector)
vec.updated(2, 10)  // Vector(1, 2, 10, 4, 5)

// Concatenate
vec ++ Vector(6, 7, 8)

// Other operations similar to List
vec.map(_ * 2)
vec.filter(_ % 2 == 0)
vec.take(3)
```

### 5.3 Vector vs List

```scala
// List: fast prepend operations
val list = List(1, 2, 3)
val newList = 0 :: list  // O(1)

// Vector: fast random access and updates
val vector = Vector(1, 2, 3)
val element = vector(100)  // O(log32 N) ≈ O(1)
val updated = vector.updated(100, 999)  // O(log32 N)

// Performance comparison
// List:
// - Prepend (::): O(1)
// - Random access: O(N)
// - Update: O(N)
// 
// Vector:
// - Prepend/append: O(log32 N)
// - Random access: O(log32 N) ≈ O(1)
// - Update: O(log32 N)

// Choosing guidelines:
// - Most operations at head → List
// - Need random access → Vector
// - Large collection with varied operations → Vector
```

---

## 6. Array

### 6.1 Array Basics

```scala
// Create an Array
val arr1 = Array(1, 2, 3, 4, 5)
val arr2 = new Array[Int](10)  // length 10, initialized to 0
val arr3 = Array.fill(5)("Hi")
val arr4 = Array.tabulate(5)(i => i * i)

// Array is mutable!
arr1(0) = 10
println(arr1(0))  // 10

// Multi-dimensional array
val matrix = Array.ofDim[Int](3, 3)
matrix(0)(0) = 1
matrix(1)(1) = 2
matrix(2)(2) = 3
```

### 6.2 Array Operations

```scala
val arr = Array(1, 2, 3, 4, 5)

// Access
arr(0)              // 1
arr.head            // 1
arr.last            // 5

// Modify (in-place)
arr(0) = 10
arr.update(1, 20)

// Length
arr.length          // 5
arr.size            // 5

// Transformation (returns new Array)
arr.map(_ * 2)
arr.filter(_ % 2 == 0)

// Sort (in-place)
val unsorted = Array(3, 1, 4, 1, 5)
scala.util.Sorting.quickSort(unsorted)
println(unsorted.mkString(", "))  // 1, 1, 3, 4, 5
```

### 6.3 Converting Between Array and Collections

```scala
// Array → other collections
val arr = Array(1, 2, 3)
arr.toList          // List(1, 2, 3)
arr.toVector        // Vector(1, 2, 3)
arr.toSet           // Set(1, 2, 3)

// Other collections → Array
List(1, 2, 3).toArray
Vector(1, 2, 3).toArray
Set(1, 2, 3).toArray

// ArrayBuffer (mutable, resizable)
import scala.collection.mutable.ArrayBuffer
val buffer = ArrayBuffer(1, 2, 3)
buffer += 4
buffer += (5, 6)
buffer.toArray      // Convert to fixed-size Array
```

---

## 7. Transformation Operations

### 7.1 The map Family

```scala
val numbers = List(1, 2, 3, 4, 5)

// map - one-to-one transformation
numbers.map(x => x * 2)      // List(2, 4, 6, 8, 10)
numbers.map(_ * 2)           // shorthand

// flatMap - one-to-many transformation then flatten
numbers.flatMap(x => List(x, x * 2))
// List(1, 2, 2, 4, 3, 6, 4, 8, 5, 10)

// flatten - flatten nested collections
List(List(1, 2), List(3, 4), List(5, 6)).flatten
// List(1, 2, 3, 4, 5, 6)

// collect - apply a partial function
numbers.collect {
  case x if x % 2 == 0 => x * 2
}
// List(4, 8)

// mapValues (Map-specific)
Map("a" -> 1, "b" -> 2).view.mapValues(_ * 2).toMap
// Map(a -> 2, b -> 4)
```

### 7.2 Practical map Examples

```scala
// String transformation
val words = List("hello", "world", "scala")
words.map(_.toUpperCase)           // List(HELLO, WORLD, SCALA)
words.map(_.length)                // List(5, 5, 5)
words.map(w => w.head -> w.length) // List((h,5), (w,5), (s,5))

// Object transformation
case class Person(name: String, age: Int)
val people = List(
  Person("Alice", 25),
  Person("Bob", 30),
  Person("Charlie", 35)
)

people.map(_.name)                 // List(Alice, Bob, Charlie)
people.map(p => p.name -> p.age)   // List((Alice,25), (Bob,30), (Charlie,35))

// Nested structures
val matrix = List(
  List(1, 2, 3),
  List(4, 5, 6),
  List(7, 8, 9)
)

matrix.map(row => row.map(_ * 2))
// List(List(2, 4, 6), List(8, 10, 12), List(14, 16, 18))

matrix.flatMap(row => row)  // or matrix.flatten
// List(1, 2, 3, 4, 5, 6, 7, 8, 9)
```

---

## 8. Filtering and Grouping

### 8.1 Filtering Operations

```scala
val numbers = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// filter - keep elements matching the condition
numbers.filter(_ % 2 == 0)         // List(2, 4, 6, 8, 10)
numbers.filter(_ > 5)              // List(6, 7, 8, 9, 10)

// filterNot - exclude elements matching the condition
numbers.filterNot(_ % 2 == 0)      // List(1, 3, 5, 7, 9)

// partition - split into two groups
val (evens, odds) = numbers.partition(_ % 2 == 0)
// evens: List(2, 4, 6, 8, 10), odds: List(1, 3, 5, 7, 9)

// span - split once based on condition
val (less5, more5) = numbers.span(_ < 5)
// less5: List(1, 2, 3, 4), more5: List(5, 6, 7, 8, 9, 10)

// takeWhile / dropWhile
numbers.takeWhile(_ < 5)           // List(1, 2, 3, 4)
numbers.dropWhile(_ < 5)           // List(5, 6, 7, 8, 9, 10)
```

### 8.2 Grouping Operations

```scala
val numbers = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// groupBy - group by condition
val grouped = numbers.groupBy(_ % 3)
// Map(1 -> List(1, 4, 7, 10), 2 -> List(2, 5, 8), 0 -> List(3, 6, 9))

// grouped - fixed-size groups
numbers.grouped(3).toList
// List(List(1, 2, 3), List(4, 5, 6), List(7, 8, 9), List(10))

// sliding - sliding window
numbers.sliding(3).toList
// List(List(1, 2, 3), List(2, 3, 4), List(3, 4, 5), ...)

// Custom sliding step
numbers.sliding(3, 2).toList
// List(List(1, 2, 3), List(3, 4, 5), List(5, 6, 7), ...)
```

### 8.3 Practical Examples

```scala
// Group by category
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

// Group by price range
val byPriceRange = products.groupBy { p =>
  if (p.price < 10) "Cheap"
  else if (p.price < 100) "Medium"
  else "Expensive"
}

// Find duplicate elements
def findDuplicates[T](list: List[T]): List[T] = {
  list.groupBy(identity)
    .filter(_._2.length > 1)
    .keys
    .toList
}

findDuplicates(List(1, 2, 2, 3, 3, 3, 4))  // List(2, 3)

// Count occurrences
def countOccurrences[T](list: List[T]): Map[T, Int] = {
  list.groupBy(identity).view.mapValues(_.length).toMap
}

countOccurrences(List("a", "b", "a", "c", "b", "a"))
// Map(a -> 3, b -> 2, c -> 1)
```

---

## 9. Aggregation Operations

### 9.1 Basic Aggregation

```scala
val numbers = List(1, 2, 3, 4, 5)

// sum, product, min, max
numbers.sum            // 15
numbers.product        // 120
numbers.min            // 1
numbers.max            // 5

// Average (no direct method)
numbers.sum.toDouble / numbers.length  // 3.0

// size, length
numbers.size           // 5
numbers.length         // 5

// count
numbers.count(_ % 2 == 0)  // 2 (count of even numbers)
```

### 9.2 reduce and fold

```scala
val numbers = List(1, 2, 3, 4, 5)

// reduce - no initial value
numbers.reduce(_ + _)              // 15
numbers.reduce(_ * _)              // 120
numbers.reduce((a, b) => a + b)    // 15

// reduceLeft / reduceRight
numbers.reduceLeft(_ - _)          // ((((1-2)-3)-4)-5) = -13
numbers.reduceRight(_ - _)         // (1-(2-(3-(4-5)))) = 3

// fold - with initial value
numbers.fold(0)(_ + _)             // 15
numbers.fold(1)(_ * _)             // 120
numbers.fold(100)(_ + _)           // 115

// foldLeft / foldRight
numbers.foldLeft(0)(_ + _)         // 15
numbers.foldLeft("")((s, n) => s + n)  // "12345"

numbers.foldRight(0)(_ + _)        // 15
numbers.foldRight("")((n, s) => s + n)  // "54321"

// scanLeft / scanRight - keep intermediate results
numbers.scanLeft(0)(_ + _)
// List(0, 1, 3, 6, 10, 15) - running sum

numbers.scanRight(0)(_ + _)
// List(15, 14, 12, 9, 5, 0)
```

### 9.3 Aggregation Examples

```scala
// String concatenation
val words = List("Hello", "Scala", "World")
words.reduce(_ + " " + _)          // "Hello Scala World"
words.mkString(" ")                // simpler approach

// Find the longest string
words.reduce((a, b) => if (a.length > b.length) a else b)
// "Hello"

// Compute factorial
def factorial(n: Int): Int = {
  (1 to n).reduce(_ * _)
}

factorial(5)  // 120

// Reverse list (using fold)
val list = List(1, 2, 3, 4, 5)
list.foldLeft(List.empty[Int])((acc, x) => x :: acc)
// List(5, 4, 3, 2, 1)

// Group counting
case class Person(name: String, age: Int, city: String)
val people = List(
  Person("Alice", 25, "Taipei"),
  Person("Bob", 30, "Tokyo"),
  Person("Charlie", 25, "Taipei"),
  Person("David", 30, "Seoul")
)

// Average age by city
val avgAgeByCity = people
  .groupBy(_.city)
  .view
  .mapValues(ps => ps.map(_.age).sum.toDouble / ps.length)
  .toMap

// Map(Taipei -> 25.0, Tokyo -> 30.0, Seoul -> 30.0)
```

### 9.4 aggregate (Parallel Aggregation)

```scala
// aggregate - supports parallel computation
val numbers = List(1, 2, 3, 4, 5)

val sum = numbers.aggregate(0)(
  (acc, n) => acc + n,          // seqop: combine individual elements
  (acc1, acc2) => acc1 + acc2   // combop: combine partial results
)
// 15

// Practical example: compute average
case class Average(sum: Double, count: Int)

val avg = numbers.aggregate(Average(0, 0))(
  (acc, n) => Average(acc.sum + n, acc.count + 1),
  (acc1, acc2) => Average(acc1.sum + acc2.sum, acc1.count + acc2.count)
)

avg.sum / avg.count  // 3.0
```

---

## 10. Advanced Collection Operations

### 10.1 zip and unzip

```scala
val list1 = List(1, 2, 3)
val list2 = List("a", "b", "c")

// zip - pair up elements
list1.zip(list2)  // List((1,a), (2,b), (3,c))

// zipWithIndex - pair with index
list1.zipWithIndex  // List((1,0), (2,1), (3,2))

// zipAll - handle different lengths
val short = List(1, 2)
val long = List("a", "b", "c", "d")
short.zipAll(long, 0, "?")
// List((1,a), (2,b), (0,c), (0,d))

// unzip - separate pairs
val pairs = List((1, "a"), (2, "b"), (3, "c"))
pairs.unzip  // (List(1, 2, 3), List(a, b, c))

// unzip3 - separate triples
val triples = List((1, "a", true), (2, "b", false))
triples.unzip3
// (List(1, 2), List(a, b), List(true, false))
```

### 10.2 Sorting

```scala
val numbers = List(3, 1, 4, 1, 5, 9, 2, 6)

// sorted - natural ordering
numbers.sorted              // List(1, 1, 2, 3, 4, 5, 6, 9)
numbers.sorted.reverse      // List(9, 6, 5, 4, 3, 2, 1, 1)

// sortBy - sort by a specific field
val words = List("apple", "pie", "ad", "banana")
words.sortBy(_.length)      // List(ad, pie, apple, banana)
words.sortBy(-_.length)     // List(banana, apple, pie, ad)

// sortWith - custom comparator function
numbers.sortWith(_ > _)     // List(9, 6, 5, 4, 3, 2, 1, 1)
words.sortWith(_.length < _.length)  // same as sortBy(_.length)

// Complex sorting
case class Person(name: String, age: Int)
val people = List(
  Person("Alice", 30),
  Person("Bob", 25),
  Person("Charlie", 30)
)

// Sort by age first, then by name
people.sortBy(p => (p.age, p.name))
// List(Person(Bob,25), Person(Alice,30), Person(Charlie,30))

// Multi-criteria sorting
people.sortWith { (p1, p2) =>
  if (p1.age != p2.age) p1.age < p2.age
  else p1.name < p2.name
}
```

### 10.3 Deduplication and Lookup

```scala
val numbers = List(1, 2, 2, 3, 3, 3, 4, 4, 4, 4)

// distinct - remove duplicates
numbers.distinct            // List(1, 2, 3, 4)

// find - find first matching element
numbers.find(_ > 2)         // Some(3)
numbers.find(_ > 10)        // None

// exists - check if any element matches
numbers.exists(_ > 5)       // false
numbers.exists(_ == 3)      // true

// forall - check if all elements match
numbers.forall(_ > 0)       // true
numbers.forall(_ % 2 == 0)  // false

// contains
numbers.contains(3)         // true

// indexOf / lastIndexOf
numbers.indexOf(3)          // 3
numbers.lastIndexOf(3)      // 5

// indexWhere
numbers.indexWhere(_ > 2)   // 3 (index of first element greater than 2)
```

### 10.4 Set-like Operations on Collections

```scala
val list1 = List(1, 2, 3, 4)
val list2 = List(3, 4, 5, 6)

// Union (preserves duplicates; use Set to remove them)
list1 ++ list2              // List(1, 2, 3, 4, 3, 4, 5, 6)
(list1 ++ list2).distinct   // List(1, 2, 3, 4, 5, 6)

// Intersection
list1.intersect(list2)      // List(3, 4)

// Difference
list1.diff(list2)           // List(1, 2)
list2.diff(list1)           // List(5, 6)

// Symmetric difference
(list1.diff(list2) ++ list2.diff(list1)).distinct
// List(1, 2, 5, 6)
```

---

## 11. for Comprehensions

### 11.1 Basic for Comprehensions

```scala
// Single generator
val result1 = for (i <- 1 to 5) yield i * 2
// Vector(2, 4, 6, 8, 10)

// Multiple generators
val result2 = for {
  i <- 1 to 3
  j <- 1 to 2
} yield (i, j)
// Vector((1,1), (1,2), (2,1), (2,2), (3,1), (3,2))

// With guard condition
val result3 = for {
  i <- 1 to 10
  if i % 2 == 0
} yield i
// Vector(2, 4, 6, 8, 10)

// Multiple conditions
val result4 = for {
  i <- 1 to 10
  if i % 2 == 0
  if i > 5
} yield i
// Vector(6, 8, 10)
```

### 11.2 for Comprehensions vs Collection Operations

```scala
val numbers = List(1, 2, 3, 4, 5)

// for comprehension
val doubled1 = for (n <- numbers) yield n * 2

// equivalent map
val doubled2 = numbers.map(_ * 2)

// for with condition
val evens1 = for (n <- numbers if n % 2 == 0) yield n

// equivalent filter
val evens2 = numbers.filter(_ % 2 == 0)

// nested for
val pairs1 = for {
  i <- 1 to 3
  j <- 1 to 2
} yield (i, j)

// equivalent flatMap + map
val pairs2 = (1 to 3).flatMap(i => (1 to 2).map(j => (i, j)))
```

### 11.3 Variable Binding

```scala
// Define variables inside a for comprehension
val result = for {
  x <- 1 to 5
  square = x * x        // variable binding
  if square > 10
} yield (x, square)
// Vector((4,16), (5,25))

// Destructuring
val pairs = List((1, "a"), (2, "b"), (3, "c"))

val result2 = for {
  (num, letter) <- pairs
  if num > 1
} yield s"$num: $letter"
// List("2: b", "3: c")
```

### 11.4 Practical Examples

```scala
// Cartesian product
val colors = List("Red", "Green", "Blue")
val sizes = List("S", "M", "L")

val products = for {
  color <- colors
  size <- sizes
} yield s"$color-$size"
// List(Red-S, Red-M, Red-L, Green-S, ...)

// Flatten nested structure
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

// Combined conditional query
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

// Option handling
def divide(a: Int, b: Int): Option[Int] = {
  if (b != 0) Some(a / b) else None
}

val result3 = for {
  a <- divide(10, 2)
  b <- divide(a, 2)
  c <- divide(b, 0)  // None
} yield c
// None (short-circuit evaluation)
```

---

## 12. Performance Considerations

### 12.1 Collection Selection Guide

```scala
// List: frequent prepend operations
val list = 1 :: 2 :: 3 :: Nil     // O(1)
list.head                          // O(1)
list.tail                          // O(1)
list(100)                          // O(N) - slow!

// Vector: need random access
val vector = Vector(1, 2, 3)
vector(100)                        // O(log32 N) ≈ O(1)
vector.updated(100, 999)           // O(log32 N)

// Array: need mutability and best performance
val array = Array(1, 2, 3)
array(0) = 10                      // O(1) - true O(1)

// Set: need uniqueness and membership testing
val set = Set(1, 2, 3)
set.contains(2)                    // O(1) average

// Map: key-value lookup
val map = Map("a" -> 1, "b" -> 2)
map("a")                           // O(1) average
```

### 12.2 Performance Pitfalls

```scala
// Bad: repeated string concatenation
var result = ""
for (i <- 1 to 1000) {
  result += i.toString  // O(N^2) - creates a new string each time!
}

// Good: use StringBuilder
val sb = new StringBuilder
for (i <- 1 to 1000) {
  sb.append(i)
}
val result = sb.toString

// Bad: appending to the end of a List
var list = List[Int]()
for (i <- 1 to 1000) {
  list = list :+ i  // O(N) each time!
}

// Good: prepend then reverse, or use ListBuffer
var list2 = List[Int]()
for (i <- 1 to 1000) {
  list2 = i :: list2
}
list2 = list2.reverse

// Or use ListBuffer
import scala.collection.mutable.ListBuffer
val buffer = ListBuffer[Int]()
for (i <- 1 to 1000) {
  buffer += i  // O(1)
}
val finalList = buffer.toList

// Bad: multiple traversals
val numbers = (1 to 1000000).toList
val sum = numbers.sum
val max = numbers.max
val min = numbers.min

// Good: single traversal
val (sum2, max2, min2) = numbers.foldLeft((0, Int.MinValue, Int.MaxValue)) {
  case ((s, max, min), n) => (s + n, math.max(max, n), math.min(min, n))
}
```

### 12.3 View (Lazy Evaluation)

```scala
// Without view - each operation creates an intermediate collection
val result1 = (1 to 1000000)
  .map(_ + 1)           // creates collection 1
  .filter(_ % 2 == 0)   // creates collection 2
  .map(_ * 2)           // creates collection 3
  .take(10)

// Using view - lazy evaluation, only processes needed elements
val result2 = (1 to 1000000).view
  .map(_ + 1)
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(10)
  .toList  // force evaluation

// Performance test
def timeIt(block: => Unit): Long = {
  val start = System.nanoTime()
  block
  System.nanoTime() - start
}

println("Without view: " + timeIt(result1))
println("With view: " + timeIt(result2))
```

---

## 13. Practice Exercises

### Exercise 1: Data Processing Pipeline

```scala
// Student grading system
case class Student(id: Int, name: String, scores: List[Int])

object GradeSystem {
  val students = List(
    Student(1, "Alice", List(85, 90, 88)),
    Student(2, "Bob", List(75, 80, 78)),
    Student(3, "Charlie", List(95, 92, 98)),
    Student(4, "David", List(60, 65, 70)),
    Student(5, "Eve", List(88, 85, 90))
  )
  
  // Calculate average score
  def average(scores: List[Int]): Double = {
    scores.sum.toDouble / scores.length
  }
  
  // Find the student with the highest average
  def topStudent: Student = {
    students.maxBy(s => average(s.scores))
  }
  
  // Find failing students (average score < 70)
  def failingStudents: List[Student] = {
    students.filter(s => average(s.scores) < 70)
  }
  
  // Count students per grade bracket
  def gradeDistribution: Map[String, Int] = {
    students.groupBy { s =>
      val avg = average(s.scores)
      if (avg >= 90) "A"
      else if (avg >= 80) "B"
      else if (avg >= 70) "C"
      else "F"
    }.view.mapValues(_.length).toMap
  }
  
  // Average score per subject
  def averageBySubject: List[Double] = {
    val allScores = students.map(_.scores)
    allScores.transpose.map(subject => subject.sum.toDouble / subject.length)
  }
  
  // Test
  def main(args: Array[String]): Unit = {
    println(s"Top student: ${topStudent.name}")
    println(s"Failing students: ${failingStudents.map(_.name)}")
    println(s"Grade distribution: $gradeDistribution")
    println(s"Average by subject: $averageBySubject")
  }
}
```

### Exercise 2: Text Analysis

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
  
  // Test
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

### Exercise 3: E-Commerce Order System

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
  
  // Order total
  def orderTotal(order: Order): Double = {
    order.items.map(_.total).sum
  }
  
  // Total revenue across all orders
  def totalRevenue: Double = {
    orders.map(orderTotal).sum
  }
  
  // Best-selling products
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
  
  // Revenue by category
  def revenueByCategory: Map[String, Double] = {
    orders
      .flatMap(_.items)
      .groupBy(_.product.category)
      .view
      .mapValues(items => items.map(_.total).sum)
      .toMap
  }
  
  // Customer spending ranking
  def topCustomers(n: Int): List[(Int, Double)] = {
    orders
      .groupBy(_.customerId)
      .view
      .mapValues(orders => orders.map(orderTotal).sum)
      .toList
      .sortBy(-_._2)
      .take(n)
  }
  
  // Average order value
  def averageOrderValue: Double = {
    totalRevenue / orders.length
  }
  
  // Test
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

### Exercise 4: Social Network Analysis

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
  
  // Find the most popular user (most followers)
  def mostPopularUser: User = {
    users.maxBy(_.followers.size)
  }
  
  // Find mutually following user pairs
  def mutualFollows: List[(Int, Int)] = {
    for {
      user1 <- users
      user2 <- users
      if user1.id < user2.id
      if user1.followers.contains(user2.id) && user2.followers.contains(user1.id)
    } yield (user1.id, user2.id)
  }
  
  // Most liked posts
  def topPosts(n: Int): List[Post] = {
    posts.sortBy(-_.likes).take(n)
  }
  
  // Trending tags
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
  
  // Total likes for a user
  def userTotalLikes(userId: Int): Int = {
    posts.filter(_.userId == userId).map(_.likes).sum
  }
  
  // User activity ranking
  def userActivity: List[(String, Int)] = {
    users.map { user =>
      val postCount = posts.count(_.userId == user.id)
      val totalLikes = userTotalLikes(user.id)
      (user.name, postCount + totalLikes)
    }.sortBy(-_._2)
  }
  
  // Recommend people to follow (followers of followers)
  def recommendFollows(userId: Int): Set[Int] = {
    val user = users.find(_.id == userId).get
    val following = user.followers
    
    following
      .flatMap(id => users.find(_.id == id).get.followers)
      .filterNot(id => following.contains(id) || id == userId)
  }
  
  // Test
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

### Exercise 5: Data Transformation Practice

```scala
object DataTransformations {
  // 1. Flatten nested lists
  def flattenNested[T](list: List[List[T]]): List[T] = {
    list.flatten
    // or list.flatMap(identity)
  }
  
  // 2. Remove consecutive duplicate elements
  def removeDuplicates[T](list: List[T]): List[T] = {
    list.foldRight(List.empty[T]) { (elem, acc) =>
      if (acc.isEmpty || acc.head != elem) elem :: acc
      else acc
    }
  }
  
  // 3. Group consecutive identical elements
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
  
  // 4. Rotate a list
  def rotate[T](list: List[T], n: Int): List[T] = {
    val size = list.length
    if (size == 0) list
    else {
      val shift = ((n % size) + size) % size
      list.drop(shift) ++ list.take(shift)
    }
  }
  
  // 5. Compress a list (Run-Length Encoding)
  def encode[T](list: List[T]): List[(Int, T)] = {
    groupConsecutive(list).map(group => (group.length, group.head))
  }
  
  // 6. Decompress a list
  def decode[T](encoded: List[(Int, T)]): List[T] = {
    encoded.flatMap { case (count, elem) => List.fill(count)(elem) }
  }
  
  // Test
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

## 14. Key Takeaways

### Choosing a Collection
- **List**: prepend operations, recursive processing
- **Vector**: random access, general-purpose sequences
- **Set**: uniqueness, membership testing
- **Map**: key-value lookup
- **Array**: mutability, best raw performance

### Core Operations
- **Transformation**: map, flatMap, collect
- **Filtering**: filter, filterNot, partition
- **Aggregation**: sum, reduce, fold, aggregate
- **Lookup**: find, exists, forall
- **Sorting**: sorted, sortBy, sortWith

### Best Practices
- Prefer immutable collections
- Use for comprehensions to improve readability
- Be aware of performance pitfalls (avoid repeated traversals)
- Use view for lazy evaluation when appropriate
- Chain operations to keep code concise

### for Comprehensions
- Equivalent to combinations of map/flatMap/filter
- Provide cleaner syntax
- Support pattern matching and variable binding
- Well-suited for working with Option, List, etc.

---

## Next Steps

After completing Part 5, you have mastered:
- The full Scala collections hierarchy
- Core collections: List, Set, Map, and more
- A rich set of collection operations
- Applying for comprehensions
- Performance optimization techniques

**Continue learning:**
- [Part 6: Pattern Matching](scala_part6_pattern_matching.md) - one of Scala's most powerful features
- [Part 7: Error Handling](scala_part7_error_handling.md) - Option, Either, Try

Ready to continue?

---

> [📚 Table of Contents](../../README.md) | [« Prev: OOP](scala_part4_oop.md) | [Next: Pattern Matching »](scala_part6_pattern_matching.md)
