package examples.errors

import examples.errors.ErrorHandlingExamples.*

class ErrorHandlingSuite extends munit.FunSuite:
  test("loads and trims a valid configuration"):
    assertEquals(load(Map("host" -> " localhost ", "port" -> "8080")), Right(AppConfig("localhost", 8080)))

  test("accepts both port boundaries"):
    assertEquals(load(Map("host" -> "localhost", "port" -> "1")), Right(AppConfig("localhost", 1)))
    assertEquals(load(Map("host" -> "localhost", "port" -> "65535")), Right(AppConfig("localhost", 65535)))

  test("reports missing and blank required values"):
    assertEquals(load(Map("port" -> "8080")), Left(ConfigError.Missing("host")))
    assertEquals(load(Map("host" -> " ", "port" -> "8080")), Left(ConfigError.Missing("host")))
    assertEquals(load(Map("host" -> "localhost")), Left(ConfigError.Missing("port")))

  test("converts integer exceptions into a typed error"):
    assertEquals(
      load(Map("host" -> "localhost", "port" -> "eight")),
      Left(ConfigError.InvalidInteger("port", "eight"))
    )

  test("rejects ports outside the valid range without changing input"):
    val values = Map("host" -> "localhost", "port" -> "0")

    assertEquals(load(values), Left(ConfigError.OutOfRange("port", 1, 65535)))
    assertEquals(load(values.updated("port", "65536")), Left(ConfigError.OutOfRange("port", 1, 65535)))
    assertEquals(values, Map("host" -> "localhost", "port" -> "0"))
