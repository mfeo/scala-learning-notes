package examples.basics

object BasicSyntaxExamples:
  def celsiusToFahrenheit(celsius: Double): Double =
    celsius * 9.0 / 5.0 + 32.0

  def grade(score: Int): Either[String, String] =
    if score < 0 || score > 100 then Left("Score must be between 0 and 100")
    else if score >= 90 then Right("A")
    else if score >= 80 then Right("B")
    else if score >= 70 then Right("C")
    else if score >= 60 then Right("D")
    else Right("F")

  def fizzBuzz(limit: Int): Either[String, List[String]] =
    Either.cond(
      limit > 0,
      (1 to limit).toList.map {
        case number if number % 15 == 0 => "FizzBuzz"
        case number if number % 3 == 0 => "Fizz"
        case number if number % 5 == 0 => "Buzz"
        case number => number.toString
      },
      "Limit must be positive"
    )
