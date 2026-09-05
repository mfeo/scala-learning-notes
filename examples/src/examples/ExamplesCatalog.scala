package examples

import examples.contextual.ContextualExamples.*

object ExamplesCatalog:
  final case class Entry(name: String, title: String, run: () => List[String])

  private final case class MacroSample(name: String, value: Int)

  val entries: List[Entry] = List(
    Entry(
      "introduction",
      "Introduction and Setup",
      () => examples.introduction.IntroductionExamples.greeting(Nil)
    ),
    Entry(
      "basics",
      "Basic Syntax",
      () => List(examples.basics.BasicSyntaxExamples.grade(85).fold(identity, grade => s"Grade: $grade"))
    ),
    Entry(
      "functions",
      "Functions and Methods",
      () => List(s"5! = ${examples.functions.FunctionExamples.factorial(5).getOrElse(0)}")
    ),
    Entry(
      "oop",
      "Object-Oriented Programming",
      () => examples.oop.ObjectOrientedExamples.Isbn.from("978-1234567890").map(isbn => s"ISBN: ${isbn.value}").toList
    ),
    Entry(
      "collections",
      "Collection Operations",
      () => List(s"Average: ${examples.collections.CollectionExamples.averageTotal(Nil).fold("n/a")(_.toString)}")
    ),
    Entry(
      "patterns",
      "Pattern Matching",
      () => List(examples.patterns.PatternMatchingExamples.parse("list").map(examples.patterns.PatternMatchingExamples.describe).fold(identity, identity))
    ),
    Entry(
      "errors",
      "Error Handling",
      () => List(examples.errors.ErrorHandlingExamples.load(Map("host" -> "localhost", "port" -> "8080")).toString)
    ),
    Entry(
      "contextual",
      "Contextual Abstractions and Type Classes",
      () => List(User("Ada", true).encoded)
    ),
    Entry(
      "macros",
      "Scala 3 Macros",
      () => examples.macros.MacroDefinitions.fieldNames[MacroSample]
    ),
    Entry(
      "modern",
      "Modern Scala 3",
      () => List(examples.modern.Domain.PublicDomain.describe(examples.modern.Domain.CheckoutState.Submitted))
    )
  )

  def list: List[String] =
    entries.map(entry => s"${entry.name}: ${entry.title}")

  def run(name: String): Either[String, List[String]] =
    entries
      .find(_.name == name.trim.toLowerCase)
      .map(_.run())
      .toRight(s"Unknown example: $name")

  def runAll: List[(String, List[String])] =
    entries.map(entry => entry.title -> entry.run())
