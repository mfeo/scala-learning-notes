package examples.patterns

object PatternMatchingExamples:
  enum Command:
    case Add(item: String, quantity: Int)
    case Remove(item: String)
    case ListItems

  def parse(input: String): Either[String, Command] =
    input.trim.split("\\s+").toList.filter(_.nonEmpty) match
      case "add" :: item :: quantity :: Nil =>
        quantity.toIntOption match
          case Some(value) if value > 0 => Right(Command.Add(item, value))
          case Some(_) => Left("Quantity must be positive")
          case None => Left("Quantity must be an integer")
      case "remove" :: item :: Nil => Right(Command.Remove(item))
      case "list" :: Nil => Right(Command.ListItems)
      case Nil => Left("Command must not be empty")
      case command :: _ => Left(s"Unknown or malformed command: $command")

  def describe(command: Command): String =
    command match
      case addition @ Command.Add(item, quantity) => s"Add ${addition.quantity} of $item"
      case Command.Remove(item) => s"Remove $item"
      case Command.ListItems => "List inventory"
