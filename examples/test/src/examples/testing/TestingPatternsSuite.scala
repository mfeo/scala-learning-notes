package examples.testing

import examples.testing.TestingExamples.*

class TestingPatternsSuite extends munit.FunSuite:
  test("reserves available inventory"):
    assertEquals(Inventory(10).reserve(3), Right(Inventory(7, 3)))

  test("supports the exact-stock boundary"):
    assertEquals(Inventory(3).reserve(3), Right(Inventory(0, 3)))

  test("rejects zero and negative quantities"):
    assertEquals(Inventory(10).reserve(0), Left(ReservationError.InvalidQuantity))
    assertEquals(Inventory(10).reserve(-1), Left(ReservationError.InvalidQuantity))

  test("reports insufficient stock and leaves the original value unchanged"):
    val inventory = Inventory(2, 1)

    assertEquals(
      inventory.reserve(3),
      Left(ReservationError.InsufficientStock(requested = 3, available = 2))
    )
    assertEquals(inventory, Inventory(2, 1))

  test("rejects invalid inventory construction"):
    val availableError = intercept[IllegalArgumentException](Inventory(-1))
    val reservedError = intercept[IllegalArgumentException](Inventory(1, -1))

    assertEquals(availableError.getMessage, "requirement failed: Available stock must not be negative")
    assertEquals(reservedError.getMessage, "requirement failed: Reserved stock must not be negative")
