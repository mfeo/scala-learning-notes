package examples

class ExamplesCatalogSuite extends munit.FunSuite:
  test("lists every content chapter in learning order"):
    assertEquals(
      ExamplesCatalog.entries.map(_.name),
      List("introduction", "basics", "functions", "oop", "collections", "patterns", "errors", "contextual", "macros", "modern")
    )
    assertEquals(ExamplesCatalog.list.size, 10)

  test("runs a named example case-insensitively"):
    assertEquals(ExamplesCatalog.run(" BASICS "), Right(List("Grade: B")))
    assertEquals(ExamplesCatalog.run("patterns"), Right(List("List inventory")))

  test("runs every example with deterministic non-empty output"):
    val firstRun = ExamplesCatalog.runAll
    val secondRun = ExamplesCatalog.runAll

    assertEquals(firstRun, secondRun)
    assertEquals(firstRun.size, 10)
    assert(firstRun.forall { case (_, lines) => lines.nonEmpty })

  test("rejects an empty or unknown example name"):
    assertEquals(ExamplesCatalog.run(""), Left("Unknown example: "))
    assertEquals(ExamplesCatalog.run("missing"), Left("Unknown example: missing"))
