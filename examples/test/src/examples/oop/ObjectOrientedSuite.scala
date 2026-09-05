package examples.oop

import examples.oop.ObjectOrientedExamples.*

class ObjectOrientedSuite extends munit.FunSuite:
  private val isbn = Isbn.from("978-1234567890").getOrElse(fail("Expected a valid ISBN"))

  test("constructs a valid ISBN and rejects invalid input"):
    assertEquals(Isbn.from("978-1234567890").map(_.value), Some("978-1234567890"))
    assertEquals(Isbn.from(""), None)
    assertEquals(Isbn.from("978-123"), None)

  test("describes every library item subtype"):
    assertEquals(LibraryItem.description(Book(isbn, "Scala", "Ada")), "Scala by Ada")
    assertEquals(LibraryItem.description(Magazine("FP Monthly", 7)), "FP Monthly, issue 7")

  test("checks out an available book immutably"):
    val original = Library(Set(isbn), Set.empty)

    assertEquals(original.checkout(isbn), Right(Library(Set.empty, Set(isbn))))
    assertEquals(original, Library(Set(isbn), Set.empty))

  test("rejects an unavailable book without changing library state"):
    val original = Library(Set.empty, Set.empty)

    assertEquals(original.checkout(isbn), Left("Book is not available"))
    assertEquals(original, Library(Set.empty, Set.empty))
