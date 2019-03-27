javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")


lazy val root = (project in file(".")).
  settings(
    name := "lambda-demo",
    version := "1.0",
    scalaVersion := "2.12.3",
    retrieveManaged := true,
    libraryDependencies += "com.amazonaws" % "aws-lambda-java-core" % "1.0.0",
    libraryDependencies += "com.amazonaws" % "aws-lambda-java-events" % "1.0.0",
//    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.6"
  )

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}
