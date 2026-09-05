package examples.errors

import examples.errors.ErrorHandlingExamples.*

object ErrorHandlingApp:
  def main(args: Array[String]): Unit =
    val values = Map("host" -> "localhost", "port" -> "8080")
    println(load(values))
