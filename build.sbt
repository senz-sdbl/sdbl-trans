name := "sdbl-trans"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {

  val akkaVersion       = "2.3.9"
  val cassandraVersion  = "2.1.9"
  val slickVersion      = "3.0.0"
  val sprayVersion      = "1.3.2"

  Seq(
    "commons-io"              % "commons-io"                % "2.5",
    "io.spray"                %% "spray-can"                % sprayVersion,
    "io.spray"                %% "spray-routing"            % sprayVersion,
    "io.spray"                %% "spray-client"             % sprayVersion,
    "io.spray"                %% "spray-json"               % sprayVersion,
    "com.typesafe.akka"       %% "akka-actor"               % akkaVersion,
    "com.typesafe.akka"       %% "akka-slf4j"               % akkaVersion,
    "com.typesafe.slick"      %% "slick"                    % slickVersion,
    "mysql"                   % "mysql-connector-java"      % "5.1.34",
    "c3p0"                    % "c3p0"                      % "0.9.1.2",
    "org.slf4j"               % "slf4j-api"                 % "1.7.5",
    "ch.qos.logback"          % "logback-classic"           % "1.0.9",
    "org.scala-lang.modules" %% "scala-parser-combinators"  % "1.0.2"
  )
}

resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

assemblyMergeStrategy in assembly := {
  case PathList(ps @ _*) if ps.last endsWith ".properties" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".RSA" => MergeStrategy.discard
  case PathList(ps @ _*) if ps.last endsWith ".keys" => MergeStrategy.discard
  case PathList(ps @ _*) if ps.last endsWith "logs" => MergeStrategy.discard

  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}


