package examples.contextual

object ContextualExamples:
  final case class User(name: String, active: Boolean)

  trait Encoder[A]:
    def encode(value: A): String

  object Encoder:
    def apply[A](using encoder: Encoder[A]): Encoder[A] = encoder

  given Encoder[String] with
    def encode(value: String): String = s"\"${escape(value)}\""

  given Encoder[Int] with
    def encode(value: Int): String = value.toString

  given Encoder[User] with
    def encode(value: User): String =
      s"{\"name\":${Encoder[String].encode(value.name)},\"active\":${value.active}}"

  extension [A: Encoder](value: A)
    def encoded: String = Encoder[A].encode(value)

  def encodeAll[A: Encoder](values: List[A]): List[String] =
    values.map(_.encoded)

  private def escape(value: String): String =
    value.flatMap {
      case '\\' => "\\\\"
      case '"' => "\\\""
      case character => character.toString
    }
