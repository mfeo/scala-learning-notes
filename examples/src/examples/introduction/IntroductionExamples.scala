package examples.introduction

object IntroductionExamples:
  def greeting(arguments: List[String]): List[String] =
    "Hello, Scala!" ::
      (if arguments.isEmpty then List("No arguments supplied.")
       else arguments.map(argument => s"Argument: $argument"))
