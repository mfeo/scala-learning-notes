package examples.basics

object BasicSyntaxApp:
  def main(args: Array[String]): Unit =
    println(s"0°C = ${BasicSyntaxExamples.celsiusToFahrenheit(0)}°F")
    println(s"Grade: ${BasicSyntaxExamples.grade(85).getOrElse("invalid")}")
    println(BasicSyntaxExamples.fizzBuzz(15).getOrElse(Nil).mkString(", "))
