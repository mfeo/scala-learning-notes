package examples.modern

import examples.modern.Domain.*
import examples.modern.Derivation.TypeName
import examples.modern.Extensions.*
import examples.modern.TypeClasses.*

class ModernScala3Suite extends munit.FunSuite:
  test("renders values with given Show instances"):
    val user = User(UserId.unsafe("u-1"), "Alice")

    assertEquals(Render.render(42), "42")
    assertEquals(Render.render("Scala"), "Scala")
    assertEquals(Render.render(user), "u-1:Alice")

  test("renders lists with a context bound"):
    assertEquals(Render.renderAll(List(1, 2, 3)), List("1", "2", "3"))

  test("adds string extension methods"):
    assertEquals("modern scala 3".words, List("modern", "scala", "3"))
    assertEquals("modern scala 3".titleCase, "Modern Scala 3")
    assertEquals("   ".words, Nil)

  test("adds list extension methods"):
    assertEquals(List("a", "b", "c").secondOption, Some("b"))
    assertEquals(List("a").secondOption, None)
    assertEquals(List("a", "bb", "ccc").mapToSet(_.length), Set(1, 2, 3))

  test("validates opaque user ids"):
    assertEquals(UserId.from(" u-123 ").map(_.value), Some("u-123"))
    assertEquals(UserId.from("   "), None)

  test("models checkout state with an enum"):
    assert(Checkout.canPay(CheckoutState.Submitted))
    assert(!Checkout.canPay(CheckoutState.Draft))
    assertEquals(Checkout.describe(CheckoutState.Paid("tx-1")), "Paid: tx-1")
    assertEquals(Checkout.describe(CheckoutState.Cancelled("timeout")), "Cancelled: timeout")

  test("exports selected domain operations through a public facade"):
    assertEquals(PublicDomain.from(" u-2 ").map(_.value), Some("u-2"))
    assert(PublicDomain.canPay(CheckoutState.Submitted))
    assertEquals(PublicDomain.describe(CheckoutState.Draft), "Draft")

  test("derives a type name instance for checkout state"):
    assertEquals(TypeName[CheckoutState].value, "CheckoutState")
