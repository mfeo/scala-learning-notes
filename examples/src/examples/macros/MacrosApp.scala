package examples.macros

import examples.macros.MacroDefinitions.*

object MacrosApp:
  final case class User(name: String, age: Int)

  def main(args: Array[String]): Unit =
    println(s"Positive literal: ${requirePositive(3)}")
    println(s"User fields: ${fieldNames[User].mkString(", ")}")
