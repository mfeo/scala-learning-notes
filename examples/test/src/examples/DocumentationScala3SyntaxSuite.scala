package examples

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import scala.compiletime.testing.typeCheckErrors
import scala.jdk.CollectionConverters.*

class DocumentationScala3SyntaxSuite extends munit.FunSuite:
  private case class ScalaFence(path: Path, startLine: Int, content: String)

  private val forbiddenSyntax = List(
    "implicit declaration" -> raw"\bimplicit\s+(?:val|def|class|object)\b".r,
    "implicit parameter list" -> raw"\(implicit\s".r,
    "implicitly summon" -> raw"\bimplicitly\s*\[".r,
    "view bound" -> raw"<%".r,
    "Scala 2 macro package" -> raw"scala\.(?:language\.experimental\.macros|reflect\.macros)".r,
    "Scala 2 macro definition" -> raw"=\s*macro\s".r,
    "underscore wildcard type" -> raw"\b(?:Array|List|Seq|Set|Map|Option|Either)\s*\[[^\]]*_".r,
    "underscore vararg splice" -> raw":\s*_\*".r,
    "package object" -> raw"\bpackage\s+object\b".r
  )

  test("Scala code fences use Scala 3 syntax"):
    val violations = markdownFiles().flatMap(scalaFences).flatMap { fence =>
      forbiddenSyntax.flatMap { case (description, pattern) =>
        pattern.findAllMatchIn(fence.content).map { matched =>
          val lineOffset = fence.content.take(matched.start).count(_ == '\n')
          s"${fence.path}:${fence.startLine + lineOffset}: $description: ${matched.matched}"
        }
      }
    }

    assert(
      violations.isEmpty,
      violations.mkString("Legacy Scala syntax found:\n", "\n", "")
    )

  test("higher-kinded parameters retain their placeholder syntax"):
    val errors = typeCheckErrors("def accept[F[_], A]: Unit = ()")
    assertEquals(errors, Nil)

  private def markdownFiles(): List[Path] =
    val docs = repositoryRoot.resolve("docs")
    val paths = Files.walk(docs)
    try paths.iterator.asScala.filter(_.toString.endsWith(".md")).toList.sorted
    finally paths.close()

  private def scalaFences(path: Path): List[ScalaFence] =
    val lines = Files.readAllLines(path, StandardCharsets.UTF_8).asScala.toList

    @annotation.tailrec
    def loop(
        remaining: List[(String, Int)],
        currentStart: Option[Int],
        currentLines: List[String],
        fences: List[ScalaFence]
    ): List[ScalaFence] =
      remaining match
        case Nil => fences.reverse
        case (line, number) :: tail if line.trim == "```scala" =>
          loop(tail, Some(number + 1), Nil, fences)
        case (line, _) :: tail if currentStart.nonEmpty && line.trim.startsWith("```") =>
          val fence = ScalaFence(path, currentStart.get, currentLines.reverse.mkString("\n"))
          loop(tail, None, Nil, fence :: fences)
        case (line, _) :: tail if currentStart.nonEmpty =>
          loop(tail, currentStart, line :: currentLines, fences)
        case _ :: tail =>
          loop(tail, None, Nil, fences)

    loop(lines.zipWithIndex.map { case (line, index) => line -> (index + 1) }, None, Nil, Nil)

  private def repositoryRoot: Path =
    Iterator
      .iterate(Path.of("").toAbsolutePath)(_.getParent)
      .takeWhile(_ != null)
      .find(path => Files.isDirectory(path.resolve("docs")) && Files.exists(path.resolve("build.mill")))
      .getOrElse(fail("Could not locate the repository root"))
