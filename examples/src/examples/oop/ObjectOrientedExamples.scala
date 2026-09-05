package examples.oop

object ObjectOrientedExamples:
  opaque type Isbn = String

  object Isbn:
    def from(value: String): Option[Isbn] =
      Option.when(value.matches("\\d{3}-\\d{10}"))(value)

  extension (isbn: Isbn)
    def value: String = isbn

  sealed trait LibraryItem:
    def title: String

  final case class Book(isbn: Isbn, title: String, author: String) extends LibraryItem
  final case class Magazine(title: String, issue: Int) extends LibraryItem

  final case class Library(available: Set[Isbn], checkedOut: Set[Isbn]):
    def checkout(isbn: Isbn): Either[String, Library] =
      if !available.contains(isbn) then Left("Book is not available")
      else Right(copy(available = available - isbn, checkedOut = checkedOut + isbn))

  object LibraryItem:
    def description(item: LibraryItem): String =
      item match
        case Book(_, title, author) => s"$title by $author"
        case Magazine(title, issue) => s"$title, issue $issue"
