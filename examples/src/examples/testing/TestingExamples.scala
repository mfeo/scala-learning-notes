package examples.testing

object TestingExamples:
  enum ReservationError:
    case InvalidQuantity
    case InsufficientStock(requested: Int, available: Int)

  final case class Inventory(available: Int, reserved: Int = 0):
    require(available >= 0, "Available stock must not be negative")
    require(reserved >= 0, "Reserved stock must not be negative")

    def reserve(quantity: Int): Either[ReservationError, Inventory] =
      if quantity <= 0 then Left(ReservationError.InvalidQuantity)
      else if quantity > available then
        Left(ReservationError.InsufficientStock(quantity, available))
      else Right(copy(available = available - quantity, reserved = reserved + quantity))
