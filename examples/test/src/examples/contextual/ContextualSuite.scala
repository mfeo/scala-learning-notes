package examples.contextual

import examples.contextual.ContextualExamples.*

class ContextualSuite extends munit.FunSuite:
  test("resolves given encoders for primitive values"):
    assertEquals("Scala".encoded, "\"Scala\"")
    assertEquals(42.encoded, "42")

  test("escapes special characters when encoding strings"):
    assertEquals("a\\\"b".encoded, "\"a\\\\\\\"b\"")
    assertEquals("".encoded, "\"\"")

  test("composes encoders for domain values"):
    assertEquals(User("Ada", true).encoded, "{\"name\":\"Ada\",\"active\":true}")
    assertEquals(User("Linus", false).encoded, "{\"name\":\"Linus\",\"active\":false}")

  test("encodes lists through a context bound without changing the input"):
    val users = List(User("Ada", true), User("Linus", false))

    assertEquals(
      encodeAll(users),
      List("{\"name\":\"Ada\",\"active\":true}", "{\"name\":\"Linus\",\"active\":false}")
    )
    assertEquals(users, List(User("Ada", true), User("Linus", false)))
    assertEquals(encodeAll(List.empty[User]), Nil)
