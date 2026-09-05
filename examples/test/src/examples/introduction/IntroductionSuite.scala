package examples.introduction

class IntroductionSuite extends munit.FunSuite:
  test("greets a caller without arguments"):
    assertEquals(
      IntroductionExamples.greeting(Nil),
      List("Hello, Scala!", "No arguments supplied.")
    )

  test("preserves argument order without changing the input"):
    val arguments = List("Alice", "Bob")

    assertEquals(
      IntroductionExamples.greeting(arguments),
      List("Hello, Scala!", "Argument: Alice", "Argument: Bob")
    )
    assertEquals(arguments, List("Alice", "Bob"))
