package examples.contextual

import examples.contextual.ContextualExamples.*

object ContextualApp:
  def main(args: Array[String]): Unit =
    val users = List(User("Ada", true), User("Linus", false))
    encodeAll(users).foreach(println)
