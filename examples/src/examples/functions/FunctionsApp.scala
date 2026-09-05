package examples.functions

object FunctionsApp:
  def main(args: Array[String]): Unit =
    val normalize = FunctionExamples.pipeline[String, String, Int](_.trim, _.length)

    println(s"Apply twice: ${FunctionExamples.applyTwice(3)(_ + 1)}")
    println(s"Curried multiply: ${FunctionExamples.multiply(6)(7)}")
    println(s"Pipeline: ${normalize(" Scala ")}")
    println(s"Factorial: ${FunctionExamples.factorial(5).getOrElse(0)}")
