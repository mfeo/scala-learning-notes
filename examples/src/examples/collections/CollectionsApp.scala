package examples.collections

import examples.collections.CollectionExamples.*

object CollectionsApp:
  def main(args: Array[String]): Unit =
    val orders = List(
      Order("o-1", "Ada", BigDecimal(120), List("book", "pen")),
      Order("o-2", "Ada", BigDecimal(80), List("notebook")),
      Order("o-3", "Linus", BigDecimal(200), List("keyboard"))
    )

    println(s"Totals: ${totalByCustomer(orders)}")
    println(s"Products: ${distinctProducts(orders).toList.sorted.mkString(", ")}")
    println(s"Largest: ${largestOrders(orders, 2).getOrElse(Nil).map(_.id).mkString(", ")}")
