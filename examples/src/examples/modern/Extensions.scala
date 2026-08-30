package examples.modern

object Extensions:
  extension (text: String)
    def words: List[String] =
      text.trim.split("\\s+").toList.filter(_.nonEmpty)

    def titleCase: String =
      words
        .map(word => s"${word.head.toUpper}${word.tail.toLowerCase}")
        .mkString(" ")

  extension [A](values: List[A])
    def secondOption: Option[A] =
      values.drop(1).headOption

    def nonEmptyCount: Int =
      values.count(_ != null)

    def mapToSet[B](f: A => B): Set[B] =
      values.map(f).toSet
