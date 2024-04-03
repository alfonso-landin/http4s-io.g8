package $package$

import cats.effect.IO
import cats.implicits.*
import io.circe.Decoder
import io.circe.Encoder
import io.circe.*
import org.http4s.Method.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.client.Client
import org.http4s.client.dsl.io.*
import org.http4s.implicits.*

trait Jokes:
  def get: IO[Jokes.Joke]

object Jokes:
  final case class Joke(joke: String)

  object Joke:
    given Decoder[Joke] = Decoder.derived[Joke]
    given EntityDecoder[IO, Joke] = jsonOf
    given Encoder[Joke] = Encoder.AsObject.derived[Joke]
    given EntityEncoder[IO, Joke] = jsonEncoderOf

  final case class JokeError(e: Throwable) extends RuntimeException

  def impl(C: Client[IO]): Jokes = new Jokes:
    def get: IO[Jokes.Joke] =
      C.expect[Joke](GET(uri"https://icanhazdadjoke.com/"))
        // Prevent Client Json Decoding Failure Leaking
        .adaptError { case t => JokeError(t) }
