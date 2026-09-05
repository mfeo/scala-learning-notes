package examples.patterns

import examples.patterns.PatternMatchingExamples.*

class PatternMatchingSuite extends munit.FunSuite:
  test("parses every supported command shape"):
    assertEquals(parse("add book 2"), Right(Command.Add("book", 2)))
    assertEquals(parse("remove book"), Right(Command.Remove("book")))
    assertEquals(parse("list"), Right(Command.ListItems))

  test("trims whitespace before matching a command sequence"):
    assertEquals(parse("  add   pen   1  "), Right(Command.Add("pen", 1)))

  test("rejects empty, unknown, and malformed commands"):
    assertEquals(parse("   "), Left("Command must not be empty"))
    assertEquals(parse("rename book"), Left("Unknown or malformed command: rename"))
    assertEquals(parse("remove"), Left("Unknown or malformed command: remove"))

  test("rejects non-integer and non-positive quantities"):
    assertEquals(parse("add book many"), Left("Quantity must be an integer"))
    assertEquals(parse("add book 0"), Left("Quantity must be positive"))
    assertEquals(parse("add book -1"), Left("Quantity must be positive"))

  test("describes every command subtype"):
    assertEquals(describe(Command.Add("book", 2)), "Add 2 of book")
    assertEquals(describe(Command.Remove("book")), "Remove book")
    assertEquals(describe(Command.ListItems), "List inventory")
