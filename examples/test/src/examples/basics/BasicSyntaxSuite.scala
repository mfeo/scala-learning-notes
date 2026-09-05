package examples.basics

class BasicSyntaxSuite extends munit.FunSuite:
  test("converts Celsius boundary values to Fahrenheit"):
    assertEquals(BasicSyntaxExamples.celsiusToFahrenheit(0), 32.0)
    assertEquals(BasicSyntaxExamples.celsiusToFahrenheit(-40), -40.0)

  test("assigns grades at every boundary"):
    val cases = List(0 -> "F", 59 -> "F", 60 -> "D", 69 -> "D", 70 -> "C", 79 -> "C", 80 -> "B", 89 -> "B", 90 -> "A", 100 -> "A")

    cases.foreach { case (score, expected) =>
      assertEquals(BasicSyntaxExamples.grade(score), Right(expected))
    }

  test("rejects scores outside the valid range"):
    assertEquals(BasicSyntaxExamples.grade(-1), Left("Score must be between 0 and 100"))
    assertEquals(BasicSyntaxExamples.grade(101), Left("Score must be between 0 and 100"))

  test("generates FizzBuzz without changing the requested limit"):
    val limit = 15

    assertEquals(
      BasicSyntaxExamples.fizzBuzz(limit),
      Right(List("1", "2", "Fizz", "4", "Buzz", "Fizz", "7", "8", "Fizz", "Buzz", "11", "Fizz", "13", "14", "FizzBuzz"))
    )
    assertEquals(limit, 15)

  test("rejects non-positive FizzBuzz limits"):
    assertEquals(BasicSyntaxExamples.fizzBuzz(0), Left("Limit must be positive"))
    assertEquals(BasicSyntaxExamples.fizzBuzz(-1), Left("Limit must be positive"))
