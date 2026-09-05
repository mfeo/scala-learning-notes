package examples.collections

import examples.collections.CollectionExamples.*

class CollectionsSuite extends munit.FunSuite:
  private val orders = List(
    Order("o-1", "Ada", BigDecimal(30), List("book", "pen")),
    Order("o-2", "Ada", BigDecimal(20), List("book")),
    Order("o-3", "Linus", BigDecimal(40), Nil)
  )

  test("aggregates order totals by customer"):
    assertEquals(totalByCustomer(orders), Map("Ada" -> BigDecimal(50), "Linus" -> BigDecimal(40)))
    assertEquals(totalByCustomer(Nil), Map.empty)

  test("collects distinct products from nested lists"):
    assertEquals(distinctProducts(orders), Set("book", "pen"))
    assertEquals(distinctProducts(Nil), Set.empty)

  test("sorts and limits orders without modifying the input"):
    assertEquals(largestOrders(orders, 2).map(_.map(_.id)), Right(List("o-3", "o-1")))
    assertEquals(largestOrders(orders, 0), Right(Nil))
    assertEquals(orders.map(_.id), List("o-1", "o-2", "o-3"))

  test("uses the order id to make equal-total sorting deterministic"):
    val tied = List(
      Order("o-2", "Ada", BigDecimal(10), Nil),
      Order("o-1", "Ada", BigDecimal(10), Nil)
    )

    assertEquals(largestOrders(tied, 2).map(_.map(_.id)), Right(List("o-1", "o-2")))

  test("rejects a negative order limit without changing the input"):
    assertEquals(largestOrders(orders, -1), Left("Limit must not be negative"))
    assertEquals(orders.size, 3)

  test("calculates an average only for non-empty orders"):
    assertEquals(averageTotal(orders), Some(BigDecimal(30)))
    assertEquals(averageTotal(Nil), None)
