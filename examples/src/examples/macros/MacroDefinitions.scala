package examples.macros

import scala.compiletime.error
import scala.quoted.*

object MacroDefinitions:
  inline def requirePositive(inline value: Int): Int =
    inline if value > 0 then value
    else error("Expected a positive integer literal")

  inline def fieldNames[A]: List[String] = ${ fieldNamesImpl[A] }

  private def fieldNamesImpl[A: Type](using quotes: Quotes): Expr[List[String]] =
    import quotes.reflect.*

    val fields = TypeRepr.of[A].typeSymbol.caseFields.map(field => Expr(field.name))
    Expr.ofList(fields)
