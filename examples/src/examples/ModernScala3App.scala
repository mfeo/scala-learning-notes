package examples

import examples.modern.Domain.*
import examples.modern.Derivation.TypeName
import examples.modern.Extensions.*
import examples.modern.TypeClasses.*

object ModernScala3App:
  def main(args: Array[String]): Unit =
    val user = User(UserId.from("u-100").getOrElse(UserId.unsafe("unknown")), "Ada")
    val status = CheckoutState.Submitted
    val title = "modern scala 3 examples".titleCase

    println(Render.render(user))
    println(s"Status: ${Checkout.describe(status)}")
    println(s"Title: $title")
    println(s"Second item: ${List(1, 2, 3).secondOption.getOrElse(0)}")
    println(s"Derived type name: ${TypeName[CheckoutState].value}")
    println(s"Exported description: ${PublicDomain.describe(status)}")
