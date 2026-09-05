package examples.functions

class FunctionsSuite extends munit.FunSuite:
  test("applies a higher-order function twice"):
    assertEquals(FunctionExamples.applyTwice(3)(_ + 2), 7)
    assertEquals(FunctionExamples.applyTwice("a")(_ + "b"), "abb")

  test("supports partial application through curried parameters"):
    val double = FunctionExamples.multiply(2)

    assertEquals(double(0), 0)
    assertEquals(double(21), 42)

  test("composes functions in execution order"):
    val lengthAfterTrim = FunctionExamples.pipeline[String, String, Int](_.trim, _.length)

    assertEquals(lengthAfterTrim(" Scala "), 5)
    assertEquals(lengthAfterTrim("   "), 0)

  test("calculates factorial boundary and ordinary values"):
    assertEquals(FunctionExamples.factorial(0), Some(BigInt(1)))
    assertEquals(FunctionExamples.factorial(1), Some(BigInt(1)))
    assertEquals(FunctionExamples.factorial(10), Some(BigInt(3628800)))

  test("rejects a negative factorial without throwing"):
    assertEquals(FunctionExamples.factorial(-1), None)
