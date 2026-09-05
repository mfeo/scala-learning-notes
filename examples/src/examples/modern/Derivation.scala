package examples.modern

import scala.compiletime.constValue
import scala.deriving.Mirror

object Derivation:
  trait TypeName[A]:
    def value: String

  object TypeName:
    def apply[A](using typeName: TypeName[A]): TypeName[A] = typeName

    inline def derived[A](using mirror: Mirror.Of[A]): TypeName[A] =
      instance(constValue[mirror.MirroredLabel])

    private def instance[A](name: String): TypeName[A] =
      new TypeName[A]:
        val value: String = name
