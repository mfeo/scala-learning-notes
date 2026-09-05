package examples.modern

import examples.modern.Derivation.TypeName

object Domain:
  opaque type UserId = String

  object UserId:
    def from(value: String): Option[UserId] =
      Option.when(value.trim.nonEmpty)(value.trim)

    def unsafe(value: String): UserId =
      value

  extension (id: UserId)
    def value: String = id

  final case class User(id: UserId, name: String)

  enum CheckoutState derives TypeName:
    case Draft
    case Submitted
    case Paid(transactionId: String)
    case Cancelled(reason: String)

  object Checkout:
    def canPay(state: CheckoutState): Boolean =
      state match
        case CheckoutState.Submitted => true
        case _ => false

    def describe(state: CheckoutState): String =
      state match
        case CheckoutState.Draft => "Draft"
        case CheckoutState.Submitted => "Submitted"
        case CheckoutState.Paid(transactionId) => s"Paid: $transactionId"
        case CheckoutState.Cancelled(reason) => s"Cancelled: $reason"

  object PublicDomain:
    export UserId.from
    export Checkout.{canPay, describe}
