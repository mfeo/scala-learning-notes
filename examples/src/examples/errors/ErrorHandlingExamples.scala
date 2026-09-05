package examples.errors

import scala.util.Try

object ErrorHandlingExamples:
  final case class AppConfig(host: String, port: Int)

  enum ConfigError:
    case Missing(key: String)
    case InvalidInteger(key: String, value: String)
    case OutOfRange(key: String, minimum: Int, maximum: Int)

  def load(values: Map[String, String]): Either[ConfigError, AppConfig] =
    for
      host <- required(values, "host")
      portText <- required(values, "port")
      port <- parseInt("port", portText)
      validPort <- validateRange("port", port, 1, 65535)
    yield AppConfig(host, validPort)

  private def required(values: Map[String, String], key: String): Either[ConfigError, String] =
    values.get(key).filter(_.trim.nonEmpty).map(_.trim).toRight(ConfigError.Missing(key))

  private def parseInt(key: String, value: String): Either[ConfigError, Int] =
    Try(value.toInt).toEither.left.map(_ => ConfigError.InvalidInteger(key, value))

  private def validateRange(
      key: String,
      value: Int,
      minimum: Int,
      maximum: Int
  ): Either[ConfigError, Int] =
    Either.cond(
      value >= minimum && value <= maximum,
      value,
      ConfigError.OutOfRange(key, minimum, maximum)
    )
