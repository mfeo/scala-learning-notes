package examples.introduction

object IntroductionApp:
  def main(args: Array[String]): Unit =
    IntroductionExamples.greeting(args.toList).foreach(println)
