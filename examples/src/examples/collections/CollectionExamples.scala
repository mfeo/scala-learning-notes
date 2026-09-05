package examples.collections

object CollectionExamples:
  final case class Order(id: String, customer: String, total: BigDecimal, products: List[String])

  def totalByCustomer(orders: List[Order]): Map[String, BigDecimal] =
    orders
      .groupMapReduce(_.customer)(_.total)(_ + _)

  def distinctProducts(orders: List[Order]): Set[String] =
    orders.iterator.flatMap(_.products).toSet

  def largestOrders(orders: List[Order], limit: Int): Either[String, List[Order]] =
    Either.cond(
      limit >= 0,
      orders.sortBy(order => (-order.total, order.id)).take(limit),
      "Limit must not be negative"
    )

  def averageTotal(orders: List[Order]): Option[BigDecimal] =
    Option.when(orders.nonEmpty)(orders.map(_.total).sum / orders.size)
