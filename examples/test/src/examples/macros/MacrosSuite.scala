package examples.macros

import examples.macros.MacroDefinitions.*
import scala.compiletime.testing.typeCheckErrors

class MacrosSuite extends munit.FunSuite:
  final case class Empty()
  final case class User(name: String, age: Int)

  test("accepts a positive integer literal at compile time"):
    assertEquals(requirePositive(1), 1)
    assertEquals(requirePositive(Int.MaxValue), Int.MaxValue)

  test("rejects zero and negative literals at compile time"):
    val zeroErrors = typeCheckErrors("examples.macros.MacroDefinitions.requirePositive(0)")
    val negativeErrors = typeCheckErrors("examples.macros.MacroDefinitions.requirePositive(-1)")

    assert(zeroErrors.exists(_.message.contains("Expected a positive integer literal")))
    assert(negativeErrors.exists(_.message.contains("Expected a positive integer literal")))

  test("extracts case class fields in declaration order"):
    assertEquals(fieldNames[User], List("name", "age"))
    assertEquals(fieldNames[Empty], Nil)

  test("returns no fields for a non-product type"):
    assertEquals(fieldNames[String], Nil)
