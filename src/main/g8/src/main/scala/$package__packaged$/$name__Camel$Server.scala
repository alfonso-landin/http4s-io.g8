package $package$

import cats.effect.IO
import cats.implicits.*
import com.comcast.ip4s.*
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.middleware.Logger
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory

object $name;format="Camel"$Server:

  def run: IO[Nothing] =
    given LoggerFactory[IO] = Slf4jFactory.create
    val server = for
      client <- EmberClientBuilder.default[IO].build
      helloWorldAlg = HelloWorld.impl
      jokeAlg = Jokes.impl(client)

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      httpApp = (
        $name;format="Camel"$Routes.helloWorldRoutes(helloWorldAlg) <+>
          $name;format="Camel"$Routes.jokeRoutes(jokeAlg)
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      _ <-
        EmberServerBuilder
          .default[IO]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(finalHttpApp)
          .build
    yield ()
    server.useForever
