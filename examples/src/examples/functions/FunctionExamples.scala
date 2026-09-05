package examples.functions

import scala.annotation.tailrec

object FunctionExamples:
  def applyTwice[A](value: A)(function: A => A): A =
    function(function(value))

  def multiply(left: Int)(right: Int): Int =
    left * right

  def pipeline[A, B, C](first: A => B, second: B => C): A => C =
    first.andThen(second)

  def factorial(number: Int): Option[BigInt] =
    @tailrec
    def loop(remaining: Int, accumulator: BigInt): BigInt =
      if remaining <= 1 then accumulator
      else loop(remaining - 1, accumulator * remaining)

    Option.when(number >= 0)(loop(number, 1))
