package $package$

import cats.effect.IO
import cats.effect.IOApp

object Main extends IOApp.Simple:
  val run: IO[Unit] = $name;format="Camel"$Server.run
