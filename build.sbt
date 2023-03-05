lazy val akkaVersion        = "2.5.13"
lazy val akkaHttpVersion    = "10.1.3"
lazy val scalatestVersion   = "3.0.5"
lazy val circeVersion       = "0.9.3"
lazy val akkaCirceVersion   = "1.21.0"
lazy val pureconfigVersion  = "0.9.1"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      scalaVersion    := "2.12.17",
      version         := "0.1"
    )),
    name := "akka-http-imdb",
    libraryDependencies ++= Seq(
      // Akka dependencies
      "com.typesafe.akka"     %% "akka-actor"          % akkaVersion,
      "com.typesafe.akka"     %% "akka-stream"         % akkaVersion,
      "com.typesafe.akka"     %% "akka-http"           % akkaHttpVersion,

      // Akka test dependencies
      "com.typesafe.akka"     %% "akka-testkit"        % akkaVersion       % Test,
      "com.typesafe.akka"     %% "akka-stream-testkit" % akkaVersion       % Test,
      "com.typesafe.akka"     %% "akka-http-testkit"   % akkaHttpVersion   % Test,
      "org.scalatest"         %% "scalatest"           % scalatestVersion  % Test,

      // Circe / parsing dependencies  
      "io.circe"              %% "circe-core"          % circeVersion,
      "io.circe"              %% "circe-generic"       % circeVersion,
      "io.circe"              %% "circe-parser"        % circeVersion,
      "de.heikoseeberger"     %% "akka-http-circe"     % akkaCirceVersion,
      "com.github.pureconfig" %% "pureconfig"          % pureconfigVersion,
    )
  )