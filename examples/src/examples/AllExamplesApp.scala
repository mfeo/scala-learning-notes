package examples

object AllExamplesApp:
  def main(args: Array[String]): Unit =
    args.toList match
      case Nil => ExamplesCatalog.list.foreach(println)
      case "all" :: Nil =>
        ExamplesCatalog.runAll.foreach { case (title, lines) =>
          println(s"== $title ==")
          lines.foreach(println)
        }
      case name :: Nil =>
        ExamplesCatalog.run(name).fold(
          error => Console.err.println(error),
          _.foreach(println)
        )
      case _ => Console.err.println("Usage: AllExamplesApp [all|example-name]")
