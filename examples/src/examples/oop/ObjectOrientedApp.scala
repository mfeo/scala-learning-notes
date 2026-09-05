package examples.oop

import examples.oop.ObjectOrientedExamples.*

object ObjectOrientedApp:
  def main(args: Array[String]): Unit =
    val isbn = Isbn.from("978-1234567890").getOrElse(sys.error("Valid sample ISBN expected"))
    val book = Book(isbn, "Programming in Scala", "Martin Odersky")
    val library = Library(Set(isbn), Set.empty)

    println(LibraryItem.description(book))
    println(s"Checkout succeeded: ${library.checkout(isbn).isRight}")
