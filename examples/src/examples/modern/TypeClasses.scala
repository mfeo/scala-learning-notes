package examples.modern

import examples.modern.Domain.*

object TypeClasses:
  trait Show[A]:
    def show(value: A): String

  object Show:
    def apply[A](using instance: Show[A]): Show[A] =
      instance

  given Show[Int] with
    def show(value: Int): String =
      value.toString

  given Show[String] with
    def show(value: String): String =
      value

  given Show[UserId] with
    def show(value: UserId): String =
      value.value

  given Show[User] with
    def show(value: User): String =
      s"${value.id.value}:${value.name}"

  object Render:
    def render[A](value: A)(using show: Show[A]): String =
      show.show(value)

    def renderAll[A: Show](values: List[A]): List[String] =
      values.map(value => Show[A].show(value))
