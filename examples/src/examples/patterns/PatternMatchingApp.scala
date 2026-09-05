package examples.patterns

import examples.patterns.PatternMatchingExamples.*

object PatternMatchingApp:
  def main(args: Array[String]): Unit =
    val input = args.mkString(" ") match
      case "" => "add book 2"
      case supplied => supplied

    println(parse(input).map(describe).fold(identity, identity))
